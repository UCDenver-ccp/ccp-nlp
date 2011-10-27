/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters;

import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.DocumentLevelAnnotationCreator;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * <p>
 * A simple utility analysis engine that creates document-level annotations of the specified type. A
 * document-level annotation is defined as a spanless annotation (or an annotation with meaningless
 * span [0..1] for example) that represents information contained in a document. One example would
 * be to use the document-level annotation to catalog the genes that have been discussed in the
 * document. Here we are interested not in each mention of a particular gene, but simply the fact
 * that a particular gene was mentioned at least once.
 * <p>
 * The user can optionally stipulate that certain slots be present in order for the annotation to be
 * created as a document-level annotation. To take our gene document-level annotation a step
 * further, we might want to require the gene mentions to have been assigned a gene_id slot. This
 * way, we could use the existing annotation comparison machinery to judge the performance of a gene
 * normalization system for instance. using our existing comparison machinery.
 * 
 * @author William A Baumgartner Jr.
 * 
 */
public class DocumentLevelAnnotationCreator_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ANNOTATION_TYPE_TO_CONVERT = ConfigurationParameterFactory
			.createConfigurationParameterName(DocumentLevelAnnotationCreator_AE.class, "annotationTypeToConvert");
	@ConfigurationParameter(mandatory = true)
	private String annotationTypeToConvert;

	public static final String PARAM_REQUIRED_SLOTS = ConfigurationParameterFactory.createConfigurationParameterName(
			DocumentLevelAnnotationCreator_AE.class, "requiredSlots");
	@ConfigurationParameter(mandatory = true)
	private Set<String> requiredSlots;

	public static final String PARAM_ANNOTATOR_IDS_TO_IGNORE = ConfigurationParameterFactory
			.createConfigurationParameterName(DocumentLevelAnnotationCreator_AE.class, "annotatorIdsToIgnore");
	@ConfigurationParameter(mandatory = true)
	private Set<Integer> annotatorIdsToIgnore;

	private DocumentLevelAnnotationCreator documentLevelAnnotationCreator;
	private Logger logger;

	@Override
	public void initialize(UimaContext ac) throws ResourceInitializationException {
		super.initialize(ac);
		logger = ac.getLogger();
		documentLevelAnnotationCreator = new DocumentLevelAnnotationCreator(annotationTypeToConvert, requiredSlots);
	}

	/**
	 * cycle through all annotations and convert the appropriate ones to document-level annotations
	 */
	public void process(JCas jcas) {
		UIMA_Util uimaUtil = new UIMA_Util();
		List<TextAnnotation> annotationsInCas = UIMA_Util.getAnnotationsFromCas(jcas);
		List<TextAnnotation> documentLevelAnnotations = documentLevelAnnotationCreator.createDocumentLevelAnnotations(
				annotationsInCas, annotatorIdsToIgnore);
		logger.log(Level.INFO,"Adding " + documentLevelAnnotations.size() + " document annotations to the cas");
		uimaUtil.putTextAnnotationsIntoJCas(jcas, documentLevelAnnotations);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			String annotationTypeToConvert, String[] requiredSlotNames, Integer[] annotatorIdsToIgnore)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(DocumentLevelAnnotationCreator_AE.class, tsd,
				PARAM_ANNOTATION_TYPE_TO_CONVERT, annotationTypeToConvert, PARAM_REQUIRED_SLOTS, requiredSlotNames,
				PARAM_ANNOTATOR_IDS_TO_IGNORE, annotatorIdsToIgnore);
	}

}
