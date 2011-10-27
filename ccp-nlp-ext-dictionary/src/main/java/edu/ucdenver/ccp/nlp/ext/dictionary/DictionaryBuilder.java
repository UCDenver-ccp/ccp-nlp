/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary;

import java.util.Iterator;

import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DictionaryBuilder<T extends DictionaryEntry> {

	

	/**
	 * Does not check to see if an entry (primary key) is already present. If a duplicate primary
	 * key is used then the entry already stored is simply overwritten.
	 * 
	 * @param dictionary
	 * @param entryIterator
	 * @param entryRegularizer
	 * @param loadMode
	 *            UPDATE to merge incoming entries with entries already present in the dictionary
	 *            having the same primary key, OVERWRITE to overwrite entries already present in the
	 *            dictionary having the same primary key.
	 */
	public void buildDictionary(Dictionary<T> dictionary, Iterator<T> entryIterator,
			EntryRegularizer<T> entryRegularizer, LoadMode loadMode) {
		dictionary.open(AccessMode.READWRITE);
		Iterator<T> entries = entryIterator;
		if (entryRegularizer != null)
			entries = entryRegularizer.regularize(entryIterator);
		dictionary.store(entries, loadMode);
		dictionary.shutdown();
	}

}
