package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;
import edu.ucdenver.ccp.wrapper.knowtator.annotation.impl.WrappedKnowtatorAnnotation;

public class WrappedKnowtatorClassMention extends ClassMention {

	private SimpleInstance knowtatorCM;
	private KnowtatorUtil ku;

	public WrappedKnowtatorClassMention(SimpleInstance knowtatorMention, KnowtatorUtil ku) {
		super(
				knowtatorMention, ku);
		this.ku = ku;
	}

	@Override
	public SimpleInstance getWrappedObject() {
		return knowtatorCM;
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception {
		if (wrappedObjectPlusGlobalVars[0] instanceof SimpleInstance) {
			SimpleInstance possibleCM = (SimpleInstance) wrappedObjectPlusGlobalVars[0];

			setGlobalVars(wrappedObjectPlusGlobalVars);
//			System.err.println("KU is null: " + (ku == null));
//			System.err.println("classmentioncls: " + ku.getClassMentionCls().getName());
			if (possibleCM.getDirectType().getName().equals(ku.getClassMentionCls().getName())) {
				knowtatorCM = possibleCM;
				return;
			}
		}
		throw new KnowledgeRepresentationWrapperException("Expected SimpleInstance for a ClassMention. Cannot wrap a "
				+ wrappedObjectPlusGlobalVars.getClass().getName() + " with the WrappedKnowtatorClassMention class.");
	}

	private void setGlobalVars(Object... wrappedObjectPlusGlobalVars) throws KnowledgeRepresentationWrapperException {
		if (wrappedObjectPlusGlobalVars.length > 1) {
			this.ku = (KnowtatorUtil) wrappedObjectPlusGlobalVars[1];
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Expected KnowtatorUtil object in the constructor for the WrappedKnowtatorClassMention.");
		}
	}

//	@Override
//	protected void initializeMention() {
//		// do nothing
//	}

//	@Override
//	protected void setWrappedObjectMentionID(long mentionID) {
//		throw new UnsupportedOperationException(
//				"Mention ID is not supported in the WrappedKnowtatorClassMention class.");
//	}
	
	// @Override
	// protected Long getMentionIDForTraversal(int traversalID) {
	// return ku.getMentionIDForTraversal(knowtatorCM, traversalID);
	// }
	//
	// @Override
	// protected void removeMentionIDForTraversal(int traversalID) {
	// ku.removeTraversalID(traversalID, knowtatorCM);
	// }
	//
	// @Override
	// protected void setMentionIDForTraversal(long mentionID, int traversalID) {
	// ku.setMentionIDForTraversal(mentionID, traversalID, knowtatorCM);
	// }

	public void addComplexSlotMention(ComplexSlotMention csm) throws Exception {
		if (csm instanceof WrappedKnowtatorComplexSlotMention) {
			ku.addSlotMentionToClassMention(knowtatorCM, ((WrappedKnowtatorComplexSlotMention) csm).getWrappedObject());
		} else {
			throw new KnowledgeRepresentationWrapperException("Cannot add complex slot mention of type: "
					+ csm.getClass().getName() + " to a WrappedKnowtatorClassMention.");
		}
	}

	public void addPrimitiveSlotMention(PrimitiveSlotMention sm) throws Exception {
		if (sm instanceof WrappedKnowtatorBooleanSlotMention) {
			ku.addSlotMentionToClassMention(knowtatorCM, ((WrappedKnowtatorBooleanSlotMention) sm).getWrappedObject());
		} else if (sm instanceof WrappedKnowtatorIntegerSlotMention) {
			ku.addSlotMentionToClassMention(knowtatorCM, ((WrappedKnowtatorIntegerSlotMention) sm).getWrappedObject());
		} else if (sm instanceof WrappedKnowtatorFloatSlotMention) {
			ku.addSlotMentionToClassMention(knowtatorCM, ((WrappedKnowtatorFloatSlotMention) sm).getWrappedObject());
		} else if (sm instanceof WrappedKnowtatorStringSlotMention) {
			ku.addSlotMentionToClassMention(knowtatorCM, ((WrappedKnowtatorStringSlotMention) sm).getWrappedObject());
		} else {
			throw new KnowledgeRepresentationWrapperException("Cannot add primitive slot mention of type: "
					+ sm.getClass().getName() + " to a WrappedKnowtatorClassMention.");
		}
	}

	public ComplexSlotMention createComplexSlotMention(String slotMentionName) {
		return new WrappedKnowtatorComplexSlotMention(ku.createKnowtatorSlotMention(slotMentionName), ku);
	}

	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValue) throws KnowledgeRepresentationWrapperException {
		if (slotValue instanceof Boolean) {
			return new WrappedKnowtatorBooleanSlotMention(ku.createKnowtatorPrimitiveSlotMention(slotMentionName,
					slotValue), ku);
		} else if (slotValue instanceof Integer) {
			return new WrappedKnowtatorIntegerSlotMention(ku.createKnowtatorPrimitiveSlotMention(slotMentionName,
					slotValue), ku);
		} else if (slotValue instanceof Float) {
			return new WrappedKnowtatorFloatSlotMention(ku.createKnowtatorPrimitiveSlotMention(slotMentionName,
					slotValue), ku);
		} else if (slotValue instanceof String) {
			return new WrappedKnowtatorStringSlotMention(ku.createKnowtatorPrimitiveSlotMention(slotMentionName,
					slotValue), ku);
		} else {
			throw new KnowledgeRepresentationWrapperException(
					"Cannot create knowtator primitive slot mention for slot value of type: "
							+ slotValue.getClass().getName());
		}

	}

