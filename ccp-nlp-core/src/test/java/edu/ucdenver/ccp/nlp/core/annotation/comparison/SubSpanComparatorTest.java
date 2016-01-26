package edu.ucdenver.ccp.nlp.core.annotation.comparison;

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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SubSpanComparatorTest {

	@Test
	public void testEqual() throws Exception {
		// -----
		// -----
		Span a = new Span(3, 10);
		Span b = new Span(3, 10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));
	}

	@Test
	public void testInside() throws Exception {
		// ---
		// -----
		Span a = new Span(5, 8);
		Span b = new Span(3, 10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}

	@Test
	public void testOutsideLeft() throws Exception {
		// --
		// ---
		Span a = new Span(3, 5);
		Span b = new Span(6, 10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideRight() throws Exception {
		// --
		// --
		Span a = new Span(8, 10);
		Span b = new Span(3, 5);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideLeftEqual() throws Exception {
		// ---
		// ---
		Span a = new Span(3, 5);
		Span b = new Span(5, 10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideRightEqual() throws Exception {
		// ----
		// ----
		Span a = new Span(6, 10);
		Span b = new Span(3, 6);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a, b));

	}

	@Test
	public void testEqualLeft() throws Exception {
		// -----
		// --
		Span a = new Span(3, 10);
		Span b = new Span(3, 6);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}

	@Test
	public void testEqualRight() throws Exception {
		// -----
		// ---
		Span a = new Span(3, 10);
		Span b = new Span(8, 10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}

	@Test
	public void testEqualLists() throws Exception {
		// -----
		// -----
		Span a = new Span(3, 10);
		Span a1 = new Span(13, 20);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(3, 10);
		Span b1 = new Span(13, 20);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(aList, bList));
	}

	@Test
	public void testInsideLists() throws Exception {
		// ---
		// -----
		Span a = new Span(5, 8);
		Span a1 = new Span(9, 12);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(3, 10);
		Span b1 = new Span(12, 20);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}

	@Test
	public void testOutsideLeftLists() throws Exception {
		// --
		// ---
		Span a = new Span(3, 5);
		Span a1 = new Span(7, 9);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(11, 13);
		Span b1 = new Span(15, 20);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideRightLists() throws Exception {
		// --
		// --
		Span a = new Span(11, 13);
		Span a1 = new Span(15, 17);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(3, 5);
		Span b1 = new Span(7, 9);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideLeftEqualLists() throws Exception {
		// ---
		// ---
		Span a = new Span(3, 5);
		Span a1 = new Span(8, 12);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(5, 10);
		Span b1 = new Span(12, 15);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a, b));

	}

	@Test
	public void testOutsideRightEqualLists() throws Exception {
		// ----
		// ----
		Span a = new Span(8, 10);
		Span a1 = new Span(12, 15);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(3, 6);
		Span b1 = new Span(7, 11);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a, b));

	}

	@Test
	public void testEqualLeftLists() throws Exception {
		// -----
		// --
		Span a = new Span(3, 10);
		Span a1 = new Span(12, 20);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(3, 6);
		Span b1 = new Span(7, 10);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}

	@Test
	public void testEqualRightLists() throws Exception {
		// -----
		// ---
		Span a = new Span(3, 10);
		Span a1 = new Span(12, 20);
		List<Span> aList = new ArrayList<Span>();
		aList.add(a);
		aList.add(a1);
		Span b = new Span(8, 10);
		Span b1 = new Span(12, 20);
		List<Span> bList = new ArrayList<Span>();
		bList.add(b);
		bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a, b));

	}
}
