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
package edu.ucdenver.ccp.nlp.core.annotation.serialization;

import java.io.PrintStream;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;

/**
 * This Annotation Printer outputs annotation in a format that can be loaded by the
 * AnnotationFromFileLoader. Currently, this will only work for annotations with non-complex slots. <br>
 * <br>
 * The output consists of a single annotation per line taking the format:<br>
 * <br>
 * 
 * <pre>
 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText|slotname1|slot1value1,slot1Value2|slotname2|slot2value1
 * </pre>
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */
public class AnnotationToFileOutput extends AnnotationPrinterUtil {

	@Override
	public void printTextAnnotation(TextAnnotation ta, PrintStream ps) {
		ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
	}

	// public void printDocumentCollection(List<GenericDocument> documents, PrintStream ps) {
	// for (GenericDocument gd : documents) {
	// printDocument(gd, ps);
	// }
	// }
	//
	// public void printDocumentCollection(List<GenericDocument> documents) {
	// printDocumentCollection(documents, System.out);
	// }
	//
	// public void printDocument(GenericDocument gd) {
	// printDocument(gd, System.out);
	// }
	//
	// public void printDocument(GenericDocument gd, PrintStream ps) {
	// List<TextAnnotation> annotations = gd.getAnnotations();
	//
	// for (TextAnnotation ta : annotations) {
	// // printAnnotation(ta, ps);
	// ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
	//
	// }
	// }
	//
	// @Override
	// public void printAnnotations(Collection<TextAnnotation> textAnnotations, PrintStream ps) {
	// List<TextAnnotation> taList = new ArrayList<TextAnnotation>(textAnnotations);
	// Collections.sort(taList, TextAnnotation.BY_SPAN());
	// for (TextAnnotation ta : taList) {
	// ps.println(TextAnnotationUtil.printAnnotationToLine(ta));
	// }
	// }

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
