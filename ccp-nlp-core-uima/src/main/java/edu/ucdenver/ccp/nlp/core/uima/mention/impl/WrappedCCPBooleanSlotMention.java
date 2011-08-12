package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.BooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPBooleanSlotMention;

public class WrappedCCPBooleanSlotMention extends BooleanSlotMention {

	private CCPBooleanSlotMention wrappedSM;
//	private JCas jcas;

	public WrappedCCPBooleanSlotMention(CCPBooleanSlotMention ccpSSM) {
		super(new UimaMentionTraversalTracker(ccpSSM), ccpSSM);
	}

	@Override
	public CCPBooleanSlotMention getWrappedObject() {
		return wrappedSM;
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Boolean> sortedSlotValues = new ArrayList<Boolean>();
		sortedSlotValues.add(wrappedSM.getSlotValue());
		return getStringRepresentation(indentLevel, sortedSlotValues);
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		if (wrappedObjectPlusGlobalVars.length == 1) {
			Object wrappedObject = wrappedObjectPlusGlobalVars[0];
			if (wrappedObject instanceof CCPBooleanSlotMention) {
				wrappedSM = (CCPBooleanSlotMention) wrappedObject;
//				jcas = wrappedSpM.getCAS().getJCas();
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Expected CCPNonComplexSlotMention. Cannot wrap class " + wrappedObject.getClass().getName()
								+ " inside a WrappedCCPBooleanSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPBooleanMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

//	public void addSlotValue(Boolean slotValue) throws InvalidInputException {
//		BooleanArray updatedBooleanArray = UIMA_Util.addToBooleanArray(wrappedSM.getSlotValues(), slotValue, jcas);
//		wrappedSM.setSlotValues(updatedBooleanArray);
//	}
//
//	public void addSlotValues(Collection<Boolean> slotValues) throws InvalidInputException {
//		for (Boolean booleanToAdd : slotValues) {
//			addSlotValue(booleanToAdd);
//		}
//	}

	public Collection<Boolean> getSlotValues() {
		Collection<Boolean> slotValuesToReturn = new ArrayList<Boolean>();
		slotValuesToReturn.add(wrappedSM.getSlotValue());
		return slotValuesToReturn;
	}

	public void overwriteSlotValues(Boolean slotValue) throws InvalidInputException {
		wrappedSM.setSlotValue(slotValue);
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

	

//	public void setSlotValues(Collection<Boolean> slotValues) throws InvalidInputException {
//		List<Boolean> slotValuesList = new ArrayList<Boolean>(slotValues);
//		BooleanArray booleanArray = new BooleanArray(jcas, slotValues.size());
//		for (int i = 0; i < slotValues.size(); i++) {
//			booleanArray.set(i, slotValuesList.get(i));
//		}
//		wrappedSM.setSlotValues(booleanArray);
//	}

	

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
