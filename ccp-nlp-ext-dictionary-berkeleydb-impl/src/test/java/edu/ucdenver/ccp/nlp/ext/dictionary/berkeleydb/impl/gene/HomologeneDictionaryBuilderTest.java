/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionary;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class HomologeneDictionaryBuilderTest extends DefaultTestCase {

	private static final String SAMPLE_EG_INFO_FILE_NAME = "gene_info";
	private static final String SAMPLE_EG_INFO_READY_FILE = "gene_info.ready";
	private static final String SAMPLE_HOMOLOGENE_FILE_NAME = "homologene.data";
	private static final String SAMPLE_HOMOLOGENE_DATA_FILE_READY_FILE = "homologene.data.ready";

	/**
	 * @param dataDirectory
	 * @throws IOException
	 */
	private void createTestDataFiles(File dataDirectory) throws IOException {
		ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_EG_INFO_FILE_NAME, dataDirectory);
		ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_HOMOLOGENE_FILE_NAME, dataDirectory);
		assertTrue(new File(dataDirectory, SAMPLE_HOMOLOGENE_DATA_FILE_READY_FILE).createNewFile());
		assertTrue(new File(dataDirectory, SAMPLE_EG_INFO_READY_FILE).createNewFile());
	}

	@Test
	public void testBuildHomologeneDictionary() throws IOException {
		File dictionaryDirectory = folder.newFolder("homolo-dict");
		File dataDirectory = folder.newFolder("data");
		createTestDataFiles(dataDirectory);
		assertEquals(4, dataDirectory.list().length);

		HomologeneDictionaryBuilder dictBuilder = new HomologeneDictionaryBuilder(dictionaryDirectory, dataDirectory,
				false);
		dictBuilder.buildDictionary(LoadMode.OVERWRITE);

		Dictionary<BerkeleyDbGeneNameEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(
				dictionaryDirectory, HomologeneDictionaryBuilder.STORE_NAME,
				HomologeneDictionaryBuilder.ENTRY_INDEX_ACCESSOR_CLASS,
				HomologeneDictionaryBuilder.DICTIONARY_ENTRY_CLASS);
		dictionary.open(AccessMode.READONLY);

		BerkeleyDbGeneNameEntry entry = dictionary.retrieve("HOMOLOGENE_GROUP_75");
		assertNotNull(entry);
		entry = dictionary.retrieve("HOMOLOGENE_GROUP_52");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_12780");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_11308");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_11434");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_19684");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_22350");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_13800");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_22787");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_22788");
		assertNotNull(entry);

		Set<BerkeleyDbGeneNameEntry> entry1Hits = dictionary.searchAliases("NPC derived proline rich protein 1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("HOMOLOGENE_GROUP_52", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());

		entry1Hits = dictionary.searchAliases("npcderivedprolinerichprotein");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("HOMOLOGENE_GROUP_52", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());

		dictionary.shutdown();

	}

}
