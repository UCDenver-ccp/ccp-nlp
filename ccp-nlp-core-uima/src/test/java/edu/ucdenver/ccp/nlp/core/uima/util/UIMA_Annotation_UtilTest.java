package edu.ucdenver.ccp.nlp.core.uima.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;

public class UIMA_Annotation_UtilTest {

	private JCas jcas;

	private Annotation testAnnotation;

	private CCPTextAnnotation testCcpTA;

	@Before
	public void setUp() throws Exception {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		jcas = JCasFactory.createJCas(tsd);

		String docText = "\n<A HREF=\"blah\"> &nbsp;&nbsp;  Annotation text&nbsp;  &nbsp;&nbsp;\n</NOBR>\n\n\n";
		jcas.setDocumentText(docText);

		testAnnotation = new Annotation(jcas);
		testAnnotation.setBegin(0);
		testAnnotation.setEnd(docText.length());
		testAnnotation.addToIndexes();

		testCcpTA = new CCPTextAnnotation(jcas);
		testCcpTA.setBegin(16);
		testCcpTA.setEnd(66);
	}

    
    @Test
	public void testCreateTextAnnotation() throws Exception {
		CCPTextAnnotation ccpTA = UIMA_Annotation_Util.createCCPTextAnnotation("protein",new int[]{16,66}, jcas);
		assertEquals("protein", ccpTA.getClassMention().getMentionName());
		assertEquals(16, ccpTA.getBegin());
		assertEquals(66, ccpTA.getEnd());
		assertEquals(1, ccpTA.getSpans().size());
		assertEquals(16, ((CCPSpan)ccpTA.getSpans().get(0)).getSpanStart());
		assertEquals(66, ((CCPSpan)ccpTA.getSpans().get(0)).getSpanEnd());
	}
    
    
	/* Test the removal of blank lines from the start and end of an annotation */
	@Test
	public void testRemoveLeadingAndTrailingBlankLines() {
		/* make sure the testAnnotation starts and ends with a blank line */
		assertTrue(testAnnotation.getCoveredText().startsWith("\n"));
		assertTrue(testAnnotation.getCoveredText().endsWith("\n"));

		UIMA_Annotation_Util.removeLeadingAndTrailingBlankLines(testAnnotation);

		/* testAnnotation should no longer start and end with blank lines */
		assertFalse(testAnnotation.getCoveredText().startsWith("\n"));
		assertFalse(testAnnotation.getCoveredText().endsWith("\n"));
	}

	@Test
	public void testTrimCCPTextAnnotation() {
		// System.out.println("BEFORE TRIM: " + testCcpTA.getCoveredText());
		try {
			UIMA_Annotation_Util.trimCCPTextAnnotation(testCcpTA);
		} catch (CASException e) {
			e.printStackTrace();
			fail("Test failed.. exception while trimming CCPTextAnnotation");
		}
		// System.out.println("AFTER TRIM:  " + testCcpTA.getCoveredText());

		assertEquals("Annotation text", testCcpTA.getCoveredText());

	}

//	@Test
//	public void testToString() throws Exception {
//		AnalysisEngine semanticConjunctionCreator = AnalysisEngineFactory
//				.createPrimitiveFromPath("data/test/desc/TestAnalysisEngine_AE.xml");
//
//		/* ________________________________________1_________2_________3_________4_________5 */
//		/* ______________________________012345678901234567890123456789012345678901234567890123456789 */
//		String proteinConjunctionText = "p53 is activated by proteins ABC-1, CDE-2, and DEF-3.";
//
//		JCas jcas = AnalysisEngineFactory.process(semanticConjunctionCreator, proteinConjunctionText);
//
//		/* Now we add some protein annotations */
//		List<CCPTextAnnotation> annotationsInCAS = new ArrayList<CCPTextAnnotation>();
//		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas));
//		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 29, 34 }, jcas));
//		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 36, 41 }, jcas));
//		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 47, 52 }, jcas));
//
//		/* Add an activation annotation */
//		CCPTextAnnotation activationAnnot = UIMA_Annotation_Util.createCCPTextAnnotation("activation", new int[] { 7, 16 }, jcas);
//		UIMA_Util.addSlotValue(activationAnnot.getClassMention(), "activator", annotationsInCAS.get(1).getClassMention());
//		UIMA_Util.addSlotValue(activationAnnot.getClassMention(), "activated_entity", annotationsInCAS.get(1).getClassMention());
//
//		String proteinStr = UIMA_Annotation_Util.toString(annotationsInCAS.get(3), true, false);
//		String expectedProteinStr = "[47..52] \"DEF-3\" Default|-1 [protein]";
//		System.out.println(proteinStr);
//		// assertEquals(expectedProteinStr, proteinStr);
//
//		String proteinStr_anonymized = UIMA_Annotation_Util.toString(annotationsInCAS.get(3), true, true);
//		String expectedProteinStr_anonymized = "[47..52] \"DEF-3\" [protein]";
//		System.out.println(proteinStr_anonymized);
//		// assertEquals(expectedProteinStr_anonymized, proteinStr_anonymized);
//
//		String activationStr = UIMA_Annotation_Util.toString(activationAnnot, true, false);
//		String expectedActivationStr = "";
//		System.out.println(activationStr);
//		// assertEquals(expectedActivationStr, activationStr);
//
//		String activationStr_anonymized = UIMA_Annotation_Util.toString(activationAnnot, true, true);
//		String expectedActivationStr_anonymized = "";
//		System.out.println(activationStr_anonymized);
//		UIMA_Util.printCCPTextAnnotation(activationAnnot, System.out);
//		// assertEquals(expectedActivationStr_anonymized, activationStr_anonymized);
//
//	}

