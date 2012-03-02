/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.dictionary;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.SearchStrategyParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer.OffsetTokenizerFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperAaeFactory {

	/**
	 * Returns an aggregate description for a UIMA pipeline containing the OffsetTokenizer followed
	 * by the ConceptMapper
	 * 
	 * @param tsd
	 * @param dictionaryFile
	 * @param caseMatchParamValue
	 * @param searchStrategyParamValue
	 * @param spanFeatureStructureClass 
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription getOffsetTokenizerConceptMapperPipelineDescription(TypeSystemDescription tsd,
			File dictionaryFile, CaseMatchParamValue caseMatchParamValue,
			SearchStrategyParamValue searchStrategyParamValue, Class<? extends Annotation> spanFeatureStructureClass) throws UIMAException, IOException {

		/* Init the tokenizer */
		Object[] tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue);
		AnalysisEngineDescription offsetTokenizerDescription = OffsetTokenizerFactory.buildOffsetTokenizerDescription(
				tsd, tokenizerConfigData);

		/* Init the concept mapper */
		AnalysisEngineDescription conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				dictionaryFile, caseMatchParamValue, searchStrategyParamValue, spanFeatureStructureClass,
				offsetTokenizerDescription);

		return AnalysisEngineFactory.createAggregateDescription(offsetTokenizerDescription, conceptMapperDescription);

	}
}
