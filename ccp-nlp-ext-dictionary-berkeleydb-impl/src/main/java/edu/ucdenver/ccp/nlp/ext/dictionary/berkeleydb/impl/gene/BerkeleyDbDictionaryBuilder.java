/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.io.File;
import java.util.Iterator;

import edu.ucdenver.ccp.fileparsers.DataRecord;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionaryEntryIndexAccessor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class BerkeleyDbDictionaryBuilder<T extends DictionaryEntry> extends DictionaryBuilder<T> {

	private final Dictionary<T> dictionary;
	private final EntryRegularizer<T> entryRegularizer;

	/**
	 * @param dictionaryDirectory
	 * @param storeName
	 * @param entryRegularizerClass
	 * @param indexAccessorClass 
	 * @param dictionaryEntryClass 
	 */
	public BerkeleyDbDictionaryBuilder(File dictionaryDirectory, String storeName,
			Class<? extends EntryRegularizer<T>> entryRegularizerClass,
			Class<? extends BerkeleyDbDictionaryEntryIndexAccessor<T>> indexAccessorClass, Class<T> dictionaryEntryClass) {
		dictionary = new BerkeleyDbDictionary<T>(dictionaryDirectory, storeName, indexAccessorClass,
				dictionaryEntryClass);
		try {
			entryRegularizer = (entryRegularizerClass != null) ? entryRegularizerClass.newInstance() : null;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Error while initializing EntryRegularizer.", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Error while initializing EntryRegularizer.", e);
		}

	}

	public void buildDictionary(LoadMode loadMode) {
		buildDictionary(dictionary, getEntryIterator(), entryRegularizer, loadMode);
	}

	protected abstract T createEntry(DataRecord dataRecord);

	protected abstract Iterator<T> getEntryIterator();

}
