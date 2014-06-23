/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer;

import java.io.FileNotFoundException;
import java.text.ParseException;

import org.apache.uima.conceptMapper.support.stemmer.Stemmer;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperPorterStemmer implements Stemmer {

	private static org.tartarus.martin.Stemmer stemmer = new org.tartarus.martin.Stemmer();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.conceptMapper.support.stemmer.Stemmer#stem(java.lang.String)
	 */
	@Override
	public String stem(String token) {
		stemmer.add(token.toCharArray(), token.toCharArray().length);
		stemmer.stem();
		return stemmer.toString();
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
