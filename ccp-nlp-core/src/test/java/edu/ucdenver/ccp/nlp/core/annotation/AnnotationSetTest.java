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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnnotationSetTest {

	private AnnotationSet annotationSet1;

	private AnnotationSet annotationSet2;

	private AnnotationSet annotationSet3;

	@Before
	public void setUp() throws Exception {
		annotationSet1 = new AnnotationSet(new Integer(1), "SetName1", "Description1");
		annotationSet2 = new AnnotationSet(new Integer(2), "SetName2", "Description2");
		annotationSet3 = new AnnotationSet(new Integer(3), "setname1", "description3");
	}

	@After
	public void tearDown() throws Exception {
		annotationSet1 = null;
		annotationSet2 = null;
		annotationSet3 = null;
	}

	@Test
	public void testCompareTo() {
		assertEquals(annotationSet1.compareTo(annotationSet2), -1);
		assertEquals(annotationSet1.compareTo(annotationSet1), 0);
		assertEquals(annotationSet1.compareTo(annotationSet3), 0);
		assertEquals(annotationSet2.compareTo("this is not an annotationSet"), -1);
	}

	@Test
	public void testEquals() {
		assertTrue(annotationSet1.equals(annotationSet1));
		assertFalse(annotationSet1.equals(annotationSet2));
		assertTrue(annotationSet1.equals(annotationSet3));
		assertFalse(annotationSet2.equals("this is not an annotationSet"));
	}

	@Test
	public void testHashCode() {
		String key = annotationSet1.getAnnotationSetName();
		key = key.toLowerCase();
		int hashcode = key.hashCode();
		assertEquals(annotationSet1.hashCode(), hashcode);
		assertEquals(annotationSet1.hashCode(), annotationSet3.hashCode());
	}

	@Test
	public void testGetters() {
		assertEquals(annotationSet2.getAnnotationSetID(), new Integer(2));
		assertEquals(annotationSet2.getAnnotationSetName(), "SetName2");
		assertEquals(annotationSet2.getAnnotationSetDescription(), "Description2");
	}

	@Test
	public void testSettersAndGetters() {
		annotationSet3.setAnnotationSetID(new Integer(4));
		annotationSet3.setAnnotationSetName("SetName4");
		annotationSet3.setAnnotationSetDescription("Description4");

		assertEquals(annotationSet3.getAnnotationSetID(), new Integer(4));
		assertEquals(annotationSet3.getAnnotationSetName(), "SetName4");
		assertEquals(annotationSet3.getAnnotationSetDescription(), "Description4");
	}

}
