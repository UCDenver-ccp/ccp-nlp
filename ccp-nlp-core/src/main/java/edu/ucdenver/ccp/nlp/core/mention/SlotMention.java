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
package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * The slot mention represents a slot for a given frame. There are two subclasses of SlotMention,
 * ComplexSlotMention which has ClassMentions as slot fillers and PrimitiveSlotMention which has
 * primitives (String, Integer, Boolean, Float, etc.) as slot fillers.
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class SlotMention<E> extends Mention implements ISlotMention<E> {

	protected static Logger logger = Logger.getLogger(SlotMention.class);

	// protected Collection<E> slotValues;

	public SlotMention(String mentionName, Object... wrappedObjectPlusGlobalVars) {
		super(mentionName, wrappedObjectPlusGlobalVars);
	}

	public SlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	// @Override
	// protected void initializeMention() {
	// slotValues = new ArrayList<E>();
	// }

	// /**
	// * Get the slot values
	// *
	// * @return
	// */
	// public Collection<E> getSlotValues() {
	// return slotValues;
	// }

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

	// /**
	// * Set the slot values
	// *
	// * @param slotValues
	// */
	// public void setSlotValues(Collection<E> slotValues) {
	// this.slotValues = slotValues;
	// if (hasWrappedMention) {
	// try {
	// setSlotValuesForWrappedMention(slotValues);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// protected abstract void setSlotValuesForWrappedMention(Collection<E> slotValues) throws
	// Exception;
	//
	// /**
	// * Add a slot value to this slot mention
	// *
	// * @param slotValue
	// */
	// public void addSlotValue(E slotValue) {
	// this.slotValues.add(slotValue);
	// if (hasWrappedMention) {
	// try {
	// addSlotValueToWrappedMention(slotValue);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// protected abstract void addSlotValueToWrappedMention(E slotValue) throws Exception;
	//
	// public void addSlotValues(Collection<E> slotValues) {
	// this.slotValues.addAll(slotValues);
	// if (hasWrappedMention) {
	// try {
	// addSlotValuesToWrappedMention(slotValues);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// protected abstract void addSlotValuesToWrappedMention(Collection<E> slotValues) throws
	// Exception;
	//
	// public void overwriteSlotValues(E slotValue) {
	// this.slotValues = new ArrayList<E>();
	// this.slotValues.add(slotValue);
	// if (hasWrappedMention) {
	// clearWrappedMentionSlotValues();
	// try {
	// addSlotValueToWrappedMention(slotValue);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// protected abstract void clearWrappedMentionSlotValues();
	//
	// public void overwriteSlotValues(Collection<E> slotValues) {
	// this.slotValues = slotValues;
	// if (hasWrappedMention) {
	// clearWrappedMentionSlotValues();
	// try {
	// setSlotValuesForWrappedMention(slotValues);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

}
