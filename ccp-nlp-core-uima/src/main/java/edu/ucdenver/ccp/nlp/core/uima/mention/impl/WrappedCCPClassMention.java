package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPClassMention extends ClassMention {

	public WrappedCCPClassMention(CCPClassMention ccpCM) {
		super( ccpCM);
	}

	private static Logger logger = Logger.getLogger(WrappedCCPClassMention.class);

	private CCPClassMention wrappedCM;
	private JCas jcas;

//	@Override
//	protected void setWrappedObjectMentionID(long mentionID) {
//		wrappedCM.setMentionID(mentionID);
//	}

	@Override
	public CCPClassMention getWrappedObject() {
		return wrappedCM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlustGlobalVars) {
		if (wrappedObjectPlustGlobalVars[0] instanceof CCPClassMention) {
			wrappedCM = (CCPClassMention) wrappedObjectPlustGlobalVars[0];
			try {
				jcas = wrappedCM.getCAS().getJCas();
			} catch (CASException e) {
				throw new RuntimeException(e);
			}
			
//			mentionName = wrappedCM.getMentionName();
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected CCPClassMention. Cannot wrap a "
					+ wrappedObjectPlustGlobalVars.getClass().getName() + " with this class.");
		}
	}

	public void addComplexSlotMention(ComplexSlotMention csm) {
		if (csm.getWrappedObject() instanceof CCPComplexSlotMention) {
			CCPComplexSlotMention ccpCSM = (CCPComplexSlotMention) csm.getWrappedObject();
			FSArray slotMentions = wrappedCM.getSlotMentions();
			FSArray updatedSlotMentions = UIMA_Util.addToFSArray(slotMentions, ccpCSM, jcas);
			wrappedCM.setSlotMentions(updatedSlotMentions);
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected CCPComplexSlotMention. Cannot add a "
					+ csm.getWrappedObject().getClass().getName()
					+ " as a complex slot filler for the WrappedCCPClassMention class.");
		}
	}

	public void addPrimitiveSlotMention(PrimitiveSlotMention sm) {
		if (sm.getWrappedObject() instanceof CCPPrimitiveSlotMention) {
			CCPPrimitiveSlotMention ccpNCSM = (CCPPrimitiveSlotMention) sm.getWrappedObject();
			FSArray slotMentions = wrappedCM.getSlotMentions();
			FSArray updatedSlotMentions = UIMA_Util.addToFSArray(slotMentions, ccpNCSM, jcas);
			wrappedCM.setSlotMentions(updatedSlotMentions);
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected CCPPrimitiveSlotMention. Cannot add a "
					+ sm.getWrappedObject().getClass().getName()
					+ " as a primitive slot filler for the WrappedCCPClassMention class.");
		}
	}

	public ComplexSlotMention createComplexSlotMention(String slotMentionName) {
		CCPComplexSlotMention ccpCSM = null;
		try {
			ccpCSM = new CCPComplexSlotMention(wrappedCM.getCAS().getJCas());
		} catch (CASException e) {
			e.printStackTrace();
		}
		ccpCSM.setMentionName(slotMentionName);
		return new WrappedCCPComplexSlotMention(ccpCSM);
	}

	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValue) {
		try {
			return CCPPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slotMentionName, slotValue, jcas);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ComplexSlotMention getComplexSlotMentionByName(String complexSlotMentionName) {
		CCPComplexSlotMention ccpCSM = UIMA_Util.getComplexSlotMentionByName(wrappedCM, complexSlotMentionName);
		if (ccpCSM != null) {
			return new WrappedCCPComplexSlotMention(ccpCSM);
		}
		return null;
	}

	public Collection<String> getComplexSlotMentionNames() {
		return UIMA_Util.getSlotNames(wrappedCM, CCPComplexSlotMention.class);
	}

	public Collection<ComplexSlotMention> getComplexSlotMentions() {
		Collection<CCPComplexSlotMention> ccpCSMs = UIMA_Util.getComplexSlotMentions(wrappedCM);
		Collection<ComplexSlotMention> csms = new ArrayList<ComplexSlotMention>();
		for (CCPComplexSlotMention ccpCSM : ccpCSMs) {
			csms.add(new WrappedCCPComplexSlotMention(ccpCSM));
		}
		return csms;
	}

	public PrimitiveSlotMention getPrimitiveSlotMentionByName(String slotMentionName) {
		CCPPrimitiveSlotMention ccpPSM = UIMA_Util.getPrimitiveSlotMentionByName(wrappedCM, slotMentionName);
		if (ccpPSM != null) {
			try {
				return CCPPrimitiveSlotMentionFactory.createPrimitiveSlotMention(ccpPSM);
			} catch (KnowledgeRepresentationWrapperException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Collection<String> getPrimitiveSlotMentionNames() {
		return UIMA_Util.getSlotNames(wrappedCM, CCPPrimitiveSlotMention.class);
	}

	public Collection<PrimitiveSlotMention> getPrimitiveSlotMentions() {
		Collection<CCPPrimitiveSlotMention> ccpPSMs = UIMA_Util.getPrimitiveSlotMentions(wrappedCM);
		Collection<PrimitiveSlotMention> ncsms = new ArrayList<PrimitiveSlotMention>();
		for (CCPPrimitiveSlotMention ccpPSM : ccpPSMs) {
			try {
				ncsms.add(CCPPrimitiveSlotMentionFactory.createPrimitiveSlotMention(ccpPSM));
			} catch (KnowledgeRepresentationWrapperException e) {
				e.printStackTrace();
			}
		}
		return ncsms;
	}

	public TextAnnotation getTextAnnotation() {
		return new WrappedCCPTextAnnotation(wrappedCM.getCcpTextAnnotation());
	}

	public void setComplexSlotMentions(Collection<ComplexSlotMention> complexSlotMentions) {
		List<CCPSlotMention> csmList = new ArrayList<CCPSlotMention>();
		for (ComplexSlotMention csm : complexSlotMentions) {
			if (csm.getWrappedObject() instanceof CCPComplexSlotMention) {
				csmList.add((CCPComplexSlotMention) csm.getWrappedObject());
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot add " + csm.getClass().getName()
						+ " to a CCPClassMention as a ComplexSlotMention. Expected CCPComplexSlotMention.");
			}
		}
		UIMA_Util.removeSlotMentions(wrappedCM, CCPComplexSlotMention.class, jcas);
		UIMA_Util.addSlotMentions(wrappedCM, csmList, jcas);
	}

	public void setPrimitiveSlotMentions(Collection<PrimitiveSlotMention> primitiveSlotMentions) {
		List<CCPSlotMention> psmList = new ArrayList<CCPSlotMention>();
		for (PrimitiveSlotMention psm : primitiveSlotMentions) {
			if (psm.getWrappedObject() instanceof CCPPrimitiveSlotMention) {
				psmList.add((CCPPrimitiveSlotMention) psm.getWrappedObject());
			} else {
				throw new KnowledgeRepresentationWrapperException("Cannot add " + psm.getClass().getName()
						+ " to a CCPClassMention as a PrimitiveSlotMention. Expected CCPNonComplexSlotMention.");
			}
		}
		UIMA_Util.removeSlotMentions(wrappedCM, CCPPrimitiveSlotMention.class, jcas);
		UIMA_Util.addSlotMentions(wrappedCM, psmList, jcas);
	}

	public void setTextAnnotation(TextAnnotation textAnnotation) throws InvalidInputException {
		if (textAnnotation.getWrappedObject() instanceof CCPTextAnnotation) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) textAnnotation.getWrappedObject();
			wrappedCM.setCcpTextAnnotation(ccpTA);
			ccpTA.setClassMention(wrappedCM);
		} else {
			throw new KnowledgeRepresentationWrapperException("Cannot set "
					+ textAnnotation.getWrappedObject().getClass().getName()
					+ " to be the annotation for a WrappedCCPClassMention.");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

	
	@Override
	public long getMentionID() {
		return wrappedCM.getMentionID();
	}

	@Override
	public String getMentionName() {
	return wrappedCM.getMentionName();
	}

	@Override
	public void setMentionID(long mentionID) {
wrappedCM.setMentionID(mentionID);
}

	@Override
	protected void setMentionName(String mentionName) {
wrappedCM.setMentionName(mentionName);
	}
	

//	@Override
//	protected Long getMentionIDForTraversal(int traversalID) {
//		return UIMA_Util.getMentionIDForTraversal(wrappedCM, traversalID);
//	}
//
//	@Override
//	protected void removeMentionIDForTraversal(int traversalID) {
//		UIMA_Util.removeMentionIDForTraversal(wrappedCM, traversalID, jcas);
//	}
//
//	@Override
//	protected void setMentionIDForTraversal(long mentionID, int traversalID) {
//		UIMA_Util.setMentionIDForTraversal(wrappedCM, mentionID, traversalID, jcas);
//	}

}
