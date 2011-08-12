package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collection;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;

public interface IClassMention {

	public void setTextAnnotation(TextAnnotation textAnnotation) throws InvalidInputException;
	
	public TextAnnotation getTextAnnotation();

	public Collection<ComplexSlotMention> getComplexSlotMentions();

	public void setComplexSlotMentions(Collection<ComplexSlotMention> complexSlotMentions) throws Exception;

	public Collection<PrimitiveSlotMention> getPrimitiveSlotMentions() throws KnowledgeRepresentationWrapperException;

	public void setPrimitiveSlotMentions(Collection<PrimitiveSlotMention> primitiveSlotMentions) throws Exception;

	public void addComplexSlotMention(ComplexSlotMention csm) throws Exception;

	public void addPrimitiveSlotMention(PrimitiveSlotMention sm) throws Exception;

	public Collection<String> getPrimitiveSlotMentionNames();

	public Collection<String> getComplexSlotMentionNames();

	public PrimitiveSlotMention getPrimitiveSlotMentionByName(String slotMentionName);

	public ComplexSlotMention getComplexSlotMentionByName(String complexSlotMentionName);

	public ComplexSlotMention createComplexSlotMention(String slotMentionName);

	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Object slotValue) throws KnowledgeRepresentationWrapperException;

//	public void removeComplexSlotMention(String slotMentionName);
//	
//	public void removePrimitiveSlotMention(String slotMentionName);
		
	
}
