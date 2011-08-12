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

public class DefaultPrimitiveSlotMentionFactory {

	private static Logger logger = Logger.getLogger(DefaultPrimitiveSlotMentionFactory.class);

	public static PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValueObject) throws InvalidInputException {
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
			// }
			// else if (slotValueObject instanceof Double) {
			// Double slotValue = (Double) slotValueObject;
			// DoubleSlotMention dsm = new DoubleSlotMention(slotMentionName);
			// dsm.addSlotValue(slotValue);
			// return dsm;
		} else {
			logger.error("Invalid primitive slot value type: " + slotValueObject.getClass().getName());
			return null;
		}

	}

	public static PrimitiveSlotMention createPrimitiveSlotMentionWithCollection(String slotMentionName, Collection slotValueObjects) throws InvalidInputException {
		logger.debug("Creating PrimitiveSlot from Collection...");
		List<Object> slotValues = Collections.list(Collections.enumeration(slotValueObjects));
		PrimitiveSlotMention psm = createPrimitiveSlotMention(slotMentionName, slotValues.get(0));
		for (int i = 1; i < slotValues.size(); i++) {
			psm.addSlotValue(slotValues.get(i));
		}
		return psm;
	}

	/**
	 * Tries to parse the string as one of the other primitives first. Defaults to Float if it is a number.
	 * 
	 * @param slotMentionName
	 * @param slotValueAsString
	 * @return
	 * @throws InvalidInputException 
	 */
	public static PrimitiveSlotMention createPrimitiveSlotMentionFromStringValue(String slotMentionName, String slotValueAsString) throws InvalidInputException {
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
