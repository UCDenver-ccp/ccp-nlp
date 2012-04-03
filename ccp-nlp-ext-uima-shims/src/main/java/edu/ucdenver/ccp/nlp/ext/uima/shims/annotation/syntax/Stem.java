/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax;

import edu.ucdenver.ccp.common.string.StringSerializable;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Stem implements StringSerializable<Stem> {

	private final String stem;

	/**
	 * @return the stem
	 */
	public String getStem() {
		return stem;
	}

	/**
	 * @param stem
	 */
	public Stem(String stem) {
		super();
		this.stem = stem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Stem [stem=" + stem + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.common.string.StringSerializable#serializeToString()
	 */
	@Override
	public String serializeToString() {
		return stem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.common.string.StringSerializable#deserializeFromString(java.lang.String)
	 */
	public static Stem deserializeFromString(String input) throws IllegalArgumentException {
		return new Stem(input);
	}

}
