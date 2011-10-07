/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import java.net.MalformedURLException;
import java.net.URL;

import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.DocumentUrlFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class CraftDocumentUrlFactory implements DocumentUrlFactory {

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.annotation.serialization.rdf.uima.DocumentUrlFactory#generateUrl(java.lang.String)
	 */
	@Override
	public URL generateUrl(String documentId) {
		String pmid = StringUtil.removeSuffix(documentId, ".txt");
		CraftDocument craftDocument = CraftDocument.valueOf(new PubMedID(pmid));
		try {
			return new URL(craftDocument.pubMedCentralUri().toString());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Unable to create craft URL from document ID: " +documentId);
		}
	}

}
