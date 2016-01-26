/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

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
