package edu.ucdenver.ccp.nlp.core.annotation.comparison;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.comparison.MentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.util.StopWordUtil;

/**
 * This class implements the functionality of the "Protein Name Parts" comparison metric described
 * in Olson et al. 2002.
 * <p>
 * The usefulness of this kind of comparison is limited to simple entity mentions, therefore, the
 * mention comparison will compare the entire mention hierarchy, however, the entity parts
 * computation will only take place on the base annotation span.
 * <p>
 * For example, if for some reason you wanted to run the entity-parts metric on a group of transport
 * annotations, where each transport mention has complex slot mentions (transported entities,
 * source, destination, transport participants), then this method will
 * <p>
 * <ol>
 * <li>find equivalent transport annotations using the SloppySpanComparator
 * <li>if equivalent annotations are found, then the covered text for each annotation (and in this
 * case, just for the transport annotation, not any of its slot fillers) will be tokenized on
 * whitespace, and the entity-parts metric will be done using those tokens.
 * </ol>
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class EntityPartsAnnotationComparator extends AnnotationComparator {

	@Override
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> testTaList) {

		List<TextAnnotation> goldAnnotationPieces = new ArrayList<TextAnnotation>();
		List<TextAnnotation> testAnnotationPieces = new ArrayList<TextAnnotation>();

		/* for each gold annotation, split it and add its pieces to the pieces list */
		for (TextAnnotation ta : goldTaList) {
			List<TextAnnotation> pieces = tokenizeAnnotation(ta);
			goldAnnotationPieces.addAll(pieces);
		}

		/* for each test annotation, split it and add its pieces to the pieces list */
		for (TextAnnotation ta : testTaList) {
			List<TextAnnotation> pieces = tokenizeAnnotation(ta);
			testAnnotationPieces.addAll(pieces);
		}

		AnnotationComparator ac = new AnnotationComparator();
		return ac.compare(goldAnnotationPieces, testAnnotationPieces);

	}

	/**
	 * Since this method returns an integer, it does not make sense to use it to compare the entity
	 * parts, however, in order to override the default compare() method of AnnotationComparator
	 * which employs the strict span comparator, we will implement this method to return 0 if the
	 * resulting F measure is 1.0, -1 otherwise.
	 */
	@Override
	public int compare(TextAnnotation goldTA, TextAnnotation testTA) {
		List<TextAnnotation> goldTAList = new ArrayList<TextAnnotation>();
		List<TextAnnotation> testTAList = new ArrayList<TextAnnotation>();

		goldTAList.add(goldTA);
		testTAList.add(testTA);

		PRFResult prf = compare(goldTAList, testTAList);
		if (prf.getFmeasure() == 1.0) {
			return 0;
		} else {
			return -1;
		}

	}

	/*
	 * Tokenize an annotation by whitespace.. this process removes any mention structure below the
	 * initial class mention.
	 * 
	 * Stopwords are not counted.
	 */
	private static List<TextAnnotation> tokenizeAnnotation(TextAnnotation ta) {
		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();

		String coveredText = ta.getCoveredText();
		String[] toks = coveredText.split(" ");

		int offset = ta.getAnnotationSpanStart();

		for (String tok : toks) {
			if (!StopWordUtil.isStopWord(tok)) {
				/* if it's not a stop word, then create a TextAnnotation */
				if (!tok.equals(" ")) {
					/* if it's not a space then create a TextAnnotation */
					TextAnnotation newAnnot = new DefaultTextAnnotation(offset, offset + tok.length(), tok,
							ta.getAnnotator(), new AnnotationSet(-1, "", ""), ta.getAnnotationID(),
							ta.getDocumentCollectionID(), ta.getDocumentID(), ta.getDocumentSectionID(),
							new DefaultClassMention(ta.getClassMention().getMentionName()));
					annotationsToReturn.add(newAnnot);
				}
			}
			offset += (tok.length() + 1);
		}
		return annotationsToReturn;
	}

	/**
	 * Overridden - note this ignores all extraneous input parameters and simple returns
	 * EntityPartsAnnotationComparator.compare(List<TextAnnotation> goldTaList, List<TextAnnotation>
	 * testTaList)
	 */
	@Override
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> testTaList,
			SpanComparator spanComparator, MentionComparator mentionComparator, int maximumComparisonDepth) {
		return compare(goldTaList, testTaList);
	}

	/**
	 * Overridden - note this ignores all extraneous input parameters and simple returns
	 * EntityPartsAnnotationComparator.compare(List<TextAnnotation> goldTaList, List<TextAnnotation>
	 * testTaList)
	 */
	@Override
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> taList,
			SpanComparator spanComparator, MentionComparator mentionComparator) {
		return compare(goldTaList, taList);
	}

	/**
	 * Overridden - note this ignores all extraneous input parameters and simple returns
	 * EntityPartsAnnotationComparator.compare(TextAnnotation ta1, TextAnnotation ta2)
	 */
	@Override
	public int compare(TextAnnotation ta1, TextAnnotation ta2, SpanComparator spanComparator,
			MentionComparator mentionComparator, int maximumComparisonDepth) {
		return compare(ta1, ta2);
	}

	/**
	 * Overridden - note this ignores all extraneous input parameters and simple returns
	 * EntityPartsAnnotationComparator.compare(TextAnnotation ta1, TextAnnotation ta2)
	 */
	@Override
	public int compare(TextAnnotation ta1, TextAnnotation ta2, SpanComparator spanComparator,
			MentionComparator mentionComparator) {
		return compare(ta1, ta2);
	}

}
