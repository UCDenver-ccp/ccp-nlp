/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.core.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TextAnnotationTest {

	private TextAnnotation ta;

	@Before
	public void setUp() throws Exception {
		DefaultClassMention cm = new DefaultClassMention("classMention");
		/*
		 * Span: 4-7 AnnotatorID: 5 AnnotationSetID: 6 AnnotationID: 12 DocumentID: 9
		 * DocumentCollectionID: 8 DocumentSectionID: 10
		 */
		Annotator annotator = new Annotator(new Integer(5), "TestAnnotatorFirstName", "TestAnnotatorLastName",
				"TestAnnotatorAffiliation");
		AnnotationSet annotationSet = new AnnotationSet(new Integer(6), "TestAnnotationSetName",
				"TestAnnotationSetDescription");
		ta = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10, cm);

	}

	@After
	public void tearDown() throws Exception {
		ta = null;
	}

	/**
	 * Test the default constructor - this constructor will disappear some day
	 * 
	 */
	@Test
	public void testDefaultConstructor() throws Exception {
		ta = new DefaultTextAnnotation(0, 0);

		// assertEquals(TextAnnotationTypes.SEMANTIC, ta.getAnnotationType());
		assertEquals(-1, ta.getAnnotationID());
		assertEquals(-1, ta.getAnnotatorID());
		Set<AnnotationSet> annotationSets = ta.getAnnotationSets();
		assertEquals(0, annotationSets.size());
		assertEquals("-1", ta.getDocumentID());
		assertEquals(-1, ta.getDocumentCollectionID());
		assertEquals(-1, ta.getDocumentSectionID());
		assertEquals("", ta.getCoveredText());
		List<Span> spanList = ta.getSpans();
		Span span = spanList.get(0);
		assertEquals(1, spanList.size());
		assertEquals(0, span.getSpanStart());
		assertEquals(0, span.getSpanEnd());
		assertNull(ta.getClassMention());
	}

	/**
	 * Test the constructor
	 * 
	 */
	@Test
	public void testConstructor() throws Exception {
		// assertEquals(TextAnnotationTypes.SYNTACTIC, ta.getAnnotationType());
		assertEquals(12, ta.getAnnotationID());
		assertEquals(5, ta.getAnnotatorID());
		Set<AnnotationSet> annotationSets = ta.getAnnotationSets();
		assertEquals(1, annotationSets.size());
		assertEquals(new Integer(6), Collections.list(Collections.enumeration(annotationSets)).get(0)
				.getAnnotationSetID());
		assertEquals("9", ta.getDocumentID());
		assertEquals(8, ta.getDocumentCollectionID());
		assertEquals(10, ta.getDocumentSectionID());
		assertEquals("coveredText", ta.getCoveredText());
		List<Span> spanList = ta.getSpans();
		Span span = spanList.get(0);
		assertEquals(1, spanList.size());
		assertEquals(4, span.getSpanStart());
		assertEquals(7, span.getSpanEnd());
		assertNotNull(ta.getClassMention());
	}

	/**
	 * Test the span interface.. ability to change spans, etc.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpanInterface() throws Exception {
		assertEquals(4, ta.getAnnotationSpanStart());
		assertEquals(7, ta.getAnnotationSpanEnd());

		ta.setAnnotationSpanEnd(38);
		ta.setAnnotationSpanStart(33);

		assertEquals(ta.getAnnotationSpanStart(), 33);
		assertEquals(ta.getAnnotationSpanEnd(), 38);
	}

	/**
	 * Check that the spans get sorted properly when they are added to the TextAnnotation
	 * 
	 */
	@Test
	public void testSortSpans() throws Exception {
		assertEquals(3, ta.length());

		try {
			Span secondSpan = new Span(11, 15);
			ta.addSpan(secondSpan);
			assertEquals(11, ta.length());
			Span thirdSpan = new Span(3, 4);
			ta.addSpan(thirdSpan);
			assertEquals(12, ta.length());
		} catch (InvalidSpanException notExpected) {
			fail("Should not have raised an InvalidSpanException");
		}

		List<Span> spans = ta.getSpans();
		assertEquals(3, spans.size());
		assertEquals(3, spans.get(0).getSpanStart());
		assertEquals(4, spans.get(1).getSpanStart());
		assertEquals(11, spans.get(2).getSpanStart());

		assertEquals(3, ta.getAnnotationSpanStart());
		assertEquals(15, ta.getAnnotationSpanEnd());

		Span expectedAggregateSpan = new Span(3, 15);
		assertEquals(expectedAggregateSpan, ta.getAggregateSpan());

		expectedAggregateSpan = new Span(3, 55);
		ta.setAnnotationSpanEnd(55);
		assertEquals(expectedAggregateSpan, ta.getAggregateSpan());

		expectedAggregateSpan = new Span(0, 55);
		ta.setAnnotationSpanStart(0);
		assertEquals(expectedAggregateSpan, ta.getAggregateSpan());

		/*
		 * this should throw an exception, but it will be caught automatically - perhaps this is not
		 * a good idea?
		 */
		// expectedAggregateSpan = new Span(33,55);
	}

	/**
	 * Test the assignment of a ClassMention to the TextAnnotation. Ensure that the ClassMention is
	 * linked to the TextAnnotation properly
	 * 
	 * @throws InvalidInputException
	 * 
	 */
	@Test
	public void testSetClassMention() throws Exception {
		/*
		 * make sure that when you add a class mention, the text annotation gets added to the
		 * classmention's TextAnnotation list
		 */
		DefaultClassMention cm = new DefaultClassMention("new class mention");
		ta.setClassMention(cm);

		assertEquals(ta, cm.getTextAnnotation());
	}

	/**
	 * Test the default equals() method. This equals() method requires exact span matches, and
	 * identical class mention structure to return true.
	 * 
	 */
	@Test
	public void testEquals() {
		DefaultClassMention cm = new DefaultClassMention("classMention");
		Annotator annotator = new Annotator(new Integer(5), "TestAnnotatorFirstName", "TestAnnotatorLastName",
				"TestAnnotatorAffiliation");
		AnnotationSet annotationSet = new AnnotationSet(new Integer(6), "TestAnnotationSetName",
				"TestAnnotationSetDescription");

		/* annotation to test against */
		TextAnnotation ta0 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* identical annotation */
		TextAnnotation ta1 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* different span */
		TextAnnotation ta2 = new DefaultTextAnnotation(4, 8, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* different doc id */
		TextAnnotation ta3 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "7", 10,
				cm);

		/* different doc col id */
		TextAnnotation ta4 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 7, "9", 10,
				cm);

		/* different class mention */
		DefaultClassMention cm2 = new DefaultClassMention("classMention2");
		TextAnnotation ta5 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm2);

		assertTrue(ta0.equals(ta1));
		assertFalse(ta0.equals(ta2));
		assertFalse(ta0.equals(ta3));
		assertFalse(ta0.equals(ta4));
		assertFalse(ta0.equals(ta5));
	}

	/**
	 * Test the overlaps() method
	 * 
	 */
	@Test
	public void testOverlaps() {
		DefaultClassMention cm = new DefaultClassMention("classMention");
		Annotator annotator = new Annotator(new Integer(5), "TestAnnotatorFirstName", "TestAnnotatorLastName",
				"TestAnnotatorAffiliation");
		AnnotationSet annotationSet = new AnnotationSet(new Integer(6), "TestAnnotationSetName",
				"TestAnnotationSetDescription");

		/* annotation to test against */
		TextAnnotation ta0 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* identical annotation */
		TextAnnotation ta1 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* overlapping span */
		TextAnnotation ta2 = new DefaultTextAnnotation(5, 9, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		/* nonoverlapping span */
		TextAnnotation ta3 = new DefaultTextAnnotation(17, 19, "coveredText", annotator, annotationSet, 12, 8, "9", 10,
				cm);

		assertTrue(ta0.overlaps(ta1));
		assertTrue(ta0.overlaps(ta2));
		assertFalse(ta0.overlaps(ta3));

		/* add overlapping span to nonoverlapping annotation */
		Span span = null;
		try {
			span = new Span(4, 8);
		} catch (InvalidSpanException e) {
			e.printStackTrace();
			fail("Test failed - InvalidSpanException...");
		}
		ta3.addSpan(span);
		assertTrue(ta0.overlaps(ta3));
	}

	/**
	 * Test the getAnnotationSetIDs() method and the isMemberOfAnnotationSet() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAnnotationSetIDs() throws Exception {
		Set<Integer> expectedIDs = new HashSet<Integer>();
		expectedIDs.add(6);
		assertEquals(expectedIDs, ta.getAnnotationSetIDs());
		assertTrue(ta.isMemberOfAnnotationSet(6));
		assertFalse(ta.isMemberOfAnnotationSet(222));
		assertFalse(ta.isMemberOfAnnotationSet(223));

		expectedIDs.add(222);
		AnnotationSet annotationSet = new AnnotationSet(new Integer(222), "TestAnnotationSetName222",
				"TestAnnotationSetDescription");
		ta.addAnnotationSet(annotationSet);
		assertEquals(expectedIDs, ta.getAnnotationSetIDs());
		assertTrue(ta.isMemberOfAnnotationSet(6));
		assertTrue(ta.isMemberOfAnnotationSet(222));
		assertFalse(ta.isMemberOfAnnotationSet(223));

		/* add a duplicate set, ensure that it is not stored (since it is a duplicate) */
		AnnotationSet annotationSet2 = new AnnotationSet(new Integer(222), "TestAnnotationSetName222",
				"TestAnnotationSetDescription");
		ta.addAnnotationSet(annotationSet2);
		assertEquals(2, ta.getAnnotationSetIDs().size());
		assertEquals(expectedIDs, ta.getAnnotationSetIDs());
		assertTrue(ta.isMemberOfAnnotationSet(6));
		assertTrue(ta.isMemberOfAnnotationSet(222));
		assertFalse(ta.isMemberOfAnnotationSet(223));

		expectedIDs.add(223);
		AnnotationSet annotationSet3 = new AnnotationSet(new Integer(223), "TestAnnotationSetName223",
				"TestAnnotationSetDescription");
		ta.addAnnotationSet(annotationSet3);
		assertEquals(expectedIDs, ta.getAnnotationSetIDs());
		assertTrue(ta.isMemberOfAnnotationSet(6));
		assertTrue(ta.isMemberOfAnnotationSet(222));
		assertTrue(ta.isMemberOfAnnotationSet(223));
	}

	/**
	 * Test the default compareTo() method. This method should utilize the strictest possible
	 * criteria for matching
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareTo() throws Exception {
		DefaultClassMention cm = new DefaultClassMention("classMention");
		Annotator annotator = new Annotator(new Integer(5), "TestAnnotatorFirstName", "TestAnnotatorLastName",
				"TestAnnotatorAffiliation");
		AnnotationSet annotationSet = new AnnotationSet(new Integer(6), "TestAnnotationSetName",
				"TestAnnotationSetDescription");

		/* annotation to test against */
		DefaultTextAnnotation ta0 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8,
				"9", 10, cm);

		/* identical annotation */
		DefaultTextAnnotation ta1 = new DefaultTextAnnotation(4, 7, "coveredText", annotator, annotationSet, 12, 8,
				"9", 10, cm);

		/* overlapping span, but starts before ta0 */
		DefaultTextAnnotation ta2 = new DefaultTextAnnotation(2, 9, "coveredText", annotator, annotationSet, 12, 8,
				"9", 10, cm);

		/* nonoverlapping span, and starts after ta0 */
		DefaultTextAnnotation ta3 = new DefaultTextAnnotation(17, 19, "coveredText", annotator, annotationSet, 12, 8,
				"9", 10, cm);

		assertEquals(0, ta0.compareTo(ta1));
		assertEquals(1, ta0.compareTo(ta2));
		assertEquals(-1, ta0.compareTo(ta3));
	}

	/**
	 * Test the setSpans() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetSpans() throws Exception {
		List<Span> spanSet = new ArrayList<Span>();
		Span span1 = new Span(5, 7);
		spanSet.add(span1);

		List<Span> expectedSpans = new ArrayList<Span>();
		Span expectedSpan1 = new Span(5, 7);
		expectedSpans.add(expectedSpan1);

		ta.setSpans(spanSet);
		assertEquals(expectedSpans, ta.getSpans());

		/* now test with multiple spans */

		Span span2 = new Span(16, 99);
		Span span3 = new Span(0, 4);
		spanSet.add(span2);
		spanSet.add(span3);

		Span expectedSpan2 = new Span(16, 99);
		Span expectedSpan3 = new Span(0, 4);
		expectedSpans = new ArrayList<Span>();
		expectedSpans.add(expectedSpan3);
		expectedSpans.add(expectedSpan1);
		expectedSpans.add(expectedSpan2);

		ta.setSpans(spanSet);
		assertEquals(expectedSpans, ta.getSpans());
	}

	private List<Span> createList() throws Exception {
		List<Span> spanList = new ArrayList<Span>();

		spanList.add(new Span(5, 7));
		spanList.add(new Span(9, 11));
		spanList.add(new Span(13, 15));
		spanList.add(new Span(17, 19));

		return spanList;
	}

	@Test
	public void testAddSpan() throws Exception {
		List<Span> spanList = createList();

		TextAnnotation ta = new DefaultTextAnnotation(0, 1);
		ta.setSpans(spanList);

		// middle
		Span newSpan = new Span(11, 13);
		ta.addSpan(newSpan);
		assertEquals(5, ta.getSpans().size());
		assertEquals(newSpan, ta.getSpans().get(2));

		// begining
		newSpan = new Span(1, 3);
		ta.addSpan(newSpan);
		assertEquals(6, ta.getSpans().size());
		assertEquals(newSpan, ta.getSpans().get(0));

		// end
		newSpan = new Span(21, 23);
		ta.addSpan(newSpan);
		assertEquals(7, ta.getSpans().size());
		assertEquals(newSpan, ta.getSpans().get(6));

		// overlapping/equal start
		newSpan = new Span(9, 15);
		ta.addSpan(newSpan);
		assertEquals(8, ta.getSpans().size());
		assertEquals(newSpan, ta.getSpans().get(3));
	}

	/**
	 * Test the setSpan() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetSpan() throws Exception {
		Span span1 = new Span(5, 7);
		Span span2 = new Span(16, 99);
		Span span3 = new Span(0, 4);

		Span expectedSpan1 = new Span(5, 7);
		Span expectedSpan2 = new Span(16, 99);
		Span expectedSpan3 = new Span(0, 4);

		ta.setSpan(span1);
		assertEquals(expectedSpan1, ta.getAggregateSpan());

		ta.setSpan(span2);
		assertEquals(expectedSpan2, ta.getAggregateSpan());

		ta.setSpan(span3);

		List<Span> expectedSpans = new ArrayList<Span>();
		expectedSpans.add(expectedSpan3);
		assertEquals(expectedSpans, ta.getSpans());

	}

	@Test
	public void testToString() {
		String expectedStr = "======================= Annotation: 12 =======================\n"
				+ "Annotator: 5|TestAnnotatorFirstName|TestAnnotatorLastName|TestAnnotatorAffiliation\n"
				+ "--- AnnotationSets: 6|TestAnnotationSetName|TestAnnotationSetDescription\n" + "--- Comment: \n\n"
				+ "--- Span: 4 - 7  \n" + "--- DocCollection: 8  DocID: 9  DocumentSection: 10\n"
				+ "--- Covered Text: coveredText\n" + "-CLASS MENTION: classMention \"coveredText\"	[4..7]\n\n"
				+ "=================================================================================";
		System.out.println("EXPECTED:\n" + expectedStr);
		System.err.println("TA:\n" + ta.toString());
		assertEquals(expectedStr, ta.toString());
	}

}
