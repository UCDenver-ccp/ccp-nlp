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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TextAnnotationUtilTest {
	@Test
	public void testGetRedundantAnnotations() throws Exception {

		final Annotator annotator1 = new Annotator(3, "first1", "last1", "affiliation1");
		final AnnotationSet annotationSet1 = new AnnotationSet(5, "set name1", "desc1");
		final int annotationID1 = 37;
		final Annotator annotator2 = new Annotator(3, "first2", "last2", "affiliation2");
		final AnnotationSet annotationSet2 = new AnnotationSet(5, "set name2", "desc2");
		final int annotationID2 = 39;

		final int documentCollectionID = 4;
		final String documentID = "123456";
		final int documentSectionID = -1;

		Collection<TextAnnotation> annotations = new ArrayList<TextAnnotation>();

		ClassMention cm1 = new DefaultClassMention("protein");
		TextAnnotation ta1 = new DefaultTextAnnotation(0, 5, "ABC1", annotator1, annotationSet1, annotationID1,
				documentCollectionID, documentID, documentSectionID, cm1);
		ClassMention cm2 = new DefaultClassMention("protein");
		TextAnnotation ta2 = new DefaultTextAnnotation(8, 15, "protein34", annotator1, annotationSet1, annotationID1,
				documentCollectionID, documentID, documentSectionID, cm2);
		ClassMention cm3 = new DefaultClassMention("protein");
		TextAnnotation ta3 = new DefaultTextAnnotation(10, 15, "blahblah", annotator1, annotationSet1, annotationID1,
				documentCollectionID, documentID, documentSectionID, cm3);

		Collection<TextAnnotation> redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(0, redundantAnnotations.size());

		annotations.add(ta1);

		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(0, redundantAnnotations.size());

		ClassMention cm0 = new DefaultClassMention("protein");
		TextAnnotation ta0 = new DefaultTextAnnotation(0, 5, "ABC1", annotator1, annotationSet1, annotationID1,
				documentCollectionID, documentID, documentSectionID, cm0);
		annotations.add(ta0);

		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(1, redundantAnnotations.size());

		ClassMention cm4 = new DefaultClassMention("protein");
		TextAnnotation redundantTa1 = new DefaultTextAnnotation(0, 5, "ABC1", annotator2, annotationSet2,
				annotationID2, documentCollectionID, documentID, documentSectionID, cm4);
		annotations.add(redundantTa1);

		assertEquals(3, annotations.size());

		assertTrue(redundantTa1.equals(ta0));
		assertTrue(ta0.equals(ta1));
		assertTrue(redundantTa1.equals(ta1));
		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(2, redundantAnnotations.size());

		annotations.add(ta2);
		annotations.add(ta3);
		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(2, redundantAnnotations.size());

		ClassMention cm5 = new DefaultClassMention("protein");
		TextAnnotation redundantTa2 = new DefaultTextAnnotation(8, 15, "protein34", annotator1, annotationSet1,
				annotationID1, documentCollectionID, documentID, documentSectionID, cm5);
		ClassMention cm6 = new DefaultClassMention("protein");
		TextAnnotation redundantTa3 = new DefaultTextAnnotation(10, 15, "blahblah", annotator1, annotationSet1,
				annotationID1, documentCollectionID, documentID, documentSectionID, cm6);

		annotations.add(redundantTa2);
		annotations.add(redundantTa3);

		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(4, redundantAnnotations.size());

		ClassMention cm7 = new DefaultClassMention("protein");
		PrimitiveSlotMention<String> sm = new DefaultStringSlotMention("slot1");
		sm.addSlotValue("slot value 1");
		cm7.addPrimitiveSlotMention(sm);
		TextAnnotation almostRedundantTa3 = new DefaultTextAnnotation(10, 15, "blahblah", annotator1, annotationSet1,
				annotationID1, documentCollectionID, documentID, documentSectionID, cm7);
		annotations.add(almostRedundantTa3);

		redundantAnnotations = TextAnnotationUtil.getRedundantAnnotations(annotations);
		assertEquals(4, redundantAnnotations.size());

	}
}
