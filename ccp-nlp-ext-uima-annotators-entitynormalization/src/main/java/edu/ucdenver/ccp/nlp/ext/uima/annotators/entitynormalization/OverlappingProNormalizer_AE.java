/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * If there is a protein that overlaps with a PRO ontology annotation, then we infer that that
 * protein can be normalized to that PRO ontology term, though we ignore PR:0000000001 which matches
 * "protein".
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class OverlappingProNormalizer_AE extends JCasAnnotator_ImplBase {

	private static final Set<String> PRO_TERMS_TO_IGNORE = CollectionsUtil.createSet("PR:000000001");
	public static final String PRO_ID_SLOT_NAME = "hasProId"; 
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<CCPTextAnnotation> proteinAnnots = CollectionsUtil.createList(UIMA_Util.getTextAnnotationIterator(jcas, "protein"));
		
		for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.startsWith("PR:") && !PRO_TERMS_TO_IGNORE.contains(mentionName)) {
				for (CCPTextAnnotation proteinAnnot : proteinAnnots) {
					if (new WrappedCCPTextAnnotation(proteinAnnot).overlaps(new WrappedCCPTextAnnotation(ccpTa))) {
						assignProIdToAnnotation(proteinAnnot, mentionName);
					}
				}
			}
		}
	}

	/**
	 * @param ccpTa
	 * @param mentionName
	 * @throws AnalysisEngineProcessException 
	 */
	private void assignProIdToAnnotation(CCPTextAnnotation ccpTa, String proIdStr) throws AnalysisEngineProcessException {
		try {
			ccpTa.getClassMention().setMentionName(proIdStr);
			//TextAnnotationUtil.addSlotValue(new WrappedCCPTextAnnotation(ccpTa), PRO_ID_SLOT_NAME, proIdStr);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}

}
