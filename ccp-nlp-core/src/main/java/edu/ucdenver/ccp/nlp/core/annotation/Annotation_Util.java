package edu.ucdenver.ccp.nlp.core.annotation;

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


import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * This class contains some utility methods for dealing with text annotations.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Annotation_Util {
	private static Logger logger = Logger.getLogger(Annotation_Util.class);
	public final static String UNKNOWN_TAGSET = "Unknown";

	/**
	 * Given a part of speech label, stem, lemma, and token number, create a
	 * <code>ClassMention</code> that represents a token.
	 * 
	 * @param posLabel
	 *            part of speech label
	 * @param stem
	 *            the stem of the token
	 * @param lemma
	 *            the lemma of the token
	 * @param tokenNumber
	 *            the token number of the token
	 * @return a <code>ClassMention</code> representing a token
	 * @throws Exception
	 */
	public static ClassMention createTokenMention(String posLabel, String posTagSet, String stem, String lemma,
			Integer tokenNumber) {
		ClassMention cm = new DefaultClassMention(ClassMentionType.TOKEN.typeName());

		if (posLabel != null) {
			/* create POS slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionType.TOKEN_PARTOFSPEECH.typeName());
			sm.addSlotValue(posLabel);

			if (posTagSet == null) {
				posTagSet = UNKNOWN_TAGSET;
			}
			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
			tagSetSlot.addSlotValue(posTagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}

		StringSlotMention sm;
		if (stem != null) {
			/* create stem slot */
			sm = new DefaultStringSlotMention(SlotMentionType.TOKEN_STEM.typeName());
			sm.addSlotValue(stem);
			cm.addPrimitiveSlotMention(sm);
		}
		if (lemma != null) {
			/* create lemma slot */
			sm = new DefaultStringSlotMention(SlotMentionType.TOKEN_LEMMA.typeName());
			sm.addSlotValue(lemma);
			cm.addPrimitiveSlotMention(sm);
		}
		if (tokenNumber != null) {
			/* create tokenNumber slot */
			IntegerSlotMention ism = new DefaultIntegerSlotMention(SlotMentionType.TOKEN_NUMBER.typeName());
			ism.addSlotValue(tokenNumber);
			cm.addPrimitiveSlotMention(ism);
		}
		return cm;
	}

	public static ClassMention createPhraseMention(String phraseTypeLabel, String tagSet) {
		ClassMention cm = new DefaultClassMention(ClassMentionType.PHRASE.typeName());

		if (phraseTypeLabel != null) {
			/* create phraseType slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionType.PHRASE_TYPE.typeName());
			sm.addSlotValue(phraseTypeLabel);
			if (tagSet == null) {
				tagSet = UNKNOWN_TAGSET;
			}

			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
			tagSetSlot.addSlotValue(tagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}
		return cm;
	}

	public static ClassMention createClauseMention(String clauseTypeLabel, String tagSet) {
		ClassMention cm = new DefaultClassMention(ClassMentionType.CLAUSE.typeName());

		if (clauseTypeLabel != null) {
			/* create phraseType slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionType.CLAUSE_TYPE.typeName());
			sm.addSlotValue(clauseTypeLabel);
			if (tagSet == null) {
				tagSet = UNKNOWN_TAGSET;
			}
			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
			tagSetSlot.addSlotValue(tagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}
		return cm;
	}

	/**
	 * This utility swaps annotation information from one <code>TextAnnotation</code> to another.
	 * The fields that are transferred are:
	 * <ul>
	 * <li>annotation ID</li>
	 * <li>annotator</li>
	 * <li>annotation sets</li>
	 * <li>covered text</li>
	 * <li>document collection ID</li>
	 * <li>document ID</li>
	 * <li>document section ID</li>
	 * <li>spans</li>
	 * </ul>
	 * <p>
	 * Note: class information does not get transferred.
	 * 
	 * @param fromTA
	 *            the text annotation from which to transfer annotation information
	 * @param toTA
	 *            the text annotation to which to transfer annotation information
	 */
	public static void swapAnnotationInfo(TextAnnotation fromTA, TextAnnotation toTA) {
		toTA.setAnnotationID(fromTA.getAnnotationID());

		toTA.setAnnotationSets(fromTA.getAnnotationSets());
		toTA.setAnnotator(fromTA.getAnnotator());
		toTA.setCoveredText(fromTA.getCoveredText());
		toTA.setDocumentCollectionID(fromTA.getDocumentCollectionID());
		toTA.setDocumentID(fromTA.getDocumentID());
		toTA.setDocumentSectionID(fromTA.getDocumentSectionID());
		for (Span span : fromTA.getSpans()) {
			toTA.addSpan(span.clone());
		}

	}

	/**
	 * For a give <code>TextAnnotation</code> return a list of the slot values for a given slot. The
	 * slot is specified by using the slot name. This name must reference a <code>SlotMention</code>
	 * (and not a <code>ComplexSlotMention</code>).
	 * 
	 * @param ta
	 *            the <code>TextAnnotation</code> that contains the slot
	 * @param slotName
	 *            the name of the <code>SlotMention</code>, whose values will be retrieved.
	 * @return a list of objects stored in the slot. Often these objects are <code>Strings</code>.
	 */
	public static Collection<?> getSlotValuesByName(TextAnnotation ta, String slotName) {
		PrimitiveSlotMention<?> slotMention = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		return slotMention.getSlotValues();
	}

	public static Set<TextAnnotation> intersectAnnotations(Collection<TextAnnotation> a1, Collection<TextAnnotation> a2) {
		Set<TextAnnotation> iSet = new HashSet<TextAnnotation>(a1);
		iSet.retainAll(a2);
		return iSet;
	}

	public static Set<TextAnnotation> newAnnotations(Collection<TextAnnotation> a1, Collection<TextAnnotation> a2) {
		Set<TextAnnotation> newSet = new HashSet<TextAnnotation>(a2);
		newSet.removeAll(a1);
		return newSet;
	}

	public static Set<TextAnnotation> aggregateAnnotations(Collection<Collection<TextAnnotation>> l) {
		Set<TextAnnotation> aggregate = new HashSet<TextAnnotation>();
		for (Iterator<Collection<TextAnnotation>> i = l.iterator(); i.hasNext();) {
			aggregate.addAll(i.next());
		}
		return aggregate;
	}

	public static boolean isTokenAnnotation(TextAnnotation a) {
		// System.out.println(a.getClassMention().getMentionName());
		return a.getClassMention().getMentionName().equals("token");
	}

	public static boolean isAnnotationTypeMember(TextAnnotation a, String... types) {
		String aType = a.getClassMention().getMentionName();
		return Arrays.asList(types).contains(aType);
	}

	public static LinkedList<TextAnnotation> removeTypes(Collection<TextAnnotation> annotations, String... types) {
		LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
		for (TextAnnotation a : annotations) {
			if (!isAnnotationTypeMember(a, types)) {
				newAnnotations.add(a);
			}
		}
		return newAnnotations;
	}

	public static LinkedList<TextAnnotation> keepTypes(Collection<TextAnnotation> annotations, String... types) {
		LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
		for (TextAnnotation a : annotations) {
			if (isAnnotationTypeMember(a, types)) {
				newAnnotations.add(a);
			}
		}
		return newAnnotations;
	}

	public static LinkedList<TextAnnotation> removeTokens(Collection<TextAnnotation> annotations) {
		LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
		for (TextAnnotation a : annotations) {
			if (!isTokenAnnotation(a)) {
				newAnnotations.add(a);
			}
		}
		return newAnnotations;
	}

	public static LinkedList<TextAnnotation> getTokens(Collection<TextAnnotation> annotations) {
		LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
		for (TextAnnotation a : annotations) {
			// System.out.println(a.getClassMention().getMentionName());
			if (isTokenAnnotation(a)) {
				newAnnotations.add(a);
			}
		}
		return newAnnotations;
	}

	public static class SpanStartsComparator implements Comparator<Span> {
		public int compare(Span s1, Span s2) {
			return s1.getSpanStart() - s2.getSpanStart();
		}
	}

	public static class AnnotationSpanStartsComparator implements Comparator<TextAnnotation> {
		SpanStartsComparator comparator;

		public AnnotationSpanStartsComparator() {
			super();
			this.comparator = new SpanStartsComparator();
		}

		public int compare(TextAnnotation a1, TextAnnotation a2) {
			return comparator.compare(a1.getSpans().get(0), a2.getSpans().get(0));
		}

	}

}
