
package edu.ucdenver.ccp.nlp.uima.collections.craft;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2019 Regents of the University of Colorado
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
 * Contains entries for each CRAFT sub-project and methods for retrieving XMI file paths.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum CraftConceptType {
	TEXT_ONLY("text-only"),
	COREF("coreference"),
	TYPO("sections-and-typography"),
	TREEBANK("treebank"),
	
	CHEBI("chebi"),
	CHEBI_EXT("chebi_ext"),
	CL("cl"),
	CL_EXT("cl_ext"),
	GOCC("go_cc"),
	GOCC_EXT("go_cc_ext"),
	GOBP("go_bp"),
	GOBP_EXT("go_bp_ext"),
	GOMF("go_mf"),
	GOMF_EXT("go_mf_ext"),
	MOP("mop"),
	MOP_EXT("mop_ext"),
	NCBITAXON("ncbitaxon"),
	NCBITAXON_EXT("ncbitaxon_ext"),
	PR("pr"),
	PR_EXT("pr_ext"),
	SO("so"),
	SO_EXT("so_ext"),
	UBERON("uberon"),
	UBERON_EXT("uberon_ext");

	private final String xmiDirectoryName;

	private CraftConceptType(String xmiPath) {
		this.xmiDirectoryName = xmiPath;
	}

	public String getXmiPath(CraftRelease release, CraftXmiTypeSystem typeSystem) {
		String releaseDir = null;
		if (release.name().contains("MAIN")) {
			releaseDir = "release";
		} else {
			throw new IllegalArgumentException("CraftConceptType.getXmiPath() does not yet support CraftRelease type: "
					+ release.name());
		}
		return "craft/" + releaseDir + "/xmi/" + typeSystem.name().toLowerCase() + "/" + xmiDirectoryName;
	}

}