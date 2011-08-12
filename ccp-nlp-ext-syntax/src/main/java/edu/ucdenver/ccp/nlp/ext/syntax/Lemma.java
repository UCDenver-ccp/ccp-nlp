/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.syntax;

import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringSerializable;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Lemma implements StringSerializable<Lemma> {

	private static final String STRING_SERIALIZATION_DELIMITER = StringConstants.SEMICOLON;

	private final String lemma;
	private final PartOfSpeech pos;

	/**
	 * @param lemma
	 * @param pos
	 */
	public Lemma(String lemma, PartOfSpeech pos) {
		super();
		this.lemma = lemma;
		this.pos = pos;
	}

	/**
	 * @param lemma
	 * @param posTag
	 * @param tagSetName
	 */
	public Lemma(String lemma, String posTag, String tagSetName) {
		this(lemma, new PartOfSpeech(posTag, tagSetName));
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @return the pos
	 */
	public PartOfSpeech getPos() {
		return pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lemma [lemma=" + lemma + ", pos=" + pos.toString() + "]";
	}

	@Override
	public String serializeToString() {
		return lemma + STRING_SERIALIZATION_DELIMITER + pos.serializeToString();
	}

	public static Lemma deserializeFromString(String input) {
		String[] toks = input.split(RegExPatterns.escapeCharacterForRegEx(STRING_SERIALIZATION_DELIMITER));
		if (toks.length == 2)
			try {
				return new Lemma(toks[0], PartOfSpeech.deserializeFromString(toks[1]));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Cannot deserialize Lemma from String, illegal PartOfSpeech representation.", e);
			}
		throw new IllegalArgumentException(
				"Cannot deserialize Lemma from String. Illegal number of tokens (expected 2 but was " + toks.length
						+ "): " + input);
	}

}
