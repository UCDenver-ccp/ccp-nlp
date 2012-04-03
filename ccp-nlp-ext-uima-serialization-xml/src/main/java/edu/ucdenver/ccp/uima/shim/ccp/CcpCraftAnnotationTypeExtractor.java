package edu.ucdenver.ccp.uima.shim.ccp;

import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationTypeExtractor;

public class CcpCraftAnnotationTypeExtractor implements AnnotationTypeExtractor {
	private static final String CRAFT_TAXONOMY_ID_SLOT = "taxonomy ID";
	private static final String CRAFT_ENTREZ_GENE_ID_SLOT_NAME = "has Entrez Gene ID";
	@Override
	public String getAnnotationType(Annotation annotation) {
		if (annotation instanceof CCPTextAnnotation) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotation;
			CCPClassMention cm = ccpTa.getClassMention();
			String mentionName = cm.getMentionName();
//			System.out.println("mention name: " + mentionName);
			if (mentionName.equalsIgnoreCase("organism")) {
				CCPIntegerSlotMention taxIdSlot = (CCPIntegerSlotMention) UIMA_Util.getPrimitiveSlotMentionByName(cm, CRAFT_TAXONOMY_ID_SLOT);
				IntegerArray taxIds = taxIdSlot.getSlotValues();
				if (taxIds.size() != 1)
					throw new RuntimeException("Multiple taxonomy Ids observed in one annotation");
				return "NCBITaxon:" + new NcbiTaxonomyID(taxIds.get(0)).toString();
			} else if (hasEntrezGeneIdSlot(cm)) {
				return "EG:" + getEntrezGeneIdSlotFiller(cm).toString();
			} else 
				return ((CCPTextAnnotation) annotation).getClassMention().getMentionName();
		}
		throw new IllegalArgumentException("Cannot return type for a non-CCPTextAnnotation annotation: "
				+ annotation.getClass().getName());
	}

	
	/**
	 * Returns the {@link EntrezGeneID} associated with the input {@link CCPClassMention}
	 * @param cm
	 * @return
	 * @throws RuntimeException if an annotation is found to be associated with multiple Entrez Gene IDs
	 */
	private static EntrezGeneID getEntrezGeneIdSlotFiller(CCPClassMention cm) {
		CCPIntegerSlotMention egIdSlot = (CCPIntegerSlotMention)UIMA_Util.getSlotMentionByName(cm, CRAFT_ENTREZ_GENE_ID_SLOT_NAME);
		IntegerArray egIds= egIdSlot.getSlotValues();
		if (egIds.size() != 1)
			throw new RuntimeException("Multiple entrez gene Ids observed in a single annotation.");
		return new EntrezGeneID(egIds.get(0));
	}

	/**
	 * Returns true if the input class mention is part of the Entrez Gene ontology by checking to see if it has a slot for the Entrez Gene ID
	 * @param cm
	 * @return true if the input {@link CCPClassMention} has an Entrez Gene ID slot
	 */
	private static boolean hasEntrezGeneIdSlot(CCPClassMention cm) {
		return UIMA_Util.getSlotMentionByName(cm, CRAFT_ENTREZ_GENE_ID_SLOT_NAME) != null;
	}
	
}
