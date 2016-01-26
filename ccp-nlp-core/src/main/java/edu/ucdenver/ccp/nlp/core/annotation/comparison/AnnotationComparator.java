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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.comparison.MentionComparator;

/**
 * This class implements comparator functionality to compare two <code>TextAnnotations</code>.
 * <p>
 * The methods returns negative integer, zero, or a positive integer. Zero indicates that the two
 * TextAnnotation objects are equal given the comparison criteria. The negative and positive integer
 * serve as an aggregate score derived from comparing both the span and class of each
 * TextAnnotation.
 * <p>
 * Span comparison results in 0 if the spans are equal given the comparison criteria, -1 of the
 * span(s) from the first TextAnnotation preceeds the span(s) of the second TextAnnotation, and 1
 * otherwise.
 * <p>
 * The class comparison returns larger number. 0 is returned for classes that match given the
 * comparison criteria, however, -3 is returned TextAnnotation according to their spans.
 * <p>
 * The AnnotationComparator returns an aggreate of the span and class comparison operations, thus
 * enabling the ultimate result to be deciphered externally of the Annotation Comparator. The
 * default comparison utilizes the StrictSpanComparator and the IdenticalClassComparator
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */

public class AnnotationComparator implements Comparator<TextAnnotation> {
	private static Logger logger = Logger.getLogger(AnnotationComparator.class);

	/**
	 * TextAnnotation comparison utilizes the StrictSpanComparator and the
	 * IdenticalMentionComparator family
	 */
	public int compare(TextAnnotation ta1, TextAnnotation ta2) {
		return compare(ta1, ta2, new StrictSpanComparator(), new IdenticalMentionComparator());
	}

	/**
	 * Compares two TextAnnotations using the supplied SpanComparator and ClassMentionComparator
	 * 
	 * @param ta1
	 * @param ta2
	 * @param spanComparator
	 * @param classMentionComparator
	 * @return
	 */
	public int compare(TextAnnotation ta1, TextAnnotation ta2, SpanComparator spanComparator,
			MentionComparator mentionComparator) {
		return compare(ta1, ta2, spanComparator, mentionComparator, -1);
	}

