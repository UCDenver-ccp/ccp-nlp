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

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
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
