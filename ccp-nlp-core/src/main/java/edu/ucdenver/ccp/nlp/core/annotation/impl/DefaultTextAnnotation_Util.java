/*
 * Annotation_Util.java
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

package edu.ucdenver.ccp.nlp.core.annotation.impl;


import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * This class contains some utility methods for dealing with text annotations.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class DefaultTextAnnotation_Util {
	private static Logger logger = Logger.getLogger(DefaultTextAnnotation_Util.class);
	public final static String UNKNOWN_TAGSET = "Unknown";

	/**
	 * Given a part of speech label, stem, lemma, and token number, create a <code>ClassMention</code> that represents a
	 * token.
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
	 * @throws InvalidInputException
	 */
	public static DefaultClassMention createTokenMention(String posLabel, String posTagSet, String stem, String lemma, Integer tokenNumber)
			throws InvalidInputException {
		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.TOKEN.typeName());

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

	public static DefaultClassMention createPhraseMention(String phraseTypeLabel, String tagSet) throws InvalidInputException {
		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.PHRASE.typeName());

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

	public static DefaultClassMention createClauseMention(String clauseTypeLabel, String tagSet) throws InvalidInputException {
		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.CLAUSE.typeName());

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

	

	// /**
	// * For a give <code>TextAnnotation</code> return a list of the slot values for a given slot. The slot is specified
	// * by using the slot name. This name must reference a <code>SlotMention</code> (and not a
	// * <code>ComplexSlotMention</code>).
	// *
	// * @param ta
	// * the <code>TextAnnotation</code> that contains the slot
	// * @param slotName
	// * the name of the <code>SlotMention</code>, whose values will be retrieved.
	// * @return a list of objects stored in the slot. Often these objects are <code>Strings</code>.
	// */
	// public static List<Object> getSlotValuesByName(TextAnnotation ta, String slotName) {
	// List<SlotMention> slotMentions = ta.getClassMention().getSlotMentionsByName(slotName);
	// List<Object> slotValues = new ArrayList<Object>();
	// for (SlotMention sm : slotMentions) {
	// slotValues.addAll(sm.getSlotValues());
	// }
	// return slotValues;
	// }

	// /**
	// * Adds a slot value to a text annotation. If the slot is not present, it is created.
	// *
	// * @param ta
	// * @param slotName
	// * @param slotValue
	// */
	// public static void addSlotValue(TextAnnotation ta, String slotName, String slotValue) {
	// List<SlotMention> slotMentions = ta.getClassMention().getSlotMentionsByName(slotName);
	// if (slotMentions.size() > 0) {
	// if (slotMentions.size() > 1) {
	// SlotMention sm = new SlotMention(slotName);
	// sm.addSlotValue(slotValue);
	// ta.getClassMention().addSlotMention(sm);
	// } else {
	// SlotMention sm = slotMentions.get(0);
	// sm.addSlotValue(slotValue);
	// }
	// } else {
	// SlotMention sm = new SlotMention(slotName);
	// sm.addSlotValue(slotValue);
	// ta.getClassMention().addSlotMention(sm);
	// }
	//
	// }

	

	

	
	

}
