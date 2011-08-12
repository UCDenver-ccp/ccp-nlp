package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The BooleanSlotMention is slightly different from other PrimitiveSlotMentions as it can only have a single slot
 * value, either true or false.
 * 
 * @author billbaumgartner
 * 
 */
public abstract class BooleanSlotMention extends PrimitiveSlotMention<Boolean> {
	private static Logger logger = Logger.getLogger(BooleanSlotMention.class);

//	public BooleanSlotMention(String mentionName, IMentionTraversalTracker traversalTracker,  Object... wrappedObjectPlusGlobalVars) {
//		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars);
//	}
	
	public BooleanSlotMention(IMentionTraversalTracker traversalTracker,  Object... wrappedObjectPlusGlobalVars) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Boolean> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		/* There will only be one slot value */
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	public void addSlotValue(Boolean slotValue) {
		if (this.getSlotValues().size() > 0) {
			logger.warn("Cannot 'add' another slot value to BooleanSlotMention: " + getMentionName() + ". The previous value: "
					+ getBooleanValue().toString() + " is being replaced with " + slotValue.toString());
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
			logger.warn("Cannot 'add' another slot value to BooleanSlotMention: " + getMentionName() + ". The previous value: "
					+ getBooleanValue().toString() + " is being replaced with " + slotValue.toString());
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
