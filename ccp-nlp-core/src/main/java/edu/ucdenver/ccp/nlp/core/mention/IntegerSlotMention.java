package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class IntegerSlotMention extends PrimitiveSlotMention<Integer> {
	private static Logger logger = Logger.getLogger(IntegerSlotMention.class);

//	public IntegerSlotMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
//		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars);
//	}
	
	public IntegerSlotMention( IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Integer> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		try {
			Collections.sort(slotValuesList);
		} catch (ClassCastException cce) {
			System.err.println("CAUGHT CCE SLOT VALUES: " + slotValuesList.toString());
		}
		return getStringRepresentation(indentLevel, slotValuesList);
	}


	@Override
	public void addSlotValueAsString(String slotValue) {
		try {
			Integer integerSlotValue = Integer.parseInt(slotValue.trim());
			addSlotValue(integerSlotValue);
		} catch (Exception e) {
			logger.error("Attempting to add slot value that is not an Integer (" + slotValue
					+ ") to an IntegerSlotValue. This value has not been added to the mention hierarchy.");
		}
	}

}