	@Test
	public void testGetRedundantAnnotations() throws Exception {
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(tsd);

		/* ________________________________________1_________2_________3_________4_________5 */
		/* ______________________________012345678901234567890123456789012345678901234567890123456789 */
		String proteinConjunctionText = "p53 is activated by proteins ABC-1, CDE-2, and DEF-3.";

		jcas.setDocumentText(proteinConjunctionText);

		/* Now we add some protein annotations */
		List<CCPTextAnnotation> annotationsInCAS = new ArrayList<CCPTextAnnotation>();
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 29, 34 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 36, 41 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 47, 52 }, jcas));

		Collection<CCPTextAnnotation> duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(0, duplicateAnnotations.size());

		/* add one duplicate annotation */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(1, duplicateAnnotations.size());

		/* add a different duplicate */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 36, 41 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(2, duplicateAnnotations.size());

		/* add an identical duplicate */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 36, 41 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(3, duplicateAnnotations.size());

		/* add an identical annotatoin with a different annotator */
		CCPTextAnnotation ta = UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 36, 41 }, jcas);
		CCPAnnotator annotator = new CCPAnnotator(jcas);
		annotator.setAffiliation("BLAH");
		annotator.setAnnotatorID(99);
		annotator.setLastName("BLAHBLAH");
		annotator.setFirstName("BBB");
		ta.setAnnotator(annotator);
		annotationsInCAS.add(ta);
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(4, duplicateAnnotations.size());

	}

	@Test
	public void testDontAddDuplicateAnnotations() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
		
		/* ________________________________________1_________2_________3_________4_________5 */
		/* ______________________________012345678901234567890123456789012345678901234567890123456789 */
		String proteinConjunctionText = "p53 is activated by proteins ABC-1, CDE-2, and DEF-3.";

		jcas.setDocumentText(proteinConjunctionText);
		
		/* Now we add some protein annotations */
		UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas);
		
		// Create an exact duplicate of this one and try to add
		CCPTextAnnotation ccpTA = UIMA_Annotation_Util.createCCPTextAnnotationNoDups(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas);
		assertNull(ccpTA);
		
    }
    
    @Test
	public void testSortBySpan() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
		/*
		 * ______________________________1_________2_________3_________4_________5_________6_________7_________8_________9
		 * /___________________
		 * 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567
		 */
		jcas.setDocumentText("This test mimics a coreference annotation. A is the same as B is the same as C is the same as D.");
		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName("mainCM");
		CCPTextAnnotation ccpTA_A = UIMA_Annotation_Util.createCCPTextAnnotation("class-A", new int[] { 43, 44 }, jcas);
		CCPTextAnnotation ccpTA_B = UIMA_Annotation_Util.createCCPTextAnnotation("class-B", new int[] { 60, 61 }, jcas);
		CCPTextAnnotation ccpTA_C = UIMA_Annotation_Util.createCCPTextAnnotation("class-C", new int[] { 43, 47 }, jcas);
		CCPTextAnnotation ccpTA_D = UIMA_Annotation_Util.createCCPTextAnnotation("class-D", new int[] { 47, 95 }, jcas);

		List<CCPTextAnnotation> taList = new ArrayList<CCPTextAnnotation>();
		taList.add(ccpTA_B);
		taList.add(ccpTA_A);
		taList.add(ccpTA_C);
		taList.add(ccpTA_D);

		List<TextAnnotation> sortedTaList = Collections.list(Collections.enumeration(UIMA_Annotation_Util.convertToWrappedAnnotationList(taList)));
		Collections.sort(sortedTaList, TextAnnotation.BY_SPAN());
		
