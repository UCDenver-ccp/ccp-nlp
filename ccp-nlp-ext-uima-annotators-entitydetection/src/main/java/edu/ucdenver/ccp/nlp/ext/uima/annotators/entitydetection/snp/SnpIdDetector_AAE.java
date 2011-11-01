/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.snp;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.LingPipeSentenceDetector_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.SentenceDetector_AE;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SnpIdDetector_AAE {

	public static AnalysisEngineDescription getAggregateDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {

		AnalysisEngineDescription sentenceDetectorDescription = AnalysisEngineFactory.createPrimitiveDescription(
				LingPipeSentenceDetector_AE.class, tsd,
				SentenceDetector_AE.PARAM_TREAT_LINE_BREAKS_AS_SENTENCE_BOUNDARIES, true);

		AnalysisEngineDescription referenceSnpIdDetectorDescription = SnpIdDetector_AE
				.createAnalysisEngineDescription(tsd);

		return AnalysisEngineFactory.createAggregateDescription(sentenceDetectorDescription,
				referenceSnpIdDetectorDescription);

	}

}
