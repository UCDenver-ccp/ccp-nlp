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
 */package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.comparison.MentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * Tests a condition where the overlapping span comparator is being used and there are overlapping
 * gold standard annotations
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class AnnotationComparatorOverlapTest {
	/* 01234567890123456789012345678901234567890123456789 */
	private static final String DOCUMENT_TEXT = "We used Fekete's acid-alcohol-formalin.";

	private Annotator annotator = new Annotator(new Integer(-1), "Test", "Annotator", "UCHSC");
	private AnnotationSet annotationSet = new AnnotationSet(new Integer(-1), "Test Annotation Set",
			"This is a test annotation set.");
	private int documentCollectionID = -1;
	private int documentSectionID = -1;

	/**
	 * Shows that an eval annotation that overlaps two gold standard annotations results in 1 TP and
	 * 0 FN (and also 0 FP). This means that checking that TP+FN in the gold standard sanity check
	 * is the same as TP+FN in an overlapping eval check will sometimes not be the same.
	 */
	@Test
	public void testHandlingOfOverlappingGoldStandardAnnotations() {

		TextAnnotation goldAnnotation1 = createChemicalAnnotation(1, 17, 21, "1", DOCUMENT_TEXT); // acid
		TextAnnotation goldAnnotation2 = createChemicalAnnotation(2, 22, 29, "1", DOCUMENT_TEXT); // alcohol
		TextAnnotation evalAnnotation = createChemicalAnnotation(3, 8, 38, "1", DOCUMENT_TEXT); // Fekete's
																								// acid-alcohol-formalin

		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SloppySpanComparator();
		MentionComparator mc = new IdenticalMentionComparator();

		PRFResult prf = ac.compare(CollectionsUtil.createList(goldAnnotation1, goldAnnotation2),
				CollectionsUtil.createList(evalAnnotation), sc, mc);
		assertEquals(1, prf.getTruePositiveCount());
		assertEquals(0, prf.getFalsePositiveCount());
		assertEquals(0, prf.getFalseNegativeCount());

	}

	/**
	 * Create a protein annotation to be used for testing purposes
	 */
	private DefaultTextAnnotation createChemicalAnnotation(int annotationID, int spanStart, int spanEnd,
			String documentID, String documentText) {

		String coveredText = documentText.substring(spanStart, spanEnd);

		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.CHEMICAL.typeName());

		DefaultTextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd, coveredText, annotator, annotationSet,
				annotationID, documentCollectionID, documentID, documentSectionID, cm);

		return ta;
	}

}
