package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collections;
import java.util.List;

public abstract class StringSlotMention extends PrimitiveSlotMention<String> {

//	public StringSlotMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
//		super(mentionName,  traversalTracker, wrappedObjectPlusGlobalVars);
//	}
	
	public StringSlotMention(IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<String> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		Collections.sort(slotValuesList);
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	@Override
	public void addSlotValueAsString(String slotValue)  {
		try {
			this.addSlotValue(slotValue);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

}
