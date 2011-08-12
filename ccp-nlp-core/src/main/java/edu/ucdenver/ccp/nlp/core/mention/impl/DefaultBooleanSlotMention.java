package edu.ucdenver.ccp.nlp.core.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.ucdenver.ccp.nlp.core.mention.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;

public class DefaultBooleanSlotMention extends BooleanSlotMention {

	private String mentionName;
	private long mentionID;
	protected Collection<Boolean> slotValues;
//	protected Map<Integer, Long> traversalID2MentionIDMap;

	public DefaultBooleanSlotMention(String mentionName) {
		super(new DefaultMentionTraversalTracker(), (Object[])null);
		this.mentionName = mentionName;
		slotValues = new ArrayList<Boolean>();
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObject) {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

	@Override
	public Object getWrappedObject() {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

//	@Override
//	protected void initializeMention() {
////		traversalID2MentionIDMap = new HashMap<Integer, Long>();
//	}

	public Collection<Boolean> getSlotValues() {
		return slotValues;
	}

	public void overwriteSlotValues(Boolean slotValue) throws InvalidInputException {
		slotValues = new ArrayList<Boolean>();
		slotValues.add(slotValue);
	}

	@Override
	public long getMentionID() {
return mentionID;
}

	@Override
	public String getMentionName() {
return mentionName;
}

	@Override
	public void setMentionID(long mentionID) {
		this.mentionID = mentionID;
	}
	
	@Override
	protected void setMentionName(String mentionName) {
		this.mentionName = mentionName;
	}

//	@Override
//	protected Long getMentionIDForTraversal(int traversalID) {
//		return traversalID2MentionIDMap.get(traversalID);
//	}
//
//	@Override
//	protected void setMentionIDForTraversal(long mentionID, int traversalID) {
//		traversalID2MentionIDMap.put(traversalID, mentionID);
//	}
//
//	@Override
//	protected void removeMentionIDForTraversal(int traversalID) {
//		traversalID2MentionIDMap.remove(traversalID);
//	}

}
