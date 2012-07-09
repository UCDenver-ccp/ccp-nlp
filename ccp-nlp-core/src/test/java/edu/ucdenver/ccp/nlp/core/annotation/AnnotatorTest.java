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
