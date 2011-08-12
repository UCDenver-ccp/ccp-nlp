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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements some commonly used statistics in the field of NLP, namely precision, recall, F-measure, and the
 * Dice coefficient.
 * <p>
 * For a give count of true positives (TP), false positives (FP), and false negatives (FN), precision (P), recall (R),
 * and F-measure (F) are computed as:
 * <p>
 * P = TP/(TP+FP)
 * <p>
 * R = TP/(TP+FN)
 * <p>
 * F = 2PR/(P+R)
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class NLPStats {

	/**
	 * Given counts of TPs and FPs, compute the precision
	 * 
	 * @param tp
	 *            the number of true positives
	 * @param fp
	 *            the number of false positives
	 * @return TP/(TP+FP)
	 */
	public static double computePrecision(int tp, int fp) {
		return ((double) tp) / ((double) (tp + fp));
	}

	/**
	 * Given counts of TPs and FNs, compute the recall
	 * 
	 * @param tp
	 *            the number of true positives
	 * @param fn
	 *            the number of false positives
	 * @return TP/(TP+FN)
	 */
	public static double computeRecall(int tp, int fn) {
		return ((double) tp) / ((double) (tp + fn));
	}

	/**
	 * Given precision and recall, compute the F-measure
	 * 
	 * @param precision
	 * @param recall
	 * @return 2PR/(P+R)
	 */
	public static double computeFMeasure(double precision, double recall) {
		return 2.0 * precision * recall / (precision + recall);
	}

	/**
	 * Given counts of TPs, FPs, and FNs, compute the F-measure
	 * 
	 * @param tp
	 * @param fp
	 * @param fn
	 * @return 2PR/(P+R)
	 */
	public static double computeFMeasure(int tp, int fp, int fn) {
		return 2.0 * computePrecision(tp, fp) * computeRecall(tp, fn) / (computePrecision(tp, fp) + computeRecall(tp, fn));
	}

	/**
	 * Computes the Dice Coefficient as described in Hersh et al. TREC Genomics Track Overview, 2003.
	 * <p>
	 * For two <code>Strings</code> A and B, define X as the number of words in A, Y as the number of words in B, and Z
	 * as the number of words occurring in both A and B. The Dice coefficient is calculated as Dice(A,B) = (2*Z)/(X+Y)
	 * 
	 * @param aTokens
	 *            a <code>Set</code> of tokens from <code>String</code> A
	 * @param bTokens
	 *            a <code>Set</code> of tokens from <code>String</code> B
	 * @return the Dice Coefficient for the two sets of tokens
	 */
	public static double computeClassicDice(Set<String> aTokens, Set<String> bTokens) {
		int numWordsInA = aTokens.size();
		int numWordsInB = bTokens.size();
		aTokens.retainAll(bTokens);
		int numWordsThatCoOccur = aTokens.size();

		double diceCoefficient = ((double) (2 * numWordsThatCoOccur)) / ((double) (numWordsInA + numWordsInB));
		return diceCoefficient;
	}

	/**
	 * Computes the Dice Coefficient as described in Hersh et al. TREC Genomics Track Overview, 2003.
	 * <p>
	 * For two <code>Strings</code> A and B, define X as the number of words in A, Y as the number of words in B, and Z
	 * as the number of words occurring in both A and B. The Dice coefficient is calculated as Dice(A,B) = (2*Z)/(X+Y)
	 * <p>
	 * This method tokenizes by splitting the input Strings at spaces.
	 * 
	 * @param aString
	 *            a <code>String</code> to compare
	 * @param bString
	 *            another <code>String</code> to compare
	 * @return the Dice Coefficient for the two input Strings
	 */
	public static double computeClassicDice(String aString, String bString) {
		Set<String> aTokens = new HashSet<String>(Arrays.asList(aString.split(" ")));
		Set<String> bTokens = new HashSet<String>(Arrays.asList(bString.split(" ")));
		return computeClassicDice(aTokens, bTokens);

	}

}
