/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class SentenceAnnotationProcessor extends JCasAnnotator_ImplBase {

	/* ==== AnnotationDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceAnnotationProcessor.class, "annotationDataExtractorClassName");

	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.uima.shims.annotation.impl.DefaultAnnotationDataExtractor")
	private String annotationDataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private AnnotationDataExtractor annotationDataExtractor;


	
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_SENTENCE_ANNOTATION_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceAnnotationProcessor.class, "sentenceAnnotationName");

	@ConfigurationParameter(description = "name of the sentence annotation type to process", defaultValue = "sentence")
	private String sentenceAnnotationName;

	
	
	private Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// String documentID = UIMA_Util.getDocumentID(jcas);

		UIMA_Util uimaUtil = new UIMA_Util();

		/*
		 * Check to see if there are any Sentence annotations in this CAS. If there are, then
		 * tokenize each sentence individually. If there are not, then treat the document text as a
		 * single sentence and tokenize it.
		 */
		List<TextAnnotation> annotationsToPutInCas = new ArrayList<TextAnnotation>();
		int sentenceCount = 0;
		for (FSIterator<Annotation> annotIter = jCas.getJFSIndexRepository().getAnnotationIndex()
				.iterator(); annotIter.hasNext();) {
			Annotation annot = annotIter.next();
			String type = annotationDataExtractor.getAnnotationType(annot);
			if (type != null && type.toLowerCase().endsWith(sentenceAnnotationName)) {
//			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotIter.next();
//			if (ccpTa.getClassMention().getMentionName().equalsIgnoreCase(ClassMentionTypes.SENTENCE)) {
				sentenceCount++;
				annotationsToPutInCas.addAll(processSentence(annot.getCoveredText().replaceAll("\\n"," "), annot.getBegin(), jCas));
			}
		}
		if (sentenceCount == 0) {
			logger.log(Level.INFO, "No sentences in CAS, processing document text as a whole...");
			annotationsToPutInCas.addAll(processSentence(jCas.getDocumentText(), 0, jCas));
		}
		/* add the TextAnnotations to the JCas */
		uimaUtil.putTextAnnotationsIntoJCas(jCas, annotationsToPutInCas);

	}

	protected abstract List<TextAnnotation> processSentence(String sentenceText, int sentenceStartOffset, JCas jCas);

}
