package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPComplexSlotMention extends ComplexSlotMention {

	private CCPComplexSlotMention wrappedCSM;
	private JCas jcas;

	public WrappedCCPComplexSlotMention(CCPComplexSlotMention ccpCSM) {
		super(  ccpCSM);
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
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		if (wrappedObjectPlusGlobalVars.length == 1) {
			Object wrappedObject = wrappedObjectPlusGlobalVars[0];
			if (wrappedObject instanceof CCPComplexSlotMention) {
				wrappedCSM = (CCPComplexSlotMention) wrappedObject;
				jcas = wrappedCSM.getCAS().getJCas();
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

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

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
	protected void setMentionName(String mentionName) {
wrappedCSM.setMentionName(mentionName);
	}
	
	// @Override
	// protected Long getMentionIDForTraversal(int traversalID) {
	// return UIMA_Util.getMentionIDForTraversal(wrappedCSM, traversalID);
	// }
	//	
	// @Override
	// protected void removeMentionIDForTraversal(int traversalID) {
	// UIMA_Util.removeMentionIDForTraversal(wrappedCSM, traversalID, jcas);
	// }
	//
	// @Override
	// protected void setMentionIDForTraversal(long mentionID, int traversalID) {
	// UIMA_Util.setMentionIDForTraversal(wrappedCSM, mentionID, traversalID, jcas);
	// }
}
