/*

 * UIMA_Annotation_Util.java 

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;

/**
 * 
 * Some simple utilities for dealing with <code>CCPTextAnnotation</code> objects.
 * 
 * 
 * 
 * @author William A Baumgartner, Jr.
 * 
 * 
 */

public class UIMA_Annotation_Util {

	private static Logger logger = Logger.getLogger(UIMA_Annotation_Util.class);

	public static final boolean DEBUG = false;

	public static CCPTextAnnotation createCCPTextAnnotation(String mentionType, int[] span, JCas jcas) {
		CCPTextAnnotation ccpTA = createCCPTextAnnotationNotIndexed(mentionType, span, jcas);
		ccpTA.addToIndexes();
		return ccpTA;
	}

	public static CCPTextAnnotation createCCPTextAnnotationNotIndexed(String mentionType, int[] span, JCas jcas) {
		CCPTextAnnotation ccpTA = new CCPTextAnnotation(jcas);
		int startIndex = span[0];
		int endIndex = span[1];
		ccpTA.setBegin(startIndex);
		ccpTA.setEnd(endIndex);

		/* create span */
		FSArray ccpSpans = new FSArray(jcas, 1);
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(startIndex);
		ccpSpan.setSpanEnd(endIndex);
		ccpSpans.set(0, ccpSpan);
		ccpTA.setSpans(ccpSpans);

		/* set annotator and annotation set */

		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);

		ccpAnnotator.setAffiliation("N/A");

		ccpAnnotator.setFirstName("Default Annotator");

		ccpAnnotator.setAnnotatorID(-1);

