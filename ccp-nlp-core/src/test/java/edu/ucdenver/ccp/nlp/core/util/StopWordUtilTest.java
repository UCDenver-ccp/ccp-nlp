/**
 * 
 */
package edu.ucdenver.ccp.nlp.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class StopWordUtilTest {

	@Test
	public void testRemoveStopWords() throws Exception {

		String testText = " the cat";
		String stopWordsRemoved = StopWordUtil.removeStopWords(testText).trim();
		String expectedText = "cat";
		assertEquals(expectedText, stopWordsRemoved);

		testText = "the";
		stopWordsRemoved = StopWordUtil.removeStopWords(testText).trim();
		expectedText = "";
		assertEquals(expectedText, stopWordsRemoved);

	}

}
