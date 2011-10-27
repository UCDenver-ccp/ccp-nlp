/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.fileparsers.DataRecord;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionaryEntryIndexAccessor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneNameDictionaryTest extends DefaultTestCase {

	/**
	 * @return
	 */
	private static Iterator<BerkeleyDbGeneNameEntry> getSampleEntryWithAliasesIterator() {
		BerkeleyDbGeneNameEntry entry1 = new BerkeleyDbGeneNameEntry("key1", "entry1", 9606, "EG_1234");
		entry1.addEntryNameAlias("syn-1");
		entry1.addEntryNameAlias("syn--2");
		return CollectionsUtil.createList(entry1).iterator();
	}

	/**
	 * @return
	 */
	private static Iterator<BerkeleyDbGeneNameEntry> getSampleEntriesWithDuplicateKeysIterator() {
		BerkeleyDbGeneNameEntry entry1 = new BerkeleyDbGeneNameEntry("key1", "entry1", 9606, "EG_1234");
		entry1.addEntryNameAlias("syn-1");
		entry1.addEntryNameAlias("syn--2");
		BerkeleyDbGeneNameEntry entry2 = new BerkeleyDbGeneNameEntry("key1", "entry11", 9606, "EG_7890");
		entry1.addEntryNameAlias("syn-111");
		return CollectionsUtil.createList(entry1, entry2).iterator();
	}

	/**
	 * 
	 */
	@Test
	public void testRegularizedAliasSearch() {
		File dictionaryDirectory = folder.newFolder("dict-root");
		SimpleDictionaryBuilder dictBuilder = new SimpleDictionaryBuilder(dictionaryDirectory, "test-store",
				SimpleRegularizer.class, GeneNameEntryIndexAccessor.class, BerkeleyDbGeneNameEntry.class, false);
		dictBuilder.buildDictionary(LoadMode.OVERWRITE);

		Dictionary<BerkeleyDbGeneNameEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(
				dictionaryDirectory, "test-store", GeneNameEntryIndexAccessor.class, BerkeleyDbGeneNameEntry.class);
		dictionary.open(AccessMode.READONLY);
		BerkeleyDbGeneNameEntry entry = dictionary.retrieve("key1");
		assertEquals("key1", entry.getDictionaryKey());
		assertEquals("entry1", entry.getCanonicalEntryName());

		Set<BerkeleyDbGeneNameEntry> entry1Hits = dictionary.searchAliases("entry1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("key1", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		assertEquals("entry1", CollectionsUtil.getSingleElement(entry1Hits).getCanonicalEntryName());
		assertEquals(CollectionsUtil.createSet("entry1", "syn-1", "syn--2", "syn1", "syn2"), CollectionsUtil
				.getSingleElement(entry1Hits).getEntryNameAliases());

		Set<BerkeleyDbGeneNameEntry> syn2Hits = dictionary.searchAliases("syn2");
		assertEquals("search for syn2 alias failed.", 1, syn2Hits.size());

		syn2Hits = dictionary.searchAliases(new SimpleRegularizer(), "syn-2");
		assertEquals("search for syn-2 alias failed.", 1, syn2Hits.size());

		dictionary.shutdown();
	}

	@Test
	public void testUpdateLoadMode() {
		File dictionaryDirectory = folder.newFolder("dict-root");
		SimpleDictionaryBuilder dictBuilder = new SimpleDictionaryBuilder(dictionaryDirectory, "test-store",
				SimpleRegularizer.class, GeneNameEntryIndexAccessor.class, BerkeleyDbGeneNameEntry.class, true);
		dictBuilder.buildDictionary(LoadMode.UPDATE);

		Dictionary<BerkeleyDbGeneNameEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(
				dictionaryDirectory, "test-store", GeneNameEntryIndexAccessor.class, BerkeleyDbGeneNameEntry.class);
		dictionary.open(AccessMode.READONLY);
		BerkeleyDbGeneNameEntry entry = dictionary.retrieve("key1");
		assertEquals("key1", entry.getDictionaryKey());
		assertEquals("entry11", entry.getCanonicalEntryName()); // more recently loaded canonical
																// name is what's stored in the
																// dictionary
		assertEquals(
				CollectionsUtil.createSet("entry1", "syn-1", "syn--2", "syn1", "syn2", "entry11", "syn-111", "syn111"),
				entry.getEntryNameAliases());
		assertEquals(CollectionsUtil.createSet("EG_1234", "EG_7890"), entry.getDbIdentifiers());

		dictionary.shutdown();
	}

	/**
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	private static class SimpleDictionaryBuilder extends BerkeleyDbDictionaryBuilder<BerkeleyDbGeneNameEntry> {

		private final boolean useDuplicateKeys;

		/**
		 * @param dictionaryDirectory
		 * @param storeName
		 * @param entryRegularizerClass
		 * @param indexAccessorClass
		 * @param dictionaryEntryClass
		 */
		public SimpleDictionaryBuilder(File dictionaryDirectory, String storeName,
				Class<? extends EntryRegularizer<BerkeleyDbGeneNameEntry>> entryRegularizerClass,
				Class<? extends BerkeleyDbDictionaryEntryIndexAccessor<BerkeleyDbGeneNameEntry>> indexAccessorClass,
				Class<BerkeleyDbGeneNameEntry> dictionaryEntryClass, boolean useDuplicateKeys) {
			super(dictionaryDirectory, storeName, entryRegularizerClass, indexAccessorClass, dictionaryEntryClass);
			this.useDuplicateKeys = useDuplicateKeys;
		}

		/**
		 * 
		 */
		@Override
		protected BerkeleyDbGeneNameEntry createEntry(DataRecord dataRecord) {
			// not used
			return null;
		}

		/**
		 * 
		 */
		@Override
		protected Iterator<BerkeleyDbGeneNameEntry> getEntryIterator() {
			if (useDuplicateKeys)
				return getSampleEntriesWithDuplicateKeysIterator();
			return getSampleEntryWithAliasesIterator();
		}

	}

	/**
	 * Simple regulatizer that removes all punctuation characterss
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	static class SimpleRegularizer extends EntryRegularizer<BerkeleyDbGeneNameEntry> {

		@Override
		public String regularize(String input) {
			return input.replaceAll("\\p{Punct}", "");
		}

	}

}
