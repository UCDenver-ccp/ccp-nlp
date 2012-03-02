package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
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

	private File dictionaryFile;

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
				"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		String sentence1 = "Here is some text with some GO terms.";
		String sentence2 = "The NEF1 complex is known to ve a part of the nucleotide-excision repair complex.";
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
	public void setUp() throws Exception {
		super.setUp();
		dictionaryFile = folder.newFile("cm-dict.xml");
		ClassPathUtil.copyClasspathResourceToFile(getClass(), SAMPLE_CM_DICTIONARY_NAME, dictionaryFile);
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
		AnalysisEngineDescription conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(tsd,
				dictionaryFile, caseMatchParamValue, searchStrategyParamValue, spanFeatureStructureClass,
				offsetTokenizerDescription);

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
}
