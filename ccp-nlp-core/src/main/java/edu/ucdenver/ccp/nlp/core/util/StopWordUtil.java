/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
