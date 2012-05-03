/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer;

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

	private static BioLemmatizer bioLemmatizer = new BioLemmatizer();

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