	public ComplexSlotMention getComplexSlotMentionByName(String complexSlotMentionName) {
		for (ComplexSlotMention csm : getComplexSlotMentions()) {
			if (csm.getMentionName().equalsIgnoreCase(complexSlotMentionName)) {
				return csm;
			}
		}
		return null;
	}

	public Collection<String> getComplexSlotMentionNames() {
		return getSlotMentionNames(ku.getComplexSlotMentions(knowtatorCM));
	}

	public Collection<ComplexSlotMention> getComplexSlotMentions() {
		Collection<ComplexSlotMention> csms = new ArrayList<ComplexSlotMention>();
		Collection<SimpleInstance> complexSlotMentionInstances = ku.getComplexSlotMentions(knowtatorCM);
		for (SimpleInstance csmInstance : complexSlotMentionInstances) {
			csms.add(new WrappedKnowtatorComplexSlotMention(csmInstance, ku));
		}
		return csms;
	}

	public PrimitiveSlotMention getPrimitiveSlotMentionByName(String slotMentionName) {
		try {
			for (PrimitiveSlotMention psm : getPrimitiveSlotMentions()) {
				if (psm.getMentionName().equalsIgnoreCase(slotMentionName)) {
					return psm;
				}
			}
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection<String> getPrimitiveSlotMentionNames() {
		return getSlotMentionNames(ku.getPrimitiveSlotMentions(knowtatorCM));
	}

	private Collection<String> getSlotMentionNames(Collection<SimpleInstance> slotMentionInstances) {
		Collection<String> slotMentionNames = new ArrayList<String>();
		for (SimpleInstance smInstance : slotMentionInstances) {
			slotMentionNames.add(ku.getSlotMentionName(smInstance));
		}
		return slotMentionNames;
	}

	public Collection<PrimitiveSlotMention> getPrimitiveSlotMentions() throws KnowledgeRepresentationWrapperException {
		Collection<PrimitiveSlotMention> primitiveSlotMentions = new ArrayList<PrimitiveSlotMention>();
		Collection<SimpleInstance> primitiveSlotMentionInstances = ku.getPrimitiveSlotMentions(knowtatorCM);
		for (SimpleInstance smInstance : primitiveSlotMentionInstances) {
			if (ku.isBooleanSlotMention(smInstance)) {
				primitiveSlotMentions.add( new WrappedKnowtatorBooleanSlotMention(smInstance, ku));
			} else if (ku.isFloatSlotMention(smInstance)) {
				primitiveSlotMentions.add( new WrappedKnowtatorFloatSlotMention(smInstance, ku));
			} else if (ku.isIntegerSlotMention(smInstance)) {
				primitiveSlotMentions.add( new WrappedKnowtatorIntegerSlotMention(smInstance, ku));
			} else if (ku.isStringSlotMention(smInstance)) {
				primitiveSlotMentions.add( new WrappedKnowtatorStringSlotMention(smInstance, ku));
			} else {
				throw new KnowledgeRepresentationWrapperException(
						"Cannot create knowtator primitive slot mention for slot value of type: "
								+ smInstance.getClass().getName());
			}
		}
		return primitiveSlotMentions;
	}

	public TextAnnotation getTextAnnotation() {
		return new WrappedKnowtatorAnnotation(ku.getKnowtatorAnnotationFromClassMention(knowtatorCM), ku);
	}

	public void setComplexSlotMentions(Collection<ComplexSlotMention> complexSlotMentions) throws Exception {
		Collection<SimpleInstance> complexSlotMentionInstances = new ArrayList<SimpleInstance>();
		for (ComplexSlotMention csm : complexSlotMentions) {
			if (csm.getWrappedObject() instanceof SimpleInstance) {
				complexSlotMentionInstances.add((SimpleInstance) csm.getWrappedObject());
			}
		}
		ku.setComplexSlotMentions(knowtatorCM, complexSlotMentionInstances);
	}

	public void setPrimitiveSlotMentions(Collection<PrimitiveSlotMention> primitiveSlotMentions) throws Exception {
		Collection<SimpleInstance> primitiveSlotMentionInstances = new ArrayList<SimpleInstance>();
		for (PrimitiveSlotMention psm : primitiveSlotMentions) {
			if (psm.getWrappedObject() instanceof SimpleInstance) {
				primitiveSlotMentionInstances.add((SimpleInstance) psm.getWrappedObject());
			}
		}
		ku.setPrimitiveSlotMentions(knowtatorCM, primitiveSlotMentionInstances);
	}

	public void setTextAnnotation(TextAnnotation textAnnotation) throws InvalidInputException {
		if (textAnnotation.getWrappedObject() instanceof SimpleInstance) {
			ku.setKnowtatorAnnotationForClassMention(knowtatorCM, (SimpleInstance) textAnnotation.getWrappedObject());
		} else {
			throw new KnowledgeRepresentationWrapperException("Cannot set "
					+ textAnnotation.getWrappedObject().getClass().getName()
					+ " to be the annotation for a WrappedKnowtatorClassMention.");
		}

	}
	
	@Override
	public long getMentionID() {
		return ku.getMentionID(knowtatorCM);
	}

	@Override
	public String getMentionName() {
		return ku.getClassMentionName(knowtatorCM);
}

	@Override
	public void setMentionID(long mentionID) {
		ku.setMentionID(knowtatorCM, mentionID);
	}

	@Override
	protected void setMentionName(String mentionName) {
		ku.setClassMentionName(knowtatorCM, mentionName);
	}

}
