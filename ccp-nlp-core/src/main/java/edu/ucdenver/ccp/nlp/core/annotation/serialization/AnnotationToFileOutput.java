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

package edu.ucdenver.ccp.nlp.core.annotation.serialization;

import java.io.PrintStream;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;

/**
 * This Annotation Printer outputs annotation in a format that can be loaded by the AnnotationFromFileLoader. Currently,
 * this will only work for annotations with non-complex slots. <br>
 * <br>
 * The output consists of a single annotation per line taking the format:<br>
 * <br>
 * 
 * <pre>
 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText|slotname1|slot1value1,slot1Value2|slotname2|slot2value1
 * </pre>
 * 
 * @author William A Baumgartner, Jr.
 */
public class AnnotationToFileOutput extends AnnotationPrinterUtil {

	@Override
	public void printTextAnnotation(TextAnnotation ta, PrintStream ps) {
		ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
	}

//	public void printDocumentCollection(List<GenericDocument> documents, PrintStream ps) {
//		for (GenericDocument gd : documents) {
//			printDocument(gd, ps);
//		}
//	}
//
//	public void printDocumentCollection(List<GenericDocument> documents) {
//		printDocumentCollection(documents, System.out);
//	}
//
//	public void printDocument(GenericDocument gd) {
//		printDocument(gd, System.out);
//	}
//
//	public void printDocument(GenericDocument gd, PrintStream ps) {
//		List<TextAnnotation> annotations = gd.getAnnotations();
//
//		for (TextAnnotation ta : annotations) {
//			// printAnnotation(ta, ps);
//			ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
//
//		}
//	}
//
//	@Override
//	public void printAnnotations(Collection<TextAnnotation> textAnnotations, PrintStream ps) {
//		List<TextAnnotation> taList = new ArrayList<TextAnnotation>(textAnnotations);
//		Collections.sort(taList, TextAnnotation.BY_SPAN());
//		for (TextAnnotation ta : taList) {
//			ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
//		}
//	}

	// public void printAnnotation(TextAnnotation ta, PrintStream ps) {
	// StringBuffer outputStr = new StringBuffer();
	//
	// ClassMention cm = ta.getClassMention();
	// Annotator annotator = ta.getAnnotator();
	// outputStr.append(ta.getDocumentID() + "|" + annotator.getAnnotatorID() + "|");
	//
	// List<Span> spanList = ta.getSpans();
	// if (spanList.size() > 1) {
	// int earliestSpanStart = Integer.MAX_VALUE;
	// int latestSpanEnd = -1;
	// for (Span span : spanList) {
	// if (span.getSpanStart() < earliestSpanStart) {
	// earliestSpanStart = span.getSpanStart();
	// }
	// if (span.getSpanEnd() > latestSpanEnd) {
	// latestSpanEnd = span.getSpanEnd();
	// }
	// }
	// outputStr.append(earliestSpanStart + " " + latestSpanEnd + "|");
	// } else {
	// outputStr.append(ta.getAnnotationSpanStart() + " " + ta.getAnnotationSpanEnd() + "|");
	// }
	//
	// String coveredText = ta.getCoveredText().replaceAll("\\n", " ");
	// if (coveredText.length() > 100) {
	// coveredText = coveredText.substring(0,99) + "...";
	// }
	// outputStr.append(cm.getMentionName() + "|" + coveredText);
	//
	// String slotStr = getSlotString(cm);
	//
	// outputStr.append(slotStr);
	//
	// ps.println(outputStr.toString());
	// }
	//
	// /**
	// * Returns a String representation of the slot mention fillers
	// *
	// * @param cm
	// */
	// private String getSlotString(ClassMention cm) {
	// StringBuffer sb = new StringBuffer();
	// for (SlotMention sm : cm.getSlotMentions()) {
	// if (sm.getSlotValues().size() > 0) {
	// sb.append("|" + sm.getMentionName() + "|");
	// }
	// for (Object slotFiller : sm.getSlotValues()) {
	// sb.append(slotFiller.toString() + ",");
	// }
	// sb.deleteCharAt(sb.length() - 1);
	// }
	// return sb.toString();
	// }

}
