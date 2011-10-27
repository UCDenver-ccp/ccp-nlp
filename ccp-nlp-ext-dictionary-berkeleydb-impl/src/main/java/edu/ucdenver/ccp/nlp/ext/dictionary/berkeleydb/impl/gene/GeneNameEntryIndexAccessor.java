/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.SecondaryIndex;

import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionaryEntryIndexAccessor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneNameEntryIndexAccessor extends BerkeleyDbDictionaryEntryIndexAccessor<BerkeleyDbGeneNameEntry> {

	/**
	 * @param store
	 * @param dictionaryEntryClass
	 * @throws DatabaseException
	 */
	public GeneNameEntryIndexAccessor(EntityStore store, Class<BerkeleyDbGeneNameEntry> dictionaryEntryClass)
			throws DatabaseException {
		super(store, dictionaryEntryClass);
	}

	/**
	 * @return the secondary index storing mappings to gene name aliases
	 * @throws DatabaseException
	 */
	public SecondaryIndex<String, String, BerkeleyDbGeneNameEntry> getAliasIndex() throws DatabaseException {
		return getSecondaryIndex(BerkeleyDbGeneNameEntry.ENTRY_NAME_ALIASES_FIELD_NAME);
	}

	/**
	 * @return the secondary index storing mappings to gene ncbi taxonomy identifiers
	 * @throws DatabaseException
	 */
	public SecondaryIndex<String, String, BerkeleyDbGeneNameEntry> getNcbiTaxonomyIndex() throws DatabaseException {
		return getSecondaryIndex(BerkeleyDbGeneNameEntry.TAXONOMY_ID_FIELD_NAME);
	}

}
