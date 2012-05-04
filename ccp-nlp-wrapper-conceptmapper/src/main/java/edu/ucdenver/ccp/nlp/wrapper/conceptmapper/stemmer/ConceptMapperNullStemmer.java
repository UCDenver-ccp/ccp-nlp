/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer;

import java.io.FileNotFoundException;
import java.text.ParseException;

import org.apache.uima.conceptMapper.support.stemmer.Stemmer;

/**
 * DOES NOT STEM - simply returns the input text
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperNullStemmer implements Stemmer {

	@Override
	public String stem(String token) {
		return token;
	}

	@Override
	public void initialize(String dictionary) throws FileNotFoundException, ParseException {
		// do nothing
	}

}
