/**
 * 
 */
package edu.ucdenver.ccp.nlp.core.util;

import java.util.List;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;


/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class StopWordUtil {

	public static List<String> STOPWORDS = CollectionsUtil.createList("i", "a", "is", "if", "be", "mm", "of", "no",
			"so", "mg", "ml", "as", "we", "at", "by", "in", "on", "it", "to", "do", "kg", "km", "an", "did", "due",
			"any", "for", "use", "may", "and", "etc", "are", "but", "can", "our", "how", "nor", "the", "has", "was",
			"had", "its", "all", "this", "done", "just", "also", "make", "upon", "show", "used", "very", "from",
			"most", "were", "must", "some", "what", "have", "than", "here", "that", "such", "when", "been", "with",
			"both", "them", "then", "into", "seem", "thus", "each", "made", "seen", "does", "they", "these", "about",
			"quite", "again", "which", "found", "might", "shown", "using", "among", "those", "shows", "since", "while",
			"being", "their", "often", "would", "there", "could", "during", "nearly", "having", "mostly", "enough",
			"always", "either", "mainly", "should", "showed", "before", "within", "theirs", "itself", "rather",
			"really", "almost", "perhaps", "through", "several", "another", "various", "further", "because", "neither",
			"between", "however", "without", "overall", "obtained", "although", "therefore", "regarding", "especially",
			"significantly");

	/**
	 * Removes stop words from the input text string. Stop words list taken from PUBMED.
	 * 
	 * @param queryStr
	 *            the string to be processed
	 * @return the input string without stop words
	 */
	public static String removeStopWords(String inputStr) {
		String alteredInputStr = inputStr;
		for (String stopword : STOPWORDS)
			alteredInputStr = alteredInputStr.replaceAll(("(^| )(" + stopword + ")($| )"), " ");
		return alteredInputStr;
	}

	public static boolean isStopWord(String word) {
		return STOPWORDS.contains(word.toLowerCase());
	}

}
