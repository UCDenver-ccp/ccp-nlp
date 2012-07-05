package edu.ucdenver.ccp.nlp.core.mention;

/**
 * Some common slot mention types.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum SlotMentionType {
	/* A slot for holding ontology IDs */
	ONTOLOGY_ID("ontology ID"),

	/* Document-level Slot Mentions */
	PROCESSED_TEXT_SLOT("processed text"),

	/* misc */
	PART_OF("part of"),
	TREEBANK_STRING("treebank-pennprint"),
	CONCEPT_ID("concept ID"),
	ISA_LEVEL("IS-A Level"),
	IS_TOP_LEVEL("Is top level"),
	CONCEPT_MATCH_TYPE("Concept match type"),
	TOP_LEVEL_CONCEPT_ID("top level concept id"),
	CONTEXT_CONCEPT_ID("context concept id"),
	MAPPED_FROM_ONTOLOGY_ID("mapped from ontology"),
	MAPPED_FROM_CONCEPT_ID("mapped from concept"),

	/* slots for tokens */
	TOKEN_PARTOFSPEECH("partOfSpeech"),
	TAGSET("tag set"),
	TOKEN_LEMMA("lemma"),
	TOKEN_STEM("stem"),
	TOKEN_NUMBER("tokenNumber"),
	TOKEN_IS_ANAPHOR_FOR("isAnaphorFor"),
	TOKEN_TYPED_DEPENDENCY("typedDependency"),

	/* slots for phrases */
	PHRASE_TYPE("phraseType"),
	PHRASE_LEVEL("phraseLevel"),
	PHRASE_NUMBER("phraseNumber"),

	/* slots for causes */
	CLAUSE_TYPE("clauseType"),
	CLAUSE_LEVEL("clauseLevel"),
	CLAUSE_NUMBER("clauseNumber"),

	/* bio entity slots */
	PROTEIN_ENTREZGENEID("entrez_gene_id"),
	MESH_ID("MeshID"),
	NCBI_TAX_ID("NCBI Taxon ID"),
	ENSEMBL_GENE_ID("Ensembl Gene ID"),
	EXPRESSION_LOCATION("location"),
	EXPRESSION_EXPRESSED_GENE_OR_GENE_PRODUCT("expressed gene or gene product"),
	BIOLOGICAL_SEQUENCE_SEQUENCE("sequence"),

	/* Document Section and SubSection slots */
	DOCUMENT_SECTION_NAME("section name"),
	DOCUMENT_SUBSECTION_NAME("subsection name"),
	TABLE_ID("table id"),
	FIGURE_ID("figure id"),

	/* id used in "a1" files to be carried through from input to output */
	BIONLP09_ID("BioNLP09 ID");

	private final String typeName;

	private SlotMentionType(String typeName) {
		this.typeName = typeName;
	}

	public String typeName() {
		return typeName;
	}

}
