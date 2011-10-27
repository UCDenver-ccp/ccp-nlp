/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneNameAliasFilterTest extends DefaultTestCase {

	@Test
	public void testFiltersShortNames() {
		assertEmpty("names < 3 characters should be filtered",
				GeneNameAliasFilter.filter(CollectionsUtil.createSet("", "a", "aa")));
	}
	
	
	@Test
	public void testFiltersNamesWithOnlyNumbers() {
		assertEmpty("names with only numbers should be filtered",
				GeneNameAliasFilter.filter(CollectionsUtil.createSet("0", "1", "1234567890")));
	}

}
