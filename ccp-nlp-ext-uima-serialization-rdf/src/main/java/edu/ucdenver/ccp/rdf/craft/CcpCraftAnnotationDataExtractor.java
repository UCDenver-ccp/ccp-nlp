/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import edu.ucdenver.ccp.rdf.CcpRdfAnnotationDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class CcpCraftAnnotationDataExtractor extends CcpRdfAnnotationDataExtractor {

	public CcpCraftAnnotationDataExtractor () {
		super();
		System.out.println("Initializing new CcpCraftAnnotationDataExtractor");
		this.addAnnotationAttributeExtractor(new CcpCraftOrganismAnnotationAttributeExtractor(),CraftAnnotationAttribute.TAXONOMY_ID);
		this.addAnnotationAttributeExtractor(new CcpCraftEntrezGeneAnnotationAttributeExtractor(),CraftAnnotationAttribute.ENTREZ_GENE_ID);
	}
}
