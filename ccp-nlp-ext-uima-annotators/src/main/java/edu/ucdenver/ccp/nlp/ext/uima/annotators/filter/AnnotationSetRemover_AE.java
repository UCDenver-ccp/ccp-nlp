/*
 * AnnotationSetFilter_AE.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * This utility analysis engine removes all annotations belonging to the user-specified annotation
 * sets.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class AnnotationSetRemover_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ANNOTATIONSET_IDS_TO_REMOVE_LIST = "AnnotationSetIDsToRemoveList";

	private Set<Integer> annotationSetIDsToRemove;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		/* read in input parameters, and initialize a list of annotator ids to keep */
		Integer[] annotationSetIDs = (Integer[]) context
				.getConfigParameterValue(PARAM_ANNOTATIONSET_IDS_TO_REMOVE_LIST);
		annotationSetIDsToRemove = new HashSet<Integer>();
		for (Integer id : annotationSetIDs) {
			annotationSetIDsToRemove.add(id);
		}
		super.initialize(context);
	}

	/**
	 * cycle through all annotations and remove the annotations that do not have a annotation set in
	 * the annotation-set-ids-to-keep-list
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();

		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();

		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				FSArray ccpAnnotationSets = ccpTA.getAnnotationSets();
				boolean keep = true;
				for (int i = 0; i < ccpAnnotationSets.size(); i++) {
					CCPAnnotationSet annotationSet = (CCPAnnotationSet) ccpAnnotationSets.get(i);
					Integer annotationSetID = new Integer(annotationSet.getAnnotationSetID());
					if (annotationSetIDsToRemove.contains(annotationSetID)) {
						keep = false;
						break;
					}
				}
				if (!keep) {
					annotationsToRemove.add(ccpTA);
				}
			} else {
				System.err.println("WARNING -- AnnotationSetFilter_AE: CCPTextAnnotation expected but instead got "
						+ possibleAnnot.getClass().getName());
			}
		}

		/* now remove annotations that had annotators not in the annotatorIDsToKeep list */
		for (CCPTextAnnotation ccpTA : annotationsToRemove) {
			ccpTA.removeFromIndexes();
			ccpTA = null;
		}

	}

}
