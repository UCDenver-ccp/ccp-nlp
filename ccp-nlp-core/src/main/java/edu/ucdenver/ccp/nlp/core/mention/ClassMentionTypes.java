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
 * This interface contains some common class mention types.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public interface ClassMentionTypes {


	public static final String TREEBANK_ANNOTATION = "Stanford treebank";
	public static final String NEEDS_ANNOTATION = "needs annotation";

	/** ClassMention Identifier - if this changes, Annotation DB must be adjusted accordingly */
	public static final String CLASS_MENTION_TYPE = "class mention";

	/** for Olga's concept recognizers */
	public final static String ORGAN = "organ";

	public final static String DISEASE = "disease";

	/** Document level Class Mentions */
	public final static String HTML_PARAGRAPH = "html paragraph";

	public final static String HTML_PARAGRAPH_START = "paragraph start";

	public final static String HTML_PARAGRAPH_END = "paragraph end";

	public final static String HTML_PARAGRAPH_TAG = "paragraph tag";

	public final static String DOCUMENT_TITLE = "title";

	public final static String DOCUMENT_SECTION_HEADING = "section heading";

	public final static String DOCUMENT_SUBSECTION_HEADING = "subsection heading";

	public final static String DOCUMENT_TABLE_LEGEND = "table caption";

	public final static String DOCUMENT_FIGURE_LEGEND = "figure caption";

	/** Syntactic Class Mentions */
	public static final String TOKEN = "token";
	// public static final String TOKEN_PART_OF_SPEECH_LABEL = "part of speech label";

	public static final String PHRASE = "phrase";
	// public static final String PHRASE_TYPE_LABEL = "phrase type label";

	public static final String CLAUSE = "clause";
	// public static final String CLAUSE_TYPE_LABEL = "clause type label";

	public static final String SENTENCE = "sentence";

	public static final String PARAGRAPH = "paragraph";
	// public static final String DOCUMENTSECTION = "documentSection";

	// public static final String TYPED_DEPENDENCY = "typedDependency";

	/** Semantic predicates */
	public static final String EXPRESSION = "expression";

	/** Semantic Class Mentions */
	public static final String PROTEIN = "protein";

	public static final String GENE = "gene";

	public static final String CHEMICAL = "chemical";

	public static final String CELL_TYPE = "cell";

	public static final String PDG_ACTION = "action";

	public static final String TRANSPORT_GATED = "gated transport";

	public static final String GENE_OR_GENE_PRODUCT = "gene or gene product";

	public static final String MACROMOLECULE = "macromolecule";

	public static final String NUCLEIC_ACID = "nucleic acid";

	/* created originally for Bio1 corpus */
	public static final String CELL_LINE = "cell line";

	public static final String VIRUS = "virus";

	public static final String TISSUE = "tissue";

	public static final String SUBCELLULAR_LOCATION = "subcellular location";

	public static final String ORGANISM = "organism";

	public static final String DNA = "DNA";

	public static final String RNA = "RNA";

	/* created originally for iProLink corpus */
	public static final String PROTEIN_ACRONYM = "acronym";

	public static final String PROTEIN_COMPOUND = "compound protein";

	public static final String PROTEIN_LONG_FORM = "long form protein";

	/* for BioEI corpus */
	public static final String GENE_GENERIC = "generic gene";

	/** For Penn Bio Tagger */
	public static final String MALIGNANCY = "malignancy";

	public static final String VARIATION_EVENT = "variation-event";

	public static final String VARIATION_TYPE = "variation-type";

	/** For GENIA tagger */
	public static final String GENIA_other_artificial_source = "GENIA other artificial source";

	public static final String PROTEIN_COMPLEX = "protein complex";

	public static final String GENIA_other_organic_compound = "GENIA other organic compound";

	public static final String GENIA_RNA_substructure = "RNA substructure";

	public static final String GENIA_protein_substructure = "protein substructure";

	public static final String GENIA_carbohydrate = "carbohydrate";

	public static final String GENIA_nucleotide = "nucleotide";

	public static final String GENIA_peptide = "peptide";

	public static final String GENIA_multi_cell = "multi cell";

	public static final String GENIA_lipid = "lipid";

	public static final String GENIA_atom = "atom";

	public static final String GENIA_RNA_NA = "GENIA RNA N/A";

	public static final String GENIA_body_part = "body part";

	public static final String GENIA_polynucleotide = "polynucleotide";

	public static final String GENIA_inorganic = "inorganic";

	public static final String GENIA_protein_domain_or_region = "protein domain or region";

	public static final String GENIA_DNA_domain_or_region = "DNA domain or region";

	public static final String GENIA_DNA_substructure = "DNA substructure";

	public static final String GENIA_protein_family_or_group = "protein family or group";

	public static final String GENIA_cell_component = "cell component";

	public static final String GENIA_RNA_domain_or_region = "RNA domain or region";

	public static final String GENIA_other_name = "GENIA other name";

	public static final String GENIA_DNA_NA = "GENIA DNA N/A";

	public static final String GENIA_protein_NA = "GENIA protein N/A";

	public static final String GENIA_protein_subunit = "protein subunit";

	public static final String GENIA_RNA_family_or_group = "RNA family or group";

	public static final String GENIA_DNA_family_or_group = "DNA family or group";

	public static final String GENIA_mono_cell = "mono cell";

	/** for Mutation project */
	public static final String MUTATION_EVENT = "mutation event";

	public static final String SUBSTITUTION = "substitution";

	public static final String DELETION = "deletion";

	public static final String INSERTION = "insertion";

	public static final String BIOLOGICAL_SEQUENCE = "biological sequence";

	public static final String POLYPEPTIDE_SEQUENCE = "polypeptide sequence";

	public static final String BIOLOGICAL_SEQUENCE_POSITION = "biological sequence position";

	public static final String BIOLOGICAL_SEQUENCE_ELEMENT = "biological sequence element";

	public static final String AMINO_ACID = "amino acid";

	public static final String ALANINE = "Alanine, Ala, A";

	public static final String GLYCINE = "Glycine, Gly, G";

	public static final String VALINE = "Valine, Val, V";

	public static final String LEUCINE = "Leucine, Leu, L";

	public static final String ISOLEUCINE = "Isoleucine, Ile, I";

	public static final String METHIONINE = "Methionine, Met, M";

	public static final String PHENYLALANINE = "Phenylalanine, Phe, F";

	public static final String TYROSINE = "Tyrosine, Tyr, Y";

	public static final String TRYPTOPHAN = "Tryptophan, Trp, W";

	public static final String SERINE = "Serine, Ser, S";

	public static final String PROLINE = "Proline, Pro, P";

	public static final String THREONINE = "Threonine, Thr, T";

	public static final String CYSTEINE = "Cysteine, Cys, C";

	public static final String ASPARAGINE = "Asparagine, Asn, N";

	public static final String GLUTAMINE = "Glutamine, Gln, Q";

	public static final String LYSINE = "Lysine, Lys, K";

	public static final String HISTIDINE = "Histidine, His, H";

	public static final String ARGININE = "Arginine, Arg, R";

	public static final String ASPARTATE = "Aspartate, Asp, D";
    public static final String POLYPEPTIDE = "polypeptide";

	public static final String GLUTAMATE = "Glutamate, Glu, E";

	public static final String BIOLOGICAL_SUBSEQUENCE = "biological subsequence";

	public static final String POLYPEPTIDE_SUBSEQUENCE = "polypeptide subsequence";

	/** For Nominalization project */
	public static final String NOMINALIZATION = "nominalization";

	public static final String NOMINALIZATION_PHOSPHORYLATION = "Phosphorylation";

	public static final String NOMINALIZATION_MUTATION = "Mutation";

	public static final String NOMINALIZATION_FUNCTION = "Function";

	public static final String NOMINALIZATION_RESPONSE = "Response";

	public static final String NOMINALIZATION_REGULATION = "Regulation";

	public static final String NOMINALIZATION_DEVELOPMENT = "Development";

	public static final String NOMINALIZATION_GROWTH = "Growth";

	public static final String NOMINALIZATION_TRANSCRIPTION = "Transcription";

	public static final String NOMINALIZATION_ACTIVATION = "Activation";

	public static final String NOMINALIZATION_INTERACTION = "Interaction";

	public static final String NOMINALIZATION_EXPRESSION = "Expression";

	public static final String NOMINALIZATION_INHIBITION = "Inhibition";

	/** For MedLEE */
	public static final String UNDEFINED = "undefined";

	public static final String DOCUMENT_SECTION = "section";

	public static final String DOCUMENT_SUBSECTION = "subsection";

	/** For Species Classification */
	public static final String TAXON = "taxon";

	public static final String TRANSCRIPT = "transcript";

	
	
	/* created for CRAFT-trained ABNER */
	// public static final String GENE =	"gene"; 
	// public static final String POLYPEPTIDE = 	"polypeptide"; 		
	 public static final String PROBE =	"probe";			
	// public static final String TRANSCRIPT =	"transcript";		
	 public static final String M_RNA = 	"mRNA"; 			
	 public static final String C_DNA =	"cDNA"; 			
	 public static final String TRANSGENE =	"transgene";		
	 public static final String ENGINEERED_REGION =	"engineered_region"; 
	 public static final String PSEUDOGENE =	"pseudogene"; 		
	 public static final String C_DNA_CLONE = "cDNA_clone"; 		
	 public static final String CLONE =	"clone"; 			
	 //public static final String RNA = "RNA"; 				
	 public static final String PROMOTER = "promoter";			
	 public static final String VECTOR_REPLICON = "vector_replicon"; 	
	 public static final String ANTISENSE_PROBE =	"antisense_probe"; 	
	 public static final String FUSION = "fusion";			
	 public static final String QTL = "QTL"; 				
	 public static final String PLASMID = "plasmid";			
	 public static final String GENE_CASSETTE = "gene_cassette"; 	
	 public static final String C_DNA_PROBE = "cDNA_probe"; 		
	 public static final String RNA_I_PLASMID_VECTOR = "RNAi_plasmid_vector";
	 public static final String S_I_RNA_VECTOR = "siRNA_vector"; 	
	 public static final String RNA_PROBE = "RNA_probe";		
	
	 public static final String PRIMER = "primer"; 			
	 public static final String M_RNA_PROBE = "mRNA_probe"; 		
	 public static final String S_I_RNA = "siRNA"; 			
	 public static final String RNA_I_VECTOR = "RNAi_vector"; 		
	
}
