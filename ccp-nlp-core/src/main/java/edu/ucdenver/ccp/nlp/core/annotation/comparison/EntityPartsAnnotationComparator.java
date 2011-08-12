/*
 * EntityPartsAnnotationComparator.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package edu.ucdenver.ccp.nlp.core.annotation.comparison;

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
 * This class implements the functionality of the "Protein Name Parts" comparison metric described in Olson et al. 2002.
 * <p>
 * The usefulness of this kind of comparison is limited to simple entity mentions, therefore, the mention comparison
 * will compare the entire mention hierarchy, however, the entity parts computation will only take place on the base
 * annotation span.
 * <p>
 * For example, if for some reason you wanted to run the entity-parts metric on a group of transport annotations, where
 * each transport mention has complex slot mentions (transported entities, source, destination, transport participants),
 * then this method will
 * <p>
 * <ol>
 * <li>find equivalent transport annotations using the SloppySpanComparator
 * <li>if equivalent annotations are found, then the covered text for each annotation (and in this case, just for the
 * transport annotation, not any of its slot fillers) will be tokenized on whitespace, and the entity-parts metric will
 * be done using those tokens.
 * </ol>
 * 
 * @author William A Baumgartner, Jr.
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
	 * Since this method returns an integer, it does not make sense to use it to compare the entity parts, however, in
	 * order to override the default compare() method of AnnotationComparator which employs the strict span comparator,
	 * we will implement this method to return 0 if the resulting F measure is 1.0, -1 otherwise.
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
	 * Tokenize an annotation by whitespace.. this process removes any mention structure below the initial class
	 * mention.
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
					TextAnnotation newAnnot = new DefaultTextAnnotation(offset, offset + tok.length(), tok, ta.getAnnotator(), new AnnotationSet(
							-1, "", ""), ta.getAnnotationID(), ta.getDocumentCollectionID(), ta.getDocumentID(), ta.getDocumentSectionID(),
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
	 * EntityPartsAnnotationComparator.compare(List<TextAnnotation> goldTaList, List<TextAnnotation> testTaList)
	 */
	@Override
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> testTaList, SpanComparator spanComparator,
			MentionComparator mentionComparator, int maximumComparisonDepth) {
		return compare(goldTaList, testTaList);
	}

	/**
	 * Overridden - note this ignores all extraneous input parameters and simple returns
	 * EntityPartsAnnotationComparator.compare(List<TextAnnotation> goldTaList, List<TextAnnotation> testTaList)
	 */
	@Override
	public PRFResult compare(Collection<TextAnnotation> goldTaList, Collection<TextAnnotation> taList, SpanComparator spanComparator,
			MentionComparator mentionComparator) {
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
	public int compare(TextAnnotation ta1, TextAnnotation ta2, SpanComparator spanComparator, MentionComparator mentionComparator) {
		return compare(ta1, ta2);
	}

}
