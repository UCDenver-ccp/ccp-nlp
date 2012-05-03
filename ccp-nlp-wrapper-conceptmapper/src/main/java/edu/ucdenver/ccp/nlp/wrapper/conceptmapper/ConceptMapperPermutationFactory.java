/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.collections.CombinationsUtil;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperAggregateFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.SearchStrategyParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.UimaConfigParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer.ConceptMapperStemmerFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer.ConceptMapperStemmerFactory.StemmerType;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer.OffsetTokenizerFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperPermutationFactory {
	private static Logger logger = Logger.getLogger(ConceptMapperPermutationFactory.class);

	/**
	 * 
	 */
	private static final String CASE_MATCH_KEY = "CaseMatch:";

	/**
	 * 
	 */
	private static final String SEARCH_STRATEGY_KEY = "SearchStrategy:";

	private static final String STEMMER_KEY = "Stemmer:";
	private static final String STOPWORDS_KEY = "Stopwords:";

	private static final String ORDER_INDEPENDENT_LOOKUP_KEY = "OrderIndependentLookup:";

	private static final String REPLACE_COMMA_WITH_AND_KEY = "ReplaceCommaWithAnd:";

	private static final String FIND_ALL_MATCHES_KEY = "FindAllMatches:";

	public static final List<List<String>> PARAM_COMBINATIONS = CollectionsUtil.createList(CombinationsUtil
			.computeCombinations(buildParameterValueLists()));;

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperStemmerParam implements UimaConfigParamValue<Class<? extends Stemmer>> {
		BIOLEMMATIZER(StemmerType.BIOLEMMATIZER),
		PORTER(StemmerType.PORTER),
		NONE(StemmerType.NONE);

		private final StemmerType stemmerType;

		private ConceptMapperStemmerParam(StemmerType stemmerType) {
			this.stemmerType = stemmerType;
		}

		@Override
		public Class<? extends Stemmer> paramValue() {
			return ConceptMapperStemmerFactory.getStemmerClass(stemmerType);
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperStopWordsParam implements UimaConfigParamValue<String[]> {
		PUBMED(new String[] { "a", "about", "again", "all", "almost", "also", "although", "always", "among", "an",
				"and", "another", "any", "are", "as", "at", "be", "because", "been", "before", "being", "between",
				"both", "but", "by", "can", "could", "did", "do", "does", "done", "due", "during", "each", "either",
				"enough", "especially", "etc", "for", "found", "from", "further", "had", "has", "have", "having",
				"here", "how", "however", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "kg", "km",
				"made", "mainly", "make", "may", "mg", "might", "ml", "mm", "most", "mostly", "must", "nearly",
				"neither", "no", "nor", "obtained", "of", "often", "on", "our", "overall", "perhaps", "pmid", "quite",
				"rather", "really", "regarding", "seem", "seen", "several", "should", "show", "showed", "shown",
				"shows", "significantly", "since", "so", "some", "such", "than", "that", "the", "their", "theirs",
				"them", "then", "there", "therefore", "these", "they", "this", "those", "through", "thus", "to",
				"upon", "use", "used", "using", "various", "very", "was", "we", "were", "what", "when", "which",
				"while", "with", "within", "without", "would" }),
		NONE(new String[] {});

		private final String[] stopWords;

		private ConceptMapperStopWordsParam(String[] stopWords) {
			this.stopWords = stopWords;
		}

		@Override
		public String[] paramValue() {
			return stopWords;
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperOrderIndependentLookupParam implements UimaConfigParamValue<Boolean> {
		ON(true),
		OFF(false);

		private final Boolean doOrderIndependentLookup;

		private ConceptMapperOrderIndependentLookupParam(Boolean doOrderIndependentLookup) {
			this.doOrderIndependentLookup = doOrderIndependentLookup;
		}

		@Override
		public Boolean paramValue() {
			return doOrderIndependentLookup;
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperReplaceCommaWithAndParam implements UimaConfigParamValue<Boolean> {
		ON(true),
		OFF(false);

		private final Boolean replaceCommaWithAnd;

		private ConceptMapperReplaceCommaWithAndParam(Boolean replaceCommaWithAnd) {
			this.replaceCommaWithAnd = replaceCommaWithAnd;
		}

		@Override
		public Boolean paramValue() {
			return replaceCommaWithAnd;
		}
	}

	/**
	 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum ConceptMapperFindAllMatchesParam implements UimaConfigParamValue<Boolean> {
		YES(true),
		NO(false);

		private final Boolean findAllMatches;

		private ConceptMapperFindAllMatchesParam(Boolean findAllMatches) {
			this.findAllMatches = findAllMatches;
		}

		@Override
		public Boolean paramValue() {
			return findAllMatches;
		}
	}

	/**
	 * @return
	 * 
	 */
	private static List<Collection<String>> buildParameterValueLists() {
		List<Collection<String>> paramValueLists = new ArrayList<Collection<String>>();
		paramValueLists.add(getSearchStrategyParamValues());
		paramValueLists.add(getCaseMatchParamValues());
		paramValueLists.add(getStemmerParamValues());
		paramValueLists.add(getStopwordListParamValues());
		paramValueLists.add(getOrderIndependentLookupParamValues());
		paramValueLists.add(getFindAllMatchesParamValues());
		paramValueLists.add(getReplaceCommaWithAndParamValues());
		return paramValueLists;
	}

	/**
	 * @return a list of the different Concept Mapper "find all matches" parameter value options
	 */
	private static List<String> getFindAllMatchesParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperFindAllMatchesParam value : ConceptMapperFindAllMatchesParam.values()) {
			paramValues.add(FIND_ALL_MATCHES_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper replace comma with "and" parameter value
	 *         options
	 */
	private static List<String> getReplaceCommaWithAndParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperReplaceCommaWithAndParam value : ConceptMapperReplaceCommaWithAndParam.values()) {
			paramValues.add(REPLACE_COMMA_WITH_AND_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper order independent lookup parameter value
	 *         options
	 */
	private static List<String> getOrderIndependentLookupParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperOrderIndependentLookupParam value : ConceptMapperOrderIndependentLookupParam.values()) {
			paramValues.add(ORDER_INDEPENDENT_LOOKUP_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper stemmer parameter value options
	 */
	private static List<String> getStemmerParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperStemmerParam value : ConceptMapperStemmerParam.values()) {
			paramValues.add(STEMMER_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper stopword list parameter value options
	 */
	private static List<String> getStopwordListParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (ConceptMapperStopWordsParam value : ConceptMapperStopWordsParam.values()) {
			paramValues.add(STOPWORDS_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @return a list of the different Concept Mapper "search strategy" parameter value options
	 */
	private static List<String> getSearchStrategyParamValues() {
		List<String> searchStrategyParamValues = new ArrayList<String>();
		for (SearchStrategyParamValue value : SearchStrategyParamValue.values()) {
			searchStrategyParamValues.add(SEARCH_STRATEGY_KEY + value.name());
		}
		return searchStrategyParamValues;
	}

	/**
	 * @return a list of the different Concept Mapper "case match" parameter value options
	 */
	private static List<String> getCaseMatchParamValues() {
		List<String> paramValues = new ArrayList<String>();
		for (CaseMatchParamValue value : CaseMatchParamValue.values()) {
			paramValues.add(CASE_MATCH_KEY + value.name());
		}
		return paramValues;
	}

	/**
	 * @param conceptMapperPermutationNumber
	 * @param spanFeatureStructureClass
	 * @return
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static AnalysisEngineDescription buildConceptMapperAggregatePermutation(int conceptMapperPermutationNumber,
			TypeSystemDescription tsd, File dictionaryFile, Class<? extends Annotation> spanFeatureStructureClass)
			throws UIMAException, IOException {
		List<String> params = PARAM_COMBINATIONS.get(conceptMapperPermutationNumber);
		return buildConceptMapperAggregate(params, tsd, dictionaryFile, spanFeatureStructureClass);
	}

	private static AnalysisEngineDescription buildConceptMapperAggregate(List<String> paramValues,
			TypeSystemDescription tsd, File dictionaryFile, Class<? extends Annotation> spanFeatureStructureClass)
			throws UIMAException, IOException {

		CaseMatchParamValue caseMatchParamValue = getCaseMatchParamValue(paramValues);
		SearchStrategyParamValue searchStrategyParamValue = getSearchStrategyParamValue(paramValues);
		Class<? extends Stemmer> stemmerClass = getStemmerClass(paramValues);
		String[] stopwordList = getStopWordList(paramValues);
		boolean orderIndependentLookup = getOrderIndependentLookup(paramValues);
		boolean findAllMatches = getFindAllMatches(paramValues);
		boolean replaceCommaWithAnd = getReplaceCommaWithAnd(paramValues);

		return ConceptMapperAggregateFactory.getOffsetTokenizerConceptMapperAggregateDescription(tsd, dictionaryFile,
				caseMatchParamValue, searchStrategyParamValue, spanFeatureStructureClass, stemmerClass, stopwordList,
				orderIndependentLookup, findAllMatches, replaceCommaWithAnd);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static SearchStrategyParamValue getSearchStrategyParamValue(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(0), SEARCH_STRATEGY_KEY);
		return SearchStrategyParamValue.valueOf(value);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static CaseMatchParamValue getCaseMatchParamValue(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(1), CASE_MATCH_KEY);
		return CaseMatchParamValue.valueOf(value);
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static Class<? extends Stemmer> getStemmerClass(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(2), STEMMER_KEY);
		return ConceptMapperStemmerParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static String[] getStopWordList(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(3), STOPWORDS_KEY);
		return ConceptMapperStopWordsParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static boolean getOrderIndependentLookup(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(4), ORDER_INDEPENDENT_LOOKUP_KEY);
		return ConceptMapperOrderIndependentLookupParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static boolean getFindAllMatches(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(5), FIND_ALL_MATCHES_KEY);
		return ConceptMapperFindAllMatchesParam.valueOf(value).paramValue();
	}

	/**
	 * @param paramValues
	 * @return
	 */
	private static boolean getReplaceCommaWithAnd(List<String> paramValues) {
		String value = StringUtil.removePrefix(paramValues.get(6), REPLACE_COMMA_WITH_AND_KEY);
		return ConceptMapperReplaceCommaWithAndParam.valueOf(value).paramValue();
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		int index = 0;
		for (List<String> paramPermutation : PARAM_COMBINATIONS) {
			logger.info("Permutation " + index++ + ": " + paramPermutation.toString());
		}
	}

}
