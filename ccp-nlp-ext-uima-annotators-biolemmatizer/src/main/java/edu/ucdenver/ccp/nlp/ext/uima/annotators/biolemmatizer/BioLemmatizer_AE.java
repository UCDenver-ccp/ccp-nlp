/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.biolemmatizer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry.Lemma;
import edu.ucdenver.ccp.nlp.core.uima.build.XmlDescriptorWriter;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.ext.syntax.PartOfSpeech;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeInserter;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioLemmatizer_AE extends JCasAnnotator_ImplBase {

	private static final String ANNOTATOR_DESCRIPTION = String
			.format("This annotator processes tokens in the CAS and inserts corresponding lemmas. This annotator is "
					+ "type-system-independent and relies on implementations of TokenAttributeExtractor, "
					+ "TokenAttributeInserter, and AnnotationDataExtractor in order to function as intended.");

	private static final String ANNOTATOR_VENDOR = "UC Denver - CCP";

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_TOKEN_ATTRIBUTE_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(BioLemmatizer_AE.class, "tokenAttributeExtractorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the TokenAttributeExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.impl.CcpTokenAttributeExtractor")
	private String tokenAttributeExtractorClassName;

	/**
	 * this {@link TokenAttributeExtractor} will be initialized based on the class name specified by
	 * the tokenAttributeExtractorClassName parameter
	 */
	private TokenAttributeExtractor tokenAttributeExtractor;

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute inserter
	 * implementation to use
	 */
	public static final String PARAM_TOKEN_ATTRIBUTE_INSERTER_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(BioLemmatizer_AE.class, "tokenAttributeInserterClassName");

	/**
	 * The name of the TokenAttributeInserter implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the TokenAttributeInserter implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.impl.CcpTokenAttributeInserter")
	private String tokenAttributeInserterClassName;

	/**
	 * this {@link TokenAttributeInserter} will be initialized based on the class name specified by
	 * the tokenAttributeInserterClassName parameter
	 */
	private TokenAttributeInserter tokenAttributeInserter;

	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(BioLemmatizer_AE.class, "annotationDataExtractorClassName");

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

	/**
	 * This {@link BioLemmatizer} will do the bulk of the work in the
	 * {@link BioLemmatizer_AE#process(JCas)} method
	 */
	private BioLemmatizer bioLemmatizer;

	/**
	 * Initializes the {@link BioLemmatizer} that will be used by the
	 * {@link BioLemmatizer_AE#process(JCas)} method
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		bioLemmatizer = new BioLemmatizer();
		tokenAttributeExtractor = (TokenAttributeExtractor) ConstructorUtil
				.invokeConstructor(tokenAttributeExtractorClassName);
		tokenAttributeInserter = (TokenAttributeInserter) ConstructorUtil
				.invokeConstructor(tokenAttributeInserterClassName);
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
	}

	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	/* AnalysisEngineProcessException is never thrown by this process(JCas) method. */
	@SuppressWarnings("unused")
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Iterator<Annotation> annotIter = jCas.getJFSIndexRepository().getAnnotationIndex().iterator(); annotIter
				.hasNext();) {
			Annotation annotation = annotIter.next();
			if (annotationDataExtractor.getAnnotationType(annotation).equals(tokenAttributeExtractor.getTokenType())) {
				List<PartOfSpeech> partsOfSpeech = tokenAttributeExtractor.getPartsOfSpeech(annotation);
				String coveredText = annotationDataExtractor.getCoveredText(annotation);
				for (PartOfSpeech pos : partsOfSpeech) {
					LemmataEntry lemmata = bioLemmatizer.lemmatizeByLexiconAndRules(coveredText, pos.getPosTag());
					for (Lemma lemma : lemmata.getLemmas())
						tokenAttributeInserter.insertLemma(
								annotation,
								new edu.ucdenver.ccp.nlp.ext.syntax.Lemma(lemma.getLemma(), new PartOfSpeech(lemma
										.getPos(), lemma.getTagSetName())));
				}
			}
		}
	}

	/**
	 * Initializes an {@link AnalysisEngine} that will determine lemmas for tokens that are present
	 * in the {@link CAS}
	 * 
	 * @param tsd
	 * @param annotationDataExtractorClass
	 * @param tokenAttributeExtractorClass
	 * @param tokenAttributeInserterClass
	 * @return
	 * @throws ResourceInitializationException
	 * 
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass,
			Class<? extends TokenAttributeExtractor> tokenAttributeExtractorClass,
			Class<? extends TokenAttributeInserter> tokenAttributeInserterClass) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(BioLemmatizer_AE.class, tsd,
				PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS, annotationDataExtractorClass.getName(),
				PARAM_TOKEN_ATTRIBUTE_EXTRACTOR_CLASS, tokenAttributeExtractorClass.getName(),
				PARAM_TOKEN_ATTRIBUTE_INSERTER_CLASS, tokenAttributeInserterClass.getName());
	}

	public static void exportXmlDescriptor(File baseDescriptorDirectory, String version) {
		try {
			Class<BioLemmatizer_AE> cls = BioLemmatizer_AE.class;
			AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(cls,
					TypeSystemUtil.getCcpTypeSystem());
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
