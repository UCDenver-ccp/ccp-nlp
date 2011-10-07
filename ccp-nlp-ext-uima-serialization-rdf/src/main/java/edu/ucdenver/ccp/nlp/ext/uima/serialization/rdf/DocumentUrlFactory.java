/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import java.net.URL;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentUrlFactory {
	public URL generateUrl(String documentId);
}
