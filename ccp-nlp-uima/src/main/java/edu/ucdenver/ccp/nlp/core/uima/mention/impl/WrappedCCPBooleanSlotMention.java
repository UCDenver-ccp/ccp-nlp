/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
		super( ccpSSM);
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
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) {
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
