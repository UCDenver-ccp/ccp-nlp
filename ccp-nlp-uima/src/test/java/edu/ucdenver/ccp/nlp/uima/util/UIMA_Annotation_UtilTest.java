package edu.ucdenver.ccp.nlp.uima.util;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class UIMA_Annotation_UtilTest {

	private JCas jcas;

	private Annotation testAnnotation;

	private CCPTextAnnotation testCcpTA;

	@Before
	public void setUp() throws Exception {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
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
		CCPTextAnnotation ccpTA = UIMA_Annotation_Util.createCCPTextAnnotation("protein", new int[] { 16, 66 }, jcas);
		assertEquals("protein", ccpTA.getClassMention().getMentionName());
		assertEquals(16, ccpTA.getBegin());
		assertEquals(66, ccpTA.getEnd());
		assertEquals(1, ccpTA.getSpans().size());
		assertEquals(16, ((CCPSpan) ccpTA.getSpans().get(0)).getSpanStart());
		assertEquals(66, ((CCPSpan) ccpTA.getSpans().get(0)).getSpanEnd());
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

	@Test
	public void testGetRedundantAnnotations() throws Exception {

		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(tsd);

		/* ________________________________________1_________2_________3_________4_________5 */
		/* ______________________________012345678901234567890123456789012345678901234567890123456789 */
		String proteinConjunctionText = "p53 is activated by proteins ABC-1, CDE-2, and DEF-3.";

		jcas.setDocumentText(proteinConjunctionText);

		/* Now we add some protein annotations */
		List<CCPTextAnnotation> annotationsInCAS = new ArrayList<CCPTextAnnotation>();
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 0, 3 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 29, 34 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 36, 41 }, jcas));
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 47, 52 }, jcas));

		Collection<CCPTextAnnotation> duplicateAnnotations = UIMA_Annotation_Util
				.getRedundantAnnotations(annotationsInCAS);
		assertEquals(0, duplicateAnnotations.size());

		/* add one duplicate annotation */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 0, 3 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(1, duplicateAnnotations.size());

		/* add a different duplicate */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 36, 41 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(2, duplicateAnnotations.size());

		/* add an identical duplicate */
		annotationsInCAS.add(UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 36, 41 }, jcas));
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(3, duplicateAnnotations.size());

		/* add an identical annotatoin with a different annotator */
		CCPTextAnnotation ta = UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(),
				new int[] { 36, 41 }, jcas);
		CCPAnnotator annotator = new CCPAnnotator(jcas);
		annotator.setAffiliation("BLAH");
		annotator.setAnnotatorID("99");
		annotator.setName("BLAHBLAH");
		ta.setAnnotator(annotator);
		annotationsInCAS.add(ta);
		duplicateAnnotations = UIMA_Annotation_Util.getRedundantAnnotations(annotationsInCAS);
		assertEquals(4, duplicateAnnotations.size());

	}

	@Test
	public void testDontAddDuplicateAnnotations() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);

		/* ________________________________________1_________2_________3_________4_________5 */
		/* ______________________________012345678901234567890123456789012345678901234567890123456789 */
		String proteinConjunctionText = "p53 is activated by proteins ABC-1, CDE-2, and DEF-3.";

		jcas.setDocumentText(proteinConjunctionText);

		/* Now we add some protein annotations */
		UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.PROTEIN.typeName(), new int[] { 0, 3 }, jcas);

		// Create an exact duplicate of this one and try to add
		CCPTextAnnotation ccpTA = UIMA_Annotation_Util.createCCPTextAnnotationNoDups(
				ClassMentionType.PROTEIN.typeName(), new int[] { 0, 3 }, jcas);
		assertNull(ccpTA);

	}

	@Test
	public void testSortBySpan() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
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

		List<TextAnnotation> sortedTaList = Collections.list(Collections.enumeration(UIMA_Annotation_Util
				.convertToWrappedAnnotationList(taList)));
		Collections.sort(sortedTaList, TextAnnotation.BY_SPAN());

		assertEquals("class-A", sortedTaList.get(0).getClassMention().getMentionName());
		assertEquals("class-C", sortedTaList.get(1).getClassMention().getMentionName());
		assertEquals("class-D", sortedTaList.get(2).getClassMention().getMentionName());
		assertEquals("class-B", sortedTaList.get(3).getClassMention().getMentionName());

	}

	private final String documentID = "1234";
	private final int documentCollectionID = 99;
	private final String documentText = "Src translated KDEL-R from the mitochondria to the golgi apparatus, as does ABC-34.";

	@Test
	public void testAddSpan() throws Exception {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
		jcas.setDocumentText(documentText);
		UIMA_Util.setDocumentID(jcas, documentID);
		UIMA_Util.setDocumentCollectionID(jcas, documentCollectionID);

		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAnnotatorID("15");
		ccpAnnotator.setName("Bob");
		ccpAnnotator.setAffiliation("UCD");
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(23);
		ccpAnnotationSet.setAnnotationSetDescription("description");

		CCPTextAnnotation proteinAnnot = UIMA_Annotation_Util.createCCPTextAnnotation(
				ClassMentionType.PROTEIN.typeName(), new int[] { 0, 3 }, jcas, ccpAnnotator, ccpAnnotationSet);
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
