package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


public abstract class FloatSlotMention extends PrimitiveSlotMention<Float> {
	private static Logger logger = Logger.getLogger(FloatSlotMention.class);

//	public FloatSlotMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
//		super(mentionName,traversalTracker, wrappedObjectPlusGlobalVars);
//	}
	
	public FloatSlotMention(IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Float> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		Collections.sort(slotValuesList);
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	@Override
	public void addSlotValueAsString(String slotValue) {
		try {
			Float floatSlotValue = Float.parseFloat(slotValue.trim());
			addSlotValue(floatSlotValue);
		} catch (Exception e) {
			logger.error("Attempting to add slot value that is not an Float (" + slotValue
					+ ") to an FloatSlotValue. This value has not been added to the mention hierarchy.");
		}
	}
}
