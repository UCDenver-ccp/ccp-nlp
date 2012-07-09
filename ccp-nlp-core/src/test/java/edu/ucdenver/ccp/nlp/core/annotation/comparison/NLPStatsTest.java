/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
