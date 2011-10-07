package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.build.XmlDescriptorWriter;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.RdfAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.CcpUriUtil;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdf.ao.AoSelectorType;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil.RdfFormat;
import edu.ucdenver.ccp.rdfizer.validate.RdfValidator;
import edu.ucdenver.ccp.rdfizer.validate.RdfValidatorFactory;

public class RdfSerialization_AE extends JCasAnnotator_ImplBase {

	private static final String ANNOTATOR_DESCRIPTION = String
			.format("This annotator consumes annotations in the CAS and outputs them in an RDF representation.");

	private static final String ANNOTATOR_VENDOR = "UC Denver - CCP";

	/* ==== DocumentMetaDataExtractor configuration ==== */
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

	/* ==== AnnotationDataExtractor configuration ==== */
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
	 * this {@link RdfAnnotationDataExtractor} will be initialized based on the class name specified
	 * by the annotationDataExtractorClassName parameter
	 */
	private RdfAnnotationDataExtractor annotationDataExtractor;

	/* ==== DocumentRdfGenerator configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_RDF_GENERATOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(RdfSerialization_AE.class, "documentRdfGeneratorClassName");

	/**
	 * The name of the DocumentRdfGenerator implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentRdfGenerator implementation to use")
	private String documentRdfGeneratorClassName;

	/**
	 * this {@link DocumentRdfGenerator} will be initialized based on the class name specified by
	 * the documentRdfGeneratorClassName parameter
	 */
	private DocumentRdfGenerator documentRdfGenerator;

	/* ==== AnnotationRdfGenerator configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation RDF generator
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_RDF_GENERATOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(RdfSerialization_AE.class, "annotationRdfGeneratorClassName");

	/**
	 * The name of the AnnotationRdfGenerator implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationRdfGenerator implementation to use")
	private String annotationRdfGeneratorClassName;

	/**
	 * this {@link AnnotationRdfGenerator} will be initialized based on the class name specified by
	 * the annotationRdfGeneratorClassName parameter
	 */
	private AnnotationRdfGenerator annotationRdfGenerator;

	/* ==== UriFactory configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the URI Factory implementation to use
	 */
	public static final String PARAM_URI_FACTORY_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(RdfSerialization_AE.class, "uriFactoryClassName");

	/**
	 * The name of the UriFactory implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the UriFactory implementation to use")
	private String uriFactoryClassName;

	/**
	 * this {@link UriFactory} will be initialized based on the class name specified by the
	 * uriFactoryClassName parameter
	 */
	private UriFactory uriFactory;

