/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationAttribute;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum CraftAnnotationAttribute implements AnnotationAttribute {
	ENTREZ_GENE_ID,
	TAXONOMY_ID;

	public static final String CRAFT_TAXONOMY_ID_SLOT_NAME = "taxonomy ID";
	public static final String CRAFT_ENTREZ_GENE_ID_SLOT_NAME = "has Entrez Gene ID";

}
