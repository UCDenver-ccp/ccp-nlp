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
package edu.ucdenver.ccp.nlp.core.mention.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.FloatSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DefaultPrimitiveSlotMentionFactory {

	private static Logger logger = Logger.getLogger(DefaultPrimitiveSlotMentionFactory.class);

	public static PrimitiveSlotMention createPrimitiveSlotMention(
		String slotMentionName, 
		String slotValue)
	throws InvalidInputException {
		logger.debug("Creating String primitive slot with value type: " + slotValue.getClass().getName());
		StringSlotMention sm = new DefaultStringSlotMention(slotMentionName);
		sm.addSlotValue(slotValue);
		return sm;
	}

	public static PrimitiveSlotMention createPrimitiveSlotMention(
		String slotMentionName, 
		Integer slotValue)
	throws InvalidInputException {
		logger.debug("Creating Integer primitive slot with value type: " + slotValue.getClass().getName());
		IntegerSlotMention sm = new DefaultIntegerSlotMention(slotMentionName);
		sm.addSlotValue(slotValue);
		return sm;
	}
	public static PrimitiveSlotMention createPrimitiveSlotMention(
		String slotMentionName, 
		Boolean slotValue)
	throws InvalidInputException {
		logger.debug("Creating Boolean primitive slot with value type: " + slotValue.getClass().getName());
		BooleanSlotMention sm = new DefaultBooleanSlotMention(slotMentionName);
		sm.addSlotValue(slotValue);
		return sm;
	}
	public static PrimitiveSlotMention createPrimitiveSlotMention(
		String slotMentionName, 
		Float slotValue)
	throws InvalidInputException {
		logger.debug("Creating Float primitive slot with value type: " + slotValue.getClass().getName());
		FloatSlotMention sm = new DefaultFloatSlotMention(slotMentionName);
		sm.addSlotValue(slotValue);
		return sm;
	}

	/* Type Erasure in Java Generics prevents this from being a single function name
      overloaded 4 times.
    */
	public static PrimitiveSlotMention createPrimitiveSlotMentionWithStringCollection(
			String slotMentionName,
			Collection<String> values) 
	throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		StringSlotMention sm = new DefaultStringSlotMention(slotMentionName);
		for (String s : values) {
			sm.addSlotValue(s);
		}
		return sm;
	}

	public static PrimitiveSlotMention createPrimitiveSlotMentionWithFloatCollection(
			String slotMentionName,
			Collection<Float> values) 
	throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		FloatSlotMention sm = new DefaultFloatSlotMention(slotMentionName);
		for (Float f : values) {
			sm.addSlotValue(f);
		}
		return sm;
	}

	public static PrimitiveSlotMention createPrimitiveSlotMentionWithIntegerCollection(
			String slotMentionName,
			Collection<Integer> values) 
	throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		IntegerSlotMention sm = new DefaultIntegerSlotMention(slotMentionName);
		for (Integer i : values) {
			sm.addSlotValue(i);
		}
		return sm;
	}

	public static PrimitiveSlotMention createPrimitiveSlotMentionWithBooleanCollection(
			String slotMentionName,
			Collection<Boolean> values) 
	throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		BooleanSlotMention sm = new DefaultBooleanSlotMention(slotMentionName);
		for (Boolean b : values) {
			sm.addSlotValue(b);
		}
		return sm;
	}

	/**
	 * Tries to parse the string as one of the other primitives first. Defaults to Float if it is a
	 * number.
	 * 
	 * @param slotMentionName
	 * @param slotValueAsString
	 * @return
	 * @throws InvalidInputException
	 */
	public static PrimitiveSlotMention createPrimitiveSlotMentionFromStringValue(String slotMentionName,
			String slotValueAsString) throws InvalidInputException {
		slotValueAsString = slotValueAsString.trim();
		try {
			Integer integerSlotValue = Integer.parseInt(slotValueAsString);
			return createPrimitiveSlotMention(slotMentionName, integerSlotValue);
		} catch (Exception e1) {
			try {
				if (slotValueAsString.equalsIgnoreCase("true") | slotValueAsString.equalsIgnoreCase("false")) {
					Boolean booleanSlotValue = Boolean.parseBoolean(slotValueAsString);
					return createPrimitiveSlotMention(slotMentionName, booleanSlotValue);
				}
				throw new Exception();
			} catch (Exception e2) {
				try {
					Float floatSlotValue = Float.parseFloat(slotValueAsString);
					return createPrimitiveSlotMention(slotMentionName, floatSlotValue);
				} catch (Exception e3) {
					return createPrimitiveSlotMention(slotMentionName, slotValueAsString);
				}
			}
		}

	}

}