	public final static String PARAM_OUTPUT_DIRECTORY = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "outputDirectory");
	@ConfigurationParameter(mandatory = true, description = "Describes the location where generated RDF files will be placed.")
	private File outputDirectory;

	public final static String PARAM_FILE_PREFIX = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "outputFilePrefix");
	@ConfigurationParameter(mandatory = true, description = "This string will be used as a prefix to the output files.")
	private String outputFilePrefix;

	public final static String PARAM_RDF_FORMAT = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "rdfFormat");
	@ConfigurationParameter(mandatory = true, description = "This string specifies the RDF format to use")
	private RdfFormat rdfFormat;

	public final static String PARAM_AO_SELECTOR_TYPE = ConfigurationParameterFactory.createConfigurationParameterName(
			RdfSerialization_AE.class, "aoSelectorType");

	private static final Object DOCUMENT_URI_KEY = "documentUri";

	private static final Object DOCUMENT_URL_KEY = "documentUrl";
	@ConfigurationParameter(mandatory = true, description = "This string specifies the AO Text Selector type to use")
	private AoSelectorType aoSelectorType;

	private RDFWriter documentRdfWriter;
	private RDFWriter annotationRdfWriter;
	// private RdfFormat format;
	private Logger logger;

	// public final static String PARAM_ANNOTATION_URI_LOCAL_NAME_PREFIX =
	// ConfigurationParameterFactory
	// .createConfigurationParameterName(RdfSerialization_AE.class, "annotationUriLocalNamePrefix");
	// @ConfigurationParameter(mandatory = true, description =
	// "This string specifies a prefix for the annotation local name (which will include an incremented count, e.g. CRAFT_ANNOTATION_123")
	// private String annotationUriLocalNamePrefix;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		logger = context.getLogger();
		super.initialize(context);

		// format = RdfFormat.NTRIPLES;
		// if (rdfFormat.equalsIgnoreCase("rdf/xml"))
		// format = RdfFormat.RDFXML;
		// else if (rdfFormat.equalsIgnoreCase("n3"))
		// format = RdfFormat.N3;
		// else if (rdfFormat.equalsIgnoreCase("turtle"))
		// format = RdfFormat.TURTLE;
		// else if (rdfFormat.equalsIgnoreCase("ntriples"))
		// format = RdfFormat.NTRIPLES;
		// else if (rdfFormat.equalsIgnoreCase("nquads"))
		// format = RdfFormat.NQUADS;
		// else
		// logger.log(
		// Level.WARNING,
		// String.format(
		// "Unknown RDF format requested: %s. N-Triple format will be output by default. Valid formats include: ntriples, rdfxml, n3, and turtle.",
		// rdfFormat));

		File documentRdfOutputFile = getDocumentRdfOutputFile();
		File annotationRdfOutputFile = getAnnotationRdfOutputFile();
		documentRdfWriter = RdfUtil.openWriter(documentRdfOutputFile, CharacterEncoding.UTF_8, rdfFormat);
		annotationRdfWriter = RdfUtil.openWriter(annotationRdfOutputFile, CharacterEncoding.UTF_8, rdfFormat);

		writeStatements(CcpUriUtil.getCcpFoafOrganizationStmts(), annotationRdfWriter);

		documentMetaDataExtractor = (DocumentMetaDataExtractor) ConstructorUtil
				.invokeConstructor(documentMetadataExtractorClassName);
		annotationDataExtractor = (RdfAnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);

		documentRdfGenerator = (DocumentRdfGenerator) ConstructorUtil.invokeConstructor(documentRdfGeneratorClassName);
		annotationRdfGenerator = (AnnotationRdfGenerator) ConstructorUtil
				.invokeConstructor(annotationRdfGeneratorClassName);
		uriFactory = (UriFactory) ConstructorUtil.invokeConstructor(uriFactoryClassName);

	}

	private void writeStatements(Collection<? extends Statement> stmts, RDFWriter writer) {
		for (Statement s : stmts) {
			try {
				writer.handleStatement(s);
			} catch (RDFHandlerException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private File getDocumentRdfOutputFile() {
		return new File(outputDirectory, String.format("%s-documents.%s", outputFilePrefix,
				rdfFormat.defaultFileExtension()));
	}

	private File getAnnotationRdfOutputFile() {
		return new File(outputDirectory, String.format("%s-annotations.%s", outputFilePrefix,
				rdfFormat.defaultFileExtension()));
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		writeDocumentStatements(jcas);
		URI documentUri = documentRdfGenerator.getDocumentUri();
		URL documentUrl = documentRdfGenerator.getDocumentUrl();
		Map<String, Set<Statement>> annotationKeyToUrisMap = new HashMap<String, Set<Statement>>();

		Map<String, URI> annotationKeyToUriMap = new HashMap<String, URI>();
		Map<String, Set<URI>> annotationKeyToSelectorURIsMap = new HashMap<String, Set<URI>>();
		Map<String, URI> annotationKeyToSemanticInstanceUriMap = new HashMap<String, URI>();

		/* Write the annotation RDF */ 
		/* TODO - make this CCP type system independent by removing the reference to the CCPTextAnnotation type */
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (!annotationKeyToUrisMap.containsKey(annotationDataExtractor.getAnnotationKey(ccpTa))) {
				if (!mentionName.equals("syntactic context") && !mentionName.equals("continuant")) {
					Collection<? extends Statement> stmts = annotationRdfGenerator.generateRdf(annotationDataExtractor,
							ccpTa, uriFactory, documentUri, documentUrl, jcas.getDocumentText(), annotationKeyToUriMap,
							annotationKeyToSelectorURIsMap, annotationKeyToSemanticInstanceUriMap);
					writeStatements(stmts, annotationRdfWriter);
				}
			}
		}

	}

	private void writeDocumentStatements(JCas jcas) {
		Collection<Statement> documentStmts = documentRdfGenerator.generateRdf(jcas, documentMetaDataExtractor);
		writeStatements(documentStmts, documentRdfWriter);
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		RdfUtil.closeWriter(documentRdfWriter);
		RdfUtil.closeWriter(annotationRdfWriter);

		logger.log(Level.INFO, "Validating RDF annotation and document files...");
		// try {
		// for (String line : FileReaderUtil.loadLinesFromFile(getDocumentRdfOutputFile(),
		// CharacterEncoding.UTF_8)) {
		// System.out.println("DOC RDF LINE: " + line);
		// }
		//
		// for (String line : FileReaderUtil.loadLinesFromFile(getAnnotationRdfOutputFile(),
		// CharacterEncoding.UTF_8)) {
		// System.out.println("ANN RDF LINE: " + line);
		// }
		// } catch (IOException e) {
		// throw new AnalysisEngineProcessException(e);
		// }

		/*
		 * The NQUADS validator requires there to be 4 things inside angle brackets on each line of
		 * the file. AG will load a file containing a mixture of 3 or 4 things on a line, i.e. some
		 * triples and some quads. Because of this, and because we want to be able to generate
		 * triples that use the default context, we won't validate the files if the NQUADS format is
		 * used.
		 */
		if (!rdfFormat.equals(RdfFormat.NQUADS)) {
			RdfValidator rdfValidator = RdfValidatorFactory.getRdfValidator(rdfFormat);
			if (!rdfValidator.isValidRdf(getDocumentRdfOutputFile(), CharacterEncoding.UTF_8, "http://kabob",
					org.apache.log4j.Logger.getLogger(getClass())))
				throw new IllegalStateException("RDF Validation failed for file: "
						+ getDocumentRdfOutputFile().getAbsolutePath());
			if (!rdfValidator.isValidRdf(getAnnotationRdfOutputFile(), CharacterEncoding.UTF_8, "http://kabob",
					org.apache.log4j.Logger.getLogger(getClass())))
				throw new IllegalStateException("RDF Validation failed for file: "
						+ getDocumentRdfOutputFile().getAbsolutePath());
		}
	}

	/**
	 * @param tsd
	 * @param outputDirectory
	 * @param outputFilePrefix
	 * @param selectorType
	 *            TODO: Is this parameter unnecessary since the annotaitionRdfGenerator class seems
	 *            to also indicate a selector???
	 * @param format
	 * @param documentMetaDataExtractorClass
	 * @param annotationDataExtractorClass
	 * @param annotationRdfGeneratorClass
	 * @param documentRdfGeneratorClass
	 * @param documentUrlFactoryClass
	 * @param uriFactoryClass
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File outputDirectory,
			String outputFilePrefix, AoSelectorType selectorType, RdfFormat format,
			Class<? extends DocumentMetaDataExtractor> documentMetaDataExtractorClass,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass,
			Class<? extends AnnotationRdfGenerator> annotationRdfGeneratorClass,
			Class<? extends DocumentRdfGenerator> documentRdfGeneratorClass, Class<? extends UriFactory> uriFactoryClass)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(RdfSerialization_AE.class, tsd,
				RdfSerialization_AE.PARAM_FILE_PREFIX, outputFilePrefix, RdfSerialization_AE.PARAM_RDF_FORMAT,
				format.name(), RdfSerialization_AE.PARAM_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath(),
				PARAM_AO_SELECTOR_TYPE, selectorType.name(), PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS,
				documentMetaDataExtractorClass.getName(), PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS,
				annotationDataExtractorClass.getName(), PARAM_ANNOTATION_RDF_GENERATOR_CLASS,
				annotationRdfGeneratorClass.getName(), PARAM_DOCUMENT_RDF_GENERATOR_CLASS,
				documentRdfGeneratorClass.getName(), PARAM_URI_FACTORY_CLASS, uriFactoryClass.getName());
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
