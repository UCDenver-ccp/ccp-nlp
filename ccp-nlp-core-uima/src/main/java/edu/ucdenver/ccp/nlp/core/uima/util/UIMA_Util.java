/*
 * UIMA_Util.java
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

package edu.ucdenver.ccp.nlp.core.uima.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIntConstraint;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.LongArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.collections.LegacyCollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.document.DocumentSection;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentSection;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.EvaluationResultProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalseNegativeProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalsePositiveProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPBooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.impl.CCPPrimitiveSlotMentionFactory;
import edu.ucdenver.ccp.nlp.core.uima.mention.impl.WrappedCCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.impl.WrappedCCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.impl.WrappedCCPStringSlotMention;

/**
 * This is a utility class meant to streamline interaction with the <code>CCPTextAnnotation</code>
 * class.
 * 
 * @author Bill Baumgartner
 * 
 */
public class UIMA_Util {

	private static Logger logger = Logger.getLogger(UIMA_Util.class);

	/**
	 * Returns the documentID from the CCPDocumentInformation annotation if there is one. Returns
	 * "-1" otherwise.
	 * 
	 * @param jcas
	 * @return
	 */
	public static String getDocumentID(JCas jcas) {
		String documentID;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
			CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
			documentID = docInfo.getDocumentID();
			return documentID;
		} else {
			logger.warn("No document ID found, returning -1.");
			return "-1";
		}
	}
	
	/**
	 * Retrieves the document encoding for the document text stored in the JCas
	 * 
	 * @param jCas
	 * @return
	 * @throws IllegalStateException
	 *             if the encoding has not been set
	 */
	public static CharacterEncoding getDocumentEncoding(JCas jCas) {
		String encoding = getCcpDocumentInformation(jCas).getEncoding();
		if (encoding == null)
			throw new IllegalStateException(
					"The encoding field has not been set in the CCPDocumentInformation instance. " +
					"The most likely reason for this is the collection reader implementation you are " +
					"using did not set the encoding value. Use the FileSystemCollectionReader or " +
					"adjust your collection reader accordingly.");
		return CharacterEncoding.valueOf(encoding);
	}
	
	/**
	 * Sets the encoding field for the meta data. This encoding should correspond to the encoding used by the document text stored in the JCas.
	 * @param jCas
	 * @param encoding
	 */
	public static void setDocumentEncoding(JCas jCas, CharacterEncoding encoding) {
		getCcpDocumentInformation(jCas).setEncoding(encoding.name());
	}
	
	
	

//	/**
//	 * 
//	 * Sets the CCPDocumentInformation documentID field
//	 * 
//	 * @param jcas
//	 * @param documentID
//	 */
//	public static void setDocumentID(JCas jcas, String documentID) {
//		CCPDocumentInformation docInfo;
//		FSIterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
//		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
//			docInfo = (CCPDocumentInformation) it.next();
//		} else {
//			docInfo = new CCPDocumentInformation(jcas);
//			docInfo.addToIndexes();
//		}
//		docInfo.setDocumentID(documentID);
//	}

	/**
	 * Returns the documentID from the CCPDocumentInformation annotation if there is one. Returns
	 * "-1" otherwise.
	 * 
	 * @param jcas
	 * @return
	 */
	public static int getDocumentCollectionID(JCas jcas) {
		int documentCollectionID;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
			CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
			documentCollectionID = docInfo.getDocumentCollectionID();
			return documentCollectionID;
		} else {
			logger.warn("No document collection ID found, returning -1.");
			return -1;
		}
	}

	
	/**
	 * Returns an Iterator over CCPTextAnnotations that are in the CAS.
	 * 
	 * @param jcas
	 * @param classType
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getTextAnnotationIterator(JCas jcas) {
		return getTextAnnotationIterator(jcas, (String[]) null);
	}

	// /**
	// * Not tested.. trying to create a method that will return a generic paramertized iterator
	// (wihtout any casting warnings)
	// * @param <T>
	// * @param jcas
	// * @param T
	// * @return
	// */
	// public static <T extends FeatureStructure> Iterator<T> getFeatureStructureIterator(JCas jcas,
	// Type T) {
	// final FSIterator annotIter = jcas.getJFSIndexRepository().getAllIndexedFS(T);
	//		
	// return new Iterator<T>() {
	// private T nextFS = null;
	//			
	// @Override
	// public boolean hasNext() {
	// if (nextFS == null) {
	// if (annotIter.hasNext()) {
	// nextFS = (T) annotIter.next();
	// return true;
	// }
	// return false;
	// }
	// return true;
	// }
	//
	// @Override
	// public T next() {
	// if (!hasNext()) {
	// throw new NoSuchElementException();
	// }
	//
	// T fsToReturn = nextFS;
	// nextFS = null;
	// return fsToReturn;
	// }
	//
	// @Override
	// public void remove() {
	// annotIter.remove();
	// }
	//			
	// private CCPTextAnnotation validateObjectType(Object possibleT) {
	// if (possibleT instanceof ) {
	// return (CCPTextAnnotation) possibleAnnot;
	// } else {
	// logger.warn("Expected CCPTextAnnotation in FSIterator but observed a "
	// + possibleAnnot.getClass().getName());
	// return null;
	// }
	// }
	// };
	// }

	/**
	 * Returns an Iterator over CCPTextAnnotations that have a give class type (class mention name).
	 * 
	 * @param jcas
	 * @param classType
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getTextAnnotationIterator(JCas jcas, final String... classTypes) {
		Set<String> tempClassTypesSet;
		if (classTypes == null)
			tempClassTypesSet = new HashSet<String>();
		else
			tempClassTypesSet = CollectionsUtil.array2Set(classTypes);

		final FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		final Set<String> classTypesSet = tempClassTypesSet;

		return new Iterator<CCPTextAnnotation>() {
			private CCPTextAnnotation nextAnnot = null;

			public boolean hasNext() {
				if (nextAnnot == null) {
					if (annotIter.hasNext()) {
						CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
						if (checkForCorrectClassType(ccpTA)) {
							nextAnnot = ccpTA;
						} else {
							return hasNext();
						}
					} else {
						return false;
					}
				}
				return true;
			}

			public CCPTextAnnotation next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				CCPTextAnnotation annotToReturn = nextAnnot;
				nextAnnot = null;
				return annotToReturn;
			}

			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}

			/**
			 * If the input CCPTextAnnotation is not null, and the classType is not null, then this
			 * method returns true if the classType matches the class mention name for the
			 * CCPTextAnnotation. If classType is null, then this method returns true as long as the
			 * input CCPTextAnnotation is not null.
			 * 
			 * @param ccpTA
			 * @return
			 */
			private boolean checkForCorrectClassType(CCPTextAnnotation ccpTA) {
//				System.err.println("mention type = " + ccpTA.getClassMention().getMentionName()
//						+ "  -- classTypes == null: " + (classTypes == null) + " classtypes: " + Arrays.toString(classTypes));
				if (classTypes == null || (classTypesSet.contains(ccpTA.getClassMention().getMentionName()))) {
					return true;
				}
				return false;
			}
		};
	}

	// /**
	// * Sets all mention IDs in a CAS to unique values
	// *
	// * @param jcas
	// */
	// private static void resetMentionIDs(JCas jcas) {
	// long mentionID = System.currentTimeMillis();
	// FSIterator annotIter =
	// jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
	// while (annotIter.hasNext()) {
	// Object possibleAnnot = annotIter.next();
	// if (possibleAnnot instanceof CCPTextAnnotation) {
	// ((CCPTextAnnotation) possibleAnnot).getClassMention().setMentionID(mentionID++);
	// }
	// }
	// }

	public static List<TextAnnotation> getAnnotationsFromCas(JCas jcas) {
		return getAnnotationsFromCas(jcas, (String[]) null);
	}

	// /**
	// * Returns a List<TextAnnotation> containing TextAnnotation (Util-version) objects for all
	// CCPTextAnnotations in
	// the
	// * CAS for the given classType (class mention name). If classType is null, then all
	// TextAnnotations are returned.
	// *
	// * @param jcas
	// * @param classType
	// * @return
	// * @throws Exception
	// */
	// public static List<TextAnnotation> getAnnotationsFromCas(JCas jcas, String classType) throws
	// Exception {
	// logger.debug("Retrieving annotations from the CAS...");
	// resetMentionIDs(jcas);
	// List<TextAnnotation> textAnnotationsToReturn = new ArrayList<TextAnnotation>();
	// // Map<Long, List<Long>> mentionID2AncestorIDsMap = new HashMap<Long, List<Long>>();
	//
	// Map<Long, DefaultClassMention> mentionID2ClassMentionMap = new HashMap<Long,
	// DefaultClassMention>();
	// Iterator<CCPTextAnnotation> annotIter = getTextAnnotationIterator(jcas, classType);
	// while (annotIter.hasNext()) {
	// Set<Long> ancestorMentionIDs = new HashSet<Long>();
	// TextAnnotation ta = extractUtilTextAnnotationFromCas(annotIter.next(), ancestorMentionIDs,
	// mentionID2ClassMentionMap, null,
	// jcas);
	// textAnnotationsToReturn.add(ta);
	// }
	// return textAnnotationsToReturn;
	// }

	public static List<TextAnnotation> getAnnotationsFromCas(JCas jcas, String... classTypes) {
		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();
		Iterator<CCPTextAnnotation> annotIter = getTextAnnotationIterator(jcas, classTypes);
		while (annotIter.hasNext()) {
			annotationsToReturn.add(new WrappedCCPTextAnnotation(annotIter.next()));
		}
		System.err.println(String.format("Returning %d annotations from the JCAS", annotationsToReturn.size()));
		logger.info(String.format("Returning %d annotations from the JCAS", annotationsToReturn.size()));
		return annotationsToReturn;
	}

	// /**
	// * Returns a TextAnnotation (Util-version) object representing the given CCPTextAnnotation
	// object
	// *
	// * @param ccpTA
	// * @param mentionID2AncestorIDsMap
	// * @param mentionID2ClassMentionMap
	// * @param cm
	// * @param jcas
	// * @return
	// * @throws Exception
	// */
	// private static TextAnnotation extractUtilTextAnnotationFromCas(CCPTextAnnotation ccpTA,
	// Set<Long>
	// ancestorMentionIDs,
	// Map<Long, DefaultClassMention> mentionID2ClassMentionMap, DefaultClassMention cm, JCas jcas)
	// throws Exception {
	// logger.debug("Extracting TA [" + ccpTA.getBegin() + ".." + ccpTA.getEnd() + "] '" +
	// ccpTA.getCoveredText() +
	// "' ("
	// + ccpTA.getClassMention().getMentionName() + ") MentionID=" +
	// ccpTA.getClassMention().getMentionID());
	// long mentionID = ccpTA.getClassMention().getMentionID();
	// if (mentionID2ClassMentionMap.containsKey(mentionID)) {
	// logger.debug("TA already exists because Mention already exists. Returning TA");
	// /* This text annotation/class mention combination has already been extracted from the CAS, so
	// return it. */
	// return mentionID2ClassMentionMap.get(mentionID).getTextAnnotation();
	// } else {
	// logger.debug("TA does not exist yet. Creating new TA.");
	// DefaultTextAnnotation ta = new DefaultTextAnnotation();
	// swapAnnotationInfo(ccpTA, ta, jcas);
	// if (cm == null) {
	// cm = (DefaultClassMention) extractUtilMentionFromCas(ccpTA.getClassMention(),
	// ancestorMentionIDs,
	// mentionID2ClassMentionMap, jcas);
	// }
	// cm.setTextAnnotation(ta);
	// ta.setClassMention(cm);
	// return ta;
	// }
	//
	// }
	//
	// /**
	// * Returns the appropriate Mention (Util-version) object representation of the input
	// CCPMention
	// *
	// * @param ccpMention
	// * @param mentionID2AncestorIDsMap
	// * @param mentionID2ClassMentionMap
	// * @param jcas
	// * @return
	// * @throws Exception
	// */
	// private static Mention extractUtilMentionFromCas(CCPMention ccpMention, Set<Long>
	// ancestorMentionIDs,
	// Map<Long, DefaultClassMention> mentionID2ClassMentionMap, JCas jcas) throws Exception {
	// Mention returnMention = null;
	// if (ccpMention instanceof CCPClassMention) {
	// returnMention = extractUtilClassMentionFromCas((CCPClassMention) ccpMention,
	// ancestorMentionIDs,
	// mentionID2ClassMentionMap,
	// jcas);
	// } else if (ccpMention instanceof CCPComplexSlotMention) {
	// returnMention = extractUtilComplexSlotMentionFromCas((CCPComplexSlotMention) ccpMention,
	// ancestorMentionIDs,
	// mentionID2ClassMentionMap, jcas);
	// } else if (ccpMention instanceof CCPNonComplexSlotMention) {
	// returnMention = extractUtilPrimitiveSlotMentionFromCas((CCPNonComplexSlotMention) ccpMention,
	// jcas);
	// } else {
	// logErrorWhileExtractingUtilMentionFromCas(ccpMention);
	// }
	// return returnMention;
	// }
	//
	// /**
	// * Returns a ClassMention (Util-version) object for the input CCPClassMention object TODO:
	// check for cycles
	// *
	// * @param ccpCM
	// * @param mentionID2AncestorIDsMap
	// * @param mentionID2ClassMentionMap
	// * @param jcas
	// * @return
	// * @throws Exception
	// */
	// private static Mention extractUtilClassMentionFromCas(CCPClassMention ccpCM, Set<Long>
	// ancestorMentionIDs,
	// Map<Long, DefaultClassMention> mentionID2ClassMentionMap, JCas jcas) throws Exception {
	// logger.debug("Extracting CM (" + ccpCM.getMentionName() + ") MentionID=" +
	// ccpCM.getMentionID() + " AncestorIDs="
	// + ancestorMentionIDs.toString());
	// long mentionID = ccpCM.getMentionID();
	// if (mentionID2ClassMentionMap.containsKey(mentionID)) {
	// logger.debug("CM already exists. Returning CM. MentionID=" + mentionID);
	// return mentionID2ClassMentionMap.get(mentionID);
	// }
	//
	// /* check for mention hierarchy cycle here - return null if */
	// if (classMentionCycleDetected(mentionID, ancestorMentionIDs)) {
	// logger.debug("CYCLE DETECTED!! Returning null!");
	// return null;
	// }
	//
	// logger.debug("Creating new CM (" + ccpCM.getMentionName() + ") MentionID=" + mentionID);
	// DefaultClassMention returnCM = new DefaultClassMention(ccpCM.getMentionName());
	// returnCM.setMentionID(mentionID);
	//
	// Set<Long> updatedAncestorMentionIDs = cloneSet(ancestorMentionIDs);
	// updatedAncestorMentionIDs.add(mentionID);
	//
	// FSArray ccpSlotMentionArray = ccpCM.getSlotMentions();
	// if (ccpSlotMentionArray != null) {
	// for (int i = 0; i < ccpSlotMentionArray.size(); i++) {
	// CCPSlotMention ccpSlotMention = (CCPSlotMention) ccpSlotMentionArray.get(i);
	// Mention mention = extractUtilMentionFromCas(ccpSlotMention, updatedAncestorMentionIDs,
	// mentionID2ClassMentionMap,
	// jcas);
	// if (mention instanceof DefaultComplexSlotMention) {
	// DefaultComplexSlotMention csm = (DefaultComplexSlotMention) mention;
	// if (csm.getClassMentions().size() > 0) {
	// /* Empty CSM are common when a cycle in the mention hierarchy is detected. */
	// returnCM.addComplexSlotMention(csm);
	// }
	// } else if (mention instanceof PrimitiveSlotMention) {
	// returnCM.addPrimitiveSlotMention((PrimitiveSlotMention<?>) mention);
	// }
	// }
	// }
	//
	// CCPTextAnnotation associatedTextAnnotation = ccpCM.getCcpTextAnnotation();
	// TextAnnotation ta = extractUtilTextAnnotationFromCas(associatedTextAnnotation,
	// updatedAncestorMentionIDs,
	// mentionID2ClassMentionMap, returnCM, jcas);
	// returnCM.setTextAnnotation(ta);
	//
	// mentionID2ClassMentionMap.put(mentionID, returnCM);
	//
	// return returnCM;
	//
	// }
	//
	// private static <E extends Object> Set<E> cloneSet(Set<E> inputSet) {
	// Set<E> clone = new HashSet<E>(inputSet);
	// return clone;
	// }
	//
	// /**
	// * Returns true if the input mention ID has itself as an ancestor. This means there is a cycle
	// in the mention
	// * hierarchy.
	// *
	// * @param mentionID
	// * @param mentionID2AncestorIDsMap
	// * @return
	// */
	// private static boolean classMentionCycleDetected(long mentionID, Set<Long>
	// ancestorMentionIDs) {
	// if (ancestorMentionIDs.contains(mentionID)) {
	// return true;
	// }
	// return false;
	// }
	//
	// /**
	// * Returns a ComplexSlotMention object (Util-version) corresponding to the input
	// CCPComplexSlotMention object
	// *
	// * @param ccpCSM
	// * @param mentionID2AncestorIDsMap
	// * @param mentionID2ClassMentionMap
	// * @param jcas
	// * @return
	// * @throws Exception
	// */
	// private static Mention extractUtilComplexSlotMentionFromCas(CCPComplexSlotMention ccpCSM,
	// Set<Long>
	// ancestorMentionIDs,
	// Map<Long, DefaultClassMention> mentionID2ClassMentionMap, JCas jcas) throws Exception {
	// logger.debug("Extracting CSM (" + ccpCSM.getMentionName() + ")");
	// DefaultComplexSlotMention returnCSM = new DefaultComplexSlotMention(ccpCSM.getMentionName());
	// for (int i = 0; i < ccpCSM.getClassMentions().size(); i++) {
	// CCPClassMention ccpClassMention = (CCPClassMention) ccpCSM.getClassMentions().get(i);
	// DefaultClassMention cm = (DefaultClassMention) extractUtilMentionFromCas(ccpClassMention,
	// ancestorMentionIDs,
	// mentionID2ClassMentionMap, jcas);
	// if (cm != null) {
	// returnCSM.addClassMention(cm);
	// }
	// }
	// return returnCSM;
	// }

	// /**
	// * Updates the mentionID2AncestorIDsMap with the given input.
	// *
	// * @param parentMentionID
	// * @param mentionID
	// * @param mentionID2AncestorIDsMap
	// */
	// private static void updateAncestorIDMap(long parentMentionID, long mentionID, Map<Long,
	// List<Long>>
	// mentionID2AncestorIDsMap) {
	// if (mentionID2AncestorIDsMap.containsKey(mentionID)) {
	// mentionID2AncestorIDsMap.get(mentionID).add(parentMentionID);
	// } else {
	// List<Long> parentMentionIDs = new ArrayList<Long>();
	// parentMentionIDs.add(parentMentionID);
	// mentionID2AncestorIDsMap.put(mentionID, parentMentionIDs);
	// }
	// logger.debug("Updated AncestorID Map: " + mentionID2AncestorIDsMap.toString());
	// }

	// /**
	// * Returns a PrimitiveSlotMention object (Util-version) corresponding to the input
	// CCPNonComplexSlotMention
	// object.
	// *
	// * @param ccpMention
	// * @param jcas
	// * @return
	// * @throws InvalidInputException
	// */
	// private static Mention extractUtilPrimitiveSlotMentionFromCas(CCPNonComplexSlotMention
	// ccpMention, JCas jcas)
	// throws InvalidInputException {
	// logger.debug("Extracting PrimitiveSM (" + ccpMention.getMentionName() + ")");
	// CCPNonComplexSlotMention ccpNonComplexSlotMention = (CCPNonComplexSlotMention) ccpMention;
	// PrimitiveSlotMention<?> returnSM = null;
	//
	// if (ccpNonComplexSlotMention.getSlotValues() != null) {
	// Collection<String> slotValues =
	// Arrays.asList(ccpNonComplexSlotMention.getSlotValues().toArray());
	// returnSM =
	// DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionWithCollection(ccpNonComplexSlotMention
	// .getMentionName(), slotValues);
	// }
	// return returnSM;
	// }
	//
	// /**
	// * Logs an appropriate error message depending on whether or not the input CCPMention is null.
	// *
	// * @param ccpMention
	// */
	// private static void logErrorWhileExtractingUtilMentionFromCas(CCPMention ccpMention) {
	// if (ccpMention == null) {
	// logger.error("A null mention has been found. Please make sure each CCPTextAnnotation is associated with a CCPClassMention.");
	// } else {
	// logger.error("The mention you are trying to create is an instance of: " +
	// ccpMention.getClass().getName());
	// logger.error("Currently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
	// }
	// }

