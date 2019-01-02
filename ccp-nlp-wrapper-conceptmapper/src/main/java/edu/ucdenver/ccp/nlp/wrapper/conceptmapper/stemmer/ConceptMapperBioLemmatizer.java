/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.conceptMapper.support.stemmer.Stemmer;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry.Lemma;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperBioLemmatizer implements Stemmer {

	private BioLemmatizer bioLemmatizer;

	public ConceptMapperBioLemmatizer() {
		bioLemmatizer = new BioLemmatizer();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.conceptMapper.support.stemmer.Stemmer#stem(java.lang.String)
	 */
	@Override
	public String stem(String token) {
		if (bioLemmatizer == null) {
			throw new RuntimeException("NULL BIOLEMMATIZER");
		}
		LemmataEntry entry = bioLemmatizer.lemmatizeByLexiconAndRules(token, null);
		Set<String> lemmaStrs = new HashSet<String>();
		for (Lemma lemma : entry.getLemmas()) {
			lemmaStrs.add(lemma.getLemma());
		}
		if (lemmaStrs.size() == 1) {
			return CollectionsUtil.getSingleElement(lemmaStrs);
		}
		/*
		 * If there are multiple lemma's returned - due to part-of-speech ambiguation - then we
		 * simply sort them and return the first one. This is a bit of a hack for sure, but it will
		 * at least be consistent.
		 */
		List<String> sortedLemmas = new ArrayList<String>(lemmaStrs);
		Collections.sort(sortedLemmas);
		return sortedLemmas.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.conceptMapper.support.stemmer.Stemmer#initialize(java.lang.String)
	 */
	@Override
	public void initialize(String dictionary) throws FileNotFoundException, ParseException {
	}

}
