package edu.ucdenver.ccp.nlp.uima.annotators.filter;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;

/**
 * This utility analysis engine enables the user to remove all annotations that are not a specific
 * class mention type from the CAS indexes.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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
		logger.info("ClassMentionRemovalFilter Removed " + count + " annotations matching: "
				+ classMentionTypesToRemove.toString());

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
