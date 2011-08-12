package edu.ucdenver.ccp.annotation.serialization.rdf.uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.build.XmlDescriptorWriter;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.CcpUriUtil;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdf.craft.CraftDocument;
import edu.ucdenver.ccp.rdf.craft.CraftUriFactory;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;
import edu.ucdenver.ccp.rdfizer.validate.RdfValidator;
import edu.ucdenver.ccp.rdfizer.validate.RdfValidatorFactory;

public class RdfSerialization_AE extends JCasAnnotator_ImplBase {

	private static final String ANNOTATOR_DESCRIPTION = String
			.format("This annotator consumes annotations in the CAS and outputs them in an RDF representation.");

	private static final String ANNOTATOR_VENDOR = "UC Denver - CCP";

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(RdfSerialization_AE.class, "documentMetadataExtractorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetaDataExtractor documentMetaDataExtractor;

	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(RdfSerialization_AE.class, "annotationDataExtractorClassName");

	/**
	 * The name of the {@link AnnotationDataExtractor} implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor")
	private String annotationDataExtractorClassName;

	/**
	 * this {@link AnnotationDataExtractor} will be initialized based on the class name specified by
	 * the annotationDataExtractorClassName parameter
	 */
	private AnnotationDataExtractor annotationDataExtractor;

	public final static String PARAM_OUTPUT_DIRECTORY = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "outputDirectory");
	@ConfigurationParameter(mandatory = true, description = "Describes the location where generated RDF files will be placed.")
	private File outputDirectory;