//		Collections.sort(taList, UIMA_Annotation_Util.SORT_BY_SPAN());

		assertEquals("class-A", sortedTaList.get(0).getClassMention().getMentionName());
		assertEquals("class-C", sortedTaList.get(1).getClassMention().getMentionName());
		assertEquals("class-D", sortedTaList.get(2).getClassMention().getMentionName());
		assertEquals("class-B", sortedTaList.get(3).getClassMention().getMentionName());

	}

    // worthwhile test, but has a dependency on the uima-syntax extension project. perhaps it can be moved there or revised
//	@Test
//	public void testGetAnnotationProperties() throws Exception {
//		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
//		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
//		/*
//		 * ______________________________1_________2_________3_________4_________5_________6_________7_________8_________9
//		 * /___________________
//		 * 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567
//		 */
//		jcas.setDocumentText("token1 token2 token3.");
//		CCPTextAnnotation token1Annot = UIMA_Annotation_Util.createCCPTextAnnotation("token", new int[] { 0, 6 }, jcas);
//		UIMA_Util.addSlotValue(token1Annot.getClassMention(), SlotMentionTypes.TOKEN_PARTOFSPEECH, "NN");
//		UIMA_Util.addSlotValue(token1Annot.getClassMention(), SlotMentionTypes.TOKEN_STEM, "tok");
//
//		UIMASyntacticAnnotation_Util.normalizeSyntacticAnnotations(jcas);
//
//		/* The token annotation should now have two annotation properties, 1 for pos and 1 for stem */
//		assertEquals(1, jcas.getJFSIndexRepository().getAnnotationIndex(CCPTokenAnnotation.type).size());
//
//		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTokenAnnotation.type).iterator();
//		if (annotIter.hasNext()) {
//			CCPTokenAnnotation tokenAnnot = (CCPTokenAnnotation) annotIter.next();
//			assertEquals(1, UIMA_Annotation_Util.getAnnotationProperties(tokenAnnot, PartOfSpeechProperty.class).size());
//			assertEquals(1, UIMA_Annotation_Util.getAnnotationProperties(tokenAnnot, StemProperty.class).size());
//			assertEquals(2, UIMA_Annotation_Util.getAnnotationProperties(tokenAnnot, TokenAnnotationProperty.class).size());
//		}
//
//	}

	private final String documentID = "1234";
	private final int documentCollectionID = 99;
	private final String documentText = "Src translated KDEL-R from the mitochondria to the golgi apparatus, as does ABC-34.";

	@Test
	public void testAddSpan() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
		jcas.setDocumentText(documentText);
		UIMA_Util.setDocumentID(jcas, documentID);
		UIMA_Util.setDocumentCollectionID(jcas, documentCollectionID);

		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAnnotatorID(15);
		ccpAnnotator.setFirstName("Bob");
		ccpAnnotator.setLastName("Builder");
		ccpAnnotator.setAffiliation("UCD");
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(23);
		ccpAnnotationSet.setAnnotationSetDescription("description");

		CCPTextAnnotation proteinAnnot = UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.PROTEIN, new int[] { 0, 3 }, jcas,
				ccpAnnotator, ccpAnnotationSet);
		assertEquals(1, proteinAnnot.getSpans().size());

		Span spanToAdd = new Span(5, 7);
		UIMA_Annotation_Util.addSpan(proteinAnnot, spanToAdd, jcas);
		assertEquals(2, proteinAnnot.getSpans().size());
		assertEquals(0, proteinAnnot.getBegin());
		assertEquals(7, proteinAnnot.getEnd());

		CCPSpan ccpSpanToAdd = new CCPSpan(jcas);
		ccpSpanToAdd.setSpanStart(11);
		ccpSpanToAdd.setSpanEnd(14);
		UIMA_Annotation_Util.addSpan(proteinAnnot, ccpSpanToAdd, jcas);
		assertEquals(3, proteinAnnot.getSpans().size());
		assertEquals(0, proteinAnnot.getBegin());
		assertEquals(14, proteinAnnot.getEnd());

		ccpSpanToAdd = new CCPSpan(jcas);
		ccpSpanToAdd.setSpanStart(4);
		ccpSpanToAdd.setSpanEnd(5);
		UIMA_Annotation_Util.addSpan(proteinAnnot, ccpSpanToAdd, jcas);
		assertEquals(4, proteinAnnot.getSpans().size());
		assertEquals(0, proteinAnnot.getBegin());
		assertEquals(14, proteinAnnot.getEnd());

	}

}
