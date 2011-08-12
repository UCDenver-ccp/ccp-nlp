/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.core.mention;

/**
 * Some common slot mention types.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public interface SlotMentionTypes {
	/** SlotMention Identifier - if this changes, Annotation DB must be adjusted accordingly */
	public static final String SLOT_MENTION_TYPE = "slot mention";

	/** A slot for holding ontology IDs */
	public static final String ONTOLOGY_ID = "ontology ID";

	// public static final String SLOT_VALUE_TYPE = "slot value";

	/** Document-level Slot Mentions */
	public static final String PROCESSED_TEXT_SLOT = "processed text";

	/** Syntactic Slot Mentions */
	public static final String TOKEN_PARTOFSPEECH = "partOfSpeech";
	// public static final String TAGSET_LABEL = "label";
	public static final String TAGSET = "tag set";
    
    public static final String CONCEPT_ID = "concept ID";
    
    public static final String ISA_LEVEL = "IS-A Level";
    
    public static final String IS_TOP_LEVEL = "Is top level";
    
    public static final String CONCEPT_MATCH_TYPE = "Concept match type";
    
    public static final String TOP_LEVEL_CONCEPT_ID = "top level concept id";

    public static final String CONTEXT_CONCEPT_ID = "context concept id";
    
    public static final String MAPPED_FROM_ONTOLOGY_ID = "mapped from ontology";
    public static final String MAPPED_FROM_CONCEPT_ID = "mapped from concept";
    
    
    // public static final String SLOT_VALUE_TYPE = "slot value";

	public static final String TOKEN_LEMMA = "lemma";

	public static final String TOKEN_STEM = "stem";

	public static final String TOKEN_NUMBER = "tokenNumber";

	public static final String TOKEN_IS_ANAPHOR_FOR = "isAnaphorFor";

	public static final String TOKEN_TYPED_DEPENDENCY = "typedDependency";

	// public static final String TYPEDEPENDENCY_GOVERNOR_TOKEN = "governorToken";
	// public static final String TYPEDEPENDENCY_DEPENDENT_TOKEN = "dependentToken";
	// public static final String TYPEDEPENDENCY_RELATION = "relation";

	public static final String PHRASE_TYPE = "phraseType";

	public static final String PHRASE_LEVEL = "phraseLevel";

	public static final String PHRASE_NUMBER = "phraseNumber";

	public static final String CLAUSE_TYPE = "clauseType";

	public static final String CLAUSE_LEVEL = "clauseLevel";

	public static final String CLAUSE_NUMBER = "clauseNumber";

	/** Semantic Slot Mentions */
	public static final String PROTEIN_ENTREZGENEID = "entrez_gene_id";

	public static final String EXPRESSION_LOCATION = "location";
    
    public static final String TREEBANK_STRING = "treebank-pennprint";

	public static final String EXPRESSION_EXPRESSED_GENE_OR_GENE_PRODUCT = "expressed gene or gene product";

	public static final String PART_OF = "part of";

	/** for Mutation project */
	public static final String BIOLOGICAL_SEQUENCE_SEQUENCE = "sequence";

	/** Document Section and SubSection slots */
	public static final String DOCUMENT_SECTION_NAME = "section name";

	public static final String DOCUMENT_SUBSECTION_NAME = "subsection name";

	public static final String TABLE_ID = "table id";

	public static final String FIGURE_ID = "figure id";

	/** for Olga's concept recognizers */
	public static final String MESH_ID = "MeshID";

	/** for MedLEE */
	public static final String MEDLEE_ID = "MedLEE ID";

	/** For Species Classification */
	public static final String NCBI_TAX_ID = "NCBI Taxon ID";

	public static final String ESEMBL_GENE_ID = "Ensembl Gene ID";

	// id used in "a1" files to be carried through from input to output
	public static final String BIONLP09_ID = "BioNLP09 ID";

}
