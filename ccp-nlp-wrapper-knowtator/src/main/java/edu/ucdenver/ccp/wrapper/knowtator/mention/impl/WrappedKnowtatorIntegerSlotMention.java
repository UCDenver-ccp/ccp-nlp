package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class WrappedKnowtatorIntegerSlotMention extends IntegerSlotMention {
	private SimpleInstance knowtatorSM;
	private KnowtatorUtil ku;

	public WrappedKnowtatorIntegerSlotMention(SimpleInstance knowtatorMention, KnowtatorUtil ku) {
		super(
				knowtatorMention, ku);
		this.ku = ku;
	}

	@Override
	public SimpleInstance getWrappedObject() {
		return knowtatorSM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		Object wrappedObject = wrappedObjectPlusGlobalVars[0];
		if (wrappedObject instanceof DefaultSimpleInstance) {
			
			DefaultSimpleInstance dsi = (DefaultSimpleInstance) wrappedObject;
			setGlobalVars(wrappedObjectPlusGlobalVars);
			SimpleInstance possibleSM = (SimpleInstance) wrappedObject;
			if (ku.isIntegerSlotMention(possibleSM)) {
//				if (possibleSM.getDirectType().getName().equals(ku.getSlotMentionCls().getName())) {
//					if (ku.isIntegerSlotMention(possibleSM)) {
						this.knowtatorSM = possibleSM;
						return;
//					}
//				}
			} 
		}
		throw new KnowledgeRepresentationWrapperException("Expected Knowtator integer slot mention. Cannot wrap a "
				+ wrappedObject.getClass().getName() + " with the WrappedKnowtatorIntegerSlotMention class.");
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	private void setGlobalVars(Object... wrappedObjectPlusGlobalVars) throws KnowledgeRepresentationWrapperException {
		if (wrappedObjectPlusGlobalVars.length > 1) {
			this.ku = (KnowtatorUtil) wrappedObjectPlusGlobalVars[1];
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Expected KnowtatorUtil object in the constructor for the WrappedKnowtatorClassMention.");
		}
	}

	public void addSlotValue(Integer slotValue) throws InvalidInputException {
		ku.addSlotValue(knowtatorSM, slotValue);
	}

	public void addSlotValues(Collection<Integer> slotValues) throws InvalidInputException {
		for (Integer i : slotValues) {
			addSlotValue(i);
		}
	}

	public Collection<Integer> getSlotValues() {
//		System.err.println("Wrapped knowtatorSM is null: " + (knowtatorSM==null));
		return ku.getSlotValues(knowtatorSM);
	}

	public void overwriteSlotValues(Integer slotValue) throws InvalidInputException {
		Collection<Integer> newSlotValues = new ArrayList<Integer>();
		newSlotValues.add(slotValue);
		setSlotValues(newSlotValues);
	}

	public void setSlotValues(Collection<Integer> slotValues) throws InvalidInputException {
		ku.setSlotValues(knowtatorSM, new ArrayList<Object>(slotValues));
	}

	@Override
	public long getMentionID() {
		return ku.getMentionID(knowtatorSM);
	}

	@Override
	public String getMentionName() {
		return ku.getSlotMentionName(knowtatorSM);
}

	@Override
	public void setMentionID(long mentionID) {
		ku.setMentionID(knowtatorSM, mentionID);
	}

	@Override
	protected void setMentionName(String mentionName) {
		ku.setSlotMentionName(knowtatorSM, mentionName);
	}
	
}
