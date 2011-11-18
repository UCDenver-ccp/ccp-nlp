/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.line;

import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * Designed to extract a Medline document from a line using the following format: <br>
 * pmid <tab> title <tab> abstract <br>
 * The output text consists of the title on one line, and the abstract on the next line (if the
 * Medline record has an abstract). <br>
 * 
 * Note: some abstracts have tabs in them.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MedlineLineDocumentExtractor implements DocumentExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.line.DocumentExtractor#extractDocument(java.lang
	 * .String)
	 */
	@Override
	public GenericDocument extractDocument(String line) {
		String[] toks = line.split(RegExPatterns.TAB);
		if (line.isEmpty() || toks.length < 2)
			return null;
		String pmidStr = toks[0];
		String documentText = toks[1];
		if (toks.length > 2) {
			String abstractText = line.substring(pmidStr.length() + 1 + documentText.length() + 1);
			documentText += (StringConstants.NEW_LINE + abstractText);
		}
		GenericDocument gd = new GenericDocument(pmidStr);
		gd.setDocumentText(documentText);
		return gd;
	}

}
