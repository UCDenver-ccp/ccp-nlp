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
import org.apache.uima.jcas.cas.FSArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * Wrapper class for the {@link CCPComplexSlotMention} that complies with the
 * {@link ComplexSlotMention} abstract class
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class WrappedCCPComplexSlotMention extends ComplexSlotMention {

	private CCPComplexSlotMention wrappedCSM;
	private JCas jcas;

	public WrappedCCPComplexSlotMention(CCPComplexSlotMention ccpCSM) {
		super(ccpCSM);
	}

	@Override
	public ClassMention createClassMention(String classMentionName) {
		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName(classMentionName);
		return new WrappedCCPClassMention(ccpCM);
	}

	@Override
	public CCPComplexSlotMention getWrappedObject() {
		return wrappedCSM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) {
		if (wrappedObjectPlusGlobalVars.length == 1) {
			Object wrappedObject = wrappedObjectPlusGlobalVars[0];
			if (wrappedObject instanceof CCPComplexSlotMention) {
				wrappedCSM = (CCPComplexSlotMention) wrappedObject;
				try {
					jcas = wrappedCSM.getCAS().getJCas();
				} catch (CASException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new KnowledgeRepresentationWrapperException("Expected CCPComplexSlotMention. Cannot wrap class "
						+ wrappedObject.getClass().getName() + " inside a WrappedCCPComplexSlotMention.");
			}
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Single input parameter expected for WrappedCCPComplexSlotMention. Instead, observed "
							+ wrappedObjectPlusGlobalVars.length + " parameter(s)");
		}
	}

	public void addSlotValue(ClassMention slotValue) throws InvalidInputException {
		Object wrappedClassMention = slotValue.getWrappedObject();
		if (wrappedClassMention instanceof CCPClassMention) {
			CCPClassMention ccpCM = (CCPClassMention) wrappedClassMention;
			FSArray updatedClassMentions = UIMA_Util.addToFSArray(wrappedCSM.getClassMentions(), ccpCM, jcas);
			wrappedCSM.setClassMentions(updatedClassMentions);
		} else {
			throw new InvalidInputException("Expected CCPClassMention. Cannot add class"
					+ wrappedClassMention.getClass().getName()
					+ " to the ClassMentions list of a CCPComplexSlotMention");
		}
	}

	public void addSlotValues(Collection<ClassMention> slotValues) throws InvalidInputException {
		for (ClassMention cm : slotValues) {
			addSlotValue(cm);
		}
	}

	public Collection<ClassMention> getSlotValues() {
		Collection<ClassMention> classMentionsToReturn = new ArrayList<ClassMention>();
		FSArray slotValues = wrappedCSM.getClassMentions();
		if (slotValues != null) {
			for (int i = 0; i < slotValues.size(); i++) {
				CCPClassMention ccpCM = (CCPClassMention) slotValues.get(i);
				classMentionsToReturn.add(new WrappedCCPClassMention(ccpCM));
			}
		}
		return classMentionsToReturn;
	}

	public void overwriteSlotValues(ClassMention slotValue) throws InvalidInputException {
		Object wrappedClassMention = slotValue.getWrappedObject();
		if (wrappedClassMention instanceof CCPClassMention) {
			CCPClassMention ccpCM = (CCPClassMention) wrappedClassMention;
			FSArray updatedClassMentions = new FSArray(jcas, 1);
			updatedClassMentions.set(0, ccpCM);
			wrappedCSM.setClassMentions(updatedClassMentions);
		} else {
			throw new InvalidInputException("Expected CCPClassMention. Cannot add class"
					+ wrappedClassMention.getClass().getName()
					+ " to the ClassMentions list of a CCPComplexSlotMention");
		}
	}

	public void setSlotValues(Collection<ClassMention> slotValues) throws InvalidInputException {
		List<CCPClassMention> updatedClassMentions = new ArrayList<CCPClassMention>();
		for (ClassMention cm : slotValues) {
			Object wrappedClassMention = cm.getWrappedObject();
			if (wrappedClassMention instanceof CCPClassMention) {
				CCPClassMention ccpCM = (CCPClassMention) wrappedClassMention;
				updatedClassMentions.add(ccpCM);
			} else {
				throw new InvalidInputException("Expected CCPClassMention. Cannot add class"
						+ wrappedClassMention.getClass().getName()
						+ " to the ClassMentions list of a CCPComplexSlotMention");
			}
		}

		FSArray updatedCMs = new FSArray(jcas, updatedClassMentions.size());
		for (int i = 0; i < updatedClassMentions.size(); i++) {
			updatedCMs.set(i, updatedClassMentions.get(i));
		}
		wrappedCSM.setClassMentions(updatedCMs);
	}

	@Override
	public long getMentionID() {
		return wrappedCSM.getMentionID();
	}

	@Override
	public String getMentionName() {
		return wrappedCSM.getMentionName();
	}

	@Override
	public void setMentionID(long mentionID) {
		wrappedCSM.setMentionID(mentionID);
	}

	@Override
	public void setMentionName(String mentionName) {
		wrappedCSM.setMentionName(mentionName);
	}

}
