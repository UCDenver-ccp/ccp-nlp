/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.conceptMapper.support.tokenizer.OffsetTokenizer;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.UimaConfigParam;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class OffsetTokenizerFactory {

	private static final String OFFSET_TOKENIZER_DESCRIPTOR_PATH = "analysis_engine.primitive.OffsetTokenizer";

	private enum OffsetTokenizerConfigParam implements UimaConfigParam {

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

	public static Object[] buildConfigurationData(CaseMatchParamValue caseMatchParamValue, Class<? extends Stemmer> stemmerClass,
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
	public static Object[] buildConfigurationData(CaseMatchParamValue caseMatchParamValue, Class<? extends Stemmer> stemmerClass) {
		/* @formatter:off */
		return new Object[] {
				OffsetTokenizerConfigParam.CASE_MATCH.paramName(), caseMatchParamValue.paramValue(),
				OffsetTokenizerConfigParam.STEMMER_CLASS_NAME.paramName(), stemmerClass.getName()};
		/* @formatter:on */}

}
