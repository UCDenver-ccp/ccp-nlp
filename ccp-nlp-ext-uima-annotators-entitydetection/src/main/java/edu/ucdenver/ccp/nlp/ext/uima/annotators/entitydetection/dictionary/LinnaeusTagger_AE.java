/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import uk.ac.man.entitytagger.Mention;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDecorator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDecorator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.nlp.wrapper.linnaeus.LinnaeusUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class LinnaeusTagger_AE extends JCasAnnotator_ImplBase {

	public final static String ATTRIBUTE_IS_AMBIGUOUS = "isAmbiguous";
	public final static String ATTRIBUTE_HAS_IDENTIFIER = "hasIdentifier";
	public final static String ATTRIBUTE_PROBABILITY = "probability";

	/* ==== Linnaeus properties file configuration ==== */
	public final static String PARAM_LINNAEUS_PROPERTIES_FILE = ConfigurationParameterFactory
			.createConfigurationParameterName(LinnaeusTagger_AE.class, "linnaeusPropertiesFile");
	@ConfigurationParameter(mandatory = true, description = "The path to the properties file used by the Linnaeus tagger during initialization.")
	private File linnaeusPropertiesFile;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the document metadata extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(LinnaeusTagger_AE.class, "documentMetadataExtractorClassName");

	/**
	 * The name of the {@link DocumentMetaDataExtractor} implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetaDataExtractor documentMetaDataExtractor;

	/* ==== AnnotationDecorator configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the {@link AnnotationDecorator}
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DECORATOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(LinnaeusTagger_AE.class, "annotationDecoratorClassName");

	/**
	 * The name of the {@link AnnotationDecorator} implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDecorator implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDecorator")
	private String annotationDecoratorClassName;

	/**
	 * this {@link AnnotationDecorator} will be initialized based on the class name specified by the
	 * annotationDecoratorClassName parameter
	 */
	private AnnotationDecorator annotationDecorator;

	/**
	 * Utility used to interact with the Linnaeus tagger
	 */
	private LinnaeusUtil linnaeusUtil;

	/**
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		documentMetaDataExtractor = (DocumentMetaDataExtractor) ConstructorUtil
				.invokeConstructor(documentMetadataExtractorClassName);
		annotationDecorator = (AnnotationDecorator) ConstructorUtil.invokeConstructor(annotationDecoratorClassName);
		linnaeusUtil = new LinnaeusUtil(linnaeusPropertiesFile);

	}

	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String documentId = documentMetaDataExtractor.extractDocumentId(jCas);

		Collection<Mention> matches = linnaeusUtil.searchText(documentId, jCas.getDocumentText());

		for (Mention mention : matches) {
			// System.out.println("toString: " + mention.toString());
			// System.out.println("comment: " + mention.getComment());
			// System.out.println("docid: " + mention.getDocid());
			// System.out.println("end: " + mention.getEnd());
			// System.out.println("idsToString: " + mention.getIdsToString());
			// System.out.println("most probable id: " + mention.getMostProbableID());
			// System.out.println("most probable id with id line: " +
			// mention.getMostProbableIDWithIdLine());
			// System.out.println("start: " + mention.getStart());
			// System.out.println("text: " + mention.getText());
			// System.out.println("ids[]: " + Arrays.toString(mention.getIds()));
			// System.out.println("idsWithLineNumbers[]: " +
			// Arrays.toString(mention.getIdsWithLineNumbers()));
			// System.out.println("probabilities: " + Arrays.toString(mention.getProbabilities()));
			Span span = new Span(mention.getStart(), mention.getEnd());
			Annotation annotation = annotationDecorator.newAnnotation(jCas, mention.getMostProbableID(), span);
			annotationDecorator.addAnnotationAttribute(annotation, ATTRIBUTE_IS_AMBIGUOUS, mention.isAmbigous());
			if (mention.getProbabilities() != null)
				annotationDecorator.addAnnotationAttribute(annotation, ATTRIBUTE_PROBABILITY,
						mention.getProbabilities()[0]);
			for (String id : mention.getIds())
				annotationDecorator.addAnnotationAttribute(annotation, ATTRIBUTE_HAS_IDENTIFIER, id);
		}
	}

	public static AnalysisEngineDescription getAnalysisEngineDescription(TypeSystemDescription tsd,
			File dictionaryFile, File stopWordListFile, File synonymsAcronymsFile, File frequencyFile,
			File supplementaryDictionaryFile, List<String> extraPropertyLines,
			Class<? extends DocumentMetaDataExtractor> documentMetadataExtractorClass,
			Class<? extends AnnotationDecorator> annotationDecoratorClass) throws IOException,
			ResourceInitializationException {
		File propertiesFile = LinnaeusUtil.createLinnaeusPropertiesFile(dictionaryFile, stopWordListFile,
				synonymsAcronymsFile, frequencyFile, supplementaryDictionaryFile, extraPropertyLines);
		return AnalysisEngineFactory.createPrimitiveDescription(LinnaeusTagger_AE.class, tsd,
				LinnaeusTagger_AE.PARAM_LINNAEUS_PROPERTIES_FILE, propertiesFile.getAbsolutePath(),
				LinnaeusTagger_AE.PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS, documentMetadataExtractorClass.getName(),
				LinnaeusTagger_AE.PARAM_ANNOTATION_DECORATOR_CLASS, annotationDecoratorClass.getName());
	}

	public static AnalysisEngineDescription getAnalysisEngineDescription(TypeSystemDescription tsd,
			File dictionaryFile, List<String> extraPropertyLines) throws ResourceInitializationException, IOException {
		return getAnalysisEngineDescription(tsd, dictionaryFile, null, null, null, null, extraPropertyLines,
				CcpDocumentMetaDataExtractor.class, CcpAnnotationDecorator.class);
	}

}
