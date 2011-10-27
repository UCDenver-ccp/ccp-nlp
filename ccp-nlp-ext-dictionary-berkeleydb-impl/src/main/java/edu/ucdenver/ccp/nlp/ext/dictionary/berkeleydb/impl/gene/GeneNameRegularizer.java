/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneNameRegularizer extends EntryRegularizer<BerkeleyDbGeneNameEntry> {

	@SuppressWarnings("unchecked")
	private static final Map<String, String> greekLetterToRegularizedFormMap = CollectionsUtil
			.createMap(CollectionsUtil.createList(new SimpleEntry<String, String>("alpha", "A"),
					new SimpleEntry<String, String>("\u03B1", "A"), new SimpleEntry<String, String>("beta", "B"),
					new SimpleEntry<String, String>("\u03B2", "B"), new SimpleEntry<String, String>("gamma", "G"),
					new SimpleEntry<String, String>("\u03B3", "G"), new SimpleEntry<String, String>("kappa", "K"),
					new SimpleEntry<String, String>("\u03BA", "K"), new SimpleEntry<String, String>("delta", "D"),
					new SimpleEntry<String, String>("\u03B4", "D"), new SimpleEntry<String, String>("lamda", "L"),
					new SimpleEntry<String, String>("\u03BB", "L"), new SimpleEntry<String, String>("epsilon", "E"),
					new SimpleEntry<String, String>("\u03B5", "E"), new SimpleEntry<String, String>("Alpha", "A"),
					new SimpleEntry<String, String>("\u0391", "A"), new SimpleEntry<String, String>("Beta", "B"),
					new SimpleEntry<String, String>("\u0392", "B"), new SimpleEntry<String, String>("Gamma", "G"),
					new SimpleEntry<String, String>("\u0393", "G"), new SimpleEntry<String, String>("Kappa", "K"),
					new SimpleEntry<String, String>("\u039A", "K"), new SimpleEntry<String, String>("Delta", "D"),
					new SimpleEntry<String, String>("\u0394", "D"), new SimpleEntry<String, String>("Lamda", "L"),
					new SimpleEntry<String, String>("\u039B", "L"), new SimpleEntry<String, String>("Epsilon", "E"),
					new SimpleEntry<String, String>("\u0395", "E")));

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer#regularize(java.lang.String)
	 */
	@Override
	public String regularize(String input) {
		String regularizedInput = doRoman(input);
		regularizedInput = doGreek(regularizedInput);
		regularizedInput = doCases(regularizedInput);
		regularizedInput = doParen(regularizedInput);
		regularizedInput = doPunct(regularizedInput);
		regularizedInput = doSpace(regularizedInput);
		regularizedInput = doLength(regularizedInput);
		regularizedInput = doEqual(regularizedInput);
		regularizedInput = getAlternative(regularizedInput);
		return regularizedInput;
	}

	/**
	 * @param regularizedInput
	 * @return
	 */
	private String getAlternative(String input) {
		/* A trailing '1' seems to be optional, e.g. TGF-beta1 is equivalent to TGF-beta */
		if (input.matches(".*?[^\\d]1"))
			return StringUtil.removeNTrailingCharacters(input, 1);
		return input;
	}

	/** replace equal strings */
	public static String doEqual(String str) {
		str = str.replaceAll("(?i)adrenergic receptor", "adrenoceptor");
		str = str.replaceAll("(?i)receptors?", "r");
		str = str.replaceAll("(?i)interleukin", "il");
		str = str.replaceAll("(?i)vacuolar", "v");
		return str;
	}
	
	/** normalize case */
	public static String doCases(String str) {
		return str.toLowerCase();
	}

	/** replace hyphens w/ spaces */
	public static String doHyphe(String str) {
		return str.replaceAll("-", " ");
	}

	/** remove punctuation */
	public static String doPunct(String str) {
		return str.replaceAll("\\p{Punct}+", "");
	}

	/** remove parenthesized materials */
	public static String doParen(String str) {
		return str.replaceAll("\\(.+?\\)", "");
	}

	/** remove very short strings */
	public static String doLength(String str) {
		if (str.length() <= 2)
			return "";
		return str;
	}

	/** remove all spaces */
	public static String doSpace(String str) {
		return str.replaceAll("\\s", "");
	}

	/** replace Greek letters with numerals - when they are followed by a word boundary or a digit  */
	public static String doGreek(String str) {
		Pattern pattern = Pattern
				.compile("(alpha|\u03B1|beta|\u03B2|gamma|\u03B3|kappa|\u03BA|delta|\u03B4|epsilon|\u03B5|lamda|\u03BB|Alpha|\u0391|Beta|\u0392|Gamma|\u0393|Kappa|\u039A|Delta|\u0394|Epsilon|\u0395|Lamda|\u039B)(\\b|\\d)");
		Matcher matcher = pattern.matcher(str);

		boolean found = false;
		while (matcher.find()) {
			String greek = matcher.group(1);
			str = str.replaceAll(greek, greekLetterToRegularizedFormMap.get(greek));
		}

		return str;
	}

	/** substitute Roman letters with numerals */
	public static String doRoman(String str) {
		Pattern pattern = Pattern.compile("(type\\s)?\\b([IiXxVv]+)\\b");
		Matcher matcher = pattern.matcher(str);
		StringBuffer newString = new StringBuffer();

		boolean found = false;
		int prevEnd = 0;
		while (matcher.find()) {
			MatchResult curMatch = matcher.toMatchResult();
			newString.append(str.substring(prevEnd, curMatch.start()));
			String substr = str.substring(curMatch.start(), curMatch.end());
			prevEnd = curMatch.end();

			String roman = matcher.group(2);

			if (matcher.group(1) != null) {
				substr = substr.replaceAll(matcher.group(1), "");
			}
			/*
			 * if (str.startsWith(roman)) { str = str.replaceAll(roman, ""); str = str.trim(); str =
			 * str.concat(" " + String.valueOf(roman2arabic(roman))); } else
			 */
			substr = substr.replaceAll(roman, String.valueOf(roman2arabic(roman)));
			newString.append(substr);
		}
		newString.append(str.substring(prevEnd, str.length()));
		return newString.toString();
	}

	/** transform Roman to Arabic numerals */
	private static int roman2arabic(String str) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("I", 1);
		map.put("V", 5);
		map.put("X", 10);
		map.put("i", 1);
		map.put("v", 5);
		map.put("x", 10);

		int curr = map.get(str.substring(0, 1));
		int sum = 0;

		for (int i = 1; i < str.length(); i++) {
			int next = map.get(str.substring(i, i + 1));
			if (curr < next) {
				sum -= curr;
			} else {
				sum += curr;
			}
			curr = next;
		}
		sum += curr;
		return sum;
	}

}