		ccpAnnotator.setLastName("Default Annotator");

		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);

		ccpAnnotationSet.setAnnotationSetID(-1);

		ccpAnnotationSet.setAnnotationSetName("Default Set");

		ccpAnnotationSet.setAnnotationSetDescription("");

		ccpTA.setAnnotator(ccpAnnotator);

		FSArray asets = new FSArray(jcas, 1);

		asets.set(0, ccpAnnotationSet);

		ccpTA.setAnnotationSets(asets);

		ccpTA.setDocumentSectionID(-1);

		CCPClassMention ccpCM = new CCPClassMention(jcas);

		ccpCM.setMentionName(mentionType);

		ccpTA.setClassMention(ccpCM);

		ccpCM.setCcpTextAnnotation(ccpTA);

		return ccpTA;

	}

	public static CCPTextAnnotation createCCPTextAnnotation(String mentionType, int start, int end, JCas jcas) {

		int[] span = new int[2];

		span[0] = start;

		span[1] = end;

		CCPTextAnnotation ccpTA = createCCPTextAnnotation(mentionType, span, jcas);

		return ccpTA;

	}

	public static void addTextAnnotationsToIndex(List<CCPTextAnnotation> listTAs) {

		for (CCPTextAnnotation ccpTA : listTAs) {

			ccpTA.addToIndexes();

		}

	}

	public static CCPTextAnnotation createCCPTextAnnotation(String mentionType, int[] span, JCas jcas,

	CCPAnnotator annotatorInfo, CCPAnnotationSet annotationSetInfo) {

		CCPTextAnnotation ccpTA = new CCPTextAnnotation(jcas);

		int startIndex = span[0];

		int endIndex = span[1];

		ccpTA.setBegin(startIndex);

		ccpTA.setEnd(endIndex);

		/* create span */

		FSArray ccpSpans = new FSArray(jcas, 1);

		CCPSpan ccpSpan = new CCPSpan(jcas);

		ccpSpan.setSpanStart(startIndex);

		ccpSpan.setSpanEnd(endIndex);

		ccpSpans.set(0, ccpSpan);

		ccpTA.setSpans(ccpSpans);

		ccpTA.setAnnotator(annotatorInfo);

		FSArray asets = new FSArray(jcas, 1);

		asets.set(0, annotationSetInfo);

		ccpTA.setAnnotationSets(asets);

		ccpTA.setDocumentSectionID(-1);

		CCPClassMention ccpCM = new CCPClassMention(jcas);

		ccpCM.setMentionName(mentionType);

		ccpTA.setClassMention(ccpCM);

		ccpCM.setCcpTextAnnotation(ccpTA);

		// try {

		// UIMA_Util.addCCPTextAnnotationToCCPClassMention(ccpTA, ccpCM);

		// } catch (CASException e) {

		// e.printStackTrace();

		// }

		ccpTA.addToIndexes();

		return ccpTA;

	}

	public static boolean hasValidSpans(CCPTextAnnotation ccpTA, JCas jcas) {

		try {

			List<Span> spanList = getSpanList(ccpTA);

			int documentTextLength = jcas.getDocumentText().length();

			for (Span span : spanList) {

				if (span.getSpanStart() > documentTextLength || span.getSpanEnd() > documentTextLength) {

					return false;

				}

			}

			return true;

		} catch (KnowledgeRepresentationWrapperException e) {

			e.printStackTrace();

			return false;

		} catch (InvalidSpanException ise) {

			return false;

		}

	}

	/**
	 * 
	 * Adds a span to a CCPTextAnnotation. The aggregate span (begin and end fields) is also
	 * 
	 * updated.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param span
	 * 
	 * @param jcas
	 */

	public static void addSpan(CCPTextAnnotation ccpTA, Span span, JCas jcas) {

		CCPSpan spanToAdd = new CCPSpan(jcas);

		spanToAdd.setSpanStart(span.getSpanStart());

		spanToAdd.setSpanEnd(span.getSpanEnd());

		addSpan(ccpTA, spanToAdd, jcas);

	}

	/**
	 * 
	 * Adds a span to a CCPTextAnnotation. The aggregate span (begin and end fields) is also
	 * 
	 * updated.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param spanToAdd
	 * 
	 * @param jcas
	 */

	public static void addSpan(CCPTextAnnotation ccpTA, CCPSpan spanToAdd, JCas jcas) {

		try {

			FSArray spanList = ccpTA.getSpans();

			if (spanList != null) {

				boolean addedNewSpan = false;

				int updatedIndex = 0;

				FSArray updatedSpanList = new FSArray(jcas, spanList.size() + 1);

				for (int i = 0; i < spanList.size(); i++) {

					CCPSpan span = getExpectedSpan(spanList.get(i));

					if (!addedNewSpan) {

						if (spanToAdd.getSpanStart() < span.getSpanStart()) {

							updatedSpanList.set(updatedIndex++, spanToAdd);

							addedNewSpan = true;

						}

					}

					updatedSpanList.set(updatedIndex++, span);

				}

				if (!addedNewSpan) {

					updatedSpanList.set(updatedIndex, spanToAdd);

				}

				ccpTA.setSpans(updatedSpanList);

			} else {

				spanList = new FSArray(jcas, 1);

				spanList.set(0, spanToAdd);

				ccpTA.setSpans(spanList);

			}

			updateAggregateSpan(ccpTA);

		} catch (KnowledgeRepresentationWrapperException kre) {

			kre.printStackTrace();

		}

	}

	public static void sortSpanList(CCPTextAnnotation ccpTA) {

		JCas jcas;

		try {

			jcas = ccpTA.getCAS().getJCas();

			List<Span> spanList = getSpanList(ccpTA);

			if (spanList != null) {

				Collections.sort(spanList, Span.ASCENDING());

				FSArray sortedSpans = new FSArray(jcas, spanList.size());

				for (int i = 0; i < spanList.size(); i++) {

					sortedSpans.set(i, convertSpan(spanList.get(i), jcas));

				}

				ccpTA.setSpans(sortedSpans);

			}

		} catch (CASException e) {

			e.printStackTrace();

		} catch (KnowledgeRepresentationWrapperException e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * Checks to see if a FeatureStructure that is expected to be a CCPSpan, really is a CCPSpan.
	 * 
	 * 
	 * 
	 * @param possibleSpan
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	private static CCPSpan getExpectedSpan(FeatureStructure possibleSpan)

	throws KnowledgeRepresentationWrapperException {

		if (possibleSpan instanceof CCPSpan) {

			CCPSpan span = (CCPSpan) possibleSpan;

			return span;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Expected CCPSpan in CCPTextAnnotation span list but observed a "

			+ possibleSpan.getClass().getName());

		}

	}

	/**
	 * 
	 * Extracts the aggregate span from the individual CCPSpans in the CCPTextAnnotation span list
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	public static void updateAggregateSpan(CCPTextAnnotation ccpTA) throws KnowledgeRepresentationWrapperException {

		FSArray spanList = ccpTA.getSpans();

		if (spanList != null) {

			int aggregateSpanStart = Integer.MAX_VALUE;

			int aggregateSpanEnd = Integer.MIN_VALUE;

			for (int i = 0; i < spanList.size(); i++) {

				CCPSpan span = getExpectedSpan(spanList.get(i));

				if (span.getSpanStart() < aggregateSpanStart) {

					aggregateSpanStart = span.getSpanStart();

				}

				if (span.getSpanEnd() > aggregateSpanEnd) {

					aggregateSpanEnd = span.getSpanEnd();

				}

			}

			ccpTA.setBegin(aggregateSpanStart);

			ccpTA.setEnd(aggregateSpanEnd);

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Cannot update aggregate span, the CCPTextAnnotation has no span list.");

		}

	}

	/**
	 * 
	 * Clears the span list for the input CCPTextAnnotation. (This should be done on a temporary
	 * 
	 * basis only!!)
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param jcas
	 */

	public static void clearSpanList(CCPTextAnnotation ccpTA, JCas jcas) {

		ccpTA.setSpans(new FSArray(jcas, 0));

		ccpTA.setBegin(-1);

		ccpTA.setEnd(-1);

	}

	public static void setAggregateSpanStart(CCPTextAnnotation ccpTA, int spanStart, JCas jcas)

	throws KnowledgeRepresentationWrapperException, InvalidSpanException {

		ccpTA.setBegin(spanStart);

		CCPSpan leadingSpan = getLeadingSpan(ccpTA);

		if (spanStart < leadingSpan.getSpanEnd()) {

			leadingSpan.setSpanStart(spanStart);

		} else {

			throw new InvalidSpanException(

					"Updating aggregate span start has caused an invalid span. The desired span start offset is to the right of the end of the leading (firat) span for this annotation.");

		}

	}

	public static void setAggregateSpanEnd(CCPTextAnnotation ccpTA, int spanEnd, JCas jcas)

	throws KnowledgeRepresentationWrapperException, InvalidSpanException {

		ccpTA.setEnd(spanEnd);

		CCPSpan trailingSpan = getTrailingSpan(ccpTA);

		if (spanEnd > trailingSpan.getSpanStart()) {

			trailingSpan.setSpanEnd(spanEnd);

		} else {

			throw new InvalidSpanException(

					"Updating aggregate span end has caused an invalid span. The desired span end offset is to the left of the start of the trailing (last) span for this annotation.");

		}

	}

	/**
	 * 
	 * Returns the left-most CCPSpan in the span list.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	private static CCPSpan getLeadingSpan(CCPTextAnnotation ccpTA) throws KnowledgeRepresentationWrapperException {

		FSArray spanList = ccpTA.getSpans();

		if (spanList != null && spanList.size() > 0) {

			CCPSpan leadingSpan = getExpectedSpan(spanList.get(0));

			for (int i = 1; i < spanList.size(); i++) {

				CCPSpan span = getExpectedSpan(spanList.get(i));

				if (span.getSpanStart() < leadingSpan.getSpanStart()) {

					leadingSpan = span;

				}

			}

			return leadingSpan;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Cannot get leading span, the CCPTextAnnotation has no span list.");

		}

	}

	/**
	 * 
	 * Returns the left-most CCPSpan in the span list.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	private static CCPSpan getTrailingSpan(CCPTextAnnotation ccpTA) throws KnowledgeRepresentationWrapperException {

		FSArray spanList = ccpTA.getSpans();

		if (spanList != null && spanList.size() > 0) {

			CCPSpan trailingSpan = getExpectedSpan(spanList.get(0));

			for (int i = 1; i < spanList.size(); i++) {

				CCPSpan span = getExpectedSpan(spanList.get(i));

				if (span.getSpanEnd() > trailingSpan.getSpanEnd()) {

					trailingSpan = span;

				}

			}

			return trailingSpan;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Cannot get leading span, the CCPTextAnnotation has no span list.");

		}

	}

	/**
	 * 
	 * Returns a list of Spans for the input CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	public static List<Span> getSpanList(CCPTextAnnotation ccpTA) throws KnowledgeRepresentationWrapperException {

		List<Span> spanListToReturn = new ArrayList<Span>();

		FSArray spanList = ccpTA.getSpans();

		if (spanList != null && spanList.size() > 0) {

			for (int i = 0; i < spanList.size(); i++) {

				CCPSpan ccpSpan = getExpectedSpan(spanList.get(i));

				spanListToReturn.add(convertSpan(ccpSpan));

			}

			return spanListToReturn;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Cannot return list of spans, the CCPTextAnnotation has no span list.");

		}

	}

	/**
	 * 
	 * Returns the Span equivalent of a CCPSpan
	 * 
	 * 
	 * 
	 * @param ccpSpan
	 * 
	 * @return
	 */

	public static Span convertSpan(CCPSpan ccpSpan) {

		return new Span(ccpSpan.getSpanStart(), ccpSpan.getSpanEnd());

	}

	public static CCPSpan convertSpan(Span span, JCas jcas) {

		CCPSpan ccpSpan = new CCPSpan(jcas);

		ccpSpan.setSpanStart(span.getSpanStart());

		ccpSpan.setSpanEnd(span.getSpanEnd());

		return ccpSpan;

	}

	/**
	 * 
	 * Offsets the current span of the input CCPTextAnnotation by the input offset amount. "Offset"
	 * 
	 * is in terms of characters.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param offset
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	public static void offsetSpans(CCPTextAnnotation ccpTA, int offset) throws KnowledgeRepresentationWrapperException {

		FSArray spanList = ccpTA.getSpans();

		if (spanList != null) {

			for (int i = 0; i < spanList.size(); i++) {

				CCPSpan span = getExpectedSpan(spanList.get(i));

				span.setSpanStart(span.getSpanStart() + offset);

				span.setSpanEnd(span.getSpanEnd() + offset);

			}

			updateAggregateSpan(ccpTA);

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Cannot offset the annotation span(s), the CCPTextAnnotation has no span list.");

		}

	}

	/**
	 * 
	 * Updates the annotation sets for the input CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param annotationSets
	 * 
	 * @param jcas
	 */

	public static void setAnnotationSets(CCPTextAnnotation ccpTA, Set<AnnotationSet> annotationSets, JCas jcas) {

		FSArray updatedAnnotationSets = new FSArray(jcas, annotationSets.size());

		int index = 0;

		for (AnnotationSet aSet : annotationSets) {

			CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);

			UIMA_Util.swapAnnotationSetInfo(aSet, ccpAnnotationSet);

			updatedAnnotationSets.set(index++, ccpAnnotationSet);

		}

		ccpTA.setAnnotationSets(updatedAnnotationSets);

	}

	/**
	 * 
	 * Sets the annotator for the input CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param annotator
	 * 
	 * @param jcas
	 */

	public static void setAnnotator(CCPTextAnnotation ccpTA, Annotator annotator, JCas jcas) {

		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);

		UIMA_Util.swapAnnotatorInfo(annotator, ccpAnnotator);

		ccpTA.setAnnotator(ccpAnnotator);

	}

	/**
	 * 
	 * Returns the AnnotationMetaData object for a CCPTextAnnotation. If one does not exist, a new
	 * 
	 * AnnotationMetadata is initialized and added to the CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 * 
	 * @throws CASException
	 */

	public static AnnotationMetadata getAnnotationMetadata(CCPTextAnnotation ccpTA, JCas jcas) {

		AnnotationMetadata metaData = ccpTA.getAnnotationMetadata();

		if (metaData == null) {

			metaData = new AnnotationMetadata(jcas);

			ccpTA.setAnnotationMetadata(metaData);

		}

		return metaData;

	}

	/**
	 * 
	 * Returns the edu.uchsc.ccp.util.nlp.annotation.metadata.AnnotationMetadata object for a
	 * 
	 * CCPTextAnnotation.
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 */

	public static edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata getUtilAnnotationMetadata(

	CCPTextAnnotation ccpTA, JCas jcas) {

		AnnotationMetadata metaData = getAnnotationMetadata(ccpTA, jcas);

		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata utilMetaData = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata();

		UIMA_Util.swapAnnotationMetadata(metaData, utilMetaData, jcas);

		return utilMetaData;

	}

	/**
	 * 
	 * Adds a collection of meta data properties to an annotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param propertiesToAdd
	 * 
	 * @param jcas
	 * 
	 * @throws CASException
	 */

	public static void addMetaDataProperties(CCPTextAnnotation ccpTA, Collection<TOP> propertiesToAdd, JCas jcas) {

		AnnotationMetadata metaData = getAnnotationMetadata(ccpTA, jcas);

		FSArray properties = metaData.getMetadataProperties();

		FSArray updatedProperties = UIMA_Util.addToFSArray(properties, propertiesToAdd, jcas);

		metaData.setMetadataProperties(updatedProperties);

	}

	/**
	 * 
	 * Adds a single meta data property to an annotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param propertyToAdd
	 * 
	 * @param jcas
	 * 
	 * @throws CASException
	 */

	public static void addMetaDataProperty(CCPTextAnnotation ccpTA, AnnotationMetadataProperty propertyToAdd, JCas jcas) {

		Collection<TOP> annotationPropertiesToAdd = new ArrayList<TOP>();

		annotationPropertiesToAdd.add(propertyToAdd);

		addMetaDataProperties(ccpTA, annotationPropertiesToAdd, jcas);

	}

	/**
	 * 
	 * Adds an annotation comment property
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @param comment
	 * 
	 * @param jcas
	 */

	public static void addAnnotationCommentProperty(CCPTextAnnotation ccpTA, String comment, JCas jcas) {

		AnnotationCommentProperty prop = new AnnotationCommentProperty(jcas);

		prop.setComment(comment);

		addMetaDataProperty(ccpTA, prop, jcas);

	}

	public static String getAnnotationCommentPropertyValue(CCPTextAnnotation ccpTA, JCas jcas) {

		return getUtilAnnotationMetadata(ccpTA, jcas).getAnnotationComment();

	}

	/**
	 * 
	 * Returns a Collection of a specific annotation property type
	 * 
	 * 
	 * 
	 * @param <T>
	 * 
	 * @param ccpTA
	 * 
	 * @param annotationPropertyClass
	 * 
	 * @param jcas
	 * 
	 * @return
	 */

	public static <T extends AnnotationMetadataProperty> Collection<T> getAnnotationProperties(CCPTextAnnotation ccpTA,

	Class<T> annotationPropertyClass, JCas jcas) {

		// int returnType = getFeatureStructureType(annotationPropertyClass);

		Collection<T> annotationPropertiesToReturn = new ArrayList<T>();

		AnnotationMetadata metaData = getAnnotationMetadata(ccpTA, jcas);

		FSArray annotationProperties = metaData.getMetadataProperties();

		if (annotationProperties != null) {

			for (int i = 0; i < annotationProperties.size(); i++) {

				FeatureStructure fs = annotationProperties.get(i);

				if (annotationPropertyClass.isAssignableFrom(fs.getClass())) {

					annotationPropertiesToReturn.add(annotationPropertyClass.cast(fs));

				}

			}

		}

		return annotationPropertiesToReturn;

	}

	public static <T extends AnnotationMetadataProperty> Collection<T> getAnnotationProperties(CCPTextAnnotation ccpTA,

	Class<T> annotationPropertyClass) {

		try {

			JCas jcas = ccpTA.getCAS().getJCas();

			return getAnnotationProperties(ccpTA, annotationPropertyClass, jcas);

		} catch (CASException e) {

			e.printStackTrace();

			return null;

		}

	}

	// private static <T extends FeatureStructure> int getFeatureStructureType(Class<T> fsClass) {

	// int type;

	// try {

	// type = fsClass.getField("type").getInt(null);

	// } catch (IllegalArgumentException e) {

	// throw new RuntimeException(e);

	// } catch (SecurityException e) {

	// throw new RuntimeException(e);

	// } catch (IllegalAccessException e) {

	// throw new RuntimeException(e);

	// } catch (NoSuchFieldException e) {

	// throw new RuntimeException(e);

	// }

	// return type;

	// }

	/*
	 * 
	 * This method removes blank lines from the beginning and end of the input paragraph
	 */

	public static void removeLeadingAndTrailingBlankLines(Annotation annotation) {

		Matcher matcher;

		Pattern blankLineAtStartPattern = Pattern.compile("^(\\n+)");

		matcher = blankLineAtStartPattern.matcher(annotation.getCoveredText());

		if (matcher.find()) {

			if (DEBUG) {

				System.out

						.print("DEBUG -- UIMA_Annotation_Util.removeLeadingAndTrailingBlankLines: Removing blank line at start of annotation. Start index before removal: "

								+ annotation.getBegin() + "  Start index after removal: ");

			}

			annotation.setBegin(matcher.end(1));

			if (DEBUG) {

				System.out.println(annotation.getBegin());

			}

		}

		Pattern blankLineAtEndPattern = Pattern.compile("(\\n+)$");

		matcher = blankLineAtEndPattern.matcher(annotation.getCoveredText());

		if (matcher.find()) {

			if (DEBUG) {

				System.out

						.print("DEBUG -- UIMA_Annotation_Util.removeLeadingAndTrailingBlankLines: Removing blank line at end of annotation. End index before removal: "

								+ annotation.getEnd() + "  End index after removal: ");

			}

			annotation.setEnd(matcher.start(1));

			if (DEBUG) {

				System.out.println(annotation.getEnd());

			}

		}

	}

	/**
	 * 
	 * trims spaces and &nbsp; from ends of a CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @throws CASException
	 */

	public static void trimCCPTextAnnotation(CCPTextAnnotation ccpTA) throws CASException {

		/* trim beginning */

		int startIndex = 0;

		String coveredText = ccpTA.getCoveredText();

		String tempText = coveredText;

		int endIndex = coveredText.length();

		int count = 0;

		while ((tempText.startsWith(" ") || tempText.startsWith("&nbsp;")) & count < 10) {

			if (tempText.startsWith(" ")) {

				if (DEBUG) {

					System.err.println("Trimming space from beginning...");

				}

				startIndex++;

			} else if (tempText.startsWith("&nbsp;")) {

				if (DEBUG) {

					System.err.println("Trimming &nbsp; from beginning...");

				}

				startIndex += 6;

			} else {

				if (DEBUG) {

					System.err.println("neither space nor &nbsp; so the loop should break...");

				}

			}

			tempText = coveredText.substring(startIndex, endIndex);

			if (DEBUG) {

				System.err.println("NEW TEMP TEXT =" + tempText + ";;");

			}

			count++;

		}

		count = 0;

		/* trim end */

		while ((tempText.endsWith(" ") || tempText.endsWith("&nbsp;")) & count < 10) {

			if (tempText.endsWith(" ")) {

				if (DEBUG) {

					System.err.println("Trimming space from end...");

				}

				endIndex--;

			} else if (tempText.endsWith("&nbsp;")) {

				if (DEBUG) {

					System.err.println("Trimming space from end...");

				}

				endIndex -= 6;

			} else {

				if (DEBUG) {

					System.err.println("neither space nor &nbsp; so the loop should break...");

				}

			}

			tempText = coveredText.substring(startIndex, endIndex);

			if (DEBUG) {

				System.err.println("NEW TEMP TEXT =" + tempText + ";;");

			}

			count++;

		}

		int offset = ccpTA.getBegin();

		UIMA_Util.setCCPTextAnnotationSpan(ccpTA, startIndex + offset, endIndex + offset);

	}

	public static boolean overlaps(CCPTextAnnotation ccpTA1, CCPTextAnnotation ccpTA2) {

		int s0start = ccpTA1.getBegin();

		int s0end = ccpTA1.getEnd();

		int s1start = ccpTA2.getBegin();

		int s1end = ccpTA2.getEnd();

		if ((s0start >= s1start & s0start < s1end) | (s0end > s1start & s0end <= s1end)

		| (s0start <= s1start & s0end > s1start) | (s0end >= s1end & s0start < s1end)) {

			return true;

		} else {

			return false;

		}

	}

	// /**

	// * Returns a String representation of a CCPTextAnnotation object <br>

	// *

	// * @param ccpTA

	// * @param deep

	// * - if false, it only prints the text annotation information, e.g. span, covered text

	// * @param anonymize

	// * - if true, meta information such as annotator, annotation sets, etc. are not included in

	// the output

	// * @return

	// */

	// public static String toString(CCPTextAnnotation ccpTA, boolean deep, boolean anonymize) {

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

	//

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

	// if (anonymize) {

	// repStr += (spansStr + "|\"" + coveredText + "\"");

	// } else {

	// repStr += (spansStr + "|\"" + coveredText + "\"" + "|" + lastName + "|" + annotationSetStr);

	// }

	//

	// if (deep) {

	// repStr += (" -- " + toString(ccpTA.getClassMention(), anonymize));

	// }

	// return repStr;

	// }

	//

	// public static String toString(CCPClassMention ccpCM, boolean anonymize) {

	// if (ccpCM != null) {

	// CCPTextAnnotation associatedTextAnnotation = ccpCM.getCcpTextAnnotation();

	//

	// String repStr = " [" + ccpCM.getMentionName() + "] ";

	// if (associatedTextAnnotation == null) {

	// logger.error("FOUND ClassMention without a reference to its TextAnnotation...");

	// UIMA_Util.printCCPClassMention(ccpCM, System.err, 0);

	// return null;

	// } else {

	// repStr += toString(associatedTextAnnotation, false, anonymize) + "(";

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

	// repStr += toString(sm, anonymize);

	// }

	//

	// repStr += ")";

	// return repStr;

	// } else {

	// return null;

	// }

	// }

	//

	// /**

	// * Prints a CCPNonComplexSlotMention if it has slot values, otherwise returns an empty String.

	// *

	// * @param ccpNCSM

	// * @return

	// */

	// public static String toString(CCPNonComplexSlotMention ccpNCSM) {

	// String repStr = "";

	// StringArray slotValues = ccpNCSM.getSlotValues();

	// if (slotValues != null && slotValues.size() > 0) {

	// repStr = " [" + ccpNCSM.getMentionName() + "]:";

	//

	// /* get a sorted list of the slot values */

	// List<String> slotValuesList = Arrays.asList(slotValues.toArray());

	// Collections.sort(slotValuesList);

	//

	// for (String slotValue : slotValuesList) {

	// repStr += slotValue + "|";

	// }

	// /* remove trailing pipe */

	// repStr = repStr.substring(0, repStr.length() - 1);

	// }

	// return repStr;

	// }

	//

	// /**

	// * Returns a String representation of a CCPComplexSlotMention if it has any slots, an empty

	// String otherwise

	// *

	// * @param ccpCSM

	// * @return

	// */

	// public static String toString(CCPComplexSlotMention ccpCSM, boolean anonymize) {

	// String repStr = "";

	// FSArray classMentions = ccpCSM.getClassMentions();

	// if (classMentions != null && classMentions.size() > 0) {

	// repStr = " [" + ccpCSM.getMentionName() + "]:";

	//

	// Map<String, CCPClassMention> classMentionKey2MentionMap = new HashMap<String,

	// CCPClassMention>();

	// List<String> classMentionKeys = new ArrayList<String>();

	//

	// for (int i = 0; i < classMentions.size(); i++) {

	// CCPClassMention ccpCM = (CCPClassMention) classMentions.get(i);

	// String key = toString(ccpCM, anonymize);

	// classMentionKey2MentionMap.put(key, ccpCM);

	// classMentionKeys.add(key);

	// }

	//

	// Collections.sort(classMentionKeys);

	//

	// for (String key : classMentionKeys) {

	// CCPClassMention ccpCM = classMentionKey2MentionMap.get(key);

	// CCPTextAnnotation associatedTextAnnotation = ccpCM.getCcpTextAnnotation();

	// if (associatedTextAnnotation != null) {

	// repStr += toString(associatedTextAnnotation, true, anonymize);

	// }

	// }

	// }

	// return repStr;

	//

	// }

	//

	// private static String toString(CCPSlotMention ccpSM, boolean anonymize) {

	// if (ccpSM instanceof CCPNonComplexSlotMention) {

	// return toString((CCPNonComplexSlotMention) ccpSM);

	// } else if (ccpSM instanceof CCPComplexSlotMention) {

	// return toString((CCPComplexSlotMention) ccpSM, anonymize);

	// } else {

	// logger.error("Expecting CCPNonComplexSlotMention or CCPComplexSlotMention but instead got " +

	// ccpSM.getClass().getName());

	// return null;

	// }

	// }

	//

	// /**

	// * Returns a list of slot mentions sorted such that non-complex slot mentions are returned

	// before complex slot

	// * mentions, and within each group the slots are returned in alphabetical order.

	// *

	// * @param slotMentions

	// * @return

	// */

	// private static List<CCPSlotMention> sortSlotMentions(List<CCPSlotMention> slotMentions) {

	// List<CCPSlotMention> sortedCSMList = new ArrayList<CCPSlotMention>();

	// List<CCPSlotMention> sortedSMList = new ArrayList<CCPSlotMention>();

	//

	// Map<String, CCPSlotMention> inputCSMHash = new HashMap<String, CCPSlotMention>();

	// Map<String, CCPSlotMention> inputSMHash = new HashMap<String, CCPSlotMention>();

	//

	// /* divide the slot mentions into complex and non-complex */

	// for (CCPSlotMention ment : slotMentions) {

	// if (ment instanceof CCPComplexSlotMention) {

	// inputCSMHash.put(toString(ment, true), ment);

	// } else if (ment instanceof CCPNonComplexSlotMention) {

	// inputSMHash.put(toString(ment, true), ment);

	// } else {

	// logger.error("Expecting sm or csm in uima_util.sortSLotMentions!!!!! Invalid Mention structure. ");

	// }

	// }

	//

	// /* sort the complex slot mentions */

	// Set<String> keys = inputCSMHash.keySet();

	// List<String> sortedKeys = Arrays.asList(keys.toArray(new String[0]));

	// Collections.sort(sortedKeys);

	// for (String key : sortedKeys) {

	// sortedCSMList.add(inputCSMHash.get(key));

	// }

	//

	// /* sort the non-complex slot mentions */

	// keys = inputSMHash.keySet();

	// sortedKeys = Arrays.asList(keys.toArray(new String[0]));

	// Collections.sort(sortedKeys);

	// for (String key : sortedKeys) {

	// sortedSMList.add(inputSMHash.get(key));

	// }

	//

	// /* add the complex slot mentions to the end of the non-complex slot mentions list and return

	// */

	// sortedSMList.addAll(sortedCSMList);

	// return sortedSMList;

	//

	// }

	/**
	 * 
	 * Given a list of CCPTextAnnotation objects, this method returns a list of duplicate
	 * 
	 * annotations. The meta-data, e.g. annotator, annotation set, is not considered when
	 * 
	 * determining duplicates.
	 * 
	 * 
	 * 
	 * @param ccpTextAnnotations
	 * 
	 * @return
	 * 
	 * @throws InvalidInputException
	 */

	public static Collection<CCPTextAnnotation> getRedundantAnnotations(List<CCPTextAnnotation> ccpTextAnnotations)

	throws InvalidInputException {

		Collection<TextAnnotation> wrappedAnnotations = convertToWrappedAnnotationList(ccpTextAnnotations);

		Collection<TextAnnotation> redundantAnnotations = TextAnnotationUtil

		.getRedundantAnnotations(wrappedAnnotations);

		return convertFromWrappedAnnotationList(redundantAnnotations);

		//

		// Set<String> alreadyObservedAnnotations = new HashSet<String>();

		// List<CCPTextAnnotation> duplicateAnnotations = new ArrayList<CCPTextAnnotation>();

		// boolean deep = true;

		// boolean anonymize = true;

		// for (CCPTextAnnotation ccpTA : ccpTextAnnotations) {

		// String stringRep = toString(ccpTA, deep, anonymize);

		// if (!alreadyObservedAnnotations.contains(stringRep)) {

		// alreadyObservedAnnotations.add(stringRep);

		// } else {

		// duplicateAnnotations.add(ccpTA);

		// }

		// }

		//

		// return duplicateAnnotations;

	}

	public static Collection<CCPTextAnnotation> removeDuplicateAnnotations(List<CCPTextAnnotation> ccpTextAnnotations) {

		Collection<TextAnnotation> wrappedAnnotations = convertToWrappedAnnotationList(ccpTextAnnotations);

		Collection<TextAnnotation> uniqueAnnotations = TextAnnotationUtil

		.removeDuplicateAnnotations(wrappedAnnotations);

		return convertFromWrappedAnnotationList(uniqueAnnotations);

	}

	/**
	 * 
	 * Converts a list of CCPTextAnnotation objects to a list of WrappedCCPTextAnnotation objects
	 * 
	 * 
	 * 
	 * @param ccpTextAnnotations
	 * 
	 * @return
	 */

	public static Collection<TextAnnotation> convertToWrappedAnnotationList(

	Collection<CCPTextAnnotation> ccpTextAnnotations) {

		Collection<TextAnnotation> wrappedList = new ArrayList<TextAnnotation>();

		for (CCPTextAnnotation ccpTA : ccpTextAnnotations) {

			wrappedList.add(new WrappedCCPTextAnnotation(ccpTA));

		}

		return wrappedList;

	}

	/**
	 * 
	 * Converts a list of WrappedCCPTextAnnotation objects to a list of CCPTextAnnotation objects
	 * 
	 * 
	 * 
	 * @param ccpTextAnnotations
	 * 
	 * @return
	 * 
	 * @throws InvalidInputException
	 */

	public static Collection<CCPTextAnnotation> convertFromWrappedAnnotationList(

	Collection<TextAnnotation> wrappedCcpTextAnnotations) throws InvalidInputException {

		Collection<CCPTextAnnotation> unWrappedList = new ArrayList<CCPTextAnnotation>();

		for (TextAnnotation wrappedCcpTA : wrappedCcpTextAnnotations) {

			if (wrappedCcpTA instanceof WrappedCCPTextAnnotation) {

				unWrappedList.add(((WrappedCCPTextAnnotation) wrappedCcpTA).getWrappedObject());

			} else {

				throw new InvalidInputException("Expected WrappedCCPTextAnnotation but found: "

				+ wrappedCcpTA.getClass().getName());

			}

		}

		return unWrappedList;

	}

	// /**

	// * Creates a comparator for CCPTextAnnotations based on the span of each annotation.

	// *

	// * @param <T>

	// * @return

	// */

	// public static <T extends CCPTextAnnotation> Comparator<T> SORT_BY_SPAN() {

	//

	// return new Comparator<T>() {

	//

	// public int compare(T ccpTA1, T ccpTA2) {

	//

	// Integer spanStart1 = ccpTA1.getBegin();

	// Integer spanStart2 = ccpTA2.getBegin();

	//

	// int result = spanStart1.compareTo(spanStart2);

	//

	// if (result == 0) {

	// Integer spanEnd1 = ccpTA1.getEnd();

	// Integer spanEnd2 = ccpTA2.getEnd();

	// return spanEnd1.compareTo(spanEnd2);

	// } else {

	// return result;

	// }

	// }

	// };

	// }

	/**
	 * 
	 * Returns the Annotator equivalent of the CCPAnnotator for the input CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 */

	public static Annotator getAnnotator(CCPTextAnnotation ccpTA) {

		Annotator annotator = new Annotator(1, "", "", "");

		UIMA_Util.swapAnnotatorInfo(ccpTA.getAnnotator(), annotator);

		return annotator;

	}

	/**
	 * 
	 * Returns a Set of AnnotationSet objects for the input CCPTextAnnotation
	 * 
	 * 
	 * 
	 * @param ccpTA
	 * 
	 * @return
	 */

	public static Set<AnnotationSet> getAnnotationSets(CCPTextAnnotation ccpTA) {

		Set<AnnotationSet> annotationSetsToReturn = new HashSet<AnnotationSet>();

		FSArray ccpAnnotationSets = ccpTA.getAnnotationSets();

		if (ccpAnnotationSets != null) {

			for (int i = 0; i < ccpAnnotationSets.size(); i++) {

				AnnotationSet aSet = new AnnotationSet();

				try {

					UIMA_Util.swapAnnotationSetInfo(getExpectedAnnotationSet(ccpAnnotationSets.get(i)), aSet);

				} catch (KnowledgeRepresentationWrapperException e) {

					e.printStackTrace();

				}

				annotationSetsToReturn.add(aSet);

			}

		}

		return annotationSetsToReturn;

	}

	/**
	 * 
	 * Checks to see if a FeatureStructure that is expected to be a CCPAnnotationSet, really is a
	 * 
	 * CCPAnnotationSet.
	 * 
	 * 
	 * 
	 * @param possibleAnnotationSet
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	private static CCPAnnotationSet getExpectedAnnotationSet(FeatureStructure possibleAnnotationSet)

	throws KnowledgeRepresentationWrapperException {

		if (possibleAnnotationSet instanceof CCPAnnotationSet) {

			CCPAnnotationSet ccpAnnotationSet = (CCPAnnotationSet) possibleAnnotationSet;

			return ccpAnnotationSet;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Expected CCPAnnotationSet in CCPTextAnnotation annotation set list but observed a "

			+ possibleAnnotationSet.getClass().getName());

		}

	}

	/**
	 * 
	 * Checks to see if a FeatureStructure that is expected to be a CCPAnnotationSet, really is a
	 * 
	 * CCPAnnotationSet.
	 * 
	 * 
	 * 
	 * @param possibleClassMention
	 * 
	 * @return
	 * 
	 * @throws KnowledgeRepresentationWrapperException
	 */

	public static CCPClassMention getExpectedClassMention(FeatureStructure possibleClassMention)

	throws KnowledgeRepresentationWrapperException {

		if (possibleClassMention instanceof CCPClassMention) {

			CCPClassMention ccpCM = (CCPClassMention) possibleClassMention;

			return ccpCM;

		} else {

			throw new KnowledgeRepresentationWrapperException(

			"Expected CCPClassMention in class mention list but observed a "

			+ possibleClassMention.getClass().getName());

		}

	}

	/**
	 * 
	 * This method removes all CCPNonComplexSlotMentions from the input CCPClassMention
	 * 
	 * 
	 * 
	 * @param ccpCM
	 * 
	 * @param jcas
	 */

	public static void removePrimitiveSlotMentions(CCPClassMention ccpCM, JCas jcas) {

		List<FeatureStructure> slotsToKeep = new ArrayList<FeatureStructure>();

		FSArray slotMentions = ccpCM.getSlotMentions();

		if (slotMentions != null) {

			for (int i = 0; i < slotMentions.size(); i++) {

				if (!(slotMentions.get(i) instanceof CCPPrimitiveSlotMention)) {

					slotsToKeep.add(slotMentions.get(i));

				}

			}

		}

		ccpCM.setSlotMentions(UIMA_Util.listToFsarray(slotsToKeep, jcas));

	}

	/**
	 * 
	 * This method removes all CCPComplexSlotMentions from the input CCPClassMention
	 * 
	 * 
	 * 
	 * @param ccpCM
	 * 
	 * @param jcas
	 */

	public static void removeComplexSlotMentions(CCPClassMention ccpCM, JCas jcas) {

		List<FeatureStructure> slotsToKeep = new ArrayList<FeatureStructure>();

		FSArray slotMentions = ccpCM.getSlotMentions();

		if (slotMentions != null) {

			for (int i = 0; i < slotMentions.size(); i++) {

				if (!(slotMentions.get(i) instanceof CCPComplexSlotMention)) {

					slotsToKeep.add(slotMentions.get(i));

				}

			}

		}

		ccpCM.setSlotMentions(UIMA_Util.listToFsarray(slotsToKeep, jcas));

	}

	
	public static CCPTextAnnotation createCCPTextAnnotationNoDups(String mentionType, int[] span, JCas jcas) {
        CCPTextAnnotation ccpTA = new CCPTextAnnotation(jcas);

        int startIndex = span[0];
        int endIndex = span[1];

        ccpTA.setBegin(startIndex);
        ccpTA.setEnd(endIndex);

        /* create span */
        FSArray ccpSpans = new FSArray(jcas, 1);
        CCPSpan ccpSpan = new CCPSpan(jcas);
        ccpSpan.setSpanStart(startIndex);
        ccpSpan.setSpanEnd(endIndex);
        ccpSpans.set(0, ccpSpan);
        ccpTA.setSpans(ccpSpans);

        /* set annotator and annotation set */
        CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
        ccpAnnotator.setAffiliation("N/A");
        ccpAnnotator.setFirstName("Default Annotator");
        ccpAnnotator.setAnnotatorID(-1);
        ccpAnnotator.setLastName("Default Annotator");

        CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
        ccpAnnotationSet.setAnnotationSetID(-1);
        ccpAnnotationSet.setAnnotationSetName("Default Set");
        ccpAnnotationSet.setAnnotationSetDescription("");

        ccpTA.setAnnotator(ccpAnnotator);
        FSArray asets = new FSArray(jcas, 1);
        asets.set(0, ccpAnnotationSet);
        ccpTA.setAnnotationSets(asets);

        ccpTA.setDocumentSectionID(-1);

        CCPClassMention ccpCM = new CCPClassMention(jcas);
        ccpCM.setMentionName(mentionType);

        ccpTA.setClassMention(ccpCM);
        ccpCM.setCcpTextAnnotation(ccpTA);
       

        boolean deep = true;
        boolean anonymize = true;
        
        WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation(ccpTA);
//        String ccpTA_string = toString(ccpTA, deep, anonymize);

        FSIterator completeIt = jcas.getAnnotationIndex().iterator();
        completeIt.moveTo(ccpTA);
        if ( completeIt.isValid() ) {
                if (completeIt.get() instanceof CCPTextAnnotation) {
                        CCPTextAnnotation ccpTA_comp = (CCPTextAnnotation)completeIt.get();
                        WrappedCCPTextAnnotation wrappedCcpTa_comp = new WrappedCCPTextAnnotation(ccpTA_comp);
                        
//                        String stringRep = toString(ccpTA_comp, deep, anonymize);
                        if (wrappedCcpTa.equals(wrappedCcpTa_comp)) {
                                return null;
                        } else {
                                ccpTA.addToIndexes();
                                return ccpTA;
                        }
                }
        } else {
                ccpTA.addToIndexes();
                return ccpTA;
        }

        return null;
}
	
