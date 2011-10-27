/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BerkeleyDbDictionary<T extends DictionaryEntry> implements Dictionary<T> {

	private static final Logger logger = Logger.getLogger(BerkeleyDbDictionary.class);

	private Environment env;
	private EntityStore store;
	private IndexAccessor<T> indexAccessor;
	private AccessMode accessMode;
	private final File dictionaryDirectory;
	private final String storeName;
	private final Class<? extends IndexAccessor<T>> indexAccessorClass;
	private final Class<T> dictionaryEntryClass;

	/**
	 * @param dictionaryDirectory
	 * @param storeName
	 * @param indexAccessorClass
	 */
	public BerkeleyDbDictionary(File dictionaryDirectory, String storeName,
			Class<? extends IndexAccessor<T>> indexAccessorClass, Class<T> dictionaryEntryClass) {
		validateDictionaryDirectory(dictionaryDirectory);
		this.dictionaryDirectory = dictionaryDirectory;
		this.storeName = storeName;
		this.indexAccessorClass = indexAccessorClass;
		this.dictionaryEntryClass = dictionaryEntryClass;
	}

	/**
	 * 
	 */
	@Override
	public void open(AccessMode accessMode) {
		if (accessMode.equals(AccessMode.READONLY))
			validateDictionaryDirectoryForReadOnlyAccess();
		this.accessMode = accessMode;

		EnvironmentConfig config = new EnvironmentConfig();
		config.setReadOnly(accessMode.readOnly());
		config.setAllowCreate(!accessMode.readOnly());
		config.setTxnNoSync(true);
		config.setTxnWriteNoSync(true);

		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setReadOnly(accessMode.readOnly());
		storeConfig.setAllowCreate(!accessMode.readOnly());

		try {
			env = new Environment(dictionaryDirectory, config);
			store = new EntityStore(env, getStoreName(), storeConfig);
			indexAccessor = (IndexAccessor<T>) ConstructorUtil.invokeConstructor(indexAccessorClass.getName(), store,
					dictionaryEntryClass);
		} catch (EnvironmentLockedException e) {
			throw new RuntimeException(e);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the specified dictionary directory is empty
	 */
	private void validateDictionaryDirectoryForReadOnlyAccess() {
		if (dictionaryDirectory.list().length == 0)
			throw new IllegalArgumentException("Cannot open empty dictionary with a read-only connection.");
	}

	/**
	 * @param dictionaryDirectory
	 * @throws IllegalStateException
	 *             if an attempt is made to open a connection on a directory that does not exist
	 */
	private static void validateDictionaryDirectory(File dictionaryDirectory) {
		try {
			FileUtil.validateDirectory(dictionaryDirectory);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					"Berkeley DB dictionary directory must exist in order to open the dictionary.");
		}
	}

	/**
	 * 
	 */
	@Override
	public void shutdown() {
		if (store != null) {
			try {
				store.close();
			} catch (DatabaseException e) {
				throw new RuntimeException(e);
			}
		}
		if (env != null) {
			try {
				if (!accessMode.equals(AccessMode.READONLY))
					env.cleanLog();
				env.close();
			} catch (DatabaseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * 
	 */
	@Override
	public void store(Iterator<T> entryIterator, LoadMode loadMode) {
		if (!store.getConfig().getAllowCreate())
			throw new IllegalStateException("Cannot store dictionary entries. Berkeley DB store is set to read-only.");
		long startTime = System.currentTimeMillis();
		long prevTime = startTime;
		int count = 0;
		while (entryIterator.hasNext()) {
			if (count++ % 10000 == 0) {
				long batchTime = System.currentTimeMillis() - prevTime;
				prevTime = System.currentTimeMillis();
				logger.info("Dictionary load progress: " + (count - 1) + " Last batch took: "
						+ (batchTime/1000) + "s");
			}
			if ((count - 1) % 30000 == 0) {
				try {
					logger.info("Dictionary load progress: Cleaning log files.");
					env.cleanLog();
				} catch (DatabaseException e) {
					throw new RuntimeException(e);
				}
			}
			try {
				storeEntry(entryIterator.next(), loadMode);
			} catch (DatabaseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @param entry
	 * @param loadMode
	 * @throws DatabaseException
	 */
	private void storeEntry(T entry, LoadMode loadMode) throws DatabaseException {
		System.out.println("Storing entry: " + entry.toString());
		if (loadMode.equals(LoadMode.UPDATE)) {
			if (indexAccessor.getPrimaryIndex().contains(entry.getDictionaryKey())) {
				T existingEntry = indexAccessor.getPrimaryIndex().get(entry.getDictionaryKey());
				entry.merge(existingEntry);
			}
		}
		indexAccessor.getPrimaryIndex().put(entry);
	}

	/**
	 * 
	 */
	@Override
	public T retrieve(String primaryKey) {
		try {
			return indexAccessor.getPrimaryIndex().get(primaryKey);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	@Override
	public Set<T> searchAliases(String searchStr) {
		try {
			EntityCursor<T> cursor = indexAccessor
					.getSecondaryIndex(BerkeleyDbDictionaryEntry.ENTRY_NAME_ALIASES_FIELD_NAME).subIndex(searchStr)
					.entities();
			try {
				Set<T> entries = new HashSet<T>();
				for (Iterator<T> iterator = cursor.iterator(); iterator.hasNext();) {
					T next = iterator.next();
					entries.add(next);
				}
				return entries;
			} finally {
				cursor.close();
			}
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	@Override
	public Set<T> searchAliases(EntryRegularizer<T> regularizer, String searchStr) {
//		System.out.println("Regularized search string: " + regularizer.regularize(searchStr));
		return searchAliases(regularizer.regularize(searchStr));
	}

}
