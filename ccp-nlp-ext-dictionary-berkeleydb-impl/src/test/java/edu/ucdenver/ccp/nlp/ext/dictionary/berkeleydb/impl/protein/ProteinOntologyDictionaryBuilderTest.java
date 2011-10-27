/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.protein;

import static org.junit.Assert.assertEquals;
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
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbGeneNameEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.GeneNameRegularizer;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class ProteinOntologyDictionaryBuilderTest extends DefaultTestCase {

	private static final String SAMPLE_PRO_OBO_FILE_NAME = "pro.obo";

	/**
	 * @param dataDirectory
	 * @throws IOException
	 */
	private void createTestDataFiles(File dataDirectory) throws IOException {
		ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_PRO_OBO_FILE_NAME, dataDirectory);
	}

	@Test
	public void testBuildProteinOntologyDictionary() throws IOException, InstantiationException, IllegalAccessException {
		File dictionaryDirectory = folder.newFolder("pro-dict");
		File dataDirectory = folder.newFolder("data");
		createTestDataFiles(dataDirectory);
		assertEquals(1, dataDirectory.list().length);

		ProteinOntologyDictionaryBuilder dictBuilder = new ProteinOntologyDictionaryBuilder(dictionaryDirectory, dataDirectory,
				false);
		dictBuilder.buildDictionary(LoadMode.OVERWRITE);

		Dictionary<BerkeleyDbGeneNameEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(
				dictionaryDirectory, ProteinOntologyDictionaryBuilder.STORE_NAME,
				ProteinOntologyDictionaryBuilder.ENTRY_INDEX_ACCESSOR_CLASS,
				ProteinOntologyDictionaryBuilder.DICTIONARY_ENTRY_CLASS);
		dictionary.open(AccessMode.READONLY);

		BerkeleyDbGeneNameEntry entry = dictionary.retrieve("PR:000000046");
		assertNotNull(entry);
		
		Set<BerkeleyDbGeneNameEntry> entry1Hits = dictionary.searchAliases("tgfb");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("PR:000000046", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());

		GeneNameRegularizer er = ProteinOntologyDictionaryBuilder.ENTRY_REGULARIZER_CLASS.newInstance();
		entry1Hits = dictionary.searchAliases(er, "TGF-beta");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("PR:000000046", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		
		entry1Hits = dictionary.searchAliases(er, "TGF-beta1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("PR:000000046", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		
		
		entry1Hits = dictionary.searchAliases(er, "LTBP1");
		assertEquals("search for entry1 alias failed.", 1, entry1Hits.size());
		assertEquals("PR:000000101", CollectionsUtil.getSingleElement(entry1Hits).getDictionaryKey());
		
		entry1Hits = dictionary.searchAliases(er, "E7");
		assertEquals("search for entry1 alias failed.", 0, entry1Hits.size());
		dictionary.shutdown();

	}

}
