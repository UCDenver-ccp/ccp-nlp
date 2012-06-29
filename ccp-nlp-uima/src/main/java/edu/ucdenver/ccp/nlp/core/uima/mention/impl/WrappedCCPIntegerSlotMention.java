package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPIntegerSlotMention extends IntegerSlotMention {

	private CCPIntegerSlotMention wrappedSM;
	private JCas jcas;

	public WrappedCCPIntegerSlotMention(CCPIntegerSlotMention ccpSSM) {
		super(ccpSSM);
	}

	@Override
	public CCPIntegerSlotMention getWrappedObject() {
		return wrappedSM;
	}

	// @Override
	// public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo)
	// {
	// List<Integer> sortedSlotValues = new ArrayList<Integer>();
	// IntegerArray slotValues = wrappedSM.getSlotValues();
	// for (int i = 0; i < slotValues.size(); i++) {
	// sortedSlotValues.add(slotValues.get(i));
	// }
	// Collections.sort(sortedSlotValues);
	// return getStringRepresentation(indentLevel, sortedSlotValues);
	// }

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) {
		if (wrappedObjectPlusGlobalVars.length == 1) {
			Object wrappedObject = wrappedObjectPlusGlobalVars[0];
			if (wrappedObject instanceof CCPIntegerSlotMention) {
				wrappedSM = (CCPIntegerSlotMention) wrappedObject;
				try {
					jcas = wrappedSM.getCAS().getJCas();
				} catch (CASException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Expected CCPNonComplexSlotMention. Cannot wrap class " + wrappedObject.getClass().getName()
								+ " inside a WrappedCCPIntegerSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPIntegerMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

	// @Override
	// protected void initializeMention() {
	// // do nothing
	// }

	public void addSlotValue(Integer slotValue) throws InvalidInputException {
		IntegerArray updatedIntegerArray = UIMA_Util.addToIntegerArray(wrappedSM.getSlotValues(), slotValue, jcas);
		wrappedSM.setSlotValues(updatedIntegerArray);
	}

	public void addSlotValues(Collection<Integer> slotValues) throws InvalidInputException {
		for (Integer integerToAdd : slotValues) {
			addSlotValue(integerToAdd);
		}
	}

	public Collection<Integer> getSlotValues() {
		Collection<Integer> slotValuesToReturn = new ArrayList<Integer>();
		IntegerArray slotValues = wrappedSM.getSlotValues();
		for (int i = 0; i < slotValues.size(); i++) {
			slotValuesToReturn.add(slotValues.get(i));
		}
		return slotValuesToReturn;
	}

	public void overwriteSlotValues(Integer slotValue) throws InvalidInputException {
		IntegerArray integerArray = new IntegerArray(jcas, 1);
		integerArray.set(0, slotValue);
		wrappedSM.setSlotValues(integerArray);
	}

	public void setSlotValues(Collection<Integer> slotValues) throws InvalidInputException {
		List<Integer> slotValuesList = new ArrayList<Integer>(slotValues);
		IntegerArray integerArray = new IntegerArray(jcas, slotValues.size());
		for (int i = 0; i < slotValues.size(); i++) {
			integerArray.set(i, slotValuesList.get(i));
		}
		wrappedSM.setSlotValues(integerArray);
	}

	@Override
	public long getMentionID() {
		return wrappedSM.getMentionID();
	}

	@Override
	public String getMentionName() {
		return wrappedSM.getMentionName();
	}

	@Override
	public void setMentionID(long mentionID) {
		wrappedSM.setMentionID(mentionID);
	}

	@Override
	protected void setMentionName(String mentionName) {
		wrappedSM.setMentionName(mentionName);
	}

	// @Override
	// protected Long getMentionIDForTraversal(int traversalID) {
	// return UIMA_Util.getMentionIDForTraversal(wrappedNCSM, traversalID);
	// }
	//
	// @Override
	// protected void removeMentionIDForTraversal(int traversalID) {
	// UIMA_Util.removeMentionIDForTraversal(wrappedNCSM, traversalID, jcas);
	// }
	//
	// @Override
	// protected void setMentionIDForTraversal(long mentionID, int traversalID) {
	// UIMA_Util.setMentionIDForTraversal(wrappedNCSM, mentionID, traversalID, jcas);
	// }

}
