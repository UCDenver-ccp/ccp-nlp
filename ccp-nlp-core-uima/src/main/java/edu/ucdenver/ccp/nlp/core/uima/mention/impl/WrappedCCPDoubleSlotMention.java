package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.DoubleSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPDoubleSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPDoubleSlotMention extends DoubleSlotMention {

	private CCPDoubleSlotMention wrappedSM;
	private JCas jcas;

	public WrappedCCPDoubleSlotMention(CCPDoubleSlotMention ccpSSM) {
		super(new UimaMentionTraversalTracker(ccpSSM), ccpSSM);
	}

	@Override
	public CCPDoubleSlotMention getWrappedObject() {
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
			if (wrappedObject instanceof CCPDoubleSlotMention) {
				wrappedSM = (CCPDoubleSlotMention) wrappedObject;
				jcas = wrappedSM.getCAS().getJCas();
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Expected CCPNonComplexSlotMention. Cannot wrap class " + wrappedObject.getClass().getName()
								+ " inside a WrappedCCPDoubleSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPDoubleMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	public void addSlotValue(Double slotValue) throws InvalidInputException {
		DoubleArray updatedDoubleArray = UIMA_Util.addToDoubleArray(wrappedSM.getSlotValues(), slotValue, jcas);
		wrappedSM.setSlotValues(updatedDoubleArray);
	}

	public void addSlotValues(Collection<Double> slotValues) throws InvalidInputException {
		for (Double floatToAdd : slotValues) {
			addSlotValue(floatToAdd);
		}
	}

	public Collection<Double> getSlotValues() {
		Collection<Double> slotValuesToReturn = new ArrayList<Double>();
		DoubleArray slotValues = wrappedSM.getSlotValues();
		for (int i = 0; i < slotValues.size(); i++) {
			slotValuesToReturn.add(slotValues.get(i));
		}
		return slotValuesToReturn;
	}

	public void overwriteSlotValues(Double slotValue) throws InvalidInputException {
		DoubleArray floatArray = new DoubleArray(jcas, 1);
		floatArray.set(0, slotValue);
		wrappedSM.setSlotValues(floatArray);
	}

	public void setSlotValues(Collection<Double> slotValues) throws InvalidInputException {
		List<Double> slotValuesList = new ArrayList<Double>(slotValues);
		DoubleArray floatArray = new DoubleArray(jcas, slotValues.size());
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
