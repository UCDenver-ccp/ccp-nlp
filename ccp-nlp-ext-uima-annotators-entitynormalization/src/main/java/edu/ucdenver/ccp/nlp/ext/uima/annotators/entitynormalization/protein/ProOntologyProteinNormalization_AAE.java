/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.protein;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.ClassMentionConverter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.ABNER_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.BannerEntityTagger_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.filter.AnnotationConsensusFilter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.filter.AnnotationSetFilter_AE;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ProOntologyProteinNormalization_AAE {

	public static AnalysisEngineDescription getAggregateDescription(TypeSystemDescription tsd,
			File proOntologyDictionaryDirectory, AnalysisEngineDescription sentenceDetectorDescription)
			throws ResourceInitializationException {

		AnalysisEngineDescription abnerBcDescription = ABNER_AE.createAnalysisEngineDescription_BioCreative(tsd);
		AnalysisEngineDescription abnerNlpbaDescription = ABNER_AE.createAnalysisEngineDescription_NLPBA(tsd);
		AnalysisEngineDescription bannerBcDescription = BannerEntityTagger_AE
				.createAnalysisEngineDescription_BioCreative(tsd);

		AnalysisEngineDescription geneToProteinConverter = ClassMentionConverter_AE.createAnalysisEngineDescription(
				tsd, "protein", new String[] { "gene" });

		AnalysisEngineDescription consensusFilter = AnalysisEngineFactory.createPrimitiveDescription(
				AnnotationConsensusFilter_AE.class, tsd, AnnotationConsensusFilter_AE.PARAM_CONSENSUS_THRESHOLD, 2,
				AnnotationConsensusFilter_AE.PARAM_ANNOTATION_TYPE_OF_INTEREST, "protein",
				AnnotationConsensusFilter_AE.PARAM_ANNOTATION_SETS_TO_IGNORE, new int[] {});

		// the annotation set id for consensus annotations is 88 - this AE will remove all other
		// annotations
		// zero is typically the annotation set for gold standard annotations
		AnalysisEngineDescription annotationSetFilter = AnalysisEngineFactory.createPrimitiveDescription(
				AnnotationSetFilter_AE.class, tsd, AnnotationSetFilter_AE.PARAM_ANNOTATIONSET_IDS_TO_KEEP_LIST,
				new int[] { 88, 0 });

		AnalysisEngineDescription proOntologyProteinNormalizer = ProOntologyProteinNormalizer_AE
				.createAnalysisEngineDescription(tsd, proOntologyDictionaryDirectory);

		/* @formatter:off*/
		return AnalysisEngineFactory.createAggregateDescription(
				sentenceDetectorDescription, 
				abnerBcDescription,
				abnerNlpbaDescription, 
				bannerBcDescription, 
				geneToProteinConverter, 
				consensusFilter,
				annotationSetFilter,
				proOntologyProteinNormalizer);
		/* @formatter:on*/
	}

}
