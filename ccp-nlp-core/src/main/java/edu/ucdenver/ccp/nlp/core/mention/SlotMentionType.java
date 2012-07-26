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
