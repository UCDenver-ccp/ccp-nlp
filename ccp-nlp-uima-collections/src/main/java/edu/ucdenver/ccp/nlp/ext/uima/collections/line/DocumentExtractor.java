/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.line;

import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * Interface for extracting documents from text
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentExtractor {
	public GenericDocument extractDocument(String text);
}
