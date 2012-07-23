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
package edu.ucdenver.ccp.nlp.uima.mention.impl;

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
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
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

}
