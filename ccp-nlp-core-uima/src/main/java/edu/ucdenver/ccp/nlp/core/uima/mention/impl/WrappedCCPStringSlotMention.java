package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPStringSlotMention extends StringSlotMention {

	private CCPStringSlotMention wrappedSM;
	private JCas jcas;

	public WrappedCCPStringSlotMention(CCPStringSlotMention ccpSSM) {
		super(new UimaMentionTraversalTracker(ccpSSM), ccpSSM);
	}

	@Override
	public CCPStringSlotMention getWrappedObject() {
		return wrappedSM;
	}

//	@Override
//	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
//		List<String> sortedSlotValues = new ArrayList<String>();
//		StringArray slotValues = wrappedSM.getSlotValues();
//		for (int i = 0; i < slotValues.size(); i++) {
//			sortedSlotValues.add(slotValues.get(i));
//		}
//		Collections.sort(sortedSlotValues);
//		return getStringRepresentation(indentLevel, sortedSlotValues);
//	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		if (wrappedObjectPlusGlobalVars.length == 1) {
			Object wrappedObject = wrappedObjectPlusGlobalVars[0];
			if (wrappedObject instanceof CCPStringSlotMention) {
				wrappedSM = (CCPStringSlotMention) wrappedObject;
				jcas = wrappedSM.getCAS().getJCas();
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Expected CCPNonComplexSlotMention. Cannot wrap class " + wrappedObject.getClass().getName()
								+ " inside a WrappedCCPNonComplexSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPComplexSlotMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	public void addSlotValue(String slotValue) throws InvalidInputException {
		StringArray updatedStringArray = UIMA_Util.addToStringArray(wrappedSM.getSlotValues(), slotValue, jcas);
		wrappedSM.setSlotValues(updatedStringArray);
	}

	public void addSlotValues(Collection<String> slotValues) throws InvalidInputException {
		for (String stringToAdd : slotValues) {
			addSlotValue(stringToAdd);
		}
	}

	public Collection<String> getSlotValues() {
		Collection<String> slotValuesToReturn = new ArrayList<String>();
		StringArray slotValues = wrappedSM.getSlotValues();
		for (int i = 0; i < slotValues.size(); i++) {
			slotValuesToReturn.add(slotValues.get(i));
		}
		return slotValuesToReturn;
	}

	public void overwriteSlotValues(String slotValue) throws InvalidInputException {
		StringArray stringArray = new StringArray(jcas, 1);
		stringArray.set(0, slotValue);
		wrappedSM.setSlotValues(stringArray);
	}

	public void setSlotValues(Collection<String> slotValues) throws InvalidInputException {
		List<String> slotValuesList = new ArrayList<String>(slotValues);
		StringArray stringArray = new StringArray(jcas, slotValues.size());
		for (int i = 0; i < slotValues.size(); i++) {
			stringArray.set(i, slotValuesList.get(i));
		}
		wrappedSM.setSlotValues(stringArray);
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
