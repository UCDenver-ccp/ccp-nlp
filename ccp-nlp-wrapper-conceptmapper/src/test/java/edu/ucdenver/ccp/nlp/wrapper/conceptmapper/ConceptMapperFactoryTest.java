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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Ignore;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.SearchStrategyParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer.OffsetTokenizerFactory;

/**
 * 
 */

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperFactoryTest extends DefaultUIMATestCase {

	private static final String SAMPLE_CM_DICTIONARY_NAME = "sample-cm-dictionary.xml";
	// private static final String TOKENIZER_XML_NAME = "analysis_engine.primitive.OffsetTokenizer";
	private static final String TOKENIZER_XML_NAME = "analysis_engine/primitive/OffsetTokenizer.xml";

	private File dictionaryFile;
	private File tokenizerXmlFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#getTypeSystem()
	 */
	@Override
	protected TypeSystemDescription getTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem",
				"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.TypeSystem",
				"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.TestTypeSystem", "analysis_engine.primitive.DictTerm",
				"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation", "uima.tt.TokenAnnotation");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws UIMAException {
		String sentence1 = "Here is some text with some GO terms.";
		String sentence2 = "The NEF1 complex is known to be a part of the nucleotide-excision repair complex.";

		jcas.setDocumentText(sentence1 + " " + sentence2);

		Sentence sentenceAnnot1 = new Sentence(jcas, 0, sentence1.length());
		sentenceAnnot1.addToIndexes();

		Sentence sentenceAnnot2 = new Sentence(jcas, sentence1.length() + 1, sentence1.length() + 1
				+ sentence2.length());
		sentenceAnnot2.addToIndexes();

		assertEquals(sentence1, sentenceAnnot1.getCoveredText());
		assertEquals(sentence2, sentenceAnnot2.getCoveredText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#setUp()
	 */
	@Override
	public void setUp() throws UIMAException, IOException {
		super.setUp();
		dictionaryFile = folder.newFile("cm-dict.xml");
		ClassPathUtil.copyClasspathResourceToFile(getClass(), SAMPLE_CM_DICTIONARY_NAME, dictionaryFile);
		tokenizerXmlFile = folder.newFile("offset_tokenizer.xml");
		ClassPathUtil.copyClasspathResourceToFile(getClass(), TOKENIZER_XML_NAME, tokenizerXmlFile);
	}

	@Test
	public void testBuildCmOffsetTokAggregate() throws UIMAException, IOException {
		CaseMatchParamValue caseMatchParamValue = CaseMatchParamValue.CASE_INSENSITIVE;

		/* Init the tokenizer */
		Object[] tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue);
		AnalysisEngineDescription offsetTokenizerDescription = OffsetTokenizerFactory.buildOffsetTokenizerDescription(
				tsd, tokenizerConfigData);

		/* Init the concept mapper */
		SearchStrategyParamValue searchStrategyParamValue = SearchStrategyParamValue.CONTIGUOUS_MATCH;
		Class<? extends Annotation> spanFeatureStructureClass = Sentence.class;
		Class<? extends Stemmer> stemmerClass = null;
		String[] stopwords = new String[0];
		boolean orderIndependentLookup = false;
		boolean replaceCommaWithAnd = false;
		boolean findAllMatches = false;
		AnalysisEngineDescription conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				dictionaryFile, caseMatchParamValue, searchStrategyParamValue, stemmerClass, stopwords,
				orderIndependentLookup, findAllMatches, replaceCommaWithAnd, spanFeatureStructureClass,
				offsetTokenizerDescription);

		System.out.println("offset == null: " + (offsetTokenizerDescription == null));
		System.out.println("cm == null: " + (conceptMapperDescription == null));

		System.out.println("offset: " + offsetTokenizerDescription.toString());
		
		System.out.println("offset op: "
				+ offsetTokenizerDescription.getAnalysisEngineMetaData().getOperationalProperties()
						.isMultipleDeploymentAllowed());
		System.out.println("cm op: "
				+ conceptMapperDescription.getAnalysisEngineMetaData().getOperationalProperties()
				.isMultipleDeploymentAllowed());

		/* Init the aggregate engine */
		AnalysisEngineDescription cmAggregateDescription = AnalysisEngineFactory.createAggregateDescription(
				offsetTokenizerDescription, conceptMapperDescription);
		AnalysisEngine cmAggregateEngine = AnalysisEngineFactory.createAggregate(cmAggregateDescription);

		cmAggregateEngine.process(jcas);

		List<OntologyTerm> termList = CollectionsUtil.createList(JCasUtil.iterator(jcas, OntologyTerm.class));

		assertEquals("Two ontology terms should have been found", 2, termList.size());
		assertEquals("NEF1 complex", termList.get(0).getCoveredText());
		assertEquals("GO:0000110", termList.get(0).getID());
		assertEquals("nucleotide-excision repair complex", termList.get(1).getCoveredText());
		assertEquals("GO:0000109", termList.get(1).getID());
	}

	@Test
	public void testBuildCmOffsetTokAggregate_3() throws UIMAException, IOException {

		/* Init the tokenizer using the XML descriptor file */
		CaseMatchParamValue caseMatchParamValue = CaseMatchParamValue.CASE_INSENSITIVE;
		Object[] tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue);
		AnalysisEngineDescription offsetTokenizerDescription = (AnalysisEngineDescription) ResourceCreationSpecifierFactory
				.createResourceCreationSpecifier(tokenizerXmlFile.getAbsolutePath(), tokenizerConfigData);
		offsetTokenizerDescription.getAnalysisEngineMetaData().setTypeSystem(tsd);

		/*
		 * Init the concept mapper using the ConceptMapperFactory's method that does NOT use an XML
		 * file. (using one for the tokenizer is more difficult to avoid)
		 */
		SearchStrategyParamValue searchStrategyParamValue = SearchStrategyParamValue.CONTIGUOUS_MATCH;
		Class<? extends Annotation> spanFeatureStructureClass = Sentence.class;
		Class<? extends Stemmer> stemmerClass = null;
		String[] stopwords = new String[0];
		boolean orderIndependentLookup = false;
		boolean replaceCommaWithAnd = false;
		boolean findAllMatches = false;

		AnalysisEngineDescription conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				dictionaryFile, caseMatchParamValue, searchStrategyParamValue, stemmerClass, stopwords,
				orderIndependentLookup, findAllMatches, replaceCommaWithAnd, spanFeatureStructureClass,
				tokenizerXmlFile);

		AnalysisEngine tokenizerEngine = UIMAFramework.produceAnalysisEngine(offsetTokenizerDescription);
		AnalysisEngine conceptMapperEngine = UIMAFramework.produceAnalysisEngine(conceptMapperDescription);

		tokenizerEngine.process(jcas);
		conceptMapperEngine.process(jcas);

		List<OntologyTerm> termList = CollectionsUtil.createList(JCasUtil.iterator(jcas, OntologyTerm.class));

		assertEquals("Two ontology terms should have been found", 2, termList.size());
		assertEquals("NEF1 complex", termList.get(0).getCoveredText());
		assertEquals("GO:0000110", termList.get(0).getID());
		assertEquals("nucleotide-excision repair complex", termList.get(1).getCoveredText());
		assertEquals("GO:0000109", termList.get(1).getID());
	}
}
