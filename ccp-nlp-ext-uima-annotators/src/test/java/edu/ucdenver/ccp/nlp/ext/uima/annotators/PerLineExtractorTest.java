/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PerLineExtractorTest extends DefaultUIMATestCase {
	private static final String SENTENCE1 = "This is sentence number one.";
	private static final String SENTENCE2 = "";
	private static final String SENTENCE3 = "Sentence number three.";
	private static final String SENTENCE4 = "Sentence four.";

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		String documentText = SENTENCE1 + StringConstants.NEW_LINE + SENTENCE2 + StringConstants.NEW_LINE + SENTENCE3
				+ StringConstants.NEW_LINE + SENTENCE4;
		jcas.setDocumentText(documentText);
	}

	@Test
	public void testPerLineExtractor() throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngine perLineExtractorAe = AnalysisEngineFactory.createPrimitive(PerLineExtractorAE
				.createAnalysisEngineDescription(tsd, ClassMentionTypes.SENTENCE));
		perLineExtractorAe.process(jcas);

		Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas);
		annotIter.hasNext();

		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertEquals(ClassMentionTypes.SENTENCE, ccpTa.getClassMention().getMentionName());
			assertEquals(SENTENCE1, ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");

		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertEquals(ClassMentionTypes.SENTENCE, ccpTa.getClassMention().getMentionName());
			assertEquals(SENTENCE2, ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");

		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertEquals(ClassMentionTypes.SENTENCE, ccpTa.getClassMention().getMentionName());
			assertEquals(SENTENCE3, ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");

		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertEquals(ClassMentionTypes.SENTENCE, ccpTa.getClassMention().getMentionName());
			assertEquals(SENTENCE4, ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");

		assertFalse(annotIter.hasNext());

	}
}
