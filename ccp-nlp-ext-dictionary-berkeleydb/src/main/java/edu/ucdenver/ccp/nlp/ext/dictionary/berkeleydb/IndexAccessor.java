/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface IndexAccessor<T extends DictionaryEntry> {

	/**
	 * @return
	 */
	PrimaryIndex<String, T> getPrimaryIndex();

	/**
	 * @param fieldName
	 * @return
	 * @throws DatabaseException
	 */
	SecondaryIndex<String, String, T> getSecondaryIndex(String fieldName)
			throws DatabaseException;

}
