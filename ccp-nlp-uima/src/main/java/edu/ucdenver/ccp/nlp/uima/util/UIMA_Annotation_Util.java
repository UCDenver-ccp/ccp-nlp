/*
 Copyright (c) 2012, Regents of the University of Colorado
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this 
list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
 * Neither the name of the University of Colorado nor the names of its 
contributors may be used to endorse or promote products derived from this 
software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.util;

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
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty;
//import edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.uima.annotation.impl.WrappedCCPTextAnnotation;

/**
 * 
 * Some simple utilities for dealing with <code>CCPTextAnnotation</code> objects.
 * 
 * 
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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
			// set below removes any duplicate spans
			List<Span> spanList = new ArrayList<Span>(new HashSet<Span>(getSpanList(ccpTA))); 
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
	//public static void addMetaDataProperty(CCPTextAnnotation ccpTA, AnnotationProperty propertyToAdd, JCas jcas) {
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
	//public static <T extends AnnotationProperty> Collection<T> getAnnotationProperties(
	public static <T extends AnnotationMetadataProperty> Collection<T> getAnnotationProperties(
			CCPTextAnnotation ccpTA, Class<T> annotationPropertyClass, JCas jcas) {
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

	//public static <T extends AnnotationProperty> Collection<T> getAnnotationProperties(
	public static <T extends AnnotationMetadataProperty> Collection<T> getAnnotationProperties(
			CCPTextAnnotation ccpTA, Class<T> annotationPropertyClass) {
		try {
			JCas jcas = ccpTA.getCAS().getJCas();
			return getAnnotationProperties(ccpTA, annotationPropertyClass, jcas);
		} catch (CASException e) {
			e.printStackTrace();
			return null;
		}
	}

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
		// String ccpTA_string = toString(ccpTA, deep, anonymize);
		FSIterator completeIt = jcas.getAnnotationIndex().iterator();
		completeIt.moveTo(ccpTA);
		if (completeIt.isValid()) {
			if (completeIt.get() instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA_comp = (CCPTextAnnotation) completeIt.get();
				WrappedCCPTextAnnotation wrappedCcpTa_comp = new WrappedCCPTextAnnotation(ccpTA_comp);
				// String stringRep = toString(ccpTA_comp, deep, anonymize);
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

}
