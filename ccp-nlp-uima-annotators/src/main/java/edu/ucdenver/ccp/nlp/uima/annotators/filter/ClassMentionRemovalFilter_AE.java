/*
 * ClassMentionRemovalFilter_AE.java
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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;

/**
 * This utility analysis engine enables the user to remove all annotations that are not a specific
 * class mention type from the CAS indexes.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class ClassMentionRemovalFilter_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST = "ClassMentionTypesToRemoveList";
	static Logger logger = Logger.getLogger(ClassMentionRemovalFilter_AE.class);
	private List<String> classMentionTypesToRemove;

	@Override
	public void initialize(UimaContext ac) throws ResourceInitializationException {
		/* read in input parameters, and initialize a list of class mention types to remove */

		String[] classMentionTypes = (String[]) ac.getConfigParameterValue(PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST);
		classMentionTypesToRemove = new ArrayList<String>();
		for (String type : classMentionTypes) {
			classMentionTypesToRemove.add(type.toLowerCase());
		}

		logger.info("Initialized ClassMentionRemovalFilter; types to remove: " + classMentionTypesToRemove.toString());

		super.initialize(ac);
	}

	/**
	 * cycle through all annotations and remove those that have annotation types in the
	 * class-mention-types-to-remove list
	 */
	public void process(JCas jcas) {

		List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();

		Iterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();

		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				CCPClassMention ccpCM = ccpTA.getClassMention();
				String classMentionType = ccpCM.getMentionName().toLowerCase();
				if (classMentionTypesToRemove.contains(classMentionType)) {
					annotationsToRemove.add(ccpTA);
				}
			} else {
				logger.warn("CCPTextAnnotation expected but instead got " + possibleAnnot.getClass().getName());
			}
		}

		/* now remove annotations that had class mention types in the classMentionTypesToRemove list */
		int count = 0;
		for (CCPTextAnnotation ccpTA : annotationsToRemove) {
			ccpTA.removeFromIndexes();
			// ccpTA = null;
			count++;
		}
		logger.info("ClassMentionRemovalFilter Removed " + count + " annotations matching: " + classMentionTypesToRemove.toString());

	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String[] removeMentions)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription(tsd, removeMentions));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			String[] removeMentions) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ClassMentionRemovalFilter_AE.class, tsd,
				PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST, removeMentions);
	}

}
