package edu.ucdenver.ccp.uima.shims.document;

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

import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

/**
 * The DocumentMetadataHandler interface provides a standard interface to set and retrieve document
 * metadata from a JCas.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentMetadataHandler {

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
	 * @return the {@link String} used by the document stored in the input {@link JCas}
	 */
	public String extractDocumentEncoding(JCas jCas);

	/**
	 * Sets the document identifier in the input JCas metadata (as defined by some
	 * implementation-specific type system)
	 * 
	 * @param jCas
	 * @param documentId
	 */
	public void setDocumentId(JCas jCas, String documentId);

	/**
	 * Sets the document encoding in the input JCas metadata (as defined by some
	 * implementation-specific type system)
	 * 
	 * @param jCas
	 * @param encoding
	 */
	public void setDocumentEncoding(JCas jCas, String encoding);

	/**
	 * @param jCas
	 * @return the metadata Object itself, for example if using the uima-examples type system this
	 *         method should return a {@link SourceDocumentInformation} object
	 */
	public TOP getMetaDataContainer(JCas jCas);

}