/**
	 * 
	 * Sets the CCPDocumentInformation documentID field
	 * 
	 * @param jcas
	 * @param documentID
	 */
	public static void setDocumentID(JCas jcas, String documentID) {
		CCPDocumentInformation docInfo = getCcpDocumentInformation(jcas);
		docInfo.setDocumentID(documentID);
	}
	
	public static void setDocumentCollectionID(JCas jcas, int documentCollectionID) {
		CCPDocumentInformation docInfo = getCcpDocumentInformation(jcas);
		docInfo.setDocumentCollectionID(documentCollectionID);
	}

	private static CCPDocumentInformation getCcpDocumentInformation(JCas jcas) {
		CCPDocumentInformation docInfo;
		FSIterator it = jcas.getJFSIndexRepository().getAllIndexedFS(CCPDocumentInformation.type);
		if (it.hasNext()) { /* there should be at most one CCPDocumentInformation annotation */
			docInfo = (CCPDocumentInformation) it.next();
		} else {
			docInfo = new CCPDocumentInformation(jcas);
			docInfo.addToIndexes();
		}
		return docInfo;
	}

	public static void swapDocumentInfo(JCas fromJcas, GenericDocument toGD) {
		FSIterator docInfoIter = fromJcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type)
				.iterator();
		FSIterator docSectionIter = fromJcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentSection.type)
				.iterator();

		// print document information
		String docID = "-1";
		int docCollectionID = -1;
		if (docInfoIter.hasNext()) {
			CCPDocumentInformation docInfo = (CCPDocumentInformation) docInfoIter.next();
			docID = docInfo.getDocumentID();
			docCollectionID = docInfo.getDocumentCollectionID();
			/* Get any secondary document IDs */
			StringArray secondaryIDsArray = docInfo.getSecondaryDocumentIDs();
			if (secondaryIDsArray != null) {
				for (int i = 0; i < secondaryIDsArray.size(); i++) {
					toGD.addSecondaryDocumentID(secondaryIDsArray.get(i));
				}
			}
		}

		toGD.setDocumentID(docID);
		toGD.setDocumentCollectionID(docCollectionID);

		// add the document sections to the generic document
		while (docSectionIter.hasNext()) {
			CCPDocumentSection ccpDocSection = (CCPDocumentSection) docSectionIter.next();
			DocumentSection docSection = new DocumentSection();
			docSection.setDocumentSectionID(ccpDocSection.getSectionID());
			docSection.setSectionStartIndex(ccpDocSection.getBegin());
			docSection.setSectionEndIndex(ccpDocSection.getEnd());
			toGD.addDocumentSection(docSection);
		}

		// set the document text
		toGD.setDocumentText(fromJcas.getDocumentText());

		// add the annotations to the Generic Document
		// UIMA_Util uimaUtil = new UIMA_Util();
		List<TextAnnotation> annotations = getAnnotationsFromCas(fromJcas);
		toGD.setAnnotations(annotations);
	}

	/**
	 * This method transfers general annotation info, i.e. span, annotator, etc. from a
	 * TextAnnotation to a CCPTextAnnotation.
	 * 
	 * @param ta
	 * @param ccpAnnotation
	 */
	public static void swapAnnotationInfo(TextAnnotation fromTA, CCPTextAnnotation toUIMA, JCas jcas) {
		// set the Annotation ID
		toUIMA.setAnnotationID(fromTA.getAnnotationID());

		// validate the Annotation Set ID(s) against the known annotation sets

		// set the Annotation Sets
		Set<AnnotationSet> annotationSets = fromTA.getAnnotationSets();
		FSArray ccpAnnotationSets = new FSArray(jcas, annotationSets.size());
		int index = 0;
		for (AnnotationSet aSet : annotationSets) {
			CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
			UIMA_Util.swapAnnotationSetInfo(aSet, ccpAnnotationSet);
			ccpAnnotationSets.set(index++, ccpAnnotationSet);
		}
		toUIMA.setAnnotationSets(ccpAnnotationSets);
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		UIMA_Util.swapAnnotatorInfo(fromTA.getAnnotator(), ccpAnnotator);
		toUIMA.setAnnotator(ccpAnnotator);

		/* Swap metadata info */
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata = fromTA
				.getAnnotationMetadata();
		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = new edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata(
				jcas);
		UIMA_Util.swapAnnotationMetadata(annotationMetadata, ccpAnnotationMetadata, jcas);

		/*
		 * set the Span(s) A CCPSpan object is created for each Span object associated with the
		 * TextAnnotation. Also, the default Begin and End fields for the UIMA annotation are set to
		 * the min and max indexes of all Span objects.
		 */
		int minSpanIndex = Integer.MAX_VALUE;
		int maxSpanIndex = Integer.MIN_VALUE;
		ArrayList<Span> spans = (ArrayList<Span>) fromTA.getSpans();

		toUIMA.setNumberOfSpans(spans.size());
		FSArray supplementarySpans = new FSArray(jcas, spans.size());
		for (int i = 0; i < spans.size(); i++) {
			Span span = spans.get(i);
			if (minSpanIndex > span.getSpanStart()) {
				minSpanIndex = span.getSpanStart();
			}
			if (maxSpanIndex < span.getSpanEnd()) {
				maxSpanIndex = span.getSpanEnd();
			}

			CCPSpan uimaSpan = new CCPSpan(jcas);
			uimaSpan.setSpanStart(span.getSpanStart());
			uimaSpan.setSpanEnd(span.getSpanEnd());
			supplementarySpans.set(i, uimaSpan);
		}
		toUIMA.setSpans(supplementarySpans);

		if (minSpanIndex == Integer.MAX_VALUE) {
			minSpanIndex = 0;
			maxSpanIndex = 0;
		}
		toUIMA.setBegin(minSpanIndex);
		toUIMA.setEnd(maxSpanIndex);

		if (supplementarySpans.size() == 0) {
			toUIMA.setBegin(0);
			toUIMA.setEnd(0);
		}

		// set the DocumentSection ID
		toUIMA.setDocumentSectionID(fromTA.getDocumentSectionID());
	}

	/**
	 * This method transfers general annotation info, i.e. span, annotator, etc. from a
	 * CCPTextAnnotation to a TextAnnotation
	 * 
	 * @param ccpAnnotation
	 * @param ta
	 */
	public static void swapAnnotationInfo(CCPTextAnnotation fromUIMA, TextAnnotation toTA, JCas jcas) {
		// set the Annotation ID
		toTA.setAnnotationID(fromUIMA.getAnnotationID());

		// set the Annotation Sets

		FSArray ccpAnnotationSets = fromUIMA.getAnnotationSets();
		Set<AnnotationSet> annotationSets = new HashSet<AnnotationSet>();
		if (ccpAnnotationSets != null) {
			for (int i = 0; i < ccpAnnotationSets.size(); i++) {
				AnnotationSet annotationSet = new AnnotationSet(new Integer(-1), "", "");
				UIMA_Util.swapAnnotationSetInfo((CCPAnnotationSet) ccpAnnotationSets.get(i), annotationSet);
				annotationSets.add(annotationSet);
			}
		}
		toTA.setAnnotationSets(annotationSets);

		// set the Annotator ID
		CCPAnnotator ccpAnnotator = fromUIMA.getAnnotator();
		Annotator annotator = new Annotator(new Integer(-1), "", "", "");
		UIMA_Util.swapAnnotatorInfo(ccpAnnotator, annotator);
		toTA.setAnnotator(annotator);

		/* swap metadata */
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata();
		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = fromUIMA
				.getAnnotationMetadata();
		UIMA_Util.swapAnnotationMetadata(ccpAnnotationMetadata, annotationMetadata, jcas);
		toTA.setAnnotationMetadata(annotationMetadata);

		// set the Span(s)
		List<Span> spans = new ArrayList<Span>();
		FSArray ccpSpans = fromUIMA.getSpans();
		/*
		 * if there are no explicit spans in the CCPTextAnnotation, then we need to create a default
		 * span using the start/end indexes
		 */
		try {
			if (ccpSpans.size() == 0) {
				Span span = new Span(fromUIMA.getStart(), fromUIMA.getEnd());
				spans.add(span);
			} else {
				for (int i = 0; i < ccpSpans.size(); i++) {// FeatureStructure fs : fsArray) {
					CCPSpan ccpSpan = (CCPSpan) ccpSpans.get(i);
					Span span = new Span(ccpSpan.getSpanStart(), ccpSpan.getSpanEnd());
					spans.add(span);
				}
			}
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}

		toTA.setSpans(spans);

		// set the DocumentSection ID
		toTA.setDocumentSectionID(fromUIMA.getDocumentSectionID());

		// set the Document ID and Document Collection ID
		String docID = getDocumentID(jcas);
		int docCollectionID = getDocumentCollectionID(jcas);
		toTA.setDocumentID(docID);
		toTA.setDocumentCollectionID(docCollectionID);

		// set the Covered Text
		try {
			toTA.setCoveredText(fromUIMA.getCoveredText());
		} catch (Exception e) {
			System.err.println("EXCEPTION: " + jcas.getDocumentText());
		}
	}

	/**
	 * Swap from UIMA Annotation to another UIMA Annotation
	 * 
	 * @param fromUIMA
	 * @param toUIMA
	 * @param jcas
	 */
	public static void swapAnnotationInfo(CCPTextAnnotation fromUIMA, CCPTextAnnotation toUIMA) throws CASException {
		// set the Annotation ID
		toUIMA.setAnnotationID(fromUIMA.getAnnotationID());

		// set the Annotation Sets
		toUIMA.setAnnotationSets(fromUIMA.getAnnotationSets());

		// set the Annotator ID
		toUIMA.setAnnotator(fromUIMA.getAnnotator());

		/* set the annotation metadata */
		toUIMA.setAnnotationMetadata(fromUIMA.getAnnotationMetadata());

		// set the Span(s)
		toUIMA.setSpans(fromUIMA.getSpans());

		/* set the number of spans */
		toUIMA.setNumberOfSpans(fromUIMA.getNumberOfSpans());

		// set the default Begin and End fields
		toUIMA.setBegin(fromUIMA.getBegin());
		toUIMA.setEnd(fromUIMA.getEnd());

		// set the DocumentSection ID
		toUIMA.setDocumentSectionID(fromUIMA.getDocumentSectionID());

		/* swap class mentions */
		CCPClassMention ccpCM = new CCPClassMention(fromUIMA.getCAS().getJCas());
		UIMA_Util.swapClassMentionInfo(fromUIMA.getClassMention(), ccpCM);
		toUIMA.setClassMention(ccpCM);
	}

	/**
	 * Clones a CCPTextAnnotation
	 * 
	 * @param ccpTA
	 * @param jcas
	 * @return
	 * @throws CASException
	 */
	public static CCPTextAnnotation cloneAnnotation(CCPTextAnnotation ccpTA, JCas jcas) throws CASException {
		CCPTextAnnotation newCCPTA = new CCPTextAnnotation(jcas);
		UIMA_Util.swapAnnotationInfo(ccpTA, newCCPTA);
		return newCCPTA;
	}

	public static void swapClassMentionInfo(CCPClassMention fromCM, CCPClassMention toCM) throws CASException {
		toCM.setMentionName(fromCM.getMentionName());
		JCas jcas = fromCM.getCAS().getJCas();
		FSArray fromSlotMentions = fromCM.getSlotMentions();
		if (fromSlotMentions != null) {
			FSArray toSlotMentions = new FSArray(jcas, fromSlotMentions.size());
			for (int i = 0; i < fromSlotMentions.size(); i++) {
				if (fromSlotMentions.get(i) instanceof CCPPrimitiveSlotMention) {
					toSlotMentions.set(i,
							copyCCPPrimitiveSlotMention((CCPPrimitiveSlotMention) fromSlotMentions.get(i)));
					// CCPNonComplexSlotMention toSM = new CCPNonComplexSlotMention(jcas);
					// UIMA_Util.swapNonComplexSlotMentionInfo((CCPNonComplexSlotMention)
					// fromSlotMentions.get(i), toSM);
					// toSlotMentions.set(i, toSM);
				} else if (fromSlotMentions.get(i) instanceof CCPComplexSlotMention) {
					CCPComplexSlotMention toSM = new CCPComplexSlotMention(jcas);
					UIMA_Util.swapComplexSlotMentionInfo((CCPComplexSlotMention) fromSlotMentions.get(i), toSM);
					toSlotMentions.set(i, toSM);
				} else {
					System.err.println("Expecting CCPNonComplexSlotMention of CCPComplexSlotMention but got: "
							+ fromSlotMentions.get(i).getClass().getName());
				}
			}
			toCM.setSlotMentions(toSlotMentions);
		}

		CCPTextAnnotation fromTextAnnotation = fromCM.getCcpTextAnnotation();
		if (fromTextAnnotation != null) {
			toCM.setCcpTextAnnotation(fromTextAnnotation);
		}
	}

	private static CCPPrimitiveSlotMention copyCCPPrimitiveSlotMention(CCPPrimitiveSlotMention fromSM)
			throws CASException {
		JCas jcas = fromSM.getCAS().getJCas();
		try {
			if (fromSM instanceof CCPStringSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPStringSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPStringSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPIntegerSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPIntegerSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPIntegerSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPFloatSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPFloatSlotMention(fromSM.getMentionName(),
						convertToCollection(((CCPFloatSlotMention) fromSM).getSlotValues()), jcas);
			} else if (fromSM instanceof CCPBooleanSlotMention) {
				return CCPPrimitiveSlotMentionFactory.createCCPBooleanSlotMention(fromSM.getMentionName(),
						((CCPBooleanSlotMention) fromSM).getSlotValue(), jcas);
			} else {
				throw new KnowledgeRepresentationWrapperException("Unknown CCP Primitive Slot Mention type: "
						+ fromSM.getClass().getName() + " Cannot copy CCPPrimitiveSlotMention.");
			}

		} catch (KnowledgeRepresentationWrapperException e) {
			throw new CASException(e);
		}
	}

	private static Collection<Integer> convertToCollection(IntegerArray iArray) {
		Collection<Integer> iCollection = new ArrayList<Integer>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	private static Collection<String> convertToCollection(StringArray iArray) {
		Collection<String> iCollection = new ArrayList<String>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	private static Collection<Float> convertToCollection(FloatArray iArray) {
		Collection<Float> iCollection = new ArrayList<Float>();
		for (int i = 0; i < iArray.size(); i++) {
			iCollection.add(iArray.get(i));
		}
		return iCollection;
	}

	// public static void swapNonComplexSlotMentionInfo(CCPNonComplexSlotMention fromSM,
	// CCPNonComplexSlotMention toSM)
	// throws CASException {
	// toSM.setMentionName(fromSM.getMentionName());
	// JCas jcas = fromSM.getCAS().getJCas();
	// StringArray fromSlotValues = fromSM.getSlotValues();
	// if (fromSlotValues != null) {
	// StringArray toSlotValues = new StringArray(jcas, fromSlotValues.size());
	// for (int i = 0; i < fromSlotValues.size(); i++) {
	// toSlotValues.set(i, fromSlotValues.get(i));
	// }
	// toSM.setSlotValues(toSlotValues);
	// }
	//
	// }

	public static void swapComplexSlotMentionInfo(CCPComplexSlotMention fromSM, CCPComplexSlotMention toSM)
			throws CASException {
		toSM.setMentionName(fromSM.getMentionName());
		JCas jcas = fromSM.getCAS().getJCas();
		FSArray fromClassMentions = fromSM.getClassMentions();
		if (fromClassMentions != null) {
			FSArray toClassMentions = new FSArray(jcas, fromClassMentions.size());
			for (int i = 0; i < fromClassMentions.size(); i++) {
				CCPClassMention toCM = new CCPClassMention(jcas);
				UIMA_Util.swapClassMentionInfo((CCPClassMention) fromClassMentions.get(i), toCM);
				toClassMentions.set(i, toCM);
			}
			toSM.setClassMentions(toClassMentions);
		}
	}

	public static void swapAnnotatorInfo(CCPAnnotator ccpAnnotator, Annotator annotator) {
		if (ccpAnnotator != null) {
			annotator.setAnnotatorID(new Integer(ccpAnnotator.getAnnotatorID()));
			annotator.setFirstName(ccpAnnotator.getFirstName());
			annotator.setLastName(ccpAnnotator.getLastName());
			annotator.setAffiliation(ccpAnnotator.getAffiliation());
		}
	}

	public static void swapAnnotatorInfo(Annotator annotator, CCPAnnotator ccpAnnotator) {
		if (annotator != null) {
			ccpAnnotator.setAnnotatorID(annotator.getAnnotatorID().intValue());
			ccpAnnotator.setFirstName(annotator.getFirstName());
			ccpAnnotator.setLastName(annotator.getLastName());
			ccpAnnotator.setAffiliation(annotator.getAffiliation());
		}
	}

	public static void swapAnnotationSetInfo(CCPAnnotationSet ccpAnnotationSet, AnnotationSet annotationSet) {
		if (ccpAnnotationSet != null) {
			annotationSet.setAnnotationSetID(new Integer(ccpAnnotationSet.getAnnotationSetID()));
			annotationSet.setAnnotationSetName(ccpAnnotationSet.getAnnotationSetName());
			annotationSet.setAnnotationSetDescription(ccpAnnotationSet.getAnnotationSetDescription());
		}
	}

	public static void swapAnnotationSetInfo(AnnotationSet annotationSet, CCPAnnotationSet ccpAnnotationSet) {
		if (annotationSet != null) {
			ccpAnnotationSet.setAnnotationSetID(annotationSet.getAnnotationSetID().intValue());
			ccpAnnotationSet.setAnnotationSetName(annotationSet.getAnnotationSetName());
			ccpAnnotationSet.setAnnotationSetDescription(annotationSet.getAnnotationSetDescription());
		}
	}

	public static void swapAnnotationMetadata(
			edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata,
			edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata, JCas jcas) {
		if (annotationMetadata != null) {
			/* See if there is an EvaluationResultProperty */
			List<AnnotationMetadataProperty> ccpMetadataPropertiesToAdd = new ArrayList<AnnotationMetadataProperty>();
			EvaluationResultProperty erp = null;
			if (annotationMetadata.isTruePositive()) {
				erp = new TruePositiveProperty(jcas);
			} else if (annotationMetadata.isFalsePositive()) {
				erp = new FalsePositiveProperty(jcas);
			} else if (annotationMetadata.isFalseNegative()) {
				erp = new FalseNegativeProperty(jcas);
			}
			if (erp != null) {
				ccpMetadataPropertiesToAdd.add(erp);
			}

			/* See if there is an OpenDMAP Pattern Property */
			OpenDMAPPatternProperty dmapPatternProp = null;
			if (annotationMetadata.getOpenDMAPPattern() != null) {
				dmapPatternProp = new OpenDMAPPatternProperty(jcas);
				dmapPatternProp.setPattern(annotationMetadata.getOpenDMAPPattern());
				dmapPatternProp.setPatternID(annotationMetadata.getOpenDMAPPatternID());
			}
			if (dmapPatternProp != null) {
				ccpMetadataPropertiesToAdd.add(dmapPatternProp);
			}
			
			AnnotationCommentProperty annotationCommentProp = null;
			logger.info("Checking to see if annotation comment is null...");
			if (annotationMetadata.getAnnotationComment() != null) {
				annotationCommentProp = new AnnotationCommentProperty(jcas);
				annotationCommentProp.setComment(annotationMetadata.getAnnotationComment());
				logger.info("Setting annotation comment: " + annotationMetadata.getAnnotationComment());
			}
			if (annotationCommentProp != null) {
				ccpMetadataPropertiesToAdd.add(annotationCommentProp);
				logger.info("adding comment property to meta data list...");
			}

			/* Swap properties here */
			FSArray metaDataProperties = ccpAnnotationMetadata.getMetadataProperties();
			int propertiesToAddCount = ccpMetadataPropertiesToAdd.size();
			if (metaDataProperties == null) {
				metaDataProperties = new FSArray(jcas, propertiesToAddCount);
				for (int i = 0; i < ccpMetadataPropertiesToAdd.size(); i++) {
					metaDataProperties.set(i, ccpMetadataPropertiesToAdd.get(i));
				}
				ccpAnnotationMetadata.setMetadataProperties(metaDataProperties);
			} else {
				/* add the properties that already exists */
				FSArray newMetaDataProperties = new FSArray(jcas, metaDataProperties.size() + propertiesToAddCount);
				for (int i = 0; i < metaDataProperties.size(); i++) {
					newMetaDataProperties.set(i, metaDataProperties.get(i));
				}
				/* now add the properties that are being transferred -- this could cause duplicates */
				int addIndex = metaDataProperties.size() - 1;
				for (int i = 0; i < ccpMetadataPropertiesToAdd.size(); i++) {
					newMetaDataProperties.set(i + addIndex, metaDataProperties.get(i));
				}
			}
		}
	}

	public static void swapAnnotationMetadata(
			edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata,
			edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata, JCas jcas) {
		if (ccpAnnotationMetadata != null) {
			FSArray metadataProperties = ccpAnnotationMetadata.getMetadataProperties();
			if (metadataProperties != null) {
				for (int i = 0; i < metadataProperties.size(); i++) {
					edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty amp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty) metadataProperties
							.get(i);
					if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty) {
						edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty ccpProp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty) amp;
						edu.ucdenver.ccp.nlp.core.annotation.metadata.OpenDMAPPatternProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.OpenDMAPPatternProperty();
						prop.setPattern(ccpProp.getPattern());
						prop.setPatternID(ccpProp.getPatternID());
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) {
						edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty ccpProp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) amp;
						edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty(ccpProp.getComment());
						annotationMetadata.addMetadataProperty(prop);
				}else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalsePositiveProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.FalsePositiveProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.FalsePositiveProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else if (amp instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalseNegativeProperty) {
						edu.ucdenver.ccp.nlp.core.annotation.metadata.FalseNegativeProperty prop = new edu.ucdenver.ccp.nlp.core.annotation.metadata.FalseNegativeProperty();
						annotationMetadata.addMetadataProperty(prop);
					} else {
						logger.error("Swapping of AnnotationMetadataProperty: " + amp.getClass().getName());
					}
				}
			}
		}
	}

	public static void addAnnotationSet(CCPTextAnnotation ccpTA, CCPAnnotationSet annotationSet, JCas jcas) {
		FSArray updatedAnnotationSets = null;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		if (annotationSets != null) {
			updatedAnnotationSets = new FSArray(jcas, annotationSets.size() + 1);
			for (int i = 0; i < annotationSets.size(); i++) {
				updatedAnnotationSets.set(i, annotationSets.get(i));
			}
		} else {
			updatedAnnotationSets = new FSArray(jcas, 1);
		}

		updatedAnnotationSets.set(updatedAnnotationSets.size() - 1, annotationSet);
		ccpTA.setAnnotationSets(updatedAnnotationSets);
	}

	// public List<DefaultTextAnnotation> getTextAnnotationsFromJCas(JCas jcas) throws Exception {
	// List<DefaultTextAnnotation> textAnnotations = new ArrayList<DefaultTextAnnotation>();
	//
	// FSIterator uimaAnnotationIter =
	// jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
	//
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations = new HashMap<String,
	// DefaultTextAnnotation>();
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions = new HashMap<String,
	// DefaultClassMention>();
	//
	// // for each knowtator annotation, create a corresponding TextAnnotation
	// while (uimaAnnotationIter.hasNext()) {
	// CCPTextAnnotation ccpTA = (CCPTextAnnotation) uimaAnnotationIter.next();
	// DefaultTextAnnotation ta = extractTextAnnotation(ccpTA, jcas, alreadyExtractedAnnotations,
	// alreadyExtractedMentions, null);
	// textAnnotations.add(ta);
	// }
	//
	// // System.out.println("#############################################");
	// // System.out.println("# of alreadyExtractedAnnotations = " +
	// alreadyExtractedAnnotations.size());
	// // Iterator keyIter = alreadyExtractedAnnotations.keySet().iterator();
	// // while (keyIter.hasNext()) {
	// // System.out.println("annotation key: " + keyIter.next());
	// // }
	// // System.out.println("# of alreadyExtractedMentions = " + alreadyExtractedMentions.size());
	// // keyIter = alreadyExtractedMentions.keySet().iterator();
	// // while (keyIter.hasNext()) {
	// // System.out.println("mention key: " + keyIter.next());
	// // }
	// // System.out.println("#############################################");
	// return textAnnotations;
	// }
	//
	// private DefaultTextAnnotation extractTextAnnotation(CCPTextAnnotation ccpTA, JCas jcas,
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions, DefaultClassMention
	// classMention) throws Exception
	// {
	//
	// DefaultTextAnnotation ta = new DefaultTextAnnotation();
	// swapAnnotationInfo(ccpTA, ta, jcas);
	//
	// if (!alreadyExtractedAnnotations.containsKey(getCCPTextAnnotationHashKey(ccpTA))) {
	//
	// /*
	// * if mention is null, then create a new Util ClassMention from the associated Knowtator
	// mention, else, use
	// * the inputted mention. this prevents infinite loops from occurring when creating an
	// annotation from a
	// * ClassMention that was found in a complexSlotMention.
	// */
	// if (classMention == null) {
	// // create associated Util ClassMention
	// CCPClassMention ccpClassMention = ccpTA.getClassMention();
	// classMention = (DefaultClassMention) extractUtilMention(ccpClassMention, jcas,
	// alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// }
	//
	// // associate this TextAnnotation with the classMention
	// classMention.setTextAnnotation(ta);
	//
	// // set the ClassMention for the TextAnnotation
	// ta.setClassMention(classMention);
	//
	// /*
	// * add the TextAnnotation to alreadyExtractedAnnotations if it is not already there Here, the
	// check is just
	// * a safeguard. The TextAnnotation should always be added to the Hash here.
	// */
	// if (!alreadyExtractedAnnotations.containsKey(ta.getSingleLineRepresentation())) {
	// alreadyExtractedAnnotations.put(ta.getSingleLineRepresentation(), ta);
	// }
	// } else {
	// ta = alreadyExtractedAnnotations.get(getCCPTextAnnotationHashKey(ccpTA));
	// }
	//
	// return ta;
	// }
	//
	// private Mention extractUtilMention(CCPMention ccpMention, JCas jcas,
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	// Mention returnMention;
	//
	// if (ccpMention instanceof CCPClassMention) {
	// returnMention = processCCPClassMention(ccpMention, jcas, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else if (ccpMention instanceof CCPComplexSlotMention) {
	// returnMention = processCCPComplexSlotMention(ccpMention, jcas, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else if (ccpMention instanceof CCPNonComplexSlotMention) {
	// returnMention = processCCPSlotMention(ccpMention, jcas, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// } else {
	// if (ccpMention == null) {
	// System.err
	// .println("A null mention has been found. Please make sure each CCPTextAnnotation is associated with a CCPClassMention.");
	// } else {
	// System.err.println("The mention you are trying to create is an instance of: " +
	// ccpMention.getClass().getName());
	// System.err.println("Currently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
	// }
	// returnMention = null;
	// }
	// return returnMention;
	// }
	//
	// private DefaultClassMention processCCPClassMention(CCPMention ccpMention, JCas jcas,
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	//
	// // create a new Util ClassMention
	// CCPClassMention ccpClassMention = (CCPClassMention) ccpMention;
	// DefaultClassMention returnCM = new DefaultClassMention(ccpClassMention.getMentionName());
	//
	// // for each slotMention, add a new Util Mention
	// FSArray ccpSlotMentionArray = ccpClassMention.getSlotMentions();
	// if (ccpSlotMentionArray != null) {
	// FeatureStructure[] ccpSlotMentions = ccpClassMention.getSlotMentions().toArray();
	//
	// // System.out.print("...This mention is a ClassMention:  " + returnCM.getMentionName());
	// // System.out.println("... has " + ccpSlotMentions.length +
	// // " Knowtator slotMentions (complex and non-complex)");
	//
	// for (FeatureStructure fs : ccpSlotMentions) {
	// CCPSlotMention ccpSlotMention = (CCPSlotMention) fs;
	// Mention mention = extractUtilMention(ccpSlotMention, jcas, alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// if (mention instanceof DefaultComplexSlotMention) {
	// returnCM.addComplexSlotMention((DefaultComplexSlotMention) mention);
	// } else if (mention instanceof SlotMention) {
	// returnCM.addPrimitiveSlotMention((PrimitiveSlotMention) mention);
	// }
	// }
	// }
	//
	// CCPTextAnnotation associatedTextAnnotation = ccpClassMention.getCcpTextAnnotation();
	// if (associatedTextAnnotation != null) {
	// DefaultTextAnnotation ta = extractTextAnnotation(associatedTextAnnotation, jcas,
	// alreadyExtractedAnnotations,
	// alreadyExtractedMentions, returnCM);
	// returnCM.setTextAnnotation(ta);
	// }
	//
	// /*
	// * we have now gotten a complete ClassMention back if it (or one like it) has already been
	// stored, then use the
	// * one already stored, else store this new ClassMention.
	// */
	// String hashkey = returnCM.getSingleLineRepresentation();
	// if (alreadyExtractedMentions.containsKey(hashkey)) {
	// returnCM = alreadyExtractedMentions.get(hashkey);
	// } else {
	// alreadyExtractedMentions.put(hashkey, returnCM);
	// }
	//
	// return returnCM;
	// }
	//
	// private DefaultComplexSlotMention processCCPComplexSlotMention(CCPMention ccpMention, JCas
	// jcas,
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions) throws Exception {
	//
	// CCPComplexSlotMention ccpComplexSlotMention = (CCPComplexSlotMention) ccpMention;
	// DefaultComplexSlotMention returnCSM = new
	// DefaultComplexSlotMention(ccpComplexSlotMention.getMentionName());
	//
	// // System.out.print("...This mention is a ComplexSlotMention:  " +
	// returnCSM.getMentionName());
	//
	// FeatureStructure[] ccpClassMentions = ccpComplexSlotMention.getClassMentions().toArray();
	// for (FeatureStructure fs : ccpClassMentions) {
	// CCPClassMention ccpClassMention = (CCPClassMention) fs;
	// DefaultClassMention cm = (DefaultClassMention) extractUtilMention(ccpClassMention, jcas,
	// alreadyExtractedAnnotations,
	// alreadyExtractedMentions);
	// returnCSM.addClassMention(cm);
	// }
	// return returnCSM;
	// }
	//
	// private PrimitiveSlotMention processCCPSlotMention(CCPMention ccpMention, JCas jcas,
	// HashMap<String, DefaultTextAnnotation> alreadyExtractedAnnotations,
	// HashMap<String, DefaultClassMention> alreadyExtractedMentions) throws InvalidInputException {
	// CCPNonComplexSlotMention ccpNonComplexSlotMention = (CCPNonComplexSlotMention) ccpMention;
	// PrimitiveSlotMention returnSM = null;
	//
	// if (ccpNonComplexSlotMention.getSlotValues() != null) {
	//
	// Collection<String> slotValues =
	// Arrays.asList(ccpNonComplexSlotMention.getSlotValues().toArray());
	// returnSM =
	// DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(ccpNonComplexSlotMention.getMentionName(),
	// slotValues);
	//
	// // System.out.println("...This mention is a SlotMention:  " + returnSM.getMentionName());
	//
	// }
	// return returnSM;
	// }
	//
	// private String getCCPTextAnnotationHashKey(CCPTextAnnotation ccpTextAnnotation) {
	// return getSingleLineRepresentationOfCCPTextAnnotation(ccpTextAnnotation, true);
	// }

	// public static void insertAnnotationsIntoCas(JCas jcas, Collection<TextAnnotation>
	// annotationsToInsert) {
	// Map<Long, CCPTextAnnotation> alreadyInsertedAnnotations = new HashMap<Long,
	// CCPTextAnnotation>();
	//		
	//		
	//		
	// }

	public void putTextAnnotationsIntoJCas(JCas jcas, Collection<TextAnnotation> textAnnotations) {
		HashMap<String, String> alreadyCreatedAnnotations = new HashMap<String, String>();
		HashMap<String, CCPClassMention> alreadyCreatedMentions = new HashMap<String, CCPClassMention>();

		for (TextAnnotation ta : textAnnotations) {
			createUIMAAnnotation(ta, jcas, alreadyCreatedAnnotations, alreadyCreatedMentions, null);
		}

		// System.out.println("#############################################");
		// System.out.println("# of alreadyCreatedAnnotations = " +
		// alreadyCreatedAnnotations.size());
		// Iterator keyIter = alreadyCreatedAnnotations.keySet().iterator();
		// while (keyIter.hasNext()) {
		// System.out.println("annotation key: " + keyIter.next());
		// }
		// System.out.println("# of alreadyCreatedMentions = " + alreadyCreatedMentions.size());
		// keyIter = alreadyCreatedMentions.keySet().iterator();
		// while (keyIter.hasNext()) {
		// System.out.println("annotation key: " + keyIter.next());
		// }
		// System.out.println("#############################################");
	}

//	/**
//	 * Adds processing of syntactic annotations to previous method
//	 */
//	public void putTextAnnotationsIntoJCas(JCas jcas, List<TextAnnotation> textAnnotations,
//			boolean processSyntacticAnnotations) {
//		putTextAnnotationsIntoJCas(jcas, textAnnotations);
//
//		if (processSyntacticAnnotations) {
//			UIMASyntacticAnnotation_Util.normalizeSyntacticAnnotations(jcas);
//		}
//	}

	private void createUIMAAnnotation(TextAnnotation ta, JCas jcas, HashMap<String, String> alreadyCreatedAnnotations,
			HashMap<String, CCPClassMention> alreadyCreatedMentions, CCPClassMention classMention) {

		if (!alreadyCreatedAnnotations.containsKey(ta.getSingleLineRepresentation())) {

			CCPTextAnnotation ccpTextAnnotation = new CCPTextAnnotation(jcas);

			// extract the annotation information from the TextAnnotation and
			// fill the corresponding fields in the SemanticAnnotation
			UIMA_Util.swapAnnotationInfo(ta, ccpTextAnnotation, jcas);

			/**
			 * This has the potential to introduce a duplicate comment, however when working with knowtator annotations - where there is no meta data, this is the only way to transfer comments
			 */
			String comment = ta.getAnnotationComment();
			if (comment != null)
				UIMA_Annotation_Util.addAnnotationCommentProperty(ccpTextAnnotation, comment, jcas);
			
//			assert ta.getAnnotationComment().equals(UIMA_Annotation_Util.getAnnotationCommentPropertyValue(ccpTextAnnotation, jcas)) : String.format("Expected annotation comment \"%s\" but observed: \"%s\"", ta.getAnnotationComment(),UIMA_Annotation_Util.getAnnotationCommentPropertyValue(ccpTextAnnotation, jcas)) ;
			
			// add key to alreadyAddedAnnotations
			alreadyCreatedAnnotations.put(ta.getSingleLineRepresentation(), "");

			// if mention is null, then create a new UIMA mention from the
			// associated ClassMention, else, use mention
			// this prevents infinite loops from occurring when creating an
			// annotation from a ClassMention that was found in a
			// complexSlotMention.
			if (classMention == null) {
				// create associated class mention
				ClassMention cm = ta.getClassMention();
				classMention = (CCPClassMention) this.createUIMAMention(cm, jcas, alreadyCreatedAnnotations,
						alreadyCreatedMentions);
			}

			if (classMention == null) {
				System.err.println("CLASSMENTION IS NULL!!!!");
			}
			classMention.setCcpTextAnnotation(ccpTextAnnotation);

			ccpTextAnnotation.setClassMention(classMention);

			// add the SemanticAnnotation to the CAS indexes
			ccpTextAnnotation.addToIndexes();
		} else {
			// do nothing, this annotation has already been created in the
			// Knowtator project
		}
	}

	private CCPMention createUIMAMention(Mention mentionToAdd, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		CCPMention returnMention;

		// System.out.println("Creating new Knowtator mention: " + mentionToAdd.getMentionName());

		if (mentionToAdd instanceof ClassMention) {
			returnMention = processClassMention((ClassMention) mentionToAdd, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else if (mentionToAdd instanceof ComplexSlotMention) {
			returnMention = processComplexSlotMention((ComplexSlotMention) mentionToAdd, jcas,
					alreadyCreatedAnnotations, alreadyCreatedMentions);
		} else if (mentionToAdd instanceof SlotMention) {
			returnMention = processSlotMention((SlotMention) mentionToAdd, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);
		} else {
			System.err.println("The mention you are trying to create is an instance of: "
					+ mentionToAdd.getClass().getName());
			System.err.println("Currently, only ClassMentions, ComplexSlotMentions, and SlotMentions can be added.");
			returnMention = null;
		}

		return returnMention;
	}

	private CCPClassMention processClassMention(ClassMention classMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		CCPClassMention ccpClassMention;

		// check to see if this mention has already been created
		if (alreadyCreatedMentions.containsKey(classMention.getSingleLineRepresentation())) {
			ccpClassMention = alreadyCreatedMentions.get(classMention.getSingleLineRepresentation());
		} else {
			// create a new Knowtator mention

			ccpClassMention = new CCPClassMention(jcas);
			ccpClassMention.setMentionName(classMention.getMentionName());

			/**
			 * should this be at the end??? No, it doesn't matter because classMention.getHashKey is
			 * complete
			 */
			// add the knowtator to the alreadyCreatedMentions hash
			alreadyCreatedMentions.put(classMention.getSingleLineRepresentation(), ccpClassMention);

			// for each complexSlotMention, add a new knowtator class
			// mention
			Collection<ComplexSlotMention> complexSlotMentions = classMention.getComplexSlotMentions();
			Collection<PrimitiveSlotMention> slotMentions = null;
			try {
				slotMentions = classMention.getPrimitiveSlotMentions();
			} catch (KnowledgeRepresentationWrapperException e) {
				e.printStackTrace();
			}

			// System.out.println("...This mention is a ClassMention");
			// System.out.println("... has " + complexSlotMentions.size() + " ComplexSlotMentions");

			int nonEmptyComplexSlotMentions = SlotMention.nonEmptySlotMentionCount(complexSlotMentions);
			int nonEmptyPrimitiveSlotMentions = SlotMention.nonEmptySlotMentionCount(slotMentions);
			// logger.info(String.format("# non-empty slots: %d", nonEmptyComplexSlotMentions +
			// nonEmptyPrimitiveSlotMentions));
			FSArray slotMentionFSArray = new FSArray(jcas, nonEmptyComplexSlotMentions + nonEmptyPrimitiveSlotMentions);
			int fsArrayIndex = 0;
			for (ComplexSlotMention csm : complexSlotMentions) {
				CCPComplexSlotMention ccpComplexSlotMention = (CCPComplexSlotMention) createUIMAMention(csm, jcas,
						alreadyCreatedAnnotations, alreadyCreatedMentions);

				// System.out.println("......adding slot: " + csm.getMentionName() + "  to: " +
				// classMention.getMentionName());
				if (ccpComplexSlotMention != null) {
					slotMentionFSArray.set(fsArrayIndex++, ccpComplexSlotMention);
				}
			}

			// for each (non-complex) slot mention

			// System.out.println("... has " + slotMentions.size() + " SlotMentions");

			for (SlotMention sm : slotMentions) {
				CCPPrimitiveSlotMention ccpNonComplexSlotMention = (CCPPrimitiveSlotMention) createUIMAMention(sm,
						jcas, alreadyCreatedAnnotations, alreadyCreatedMentions);

				// System.out.println("......adding slot: " + sm.getMentionName() + "  to: " +
				// classMention.getMentionName());
				if (ccpNonComplexSlotMention != null) {
					slotMentionFSArray.set(fsArrayIndex++, ccpNonComplexSlotMention);
				}
			}

			// add slot mentions array to classmention
			ccpClassMention.setSlotMentions(slotMentionFSArray);

			// create Knowtator annotation of TextAnnotation(s) associated with
			// this ClassMention
			createUIMAAnnotation(classMention.getTextAnnotation(), jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions, ccpClassMention);
		}
		return ccpClassMention;
	}

	private CCPComplexSlotMention processComplexSlotMention(ComplexSlotMention complexSlotMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		// System.out.println("...This mention is a ComplexSlotMention");

		// create a new Knowtator mention
		CCPComplexSlotMention ccpComplexSlotMention = new CCPComplexSlotMention(jcas);
		ccpComplexSlotMention.setMentionName(complexSlotMention.getMentionName());

		// for each of its classMentions, create a new Knowtator mention
		Collection<ClassMention> classMentions = complexSlotMention.getClassMentions();

		if (classMentions.size() == 0) {
			return null;
		}

		FSArray classMentionFSArray = new FSArray(jcas, classMentions.size());
		int fsArrayIndex = 0;

		for (ClassMention cm : classMentions) {
			CCPClassMention ccpClassMention = (CCPClassMention) createUIMAMention(cm, jcas, alreadyCreatedAnnotations,
					alreadyCreatedMentions);

			// System.out.println("......adding value: " + cm.getMentionName() +
			// "  to slot mention: "
			// + complexSlotMention.getMentionName());

			classMentionFSArray.set(fsArrayIndex++, ccpClassMention);
		}

		// add slot mentions array to classmention
		ccpComplexSlotMention.setClassMentions(classMentionFSArray);

		return ccpComplexSlotMention;
	}

	private CCPPrimitiveSlotMention processSlotMention(SlotMention slotMention, JCas jcas,
			HashMap<String, String> alreadyCreatedAnnotations, HashMap<String, CCPClassMention> alreadyCreatedMentions) {

		Collection<Object> slotValues = slotMention.getSlotValues();
		CCPPrimitiveSlotMention ccpPSM = null;
		try {
			ccpPSM = CCPPrimitiveSlotMentionFactory.createCCPPrimitiveSlotMention(slotMention.getMentionName(),
					slotValues, jcas);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		}

		return ccpPSM;

		// // create a new Knowtator mention
		// CCPNonComplexSlotMention ccpNonComplexSlotMention = new CCPNonComplexSlotMention(jcas);
		// ccpNonComplexSlotMention.setMentionName(slotMention.getMentionName());
		//
		// // for each Object slot value, add it to the Knowtator slot mention
		// ArrayList<Object> slotValues = (ArrayList<Object>) slotMention.getSlotValues();
		//
		// StringArray valueArray = new StringArray(jcas, slotValues.size());
		// int stringArrayIndex = 0;
		//
		// // we are making an assumption that the slotValues are Strings
		// for (Object value : slotValues) {
		// valueArray.set(stringArrayIndex++, (String) value.toString());
		// }
		//
		// ccpNonComplexSlotMention.setSlotValues(valueArray);
		//
		// return ccpNonComplexSlotMention;
	}

	// public static void printCCPAnnotator(CCPAnnotator ccpAnnotator, PrintStream ps) {
	// String annotatorStr;
	// if (ccpAnnotator != null) {
	// annotatorStr = ccpAnnotator.getAnnotatorID() + " - " + ccpAnnotator.getFirstName() + " " +
	// ccpAnnotator.getLastName() + " -- "
	// + ccpAnnotator.getAffiliation();
	// } else {
	// annotatorStr = "No Annotator Specified";
	// }
	// ps.println(annotatorStr);
	// }
	//
	// public static void printCCPAnnotationSet(CCPAnnotationSet ccpAnnotationSet, PrintStream ps) {
	// String annotationSetStr = ccpAnnotationSet.getAnnotationSetID() + " - " +
	// ccpAnnotationSet.getAnnotationSetName()
	// + " -- "
	// + ccpAnnotationSet.getAnnotationSetDescription();
	// ps.println(annotationSetStr);
	// }

	// public String getSingleLineRepresentationOfCCPTextAnnotation(CCPTextAnnotation ccpTA, boolean
	// deep) {
	// String repStr = "";
	//
	// String spansStr = "";
	// FSArray spans = ccpTA.getSpans();
	// if (spans != null) {
	// for (int i = 0; i < spans.size(); i++) {
	// CCPSpan span = (CCPSpan) spans.get(i);
	// spansStr += ("[" + span.getSpanStart() + ".." + span.getSpanEnd() + "]");
	// }
	// spansStr = spansStr.substring(0, spansStr.length());
	// }
	//
	// String coveredText;
	// try {
	// coveredText = ccpTA.getCoveredText();
	// } catch (StringIndexOutOfBoundsException siobe) {
	// coveredText = "";
	// }
	// CCPAnnotator ccpAnnotator = ccpTA.getAnnotator();
	// String lastName = "";
	// if (ccpAnnotator != null) {
	// lastName = ccpAnnotator.getLastName();
	// }
	//
	// String annotationSetStr = "";
	// FSArray ccpAnnotationSets = ccpTA.getAnnotationSets();
	// if (ccpAnnotationSets != null) {
	// for (int i = 0; i < ccpAnnotationSets.size(); i++) {
	// CCPAnnotationSet ccpASet = (CCPAnnotationSet) ccpAnnotationSets.get(i);
	// annotationSetStr += (ccpASet.getAnnotationSetID() + ",");
	// }
	// }
	//
	// if (deep) {
	// repStr += (spansStr + "|\"" + coveredText + "\"" + "|" + lastName + "|" + annotationSetStr +
	// "{"
	// + getSingleLineRepresentationOfCCPClassMention(ccpTA.getClassMention()) + "}");
	// } else {
	// repStr += (spansStr + "|\"" + coveredText + "\"" + "|" + lastName + "|" + annotationSetStr);
	// }
	// return repStr;
	// }
	//
	// public String getSingleLineRepresentationOfCCPNonComplexSlotMention(CCPNonComplexSlotMention
	// ccpNCSM) {
	// String repStr = " [" + ccpNCSM.getMentionName() + "]:";
	//
	// StringArray slotValues = ccpNCSM.getSlotValues();
	// if (slotValues != null) {
	// for (int i = 0; i < slotValues.size(); i++) {
	// repStr += slotValues.get(i) + ":";
	// }
	// repStr = repStr.substring(0, repStr.length() - 1);
	// }
	// return repStr;
	//
	// }
	//
	// public String getSingleLineRepresentationOfCCPComplexSlotMention(CCPComplexSlotMention
	// ccpCSM) {
	// String repStr = " [" + ccpCSM.getMentionName() + "]:";
	// FSArray classMentions = ccpCSM.getClassMentions();
	// for (int i = 0; i < classMentions.size(); i++) {
	// CCPClassMention ccpCM = (CCPClassMention) classMentions.get(i);
	// CCPTextAnnotation associatedTextAnnotation = ccpCM.getCcpTextAnnotation();
	// // FSArray associatedTextAnnotations = ccpCM.getCcpTextAnnotations();
	// if (associatedTextAnnotation != null) {
	// repStr += getSingleLineRepresentationOfCCPTextAnnotation(associatedTextAnnotation, true);
	// }
	// }
	// return repStr;
	//
	// }
	//
	// public String getSingleLineRepresentationOfCCPClassMention(CCPClassMention ccpCM) {
	// if (ccpCM != null) {
	// CCPTextAnnotation associatedTextAnnotation = ccpCM.getCcpTextAnnotation();
	// // FSArray associatedTextAnnotations = ccpCM.getCcpTextAnnotations();
	//
	// String repStr = " [" + ccpCM.getMentionName() + "]";
	// if (associatedTextAnnotation == null) {
	// System.err.println("ERROR -- UIMA_Util: FOUND ClassMention without a reference to its TextAnnotation...");
	// UIMA_Util.printCCPClassMention(ccpCM, System.err, 0);
	// return null;
	// } else {
	// repStr += getSingleLineRepresentationOfCCPTextAnnotation(associatedTextAnnotation, false) +
	// "(";
	// }
	//
	// FSArray ccpSlotMentions = ccpCM.getSlotMentions();
	// List<CCPSlotMention> ccpSlotMentionsList = new ArrayList<CCPSlotMention>();
	// if (ccpSlotMentions != null) {
	// for (int i = 0; i < ccpSlotMentions.size(); i++) {
	// ccpSlotMentionsList.add((CCPSlotMention) ccpSlotMentions.get(i));
	// }
	// }
	//
	// List<CCPSlotMention> sortedSlotMentions = sortSlotMentions(ccpSlotMentionsList);
	// for (CCPSlotMention sm : sortedSlotMentions) {
	// repStr += getSingleLineRepresentationOfCCPSlotMention(sm);
	// }
	//
	// repStr += ")";
	// return repStr;
	// } else {
	// return null;
	// }
	// }
	//
	// private String getSingleLineRepresentationOfCCPSlotMention(CCPSlotMention ccpSM) {
	// if (ccpSM instanceof CCPNonComplexSlotMention) {
	// return getSingleLineRepresentationOfCCPNonComplexSlotMention((CCPNonComplexSlotMention)
	// ccpSM);
	// } else if (ccpSM instanceof CCPComplexSlotMention) {
	// return getSingleLineRepresentationOfCCPComplexSlotMention((CCPComplexSlotMention) ccpSM);
	// } else {
	// System.err.println("Expecting CCPNonComplexSlotMention or CCPComplexSlotMention but instead got "
	// +
	// ccpSM.getClass().getName());
	// return null;
	// }
	// }
	//
	// private List<CCPSlotMention> sortSlotMentions(List<CCPSlotMention> slotMentions) {
	// List<CCPSlotMention> sortedCSMList = new ArrayList<CCPSlotMention>();
	// List<CCPSlotMention> sortedSMList = new ArrayList<CCPSlotMention>();
	//
	// HashMap<String, CCPSlotMention> inputCSMHash = new HashMap<String, CCPSlotMention>();
	// HashMap<String, CCPSlotMention> inputSMHash = new HashMap<String, CCPSlotMention>();
	//
	// for (CCPSlotMention ment : slotMentions) {
	// if (ment instanceof CCPComplexSlotMention) {
	// inputCSMHash.put(getSingleLineRepresentationOfCCPSlotMention(ment), ment);
	// } else if (ment instanceof CCPNonComplexSlotMention) {
	// inputSMHash.put(getSingleLineRepresentationOfCCPSlotMention(ment), ment);
	// } else {
	// System.err.println("Expecting sm or csm in uima_util.sortSLotMEntions!!!!!");
	// }
	//
	// }
	//
	// Set<String> keys = inputCSMHash.keySet();
	// List<String> sortedKeys = Arrays.asList(keys.toArray(new String[0]));
	// for (String key : sortedKeys) {
	// sortedCSMList.add(inputCSMHash.get(key));
	// }
	//
	// keys = inputSMHash.keySet();
	// sortedKeys = Arrays.asList(keys.toArray(new String[0]));
	// for (String key : sortedKeys) {
	// sortedSMList.add(inputSMHash.get(key));
	// }
	//
	// sortedCSMList.addAll(sortedSMList);
	// return sortedCSMList;
	//
	// }

	// public static void printCCPTextAnnotation(CCPTextAnnotation ccpTextAnnotation, PrintStream
	// ps) {
	// String spansStr = "";
	// FSArray spans = ccpTextAnnotation.getSpans();
	// for (int i = 0; i < spans.size(); i++) {
	// CCPSpan span = (CCPSpan) spans.get(i);
	// spansStr += (span.getSpanStart() + " - " + span.getSpanEnd() + "  ");
	// }
	//
	// ps.println("======================= Annotation: " + ccpTextAnnotation.getAnnotationID() +
	// " =======================");
	// ps.print("Annotator: ");
	// printCCPAnnotator(ccpTextAnnotation.getAnnotator(), ps);
	// ps.print("--- AnnotationSets: ");
	// FSArray annotationSets = ccpTextAnnotation.getAnnotationSets();
	// if (annotationSets != null) {
	// for (int i = 0; i < annotationSets.size(); i++) {
	// printCCPAnnotationSet((CCPAnnotationSet) annotationSets.get(i), ps);
	// }
	// if (annotationSets.size() == 0) {
	// ps.println();
	// }
	// } else {
	// ps.println();
	// }
	// ps.println("--- Default Span: " + ccpTextAnnotation.getBegin() + " - " +
	// ccpTextAnnotation.getEnd());
	// ps.println("--- Span: " + spansStr);
	// ps.println("--- DocumentSection: " + ccpTextAnnotation.getDocumentSectionID());
	// ps.println("--- Covered Text: " + ccpTextAnnotation.getCoveredText());
	//
	// CCPClassMention ccpCM = ccpTextAnnotation.getClassMention();
	// if (ccpCM != null) {
	// printCCPClassMention(ccpCM, ps, 0);
	// } else {
	// ps.println("--- ClassMention: NULL");
	// }
	// ps.println("=================================================================================");
	//
	// }

	public static void printCCPTextAnnotation(CCPTextAnnotation ccpTA, PrintStream ps) {
		TextAnnotation ta = new WrappedCCPTextAnnotation(ccpTA);
		ps.println(ta.toString());
	}

	// public static void printCCPClassMention(CCPClassMention ccpClassMention, PrintStream ps, int
	// indentLevel) {
	// String indentSpace = "-";
	// for (int i = 0; i < indentLevel * 4; i++) {
	// indentSpace += " ";
	// }
	// if (ccpClassMention != null) {
	// // Print out class mention name and span string
	// ps.println(indentSpace + "CLASS MENTION: " + ccpClassMention.getMentionName() + " \""
	// + getCCPClassMentionString(ccpClassMention) + "\"");
	// indentLevel++;
	//
	// try {
	// FeatureStructure[] slotMentions = ccpClassMention.getSlotMentions().toArray();
	// for (FeatureStructure slotMention : slotMentions) {
	// if (slotMention instanceof CCPComplexSlotMention) {
	// printCCPComplexSlotMention((CCPComplexSlotMention) slotMention, ps, indentLevel);
	// } else if (slotMention instanceof CCPNonComplexSlotMention) {
	// printCCPNonComplexSlotMention((CCPNonComplexSlotMention) slotMention, ps, indentLevel);
	// } else {
	// System.err
	// .println("Error while trying to print slot mention of CCPClassMention - it was neither a CCPComplexSlotMention nor a CCPNonComplexSlotMention. Instead it was a "
	// + slotMention.getClass().getName());
	// }
	// }
	// } catch (NullPointerException npe) {
	// // if the CCPClassMention has not SlotMentions, then we end up
	// // here
	// // so, do nothing.
	// }
	// } else {
	// ps.println(indentSpace + "CLASS MENTION: null");
	// }
	// }
	//
	// public static void printCCPComplexSlotMention(CCPComplexSlotMention ccpComplexSlotMention,
	// PrintStream ps, int
	// indentLevel) {
	// String indentSpace = "-";
	// for (int i = 0; i < indentLevel * 4; i++) {
	// indentSpace += " ";
	// }
	// ps.println(indentSpace + "COMPLEX SLOT MENTION: " + ccpComplexSlotMention.getMentionName());
	// indentLevel++;
	//
	// FeatureStructure[] classMentions = ccpComplexSlotMention.getClassMentions().toArray();
	// for (FeatureStructure classMention : classMentions) {
	// if (classMention instanceof CCPClassMention) {
	// printCCPClassMention((CCPClassMention) classMention, ps, indentLevel);
	// } else {
	// System.err
	// .println("Error while trying to print slot mention of CCPComplexSlotMention - it was not a CCPClassMention. Instead it was a "
	// + classMention.getClass().getName());
	// }
	// }
	// }
	//
	// public static void printCCPNonComplexSlotMention(CCPNonComplexSlotMention
	// ccpNonComplexSlotMention, PrintStream
	// ps, int indentLevel) {
	// String indentSpace = "-";
	// for (int i = 0; i < indentLevel * 4; i++) {
	// indentSpace += " ";
	// }
	// ps.print(indentSpace + "SLOT MENTION: " + ccpNonComplexSlotMention.getMentionName() +
	// " with SLOT VALUE(s): ");
	// String[] values = ccpNonComplexSlotMention.getSlotValues().toArray();
	// for (String value : values) {
	// ps.print(value + "  ");
	// }
	// ps.println();
	// }

	public static CCPSlotMention getSlotMentionByName(CCPTextAnnotation ccpTextAnnotation, String slotMentionName) {
		return getSlotMentionByName(ccpTextAnnotation.getClassMention(), slotMentionName);
	}

	public static CCPSlotMention getSlotMentionByName(CCPClassMention ccpClassMention, String slotMentionName) {
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				// FeatureStructure[] slotMentions = slotMentionsArray.toArray();
				// for (FeatureStructure fs : slotMentions) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPSlotMention) {
					CCPSlotMention ccpSlotMention = (CCPSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				} else {
					logger.error("Expecting CCPSlotMention but got a : " + fs.getClass().getName());
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static CCPPrimitiveSlotMention getPrimitiveSlotMentionByName(CCPClassMention ccpClassMention,
			String slotMentionName) {
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPPrimitiveSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				// FeatureStructure[] slotMentions = slotMentionsArray.toArray();
				// for (FeatureStructure fs : slotMentions) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPPrimitiveSlotMention) {
					CCPPrimitiveSlotMention ccpSlotMention = (CCPPrimitiveSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static CCPComplexSlotMention getComplexSlotMentionByName(CCPTextAnnotation ccpTA, String slotMentionName) {
		return getComplexSlotMentionByName(ccpTA.getClassMention(), slotMentionName);
	}

	public static CCPComplexSlotMention getComplexSlotMentionByName(CCPClassMention ccpClassMention,
			String slotMentionName) {

		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			CCPComplexSlotMention returnSlotMention = null;
			for (int i = 0; i < slotMentionsArray.size(); i++) {
				// FeatureStructure[] slotMentions = slotMentionsArray.toArray();
				// for (FeatureStructure fs : slotMentions) {
				FeatureStructure fs = slotMentionsArray.get(i);
				if (fs instanceof CCPComplexSlotMention) {
					CCPComplexSlotMention ccpSlotMention = (CCPComplexSlotMention) fs;
					if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
						returnSlotMention = ccpSlotMention;
						break;
					}
				}
			}
			return returnSlotMention;
		} else {
			return null;
		}
	}

	public static void removeSlotMentions(CCPClassMention ccpCM, Class slotType, JCas jcas) {
		List<CCPSlotMention> slotMentionsToKeep = new ArrayList<CCPSlotMention>();
		FSArray slotMentions = ccpCM.getSlotMentions();
		if (slotMentions != null) {
			for (int i = 0; i < slotMentions.size(); i++) {
				if (!(slotMentions.get(i).getClass().isInstance(slotType))) {
					slotMentionsToKeep.add((CCPSlotMention) slotMentions.get(i));
				}
			}
		}
		FSArray updatedSlotMentions = new FSArray(jcas, slotMentionsToKeep.size());
		for (int i = 0; i < slotMentionsToKeep.size(); i++) {
			updatedSlotMentions.set(i, slotMentionsToKeep.get(i));
		}
		ccpCM.setSlotMentions(updatedSlotMentions);
	}

	public static void addSlotMentions(CCPClassMention ccpCM, Collection<CCPSlotMention> slotMentions, JCas jcas) {
		FSArray updatedSlotMentions = ccpCM.getSlotMentions();
		for (CCPSlotMention ccpSM : slotMentions) {
			updatedSlotMentions = addToFSArray(updatedSlotMentions, ccpSM, jcas);
		}
		ccpCM.setSlotMentions(updatedSlotMentions);
	}

	/**
	 * Returns a list of the CCPClassMention objects that are the slot fillers for the named slot
	 * 
	 * @param ccpTA
	 * @param slotMentionName
	 * @return
	 */
	public static List<CCPClassMention> getComplexSlotValues(CCPTextAnnotation ccpTA, String slotMentionName) {
		return getComplexSlotValues(ccpTA.getClassMention(), slotMentionName);
	}

	/**
	 * Returns a list of the CCPClassMention objects that are the slot fillers for the named slot
	 * 
	 * @param ccpClassMention
	 * @param slotMentionName
	 * @return
	 */
	public static List<CCPClassMention> getComplexSlotValues(CCPClassMention ccpClassMention, String slotMentionName) {
		List<CCPClassMention> slotValuesToReturn = new ArrayList<CCPClassMention>();
		CCPSlotMention slotMention = getSlotMentionByName(ccpClassMention, slotMentionName);
		if (slotMention != null) {
			if (slotMention instanceof CCPComplexSlotMention) {
				CCPComplexSlotMention ccpCSM = (CCPComplexSlotMention) slotMention;
				slotValuesToReturn = fsarrayToList(ccpCSM.getClassMentions());
			} else {
				logger.warn("Slot: " + slotMentionName + " is not a complex slot (It is a "
						+ slotMention.getClass().getName() + "), therefore no slot values are being returned.");
			}
		}
		return slotValuesToReturn;
	}

	public static Set<String> getSlotNames(CCPClassMention ccpCM, Class slotType) {
		Set<String> slotNames = new HashSet<String>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		for (int i = 0; i < ccpSlotMentions.size(); i++) {
			if (ccpSlotMentions.get(i).getClass().isInstance(slotType)) {
				slotNames.add(((CCPSlotMention) ccpSlotMentions.get(i)).getMentionName());
			}
		}
		return slotNames;
	}

	public static Collection<CCPComplexSlotMention> getComplexSlotMentions(CCPClassMention ccpCM) {
		Collection<CCPComplexSlotMention> slotMentions = new ArrayList<CCPComplexSlotMention>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		if (ccpSlotMentions != null) {
			for (int i = 0; i < ccpSlotMentions.size(); i++) {
				if (ccpSlotMentions.get(i) instanceof CCPComplexSlotMention) {
					slotMentions.add(((CCPComplexSlotMention) ccpSlotMentions.get(i)));
				}
			}
		}
		return slotMentions;
	}

	public static Collection<CCPPrimitiveSlotMention> getPrimitiveSlotMentions(CCPClassMention ccpCM) {
		Collection<CCPPrimitiveSlotMention> slotMentions = new ArrayList<CCPPrimitiveSlotMention>();
		FSArray ccpSlotMentions = ccpCM.getSlotMentions();
		if (ccpSlotMentions != null) {
			for (int i = 0; i < ccpSlotMentions.size(); i++) {
				if (ccpSlotMentions.get(i) instanceof CCPPrimitiveSlotMention) {
					slotMentions.add(((CCPPrimitiveSlotMention) ccpSlotMentions.get(i)));
				}
			}
		}
		return slotMentions;
	}

	/**
	 * Converts an FSArray to a List<>
	 * 
	 * @param <T>
	 * @param fsArray
	 * @return
	 */
	public static <T extends FeatureStructure> List<T> fsarrayToList(FSArray fsArray) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < fsArray.size(); i++) {
			list.add((T) fsArray.get(i));
		}
		return list;
	}

	/**
	 * Converts a list of FeatureStructure objects into an FSArray
	 * 
	 * @param list
	 * @param jcas
	 * @return
	 */
	public static FSArray listToFsarray(List<FeatureStructure> list, JCas jcas) {
		FSArray fsarray = new FSArray(jcas, list.size());
		for (int i = 0; i < list.size(); i++) {
			fsarray.set(i, list.get(i));
		}
		return fsarray;
	}

	/**
	 * Returns a new FSArray consisting of the contents of the input FSArray and the Collection of
	 * FeatureStructure objects
	 * 
	 * @param fsArray
	 * @param featureStructuresToAdd
	 * @return
	 * @throws CASException
	 */
	public static FSArray addToFSArray(FSArray fsArray, Collection<TOP> featureStructuresToAdd, JCas jcas) {
		if (fsArray == null) {
			fsArray = new FSArray(jcas, 0);
		}
		FSArray fsArrayToReturn = new FSArray(jcas, fsArray.size() + featureStructuresToAdd.size());
		for (int i = 0; i < fsArray.size(); i++) {
			fsArrayToReturn.set(i, fsArray.get(i));
		}
		int index = fsArray.size();
		for (TOP fs : featureStructuresToAdd) {
			fsArrayToReturn.set(index++, fs);
		}
		return fsArrayToReturn;
	}

	/**
	 * Adds a single feature structure to a FSArray
	 * 
	 * @param fsArray
	 * @param featureStructureToAdd
	 * @param jcas
	 * @return
	 */
	public static FSArray addToFSArray(FSArray fsArray, TOP featureStructureToAdd, JCas jcas) {
		if (fsArray == null) {
			fsArray = new FSArray(jcas, 0);
		}
		FSArray fsArrayToReturn = new FSArray(jcas, fsArray.size() + 1);
		for (int i = 0; i < fsArray.size(); i++) {
			fsArrayToReturn.set(i, fsArray.get(i));
		}
		fsArrayToReturn.set(fsArray.size(), featureStructureToAdd);
		return fsArrayToReturn;
	}

	public static StringArray addToStringArray(StringArray stringArray, String stringToAdd, JCas jcas) {
		if (stringArray == null) {
			stringArray = new StringArray(jcas, 0);
		}
		StringArray stringArrayToReturn = new StringArray(jcas, stringArray.size() + 1);
		for (int i = 0; i < stringArray.size(); i++) {
			stringArrayToReturn.set(i, stringArray.get(i));
		}
		stringArrayToReturn.set(stringArray.size(), stringToAdd);
		return stringArrayToReturn;
	}

	public static IntegerArray addToIntegerArray(IntegerArray integerArray, Integer integerToAdd, JCas jcas) {
		if (integerArray == null) {
			integerArray = new IntegerArray(jcas, 0);
		}
		IntegerArray integerArrayToReturn = new IntegerArray(jcas, integerArray.size() + 1);
		for (int i = 0; i < integerArray.size(); i++) {
			integerArrayToReturn.set(i, integerArray.get(i));
		}
		integerArrayToReturn.set(integerArray.size(), integerToAdd);
		return integerArrayToReturn;
	}

	public static LongArray addToLongArray(LongArray longArray, Long longToAdd, JCas jcas) {
		if (longArray == null) {
			longArray = new LongArray(jcas, 0);
		}
		LongArray longArrayToReturn = new LongArray(jcas, longArray.size() + 1);
		for (int i = 0; i < longArray.size(); i++) {
			longArrayToReturn.set(i, longArray.get(i));
		}
		longArrayToReturn.set(longArray.size(), longToAdd);
		return longArrayToReturn;
	}

	public static FloatArray addToFloatArray(FloatArray floatArray, Float floatToAdd, JCas jcas) {
		if (floatArray == null) {
			floatArray = new FloatArray(jcas, 0);
		}
		FloatArray floatArrayToReturn = new FloatArray(jcas, floatArray.size() + 1);
		for (int i = 0; i < floatArray.size(); i++) {
			floatArrayToReturn.set(i, floatArray.get(i));
		}
		floatArrayToReturn.set(floatArray.size(), floatToAdd);
		return floatArrayToReturn;
	}

	public static int indexOf(IntegerArray intArray, Integer intValue) {
		if (intArray == null) {
			return -1;
		}
		for (int i = 0; i < intArray.size(); i++) {
			if (intArray.get(i) == intValue) {
				return i;
			}
		}
		return -1;
	}
	
	
	public static int indexOf(StringArray strArray, String strValue) {
		if (strArray == null) {
			return -1;
		}
		for (int i = 0; i < strArray.size(); i++) {
			if (strArray.get(i).equals(strValue)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(LongArray longArray, Long longValue) {
		if (longArray == null) {
			return -1;
		}
		for (int i = 0; i < longArray.size(); i++) {
			if (longArray.get(i) == longValue) {
				return i;
			}
		}
		return -1;
	}

	public static IntegerArray removeArrayIndex(IntegerArray intArray, int index, JCas jcas) {
		logger.debug("RemoveArrayIndex: intArray size: " + intArray.size() + " index: " + index);
		IntegerArray updatedArray = null;
		if (intArray != null) {
			if (index < intArray.size()) {
				int newSize = intArray.size() - 1;
				updatedArray = new IntegerArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & intArray.size() > 1) {
						updatedArray.set(i, intArray.get(i + 1));
					} else {
						updatedArray.set(i, intArray.get(i));
					}
				}
			} else {
				updatedArray = intArray;
			}
		}
		return updatedArray;
	}

	
	public static StringArray removeArrayIndex(StringArray strArray, int index, JCas jcas) {
		logger.debug("RemoveArrayIndex: strArray size: " + strArray.size() + " index: " + index);
		StringArray updatedArray = null;
		if (strArray != null) {
			if (index < strArray.size()) {
				int newSize = strArray.size() - 1;
				updatedArray = new StringArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & strArray.size() > 1) {
						updatedArray.set(i, strArray.get(i + 1));
					} else {
						updatedArray.set(i, strArray.get(i));
					}
				}
			} else {
				updatedArray = strArray;
			}
		}
		return updatedArray;
	}

	
	
	public static LongArray removeArrayIndex(LongArray longArray, int index, JCas jcas) {
		LongArray updatedArray = null;
		if (longArray != null) {
			if (index < longArray.size()) {
				int newSize = longArray.size() - 1;
				updatedArray = new LongArray(jcas, newSize);
				for (int i = 0; i < updatedArray.size(); i++) {
					if (i >= index & longArray.size() > 1) {
						updatedArray.set(i, longArray.get(i + 1));
					} else {
						updatedArray.set(i, longArray.get(i));
					}
				}
			} else {
				updatedArray = longArray;
			}
		}
		return updatedArray;
	}

	public static List<CCPSlotMention> getMultipleSlotMentionsByName(CCPTextAnnotation ccpTextAnnotation,
			String slotMentionName) {
		return getMultipleSlotMentionsByName(ccpTextAnnotation.getClassMention(), slotMentionName);
	}

	public static List<CCPSlotMention> getMultipleSlotMentionsByName(CCPClassMention ccpClassMention,
			String slotMentionName) {
		List<CCPSlotMention> returnSlotMentions = new ArrayList<CCPSlotMention>();
		FSArray slotMentionsArray = ccpClassMention.getSlotMentions();
		if (slotMentionsArray != null) {
			FeatureStructure[] slotMentions = slotMentionsArray.toArray();
			for (FeatureStructure fs : slotMentions) {
				CCPSlotMention ccpSlotMention = (CCPSlotMention) fs;
				if (ccpSlotMention.getMentionName().equals(slotMentionName)) {
					returnSlotMentions.add(ccpSlotMention);
				}
			}
			return returnSlotMentions;
		} else {
			return null;
		}
	}

	public static String getFirstSlotValue(CCPStringSlotMention ccpSSM) {
		StringArray slotArray = ccpSSM.getSlotValues();
		if (slotArray != null) {
			String[] slotValues = slotArray.toArray();
			if (slotValues.length > 0) {
				return slotValues[0];
			}
		}
		return null;
	}

	public static Integer getFirstSlotValue(CCPIntegerSlotMention ccpISM) {
		IntegerArray slotArray = ccpISM.getSlotValues();
		if (slotArray != null) {
			int[] slotValues = slotArray.toArray();
			if (slotValues.length > 0) {
				return slotValues[0];
			}
		}
		return null;
	}

	public static Float getFirstSlotValue(CCPFloatSlotMention ccpFSM) {
		FloatArray slotArray = ccpFSM.getSlotValues();
		if (slotArray != null) {
			float[] slotValues = slotArray.toArray();
			if (slotValues.length > 0) {
				return slotValues[0];
			}
		}
		return null;
	}

	public static void addSlotValue(CCPClassMention ccpClassMention, String slotMentionName, String slotValue)
			throws CASException {
		JCas jcas = ccpClassMention.getCAS().getJCas();
		CCPSlotMention slotMention = UIMA_Util.getSlotMentionByName(ccpClassMention, slotMentionName);
		if (slotMention != null) {
			if (slotMention instanceof CCPStringSlotMention) {
				CCPStringSlotMention ccpSSM = (CCPStringSlotMention) slotMention;
				StringArray slotValues = ccpSSM.getSlotValues();
				slotValues = addToStringArray(slotValues, slotValue, jcas);
				ccpSSM.setSlotValues(slotValues);
			} else {
				throw new CASException(new KnowledgeRepresentationWrapperException("Cannot store a String in a "
						+ slotMention.getClass().getName()));
			}
		} else {
			CCPStringSlotMention ccpSSM = new CCPStringSlotMention(jcas);
			ccpSSM.setMentionName(slotMentionName);
			addSlotMention(ccpClassMention, ccpSSM);
			StringArray slotValues = new StringArray(jcas, 1);
			slotValues.set(0, slotValue);
			ccpSSM.setSlotValues(slotValues);
		}

		// CCPStringSlotMention ccpNonComplexSlotMention = (CCPStringSlotMention)
		// UIMA_Util.getSlotMentionByName(ccpClassMention,
		// slotMentionName);
		// if (ccpNonComplexSlotMention == null) { // then we need to create a new
		// // CCPNonComplexSlotMention
		// ccpNonComplexSlotMention = new
		// CCPNonComplexSlotMention(ccpClassMention.getCAS().getJCas());
		// ccpNonComplexSlotMention.setMentionName(slotMentionName);
		// addSlotMention(ccpClassMention, ccpNonComplexSlotMention);
		// }
		// // add the slotvalue to the StringArray
		// addToStringArray(ccpNonComplexSlotMention, slotValue);
	}

	public static void addSlotValue(CCPClassMention ccpClassMention, String slotMentionName,
			CCPClassMention slotFillerCM) throws CASException {
		CCPComplexSlotMention ccpComplexSlotMention = (CCPComplexSlotMention) UIMA_Util.getSlotMentionByName(
				ccpClassMention, slotMentionName);
		if (ccpComplexSlotMention == null) { // then we need to create a new
			// CCPNonComplexSlotMention
			JCas jcas = ccpClassMention.getCAS().getJCas();
			ccpComplexSlotMention = new CCPComplexSlotMention(jcas);
			ccpComplexSlotMention.setMentionName(slotMentionName);
			FSArray classMentions = new FSArray(jcas, 1);
			classMentions.set(0, slotFillerCM);
			ccpComplexSlotMention.setClassMentions(classMentions);
			addSlotMention(ccpClassMention, ccpComplexSlotMention);
		} else {
			addClassMentionAsCSMSlotFiller(ccpComplexSlotMention, slotFillerCM);
		}
	}

	/**
	 * Sets a non-complex slot value (overwrites any previous slot values). Adds a new slot if one
	 * was not there prior
	 * 
	 * @param ccpClassMention
	 * @param slotMentionName
	 * @param slotValue
	 * @throws CASException
	 */
	public static void setSlotValue(CCPClassMention ccpClassMention, String slotMentionName, Object slotValue)
			throws CASException {
		JCas jcas = ccpClassMention.getCAS().getJCas();
		CCPSlotMention ccpSlotMention = UIMA_Util.getSlotMentionByName(ccpClassMention, slotMentionName);
		try {
			if (ccpSlotMention == null) {
				CCPPrimitiveSlotMention newPrimitiveSlotMention = CCPPrimitiveSlotMentionFactory
						.createCCPPrimitiveSlotMention(slotMentionName, slotValue, jcas);
				addSlotMention(ccpClassMention, newPrimitiveSlotMention);
				// CCPStringSlotMention ccpSSM = new CCPStringSlotMention(jcas);
				// ccpSSM.setMentionName(slotMentionName);
				// addSlotMention(ccpClassMention, ccpSSM);
				// StringArray slotValues = new StringArray(jcas, 1);
				// slotValues.set(0, slotValue);
				// ccpSSM.setSlotValues(slotValues);
			} else {
				if (ccpSlotMention instanceof CCPStringSlotMention) {
					if (slotValue instanceof String) {
						CCPStringSlotMention ccpSSM = (CCPStringSlotMention) ccpSlotMention;
						new WrappedCCPStringSlotMention(ccpSSM).overwriteSlotValues((String) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPFloatSlotMention) {
					if (slotValue instanceof Float) {
						CCPFloatSlotMention ccpSSM = (CCPFloatSlotMention) ccpSlotMention;
						new WrappedCCPFloatSlotMention(ccpSSM).overwriteSlotValues((Float) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPIntegerSlotMention) {
					if (slotValue instanceof Integer) {
						CCPIntegerSlotMention ccpSSM = (CCPIntegerSlotMention) ccpSlotMention;
						new WrappedCCPIntegerSlotMention(ccpSSM).overwriteSlotValues((Integer) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else if (ccpSlotMention instanceof CCPBooleanSlotMention) {
					if (slotValue instanceof Boolean) {
						CCPBooleanSlotMention ccpSSM = (CCPBooleanSlotMention) ccpSlotMention;
						ccpSSM.setSlotValue((Boolean) slotValue);
					} else {
						throw new CASException(new KnowledgeRepresentationWrapperException("Cannot use a "
								+ slotValue.getClass().getName() + " as a slot value in a "
								+ ccpSlotMention.getClass().getName()));
					}
				} else {
					throw new CASException(new KnowledgeRepresentationWrapperException(
							"Unknown Primitive Slot Mention type: " + ccpSlotMention.getClass().getName()));
				}
			}
		} catch (KnowledgeRepresentationWrapperException krwe) {
			throw new CASException(krwe);
		} catch (InvalidInputException e) {
			throw new CASException(e);
		}
	}

	// /**
	// * Sets a non-complex slot value (overwrites any previous slot values). Adds a new slot if one
	// * was not there prior
	// *
	// * @param ccpClassMention
	// * @param slotMentionName
	// * @param slotValue
	// * @throws CASException
	// */
	// public static void setSlotValue(CCPClassMention ccpClassMention, String slotMentionName,
	// String slotValue)
	// throws CASException {
	// CCPNonComplexSlotMention ccpNonComplexSlotMention = (CCPNonComplexSlotMention)
	// UIMA_Util.getSlotMentionByName(
	// ccpClassMention, slotMentionName);
	// if (ccpNonComplexSlotMention == null) { // then we need to create a new
	// // CCPNonComplexSlotMention
	// ccpNonComplexSlotMention = new CCPNonComplexSlotMention(ccpClassMention.getCAS().getJCas());
	// ccpNonComplexSlotMention.setMentionName(slotMentionName);
	// addSlotMention(ccpClassMention, ccpNonComplexSlotMention);
	// }
	// StringArray slotValues = new StringArray(ccpNonComplexSlotMention.getCAS().getJCas(), 1);
	// slotValues.set(0, slotValue);
	// ccpNonComplexSlotMention.setSlotValues(slotValues);
	// }

	// private static void addToStringArray(CCPNonComplexSlotMention ccpNonComplexSlotMention,
	// String slotValue)
	// throws CASException {
	// StringArray slotValues = ccpNonComplexSlotMention.getSlotValues();
	// if (slotValues == null) {
	// slotValues = new StringArray(ccpNonComplexSlotMention.getCAS().getJCas(), 1);
	// slotValues.set(0, slotValue);
	// ccpNonComplexSlotMention.setSlotValues(slotValues);
	// } else {
	// String[] previousValues = slotValues.toArray();
	// int index = 0;
	// slotValues = new StringArray(ccpNonComplexSlotMention.getCAS().getJCas(),
	// previousValues.length + 1);
	// for (String prevValue : previousValues) {
	// slotValues.set(index++, prevValue);
	// }
	// slotValues.set(index, slotValue);
	// }
	// ccpNonComplexSlotMention.setSlotValues(slotValues);
	// }

	private static void addClassMentionAsCSMSlotFiller(CCPComplexSlotMention ccpCSM, CCPClassMention ccpCM)
			throws CASException {
		FSArray slotFillerCMs = ccpCSM.getClassMentions();
		if (slotFillerCMs == null) {
			slotFillerCMs = new FSArray(ccpCSM.getCAS().getJCas(), 1);
			slotFillerCMs.set(0, ccpCM);
			ccpCSM.setClassMentions(slotFillerCMs);
		} else {
			FeatureStructure[] featureStructures = slotFillerCMs.toArray();
			FSArray fsArray = new FSArray(ccpCSM.getCAS().getJCas(), featureStructures.length + 1);
			for (int i = 0; i < featureStructures.length; i++) {
				fsArray.set(i, featureStructures[i]);
			}
			fsArray.set(fsArray.size() - 1, ccpCM);
			ccpCSM.setClassMentions(fsArray);
		}
	}

	private static void addSlotMention(CCPClassMention ccpClassMention, CCPSlotMention ccpSlotMention)
			throws CASException {
		FSArray slotMentions = ccpClassMention.getSlotMentions();
		if (slotMentions == null) {
			slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), 1);
			slotMentions.set(0, ccpSlotMention);
		} else {
			FeatureStructure[] featureStructures = slotMentions.toArray();
			int index = 0;
			slotMentions = new FSArray(ccpClassMention.getCAS().getJCas(), featureStructures.length + 1);
			for (FeatureStructure fs : featureStructures) {
				slotMentions.set(index++, fs);
			}
			slotMentions.set(index, ccpSlotMention);
		}
		ccpClassMention.setSlotMentions(slotMentions);
	}

	/**
	 * Create a string that includes all of the spans within this mention. This includes recursively
	 * pulling out slot spans as well.
	 * 
	 * @param mention
	 *            The mention to process
	 * @return A string summarizing all the text spans included in this mention
	 */
	private static String getCCPClassMentionString(CCPClassMention mention) {
		// Create text spans for the base annotation and all the slots
		ArrayList<TextSpan> textSpans = new ArrayList<TextSpan>();
		addClassMentionSpans(textSpans, mention);
		// Assemble the sorted spans into a single string
		StringBuffer sb = new StringBuffer();
		TextSpan previous = null;
		for (TextSpan span : textSpans) {
			if (previous == null) {
				sb.append(span.text);
			} else if ((span.start - previous.end) <= 1) {
				sb.append(" ");
				sb.append(span.text);
			} else {
				sb.append(" ... ");
				sb.append(span.text);
			}
			previous = span;
		}
		return sb.toString();
	}

	/**
	 * Add all of the text spans that take part in a mention into a sorted list of text spans.
	 * 
	 * @param textSpans
	 *            The sorted list of text spans
	 * @param mention
	 *            The mention from which spans are to be extracted
	 * @return The sorted list of text spans
	 */
	private static ArrayList<TextSpan> addClassMentionSpans(ArrayList<TextSpan> textSpans, CCPClassMention mention) {
		// Grab the original document text from the JCas
		String data = null;
		try {
			data = mention.getCAS().getJCas().getDocumentText();
		} catch (CASException e) {
			return textSpans;
		}
		// Get the base annotation for this mention (it should point to a
		// CCPTextAnnotation)
		CCPTextAnnotation annotation = mention.getCcpTextAnnotation();
		// FSArray annotations = mention.getCcpTextAnnotations();
		if ((annotation == null)) {
			return textSpans;
		}

		// Object jcasAnnotation = annotations.get(0);
		// CCPTextAnnotation annotation = null;
		// if (jcasAnnotation instanceof CCPTextAnnotation)
		// annotation = (CCPTextAnnotation) jcasAnnotation;
		// if (annotation == null)
		// return textSpans;
		// Get all the spans from the base annotation
		FSArray spans = annotation.getSpans();
		if (spans == null) {
			sortIn(textSpans, new TextSpan(annotation.getBegin(), annotation.getEnd(), data.substring(annotation
					.getBegin(), annotation.getEnd())));
		} else {
			for (int i = 0; i < spans.size(); i++) {
				Object jcasSpan = spans.get(i);
				CCPSpan span = null;
				if (jcasSpan instanceof CCPSpan) {
					span = (CCPSpan) jcasSpan;
				}
				if (span != null) {
					sortIn(textSpans, new TextSpan(span.getSpanStart(), span.getSpanEnd(), data.substring(span
							.getSpanStart(), span.getSpanEnd())));
				}
			}
		}
		// Now sort in all the spans from the mention slots
		FSArray slots = mention.getSlotMentions();
		if (slots != null) {
			for (int i = 0; i < slots.size(); i++) {
				Object jcasSlotMention = slots.get(i);
				CCPComplexSlotMention slot = null;
				if (jcasSlotMention instanceof CCPComplexSlotMention)
					slot = (CCPComplexSlotMention) jcasSlotMention;
				if (slot != null) {
					FSArray slotClassMentions = slot.getClassMentions();
					for (int j = 0; j < slotClassMentions.size(); j++) {
						Object slotClassMention = slotClassMentions.get(j);
						CCPClassMention classMention = null;
						if (slotClassMention instanceof CCPClassMention)
							classMention = (CCPClassMention) slotClassMention;
						if (classMention != null) {
							addClassMentionSpans(textSpans, classMention);
						}
					}
				}
			}
		}
		// Done
		return textSpans;
	}

	/**
	 * Sort a new text span into a list of text spans.
	 * 
	 * @param spans
	 *            The sorted list of spans
	 * @param span
	 *            The new span to add
	 * @return The sorted list of spans
	 */
	private static ArrayList<TextSpan> sortIn(ArrayList<TextSpan> spans, TextSpan span) {
		if (spans.isEmpty()) {
			spans.add(span);
		} else {
			boolean found = false;
			for (int i = 0; i < spans.size(); i++) {
				if (span.end <= spans.get(i).start) {
					spans.add(i, span);
					found = true;
					break;
				}
			}
			if (!found)
				spans.add(span);
		}
		return spans;
	}

	public static Iterator<CCPTextAnnotation> getAnnotationsWithinSpan(Span span, JCas jcas) {
		return LegacyCollectionsUtil.checkIterator(getAnnotationsWithinSpan(span, jcas, CCPTextAnnotation.type), CCPTextAnnotation.class);
	}

	/**
	 * return all annotations within the exact same span as the input span
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsWithinSpan(Span span, JCas jcas, int annotationType) {

		// System.out.println("Looking for annotations within " + span.getSpanStart() + " -- " +
		// span.getSpanEnd());
		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * is it within the span
		 * 
		 * <pre>
		 *                           ccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth = cf.and(testBegin, testEnd);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas
				.getJFSIndexRepository().getAnnotationIndex(annotationType).iterator(), testBoth);

		return iter;

	}

	public static Iterator<Annotation> getAnnotationsWithinSpan(Span span, JCas jcas, Type annotationType) {

		// System.out.println("Looking for annotations within " + span.getSpanStart() + " -- " +
		// span.getSpanEnd());

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * is it within the span
		 * 
		 * <pre>
		 *                           ccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth = cf.and(testBegin, testEnd);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas
				.getAnnotationIndex(annotationType).iterator(), testBoth);

		return iter;

	}

	public static Iterator<Annotation> getOverlappingAnnotations(CCPTextAnnotation ccpTA, JCas jcas,
			int annotType) {
		Span span = null;
		try {
			span = new Span(ccpTA.getBegin(), ccpTA.getEnd());
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}

		return getAnnotationsEncompassingSpan(span, jcas, annotType);

	}

	public static Iterator<CCPTextAnnotation> getAnnotationsEncompassingSpan(Span span, JCas jcas) {
		return LegacyCollectionsUtil.checkIterator(getAnnotationsEncompassingSpan(span, jcas, CCPTextAnnotation.type), CCPTextAnnotation.class);
	}

	/**
	 * return all annotations that contain the input span
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsEncompassingSpan(Span span, JCas jcas, int annotType) {

		// System.out.println("Looking for annotations overlapping " + span.getSpanStart() + " -- "
		// +
		// span.getSpanEnd());

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint ltEqToSpanStart = cf.createIntConstraint();
		ltEqToSpanStart.leq(span.getSpanStart());
		FSIntConstraint gtSpanStart = cf.createIntConstraint();
		gtSpanStart.gt(span.getSpanStart());
		FSIntConstraint gtEqToSpanStart = cf.createIntConstraint();
		gtEqToSpanStart.geq(span.getSpanStart());

		FSIntConstraint gtEqToSpanEnd = cf.createIntConstraint();
		gtEqToSpanEnd.geq(span.getSpanEnd());
		FSIntConstraint ltSpanEnd = cf.createIntConstraint();
		ltSpanEnd.lt(span.getSpanEnd());
		FSIntConstraint ltEqToSpanEnd = cf.createIntConstraint();
		ltEqToSpanEnd.leq(span.getSpanEnd());

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);
		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it overlap the left
		 * 
		 * <pre>
		 *                      cccccccc
		 *                          ssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin1 = cf.embedConstraint(pathToBeginValue, ltEqToSpanStart);
		FSMatchConstraint testEnd1 = cf.embedConstraint(pathToEndValue, gtSpanStart);
		/**
		 * does it overlap the right
		 * 
		 * <pre>
		 *                        ccccccccc 
		 *                     sssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin2 = cf.embedConstraint(pathToBeginValue, ltSpanEnd);
		FSMatchConstraint testEnd2 = cf.embedConstraint(pathToEndValue, gtEqToSpanEnd);
		/**
		 * is it completely in the middle
		 * 
		 * <pre>
		 *                      cccc 
		 *                    sssssssssss
		 * </pre>
		 */
		FSMatchConstraint testBegin3 = cf.embedConstraint(pathToBeginValue, gtEqToSpanStart);
		FSMatchConstraint testEnd3 = cf.embedConstraint(pathToEndValue, ltEqToSpanEnd);

		/* AND the tests for each of the three cases, then OR the AND'ed tests together */
		FSMatchConstraint testBoth1 = cf.and(testBegin1, testEnd1);
		FSMatchConstraint testBoth2 = cf.and(testBegin2, testEnd2);
		FSMatchConstraint testBoth3 = cf.and(testBegin3, testEnd3);

		FSMatchConstraint testBoth12 = cf.or(testBoth1, testBoth2);
		FSMatchConstraint testBoth123 = cf.or(testBoth12, testBoth3);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas
				.getJFSIndexRepository().getAnnotationIndex(annotType).iterator(), testBoth123);

		return iter;

	}

	/**
	 * return annotations in between spans
	 * 
	 * @param span
	 * @param jcas
	 * @return
	 */
	public static Iterator<CCPTextAnnotation> getAnnotationsInBetweenSpans(Span upstreamSpan, Span downstreamSpan,
			JCas jcas) {

		// System.out.println("Looking for annotations in between " + upstreamSpan.getSpanEnd() +
		// " -- " +
		// downstreamSpan.getSpanStart());

		Span betweenSpan = null;
		try {
			betweenSpan = new Span(upstreamSpan.getSpanEnd(), downstreamSpan.getSpanStart());
		} catch (InvalidSpanException e) {
			logger.error("Invalid span detected.. could be because of split span. These are not handled. ["
					+ upstreamSpan.getSpanEnd() + ".." + downstreamSpan.getSpanStart() + "]");
			return null;
			// e.printStackTrace();
		}

		if (betweenSpan != null) {
			return getAnnotationsWithinSpan(betweenSpan, jcas);
		} else {
			return null;
		}

	}

	/**
	 * return all annotations that start with the input startIndex
	 * 
	 * @param startIndex
	 * @param jcas
	 * @return
	 */
	public static Iterator<Annotation> getAnnotationsWithSameStart(int startIndex, JCas jcas) {

		// System.out.println("Looking for annotations starting at " + startIndex);

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint eqToSpanStart = cf.createIntConstraint();
		eqToSpanStart.eq(startIndex);

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature beginFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_BEGIN);

		/* Create a feature path for each feature */
		FeaturePath pathToBeginValue = cas.createFeaturePath();
		pathToBeginValue.addFeature(beginFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it start at the same index
		 * 
		 * <pre>
		 *                      cccccccc
		 *                      ssssssss
		 * </pre>
		 */
		FSMatchConstraint testStart = cf.embedConstraint(pathToBeginValue, eqToSpanStart);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas
				.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator(), testStart);

		return iter;

	}

	public static Iterator<Annotation> getPrecedingAnnotations(int startIndex, int ccpAnnotationType, JCas jcas) {

		// System.out.println("Looking for annotations ending before  " + startIndex);

		/* Get a reference to the CAS and the CAS ConstraintFactory */
		CAS cas = jcas.getCas();
		ConstraintFactory cf = cas.getConstraintFactory();

		/* Constraints are built from tests and feature-paths */
		/* First build the tests */
		FSIntConstraint ltSpanStart = cf.createIntConstraint();
		ltSpanStart.lt(startIndex);

		/* Get handles to the features, use the type system */
		TypeSystem ts = cas.getTypeSystem();

		Feature endFeature = ts.getFeatureByFullName(CAS.FEATURE_FULL_NAME_END);

		/* Create a feature path for each feature */
		FeaturePath pathToEndValue = cas.createFeaturePath();
		pathToEndValue.addFeature(endFeature);

		/*
		 * Connect the tests to the feature paths (s = the span of the trigger annotation, c = the
		 * span of the phrases and tokens to compare)
		 */

		/**
		 * does it start at the same index
		 * 
		 * <pre>
		 *                      cccccccc
		 *                      ssssssss
		 * </pre>
		 */
		FSMatchConstraint testStart = cf.embedConstraint(pathToEndValue, ltSpanStart);

		/* Create a filtered iterator that uses this constraint */
		Iterator<Annotation> iter = (Iterator<Annotation>) cas.createFilteredIterator(jcas
				.getJFSIndexRepository().getAnnotationIndex(ccpAnnotationType).iterator(), testStart);

		return iter;

	}

	/**
	 * this method resets the span for an annotation.. this includes the Span FSArray
	 * 
	 * @param ccpTA
	 * @param spanStart
	 * @param spanEnd
	 */
	public static void setCCPTextAnnotationSpan(CCPTextAnnotation ccpTA, int spanStart, int spanEnd)
			throws CASException {
		JCas jcas = ccpTA.getCAS().getJCas();

		/* Initialize a new CCPSpan */
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(spanStart);
		ccpSpan.setSpanEnd(spanEnd);

		/* Add the span to the span array */
		FSArray ccpSpans = new FSArray(jcas, 1);
		ccpSpans.set(0, ccpSpan);

		/* Update the text annotation with the new span information */
		ccpTA.setSpans(ccpSpans);
		ccpTA.setBegin(spanStart);
		ccpTA.setEnd(spanEnd);

		// /* update class mention to point to this text annotation */
		// CCPClassMention ccpCM = ccpTA.getClassMention();
		// if (ccpCM != null) {
		// FSArray ccpTAs = new FSArray(jcas, 1);
		// ccpTAs.set(0, ccpTA);
		// ccpCM.setCcpTextAnnotations(ccpTAs);
		// }

	}

	/**
	 * sets the class mention for the CCPTextAnnotation, and also links the CCPTextAnnotation with
	 * the CCPClassMention
	 * 
	 * @param ccpTA
	 * @param ccpCM
	 * @throws CASException
	 */
	public static void setCCPClassMentionForCCPTextAnnotation(CCPTextAnnotation ccpTA, CCPClassMention ccpCM)
			throws CASException {
		ccpTA.setClassMention(ccpCM);
		ccpCM.setCcpTextAnnotation(ccpTA);
		// addCCPTextAnnotationToCCPClassMention(ccpTA, ccpCM);
	}

	/**
	 * This method provides a sanity check when constructing CCPTextAnnotations, and in particular
	 * the class mention structure from scratch. Slots are checked for appropriate filler, and
	 * logger.error messages are raised if an invalid slot filler is discovered. True is returned if
	 * the CCPTextAnnotation has a valid mention structure, false otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPTextAnnotation(CCPTextAnnotation ccpTA) {
		boolean isValid = validateCCPClassMention(ccpTA.getClassMention());
		// UIMA_Util uimaUtil = new UIMA_Util();
		if (!isValid) {
			logger.error("Invalid class mention structure detected in CCPTextAnnotation: "
					+ (new WrappedCCPTextAnnotation(ccpTA)).getSingleLineRepresentation());
		}
		logger.debug("Returning " + isValid + " from validateCCPTextAnnotation() end");
		return isValid;
	}

	/**
	 * This method checks each slot mention associated with the input class mention for valid slot
	 * fillers. True is returned if the CCPClassMention has a valid mention structure, false
	 * otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPClassMention(CCPClassMention ccpCM) {
		boolean isValid = true;

		FSArray shouldBeSlotMentions = ccpCM.getSlotMentions();
		if (shouldBeSlotMentions != null) {
			for (int i = 0; i < shouldBeSlotMentions.size(); i++) {
				Object shouldBeSM = shouldBeSlotMentions.get(i);
				if (shouldBeSM != null) {
					if (shouldBeSM instanceof CCPSlotMention) {
						return validateCCPSlotMention((CCPSlotMention) shouldBeSM);
					} else {
						logger
								.error("Invalid mention structure detected. Unexpected object found in FSArray holding CCPSlotMentions for the CCPClassMention: \""
										+ ccpCM.getMentionName() + "\" -- " + shouldBeSM.getClass().getName());
						logger.debug("Returning false from validateCCPClassMention() mid1");
						return false;
					}
				}
			}
		}

		logger.debug("Returning " + isValid + " from validateCCPClassMention() end");
		return isValid;

	}

	/**
	 * This method determines the type of slot mention inputted, and calls either
	 * validateCCPComplexSlotMention() or validateCCPNonComplexSlotMention()
	 * 
	 * @param ccpSM
	 * @return
	 */
	public static boolean validateCCPSlotMention(CCPSlotMention ccpSM) {
		if (ccpSM instanceof CCPComplexSlotMention) {
			return validateCCPComplexSlotMention((CCPComplexSlotMention) ccpSM);
		} else if (ccpSM instanceof CCPPrimitiveSlotMention) {
			return validateCCPPrimitiveSlotMention((CCPPrimitiveSlotMention) ccpSM);
		} else {
			logger
					.error("The superclass CCPSlotMention was found to occupy a slot. Only subclasses of CCPSlotMention are allowed.");
			logger.debug("Returning false from validateCCPSlotMention() end");
			return false;
		}
	}

	/**
	 * This method checks for valid slot fillers of the input CCPComplexSlotMention, i.e. slot
	 * fillers must be valid CCPClassMentions. True is returned if the CCPClassMention has a valid
	 * mention structure, false otherwise.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPComplexSlotMention(CCPComplexSlotMention ccpCSM) {
		boolean isValid = true;

		FSArray slotValues = ccpCSM.getClassMentions();
		if (slotValues != null) {
			for (int i = 0; i < slotValues.size(); i++) {
				Object shouldBeAClassMention = slotValues.get(i);
				if (shouldBeAClassMention != null) {
					if (shouldBeAClassMention instanceof CCPClassMention) {
						isValid = isValid && validateCCPClassMention((CCPClassMention) shouldBeAClassMention);
					} else {
						logger
								.error("Invalid mention structure discovered. Instead of a CCPClassMention, this slot filler for this CCPComplexSlotMention is a: "
										+ shouldBeAClassMention.getClass().getName());
						logger.debug("Returning false from validateCCPComplexSlotMention()");
						return false;
					}
				}
			}
		}
		logger.debug("Returning " + isValid + " from validateCCPComplexSlotMention() end");
		return isValid;
	}

	/**
	 * This method checks for valid slot fillers of the input CCPNonComplexSlotMention, i.e. slot
	 * fillers must be Strings. This method is not necessary in truth because the StringArray object
	 * of each CCPNonComplexSlotMention will force the slot values to be strings, but is included
	 * for completeness.
	 * 
	 * @param ccpTA
	 * @return
	 */
	public static boolean validateCCPPrimitiveSlotMention(CCPPrimitiveSlotMention ccpNCSM) {
		/*
		 * the CCPPrimitiveSlotMention is forced to hold a certain primitive type, so its mention
		 * structure will always be valid. This method is included only for completeness.
		 */
		return true;
	}

	public static UUID getMentionIDForTraversal(CCPMention mention, UUID traversalID) {
		logger.debug("Requesting mentionID for traversal: " + traversalID + " type: " + mention.getClass().getName());
		StringArray traversalIDs = mention.getTraversalIDs();

		int index = UIMA_Util.indexOf(traversalIDs, traversalID.toString());
		if (index > -1) {
			return UUID.fromString(mention.getTraversalMentionIDs(index));
		}
		logger.debug("Requested mention ID for traversal: " + traversalID
				+ " but did not find one. This is not necessarily an error. Returning null. ");
		return null;
	}

	public static void removeMentionIDForTraversal(CCPMention mention, UUID traversalID, JCas jcas) {
		logger.debug("Removing traversalID: " + traversalID + " type: " + mention.getClass().getName());
		StringArray traversalIDs = mention.getTraversalIDs();
		int index = UIMA_Util.indexOf(traversalIDs, traversalID.toString());
		if (index > -1) {
			logger.debug("Traversal ID is at index: " + index);
			StringArray updatedTraversalIDs = UIMA_Util.removeArrayIndex(traversalIDs, index, jcas);
			mention.setTraversalIDs(updatedTraversalIDs);
			StringArray updatedTraversalMentionIDs = UIMA_Util.removeArrayIndex(mention.getTraversalMentionIDs(), index,
					jcas);
			mention.setTraversalMentionIDs(updatedTraversalMentionIDs);
		}
	}
	
	public static void showCasDebugInfo(JCas jcas, String s) throws CASException {
		System.out.println(">>>====== " + s + "   " + jcas.getViewName());
		Iterator i = jcas.getViewIterator();
		while (i.hasNext()) {
			JCas aCas = (JCas) i.next();
			System.out.println(">>-----" + aCas.getViewName() + " : " + aCas.size()); 
			AnnotationIndex ai = aCas.getAnnotationIndex();
			Iterator annotationIterator =  ai.iterator();
			while (annotationIterator.hasNext()) {
				System.out.println("    " + annotationIterator.next());
			}
			System.out.println("------<<");
		}
		System.out.println("======<<<");
	}

	public static void setMentionIDForTraversal(CCPMention mention, UUID mentionID, UUID traversalID, JCas jcas) {
		logger.debug("Setting traversalID: " + traversalID + " -- mentionID: " + mentionID + " type: "
				+ mention.getClass().getName());
		removeMentionIDForTraversal(mention, traversalID, jcas);
		StringArray traversalMentionIDs = UIMA_Util.addToStringArray(mention.getTraversalMentionIDs(), mentionID.toString(), jcas);
		StringArray traversalIDs = UIMA_Util.addToStringArray(mention.getTraversalIDs(), traversalID.toString(), jcas);
		mention.setTraversalMentionIDs(traversalMentionIDs);
		mention.setTraversalIDs(traversalIDs);
	}
}

/**
 * A local class to hold a span of text along with its document start and end position.
 * 
 * @author Jim Firby
 */
class TextSpan {

	int start = 0;

	int end = 0;

	String text = null;

	TextSpan(int start, int end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;
	}

}
