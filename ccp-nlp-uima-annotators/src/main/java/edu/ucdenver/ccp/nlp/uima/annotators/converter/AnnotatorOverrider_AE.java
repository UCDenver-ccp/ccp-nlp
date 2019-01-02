/*
 * AnnotatorOverrider_AE.java 
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

package edu.ucdenver.ccp.nlp.uima.annotators.converter;

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

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * A simple utility Analysis Engine that enables the user to change the
 * annotator assigned to all <code>CCPTextAnnotations</code> stored in the CAS.
 * For each <code>CCPTextAnnotation</code> in the CAS, the user-specified
 * annotator is assigned.
 * 
 * @author William A Baumgartner Jr
 * 
 */
public class AnnotatorOverrider_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ANNOTATOR_ID = "annotatorID";
	@ConfigurationParameter(mandatory = true)
	private int annotatorID = -1;

	public static final String PARAM_FIRST_NAME = "firstName";
	@ConfigurationParameter()
	private String firstName = null;

	public static final String PARAM_LAST_NAME = "lastName";
	@ConfigurationParameter()
	private String lastName = null;

	public static final String PARAM_AFFILIATION = "affiliation";
	@ConfigurationParameter()
	private String affiliation = null;

	public static final String PARAM_ANNOTATOR_IDS_TO_IGNORE = "annotatorIDsToIgnore";
	@ConfigurationParameter(mandatory = false)
	private Set<Integer> annotatorIDsToIgnore;

	public static final String PARAM_ANNOTATION_SET_IDS_TO_IGNORE = "annotationSetIDsToIgnore";
	@ConfigurationParameter(mandatory = false)
	private Set<Integer> annotationSetIDsToIgnore;

	/**
	 * Constant that can be used as the identifier for the gold standard
	 * annotator during annotation comparisons
	 */
	public static final int GOLD_ANNOTATOR_ID = 99099099;

	/**
	 * Constant that can be used as the identifier for the evaluation set (test
	 * set) annotator during annotation comparisons
	 */
	public static final int EVAL_ANNOTATOR_ID = 11011011;

	/**
	 * cycle through all annotations and set the annotation set
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* create an annotation set list */
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAnnotatorID(annotatorID);
		ccpAnnotator.setAffiliation(affiliation);
		ccpAnnotator.setFirstName(firstName);
		ccpAnnotator.setLastName(lastName);

		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();

			if (!ignoreAnnotationBasedOnAnnotationSets(ccpTA)) {

				CCPAnnotator currentAnnotator = ccpTA.getAnnotator();

				if (currentAnnotator == null) {
					ccpTA.setAnnotator(ccpAnnotator);
				} else {
					try {
						if (!(annotatorIDsToIgnore.contains(currentAnnotator.getAnnotatorID()))) {
							ccpTA.setAnnotator(ccpAnnotator);
						}
					} catch (NullPointerException npe) {
						ccpTA.setAnnotator(ccpAnnotator);
					}
				}
			}
		}

	}

	/**
	 * @param ccpTA
	 * @return true if the annotation is a member of one of the annotation sets
	 *         to ignore
	 */
	private boolean ignoreAnnotationBasedOnAnnotationSets(CCPTextAnnotation ccpTA) {
		if (annotationSetIDsToIgnore != null && annotationSetIDsToIgnore.size() > 0) {
			for (int setIdToIgnore : annotationSetIDsToIgnore) {
				if (UIMA_Util.hasAnnotationSet(ccpTA, setIdToIgnore)) {
					return true;
				}
			}
		}
		return false;
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, int annotatorId,
			String firstName, String lastName, String affiliation, int[] ignoreAnnotatorIds)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotatorOverrider_AE.class, tsd, PARAM_ANNOTATOR_ID,
				annotatorId, PARAM_FIRST_NAME, firstName, PARAM_LAST_NAME, lastName, PARAM_AFFILIATION, affiliation,
				PARAM_ANNOTATOR_IDS_TO_IGNORE, ignoreAnnotatorIds);
	}

	/**
	 * Returns the description for an {@link AnalysisEngine} that will override
	 * the annotator for all annotations in the CAS and set a new annotator to
	 * be the GOLD annotator. The GOLD annotator is meant to define the gold
	 * standard set of annotations to use during an annotation comparison
	 * pipeline.
	 * 
	 * @param tsd
	 * @param ignoreIds
	 *            annotators with IDs in this array will not be overriden
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createGoldAnnotatorAnalysisEngineDescription(TypeSystemDescription tsd,
			int[] ignoreIds) throws ResourceInitializationException {
		String goldAnnotatorName = "gold annotator";
		String goldAnnotatorAffiliation = "gold";
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotatorOverrider_AE.class, tsd, PARAM_ANNOTATOR_ID,
				GOLD_ANNOTATOR_ID, PARAM_FIRST_NAME, goldAnnotatorName, PARAM_LAST_NAME, goldAnnotatorName,
				PARAM_AFFILIATION, goldAnnotatorAffiliation, PARAM_ANNOTATOR_IDS_TO_IGNORE, ignoreIds);
	}

	public static AnalysisEngineDescription createGoldAnnotatorAnalysisEngineDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return createGoldAnnotatorAnalysisEngineDescription(tsd, new int[0]);
	}

	/**
	 * Returns the description for an {@link AnalysisEngine} that will override
	 * the annotator for all annotations in the CAS and set a new annotator to
	 * be the EVAL annotator. The EVAL annototar defines the set of annotations
	 * to be evaluated in an annotation comparison pipeline. Annotators
	 * identified by the GOLD_ANNOTATOR_ID are not overriden by this AE.
	 * 
	 * @param tsd
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createEvalAnnotatorAnalysisEngineDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		String evalAnnotatorName = "eval annotator";
		String evalAnnotatorAffiliation = "eval";
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotatorOverrider_AE.class, tsd, PARAM_ANNOTATOR_ID,
				EVAL_ANNOTATOR_ID, PARAM_FIRST_NAME, evalAnnotatorName, PARAM_LAST_NAME, evalAnnotatorName,
				PARAM_AFFILIATION, evalAnnotatorAffiliation, PARAM_ANNOTATOR_IDS_TO_IGNORE,
				new int[] { GOLD_ANNOTATOR_ID });
	}

	/**
	 * for all annotations not in the gold set, the annotator is set to the eval
	 * annotator
	 * 
	 * @param tsd
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createEvalAnnotatorIgnoringGoldSetDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		String evalAnnotatorName = "eval annotator";
		String evalAnnotatorAffiliation = "eval";
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotatorOverrider_AE.class, tsd, PARAM_ANNOTATOR_ID,
				EVAL_ANNOTATOR_ID, PARAM_FIRST_NAME, evalAnnotatorName, PARAM_LAST_NAME, evalAnnotatorName,
				PARAM_AFFILIATION, evalAnnotatorAffiliation, PARAM_ANNOTATION_SET_IDS_TO_IGNORE,
				new int[] { AnnotationSetOverrider_AE.GOLD_ANNOTATION_SET_ID });
	}

}
