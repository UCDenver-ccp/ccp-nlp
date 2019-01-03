
package edu.ucdenver.ccp.nlp.uima.collections.craft;

import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;

/*
 * #%L
 * Colorado Computational Pharmacology's CRAFT-related code module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
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

import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * The CRAFT corpus is currently releases as UIMA XMI using two different type systems.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum CraftXmiTypeSystem {
	/**
	 * Signifies the Colorado Computational Pharmacology type system for entity annotations.
	 * Treebank data is stored using the ClearTK type system.
	 */
	CCP(TypeSystemDescriptionFactory.createTypeSystemDescription(TypeSystemUtil.CCP_TYPE_SYSTEM,
			"org.cleartk.syntax.constituent.TypeSystem"), CcpDocumentMetadataHandler.class);
	
	private final TypeSystemDescription tsd;
	private final Class<? extends DocumentMetadataHandler> documentMetadataExtractorClass;

	private CraftXmiTypeSystem(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> docMetadataExtractorClass) {
		this.tsd = tsd;
		this.documentMetadataExtractorClass = docMetadataExtractorClass;
	}

	public TypeSystemDescription getTypeSystemDescription() {
		return tsd;
	}

	public Class<? extends DocumentMetadataHandler> getDocumentMetadataExtractorClass() {
		return documentMetadataExtractorClass;
	}
}
