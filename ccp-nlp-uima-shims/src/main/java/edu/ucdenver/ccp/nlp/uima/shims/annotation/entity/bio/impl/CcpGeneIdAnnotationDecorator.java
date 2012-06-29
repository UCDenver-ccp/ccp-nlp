/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.GeneIdAnnotationDecorator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDecorator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpGeneIdAnnotationDecorator extends CcpAnnotationDecorator implements
		GeneIdAnnotationDecorator {

	public static final String ENTREZ_GENE_ID_SLOT_NAME = SlotMentionTypes.PROTEIN_ENTREZGENEID;
	public static final String HOMOLOGENE_GROUP_ID_SLOT_NAME = "hasHomologeneGroupId";
	public static final String PRO_ID_SLOT_NAME = "hasProId";

	/**
	 * @param id
	 * @return
	 */
	private String getSlotName(DataSourceIdentifier<?> id) {
		switch (id.getDataSource()) {
		case EG:
			return ENTREZ_GENE_ID_SLOT_NAME;
		case HOMOLOGENE:
			return HOMOLOGENE_GROUP_ID_SLOT_NAME;
		case PR:
			return PRO_ID_SLOT_NAME;
		case GO:
			return PRO_ID_SLOT_NAME;
		case MOD:
			return PRO_ID_SLOT_NAME;
		case SO:
			return PRO_ID_SLOT_NAME;
		case CHEBI:
			return PRO_ID_SLOT_NAME;
			
		default:
			throw new IllegalArgumentException("Unhandled ID type: " + id.getDataSource().name());
		}
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.GeneIdAnnotationDecorator#addGeneIdentifierAttribute(org.apache.uima.jcas.tcas.Annotation, edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier)
	 */
	@Override
	public void addGeneIdentifierAttribute(Annotation annotation, DataSourceIdentifier<?> geneId) {
		String slotName = getSlotName(geneId);
		addAnnotationAttribute(annotation, slotName, geneId.getDataElement());
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDecorator#addAnnotationAttribute
//	 * (org.apache.uima.jcas.tcas.Annotation, java.lang.Object)
//	 */
//	@Override
//	public void addAnnotationAttribute(Annotation annotation, DataSourceIdentifier<?> id) {
//		String slotName = getSlotName(id);
//		addPrimitiveSlot(annotation, slotName, id.getDataElement());
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.GeneIdAnnotationDecorator#
//	 * addGeneIdentifierAttribute(org.apache.uima.jcas.tcas.Annotation,
//	 * edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier)
//	 */
//	@Override
//	public void addGeneIdentifierAttribute(Annotation annotation, DataSourceIdentifier<?> geneId) {
//		addAnnotationAttribute(annotation, geneId);
//	}

}
