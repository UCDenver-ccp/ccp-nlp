package edu.ucdenver.ccp.nlp.core.mention.comparison;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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


import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMention;

/**
 * Implements the comparison of a pair of <code>SlotMentions</code>.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class IdenticalSlotMentionComparator extends IdenticalMentionComparator implements SlotMentionComparator {

	/**
	 * Compare two slot mentions. They must be SlotMentions or else an UnsupportedOperationException
	 * is thrown. In order to be identical (and return 0), they must have the name mention name, and
	 * the identical slot values
	 */
	public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth,
			int depth) {
		if ((mention1 instanceof SlotMention) & (mention2 instanceof SlotMention)) {
			return compare((SlotMention) mention1, (SlotMention) mention2, spanComparator, maximumComparisonDepth,
					depth);
		} else {
			throw new ClassCastException("Cannot support comparison between a SlotMention and a non-SlotMention");
		}
	}

	/**
	 * Compare two SlotMentions. In order to be identical (and return 0), they must have the same
	 * mention name, and the identical slot values
	 * 
	 * @param slotMention1
	 * @param slotMention2
	 * @param maximumComparisonDepth
	 * @param depth
	 * @param spanComparator
	 * @param compareLinkedAnnotationSpans
	 * @return
	 */
	@Override
	public int compare(SlotMention slotMention1, SlotMention slotMention2, SpanComparator spanComparator,
			int maximumComparisonDepth, int depth) {
		if (slotMention1.getSlotValues().size() == slotMention2.getSlotValues().size()) {
			/* quick check to make sure each slot mention has the same number of slot values */
			if (hasEquivalentMentionNames(slotMention1, slotMention2)) {
				for (Object slotValue : slotMention1.getSlotValues()) {
					boolean objectHasMatch = false;
					for (Object svToCompare : slotMention2.getSlotValues()) {
						if (svToCompare instanceof String) {
							if (((String) slotValue).equalsIgnoreCase((String) svToCompare)) {
								objectHasMatch = true;
							}
						} else {
							if (slotValue.equals(svToCompare)) {
								objectHasMatch = true;
							}
						}
					}
					if (!objectHasMatch) {
						/*
						 * if they do not match, then simply return the comparison between their
						 * single line representation strings
						 */
						return slotMention1.getSingleLineRepresentation().compareTo(
								slotMention2.getSingleLineRepresentation())
								* MULTIPLIER;
					}
				}
				/* if we get to this point, then all slot values have a match, so return 0 */
				return 0;
			} else {
				/* the sm's have different mention names */
				return slotMention1.getSingleLineRepresentation().compareTo(slotMention2.getSingleLineRepresentation())
						* MULTIPLIER;
			}
		} else {
			/* the sm's have unequal number of slot values */
			return slotMention1.getSingleLineRepresentation().compareTo(slotMention2.getSingleLineRepresentation())
					* MULTIPLIER;
		}

	}

}
