package edu.ucdenver.ccp.rdf.craft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.jcas.cas.IntegerArray;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class CraftUriFactory extends UriFactory {

	private static final String CRAFT_NAMESPACE = "http://craft.ucdenver.edu/";
	private static final String CRAFT_ANNOTATION_NAMESPACE = "http://craft.ucdenver.edu/annotation/";
	private static final String CRAFT_TAXONOMY_ID_SLOT = "taxonomy ID";
	private static final String CRAFT_ENTREZ_GENE_ID_SLOT_NAME = "has Entrez Gene ID";

	public static final URI RDF_TYPE = RdfUtil.createUri(RdfNamespace.RDF, "type");

	public Collection<URI> getResourceUri(CCPTextAnnotation ccpTa) {
		CCPClassMention cm = ccpTa.getClassMention();
		String mentionName = cm.getMentionName();
		if (mentionName.equalsIgnoreCase("organism")) {
			CCPIntegerSlotMention taxIdSlot = (CCPIntegerSlotMention) UIMA_Util.getPrimitiveSlotMentionByName(cm,
					CRAFT_TAXONOMY_ID_SLOT);
			IntegerArray taxIds = taxIdSlot.getSlotValues();
			if (taxIds == null || taxIds.size() != 1)
				throw new RuntimeException("Zero or Multiple taxonomy Ids observed in one annotation");
			return getUri(CollectionsUtil.createList(new NcbiTaxonomyID(taxIds.get(0))));
		} else if (hasEntrezGeneIdSlot(cm)) {
			return getUri(getEntrezGeneIdSlotFiller(cm));
		} else
			return getUri(CollectionsUtil.createList(DataSourceIdResolver.resolveId(mentionName)));
	}

	public URI getAnnotationUri(long annotationId) {
		try {
			return new URI(CRAFT_ANNOTATION_NAMESPACE + "CRAFT_Annotation_" + Long.toString(annotationId));
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns the {@link EntrezGeneID} associated with the input {@link CCPClassMention}
	 * 
	 * @param cm
	 * @return
	 * @throws RuntimeException
	 *             if an annotation is found to be associated with multiple Entrez Gene IDs
	 */
	private static Collection<EntrezGeneID> getEntrezGeneIdSlotFiller(CCPClassMention cm) {
		CCPIntegerSlotMention egIdSlot = (CCPIntegerSlotMention) UIMA_Util.getSlotMentionByName(cm,
				CRAFT_ENTREZ_GENE_ID_SLOT_NAME);
		IntegerArray egIds = egIdSlot.getSlotValues();
		Collection<EntrezGeneID> ids = new ArrayList<EntrezGeneID>();
		for (int i = 0; i < egIds.size(); i++)
			ids.add(new EntrezGeneID(egIds.get(i)));
		return ids;
	}

	/**
	 * Returns true if the input class mention is part of the Entrez Gene ontology by checking to
	 * see if it has a slot for the Entrez Gene ID
	 * 
	 * @param cm
	 * @return true if the input {@link CCPClassMention} has an Entrez Gene ID slot
	 */
	private static boolean hasEntrezGeneIdSlot(CCPClassMention cm) {
		return UIMA_Util.getSlotMentionByName(cm, CRAFT_ENTREZ_GENE_ID_SLOT_NAME) != null;
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.rdf.UriFactory#getBaseUri()
	 */
	@Override
	public String getBaseUri() {
		return CRAFT_NAMESPACE;
	}

}
