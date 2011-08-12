/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.core.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;

public class SpanTest {

	@Test
	public void testGettersAndSetters() throws InvalidSpanException {

		Span span1 = new Span(5, 11);

		assertEquals(span1.getSpanStart(), 5);
		assertEquals(span1.getSpanEnd(), 11);

		span1.setSpanStart(6);
		span1.setSpanEnd(12);

		assertEquals(span1.getSpanStart(), 6);
		assertEquals(span1.getSpanEnd(), 12);
	}

	@Test (expected=InvalidSpanException.class)
	public void testInvalidSpanException() throws Exception {
			new Span(11, 5);
	}

	@Test (expected=InvalidSpanException.class)
	public void testInvalidSpanException_negatives0() throws Exception {
			new Span(-1, 5);
	}

	
	@Test (expected=InvalidSpanException.class)
	public void testInvalidSpanException_negatives1() throws Exception {
			new Span(0, -5);
	}

	
	@Test
	public void testEquals() {
		try {
			Span span1 = new Span(3, 8);
			Span span2 = new Span(3, 8);
			Span span3 = new Span(3, 9);

			assertTrue(span1.equals(span2));
			assertTrue(span2.equals(span1));
			assertFalse(span1.equals(span3));
			assertFalse(span2.equals(span3));

		} catch (InvalidSpanException e) {
			e.printStackTrace();
			fail("Test failed - invalid span exception.");
		}
	}

	
	@Test
    public void testContains() {
        try {
            Span span1 = new Span(3, 8);
            Span span2 = new Span(2, 9);
            Span span3 = new Span(4, 9);

            assertFalse(span1.containsSpan(span2));
            assertTrue(span2.containsSpan(span1));
            assertTrue(span2.containsSpan(span3));
            assertFalse(span3.containsSpan(span1));
            assertTrue(span2.containsSpan(span3));

        } catch (InvalidSpanException e) {
            e.printStackTrace();
            fail("Test failed - invalid span exception.");
        }
    }
	
	@Test
	public void testOverlaps() {
		try {
			/* span to test against */
			Span span0 = new Span(25, 30);

			/* equal span */
			Span span1 = new Span(25, 30);

			/* overlaps on left */
			Span span2 = new Span(22, 28);

			/* overlaps on right */
			Span span3 = new Span(28, 33);

			/* encompasses span0 */
			Span span4 = new Span(20, 35);

			/* is encompassed by span0 */
			Span span5 = new Span(26, 28);

			/* to the left */
			Span span6 = new Span(20, 25);

			/* to the right */
			Span span7 = new Span(30, 39);

			/* way left */
			Span span8 = new Span(0, 5);

			/* way right */
			Span span9 = new Span(50, 57);

			assertTrue(span0.overlaps(span1));
			assertTrue(span0.overlaps(span2));
			assertTrue(span0.overlaps(span3));
			assertTrue(span0.overlaps(span4));
			assertTrue(span0.overlaps(span5));
			assertFalse(span0.overlaps(span6));
			assertFalse(span0.overlaps(span7));
			assertFalse(span0.overlaps(span8));
			assertFalse(span0.overlaps(span9));

			assertFalse(span0.overlaps(24));
			assertTrue(span0.overlaps(25));
			assertTrue(span0.overlaps(26));
			assertTrue(span0.overlaps(27));
			assertTrue(span0.overlaps(28));
			assertTrue(span0.overlaps(29));
			assertFalse(span0.overlaps(30));
			assertFalse(span0.overlaps(31));

		} catch (InvalidSpanException e) {
			e.printStackTrace();
			fail("Test failed - invalid span exception.");
		}
	}

	@Test
	public void testSortOrder() throws Exception {
		Span span0 = new Span(25, 30);
		Span span1 = new Span(25, 30);
		Span span2 = new Span(22, 28);
		Span span3 = new Span(28, 33);
		Span span4 = new Span(20, 35);
		Span span5 = new Span(26, 28);
		Span span6 = new Span(20, 25);
		Span span7 = new Span(30, 39);
		Span span8 = new Span(0, 5);
		Span span9 = new Span(50, 57);

		List<Span> spanList = new ArrayList<Span>();
		spanList.add(span0);
		spanList.add(span1);
		spanList.add(span2);
		spanList.add(span3);
		spanList.add(span4);
		spanList.add(span5);
		spanList.add(span6);
		spanList.add(span7);
		spanList.add(span8);
		spanList.add(span9);

		Collections.sort(spanList, new StrictSpanComparator());

		List<Span> expectedSortedSpanList = new ArrayList<Span>();
		expectedSortedSpanList.add(span8);
		expectedSortedSpanList.add(span6);
		expectedSortedSpanList.add(span4);
		expectedSortedSpanList.add(span2);
		expectedSortedSpanList.add(span0);
		expectedSortedSpanList.add(span1);
		expectedSortedSpanList.add(span5);
		expectedSortedSpanList.add(span3);
		expectedSortedSpanList.add(span7);
		expectedSortedSpanList.add(span9);

		assertEquals(expectedSortedSpanList, spanList);

	}

	@Test
	public void testStaticToStringForSpanList() throws Exception {
		List<Span> spanList = new ArrayList<Span>();
		String spanListStr = Span.toString(spanList);
		String expectedSpanListStr = "[]";
		assertEquals(String.format("Input of an empty list should result in return of '[]'"), expectedSpanListStr, spanListStr);

		spanList.add(new Span(3,7));
		spanList.add(new Span(12,46));
		spanList.add(new Span(57,88));
		spanListStr = Span.toString(spanList);
		expectedSpanListStr = "[3..7]_[12..46]_[57..88]";
		assertEquals(String.format("Expected a listing of individual spans delimited by underscores."), expectedSpanListStr, spanListStr);
	}
	
}
