package edu.ucdenver.ccp.nlp.uima.shims.document.impl;

import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * An implementation of {@link DocumentMetaDataExtractor} used to work with the CCP type system
 * 
 * @author bill
 * 
 */
public class CcpDocumentMetadataHandler implements DocumentMetadataHandler {

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentEncoding(org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentEncoding(JCas jCas) {
//		return CharacterEncoding.UTF_8.getCharacterSetName();
		return UIMA_Util.getDocumentEncoding(jCas).getCharacterSetName();
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentId(org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentId(JCas jCas) {
		return UIMA_Util.getDocumentID(jCas);
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentId(org.apache.uima.jcas.JCas, java.lang.String)
	 */
	@Override
	public void setDocumentId(JCas jCas, String documentId) {
		UIMA_Util.setDocumentID(jCas, documentId);
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentEncoding(org.apache.uima.jcas.JCas, edu.ucdenver.ccp.common.file.CharacterEncoding)
	 */
	@Override
	public void setDocumentEncoding(JCas jCas, String encoding) {
		String enc = encoding.replaceAll("-", "_");
		UIMA_Util.setDocumentEncoding(jCas, CharacterEncoding.valueOf(enc));
	}

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#getMetaDataContainer(org.apache.uima.jcas.JCas)
	 */
	@Override
	public CCPDocumentInformation getMetaDataContainer(JCas jCas) {
		return UIMA_Util.getDocumentInfo(jCas);
	}
	
	

}
