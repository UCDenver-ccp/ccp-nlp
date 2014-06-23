/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.stemmer;

import org.apache.uima.conceptMapper.support.stemmer.Stemmer;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class ConceptMapperStemmerFactory {

	public enum StemmerType {
		PORTER, BIOLEMMATIZER, NONE
	}
	
	public static Class<? extends Stemmer> getStemmerClass(StemmerType stemmerType) {
		switch (stemmerType) {
		case PORTER:
			return ConceptMapperPorterStemmer.class;
		case BIOLEMMATIZER:
			return ConceptMapperBioLemmatizer.class;
		case NONE:
			return ConceptMapperNullStemmer.class;
		default:
			throw new IllegalArgumentException("Unhandled stemmer type: " + stemmerType.name());
		}
	}
	
	
}
