package edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl;

import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * An implementation of {@link DocumentMetaDataExtractor} used to work with the CCP type system
 * 
 * @author bill
 * 
 */
public class CcpDocumentMetaDataExtractor implements DocumentMetaDataExtractor {

	/**
	 * @see edu.ucdenver.ccp.nlp.core.uima.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentEncoding(org.apache.uima.jcas.JCas)
	 */
	@Override
	public CharacterEncoding extractDocumentEncoding(JCas jCas) {
		return CharacterEncoding.UTF_8;
//		return UIMA_Util.getDocumentEncoding(jCas);
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.core.uima.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentId(org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentId(JCas jCas) {
		return UIMA_Util.getDocumentID(jCas);
	}

}
