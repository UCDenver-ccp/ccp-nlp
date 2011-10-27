/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.gene.evaluation;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Ignore;
import org.junit.Test;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.AnalysisEngineType;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.gene.HomologeneGroupGeneNormalization_AAE;

/**
 * Evaluates the HomologeneGroupGeneNormalization pipeline against gold standard corpora
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@Ignore("Need to add homologene group generation from entrez gene IDs in the gold standard")
public class HomologeneGroupGeneNormalizationAE_Evaluation extends DefaultTestCase {

	private static final TypeSystemDescription TSD = TypeSystemDescriptionFactory.createTypeSystemDescription(
			TypeSystemUtil.CCP_TYPE_SYSTEM, "edu.ucdenver.ccp.nlp.ext.uima.syntax.TypeSystem");

	private static final File HOMOLOGENE_DICTIONARY_DIRECTORY = new File(
			"/data/NOT_BACKED_UP/projects/btrc/homologene-dictionary/dictionary");

	@Test
	public void evaluateOnBioCreative3TrainingDataSet() throws AnalysisEngineProcessException,
			ResourceInitializationException {
		BioCreative3GnEvaluationPipeline evalPipeline = new BioCreative3GnEvaluationPipeline(TSD);
		evalPipeline.evaluateNormalizationPipeline(
				HomologeneGroupGeneNormalization_AAE.getAggregateDescription(TSD, HOMOLOGENE_DICTIONARY_DIRECTORY),
				AnalysisEngineType.AGGREGATE);
	}

}
