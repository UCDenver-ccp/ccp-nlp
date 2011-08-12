package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FloatArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.FloatSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPFloatSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPFloatSlotMention extends FloatSlotMention {

	private CCPFloatSlotMention wrappedSM;
	private JCas jcas;

	public WrappedCCPFloatSlotMention(CCPFloatSlotMention ccpSSM) {
		super(new UimaMentionTraversalTracker(ccpSSM), ccpSSM);
	}

	@Override
	public CCPFloatSlotMention getWrappedObject() {
		return wrappedSM;
	}

//	@Override
//	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
//		List<Float> sortedSlotValues = new ArrayList<Float>();
//		FloatArray slotValues = wrappedSM.getSlotValues();
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
			if (wrappedObject instanceof CCPFloatSlotMention) {
				wrappedSM = (CCPFloatSlotMention) wrappedObject;
				jcas = wrappedSM.getCAS().getJCas();
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Expected CCPNonComplexSlotMention. Cannot wrap class " + wrappedObject.getClass().getName()
								+ " inside a WrappedCCPFloatSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPFloatMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	public void addSlotValue(Float slotValue) throws InvalidInputException {
		FloatArray updatedFloatArray = UIMA_Util.addToFloatArray(wrappedSM.getSlotValues(), slotValue, jcas);
		wrappedSM.setSlotValues(updatedFloatArray);
	}

	public void addSlotValues(Collection<Float> slotValues) throws InvalidInputException {
		for (Float floatToAdd : slotValues) {
			addSlotValue(floatToAdd);
		}
	}

	public Collection<Float> getSlotValues() {
		Collection<Float> slotValuesToReturn = new ArrayList<Float>();
		FloatArray slotValues = wrappedSM.getSlotValues();
		for (int i = 0; i < slotValues.size(); i++) {
			slotValuesToReturn.add(slotValues.get(i));
		}
		return slotValuesToReturn;
	}

	public void overwriteSlotValues(Float slotValue) throws InvalidInputException {
		FloatArray floatArray = new FloatArray(jcas, 1);
		floatArray.set(0, slotValue);
		wrappedSM.setSlotValues(floatArray);
	}

	public void setSlotValues(Collection<Float> slotValues) throws InvalidInputException {
		List<Float> slotValuesList = new ArrayList<Float>(slotValues);
		FloatArray floatArray = new FloatArray(jcas, slotValues.size());
		for (int i = 0; i < slotValues.size(); i++) {
			floatArray.set(i, slotValuesList.get(i));
		}
		wrappedSM.setSlotValues(floatArray);
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
