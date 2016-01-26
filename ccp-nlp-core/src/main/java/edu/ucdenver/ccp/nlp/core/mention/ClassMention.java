package edu.ucdenver.ccp.nlp.core.mention;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;

/**
 * <p>
 * The CCPClassMention is the root of a flexible class structure that can store virtually any
 * frame-based representation of a particular class. Common class mention types include, but are not
 * limited to, such things as entities (protein, cell type, cell line, disease, tissue, etc.) and
 * frames (interaction, transport, regulation, etc.).
 * <p>
 * A class mention optionally has slot mentions which represent attributes of that class. There are
 * two types of slot mentions, complex and primitive. The difference between complex and primitive
 * slot mentions is simply the type of filler (or slot value) for each. Complex slot mentions are
 * filled with a class mention, whereas primitive slot mentions are filled by simple primitive
 * values, e.g. integers, floats, etc.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class ClassMention extends Mention implements IClassMention {

	private static Logger logger = Logger.getLogger(ClassMention.class);

	public ClassMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		String returnStr = getIndentString(indentLevel) + "CLASS MENTION: " + getMentionName();
		if (showReferencingAnnotationInfo) {
			returnStr += (" " + getReferencingAnnotationString());
		}
		return returnStr;
	}

	/**
	 * Returns a compact representation of the referencing annotation: coveredText
	 * [spanStart..spanEnd]
	 * 
	 * @return
	 */
	private String getReferencingAnnotationString() {
		String annotationString = "";
		String spanStr = "";
		if (this.getTextAnnotation() != null) {
			annotationString = this.getTextAnnotation().getCoveredText();
			spanStr = this.getTextAnnotation().getAggregateSpan().toString();
		}

		return "\"" + annotationString + "\"\t" + spanStr;
	}

	@Override
	public int hashCode() {
		return getSingleLineRepresentation().hashCode();
	}

	/**
	 * The default compareTo uses the IdenticalClassMentionComparator()
	 */
	public int compareTo(Mention m) {
		if (m instanceof ClassMention) {
			ClassMention cmToCompare = (ClassMention) m;
			IdenticalMentionComparator icmc = new IdenticalMentionComparator();
			return icmc.compare(this, cmToCompare);
		} else {
			logger.warn("Unexpected object when comparing to ClassMention: object = " + m.getClass().getName());
			return -1;
		}

	}

	/**
	 * The default equals() method uses the IdenticalClassMentionComparator.
	 */
	@Override
	public boolean equals(Object classMentionToEquate) {
		if (!(classMentionToEquate instanceof ClassMention)) {
			throw new ClassCastException("A ClassMention object expected.");
		} else {
			ClassMention cm = (ClassMention) classMentionToEquate;

			IdenticalMentionComparator icmc = new IdenticalMentionComparator();

			if (icmc.compare(this, cm) == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

}
