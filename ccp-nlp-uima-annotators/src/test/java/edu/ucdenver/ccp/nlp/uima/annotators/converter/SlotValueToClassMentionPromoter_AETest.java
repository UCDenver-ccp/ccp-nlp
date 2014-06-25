package edu.ucdenver.ccp.nlp.uima.annotators.converter;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SlotValueToClassMentionPromoter_AETest extends DefaultUIMATestCase {

	private static final int EG_ID = 12345;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws UIMAException {
		addGeneAnnotationToJCas(0, 10, EG_ID);
	}

	@Test
	public void testSlotValuePromotion() throws ResourceInitializationException, AnalysisEngineProcessException {
		boolean transferSlotValues = false;
		boolean deleteSourceAnnotation = false;
		AnalysisEngineDescription aeDesc = SlotValueToClassMentionPromoter_AE.createAnalysisEngineDescription(
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionType.GENE.typeName(), transferSlotValues,
				deleteSourceAnnotation,"");
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionType.GENE.typeName())) {
				hasGeneAnnot = true;
			}
			if (mentionName.equals("12345")) {
				hasEgIdAnnot = true;
			}
		}
		assertTrue(hasEgIdAnnot && hasGeneAnnot);
	}
	
	
	@Test
	public void testSlotValuePromotion_DeleteSourceAnnotation() throws ResourceInitializationException, AnalysisEngineProcessException {
		boolean transferSlotValues = false;
		boolean deleteSourceAnnotation = true;
		AnalysisEngineDescription aeDesc = SlotValueToClassMentionPromoter_AE.createAnalysisEngineDescription(
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionType.GENE.typeName(), transferSlotValues,
				deleteSourceAnnotation,"");
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionType.GENE.typeName())) {
				hasGeneAnnot = true;
			}
			if (mentionName.equals("12345")) {
				hasEgIdAnnot = true;
			}
		}
		assertTrue(hasEgIdAnnot);
		assertFalse(hasGeneAnnot);
	}
	
	
	@Test
	public void testSlotValuePromotion_DeleteSourceAnnotationButNotOtherAnnotationsInCas() throws ResourceInitializationException, AnalysisEngineProcessException {
		boolean transferSlotValues = false;
		boolean deleteSourceAnnotation = true;
		AnalysisEngineDescription aeDesc = SlotValueToClassMentionPromoter_AE.createAnalysisEngineDescription(
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionType.GENE.typeName(), transferSlotValues,
				deleteSourceAnnotation,"");
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		
		// add another annotation to the CAS that should not be deleted
		addParagraphAnnotationToJCas(0, 15);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		int annotCount = 0;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			annotCount++;
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionType.GENE.typeName())) {
				hasGeneAnnot = true;
			}
			if (mentionName.equals("12345")) {
				hasEgIdAnnot = true;
			}
		}
		assertTrue(hasEgIdAnnot);
		assertFalse(hasGeneAnnot);
		assertEquals(2, annotCount);
	}
	
	@Test
	public void testSlotValuePromotion_UseValuePrefix() throws ResourceInitializationException, AnalysisEngineProcessException {
		boolean transferSlotValues = false;
		boolean deleteSourceAnnotation = false;
		AnalysisEngineDescription aeDesc = SlotValueToClassMentionPromoter_AE.createAnalysisEngineDescription(
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionType.GENE.typeName(), transferSlotValues,
				deleteSourceAnnotation, "EG:");
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionType.GENE.typeName())) {
				hasGeneAnnot = true;
			}
			if (mentionName.equals("EG:12345")) {
				hasEgIdAnnot = true;
			}
		}
		assertTrue(hasEgIdAnnot && hasGeneAnnot);
	}

}
