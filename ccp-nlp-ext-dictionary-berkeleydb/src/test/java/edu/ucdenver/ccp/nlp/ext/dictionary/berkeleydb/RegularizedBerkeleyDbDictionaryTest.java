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
import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class RegularizedBerkeleyDbDictionaryTest extends DefaultTestCase {

	/**
	 * @return
	 */
	private Iterator<BerkeleyDbDictionaryEntry> getSampleEntryWithAliasesIterator() {
		BerkeleyDbDictionaryEntry entry1 = new BerkeleyDbDictionaryEntry("key1", "entry1");
		entry1.addEntryNameAlias("syn-1");
		entry1.addEntryNameAlias("syn--2");
		BerkeleyDbDictionaryEntry entry2 = new BerkeleyDbDictionaryEntry("key2", "entry2");
		entry2.addEntryNameAlias("s.y.n2");
		BerkeleyDbDictionaryEntry entry3 = new BerkeleyDbDictionaryEntry("key4", "entry3");
		BerkeleyDbDictionaryEntry entry4 = new BerkeleyDbDictionaryEntry("key4", "entry4");
		entry4.addEntryNameAlias("s,yn2,");

		return CollectionsUtil.createList(entry1, entry2, entry3, entry4).iterator();
	}

	@Test
	public void testRegularizedAliasSearch() {
		File dictionaryDirectory = folder.newFolder("dict-root");
		Dictionary<BerkeleyDbDictionaryEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbDictionaryEntry>(
				dictionaryDirectory, "test-store", DefaultEntryIndexAccessor.class, BerkeleyDbDictionaryEntry.class);
		new DictionaryBuilder<BerkeleyDbDictionaryEntry>().buildDictionary(dictionary,
				getSampleEntryWithAliasesIterator(), new SimpleRegularizer(), LoadMode.OVERWRITE);

		dictionary.open(AccessMode.READONLY);
		BerkeleyDbDictionaryEntry entry = dictionary.retrieve("key2");
		assertEquals("key2", entry.getDictionaryKey());
		assertEquals("entry2", entry.getCanonicalEntryName());

		Set<BerkeleyDbDictionaryEntry> entry1Hits = dictionary.searchAliases("entry1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("key1", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		assertEquals("entry1", CollectionsUtil.getSingleElement(entry1Hits).getCanonicalEntryName());
		assertEquals(CollectionsUtil.createSet("entry1", "syn-1", "syn--2", "syn1", "syn2"), CollectionsUtil
				.getSingleElement(entry1Hits).getEntryNameAliases());

		Set<BerkeleyDbDictionaryEntry> syn2Hits = dictionary.searchAliases("syn2");
		assertEquals("search for syn1 alias failed.", 3, syn2Hits.size());

		syn2Hits = dictionary.searchAliases(new SimpleRegularizer(), "syn-2");
		assertEquals("search for syn2 alias failed.", 3, syn2Hits.size());

		dictionary.shutdown();

	}

	/**
	 * Simple regulatizer that removes all punctuation characterss
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	private static class SimpleRegularizer extends EntryRegularizer<BerkeleyDbDictionaryEntry> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * edu.ucdenver.ccp.nlp.ext.dictionarylookup.EntryRegularizer#regularize(java.lang.String)
		 */
		@Override
		public String regularize(String input) {
			return input.replaceAll("\\p{Punct}", "");
		}

	}
}
