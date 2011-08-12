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

public class AnnotatorTest {

	private Annotator annotator1;

	private Annotator annotator2;

	private Annotator annotator3;

	@Before
	public void setUp() throws Exception {
		annotator1 = new Annotator(new Integer(1), "First1", "Last1", "Affiliation1");
		annotator2 = new Annotator(new Integer(2), "First2", "Last2", "Affiliation1");
		annotator3 = new Annotator(new Integer(3), "first1", "last1", "affiliation1");
	}

	@After
	public void tearDown() throws Exception {
		annotator1 = null;
		annotator2 = null;
		annotator3 = null;
	}

	@Test
	public void testCompareTo() {
		assertEquals(annotator1.compareTo(annotator2), -1);
		assertEquals(annotator1.compareTo(annotator1), 0);
		assertEquals(annotator1.compareTo(annotator3), 0);
		assertEquals(annotator2.compareTo("this is not an annotator"), -1);
	}

	@Test
	public void testEquals() {
		assertTrue(annotator1.equals(annotator1));
		assertFalse(annotator1.equals(annotator2));
		assertTrue(annotator1.equals(annotator3));
		assertFalse(annotator2.equals("this is not an annotator"));
	}

	@Test
	public void testHashCode() {
		String key = annotator1.getFirstName() + "|" + annotator1.getLastName() + "|" + annotator1.getAffiliation();
		key = key.toLowerCase();
		int hashcode = key.hashCode();
		assertEquals(annotator1.hashCode(), hashcode);
		assertEquals(annotator1.hashCode(), annotator3.hashCode());
	}

	@Test
	public void testGetters() {
		assertEquals(annotator2.getAnnotatorID(), new Integer(2));
		assertEquals(annotator2.getFirstName(), "First2");
		assertEquals(annotator2.getLastName(), "Last2");
		assertEquals(annotator2.getAffiliation(), "Affiliation1");
	}

	@Test
	public void testSettersAndGetters() {
		annotator3.setAnnotatorID(new Integer(4));
		annotator3.setFirstName("First4");
		annotator3.setLastName("Last4");
		annotator3.setAffiliation("Affiliation4");

		assertEquals(annotator3.getAnnotatorID(), new Integer(4));
		assertEquals(annotator3.getFirstName(), "First4");
		assertEquals(annotator3.getLastName(), "Last4");
		assertEquals(annotator3.getAffiliation(), "Affiliation4");
	}

}
