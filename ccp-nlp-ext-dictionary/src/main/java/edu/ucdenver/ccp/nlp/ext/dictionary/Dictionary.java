/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface Dictionary<T extends DictionaryEntry> {

	public enum AccessMode {
		READONLY(true),
		READWRITE(false);

		private final boolean readOnly;

		private AccessMode(boolean readOnly) {
			this.readOnly = readOnly;
		}

		/**
		 * @return the readOnly
		 */
		public boolean readOnly() {
			return readOnly;
		}
	}

	/**
	 * This enum used to distiguish between dictionary loading modes.
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum LoadMode {
		/**
		 * Indicates that no checking should be performed for duplicate entry primary keys. If a
		 * duplicate primary key is used then the entry already stored is simply overwritten.
		 */
		OVERWRITE,
		/**
		 * Entries with duplicate primary keys are merged with the result stored in the dictionary.
		 * This mode involves a search and is likely to be slower than the OVERWRITE mode.
		 */
		UPDATE
	}
	
	
	/**
	 * @param accessMode
	 */
	public void open(AccessMode accessMode);

	/**
	 * 
	 */
	public void shutdown();

	/**
	 * @param entryIterator
	 * @param loadMode
	 */
	public void store(Iterator<T> entryIterator, LoadMode loadMode);

	/**
	 * Retrieves a specific {@link DictionaryEntry} based on the specified primary key
	 * {@link String}
	 * 
	 * @param primaryKey
	 * @return
	 */
	public T retrieve(String primaryKey);

	/**
	 * Searches the dictionary for matches to the specified search {@link String}. The search uses
	 * exact match.
	 * 
	 * @param searchStr
	 * @return
	 */
	public Set<T> searchAliases(String searchStr);

	/**
	 * Searches the dictionary for matches to the regularized for of the specified search
	 * {@link String}
	 * 
	 * @param regularizer
	 * @param searchStr
	 * @return
	 */
	public Set<T> searchAliases(EntryRegularizer<T> regularizer, String searchStr);

}
