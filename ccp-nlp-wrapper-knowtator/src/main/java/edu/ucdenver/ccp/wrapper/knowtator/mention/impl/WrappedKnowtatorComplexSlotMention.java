package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class WrappedKnowtatorComplexSlotMention extends ComplexSlotMention {

	private SimpleInstance knowtatorCSM;
	private KnowtatorUtil ku;

	public WrappedKnowtatorComplexSlotMention(SimpleInstance knowtatorMention, KnowtatorUtil ku) {
		super(new KnowtatorMentionTraversalTracker(knowtatorMention, ku), knowtatorMention, ku);
		this.ku = ku;
	}

	@Override
	public SimpleInstance getWrappedObject() {
		return knowtatorCSM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		Object wrappedObject = wrappedObjectPlusGlobalVars[0];
		if (wrappedObject instanceof DefaultSimpleInstance) {
			setGlobalVars(wrappedObjectPlusGlobalVars);
			SimpleInstance possibleCSM = (SimpleInstance) wrappedObject;
//			if (possibleCSM.getDirectType().getName().equals(ku.getSlotMentionCls().getName())) {
				if (ku.isComplexSlotMention(possibleCSM)) {
					this.knowtatorCSM = possibleCSM;
					return;
				}
//			}
		}
		throw new KnowledgeRepresentationWrapperException("Expected SimpleInstance for a ComplexSlotMention. Cannot wrap a "
				+ wrappedObject.getClass().getName() + " with the WrappedKnowtatorComplexSlotMention class.");
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
	@Override
	public ClassMention createClassMention(String classMentionName) {
		SimpleInstance classMentionInstance = ku.createKnowtatorClassMention(classMentionName);
		return new WrappedKnowtatorClassMention(classMentionInstance, ku);
	}

	public void addSlotValue(ClassMention slotValue) throws InvalidInputException {
		ku.addSlotValue(knowtatorCSM, slotValue.getWrappedObject());
	}

	public void addSlotValues(Collection<ClassMention> slotValues) throws InvalidInputException {
		for (ClassMention cm : slotValues) {
			addSlotValue(cm);
		}
	}

	public Collection<ClassMention> getSlotValues() {
		Collection<SimpleInstance> classMentionInstances = ku.getSlotValues(knowtatorCSM);
		Collection<ClassMention> classMentions = new ArrayList<ClassMention>();
		for (SimpleInstance cmInstance : classMentionInstances) {
			classMentions.add(new WrappedKnowtatorClassMention(cmInstance, ku));
		}
		return classMentions;
	}

	public void overwriteSlotValues(ClassMention slotValue) throws InvalidInputException {
		Collection<ClassMention> classMentions = new ArrayList<ClassMention>();
		classMentions.add(slotValue);
		setSlotValues(classMentions);
	}

	public void setSlotValues(Collection<ClassMention> slotValues) throws InvalidInputException {
		Collection<Object> cmInstances = new ArrayList<Object>();
		for (ClassMention cm : slotValues) {
			if (cm instanceof WrappedKnowtatorClassMention) {
				cmInstances.add(((WrappedKnowtatorClassMention) cm).getWrappedObject());
			}
		}
		ku.setSlotValues(knowtatorCSM, cmInstances);
	}

	@Override
	public long getMentionID() {
		return ku.getMentionID(knowtatorCSM);
	}

	@Override
	public String getMentionName() {
		return ku.getSlotMentionName(knowtatorCSM);
}

	@Override
	public void setMentionID(long mentionID) {
		ku.setMentionID(knowtatorCSM, mentionID);
	}

	@Override
	protected void setMentionName(String mentionName) {
		ku.setSlotMentionName(knowtatorCSM, mentionName);
	}
	
//	@Override
//	protected Long getMentionIDForTraversal(int traversalID) {
//		return ku.getMentionIDForTraversal(knowtatorCSM, traversalID);
//	}
//
//	@Override
//	protected void removeMentionIDForTraversal(int traversalID) {
//		ku.removeTraversalID(traversalID, knowtatorCSM);
//
//	}
//
//	@Override
//	protected void setMentionIDForTraversal(long mentionID, int traversalID) {
//		ku.setMentionIDForTraversal(mentionID, traversalID, knowtatorCSM);
//	}

}
