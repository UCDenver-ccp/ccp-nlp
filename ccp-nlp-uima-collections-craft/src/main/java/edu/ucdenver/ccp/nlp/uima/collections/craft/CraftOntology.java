package edu.ucdenver.ccp.nlp.uima.collections.craft;

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

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum CraftOntology {
	CHEBI("/craft/ontologies/CHEBI.obo.gz"),
	CHEBI_EXT("/craft/ontologies/CHEBI+extensions.obo.gz"),
	CL("/craft/ontologies/CL.obo.gz"),
	CL_EXT("/craft/ontologies/CL+extensions.obo.gz"),
	GO("/craft/ontologies/GO.obo.gz"),
	GO_MF_STUB("/craft/ontologies/GO_MF_stub.obo.gz"),
	GO_MF_STUB_EXT("/craft/ontologies/GO_MF_stub+GO_MF_extensions.obo.gz"),
	GO_BP_EXT("/craft/ontologies/GO+GO_BP_extensions.obo.gz"),
	GO_CC_EXT("/craft/ontologies/GO+GO_CC_extensions.obo.gz"),
	MOP("/craft/ontologies/MOP.obo.gz"),
	MOP_EXT("/craft/ontologies/MOP+extensions.obo.gz"),
	NCBI_TAXON("/craft/ontologies/NCBITaxon.obo.gz"),
	NCBI_TAXON_EXT("/craft/ontologies/NCBITaxon+extensions.obo.gz"),
	PR("/craft/ontologies/PR.obo.gz"),
	PR_EXT("/craft/ontologies/PR+extensions.obo.gz"),
	SO("/craft/ontologies/SO.obo.gz"),
	SO_EXT("/craft/ontologies/SO+extensions.obo.gz"),
	UBERON("/craft/ontologies/UBERON.obo.gz"),
	UBERON_EXT("/craft/ontologies/UBERON+extensions.obo.gz");
	

	/**
	 * location on the classpath of the compressed ontology OBO file
	 */
	private final String oboFilePath;

	private CraftOntology(String oboFilePath) {
		this.oboFilePath = oboFilePath;
	}

	public String oboFilePath() {
		return oboFilePath;
	}

}
