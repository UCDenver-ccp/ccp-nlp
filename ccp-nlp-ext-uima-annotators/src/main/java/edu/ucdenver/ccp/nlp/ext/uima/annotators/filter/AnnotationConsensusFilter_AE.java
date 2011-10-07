/*
 * AnnotationConsensusFilter_AE.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */

package edu.ucdenver.ccp.nlp.ext.uima.annotators.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * This utility analysis engine enables the user to create consensus annotations for a specified annotation type. The
 * input consensus threshold determines the level of consensus necessary to create a consensus annotation. The
 * annotation-sets-to-ignore list enable the user to ignore a group of annotations. This might be useful if the CAS
 * contains gold standard annotations that will later be used to evaluate a collection of applications, but should not
 * be included when determining consensus.
 * <p>
 * To determine consensus, annotations must match in type (class mention name) as well as have identical spans.
 * <p>
 * NOTE: The current implementation of this filter does not remove duplicate annotations, which, if present, will skew
 * results.
 * 
 * @author William A Baumgartner, Jr.
 */
public class AnnotationConsensusFilter_AE extends JCasAnnotator_ImplBase {

	public static final int ANNOTATOR_ID = 88;
	
	private int consensusThreshold = 1;

	public static final String PARAM_ANNOTATION_SETS_TO_IGNORE = "AnnotationSetsToIgnore";

	/**
	 * The annotation type to look at when making consensus selections
	 */
	public static final String PARAM_ANNOTATION_TYPE_OF_INTEREST = "AnnotationTypeOfInterest";

	/**
     */
	public static final String PARAM_CONSENSUS_THRESHOLD = "ConsensusThreshold";

	protected Set<Integer> annotationSetsToIgnore;

	protected String annotationTypeOfInterest;

	

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
			consensusThreshold = ((Integer) context.getConfigParameterValue(PARAM_CONSENSUS_THRESHOLD)).intValue();
			annotationTypeOfInterest = (String) context.getConfigParameterValue(PARAM_ANNOTATION_TYPE_OF_INTEREST);
			try {
				annotationSetsToIgnore = new HashSet<Integer>(Arrays.asList((Integer[]) context
						.getConfigParameterValue(PARAM_ANNOTATION_SETS_TO_IGNORE)));
			} catch (NullPointerException npe) {
				annotationSetsToIgnore = new HashSet<Integer>();
			}
		System.err.println("Initialized Consensus Filter with threshold=" + consensusThreshold);
		super.initialize(context);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		/* cycle through all annotations, and look for ones that satisfy the consensus threshold for agreement */
		Map<String, Integer> span2countMap = new HashMap<String, Integer>();

		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {

			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);

			if (!ignore) {
				if (ccpTA.getClassMention().getMentionName().equals(annotationTypeOfInterest)) {

					String spanStr = ccpTA.getBegin() + "|" + ccpTA.getEnd();
					if (span2countMap.containsKey(spanStr)) {
						int count = span2countMap.get(spanStr);
						count++;
						span2countMap.remove(spanStr);
						span2countMap.put(spanStr, new Integer(count));
					} else {
						span2countMap.put(spanStr, new Integer(1));
					}
				}
			}
		}

		/* cycle through the span2countmap and output annotations that pass the consensus threshold */
		Set<String> spanStrings = span2countMap.keySet();
		for (String spanStr : spanStrings) {
			int countForThisSpan = span2countMap.get(spanStr);
			if (countForThisSpan >= consensusThreshold) {
				/* then create a new annotation */
				String[] toks = spanStr.split("\\|");
				createConsensusAnnotation(Integer.parseInt(toks[0]), Integer.parseInt(toks[1]), jcas);
			}
		}

	}

	private void createConsensusAnnotation(int startIndex, int endIndex, JCas jcas) {
		CCPTextAnnotation consensusAnnotation = new CCPTextAnnotation(jcas);
		try {
			UIMA_Util.setCCPTextAnnotationSpan(consensusAnnotation, startIndex, endIndex);
		} catch (CASException e1) {
			e1.printStackTrace();
		}

		/* set annotator and annotation set */
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAffiliation("UCHSC");
		ccpAnnotator.setFirstName("Consensus Filter");
		ccpAnnotator.setAnnotatorID(ANNOTATOR_ID);
		ccpAnnotator.setLastName("Consensus Filter");

		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(ANNOTATOR_ID);
		ccpAnnotationSet.setAnnotationSetName("Consensus Annotations");
		ccpAnnotationSet.setAnnotationSetDescription("");

		consensusAnnotation.setAnnotator(ccpAnnotator);
		FSArray asets = new FSArray(jcas, 1);
		asets.set(0, ccpAnnotationSet);
		consensusAnnotation.setAnnotationSets(asets);

		consensusAnnotation.setDocumentSectionID(-1);

		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName(annotationTypeOfInterest);
		consensusAnnotation.setClassMention(ccpCM);
		ccpCM.setCcpTextAnnotation(consensusAnnotation);
		// try {
		// UIMA_Util.addCCPTextAnnotationToCCPClassMention(consensusAnnotation, ccpCM);
		// } catch (CASException e) {
		// e.printStackTrace();
		// }
		consensusAnnotation.addToIndexes();

	}

	protected boolean checkForIgnoreBasedOnAnnotationSet(CCPTextAnnotation ccpTA) {
		boolean ignore = false;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		for (int i = 0; i < annotationSets.size(); i++) {
			CCPAnnotationSet aSet = (CCPAnnotationSet) annotationSets.get(i);
			if (annotationSetsToIgnore.contains(aSet.getAnnotationSetID())) {
				ignore = true;
				break;
			}
		}
		return ignore;
	}

}
