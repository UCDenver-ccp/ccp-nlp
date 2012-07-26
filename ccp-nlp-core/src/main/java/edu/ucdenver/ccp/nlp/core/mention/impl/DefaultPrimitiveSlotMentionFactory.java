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

	public static PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValueObject)
			throws InvalidInputException {
		logger.debug("Creating primitive slot with value type: " + slotValueObject.getClass().getName());
		if (slotValueObject instanceof String) {
			String slotValue = (String) slotValueObject;
			StringSlotMention ssm = new DefaultStringSlotMention(slotMentionName);
			ssm.addSlotValue(slotValue);
			return ssm;
		} else if (slotValueObject instanceof Integer) {
			Integer slotValue = (Integer) slotValueObject;
			IntegerSlotMention ism = new DefaultIntegerSlotMention(slotMentionName);
			ism.addSlotValue(slotValue);
			return ism;
		} else if (slotValueObject instanceof Boolean) {
			Boolean slotValue = (Boolean) slotValueObject;
			BooleanSlotMention bsm = new DefaultBooleanSlotMention(slotMentionName);
			bsm.addSlotValue(slotValue);
			return bsm;
		} else if (slotValueObject instanceof Float) {
			Float slotValue = (Float) slotValueObject;
			FloatSlotMention fsm = new DefaultFloatSlotMention(slotMentionName);
			fsm.addSlotValue(slotValue);
			return fsm;
		} else {
			logger.error("Invalid primitive slot value type: " + slotValueObject.getClass().getName());
			return null;
		}

	}

	public static PrimitiveSlotMention createPrimitiveSlotMentionWithCollection(String slotMentionName,
			Collection slotValueObjects) throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		List<Object> slotValues = Collections.list(Collections.enumeration(slotValueObjects));
		PrimitiveSlotMention psm = createPrimitiveSlotMention(slotMentionName, slotValues.get(0));
		for (int i = 1; i < slotValues.size(); i++) {
			psm.addSlotValue(slotValues.get(i));
		}
		return psm;
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
