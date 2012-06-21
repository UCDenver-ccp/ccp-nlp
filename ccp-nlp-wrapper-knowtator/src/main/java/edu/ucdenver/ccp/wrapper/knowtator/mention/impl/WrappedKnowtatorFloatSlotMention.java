package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.FloatSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class WrappedKnowtatorFloatSlotMention extends FloatSlotMention {

	private SimpleInstance knowtatorSM;
	private KnowtatorUtil ku;
	
	public WrappedKnowtatorFloatSlotMention(SimpleInstance knowtatorMention, KnowtatorUtil ku) {
		super( knowtatorMention, ku);
	this.ku = ku;
	}

	

	@Override
	public SimpleInstance getWrappedObject() {
		return knowtatorSM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars)
			 {
		Object wrappedObject = wrappedObjectPlusGlobalVars[0];
		if (wrappedObject instanceof DefaultSimpleInstance) {
			setGlobalVars(wrappedObjectPlusGlobalVars);
			SimpleInstance possibleSM = (SimpleInstance) wrappedObject;
//			if (possibleSM.getDirectType().getName().equals(ku.getSlotMentionCls().getName())) {
				if (ku.isFloatSlotMention(possibleSM)) {
					this.knowtatorSM = possibleSM;
					return;
				}
//			}
		}
		throw new KnowledgeRepresentationWrapperException("Expected Knowtator float slot mention. Cannot wrap a "
				+ wrappedObject.getClass().getName() + " with the WrappedKnowtatorFloatSlotMention class.");
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	private void setGlobalVars(Object... wrappedObjectPlusGlobalVars) throws KnowledgeRepresentationWrapperException {
		if (wrappedObjectPlusGlobalVars.length > 1) {
			this.ku = (KnowtatorUtil) wrappedObjectPlusGlobalVars[1];
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected KnowtatorUtil object in the constructor for the WrappedKnowtatorClassMention.");
		}
	}

	public void addSlotValue(Float slotValue) throws InvalidInputException {
		ku.addSlotValue(knowtatorSM, slotValue);
	}

	public void addSlotValues(Collection<Float> slotValues)
			throws InvalidInputException {
		for (Float f : slotValues) {
			addSlotValue(f);
		}
	}

	public Collection<Float> getSlotValues() {
		return ku.getSlotValues(knowtatorSM);
	}

	public void overwriteSlotValues(Float slotValue)
			throws InvalidInputException {
		Collection<Float> newSlotValues = new ArrayList<Float>();
		newSlotValues.add(slotValue);
		setSlotValues(newSlotValues);
	}

	public void setSlotValues(Collection<Float> slotValues)
			throws InvalidInputException {
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
