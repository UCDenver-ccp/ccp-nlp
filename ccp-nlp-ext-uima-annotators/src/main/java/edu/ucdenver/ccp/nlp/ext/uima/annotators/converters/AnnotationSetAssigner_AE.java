/*
 * AnnotationSetOverrider_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * A simple utility Analysis Engine that enables the user to assign an annotation set assigned to
 * <code>CCPTextAnnotations</code> stored in the CAS. Annotation sets are assigned based on annotation type. Two
 * standard annotation set names "TEST" and "GS" are used to distinguish between the test set and gold standard set for
 * the CCP Visual Debugger.
 * 
 * <br>
 * <br>
 * Note, if no types are listed in the "Annotation Types To Assign" input parameter, then the default action is to
 * assign the annotation set to all types (all annotations).
 * 
 * @author Bill Baumgartner
 * 
 */
public class AnnotationSetAssigner_AE extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(AnnotationSetAssigner_AE.class);
	public static final String PARAM_SETID = "SetID";

	public static final String PARAM_SETNAME = "SetName";

	public static final String PARAM_SETDESCRIPTION = "SetDescription";

	public static final String PARAM_ANNOTATION_TYPES_TO_ASSIGN = "AnnotationTypesToAssignRegExes";

	public static final String PARAM_ANNOTATION_SETS_TO_IGNORE = "AnnotationSetsToIgnore";

	protected Set<String> annotationTypesToAssign;

	private int setID = -1;

	private String setName = null;

	protected Set<Integer> annotationSetsToIgnore;

	private String setDescription = null;

	private boolean assignToAll = false;

	protected Set<Pattern> annotationTypePatterns;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		/* read in input parameters */
			setID = ((Integer) context.getConfigParameterValue(PARAM_SETID)).intValue();
			setName = (String) context.getConfigParameterValue(PARAM_SETNAME);
			setDescription = (String) context.getConfigParameterValue(PARAM_SETDESCRIPTION);
			try {
				annotationTypesToAssign = new HashSet<String>(Arrays.asList((String[]) context
						.getConfigParameterValue(PARAM_ANNOTATION_TYPES_TO_ASSIGN)));
			} catch (NullPointerException npe) {
				annotationTypesToAssign = new HashSet<String>();
			}

			if (annotationTypesToAssign.size() == 0) {
				assignToAll = true;
			}

			try {
				annotationSetsToIgnore = new HashSet<Integer>(Arrays.asList((Integer[]) context
						.getConfigParameterValue(PARAM_ANNOTATION_SETS_TO_IGNORE)));
			} catch (NullPointerException npe) {
				annotationSetsToIgnore = new HashSet<Integer>();
			}

			annotationTypePatterns = new HashSet<Pattern>();
			for (String annotationType : annotationTypesToAssign) {
				annotationTypePatterns.add(Pattern.compile(annotationType));
			}

		// /* convert annotation types to lower case */
		// for (String annotationType : annotationTypesToAssign) {
		// annotationType = annotationType.toLowerCase();
		// }

		super.initialize(context);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* create an annotation set list */
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(setID);
		ccpAnnotationSet.setAnnotationSetName(setName);
		ccpAnnotationSet.setAnnotationSetDescription(setDescription);

		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);

				if (!ignore) {
					if (assignToAll || annotationTypeMatchesTypesToAssign(ccpTA.getClassMention().getMentionName())) {
						// if (assignToAll
						// ||
						// annotationTypesToAssign.contains(ccpTA.getClassMention().getMentionName().toLowerCase()))
						// {
						UIMA_Util.addAnnotationSet(ccpTA, ccpAnnotationSet, jcas);
						// logger.debug("Assigned Annotation Set: "+ setID + " to ...");
						// UIMA_Util uimaUtil = new UIMA_Util();
						// logger.debug(uimaUtil.getSingleLineRepresentationOfCCPTextAnnotation(ccpTA, false));
					}
				}
			} else {
				logger.warn("CCPTextAnnotation expected but instead got " + possibleAnnot.getClass().getName());
			}
		}

	}

	private boolean annotationTypeMatchesTypesToAssign(String type) {
		for (Pattern p : annotationTypePatterns) {
			if (p.matcher(type).matches()) {
				return true;
			}
		}
		return false;
	}

	protected boolean checkForIgnoreBasedOnAnnotationSet(CCPTextAnnotation ccpTA) {
		boolean ignore = false;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		if (annotationSets != null & annotationSetsToIgnore != null) {
			for (int i = 0; i < annotationSets.size(); i++) {
				CCPAnnotationSet aSet = (CCPAnnotationSet) annotationSets.get(i);
				if (annotationSetsToIgnore.contains(aSet.getAnnotationSetID())) {
					// logger.debug("Ignore annotation set assignment. Annotation already assigned to set: " +
					// aSet.getAnnotationSetID());
					ignore = true;
					break;
				}
			}
		}
		return ignore;
	}
	
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd,
			int setId, String setName, String description,String[] assignTypes, 
			Integer[] ignoreSets) 
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				AnnotationSetAssigner_AE.class, tsd,
				 PARAM_SETID, setId,
				 PARAM_SETNAME, setName,
				 PARAM_SETDESCRIPTION, description,
				 PARAM_ANNOTATION_TYPES_TO_ASSIGN, assignTypes,
				 PARAM_ANNOTATION_SETS_TO_IGNORE, ignoreSets
		); 
	}

}
