/**
 * 
 */
package edu.ucdenver.ccp.rdf.ao.impl;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

/**
 * Creates AO-format document RDF by extracting a string of numbers presumed to be the PubMed ID
 * from the document ID
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpPubMedAbstractAoDocumentRdfGenerator extends PubMedAoDocumentRdfGenerator {
	
	/**
	 * @param pmid
	 * @return
	 */
	@Override
	protected URI getPubMedUri(PubMedID pmid) {
		return new URIImpl(RdfUtil.createUri(RdfNamespace.KABOB, "abstract/PMID_" + pmid.toString()).toString());
	}

}
