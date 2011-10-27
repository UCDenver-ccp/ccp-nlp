/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class EntryRegularizer<T extends DictionaryEntry> {

	public abstract String regularize(String input);

	/**
	 * "regularizes" a dictionary entry by adding regularized transformations of the aliases to the
	 * entries collection of aliases.
	 * 
	 * @param dictionaryEntry
	 */
	public void regularize(T dictionaryEntry) {
		Set<String> regularizedAliases = new HashSet<String>();
		for (String alias : dictionaryEntry.getEntryNameAliases()) {
			String regularizedAlias = regularize(alias);
			if (!regularizedAlias.isEmpty())
				regularizedAliases.add(regularize(alias));
		}
		for (String regularizedAlias : regularizedAliases)
			dictionaryEntry.addEntryNameAlias(regularizedAlias);
	}

	public Iterator<T> regularize(final Iterator<T> entryIterator) {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return entryIterator.hasNext();
			}

			@Override
			public T next() {
				T entry = entryIterator.next();
				regularize(entry);
				return entry;
			}

			@Override
			public void remove() {
				entryIterator.remove();
			}

		};
	}

}
