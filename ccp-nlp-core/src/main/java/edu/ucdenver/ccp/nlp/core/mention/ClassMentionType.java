package edu.ucdenver.ccp.nlp.core.mention;

/**
 * This enum contains some common class mention types.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum ClassMentionType {

	/* Document-level */
	HTML_PARAGRAPH("html paragraph"),
	HTML_PARAGRAPH_START("paragraph start"),
	HTML_PARAGRAPH_END("paragraph end"),
	HTML_PARAGRAPH_TAG("paragraph tag"),
	DOCUMENT_TITLE("title"),
	DOCUMENT_SECTION_HEADING("section heading"),
	DOCUMENT_SUBSECTION_HEADING("subsection heading"),
	DOCUMENT_TABLE_LEGEND("table caption"),
	DOCUMENT_FIGURE_LEGEND("figure caption"),
	DOCUMENT_SECTION("section"),
	DOCUMENT_SUBSECTION("subsection"),

	/* syntactic structures */
	TOKEN("token"),
	PHRASE("phrase"),
	CLAUSE("clause"),
	SENTENCE("sentence"),
	PARAGRAPH("paragraph"),

	/* bio entities */
	PROTEIN("protein"),
	GENE("gene"),
	ORGAN("organ"),
	DISEASE("disease"),
	CHEMICAL("chemical"),
	CELL_TYPE("cell"),
	GENE_OR_GENE_PRODUCT("gene or gene product"),
	MACROMOLECULE("macromolecule"),
	NUCLEIC_ACID("nucleic acid"),

	/* created originally for Bio1 corpus */
	CELL_LINE("cell line"),
	VIRUS("virus"),
	TISSUE("tissue"),
	SUBCELLULAR_LOCATION("subcellular location"),
	ORGANISM("organism"),
	DNA("DNA"),
	RNA("RNA"),
	TAXON("taxon"),
	TRANSCRIPT("transcript"),

	/* created originally for iProLink corpus */
	PROTEIN_ACRONYM("acronym"),
	PROTEIN_COMPOUND("compound protein"),
	PROTEIN_LONG_FORM("long form protein"),

	/* for BioEI corpus */
	GENE_GENERIC("generic gene"),

	/* For Penn Bio Tagger */
	MALIGNANCY("malignancy"),
	VARIATION_EVENT("variation-event"),
	VARIATION_TYPE("variation-type"),

	/* For GENIA tagger */
	GENIA_other_artificial_source("GENIA other artificial source"),
	PROTEIN_COMPLEX("protein complex"),
	GENIA_other_organic_compound("GENIA other organic compound"),
	GENIA_RNA_substructure("RNA substructure"),
	GENIA_protein_substructure("protein substructure"),
	GENIA_carbohydrate("carbohydrate"),
	GENIA_nucleotide("nucleotide"),
	GENIA_peptide("peptide"),
	GENIA_multi_cell("multi cell"),
	GENIA_lipid("lipid"),
	GENIA_atom("atom"),
	GENIA_RNA_NA("GENIA RNA N/A"),
	GENIA_body_part("body part"),
	GENIA_polynucleotide("polynucleotide"),
	GENIA_inorganic("inorganic"),
	GENIA_protein_domain_or_region("protein domain or region"),
	GENIA_DNA_domain_or_region("DNA domain or region"),
	GENIA_DNA_substructure("DNA substructure"),
	GENIA_protein_family_or_group("protein family or group"),
	GENIA_cell_component("cell component"),
	GENIA_RNA_domain_or_region("RNA domain or region"),
	GENIA_other_name("GENIA other name"),
	GENIA_DNA_NA("GENIA DNA N/A"),
	GENIA_protein_NA("GENIA protein N/A"),
	GENIA_protein_subunit("protein subunit"),
	GENIA_RNA_family_or_group("RNA family or group"),
	GENIA_DNA_family_or_group("DNA family or group"),
	GENIA_mono_cell("mono cell"),

	/* mutation-related */
	MUTATION_EVENT("mutation event"),
	SUBSTITUTION("substitution"),
	DELETION("deletion"),
	INSERTION("insertion"),
	BIOLOGICAL_SEQUENCE("biological sequence"),
	POLYPEPTIDE_SEQUENCE("polypeptide sequence"),
	BIOLOGICAL_SEQUENCE_POSITION("biological sequence position"),
	BIOLOGICAL_SEQUENCE_ELEMENT("biological sequence element"),
	AMINO_ACID("amino acid"),
	ALANINE("Alanine, Ala, A"),
	GLYCINE("Glycine, Gly, G"),
	VALINE("Valine, Val, V"),
	LEUCINE("Leucine, Leu, L"),
	ISOLEUCINE("Isoleucine, Ile, I"),
	METHIONINE("Methionine, Met, M"),
	PHENYLALANINE("Phenylalanine, Phe, F"),
	TYROSINE("Tyrosine, Tyr, Y"),
	TRYPTOPHAN("Tryptophan, Trp, W"),
	SERINE("Serine, Ser, S"),
	PROLINE("Proline, Pro, P"),
	THREONINE("Threonine, Thr, T"),
	CYSTEINE("Cysteine, Cys, C"),
	ASPARAGINE("Asparagine, Asn, N"),
	GLUTAMINE("Glutamine, Gln, Q"),
	LYSINE("Lysine, Lys, K"),
	HISTIDINE("Histidine, His, H"),
	ARGININE("Arginine, Arg, R"),
	ASPARTATE("Aspartate, Asp, D"),
	POLYPEPTIDE("polypeptide"),
	GLUTAMATE("Glutamate, Glu, E"),
	BIOLOGICAL_SUBSEQUENCE("biological subsequence"),
	POLYPEPTIDE_SUBSEQUENCE("polypeptide subsequence"),

	/* created for CRAFT-trained ABNER */
	PROBE("probe"),
	M_RNA("mRNA"),
	C_DNA("cDNA"),
	TRANSGENE("transgene"),
	ENGINEERED_REGION("engineered_region"),
	PSEUDOGENE("pseudogene"),
	C_DNA_CLONE("cDNA_clone"),
	CLONE("clone"),
	PROMOTER("promoter"),
	VECTOR_REPLICON("vector_replicon"),
	ANTISENSE_PROBE("antisense_probe"),
	FUSION("fusion"),
	QTL("QTL"),
	PLASMID("plasmid"),
	GENE_CASSETTE("gene_cassette"),
	C_DNA_PROBE("cDNA_probe"),
	RNA_I_PLASMID_VECTOR("RNAi_plasmid_vector"),
	S_I_RNA_VECTOR("siRNA_vector"),
	RNA_PROBE("RNA_probe"),
	PRIMER("primer"),
	M_RNA_PROBE("mRNA_probe"),
	S_I_RNA("siRNA"),
	RNA_I_VECTOR("RNAi_vector");

	private final String typeName;

	private ClassMentionType(String typeName) {
		this.typeName = typeName;
	}

	public String typeName() {
		return typeName;
	}

}
