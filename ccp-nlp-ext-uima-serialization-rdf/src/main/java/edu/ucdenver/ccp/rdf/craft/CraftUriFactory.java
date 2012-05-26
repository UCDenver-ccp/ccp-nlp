package edu.ucdenver.ccp.rdf.craft;

import java.util.Collection;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.DataSourceIdentifierUriFactory;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class CraftUriFactory extends DataSourceIdentifierUriFactory {

	private static final Set<String> TAXONOMIC_RANKS = CollectionsUtil.createSet("taxonomic_rank", "class", "family",
			"forma", "genus", "infraclass", "infraorder", "kingdom", "order", "parvorder", "phylum", "species",
			"species_group", "species_subgroup", "subclass", "subfamily", "subgenus", "subkingdom", "suborder",
			"subphylum", "subspecies", "subtribe", "superclass", "superfamily", "superkingdom", "superorder",
			"superphylum", "tribe", "varietas");

	private static final String CRAFT_NAMESPACE = "http://craft.ucdenver.edu/";

	public static final URI CONTINUANT_URI = new URIImpl("http://www.ifomis.org/bfo/1.1/snap#Continuant");
	public static final URI ITALIC_URI = new URIImpl("http://craft.ucdenver.edu/iao/italic");
	public static final URI BOLD_URI = new URIImpl("http://craft.ucdenver.edu/iao/bold");
	public static final URI SUP_URI = new URIImpl("http://craft.ucdenver.edu/iao/sup");
	public static final URI SUB_URI = new URIImpl("http://craft.ucdenver.edu/iao/sub");
	public static final URI UNDERLINE_URI = new URIImpl("http://craft.ucdenver.edu/iao/underline");
	public static final URI SECTION_URI = new URIImpl("http://purl.obolibrary.org/obo/IAO_0000314"); //iao:document part

	// TODO: this can't have a trailing slash b/c the UUID gets appended to it. Future generation of
	// annotation URIs could check for the slash and remove it if necessary
	private static final String CRAFT_ANNOTATION_NAMESPACE = "http://craft.ucdenver.edu/annotation";

	@Override
	public URI getResourceUri(AnnotationDataExtractor annotationDataExtractor, Annotation annotation) {
		CraftOrganismAnnotationAttributeExtractor taxIdAttributeExtractor = (CraftOrganismAnnotationAttributeExtractor) annotationDataExtractor
				.getAnnotationAttributeExtractor(CraftAnnotationAttribute.TAXONOMY_ID);
		CraftEntrezGeneAnnotationAttributeExtractor egAttributeExtractor = (CraftEntrezGeneAnnotationAttributeExtractor) annotationDataExtractor
				.getAnnotationAttributeExtractor(CraftAnnotationAttribute.ENTREZ_GENE_ID);
		String type = annotationDataExtractor.getAnnotationType(annotation);
		if (type.equals("italic")) {
			return ITALIC_URI;
		} else if (type.equals("bold")) {
			return BOLD_URI;
		} else if (type.equals("sup")) {
			return SUP_URI;
		} else if (type.equals("sub")) {
			return SUB_URI;
		} else if (type.equals("underline")) {
			return UNDERLINE_URI;
		} else if (type.equals("section")) {
			return SECTION_URI;
		} else if (type.equals("independent_continuant")) {
			return CONTINUANT_URI;
		} else if (type.equalsIgnoreCase("organism")) {
			Collection<NcbiTaxonomyID> taxIds = taxIdAttributeExtractor.getAnnotationAttributes(annotation);
			if (taxIds == null || taxIds.size() != 1)
				throw new RuntimeException("Zero or Multiple taxonomy Ids observed in one annotation");
			return getUri(new NcbiTaxonomyID(CollectionsUtil.getSingleElement(taxIds).toString()));
		} else if (TAXONOMIC_RANKS.contains(type)) {
			return new URIImpl(RdfUtil.createUri(RdfNamespace.NCBI_TAXON, "NCBITaxon_" + type).toString());
		} else if (egAttributeExtractor.getAnnotationAttributes(annotation) != null) {
			Collection<EntrezGeneID> entrezGeneIdSlotFillers = egAttributeExtractor.getAnnotationAttributes(annotation);
			if (entrezGeneIdSlotFillers.size() != 1)
				throw new IllegalArgumentException("Zero or Multiple Entrez Gene IDs observed in one annotation");
			return getIaoUri(CollectionsUtil.getSingleElement(entrezGeneIdSlotFillers));
		} else
			return getUri(DataSourceIdResolver.resolveId(type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.rdf.DataSourceIdentifierUriFactory#getAnnotationNamespace()
	 */
	@Override
	protected org.openrdf.model.URI getAnnotationNamespace() {
		return new URIImpl(CRAFT_ANNOTATION_NAMESPACE);
	}

}
