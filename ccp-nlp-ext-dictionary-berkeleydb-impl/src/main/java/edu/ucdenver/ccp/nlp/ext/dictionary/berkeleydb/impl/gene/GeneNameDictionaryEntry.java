/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.util.Set;

import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface GeneNameDictionaryEntry extends DictionaryEntry {

	/**
	 * @return
	 */
	public Integer getNcbiTaxonomyId();

	/**
	 * @return
	 */
	Set<String> getDbIdentifiers();

	
		
	
	
}
