/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.conceptMapper.support.tokenizer.OffsetTokenizer;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.UimaConfigParam;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class OffsetTokenizerFactory {

	private static final String OFFSET_TOKENIZER_DESCRIPTOR_PATH = "analysis_engine.primitive.OffsetTokenizer";

	public enum OffsetTokenizerConfigParam implements UimaConfigParam {

		CASE_MATCH(OffsetTokenizer.PARAM_CASE_MATCH),
		STEMMER_CLASS_NAME(OffsetTokenizer.PARAM_STEMMER_CLASS),
		/**
		 * String of characters that separate tokens</description> <type>String</type>
		 * <multiValued>false</multiValued> <mandatory>false</mandatory>
		 */
		TOKEN_DELIMITERS(OffsetTokenizer.PARAM_TOKEN_DELIM);
		private final String paramName;

		private OffsetTokenizerConfigParam(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String paramName() {
			return paramName;
		}
	}

	public static Object[] buildConfigurationData(CaseMatchParamValue caseMatchParamValue, Class<Stemmer> stemmerClass,
			String tokenDelimiters) {
		/* @formatter:off */
		return new Object[] {
				OffsetTokenizerConfigParam.CASE_MATCH.paramName(), caseMatchParamValue.paramValue(),
				OffsetTokenizerConfigParam.STEMMER_CLASS_NAME.paramName(), stemmerClass.getName(),
				OffsetTokenizerConfigParam.TOKEN_DELIMITERS.paramName(), tokenDelimiters};
		/* @formatter:on */
	}

	public static AnalysisEngineDescription buildOffsetTokenizerDescription(TypeSystemDescription tsd,
			Object[] configurationData) throws UIMAException, IOException {
		 AnalysisEngineDescription description = AnalysisEngineFactory.createAnalysisEngineDescription(OFFSET_TOKENIZER_DESCRIPTOR_PATH,
				configurationData);
		 description.getAnalysisEngineMetaData().setTypeSystem(tsd);
			return description;
	}

	/**
	 * @param caseMatchParamValue
	 * @return
	 */
	public static Object[] buildConfigurationData(CaseMatchParamValue caseMatchParamValue) {
		/* @formatter:off */
		return new Object[] {
				OffsetTokenizerConfigParam.CASE_MATCH.paramName(), caseMatchParamValue.paramValue()};
		/* @formatter:on */
	}

	/**
	 * @param caseMatchParamValue
	 * @param stemmerClass
	 * @return
	 */
	public static Object[] buildConfigurationData(CaseMatchParamValue caseMatchParamValue, Class<Stemmer> stemmerClass) {
		/* @formatter:off */
		return new Object[] {
				OffsetTokenizerConfigParam.CASE_MATCH.paramName(), caseMatchParamValue.paramValue(),
				OffsetTokenizerConfigParam.STEMMER_CLASS_NAME.paramName(), stemmerClass.getName()};
		/* @formatter:on */}

}
