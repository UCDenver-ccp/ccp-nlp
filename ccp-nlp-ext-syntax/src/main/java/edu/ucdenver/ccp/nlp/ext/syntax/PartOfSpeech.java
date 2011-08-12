/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.syntax;

import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringSerializable;

/**
 * Utility class used for part-of-speech representation
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PartOfSpeech implements StringSerializable<PartOfSpeech> {
	private static final String STRING_SERIALIZATION_DELIMITER = StringConstants.VERTICAL_LINE;

	public static final String UNKNOWN_TAG_SET = "UNKNOWN";

	private final String posTag;
	private final String tagSetName;

	/**
	 * @param posTag
	 * @param tagSetName
	 */
	public PartOfSpeech(String posTag, String tagSetName) {
		super();
		this.posTag = posTag;
		this.tagSetName = tagSetName;
	}

	/**
	 * @return the posTag
	 */
	public String getPosTag() {
		return posTag;
	}

	/**
	 * @return the tagSetName
	 */
	public String getTagSetName() {
		return tagSetName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PartOfSpeech [posTag=" + posTag + ", tagSetName=" + tagSetName + "]";
	}

	@Override
	public String serializeToString() {
		return posTag + STRING_SERIALIZATION_DELIMITER + tagSetName;
	}

	
	public static PartOfSpeech deserializeFromString(String input) {
		String[] toks = input.split(RegExPatterns.escapeCharacterForRegEx(STRING_SERIALIZATION_DELIMITER));
		if (toks.length == 2)
			return new PartOfSpeech(toks[0], toks[1]);
		throw new IllegalArgumentException(
				"Cannot deserialize PartOfSpeech from String. Illegal number of tokens (expected 2 but was "
						+ toks.length + "): " + input);
	}

}
