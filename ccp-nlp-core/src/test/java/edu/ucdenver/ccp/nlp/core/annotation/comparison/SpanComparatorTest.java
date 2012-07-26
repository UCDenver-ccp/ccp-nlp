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
package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SpanComparatorTest {
	/* individual spans */
	private Span referenceSpan;

	private Span beforeSpan;

	private Span afterSpan;

	private Span beforeOverlapSpan;

	private Span afterOverlapSpan;

	private Span completeOverlapSpan;

	private Span internalSpan;

	private Span matchSpan;

	private Span sameStartButEndsBeforeSpan;

	private Span sameStartButEndsAfterSpan;

	private Span sameEndButStartsBeforeSpan;

	private Span sameEndButStartsAfterSpan;

	private Span adjacentBeforeSpan;

	private Span adjacentAfterSpan;

	/* lists of spans */
	private List<Span> referenceSpanList;

	private List<Span> beforeSpanList;

	private List<Span> afterSpanList;

	private List<Span> beforeOverlapSpanList;

	private List<Span> afterOverlapSpanList;

	private List<Span> completeOverlapSpanList;

	private List<Span> internalSpanList;

	private List<Span> matchSpanList;

	private List<Span> sameStartButEndsBeforeSpanList;

	private List<Span> sameStartButEndsAfterSpanList;

	private List<Span> sameEndButStartsBeforeSpanList;

	private List<Span> sameEndButStartsAfterSpanList;

	private List<Span> adjacentBeforeSpanList;

	private List<Span> adjacentAfterSpanList;

	SpanComparator sc;

	@Before
	public void setUp() throws Exception {
		/* set up individual spans */
		referenceSpan = new Span(36, 45);
		beforeSpan = new Span(0, 5);
		afterSpan = new Span(50, 55);
		beforeOverlapSpan = new Span(30, 40);
		afterOverlapSpan = new Span(40, 50);
		completeOverlapSpan = new Span(0, 100);
		internalSpan = new Span(38, 42);
		matchSpan = new Span(36, 45);
		sameStartButEndsBeforeSpan = new Span(36, 44);
		sameStartButEndsAfterSpan = new Span(36, 46);
		sameEndButStartsBeforeSpan = new Span(35, 45);
		sameEndButStartsAfterSpan = new Span(37, 45);
		adjacentBeforeSpan = new Span(33, 36);
		adjacentAfterSpan = new Span(45, 55);

		/* set up span lists */
		referenceSpanList = new ArrayList<Span>();
		Span referenceSpan1 = new Span(20, 25);
		Span referenceSpan2 = new Span(30, 35);
		Span referenceSpan3 = new Span(40, 45);
		referenceSpanList.add(referenceSpan1);
		referenceSpanList.add(referenceSpan2);
		referenceSpanList.add(referenceSpan3);

		Span span;

		beforeSpanList = new ArrayList<Span>();
		span = new Span(0, 5);
		beforeSpanList.add(span);
		span = new Span(10, 19);
		beforeSpanList.add(span);

		afterSpanList = new ArrayList<Span>();
		span = new Span(50, 55);
		afterSpanList.add(span);
		span = new Span(60, 75);
		afterSpanList.add(span);
		span = new Span(90, 99);
		afterSpanList.add(span);

		beforeOverlapSpanList = new ArrayList<Span>();
		span = new Span(18, 35);
		beforeOverlapSpanList.add(span);

		afterOverlapSpanList = new ArrayList<Span>();
		span = new Span(42, 49);
		afterOverlapSpanList.add(span);
		span = new Span(50, 53);
		afterOverlapSpanList.add(span);
		span = new Span(67, 98);
		afterOverlapSpanList.add(span);

		completeOverlapSpanList = new ArrayList<Span>();
		span = new Span(50, 55);
		completeOverlapSpanList.add(span);
		span = new Span(30, 44);
		completeOverlapSpanList.add(span);
		span = new Span(15, 17);
		completeOverlapSpanList.add(span);
		span = new Span(2, 5);
		completeOverlapSpanList.add(span);

		internalSpanList = new ArrayList<Span>();
		span = new Span(27, 29);
		internalSpanList.add(span);
		span = new Span(37, 39);
		internalSpanList.add(span);

		matchSpanList = new ArrayList<Span>();
		span = new Span(20, 25);
		matchSpanList.add(span);
		span = new Span(30, 35);
		matchSpanList.add(span);
		span = new Span(40, 45);
		matchSpanList.add(span);

		sameStartButEndsBeforeSpanList = new ArrayList<Span>();
		span = new Span(20, 44);
		sameStartButEndsBeforeSpanList.add(span);

		sameStartButEndsAfterSpanList = new ArrayList<Span>();
		span = new Span(20, 22);
		sameStartButEndsAfterSpanList.add(span);
		span = new Span(40, 46);
		sameStartButEndsAfterSpanList.add(span);

		sameEndButStartsBeforeSpanList = new ArrayList<Span>();
		span = new Span(40, 45);
		sameEndButStartsBeforeSpanList.add(span);
		span = new Span(33, 35);
		sameEndButStartsBeforeSpanList.add(span);
		span = new Span(18, 30);
		sameEndButStartsBeforeSpanList.add(span);

		sameEndButStartsAfterSpanList = new ArrayList<Span>();
		span = new Span(39, 45);
		sameEndButStartsAfterSpanList.add(span);

		adjacentBeforeSpanList = new ArrayList<Span>();
		span = new Span(15, 20);
		adjacentBeforeSpanList.add(span);
		span = new Span(0, 5);
		adjacentBeforeSpanList.add(span);

		adjacentAfterSpanList = new ArrayList<Span>();
		span = new Span(45, 47);
		adjacentAfterSpanList.add(span);
		span = new Span(50, 55);
		adjacentAfterSpanList.add(span);
	}

	/**
	 * Test that the compare() method functions properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStrictSpanComparator() throws Exception {
		sc = new StrictSpanComparator();
		/* test on individual spans */
		assertEquals(1, sc.compare(referenceSpan, beforeSpan));
		assertEquals(-1, sc.compare(referenceSpan, afterSpan));
		assertEquals(1, sc.compare(referenceSpan, beforeOverlapSpan));
		assertEquals(-1, sc.compare(referenceSpan, afterOverlapSpan));
		assertEquals(1, sc.compare(referenceSpan, completeOverlapSpan));
		assertEquals(-1, sc.compare(referenceSpan, internalSpan));
		assertEquals(0, sc.compare(referenceSpan, matchSpan));
		assertEquals(1, sc.compare(referenceSpan, sameStartButEndsBeforeSpan));
		assertEquals(-1, sc.compare(referenceSpan, sameStartButEndsAfterSpan));
		assertEquals(1, sc.compare(referenceSpan, sameEndButStartsBeforeSpan));
		assertEquals(1, sc.compare(referenceSpan, adjacentBeforeSpan));
		assertEquals(-1, sc.compare(referenceSpan, adjacentAfterSpan));

		/* test on span lists */
		assertEquals(1, sc.compare(referenceSpanList, beforeSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, afterSpanList));
		assertEquals(1, sc.compare(referenceSpanList, beforeOverlapSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, afterOverlapSpanList));
		assertEquals(1, sc.compare(referenceSpanList, completeOverlapSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, internalSpanList));
		assertEquals(0, sc.compare(referenceSpanList, matchSpanList));
		assertEquals(1, sc.compare(referenceSpanList, sameStartButEndsBeforeSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, sameStartButEndsAfterSpanList));
		assertEquals(1, sc.compare(referenceSpanList, sameEndButStartsBeforeSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, sameEndButStartsAfterSpanList));
		assertEquals(1, sc.compare(referenceSpanList, adjacentBeforeSpanList));
		assertEquals(-1, sc.compare(referenceSpanList, adjacentAfterSpanList));
	}

	/**
	 * Test that the overlaps() method functions properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSloppySpanComparator() throws Exception {
		sc = new SloppySpanComparator();
		/* test on individual spans */
		assertEquals(1, sc.overlaps(referenceSpan, beforeSpan));
		assertEquals(-1, sc.overlaps(referenceSpan, afterSpan));
		assertEquals(0, sc.overlaps(referenceSpan, beforeOverlapSpan));
		assertEquals(0, sc.overlaps(referenceSpan, afterOverlapSpan));
		assertEquals(0, sc.overlaps(referenceSpan, completeOverlapSpan));
		assertEquals(0, sc.overlaps(referenceSpan, internalSpan));
		assertEquals(0, sc.overlaps(referenceSpan, matchSpan));
		assertEquals(0, sc.overlaps(referenceSpan, sameStartButEndsBeforeSpan));
		assertEquals(0, sc.overlaps(referenceSpan, sameStartButEndsAfterSpan));
		assertEquals(0, sc.overlaps(referenceSpan, sameEndButStartsBeforeSpan));
		assertEquals(0, sc.overlaps(referenceSpan, sameEndButStartsAfterSpan));
		assertEquals(1, sc.overlaps(referenceSpan, adjacentBeforeSpan));
		assertEquals(-1, sc.overlaps(referenceSpan, adjacentAfterSpan));

		/* test on span lists */
		assertEquals(1, sc.overlaps(referenceSpanList, beforeSpanList));
		assertEquals(-1, sc.overlaps(referenceSpanList, afterSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, beforeOverlapSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, afterOverlapSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, completeOverlapSpanList));
		assertEquals(-1, sc.overlaps(referenceSpanList, internalSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, matchSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, sameStartButEndsBeforeSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, sameStartButEndsAfterSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, sameEndButStartsBeforeSpanList));
		assertEquals(0, sc.overlaps(referenceSpanList, sameEndButStartsAfterSpanList));
		assertEquals(1, sc.overlaps(referenceSpanList, adjacentBeforeSpanList));
		assertEquals(-1, sc.overlaps(referenceSpanList, adjacentAfterSpanList));
	}

	/**
	 * Test that the sharesSpanStart() method functions properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSharesSpanStartSpanComparator() throws Exception {
		sc = new SharedStartSpanComparator();
		/* test on individual spans */
		assertEquals(1, sc.sharesSpanStart(referenceSpan, beforeSpan));
		assertEquals(-1, sc.sharesSpanStart(referenceSpan, afterSpan));
		assertEquals(1, sc.sharesSpanStart(referenceSpan, beforeOverlapSpan));
		assertEquals(-1, sc.sharesSpanStart(referenceSpan, afterOverlapSpan));
		assertEquals(1, sc.sharesSpanStart(referenceSpan, completeOverlapSpan));
		assertEquals(-1, sc.sharesSpanStart(referenceSpan, internalSpan));
		assertEquals(0, sc.sharesSpanStart(referenceSpan, matchSpan));
		assertEquals(0, sc.sharesSpanStart(referenceSpan, sameStartButEndsBeforeSpan));
		assertEquals(0, sc.sharesSpanStart(referenceSpan, sameStartButEndsAfterSpan));
		assertEquals(1, sc.sharesSpanStart(referenceSpan, sameEndButStartsBeforeSpan));
		assertEquals(-1, sc.sharesSpanStart(referenceSpan, sameEndButStartsAfterSpan));
		assertEquals(1, sc.sharesSpanStart(referenceSpan, adjacentBeforeSpan));
		assertEquals(-1, sc.sharesSpanStart(referenceSpan, adjacentAfterSpan));

		/* test on span lists */
		assertEquals(1, sc.sharesSpanStart(referenceSpanList, beforeSpanList));
		assertEquals(-1, sc.sharesSpanStart(referenceSpanList, afterSpanList));
		assertEquals(1, sc.sharesSpanStart(referenceSpanList, beforeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanStart(referenceSpanList, afterOverlapSpanList));
		assertEquals(1, sc.sharesSpanStart(referenceSpanList, completeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanStart(referenceSpanList, internalSpanList));
		assertEquals(0, sc.sharesSpanStart(referenceSpanList, matchSpanList));
		assertEquals(0, sc.sharesSpanStart(referenceSpanList, sameStartButEndsBeforeSpanList));
		assertEquals(0, sc.sharesSpanStart(referenceSpanList, sameStartButEndsAfterSpanList));
		assertEquals(1, sc.sharesSpanStart(referenceSpanList, sameEndButStartsBeforeSpanList));
		assertEquals(-1, sc.sharesSpanStart(referenceSpanList, sameEndButStartsAfterSpanList));
		assertEquals(1, sc.sharesSpanStart(referenceSpanList, adjacentBeforeSpanList));
		assertEquals(-1, sc.sharesSpanStart(referenceSpanList, adjacentAfterSpanList));
	}

	/**
	 * Test that the sharesSpanEnd() method functions properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSharesSpanEndSpanComparator() throws Exception {
		sc = new SharedEndSpanComparator();
		/* test on individual spans */
		assertEquals(1, sc.sharesSpanEnd(referenceSpan, beforeSpan));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpan, afterSpan));
		assertEquals(1, sc.sharesSpanEnd(referenceSpan, beforeOverlapSpan));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpan, afterOverlapSpan));
		assertEquals(1, sc.sharesSpanEnd(referenceSpan, completeOverlapSpan));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpan, internalSpan));
		assertEquals(0, sc.sharesSpanEnd(referenceSpan, matchSpan));
		assertEquals(1, sc.sharesSpanEnd(referenceSpan, sameStartButEndsBeforeSpan));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpan, sameStartButEndsAfterSpan));
		assertEquals(0, sc.sharesSpanEnd(referenceSpan, sameEndButStartsBeforeSpan));
		assertEquals(0, sc.sharesSpanEnd(referenceSpan, sameEndButStartsAfterSpan));
		assertEquals(1, sc.sharesSpanEnd(referenceSpan, adjacentBeforeSpan));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpan, adjacentAfterSpan));

		/* test on span lists */
		assertEquals(1, sc.sharesSpanEnd(referenceSpanList, beforeSpanList));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpanList, afterSpanList));
		assertEquals(1, sc.sharesSpanEnd(referenceSpanList, beforeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpanList, afterOverlapSpanList));
		assertEquals(1, sc.sharesSpanEnd(referenceSpanList, completeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpanList, internalSpanList));
		assertEquals(0, sc.sharesSpanEnd(referenceSpanList, matchSpanList));
		assertEquals(1, sc.sharesSpanEnd(referenceSpanList, sameStartButEndsBeforeSpanList));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpanList, sameStartButEndsAfterSpanList));
		assertEquals(0, sc.sharesSpanEnd(referenceSpanList, sameEndButStartsBeforeSpanList));
		assertEquals(0, sc.sharesSpanEnd(referenceSpanList, sameEndButStartsAfterSpanList));
		assertEquals(1, sc.sharesSpanEnd(referenceSpanList, adjacentBeforeSpanList));
		assertEquals(-1, sc.sharesSpanEnd(referenceSpanList, adjacentAfterSpanList));
	}

	/**
	 * Test that the sharesSpanStartOrEnd() method functions properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSharesSpanStartOrEndSpanComparator() throws Exception {
		sc = new SharedStartOrEndSpanComparator();
		/* test on individual spans */
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpan, beforeSpan));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpan, afterSpan));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpan, beforeOverlapSpan));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpan, afterOverlapSpan));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpan, completeOverlapSpan));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpan, internalSpan));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpan, matchSpan));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpan, sameStartButEndsBeforeSpan));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpan, sameStartButEndsAfterSpan));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpan, sameEndButStartsBeforeSpan));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpan, sameEndButStartsAfterSpan));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpan, adjacentBeforeSpan));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpan, adjacentAfterSpan));

		/* test on span lists */
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpanList, beforeSpanList));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpanList, afterSpanList));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpanList, beforeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpanList, afterOverlapSpanList));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpanList, completeOverlapSpanList));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpanList, internalSpanList));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpanList, matchSpanList));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpanList, sameStartButEndsBeforeSpanList));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpanList, sameStartButEndsAfterSpanList));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpanList, sameEndButStartsBeforeSpanList));
		assertEquals(0, sc.sharesSpanStartOrEnd(referenceSpanList, sameEndButStartsAfterSpanList));
		assertEquals(1, sc.sharesSpanStartOrEnd(referenceSpanList, adjacentBeforeSpanList));
		assertEquals(-1, sc.sharesSpanStartOrEnd(referenceSpanList, adjacentAfterSpanList));
	}

}
