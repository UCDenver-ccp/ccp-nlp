package edu.ucdenver.ccp.nlp.core.uima.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;

public class TextAnnotationIteratorTest extends DefaultUIMATestCase {

	@Override
	protected void initJCas() throws Exception {
		jcas.setDocumentText("blahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblah");
		addGeneAnnotationToJCas(0, 1, 12345);
		addGeneAnnotationToJCas(2, 3, 12345);
		addGeneAnnotationToJCas(4, 5, 12345);

		addSentenceAnnotationToJCas(6, 7);
		addSentenceAnnotationToJCas(1, 2);
	}

	@Test
	public void testTextAnnotationIter_OneInputType() throws Exception {
		assertEquals(String.format("Should be 3 gene annotations"), 3, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, ClassMentionTypes.GENE)).size());
	}

	@Test
	public void testTextAnnotationIter_ZeroInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas)).size());
	}

	@Test
	public void testTextAnnotationIter_NullInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, (String[]) null)).size());
	}

	@Test
	public void testTextAnnotationIter_TwoInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, ClassMentionTypes.GENE, ClassMentionTypes.SENTENCE)).size());
	}

}
