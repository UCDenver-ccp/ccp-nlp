/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.shims.document.impl;

import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * An implementation of {@link DocumentMetadataHandler} that is specific to the CCP type system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpDocumentMetadataHandler implements DocumentMetadataHandler {

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentEncoding(org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentEncoding(JCas jCas) {
		return UIMA_Util.getDocumentEncoding(jCas).getCharacterSetName();
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.document.shim.document.uchsc.ccp.uima.shim.DocumentMetaDataExtractor#extractDocumentId(org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentId(JCas jCas) {
		return UIMA_Util.getDocumentID(jCas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentId(org.
	 * apache.uima.jcas.JCas, java.lang.String)
	 */
	@Override
	public void setDocumentId(JCas jCas, String documentId) {
		UIMA_Util.setDocumentID(jCas, documentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentEncoding
	 * (org.apache.uima.jcas.JCas, edu.ucdenver.ccp.common.file.CharacterEncoding)
	 */
	@Override
	public void setDocumentEncoding(JCas jCas, String encoding) {
		String enc = encoding.replaceAll("-", "_");
		UIMA_Util.setDocumentEncoding(jCas, CharacterEncoding.valueOf(enc));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#getMetaDataContainer
	 * (org.apache.uima.jcas.JCas)
	 */
	@Override
	public CCPDocumentInformation getMetaDataContainer(JCas jCas) {
		return UIMA_Util.getDocumentInfo(jCas);
	}

}
