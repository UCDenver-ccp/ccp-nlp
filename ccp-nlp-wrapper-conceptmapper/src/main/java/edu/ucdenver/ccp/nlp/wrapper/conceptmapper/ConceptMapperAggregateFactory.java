/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.OperationalProperties;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.SearchStrategyParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer.OffsetTokenizerFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperAggregateFactory {

	/**
	 * Returns an aggregate description for a UIMA pipeline containing the OffsetTokenizer followed
	 * by the ConceptMapper
	 * 
	 * @param tsd
	 * @param dictionaryFile
	 * @param caseMatchParamValue
	 * @param searchStrategyParamValue
	 * @param spanFeatureStructureClass
	 *            commonly edu.ucdenver.ccp.nlp.ext.uima.types.Sentence
	 * @param stemmerClass
	 *            optional, leave null if not desired
	 * @param stopwordList
	 * @param orderIndependentLookup
	 * @param findAllMatches
	 * @param replaceCommaWithAnd
	 * @return
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription getOffsetTokenizerConceptMapperAggregateDescription(
			TypeSystemDescription tsd, File dictionaryFile, CaseMatchParamValue caseMatchParamValue,
			SearchStrategyParamValue searchStrategyParamValue, Class<? extends Annotation> spanFeatureStructureClass,
			Class<? extends Stemmer> stemmerClass, String[] stopwordList, boolean orderIndependentLookup,
			boolean findAllMatches, boolean replaceCommaWithAnd) throws UIMAException, IOException {

		/* Init the tokenizer */
		Object[] tokenizerConfigData = null;
		if (stemmerClass == null) {
			tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue);
		} else {
			tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue, stemmerClass);
		}
		AnalysisEngineDescription offsetTokenizerDescription = OffsetTokenizerFactory.buildOffsetTokenizerDescription(
				tsd, tokenizerConfigData);

		OperationalProperties operationalProperties = offsetTokenizerDescription.getAnalysisEngineMetaData()
				.getOperationalProperties();

		// offsetTokenizerDescription.setImplementationName("offset tokenizer");
		// System.out.println(offsetTokenizerDescription.getAnalysisEngineMetaData().getOperationalProperties().isMultipleDeploymentAllowed());

		/* Init the concept mapper */
		AnalysisEngineDescription conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				dictionaryFile, caseMatchParamValue, searchStrategyParamValue, stemmerClass, stopwordList,
				orderIndependentLookup, findAllMatches, replaceCommaWithAnd, spanFeatureStructureClass,
				offsetTokenizerDescription);

		if (offsetTokenizerDescription.getAnalysisEngineMetaData().getOperationalProperties() == null) {
			offsetTokenizerDescription.getAnalysisEngineMetaData().setOperationalProperties(operationalProperties);
		}

		return AnalysisEngineFactory.createAggregateDescription(offsetTokenizerDescription, conceptMapperDescription);
	}

}
