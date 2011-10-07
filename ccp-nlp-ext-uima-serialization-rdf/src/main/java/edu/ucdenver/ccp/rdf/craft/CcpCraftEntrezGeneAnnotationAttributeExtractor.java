package edu.ucdenver.ccp.rdf.craft;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class CcpCraftEntrezGeneAnnotationAttributeExtractor extends CcpAnnotationAttributeExtractor implements
		CraftEntrezGeneAnnotationAttributeExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationAttributeExtractor#
	 * getAnnotationAttributes(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public Collection<EntrezGeneID> getAnnotationAttributes(Annotation annotation) {
		Collection<EntrezGeneID> ids = new ArrayList<EntrezGeneID>();
		CCPTextAnnotation ccpTa = checkInputType(annotation, "getEntrezGeneIdSlotFillers");
		CCPIntegerSlotMention egIdSlot = (CCPIntegerSlotMention) UIMA_Util.getSlotMentionByName(
				ccpTa.getClassMention(), CraftAnnotationAttribute.CRAFT_ENTREZ_GENE_ID_SLOT_NAME);
		if (egIdSlot == null)
			return null;
		IntegerArray egIds = egIdSlot.getSlotValues();
		for (int i = 0; i < egIds.size(); i++)
			ids.add(new EntrezGeneID(egIds.get(i)));
		return ids;
	}

}