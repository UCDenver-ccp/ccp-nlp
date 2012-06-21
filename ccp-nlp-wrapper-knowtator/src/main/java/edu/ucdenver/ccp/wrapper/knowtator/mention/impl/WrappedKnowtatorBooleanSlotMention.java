package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class WrappedKnowtatorBooleanSlotMention extends BooleanSlotMention {

	private SimpleInstance knowtatorSM;
	private KnowtatorUtil ku;

	public WrappedKnowtatorBooleanSlotMention(SimpleInstance knowtatorMention, KnowtatorUtil ku) {
		super( knowtatorMention, ku);
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
			SimpleInstance possibleSM = (SimpleInstance) wrappedObject;
			setGlobalVars(wrappedObjectPlusGlobalVars);
//			if (possibleSM.getDirectType().getName().equals(ku.getSlotMentionCls().getName())) {
				if (ku.isBooleanSlotMention(possibleSM)) {
					this.knowtatorSM = possibleSM;
					return;
				}
//			}
		}
		throw new KnowledgeRepresentationWrapperException("Expected Knowtator boolean slot mention. Cannot wrap a "
				+ wrappedObject.getClass().getName() + " with the WrappedKnowtatorBooleanSlotMention class.");

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

	public Collection<Boolean> getSlotValues() {
		return ku.getSlotValues(knowtatorSM);
	}

	public void overwriteSlotValues(Boolean slotValue) throws InvalidInputException {
		Collection<Object> slotValues = new ArrayList<Object>();
		slotValues.add(slotValue);
		ku.setSlotValues(knowtatorSM, slotValues);
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
