package edu.ucdenver.ccp.nlp.core.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;

public class DefaultStringSlotMention extends StringSlotMention {
	private String mentionName;
	private long mentionID;
	Collection<String> slotValues;
//	protected Map<Integer, Long> traversalID2MentionIDMap;

	public DefaultStringSlotMention(String mentionName) {
		super(new DefaultMentionTraversalTracker(), (Object[])null);
		this.mentionName = mentionName;
		slotValues = new ArrayList<String>();
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

	public void addSlotValue(String slotValue) throws InvalidInputException {
		slotValues.add(slotValue);
	}

	public void addSlotValues(Collection<String> slotValues) throws InvalidInputException {
		for (String s : slotValues) {
			addSlotValue(s);
		}
	}

	public Collection<String> getSlotValues() {
		return slotValues;
	}

	public void overwriteSlotValues(String slotValue) throws InvalidInputException {
		slotValues = new ArrayList<String>();
		addSlotValue(slotValue);
	}

	public void setSlotValues(Collection<String> slotValues) throws InvalidInputException {
		slotValues = new ArrayList<String>();
		addSlotValues(slotValues);
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
