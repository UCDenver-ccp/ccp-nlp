package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class DoubleSlotMention extends PrimitiveSlotMention<Double> {
	private static Logger logger = Logger.getLogger(DoubleSlotMention.class);

	// public FloatSlotMention(String mentionName, IMentionTraversalTracker traversalTracker,
	// Object... wrappedObjectPlusGlobalVars) {
	// super(mentionName,traversalTracker, wrappedObjectPlusGlobalVars);
	// }

	public DoubleSlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Double> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		Collections.sort(slotValuesList);
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	@Override
	public void addSlotValueAsString(String slotValue) {
		try {
			Double floatSlotValue = Double.parseDouble(slotValue.trim());
			addSlotValue(floatSlotValue);
		} catch (Exception e) {
			logger.error("Attempting to add slot value that is not an Float (" + slotValue
					+ ") to an FloatSlotValue. This value has not been added to the mention hierarchy.");
		}
	}
}
