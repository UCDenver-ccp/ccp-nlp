/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

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
	protected void initJCas() throws Exception {
		addGeneAnnotationToJCas(0, 10, EG_ID);
	}

	@Test
	public void testSlotValuePromotion() throws ResourceInitializationException, AnalysisEngineProcessException {
		boolean transferSlotValues = false;
		boolean deleteSourceAnnotation = false;
		AnalysisEngineDescription aeDesc = SlotValueToClassMentionPromoter_AE.createAnalysisEngineDescription(
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionTypes.GENE, transferSlotValues,
				deleteSourceAnnotation);
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionTypes.GENE)) {
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
				getTypeSystem(), HAS_ENTREZ_GENE_ID_SLOT_NAME, ClassMentionTypes.GENE, transferSlotValues,
				deleteSourceAnnotation);
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		boolean hasGeneAnnot = false;
		boolean hasEgIdAnnot = false;
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionTypes.GENE)) {
				hasGeneAnnot = true;
			}
			if (mentionName.equals("12345")) {
				hasEgIdAnnot = true;
			}
		}
		assertTrue(hasEgIdAnnot);
		assertFalse(hasGeneAnnot);
	}

}
