package edu.ucdenver.ccp.rdf.craft;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public  class CcpCraftOrganismAnnotationAttributeExtractor extends CcpAnnotationAttributeExtractor implements CraftOrganismAnnotationAttributeExtractor
			 {

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationAttributeExtractor#
		 * getAnnotationAttributes(org.apache.uima.jcas.tcas.Annotation,
		 * edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationAttribute)
		 */
		@Override
		public Collection<NcbiTaxonomyID> getAnnotationAttributes(Annotation annotation) {
			CCPTextAnnotation ccpTa = checkInputType(annotation, getClass().getName() + ".getAnnotationAttributes()");
			CCPIntegerSlotMention taxIdSlot = (CCPIntegerSlotMention) UIMA_Util.getPrimitiveSlotMentionByName(
					ccpTa.getClassMention(), CraftAnnotationAttribute.CRAFT_TAXONOMY_ID_SLOT_NAME);
			IntegerArray taxIds = taxIdSlot.getSlotValues();
			Collection<NcbiTaxonomyID> taxonomyIds = new ArrayList<NcbiTaxonomyID>();
			for (int i = 0; i < taxIds.size(); i++)
				taxonomyIds.add(new NcbiTaxonomyID(taxIds.get(i)));
			return taxonomyIds;
		}

	}
