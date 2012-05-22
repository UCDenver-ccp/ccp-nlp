/*
 * OverlappingAnnotationResolver_AE.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * Default behavior is to keep the longest annotation for any that overlap.
 * 
 * 
 * 
 * @author William A Baumgartner, Jr.
 * 
 */

/*
 * The user also has the option to keep the longest common substring among all overlapping
 * annotations. <br> <br> Known bug in longest common substring:<br> If you have three overlapping
 * annotations<br> <nbsp><nbsp>11111111<br> <nbsp><nbsp><nbsp>222222222222222222<br>
 * <nbsp><nbsp><nbsp><nbsp>333333333333333333333333<br> <br> It is possible that the code will
 * compare annotations 1 and 2 first, and result in a longest common substring of 22, and then miss
 * completely the longest common substring between 2 and 3. <br> <br> LONGEST COMMON SUBSTRING
 * FEATURE DOES NOT CURRENTLY WORK
 */
public class OverlappingAnnotationResolver_AE extends JCasAnnotator_ImplBase {

	private boolean DEBUG = false;

	public static final String PARAM_ANNOTATION_SETS_TO_IGNORE = ConfigurationParameterFactory
			.createConfigurationParameterName(OverlappingAnnotationResolver_AE.class, "annotationSetsToIgnore");

	// this may not work with Sets...
	@ConfigurationParameter
	protected Set<Integer> annotationSetsToIgnore = new HashSet<Integer>();

	// public final String PARAM_KEEP_LONGEST_COMMON_SUBSTRING = "KeepLongestCommonSubstring";

	/**
	 * The annotation type to resolve overlaps
	 */
	public static final String PARAM_ANNOTATION_TYPE_OF_INTEREST = ConfigurationParameterFactory
			.createConfigurationParameterName(OverlappingAnnotationResolver_AE.class, "annotationTypeOfInterest");

	@ConfigurationParameter(mandatory = true)
	protected String annotationTypeOfInterest;

	// protected boolean keepLongestCommonSubstring;

	// @Override
	// public void initialize(UimaContext ac) throws ResourceInitializationException {
	//
	//
	// annotationTypeOfInterest = (String)
	// ac.getConfigParameterValue(PARAM_ANNOTATION_TYPE_OF_INTEREST);
	// // keepLongestCommonSubstring = (Boolean)
	// ac.getConfigParameterValue(PARAM_KEEP_LONGEST_COMMON_SUBSTRING);
	// try {
	// annotationSetsToIgnore = new HashSet<Integer>(Arrays.asList((Integer[]) ac
	// .getConfigParameterValue(PARAM_ANNOTATION_SETS_TO_IGNORE)));
	// } catch (NullPointerException npe) {
	// annotationSetsToIgnore = new HashSet<Integer>();
	// }
	//
	//
	// super.initialize(ac);
	// }

