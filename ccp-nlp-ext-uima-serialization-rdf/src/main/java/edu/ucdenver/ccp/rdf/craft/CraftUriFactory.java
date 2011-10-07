package edu.ucdenver.ccp.rdf.craft;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.DataSourceIdentifierUriFactory;

public class CraftUriFactory extends DataSourceIdentifierUriFactory {

	private static final String CRAFT_NAMESPACE = "http://craft.ucdenver.edu/";

	// TODO: this can't have a trailing slash b/c the UUID gets appended to it. Future generation of
	// annotation URIs could check for the slash and remove it if necessary
	private static final String CRAFT_ANNOTATION_NAMESPACE = "http://craft.ucdenver.edu/annotation";

	public URI getResourceUri(AnnotationDataExtractor annotationDataExtractor, Annotation annotation) {
		CraftOrganismAnnotationAttributeExtractor taxIdAttributeExtractor = (CraftOrganismAnnotationAttributeExtractor) annotationDataExtractor
				.getAnnotationAttributeExtractor(CraftAnnotationAttribute.TAXONOMY_ID);
		CraftEntrezGeneAnnotationAttributeExtractor egAttributeExtractor = (CraftEntrezGeneAnnotationAttributeExtractor) annotationDataExtractor
				.getAnnotationAttributeExtractor(CraftAnnotationAttribute.ENTREZ_GENE_ID);
		String type = annotationDataExtractor.getAnnotationType(annotation);
		if (type.equalsIgnoreCase("organism")) {
			Collection<NcbiTaxonomyID> taxIds = taxIdAttributeExtractor.getAnnotationAttributes(annotation);
			if (taxIds == null || taxIds.size() != 1)
				throw new RuntimeException("Zero or Multiple taxonomy Ids observed in one annotation");
			return getUri(new NcbiTaxonomyID(CollectionsUtil.getSingleElement(taxIds).toString()));
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
