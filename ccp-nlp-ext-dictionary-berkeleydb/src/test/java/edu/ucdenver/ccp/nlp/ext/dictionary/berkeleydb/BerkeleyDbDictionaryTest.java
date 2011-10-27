/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BerkeleyDbDictionaryTest extends DefaultTestCase {

	private Dictionary<BerkeleyDbDictionaryEntry> dictionary;

	/**
	 * @return
	 */
	private Iterator<BerkeleyDbDictionaryEntry> getSampleEntryIterator() {
		return CollectionsUtil.createList(new BerkeleyDbDictionaryEntry("key1", "entry1"),
				new BerkeleyDbDictionaryEntry("key2", "entry2"), new BerkeleyDbDictionaryEntry("key4", "entry3"),
				new BerkeleyDbDictionaryEntry("key4", "entry4")).iterator();
	}

	/**
	 * @return
	 */
	private Iterator<BerkeleyDbDictionaryEntry> getSampleEntryWithAliasesIterator() {
		BerkeleyDbDictionaryEntry entry1 = new BerkeleyDbDictionaryEntry("key1", "entry1");
		entry1.addEntryNameAlias("syn1");
		entry1.addEntryNameAlias("syn2");
		BerkeleyDbDictionaryEntry entry2 = new BerkeleyDbDictionaryEntry("key2", "entry2");
		entry2.addEntryNameAlias("syn2");
		BerkeleyDbDictionaryEntry entry3 = new BerkeleyDbDictionaryEntry("key4", "entry3");
		BerkeleyDbDictionaryEntry entry4 = new BerkeleyDbDictionaryEntry("key4", "entry4");
		entry4.addEntryNameAlias("syn2");

		return CollectionsUtil.createList(entry1, entry2, entry3, entry4).iterator();
	}

	/**
	 * Loads the sample dictionary with the sample dictionary entries
	 */
	private void loadDictionary(File dictionaryDirectory, AccessMode accessMode,
			Iterator<BerkeleyDbDictionaryEntry> entriesToLoad) {
		dictionary = new BerkeleyDbDictionary<BerkeleyDbDictionaryEntry>(dictionaryDirectory, "test-store",
				DefaultEntryIndexAccessor.class, BerkeleyDbDictionaryEntry.class);
		dictionary.open(accessMode);

		
		if (entriesToLoad != null) {
			dictionary.store(entriesToLoad, LoadMode.OVERWRITE);
			dictionary.shutdown();
		}
	}

	/**
	 * Loads the sample dictionary with the sample dictionary entries
	 */
	private void loadDictionary() {
		loadDictionary(folder.newFolder("dict-root"), AccessMode.READWRITE, getSampleEntryIterator());
	}

	@Test
	public void testPrimaryKeyRetrieval() {
		File dictionaryDirectory = folder.newFolder("dict-root");
		loadDictionary(dictionaryDirectory, AccessMode.READWRITE, getSampleEntryIterator());
		dictionary = new BerkeleyDbDictionary<BerkeleyDbDictionaryEntry>(dictionaryDirectory, "test-store",
				DefaultEntryIndexAccessor.class, BerkeleyDbDictionaryEntry.class);
		dictionary.open(AccessMode.READONLY);
		BerkeleyDbDictionaryEntry entry = dictionary.retrieve("key2");
		assertEquals("key2", entry.getDictionaryKey());
		assertEquals("entry2", entry.getCanonicalEntryName());
		dictionary.shutdown();
	}

	@Test
	public void testAliasSearch() {
		File dictionaryDirectory = folder.newFolder("dict-syn-root");
		loadDictionary(dictionaryDirectory, AccessMode.READWRITE, getSampleEntryWithAliasesIterator());
		dictionary = new BerkeleyDbDictionary<BerkeleyDbDictionaryEntry>(dictionaryDirectory, "test-store",
				DefaultEntryIndexAccessor.class, BerkeleyDbDictionaryEntry.class);
		dictionary.open(AccessMode.READONLY);
		BerkeleyDbDictionaryEntry entry = dictionary.retrieve("key2");
		assertEquals("key2", entry.getDictionaryKey());
		assertEquals("entry2", entry.getCanonicalEntryName());

		Set<BerkeleyDbDictionaryEntry> entry1Hits = dictionary.searchAliases("entry1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("key1", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		assertEquals("entry1", CollectionsUtil.getSingleElement(entry1Hits).getCanonicalEntryName());

		Set<BerkeleyDbDictionaryEntry> syn1Hits = dictionary.searchAliases("syn1");
		assertEquals("search for syn1 alias failed.", 1, syn1Hits.size());
		assertEquals("key1", CollectionsUtil.getSingleElement(syn1Hits).getDictionaryKey());
		assertEquals("entry1", CollectionsUtil.getSingleElement(syn1Hits).getCanonicalEntryName());

		Set<BerkeleyDbDictionaryEntry> syn2Hits = dictionary.searchAliases("syn2");
		assertEquals("search for syn2 alias failed.", 3, syn2Hits.size());

		dictionary.shutdown();
	}

	/**
	 * IllegalArgumentException is thrown if the input directory is empty and the access mode is
	 * read-only
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOpenFailsIfDirectoryIsEmptyAndConnectionIsReadOnly() {
		loadDictionary(folder.newFolder("dict-root"), AccessMode.READONLY, null);
	}

	/**
	 * IllegalArgumentException is thrown if the input directory does not exist
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOpenFailsIfDirectoryDoesNotExist() {
		File dirThatDoesntExist = new File(folder.getRoot(), "dne");
		loadDictionary(dirThatDoesntExist, null, null);
	}

	/**
	 * IllegalStateException is thrown if a read-only connection attempts to store dictionary
	 * entries
	 */
	@Test(expected = IllegalStateException.class)
	public void testEntryStorageFailsIfReadOnly() {
		loadDictionary();
		dictionary.open(AccessMode.READONLY);
		dictionary.store(getSampleEntryIterator(), LoadMode.OVERWRITE); // throws IllegalArgumentException here
	}

}
