package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 * @param <T>
 */
public class BerkeleyDbDictionaryEntryIndexAccessor<T extends DictionaryEntry> implements IndexAccessor<T> {

		/**
		 * 
		 */
		private final PrimaryIndex<String, T> primaryIndex;
		/**
		 * 
		 */
		private final EntityStore store;

		/**
		 * @param store
		 * @param dictionaryEntryClass
		 * @throws DatabaseException
		 */
		public BerkeleyDbDictionaryEntryIndexAccessor(EntityStore store, Class<T> dictionaryEntryClass) throws DatabaseException {
			this.store = store;
			primaryIndex = store.getPrimaryIndex(String.class, dictionaryEntryClass);
		}

		/**
		 * @return the primaryIndex
		 */
		@Override
		public PrimaryIndex<String, T> getPrimaryIndex() {
			return primaryIndex;
		}

		/**
		 * @return the secondaryIndex
		 * @throws DatabaseException
		 */
		@Override
		public SecondaryIndex<String, String, T> getSecondaryIndex(String fieldName)
				throws DatabaseException {
			return store.getSecondaryIndex(getPrimaryIndex(), String.class, fieldName);
		}

	}