	public final static String PARAM_FILE_PREFIX = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "filePrefix");
	@ConfigurationParameter(mandatory = true, description = "This string will be used as a prefix to the output files.")
	private String filePrefix;

	public final static String PARAM_RDF_FORMAT = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "rdfFormat");
	@ConfigurationParameter(mandatory = true, description = "This string specifies the RDF format to use")
	private String rdfFormat;

	private RDFWriter documentRdfWriter;
	private RDFWriter annotationRdfWriter;
	private RDFFormat format;
	private Logger logger;
	private long annotationId = 0;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		logger = context.getLogger();
		super.initialize(context);

		format = RDFFormat.NTRIPLES;
		if (rdfFormat.equalsIgnoreCase("rdfxml"))
			format = RDFFormat.RDFXML;
		else if (rdfFormat.equalsIgnoreCase("n3"))
			format = RDFFormat.N3;
		else if (rdfFormat.equalsIgnoreCase("turtle"))
			format = RDFFormat.TURTLE;
		else if (rdfFormat.equalsIgnoreCase("ntriples"))
			format = RDFFormat.NTRIPLES;
		else
			logger.log(
					Level.WARNING,
					String.format(
							"Unknown RDF format requested: %s. N-Triple format will be output by default. Valid formats include: ntriples, rdfxml, n3, and turtle.",
							rdfFormat));

		File documentRdfOutputFile = getDocumentRdfOutputFile();
		File annotationRdfOutputFile = getAnnotationRdfOutputFile();
		try {
			documentRdfWriter = RdfUtil.openWriter(documentRdfOutputFile, CharacterEncoding.UTF_8, format);
			annotationRdfWriter = RdfUtil.openWriter(annotationRdfOutputFile, CharacterEncoding.UTF_8, format);
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

		writeStatements(CcpUriUtil.getCcpFoafOrganizationStmts(), annotationRdfWriter);
		annotationId = 0;
	}

	private void writeStatements(Collection<Statement> stmts, RDFWriter writer) {
		for (Statement s : stmts) {
			try {
				writer.handleStatement(s);
			} catch (RDFHandlerException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private File getDocumentRdfOutputFile() {
		return new File(outputDirectory, String.format("%s-documents.%s", filePrefix, format.getDefaultFileExtension()));
	}

	private File getAnnotationRdfOutputFile() {
		return new File(outputDirectory, String.format("%s-annotations.%s", filePrefix,
				format.getDefaultFileExtension()));
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		/* Write the document RDF */
		UriFactory uriFactory = new CraftUriFactory();
		String pmid = documentMetaDataExtractor.extractDocumentId(jcas);
		pmid = StringUtil.removeSuffix(pmid, ".txt");
		CraftDocument craftDocument = CraftDocument.valueOf(new PubMedID(pmid));
		Map<org.openrdf.model.URI, Collection<Statement>> documentStmts = UimaToRdfUtil.generateDocumentRdf(uriFactory,
				craftDocument.pmcId().toString(), craftDocument.pubMedCentralUri());
		for (Collection<Statement> stmts : documentStmts.values())
			writeStatements(stmts, documentRdfWriter);

		/* Write the annotation RDF */
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (!mentionName.equals("syntactic context") && !mentionName.equals("continuant")) {
				try {
					Collection<URI> denotedClasses = uriFactory.getResourceUri(ccpTa);
					for (URI denotedClass : denotedClasses) {
						Collection<Statement> annotationStmts = UimaToRdfUtil.generateAnnotationRdf(uriFactory,
								new URIImpl(denotedClass.toString()), ccpTa, annotationId++, craftDocument.pmcId()
										.toString(), new URIImpl(craftDocument.pubMedCentralUri().toString()),
								CollectionsUtil.getSingleElement(documentStmts.keySet()), jcas.getDocumentText());
						writeStatements(annotationStmts, annotationRdfWriter);
					}
				} catch (NullPointerException e) {
					logger.log(Level.WARNING, "Annotation-to-RDF failure.");
				}
			}
		}

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		RdfUtil.closeWriter(documentRdfWriter);
		RdfUtil.closeWriter(annotationRdfWriter);

		RdfValidator rdfValidator = RdfValidatorFactory.getRdfValidator(format);
		if (!rdfValidator.isValidRdf(getDocumentRdfOutputFile(), CharacterEncoding.UTF_8, "http://kabob",
				org.apache.log4j.Logger.getLogger(getClass())))
			throw new IllegalStateException("RDF Validation failed for file: "
					+ getDocumentRdfOutputFile().getAbsolutePath());
		if (!rdfValidator.isValidRdf(getAnnotationRdfOutputFile(), CharacterEncoding.UTF_8, "http://kabob",
				org.apache.log4j.Logger.getLogger(getClass())))
			throw new IllegalStateException("RDF Validation failed for file: "
					+ getDocumentRdfOutputFile().getAbsolutePath());

	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File outputDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(RdfSerialization_AE.class, tsd,
				RdfSerialization_AE.PARAM_FILE_PREFIX, "craft-prerelease", RdfSerialization_AE.PARAM_RDF_FORMAT,
				"rdfxml", RdfSerialization_AE.PARAM_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath());
	}

	public static void exportXmlDescriptor(File baseDescriptorDirectory, String version) {
		try {
			Class<RdfSerialization_AE> cls = RdfSerialization_AE.class;
			AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(cls,
					TypeSystemUtil.getCcpTypeSystem(), PARAM_OUTPUT_DIRECTORY, "[OUTPUT DIRECTORY GOES HERE]",
					PARAM_FILE_PREFIX, "[OUTPUT FILE PREFIX GOES HERE]", PARAM_RDF_FORMAT, "rdfxml");
			ResourceMetaData metaData = aed.getMetaData();
			metaData.setName(cls.getSimpleName());
			metaData.setDescription(ANNOTATOR_DESCRIPTION);
			metaData.setVendor(ANNOTATOR_VENDOR);
			metaData.setVersion(version);
			aed.setMetaData(metaData);
			XmlDescriptorWriter.exportXmlDescriptor(cls, aed, baseDescriptorDirectory);
		} catch (ResourceInitializationException e) {
			throw new RuntimeException(e);
		}
	}

}
