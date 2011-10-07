/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CasProfilerAE extends JCasAnnotator_ImplBase {

	/* ==== AnnotationDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(CasProfilerAE.class, "annotationDataExtractorClassName");

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

	/* ==== Profiler label configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for a text label assigned to this CasProfiler
	 */
	public static final String PARAM_PROFILER_LABEL = ConfigurationParameterFactory.createConfigurationParameterName(
			CasProfilerAE.class, "casProfilerLabel");

	/**
	 * The label to identify the output of this {@CasProfiler}
	 */
	@ConfigurationParameter(mandatory = false, description = "label used to identify the output of this CasProfiler", defaultValue = "")
	private String casProfilerLabel;

	private Map<String, Integer> annotationTypeToCountMap;

	private Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
		annotationTypeToCountMap = new HashMap<String, Integer>();
		logger = context.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (FSIterator<Annotation> annotIter = jCas.getJFSIndexRepository().getAnnotationIndex().iterator(); annotIter
				.hasNext();) {
			Annotation annotation = annotIter.next();
			String type = annotationDataExtractor.getAnnotationType(annotation);
			if (type == null)
				type = "NULL ANNOTATION TYPE";
			/* normalizes ontology terms by counting based on namespace only */
			if (type.contains(StringConstants.COLON))
				type = type.substring(0, type.indexOf(StringConstants.COLON));
			CollectionsUtil.addToCountMap(type, annotationTypeToCountMap);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.AnalysisComponent_ImplBase#collectionProcessComplete()
	 */
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		List<String> typeList = new ArrayList<String>(annotationTypeToCountMap.keySet());
		Collections.sort(typeList);
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n===============================================");
		buffer.append("\nCAS Profiler Output -- " + casProfilerLabel);
		for (String type : typeList)
			buffer.append("\n# " + type + " annotations: " + annotationTypeToCountMap.get(type));
		buffer.append("\n===============================================");
		logger.log(Level.INFO, buffer.toString());
	}

	/**
	 * Initializes a {@link CasProfilerAE} with the specified label. The default
	 * {@link AnnotationDataExtractor} implementation will be used.
	 * 
	 * @param tsd
	 * @param label
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String label)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(CasProfilerAE.class, tsd, CasProfilerAE.PARAM_PROFILER_LABEL,
				label);
	}

}
