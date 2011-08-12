/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class NLPStatsTest {

	private int tp;
	private int fp;
	private int fn;

	private double alpha;

	private double precision;
	private double recall;

	@Before
	public void setUp() throws Exception {
		tp = 15;
		fp = 4;
		fn = 7;
		alpha = 0.1;
		precision = 0.65;
		recall = 0.75;
	}

	@Test
	public void testComputePrecision() {
		assertEquals((double) tp / ((double) tp + (double) fp), NLPStats.computePrecision(tp, fp), 0.0);
	}

	@Test
	public void testComputeRecall() {
		assertEquals((double) tp / ((double) tp + (double) fn), NLPStats.computeRecall(tp, fn), 0.0);
	}

	@Test
	public void testComputeFMeasurePR() {
		assertEquals(2 * precision * recall / (precision + recall), NLPStats.computeFMeasure(precision, recall), 0.0);
	}

	@Test
	public void testComputeFMeasureTPFPFN() {
		assertEquals(2 * ((double) tp / ((double) tp + (double) fp)) * ((double) tp / ((double) tp + (double) fn))
				/ (((double) tp / ((double) tp + (double) fp)) + ((double) tp / ((double) tp + (double) fn))), NLPStats.computeFMeasure(tp,
				fp, fn), 0.0);
	}

	@Test
	public void testComputeClassicDice() throws Exception {
		String exampleSentence = "When taken as a string similarity measure, the coefficient may be calculated for two strings, x and y using bigrams as follows.";
		String exactMatch = "When taken as a string similarity measure, the coefficient may be calculated for two strings, x and y using bigrams as follows.";

		Set<String> exampleSentenceUniqueTokens = new HashSet<String>(Arrays.asList(exampleSentence.split(" ")));
		Set<String> exactMatchUniqueTokens = new HashSet<String>(Arrays.asList(exactMatch.split(" ")));

		/* check that equal sentences result in a Dice coefficient of 1.0 */
		assertEquals(1.0, NLPStats.computeClassicDice(exampleSentenceUniqueTokens, exactMatchUniqueTokens), 0.0);

		String partialMatch = "the coefficient may be calculated for two strings, x and y using bigrams as follows.";
		Set<String> partialMatchUniqueTokens = new HashSet<String>(Arrays.asList(partialMatch.split(" ")));
		assertEquals((2.0 * 15.0) / (21.0 + 15.0), NLPStats.computeClassicDice(exampleSentenceUniqueTokens, partialMatchUniqueTokens), 0.0);

		String noMatch = "No match in this string.";
		Set<String> noMatchUniqueTokens = new HashSet<String>(Arrays.asList(noMatch.split(" ")));
		assertEquals(0.0, NLPStats.computeClassicDice(exampleSentenceUniqueTokens, noMatchUniqueTokens), 0.0);

	}

}