//	/**
//     * Returns a String representation of a CCPTextAnnotation object <br>
//     * TODO: inverse slots will cause an infinite loop
//     *
//     * @param ccpTA
//     * @param deep
//     *            - if false, it only prints the text annotation information, e.g. span, covered text
//     * @param anonymize
//     *            - if true, meta information such as annotator, annotation sets, etc. are not included in the output
//     * @return
//     */
//    public static String toString(CCPTextAnnotation ccpTA, boolean deep, boolean anonymize) {
//            String repStr = "";
//
//            String spansStr = "";
//            FSArray spans = ccpTA.getSpans();
//            if (spans != null) {
//                    for (int i = 0; i < spans.size(); i++) {
//                            CCPSpan span = (CCPSpan) spans.get(i);
//                            spansStr += ("[" + span.getSpanStart() + ".." + span.getSpanEnd() + "]");
//                    }
//                    spansStr = spansStr.substring(0, spansStr.length());
//            }
//
//            String coveredText;
//            try {
//                    coveredText = ccpTA.getCoveredText();
//            } catch (StringIndexOutOfBoundsException siobe) {
//                    coveredText = "";
//            }
//
//            CCPAnnotator ccpAnnotator = ccpTA.getAnnotator();
//            String lastName = "";
//            if (ccpAnnotator != null) {
//                    lastName = ccpAnnotator.getLastName();
//            }
//
//            String annotationSetStr = "";
//            FSArray ccpAnnotationSets = ccpTA.getAnnotationSets();
//            if (ccpAnnotationSets != null) {
//                    for (int i = 0; i < ccpAnnotationSets.size(); i++) {
//                            CCPAnnotationSet ccpASet = (CCPAnnotationSet) ccpAnnotationSets.get(i);
//                            annotationSetStr += (ccpASet.getAnnotationSetID() + ",");
//                    }
//            }
//
//            if (anonymize) {
//                    repStr += (spansStr + "|\"" + coveredText + "\"");
//            } else {
//                    repStr += (spansStr + "|\"" + coveredText + "\"" + "|" + lastName + "|" + annotationSetStr);
//            }
//
//            if (deep) {
//                    repStr += (" -- " + toString(ccpTA.getClassMention(), anonymize));
//            }
//            return repStr;
//    }
//
//    public static String toString(CCPClassMention ccpCM, boolean anonymize) {
//            if (ccpCM != null) {
//
//                    String repStr = " [" + ccpCM.getMentionName() + "] ";
//                    
//                            repStr += toString(ccpCM.getCcpTextAnnotation(), false, anonymize) + "(";
//
//                    FSArray ccpSlotMentions = ccpCM.getSlotMentions();
//                    List<CCPSlotMention> ccpSlotMentionsList = new ArrayList<CCPSlotMention>();
//                    if (ccpSlotMentions != null) {
//                            for (int i = 0; i < ccpSlotMentions.size(); i++) {
//                                    ccpSlotMentionsList.add((CCPSlotMention) ccpSlotMentions.get(i));
//                            }
//                    }
//
//                    List<CCPSlotMention> sortedSlotMentions = sortSlotMentions(ccpSlotMentionsList);
//                    for (CCPSlotMention sm : sortedSlotMentions) {
//                            repStr += toString(sm, anonymize);
//                    }
//
//                    repStr += ")";
//                    return repStr;
//            } else {
//                    return null;
//            }
//    }
//
//    /**
//     * Prints a CCPNonComplexSlotMention if it has slot values, otherwise returns an empty String.
//     *
//     * @param ccpNCSM
//     * @return
//     */
//    public static String toString(CCPNonComplexSlotMention ccpNCSM) {
//            String repStr = "";
//            StringArray slotValues = ccpNCSM.getSlotValues();
//            if (slotValues != null && slotValues.size() > 0) {
//                    repStr = " [" + ccpNCSM.getMentionName() + "]:";
//
//                    /* get a sorted list of the slot values */
//                    List<String> slotValuesList = Arrays.asList(slotValues.toArray());
//                    Collections.sort(slotValuesList);
//
//                    for (String slotValue : slotValuesList) {
//                            repStr += slotValue + "|";
//                    }
//                    /* remove trailing pipe */
//                    repStr = repStr.substring(0, repStr.length() - 1);
//            }
//            return repStr;
//    }
//
//    /**
//     * Returns a String representation of a CCPComplexSlotMention if it has any slots, an empty String otherwise
//     *
//     * @param ccpCSM
//     * @return
//     */
//    public static String toString(CCPComplexSlotMention ccpCSM, boolean anonymize) {
//            String repStr = "";
//            FSArray classMentions = ccpCSM.getClassMentions();
//            if (classMentions != null && classMentions.size() > 0) {
//                    repStr = " [" + ccpCSM.getMentionName() + "]:";
//
//                    Map<String, CCPClassMention> classMentionKey2MentionMap = new HashMap<String, CCPClassMention>();
//                    List<String> classMentionKeys = new ArrayList<String>();
//
//                    for (int i = 0; i < classMentions.size(); i++) {
//                            CCPClassMention ccpCM = (CCPClassMention) classMentions.get(i);
//                            String key = toString(ccpCM, anonymize);
//                            classMentionKey2MentionMap.put(key, ccpCM);
//                            classMentionKeys.add(key);
//                    }
//
//                    Collections.sort(classMentionKeys);
//
//                    for (String key : classMentionKeys) {
//                            CCPClassMention ccpCM = classMentionKey2MentionMap.get(key);
//                            repStr += toString((CCPTextAnnotation) ccpCM.getCcpTextAnnotation(), true, anonymize);
//                    }
//            }
//            return repStr;
//
//    }
//
//    private static String toString(CCPSlotMention ccpSM, boolean anonymize) {
//            if (ccpSM instanceof CCPNonComplexSlotMention) {
//                    return toString((CCPNonComplexSlotMention) ccpSM);
//            } else if (ccpSM instanceof CCPComplexSlotMention) {
//                    return toString((CCPComplexSlotMention) ccpSM, anonymize);
//            } else {
//                    logger.error("Expecting CCPNonComplexSlotMention or CCPComplexSlotMention but instead got " + ccpSM.getClass().getName());
//                    return null;
//            }
//    }
}
