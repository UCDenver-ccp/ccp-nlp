/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary;

import java.util.Set;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DictionaryEntry {

	/**
	 * @return the unique key associated with this entry within the dictionary
	 */
	public String getDictionaryKey();

	public String getCanonicalEntryName();

	public Set<String> getEntryNameAliases();

	public void addEntryNameAlias(String alias);

	/**
	 * Cause the entry to be merged with the input {@link DictionaryEntry}
	 * 
	 * @param entry
	 * @throws IllegalArgumentException
	 *             if the input {@link DictionaryEntry} has a different primary key than this entry
	 */
	public void merge(DictionaryEntry entry) throws IllegalArgumentException;

}
