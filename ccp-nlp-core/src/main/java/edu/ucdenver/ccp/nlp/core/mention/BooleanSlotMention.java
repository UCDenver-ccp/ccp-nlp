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
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The BooleanSlotMention is slightly different from other PrimitiveSlotMentions as it can only have
 * a single slot value, either true or false.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class BooleanSlotMention extends PrimitiveSlotMention<Boolean> {
	private static Logger logger = Logger.getLogger(BooleanSlotMention.class);

	public BooleanSlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Boolean> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		/* There will only be one slot value */
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	public void addSlotValue(Boolean slotValue) {
		if (this.getSlotValues().size() > 0) {
			logger.warn("Cannot 'add' another slot value to BooleanSlotMention: " + getMentionName()
					+ ". The previous value: " + getBooleanValue().toString() + " is being replaced with "
					+ slotValue.toString());
		}
		setBooleanValue(slotValue);
	}

	public void addSlotValues(Collection<Boolean> slotValues) throws InvalidInputException {
		if (slotValues.size() > 1) {
			throw new InvalidInputException("Cannot add multiple Boolean values to a Boolean Slot Mention!");
		} else if (slotValues.size() == 0) {
			// do nothing
		} else if (slotValues.size() == 1) {
			Boolean slotValue = Collections.list(Collections.enumeration(slotValues)).get(0);
			logger.warn("Cannot 'add' another slot value to BooleanSlotMention: " + getMentionName()
					+ ". The previous value: " + getBooleanValue().toString() + " is being replaced with "
					+ slotValue.toString());
			setBooleanValue(slotValue);
		}
	}

	public void setSlotValues(Collection<Boolean> slotValues) throws InvalidInputException {
		if (slotValues.size() == 1) {
			Boolean slotValue = Collections.list(Collections.enumeration(slotValues)).get(0);
			setBooleanValue(slotValue);
		} else {
			throw new InvalidInputException("Cannot add multiple or zero Boolean values to a Boolean Slot Mention!");
		}
	}

	public Boolean getBooleanValue() {
		return Collections.list(Collections.enumeration(getSlotValues())).get(0);
	}

	public void setBooleanValue(boolean value) {
		try {
			overwriteSlotValues(value);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addSlotValueAsString(String slotValue) {
		slotValue = slotValue.trim();
		if (slotValue.equalsIgnoreCase("true") | slotValue.equalsIgnoreCase("false")) {
			Boolean booleanSlotValue = Boolean.parseBoolean(slotValue);
			addSlotValue(booleanSlotValue);
		} else {
			logger.error("Attempting to add slot value that is not an Integer (" + slotValue
					+ ") to an IntegerSlotValue. This value has not been added to the mention hierarchy.");
		}
	}
}
