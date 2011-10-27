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

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * This class is a data structure for cataloging the results of an NLP experiment. Not only are TP,
 * FP, and FN counts logged, but the annotations that were responsible for each TP, FP, and FN count
 * are also recorded.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class PRFResult {

	private double precision;

	private double recall;

	private double fmeasure;

	private int truePositiveCount;

	private int falsePositiveCount;

	private int falseNegativeCount;

	/* These lists are used to store the annotations responsible for the TP, FP, and FN counts. */
	private List<TextAnnotation> tpAnnotations;

	private List<TextAnnotation> fpAnnotations;

	private List<TextAnnotation> fnAnnotations;

	public enum ResultTypeEnum {
		TP,
		FP,
		FN
	};

	/* A textual description of the NLP experiment cataloged by this PRFResult object */
	private String title;

	/**
	 * Initialize a new PRFResult by inputting counts for true positives, false positives, and false
	 * negatives.
	 * 
	 * @param tp
	 *            a count of true positives
	 * @param fp
	 *            a count of false positives
	 * @param fn
	 *            a count of false negatives
	 */
	public PRFResult(int tp, int fp, int fn) {
		this.truePositiveCount = tp;
		this.falsePositiveCount = fp;
		this.falseNegativeCount = fn;

		// documentID2AnnotationsMap = new HashMap<String, Map<String, List<TextAnnotation>>>();
		tpAnnotations = new ArrayList<TextAnnotation>();
		fpAnnotations = new ArrayList<TextAnnotation>();
		fnAnnotations = new ArrayList<TextAnnotation>();

		computePRFStats();
	}

	/**
	 * Initialize a new PRFResult by inputting counts for true positives, false positives, and false
	 * negatives.
	 * 
	 * @param tp
	 *            a count of true positives
	 * @param fp
	 *            a count of false positives
	 * @param fn
	 *            a count of false negatives
	 * @param title
	 *            a textual description of the NLP experiment under study
	 */
	public PRFResult(int tp, int fp, int fn, String title) {
		this.truePositiveCount = tp;
		this.falsePositiveCount = fp;
		this.falseNegativeCount = fn;
		this.title = title;

		// documentID2AnnotationsMap = new HashMap<String, Map<String, List<TextAnnotation>>>();
		tpAnnotations = new ArrayList<TextAnnotation>();
		fpAnnotations = new ArrayList<TextAnnotation>();
		fnAnnotations = new ArrayList<TextAnnotation>();

		computePRFStats();
	}

	public void trim() {
		tpAnnotations.clear();
		tpAnnotations = null;
		fpAnnotations.clear();
		fpAnnotations = null;
		fnAnnotations.clear();
		fnAnnotations = null;
		System.gc();
	}

	/**
	 * Update the precision, recall, and F-measure statistics for this PRFResult
	 */
	private void computePRFStats() {
		this.precision = NLPStats.computePrecision(truePositiveCount, falsePositiveCount);
		this.recall = NLPStats.computeRecall(truePositiveCount, falseNegativeCount);
		this.fmeasure = NLPStats.computeFMeasure(truePositiveCount, falsePositiveCount, falseNegativeCount);
	}

	public double getFmeasure() {
		return fmeasure;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public int getFalseNegativeCount() {
		return falseNegativeCount;
	}

	public void setFalseNegativeCount(int falseNegativeCount) {
		this.falseNegativeCount = falseNegativeCount;
		computePRFStats();
	}

	public int getFalsePositiveCount() {
		return falsePositiveCount;
	}

	public void setFalsePositiveCount(int falsePositiveCount) {
		this.falsePositiveCount = falsePositiveCount;
		computePRFStats();
	}

	public int getTruePositiveCount() {
		return truePositiveCount;
	}

	public void setTruePositiveCount(int truePositiveCount) {
		this.truePositiveCount = truePositiveCount;
		computePRFStats();
	}

	/**
	 * Output to the given <code>PrintStream</code> the statistics recorded by this PRFResult
	 * 
	 * @param ps
	 *            the <code>PrintStream</code> to use
	 */
	public void printStats(PrintStream ps) {
		ps.println(getStatsString());
	}

	public String getStatsString() {
		StringBuffer sb = new StringBuffer();
		sb.append("#TP:" + truePositiveCount + " #FP:" + falsePositiveCount + " #FN:" + falseNegativeCount + "\n");
		sb.append("----  P: " + this.getPrecision() + "\n");
		sb.append("----  R: " + this.getRecall() + "\n");
		sb.append("----  F: " + this.getFmeasure() + "\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("---------------- " + title + " ----------------\n");
		sb.append(getStatsString());
		sb.append("----------------------------------------------\n");

		return sb.toString();
	}

	/**
	 * Output to the given <code>PrintStream</code> the annotations responsible for the TP, FP, and
	 * FN statistics.
	 * 
	 * @param ps
	 *            the <code>PrintStream</code> to use
	 */
	public void printAnnotations(Writer writer) {
		try {
			for (TextAnnotation ta : tpAnnotations) {
				writer.write("TP -- " + ta.getSingleLineRepresentation() + "\n");
			}
			for (TextAnnotation ta : fpAnnotations) {
				writer.write("FP -- " + ta.getSingleLineRepresentation() + "\n");
			}
			for (TextAnnotation ta : fnAnnotations) {
				writer.write("FN -- " + ta.getSingleLineRepresentation() + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a String containing the annotations responsible for the TP, FP, and FN statistics
	 * 
	 * @return
	 */
	public String tpFpFnAnnotationsToString() {
		StringBuffer outputBuffer = new StringBuffer();
		for (TextAnnotation ta : tpAnnotations) {
			outputBuffer.append("TP -- " + ta.getSingleLineRepresentation() + "\n");
		}
		for (TextAnnotation ta : fpAnnotations) {
			outputBuffer.append("FP -- " + ta.getSingleLineRepresentation() + "\n");
		}
		for (TextAnnotation ta : fnAnnotations) {
			outputBuffer.append("FN -- " + ta.getSingleLineRepresentation() + "\n");
		}

		return outputBuffer.toString();
	}

	/**
	 * Update this <code>PRFResult</code> object by adding the TP, FP, and FN counts from another
	 * <code>PRFResult</code> object. Note, at this time, the annotations do not get passed along,
	 * only the counts. This is for space considerations when evaluating large document collections.
	 * 
	 * @param prf
	 */
	public void add(PRFResult prf) {
		this.truePositiveCount = this.truePositiveCount + prf.getTruePositiveCount();
		this.falsePositiveCount = this.falsePositiveCount + prf.getFalsePositiveCount();
		this.falseNegativeCount = this.falseNegativeCount + prf.getFalseNegativeCount();
		computePRFStats();

	}

	public void addAll(PRFResult prf) {
		this.truePositiveCount = this.truePositiveCount + prf.getTruePositiveCount();
		this.falsePositiveCount = this.falsePositiveCount + prf.getFalsePositiveCount();
		this.falseNegativeCount = this.falseNegativeCount + prf.getFalseNegativeCount();
		computePRFStats();

		this.tpAnnotations.addAll(prf.getTPAnnotations());
		this.fpAnnotations.addAll(prf.getFPAnnotations());
		this.fnAnnotations.addAll(prf.getFNAnnotations());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Retrieve the annotations responsible for the false negative counts
	 * 
	 * @return a list of <code>TextAnnotation</code> objects associated with the false negative
	 *         counts
	 */
	public List<TextAnnotation> getFNAnnotations() {
		return fnAnnotations;
	}

	/**
	 * Add a false negative <code>TextAnnotation</code>
	 * 
	 * @param fnAnnotation
	 */
	public void addFNAnnotation(TextAnnotation fnAnnotation) {
		fnAnnotations.add(fnAnnotation);
	}

	/**
	 * Set the false negative <code>TextAnnotation</code> list
	 * 
	 * @param fnAnnotations
	 */
	public void setFNAnnotations(List<TextAnnotation> fnAnnotations) {
		this.fnAnnotations = fnAnnotations;
	}

	/**
	 * Retrieve the annotations responsible for the false positive counts
	 * 
	 * @return a list of <code>TextAnnotation</code> objects associated with the false positive
	 *         counts
	 */
	public List<TextAnnotation> getFPAnnotations() {
		return fpAnnotations;
	}

	/**
	 * Add a false positive <code>TextAnnotation</code>
	 * 
	 * @param fpAnnotation
	 */
	public void addFPAnnotation(TextAnnotation fpAnnotation) {
		fpAnnotations.add(fpAnnotation);
	}

	/**
	 * Set the false positive <code>TextAnnotation</code> list
	 * 
	 * @param fpAnnotations
	 */
	public void setFPAnnotations(List<TextAnnotation> fpAnnotations) {
		this.fpAnnotations = fpAnnotations;
	}

	/**
	 * Retrieve the annotations responsible for the true positive counts
	 * 
	 * @return a list of <code>TextAnnotation</code> objects associated with the true positive
	 *         counts
	 */
	public List<TextAnnotation> getTPAnnotations() {
		return tpAnnotations;
	}

	public List<TextAnnotation> getResultTypeAnnotations(ResultTypeEnum r) {
		switch (r) {
		case FN:
			return getFNAnnotations();
		case TP:
			return getTPAnnotations();
		case FP:
			return getFPAnnotations();
		}

		// too bad the compiler can't see I've covered all bases
		return new ArrayList<TextAnnotation>();

	}

	/**
	 * Add a true positive <code>TextAnnotation</code>
	 * 
	 * @param tpAnnotation
	 */
	public void addTPAnnotation(TextAnnotation tpAnnotation) {
		tpAnnotations.add(tpAnnotation);
	}

	/**
	 * Set the true positive <code>TextAnnotation</code> list
	 * 
	 * @param tpAnnotations
	 */
	public void setTPAnnotations(List<TextAnnotation> tpAnnotations) {
		this.tpAnnotations = tpAnnotations;
	}

}