	public static AnalysisEngineDescription getDescription(String annotationTypeOfInterest)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(OverlappingAnnotationResolver_AE.class,
				PARAM_ANNOTATION_TYPE_OF_INTEREST, annotationTypeOfInterest);
	}

	/**
	 * Default behavior is to keep the longest of any overlapping annotation
	 */
	public void process(JCas jcas) {
		/*
		 * cycle through each protein mention... if it overlaps with another protein mention, remove
		 * the shorter one?
		 */

		/* This hash maps from the annotation Id to the annotation itself */
		Map<Integer, CCPTextAnnotation> annotationIDMap = new HashMap<Integer, CCPTextAnnotation>();
		/*
		 * This hash maps from the annotation ID to a flag indicating whether or not this annotation
		 * has been flagged for removal. Annotation removal occurs at the end of the process to
		 * avoid ConcurrentModificationExceptions.
		 */
		Map<Integer, Boolean> annotationIDRemovedMap = new HashMap<Integer, Boolean>();

		int annotationID = 0;
		FSIterator<Annotation> textAnnotationIter = jcas.getJFSIndexRepository()
				.getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (textAnnotationIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) textAnnotationIter.next();

			boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);

			if (!ignore) {
				if (ccpTA.getClassMention().getMentionName().equals(annotationTypeOfInterest)) {
					ccpTA.setAnnotationID(annotationID);
					annotationIDMap.put(annotationID, ccpTA);
					annotationIDRemovedMap.put(annotationID++, false);
				}
			}
		}

		List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();

		// if (keepLongestCommonSubstring) {
		// // keepLongestCommonSubstring(annotationIDMap, annotationIDRemovedMap,
		// annotationsToRemove, jcas);
		// throw new
		// UnsupportedOperationException("LongestCommonSubstring feature does not currently work.");
		// } else {
		keepLongestAnnotation(annotationIDMap, annotationIDRemovedMap, annotationsToRemove, jcas);
		// }

		/* remove unnecessary annotations */
		for (CCPTextAnnotation ccpTAtoRemove : annotationsToRemove) {
			ccpTAtoRemove.removeFromIndexes();
		}

		/* Remove duplicates */
		annotationsToRemove = new ArrayList<CCPTextAnnotation>();
		Set<String> spans = new HashSet<String>();
		textAnnotationIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (textAnnotationIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) textAnnotationIter.next();

			boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);
			if (!ignore) {
				if (ccpTA.getClassMention().getMentionName().equals(annotationTypeOfInterest)) {
					String spanStr = ccpTA.getBegin() + ".." + ccpTA.getEnd();
					if (spans.contains(spanStr)) {
						annotationsToRemove.add(ccpTA);
						if (DEBUG) {
							System.out.println("REMOVING DUPLICATE: ");
							UIMA_Util.printCCPTextAnnotation(ccpTA, System.out);
						}
					} else {
						spans.add(spanStr);
					}
				}
			}
		}
		/* remove duplicate annotations */
		for (CCPTextAnnotation ccpTAtoRemove : annotationsToRemove) {
			ccpTAtoRemove.removeFromIndexes();
		}
	}

	/**
	 * keeps the longest annotation for a group of overlapping annotations
	 * 
	 * @param annotationIDMap
	 * @param annotationIDRemovedMap
	 * @param annotationsToRemove
	 * @param jcas
	 */
	protected void keepLongestAnnotation(Map<Integer, CCPTextAnnotation> annotationIDMap,
			Map<Integer, Boolean> annotationIDRemovedMap, List<CCPTextAnnotation> annotationsToRemove, JCas jcas) {
		Set<Integer> idSet = annotationIDMap.keySet();
		for (int id : idSet) {

			// while (textAnnotationIter.hasNext() & annotationsChecked < totalAnnotationsToCheck) {

			// annotationsChecked++;

			// CCPTextAnnotation ccpTA = null;
			// try {
			// ccpTA = (CCPTextAnnotation) textAnnotationIter.next();
			// } catch (NoSuchElementException nsee) {
			// /* If caught, then this annotation has already been removed from the CAS because it
			// was overlapping another annotation */
			// }
			CCPTextAnnotation ccpTA = annotationIDMap.get(id);
			if (ccpTA != null && !annotationIDRemovedMap.get(ccpTA.getAnnotationID())) {
				/*
				 * assign an arbitrary annotation id so we can keep track of which annotations are
				 * identical when comparing overlapping annotations
				 */
				// ccpTA.setAnnotationID(annotationsChecked);
				// System.err.println("AnnotationID: " + annotationsChecked);
				/* If it is a protein annotation, then look for overlapping annotations */
				// if (ccpTA.getClassMention().getMentionName().equals(ClassMentionTypes.PROTEIN)) {
				if (DEBUG) {
					System.err.println("CHECKING FOR OVERLAPS ON: ");
					UIMA_Util.printCCPTextAnnotation(ccpTA, System.err);
				}
				Span span;
				try {
					span = new Span(ccpTA.getBegin(), ccpTA.getEnd());
				} catch (InvalidSpanException e) {
					e.printStackTrace();
					span = null;
				}
				int longestAnnotation = ccpTA.getEnd() - ccpTA.getBegin();
				Iterator<CCPTextAnnotation> overlappingAnnotationsIter = UIMA_Util.getAnnotationsEncompassingSpan(span,
						jcas);
				debug(">0 overlapping annotations: " + overlappingAnnotationsIter.hasNext());
				/* Cycle through overlapping annotations, and remove if shorter than the original */

				while (overlappingAnnotationsIter.hasNext()) {
					CCPTextAnnotation overlappingAnnotation = overlappingAnnotationsIter.next();
					if (DEBUG) {
						debug("Overlapping annotation: ");
						UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
					}

					/* make sure the overlapping annotation is a protein */
					if (overlappingAnnotation.getClassMention().getMentionName().equals(annotationTypeOfInterest)) {

						/* make sure we aren't supposed to ignore this annotation */
						boolean ignore = checkForIgnoreBasedOnAnnotationSet(overlappingAnnotation);
						if (!ignore) {
							/* make sure these are not the exact same annotation */
							if (ccpTA.getAnnotationID() != overlappingAnnotation.getAnnotationID()) {
								int overlappingAnnotationLength = overlappingAnnotation.getEnd()
										- overlappingAnnotation.getBegin();
								if (overlappingAnnotationLength <= longestAnnotation) {
									if (DEBUG) {
										System.err
												.println("THIS ANNOTATION IS SHORTER THAN THE LONGEST, SO WE WILL REMOVE IT: ");
										UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
									}
									/* remove this annotation from the CAS */
									annotationsToRemove.add(overlappingAnnotation);
									annotationIDRemovedMap.remove(overlappingAnnotation.getAnnotationID());
									annotationIDRemovedMap.put(overlappingAnnotation.getAnnotationID(), true);
								} else {
									if (DEBUG) {
										System.err
												.println("THIS ANNOTATION IS LONGER THAN THE PREVIOUS LONGEST, SO WE WILL KEEP IT: ");
										UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
										System.err.println("AND THEN WE WILL REMOVE: ");
										UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
									}
									/* keep this annotaition, and remove the previous longest */
									annotationsToRemove.add(ccpTA);
									annotationIDRemovedMap.remove(ccpTA.getAnnotationID());
									annotationIDRemovedMap.put(ccpTA.getAnnotationID(), true);
									longestAnnotation = overlappingAnnotationLength;
								}
							}
						}
					}
				}
			}
		}
	}

	// /**
	// * keeps the longest common substring for a group of overlapping annotations
	// *
	// * @param annotationIDMap
	// * @param annotationIDRemovedMap
	// * @param annotationsToRemove
	// * @param jcas
	// */
	// protected void keepLongestCommonSubstring(Map<Integer, CCPTextAnnotation> annotationIDMap,
	// Map<Integer, Boolean> annotationIDRemovedMap, List<CCPTextAnnotation> annotationsToRemove,
	// JCas jcas) {
	// Set<Integer> idSet = annotationIDMap.keySet();
	// for (int id : idSet) {
	// CCPTextAnnotation ccpTA = annotationIDMap.get(id);
	// if (ccpTA != null & !annotationIDRemovedMap.get(ccpTA.getAnnotationID())) {
	// if (DEBUG) {
	// System.err.println("CHECKING FOR OVERLAPS ON: ");
	// UIMA_Util.printCCPTextAnnotation(ccpTA, System.err);
	// }
	// Span span;
	// try {
	// span = new Span(ccpTA.getBegin(), ccpTA.getEnd());
	// } catch (InvalidSpanException e) {
	// e.printStackTrace();
	// span = null;
	// }
	// // int longestAnnotation = ccpTA.getEnd() - ccpTA.getBegin();
	// Iterator<CCPTextAnnotation> overlappingAnnotationsIter =
	// UIMA_Util.getAnnotationsEncompassingSpan(span, jcas);
	// debug(">0 overlapping annotations: " + overlappingAnnotationsIter.hasNext());
	// /* Cycle through overlapping annotations, and remove if shorter than the original */
	//
	// while (overlappingAnnotationsIter.hasNext()) {
	// CCPTextAnnotation overlappingAnnotation = overlappingAnnotationsIter.next();
	// if (DEBUG) {
	// debug("Overlapping annotation: ");
	// UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
	// }
	//
	// /* make sure the overlapping annotation is a protein */
	// if
	// (overlappingAnnotation.getClassMention().getMentionName().equals(annotationTypeOfInterest)) {
	//
	// /* make sure we aren't supposed to ignore this annotation */
	// boolean ignore = checkForIgnoreBasedOnAnnotationSet(overlappingAnnotation);
	// if (!ignore) {
	// /* make sure these are not the exact same annotation */
	// if (ccpTA.getAnnotationID() != overlappingAnnotation.getAnnotationID()) {
	// /* now, alter the span of one annotation to equal the longest common substring, and then
	// remove the other */
	// int newBegin = ccpTA.getBegin();
	// int newEnd = ccpTA.getEnd();
	// if (ccpTA.getBegin() < overlappingAnnotation.getBegin()) {
	// newBegin = overlappingAnnotation.getBegin();
	// }
	//
	// /* check to see if new substring annotation is valid */
	// if (newBegin >= newEnd | newBegin < 0 | newEnd < 1) {
	// System.err.println("INVALID SPAN!!!!!!!!!!!!!!!!!!!!!! [" + newBegin + ".." + newEnd + "]");
	// UIMA_Util.printCCPTextAnnotation(ccpTA, System.err);
	// UIMA_Util.printCCPTextAnnotation(overlappingAnnotation, System.err);
	// }
	//
	// if (ccpTA.getEnd() > overlappingAnnotation.getEnd()) {
	// newEnd = overlappingAnnotation.getEnd();
	// }
	// try {
	// UIMA_Util.setCCPTextAnnotationSpan(ccpTA, newBegin, newEnd);
	// } catch (CASException e) {
	// e.printStackTrace();
	// }
	//
	// /* remove this annotation from the CAS */
	// annotationsToRemove.add(overlappingAnnotation);
	// annotationIDRemovedMap.remove(overlappingAnnotation.getAnnotationID());
	// annotationIDRemovedMap.put(overlappingAnnotation.getAnnotationID(), true);
	// }
	// }
	// }
	// }
	// }
	// }
	// }

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

	private void debug(String message) {
		if (DEBUG) {
			System.err.println("DEBUG -- OverlappingProteinResolver: " + message);
		}
	}

}
