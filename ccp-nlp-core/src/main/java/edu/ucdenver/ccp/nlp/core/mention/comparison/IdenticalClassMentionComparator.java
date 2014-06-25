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
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;

/**
 * This class implements a comparator which compares ClassMentions. ClassMentions must be identical.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class IdenticalClassMentionComparator extends IdenticalMentionComparator implements ClassMentionComparator {

	/**
	 * Compare two mentions. They must be ClassMentions or else an UnsupportedOperationException is
	 * thrown.
	 */
	public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth,
			int depth) {
		if ((mention1 instanceof ClassMention) & (mention2 instanceof ClassMention)) {
			return compare((ClassMention) mention1, (ClassMention) mention2, spanComparator, maximumComparisonDepth,
					depth);
		} else {
			throw new ClassCastException("Cannot support comparison between a ClassMention and a non-ClassMention");
		}

	}

	/**
	 * 
	 * @param cm1
	 * @param cm2
	 * @param spanComparator
	 * @param maximumComparisonDepth
	 * @return
	 */
	@Override
	public int compare(ClassMention cm1, ClassMention cm2, SpanComparator spanComparator, int maximumComparisonDepth) {
		return compare(cm1, cm2, spanComparator, maximumComparisonDepth, -1);
	}

	/**
	 * This is a more specific compare() method for ClassMentions.
	 * 
	 * @param cm1
	 * @param cm2
	 * @param maximumDepth
	 *            specifies the depth in the mention hierarchy
	 * @param depth
	 * @param compareLinkedAnnotationSpans
	 *            toggles the comparison of the linked TextAnnotations. In some cases, the linked
	 *            TextAnnotations may have already been compared, so an infinite loop would be
	 *            created by comparing them again.
	 * @return
	 * 
	 *         TODO: we are assuming only a single text annotation per class mention
	 */
	private int compare(ClassMention cm1, ClassMention cm2, SpanComparator spanComparator, int maximumComparisonDepth,
			int depth) {

		/* increment the depth level */
		depth++;

		if (hasEquivalentMentionNames(cm1, cm2)) {
			/*
			 * the mention names are identical, so we need to now check the slots and complex slots,
			 * unless we have reached the maximum comparison depth
			 */
			/* if we have reached the maximum comparison depth, then return now */
			if (maximumComparisonDepth != -1 && depth >= maximumComparisonDepth) {
				return spanComparator.compare(cm1.getTextAnnotation().getSpans(), cm2.getTextAnnotation().getSpans());
			}
			/*
			 * we have not yet reached the maximum depth, so we need to compare the slot mentions
			 * and complex slot mentions
			 */
			boolean annotationSpansMatch = false;
			annotationSpansMatch = (spanComparator.compare(cm1.getTextAnnotation().getSpans(), cm2.getTextAnnotation()
					.getSpans()) == 0);

			boolean slotMentionsMatch = false;
			boolean complexSlotMentionsMatch = false;

			if (annotationSpansMatch & slotMentionsMatch & complexSlotMentionsMatch) {
				return 0;
			}
			return cm1.getSingleLineRepresentation().compareTo(cm2.getSingleLineRepresentation()) * MULTIPLIER;

		}
		return cm1.getSingleLineRepresentation().compareTo(cm2.getSingleLineRepresentation()) * MULTIPLIER;
	}

}
