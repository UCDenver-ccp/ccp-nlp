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
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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
