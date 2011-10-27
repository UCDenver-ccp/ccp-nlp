/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneTaggerMentionFilter {

	public static String filterMention(String mention) {
		/** remove gene chromosome location like 3p11-3p12.1 */
		if (mention.matches("\\d+(p|q)(\\d|\\.)+(\\-\\d*(p|q)(\\d|\\.)+)?") || mention.matches("(p|q)\\d+\\.\\d+"))
			return "";

		/** remove all short lowercase single words */
		if (mention.matches("[a-z]+") && mention.length() <= 4)
			return "";

		/** remove mentions with only numbers and/or punctuation */
		if (mention.matches("(\\p{Punct}|\\p{Digit})+"))
			return "";

		/** remove preceding extra words */
		Pattern pattern = Pattern.compile("(?i).*(protein|gene|receptor|molecule)s?\\s(\\w+)");
		Matcher matcher = pattern.matcher(mention);
		if (matcher.find()) {
			mention = matcher.group(2);
		}

		/** remove extra words */
		List<String> list = Arrays.asList("human", "protein", "gene", "cDNA", "mRNA", "chain", "sequence", "complex",
				"transcript", "loci", "locus", "enzyme");
		for (String word : list) {
			mention = mention.replaceAll("\\b" + word + "(s|es)?\\b", " ");
			mention = mention.trim();
		}

		/** remove amino acids */
		List<String> aminoAcids = Arrays.asList("ala", "cys", "asp", "glu", "phe", "gly", "his", "lle", "lys", "leu",
				"met", "asn", "pro", "gln", "arg", "ser", "thr", "val", "trp", "tyr");
		for (String aa : aminoAcids) {
			if (mention.matches("(?i)" + aa + "-?\\d*")) {
				mention = "";
			}
		}

		/** remove protein families */
		if (mention.matches(".*(?i)(family|group|proteins|kinases|families|genes|receptors).*")) {
			mention = "";
		}

		/** remove domains, motif, fusion, etc */
		if (mention.matches("(?i).*(island|sequence|fusion|domain|motif)s?")) {
			mention = "";
		}
		/** remove amino acids */
		if (mention.matches("[U]\\d+")) {
			mention = "";
		}

		/** remove organism names */
		List<String> organismList = Arrays.asList("yeast", "Saccharomyces cerevisiae", "mouse", "rat", "murine",
				"rabbit", "Drosophila", "HIV");

		for (String word : list) {
			if (mention.matches("(?i)\\b" + word + "\\b.*")) {
				mention = "";
			}
		}

		// strip the first small case h
		if (mention.matches("^h[A-Z]\\w+$")) {
			mention = mention.substring(1);
		}

		return mention;
	}

}
