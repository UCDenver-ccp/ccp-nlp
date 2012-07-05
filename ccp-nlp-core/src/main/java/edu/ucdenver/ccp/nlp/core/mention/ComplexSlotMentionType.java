

package edu.ucdenver.ccp.nlp.core.mention;

/**
 * This enum contains some common complex slot mention types.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public enum ComplexSlotMentionType {

	/* mutation-related */
	SUBSTITUTION_MUTANT_ELEMENT ("mutant element"),
	SUBSTITUTION_WILDTYPE_ELEMENT ("wild type element"),
	DELETION_DELETED_ELEMENT ("deleted element"),
	INSERTION_INSERTED_ELEMENT ("inserted element"),
	INSERTION_INSERTION_START ("insertion start"),
	BIOLOGICAL_SEQUENCE_ELEMENT_POSITION_IN_SEQUENCE ("position in sequence"),
	BIOLOGICAL_SUBSEQUENCE_START_ELEMENT ("start element"),
	BIOLOGICAL_SUBSEQUENCE_END_ELEMENT ("end element");
	
	private final String typeName;

	private ComplexSlotMentionType(String typeName) {
		this.typeName = typeName;
	}

	public String typeName() {
		return typeName;
	}
}
