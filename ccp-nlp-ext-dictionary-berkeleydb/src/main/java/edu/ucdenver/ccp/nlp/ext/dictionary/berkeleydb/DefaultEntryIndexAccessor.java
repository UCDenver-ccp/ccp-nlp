/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class DefaultEntryIndexAccessor extends BerkeleyDbDictionaryEntryIndexAccessor<BerkeleyDbDictionaryEntry> {

	/**
	 * @param store
	 * @param dictionaryEntryClass
	 * @throws DatabaseException
	 */
	public DefaultEntryIndexAccessor(EntityStore store, Class<BerkeleyDbDictionaryEntry> dictionaryEntryClass)
			throws DatabaseException {
		super(store, dictionaryEntryClass);
	}

}
