/**
 * 
 */
package edu.ucdenver.ccp.uima.shims.document.impl;

import java.io.File;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2016 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * A {@link DocumentMetadataHandler} implementation that uses the {@link SourceDocumentInformation}
 * class that comes with the UIMA Examples type system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class UimaExampleDocumentMetadataHandler implements DocumentMetadataHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#extractDocumentId(
	 * org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentId(JCas jCas) {
		return getSourceDocInfo(jCas).getUri().substring(7); // remove the file:// prefix
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#extractDocumentEncoding
	 * (org.apache.uima.jcas.JCas)
	 */
	@Override
	public String extractDocumentEncoding(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store document encoding information");
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
		getSourceDocInfo(jCas).setUri("file://" + documentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#setDocumentEncoding
	 * (org.apache.uima.jcas.JCas, java.lang.String)
	 */
	@Override
	public void setDocumentEncoding(JCas jCas, String encoding) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store document encoding information");
	}

	private SourceDocumentInformation getSourceDocInfo(JCas jCas) {
		SourceDocumentInformation sourceDocInfo;
		FSIterator<Annotation> it = jCas.getJFSIndexRepository().getAnnotationIndex(SourceDocumentInformation.type)
				.iterator();
		if (it.hasNext()) {
			sourceDocInfo = (SourceDocumentInformation) it.next();
		} else {
			sourceDocInfo = new SourceDocumentInformation(jCas);
			sourceDocInfo.addToIndexes();
		}
		return sourceDocInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor#getMetaDataContainer
	 * (org.apache.uima.jcas.JCas)
	 */
	@Override
	public TOP getMetaDataContainer(JCas jCas) {
		return getSourceDocInfo(jCas);
	}

	@Override
	public void setSourceDocumentPath(JCas jCas, File sourceDocumentFile) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the source document path "
				+ "since we are using the URI field to store the document ID.");
		
	}

	@Override
	public File extractSourceDocumentPath(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the source document path "
				+ "since we are using the URI field to store the document ID.");
	}

	@Override
	public int getYearPublished(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the year published."); 
	}

	@Override
	public void setYearPublished(JCas jCas, int year) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the year published.");
	}

	@Override
	public int getMonthPublished(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the month published.");
	}

	@Override
	public void setMonthPublished(JCas jCas, int month) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the month published.");		
	}

	@Override
	public File getDocumentMetadataPath(JCas jCas) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the path to the document metadata file.");
	}

	@Override
	public void setDocumentMetadataPath(JCas jCas, File documentMetadataFile) {
		throw new UnsupportedOperationException(
				"The SourceDocumentInformation class does not store the path to the document metadata file.");		
	}

}
