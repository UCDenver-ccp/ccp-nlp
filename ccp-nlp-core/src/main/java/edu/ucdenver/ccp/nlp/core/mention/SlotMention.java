package edu.ucdenver.ccp.nlp.core.mention;

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

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * The slot mention represents a slot for a given frame. There are two subclasses of SlotMention,
 * ComplexSlotMention which has ClassMentions as slot fillers and PrimitiveSlotMention which has
 * primitives (String, Integer, Boolean, Float, etc.) as slot fillers.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class SlotMention<E> extends Mention implements ISlotMention<E> {

	protected static Logger logger = Logger.getLogger(SlotMention.class);

	public SlotMention(String mentionName, Object... wrappedObjectPlusGlobalVars) {
		super(mentionName, wrappedObjectPlusGlobalVars);
	}

	public SlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	/**
	 * If this slot mention is storing only a single slot filler, then the single slot filler is
	 * returned, otherwise it throws an exception if there are zero or more than one slot value from
	 * which to choose
	 * 
	 * @return
	 */
	public E getSingleSlotValue() throws SingleSlotFillerExpectedException {
		if (getSlotValues().size() == 1) {
			return Collections.list(Collections.enumeration(getSlotValues())).get(0);
		}
		throw new SingleSlotFillerExpectedException("Expected single slot filler for slot: " + getMentionName()
				+ ", however this slot has " + getSlotValues().size() + " fillers. [" + getSlotValues().toString()
				+ "] PrimitiveSlotMention.getOnlySlotValue() cannot return an appropriate value");
	}

	/**
	 * Counts the non-empty slot mentions in the input collection
	 * 
	 * @param slotMentions
	 * @return
	 */
	public static <T extends SlotMention> int nonEmptySlotMentionCount(Collection<T> slotMentions) {
		int nonEmptySlotMentionCount = 0;
		for (T sm : slotMentions) {
			if (sm.getSlotValues().size() > 0) {
				nonEmptySlotMentionCount++;
			}
		}
		return nonEmptySlotMentionCount;
	}

}