	/**
	 * comparison of two lists of TextAnnotations utilizing the StrictSpanComparator and the
	 * IdenticalMentionComparator family
	 * 
	 * @param taList1
	 * @param taList2
	 * @return
	 */
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> taList) {
		return compare(goldTaList, taList, new StrictSpanComparator(), new IdenticalMentionComparator());
	}

	/**
	 * Compares two lists of TextAnnotations using the supplied SpanComparator and
	 * ClassMentionComparator
	 * 
	 * @param taList1
	 * @param taList2
	 * @param spanComparator
	 * @param classMentionComparator
	 * @return
	 */
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> taList,
			SpanComparator spanComparator, MentionComparator mentionComparator) {
		return compare(goldTaList, taList, spanComparator, mentionComparator, -1);
	}

	/**
	 * Compares two TextAnnotations using the supplied SpanComparator and ClassMentionComparator,
	 * but only compares to the supplied comparison depth.
	 * 
	 * @param ta1
	 * @param ta2
	 * @param spanComparator
	 * @param mentionComparator
	 * @return
	 */
	public int compare(TextAnnotation ta1, TextAnnotation ta2, SpanComparator spanComparator,
			MentionComparator mentionComparator, int maximumComparisonDepth) {
		/*
		 * span comparison is done during class comparison by default, therefore this is somewhat
		 * redundant..
		 */
		int spanComparisonResult = spanComparator.compare(ta1.getSpans(), ta2.getSpans());
		int mentionComparisonResult = mentionComparator.compare(ta1.getClassMention(), ta2.getClassMention(),
				spanComparator, maximumComparisonDepth);
		int metaDataComparisonResult = metaDataCompare(ta1, ta2);
		return spanComparisonResult + mentionComparisonResult + metaDataComparisonResult;
	}

	/**
	 * Compares two lists of TextAnnotations using the supplied SpanComparator and
	 * ClassMentionComparator, but only compares to the supplied comparison depth.
	 * 
	 * @param taList1
	 * @param taList2
	 * @param spanComparator
	 * @param classMentionComparator
	 * @param maximumComparisonDepth
	 * @return
	 */
	public PRFResult compare(Collection<TextAnnotation> goldTas, Collection<TextAnnotation> testTas,
			SpanComparator spanComparator, MentionComparator mentionComparator, int maximumComparisonDepth) {

		List<TextAnnotation> goldTaList = Collections.list(Collections.enumeration(goldTas));
		List<TextAnnotation> testTaList = Collections.list(Collections.enumeration(testTas));

		boolean[] foundMatchForGold = new boolean[goldTaList.size()];
		boolean[] foundMatchForTest = new boolean[testTaList.size()];
		int fn = 0;

		/* keep track of the tp, fp, and fn annotations */
		List<TextAnnotation> tpAnnotations = new ArrayList<TextAnnotation>();
		List<TextAnnotation> fpAnnotations = new ArrayList<TextAnnotation>();
		List<TextAnnotation> fnAnnotations = new ArrayList<TextAnnotation>();

		/*
		 * If spansMustOverlapToMatch = true, then we can limit the number of comparisons made by
		 * only comparing those annotations that overlap. This should speed up processing,
		 * especially for tasks such as comparing tokens in a document where it is clearly
		 * unnecessary to compare the first token in the document with the final token. Previously
		 * this brute force approach was used. It worked, but could be slow depending on how many
		 * annotations are in the document.
		 */
		boolean spansMustOverlapToMatch = spanComparator.spansMustOverlapToMatch();

		int indexInTestAnnotationSpansList = 0;
		if (spansMustOverlapToMatch) {
			/* ensure that the gold standard TA's are sorted. */
			Collections.sort(goldTaList, TextAnnotation.BY_SPAN());
			Collections.sort(testTaList, TextAnnotation.BY_SPAN());

			/* make sure sorting worked */
			int previousIndex = -1;
			for (TextAnnotation ta : goldTaList) {
				if (ta.getAnnotationSpanStart() < previousIndex) {
					logger.error("Error in text annotation sorting...");
				}
			}
		}

		for (int i = 0; i < goldTaList.size(); i++) {
			TextAnnotation goldTA = goldTaList.get(i);
			if (spansMustOverlapToMatch) {

				/*
				 * advance the pointer into the test annotation list until we find one that overlaps
				 * the gold standard annotation, or until the gold standard annotation span start
				 * has passed the test annotation span start
				 */
				// if (indexInTestAnnotationSpansList < testTaList.size()) {
				// logger.debug("Should we compare GOLD=" +
				// goldTA.getSingleLineRepresentation(false, false) +
				// " with TEST: " +
				// testTaList.get(indexInTestAnnotationSpansList).getSingleLineRepresentation(false,
				// false));
				// }
				while (indexInTestAnnotationSpansList < testTaList.size()
						&& (goldTA.getAnnotationSpanStart() >= testTaList.get(indexInTestAnnotationSpansList)
								.getAnnotationSpanStart() && !goldTA.overlaps(testTaList
								.get(indexInTestAnnotationSpansList)))) {

					// logger.debug("advancing pointer... gold does not overlap with test: "
					// + goldTA.getSingleLineRepresentation(false, false) + " -- "
					// +
					// testTaList.get(indexInTestAnnotationSpansList).getSingleLineRepresentation(false,
					// false));
					indexInTestAnnotationSpansList++;

				}

				/*
				 * if the pointer into the test annotation list is off the bottom of the list, then
				 * there are no more test annotations to compare against. We cycle through the test
				 * annotations while they overlap the gold standard annotation, AND while/if they
				 * are upstream of the gold standard annotation. This last criteria is due to a
				 * tricky situation that arose with some annotations with split spans. See the Unit
				 * Test: testComparisonWhenEmbeddedAnnotationExists for details.
				 */
				// logger.debug("place holder = " + indexInTestAnnotationSpansList);
				int indexPlaceHolder = indexInTestAnnotationSpansList;
				while (indexInTestAnnotationSpansList < testTaList.size()
						&& (testTaList.get(indexInTestAnnotationSpansList).getAnnotationSpanEnd() < goldTA
								.getAnnotationSpanStart() || goldTA.overlaps(testTaList
								.get(indexInTestAnnotationSpansList)))) {
					TextAnnotation testTA = testTaList.get(indexInTestAnnotationSpansList);

					/* compare the two TextAnnotations */

					int comparisonResult = compare(goldTA, testTA, spanComparator, mentionComparator,
							maximumComparisonDepth);
					// logger.debug("RESULT = " + comparisonResult + " COMPARING (" +
					// indexInTestAnnotationSpansList +
					// "): "
					// + goldTA.getSingleLineRepresentation(false, false) + " -- " +
					// testTA.getSingleLineRepresentation(false, false));
					if (comparisonResult == 0) {
						/*
						 * we have found an exact match, so mark that appropriate gold annotation as
						 * matched
						 */
						foundMatchForGold[i] = true;
						foundMatchForTest[indexInTestAnnotationSpansList] = true;
					}

					indexInTestAnnotationSpansList++;
				}

				/*
				 * rollback the indexInTestAnnotationSpansList to the indexPlaceHolder to get ready
				 * for the next Gold Standard Annotations. Since the GS annotations are sorted, we
				 * know that we don't need to compare the next one to any of the previous test
				 * annotations, but we might want to compare it to some of the test annotations that
				 * were compared to the current GS annotation, so we roll back
				 */
				indexInTestAnnotationSpansList = indexPlaceHolder;

			} else {
				/*
				 * Since there is no guarantee that the spans must overlap, the brute force approach
				 * must be applied and each annotation must be compared to every other annotation in
				 * the document
				 */
				for (int j = 0; j < testTaList.size(); j++) {
					TextAnnotation testTA = testTaList.get(j);

					/* compare the two TextAnnotations */
					int comparisonResult = compare(goldTA, testTA, spanComparator, mentionComparator,
							maximumComparisonDepth);
					// logger.debug("RESULT = " + comparisonResult + " COMPARING (" +
					// indexInTestAnnotationSpansList +
					// "): "
					// + goldTA.getSingleLineRepresentation(false, false) + " -- " +
					// testTA.getSingleLineRepresentation(false, false));

					if (comparisonResult == 0) {
						/*
						 * we have found an exact match, so mark that appropriate gold annotation as
						 * matched
						 */
						foundMatchForGold[i] = true;
						foundMatchForTest[j] = true;
					}
				}
			}

			/*
			 * after cycling through all of the annotations in the testTAList, if we did not find a
			 * match for this particular goldTA, then we need to increment the False Negative count
			 */
			if (!foundMatchForGold[i]) {
				fn++;
				fnAnnotations.add(goldTA);
			}
		}

		/*
		 * now that we have cycled through the entire set of annotations, we can compute the number
		 * of True Positives and False Positives by examining the foundMatchForTest array
		 */
		int tp = 0;
		int fp = 0;
		for (int i = 0; i < foundMatchForTest.length; i++) {
			if (foundMatchForTest[i]) {
				tp++;
				tpAnnotations.add(testTaList.get(i));
			} else {
				fp++;
				fpAnnotations.add(testTaList.get(i));
			}
		}

		PRFResult prfResult = new PRFResult(tp, fp, fn);
		prfResult.setTPAnnotations(tpAnnotations);
		prfResult.setFPAnnotations(fpAnnotations);
		prfResult.setFNAnnotations(fnAnnotations);

		return prfResult;
	}

	/**
	 * Compare the meta data of two annotations (document ID, document collection ID, and span(s)).
	 * A warning is issued if it is determined that two annotations do not have identical meta data
	 * 
	 * @param ta
	 * @return
	 */
	public int metaDataCompare(TextAnnotation ta1, TextAnnotation ta2) {
		/* document IDs must be equal */
		boolean equalDocumentID = ta1.getDocumentID().equals(ta2.getDocumentID());

		/* document collection ids must be equal */
		boolean equalDocumentCollectionID = (ta1.getDocumentCollectionID() == ta2.getDocumentCollectionID());

		if (equalDocumentID & equalDocumentCollectionID) {
			return 0;
		} else {
			// warn("Detected the attempted comparison of two annotations with different meta data,
			// implying they are
			// from different document
			// collections, or different documents.");
			return -1000;
		}
	}
}
