package edu.ucdenver.ccp.nlp.ext.uima.shims.document;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

/**
 * The DocumentMetaDataExtractor interface provides a standard interface for retrieve document meta
 * data from a JCas.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentMetaDataExtractor {

	/**
	 * Extracts and returns a document identifier in the form of a {@link String} from the input
	 * {@link JCas}
	 * 
	 * @param jCas
	 *            the {@link JCas} from which to extract the document identifier
	 * @return a {@link String} representing the document identifier for the document stored in the
	 *         {@JCas}
	 */
	public String extractDocumentId(JCas jCas);

	/**
	 * Extracts and returns the character encoding used by the document stored in the {@link JCas}
	 * 
	 * @param jCas
	 *            the {@link JCas} from which to extract the character encoding
	 * @return the {@link CharacterEncoding} used by the document stored in the input {@link JCas}
	 */
	public String extractDocumentEncoding(JCas jCas);
	
	
	
	
	
	public void setDocumentId(JCas jCas, String documentId);
	
	public void setDocumentEncoding(JCas jCas, String encoding);
	
	
	public TOP getMetaDataContainer(JCas jCas);

}
