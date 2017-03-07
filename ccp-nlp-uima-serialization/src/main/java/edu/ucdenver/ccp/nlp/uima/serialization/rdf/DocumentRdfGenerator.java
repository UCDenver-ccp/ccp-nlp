/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.serialization.rdf;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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

import java.util.Collection;

import org.apache.uima.jcas.JCas;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.semanticweb.owlapi.model.IRI;

import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * An abstract class that provides a framework for generating document-related
 * RDF.
 * 
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentRdfGenerator {

	/**
	 * @param jCas
	 * @param documentMetadataHandler
	 *            an implementation of the {@link DocumentMetadataHandler} class
	 *            that knows how to extract document metadata from the input
	 *            {@link JCas} for a particular UIMA type system
	 * @return the {@link IRI} for the document represented by the input
	 *         {@link JCas}
	 */
	public URI getDocumentUri(JCas jCas, DocumentMetadataHandler documentMetadataHandler);

	/**
	 * @param jCas
	 * @param documentMetadataHandler
	 *            an implementation of the {@link DocumentMetadataHandler} class
	 *            that knows how to extract document metadata from the input
	 *            {@link JCas} for a particular UIMA type system
	 * @return a collection of RDF statements for the document represented by
	 *         the input {@link JCas}
	 */
	public Collection<Statement> generateRdf(JCas jCas, DocumentMetadataHandler documentMetadataHandler);

}
