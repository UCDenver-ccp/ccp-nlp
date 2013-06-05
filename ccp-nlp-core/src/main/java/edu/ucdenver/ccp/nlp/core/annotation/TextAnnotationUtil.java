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
package edu.ucdenver.ccp.nlp.core.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultPrimitiveSlotMentionFactory;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */
public class TextAnnotationUtil {
	private static Logger logger = Logger.getLogger(TextAnnotationUtil.class);

	/**
	 * Returns a collection of redundant text annotations from the input set of annotations. This
	 * method can be used when filtering out duplicate annotations.
	 * 
	 * @param inputAnnotations
	 * @return
	 */
	public static Collection<TextAnnotation> getRedundantAnnotations(Collection<TextAnnotation> inputAnnotations) {
		Collection<TextAnnotation> redundantAnnotations = new ArrayList<TextAnnotation>();
		Set<TextAnnotation> nonRedundantAnnotations = new HashSet<TextAnnotation>();
		for (TextAnnotation ta : inputAnnotations) {
			if (!nonRedundantAnnotations.contains(ta)) {
				System.err.println("Found NON redundant TA: " + ta.getSingleLineRepresentation());
				nonRedundantAnnotations.add(ta);
			} else {
				System.err.println("Found REDUNDANT TA: " + ta.getSingleLineRepresentation());
				redundantAnnotations.add(ta);
			}
		}
		return redundantAnnotations;
	}

	/**
	 * This method loads annotations from a file. The file may have been created by
	 * <code>AnnotationToFileOutput</code>. The input format for the file is one annotation per
	 * line: <br>
	 * <br>
	 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText
	 * 
	 * @param annotationFile
	 * @return
	 */
	public static Map<String, List<TextAnnotation>> loadAnnotationsFromFile(File annotationFile,
			CharacterEncoding encoding) throws IOException {
		Map<String, List<TextAnnotation>> documentID2AnnotationsMap = new HashMap<String, List<TextAnnotation>>();

		BufferedReader br = FileReaderUtil.initBufferedReader(annotationFile, encoding);
		String line;
		while ((line = br.readLine()) != null) {
			/* parse the line */

			TextAnnotation ta = null;
			try {
				ta = getAnnotationFromLine(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			 * if the documentID is already in the Map, then add this textAnnotation to the List
			 * that is already stored, otherwise create a new list and add it to the Map
			 */

			if (documentID2AnnotationsMap.containsKey(ta.getDocumentID())) {
				documentID2AnnotationsMap.get(ta.getDocumentID()).add(ta);
			} else {
				List<TextAnnotation> taList = new ArrayList<TextAnnotation>();
				taList.add(ta);
				documentID2AnnotationsMap.put(ta.getDocumentID(), taList);
			}

		}
		return documentID2AnnotationsMap;
	}

	private static TextAnnotation getAnnotationFromLine(String line) {
		String[] toks = line.split("\\|");
		if (toks.length > 4) {
			String documentID = toks[0];
			int annotatorID = Integer.parseInt(toks[1]);
			String annotationType = toks[3];

			String[] spanToks = toks[2].split(" ");
			int spanStart = Integer.parseInt(spanToks[0]);
			int spanEnd = Integer.parseInt(spanToks[1]);
			String coveredText = toks[4];

			/* create a new text annotation */
			TextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd);
			ta.setDocumentID(documentID);
			// ta.setAnnotationSpanEnd(spanEnd);
			// ta.setAnnotationSpanStart(spanStart);
			ta.setCoveredText(coveredText);

			DefaultClassMention cm = new DefaultClassMention(annotationType);
			ta.setClassMention(cm);

			Annotator annotator = new Annotator(annotatorID, "", "", "");
			ta.setAnnotator(annotator);

			AnnotationSet tntAnnotationSet = new AnnotationSet(new Integer(-1), "Default", "Default");
			ta.addAnnotationSet(tntAnnotationSet);

			if (toks.length > 5) {
				/* get the slot mention values */
				for (int i = 5; i < toks.length; i += 2) {
					String slotName = toks[i];
					String[] slotValues = toks[i + 1].split(",");
					PrimitiveSlotMention sm = DefaultPrimitiveSlotMentionFactory
							.createPrimitiveSlotMentionFromStringValue(slotName, slotValues[0]);
					// SlotMention sm = new SlotMention(slotName);
					for (int k = 1; k < slotValues.length; k++) {
						// for (String slotValue : slotValues) {
						System.err.println("solValues[k]: " + slotValues[k] + " sm class: " + sm.getClass().getName());
						sm.addSlotValueAsString(slotValues[k]);
					}
					cm.addPrimitiveSlotMention(sm);
				}
			}

			return ta;
		} else {
			logger.error("Expected at least 5 items on line in annotation file, but there were only: " + toks.length
					+ "  LINE=" + line);
		}
		return null;
	}

	public static String printAnnotationToLine(TextAnnotation ta) {
		StringBuffer outputStr = new StringBuffer();

		ClassMention cm = ta.getClassMention();
		Annotator annotator = ta.getAnnotator();
		outputStr.append(ta.getDocumentID() + "|" + annotator.getAnnotatorID() + "|");

		List<Span> spanList = ta.getSpans();
		if (spanList.size() > 1) {
			int earliestSpanStart = Integer.MAX_VALUE;
			int latestSpanEnd = -1;
			for (Span span : spanList) {
				if (span.getSpanStart() < earliestSpanStart) {
					earliestSpanStart = span.getSpanStart();
				}
				if (span.getSpanEnd() > latestSpanEnd) {
					latestSpanEnd = span.getSpanEnd();
				}
			}
			outputStr.append(earliestSpanStart + " " + latestSpanEnd + "|");
		} else {
			outputStr.append(ta.getAnnotationSpanStart() + " " + ta.getAnnotationSpanEnd() + "|");
		}

		String coveredText = ta.getCoveredText().replaceAll("\\n", " ");
		if (coveredText.length() > 100) {
			coveredText = coveredText.substring(0, 99) + "...";
		}
		if (coveredText.contains("|")) {
			logger.warn("Pipe '|' replaced with [PIPE] in covered text when storing annotation: "
					+ outputStr.toString() + cm.getMentionName() + "|" + coveredText
					+ "\nThis will likely have no downstream affect.");
			coveredText = coveredText.replaceAll("\\|", "[PIPE]");
		}
		outputStr.append(cm.getMentionName() + "|" + coveredText);

		String slotStr = getSlotString(cm);

		outputStr.append(slotStr);

		return outputStr.toString();
	}

	/**
	 * Returns a String representation of the slot mention fillers
	 * 
	 * @param cm
	 */
	private static String getSlotString(ClassMention cm) {
		StringBuffer sb = new StringBuffer();
		try {
			for (PrimitiveSlotMention sm : cm.getPrimitiveSlotMentions()) {
				if (sm.getSlotValues().size() > 0) {
					String mentionName = checkForPipe(sm.getMentionName(), "slot mention name",
							"This is likely to cause downstream processing errors. Please address.");
					sb.append("|" + mentionName + "|");

					for (Object slotFiller : sm.getSlotValues()) {
						String slotFillerStr = checkForPipe(slotFiller.toString(), "slot value",
								"This could cause downstream processing errors. You may want to investigate.");
						sb.append(slotFillerStr + ",");
					}
					sb.deleteCharAt(sb.length() - 1);
				}
			}
		} catch (KnowledgeRepresentationWrapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static String checkForPipe(String inputStr, String field, String message) {
		if (inputStr.contains("|")) {
			logger.warn("Pipe '|' replaced with [PIPE] in " + field + " when storing: " + inputStr + "\n" + message);
			inputStr = inputStr.replaceAll("\\|", "[PIPE]");
		}
		return inputStr;
	}

	/**
	 * Removes duplicate annotations (based on identical span and slot fillers). All meta-data is
	 * ignored, e.g. annotator, annotation set, etc except for the document ID and
	 * documentCollectionID. See TextAnnotation.equals() for details.
	 * 
	 * @param textAnnotations
	 * @return
	 */
	public static Collection<TextAnnotation> removeDuplicateAnnotations(Collection<TextAnnotation> textAnnotations) {
		Set<TextAnnotation> annotationsToKeep = new HashSet<TextAnnotation>();
		for (TextAnnotation ta : textAnnotations) {
			if (!annotationsToKeep.contains(ta)) {
				annotationsToKeep.add(ta);
			} else {
				logger.info("Duplicate Annotation Removed: " + ta.getSingleLineRepresentation());
			}
		}
		return annotationsToKeep;
	}

	/**
	 * Adds a slot value to a text annotation. If the slot is not present, it is created.
	 * 
	 * @param ta
	 * @param slotName
	 * @param slotValue
	 * @throws Exception
	 */

	public static <E extends Object> void addSlotValue(TextAnnotation ta, String slotName, ClassMention slotValue) {
		ComplexSlotMention csm = ta.getClassMention().getComplexSlotMentionByName(slotName);
		if (csm == null) {
			csm = new DefaultComplexSlotMention(slotName);
			ta.getClassMention().addComplexSlotMention(csm);
		}
		csm.addClassMention((DefaultClassMention) slotValue);
	}

	public static void addSlotValue(TextAnnotation ta, String slotName, String slotValue) {
		PrimitiveSlotMention sm = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		if (sm == null) {
			sm = ta.getClassMention().createPrimitiveSlotMention(slotName, slotValue);
			ta.getClassMention().addPrimitiveSlotMention(sm);
		} else {
			sm.addSlotValue(slotValue);
		}
	}

	public static void addSlotValue(TextAnnotation ta, String slotName, Integer slotValue) {
		PrimitiveSlotMention sm = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		if (sm == null) {
			sm = ta.getClassMention().createPrimitiveSlotMention(slotName, slotValue);
			ta.getClassMention().addPrimitiveSlotMention(sm);
		} else {
			sm.addSlotValue(slotValue);
		}
	}

	public static void addSlotValue(TextAnnotation ta, String slotName, Float slotValue) {
		PrimitiveSlotMention sm = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		if (sm == null) {
			sm = ta.getClassMention().createPrimitiveSlotMention(slotName, slotValue);
			ta.getClassMention().addPrimitiveSlotMention(sm);
		} else {
			sm.addSlotValue(slotValue);
		}
	}

	public static void addSlotValue(TextAnnotation ta, String slotName, Boolean slotValue) {
		PrimitiveSlotMention sm = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		if (sm == null) {
			sm = ta.getClassMention().createPrimitiveSlotMention(slotName, slotValue);
			ta.getClassMention().addPrimitiveSlotMention(sm);
		} else {
			sm.addSlotValue(slotValue);
		}
	}

	/*****
	public static <E extends Object> void addSlotValue(TextAnnotation ta, String slotName, E slotValue) {
		if (slotValue instanceof DefaultClassMention) {
			ComplexSlotMention csm = ta.getClassMention().getComplexSlotMentionByName(slotName);
			if (csm == null) {
				csm = new DefaultComplexSlotMention(slotName);
				ta.getClassMention().addComplexSlotMention(csm);
			}
			csm.addClassMention((DefaultClassMention) slotValue);
		} else {
			PrimitiveSlotMention<E> sm = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
			if (sm == null) {
				sm = ta.getClassMention().createPrimitiveSlotMention(slotName, slotValue);
				ta.getClassMention().addPrimitiveSlotMention(sm);
			} else {
				sm.addSlotValue(slotValue);
			}
		}
	}
	*****/

	/**
	 * This utility swaps annotation information from one <code>TextAnnotation</code> to another.
	 * The fields that are transferred are:
	 * <ul>
	 * <li>annotation ID</li>
	 * <li>annotator</li>
	 * <li>annotation sets</li>
	 * <li>covered text</li>
	 * <li>document collection ID</li>
	 * <li>document ID</li>
	 * <li>document section ID</li>
	 * <li>spans</li>
	 * </ul>
	 * <p>
	 * Note: class information does not get transferred.
	 * 
	 * @param fromTA
	 *            the text annotation from which to transfer annotation information
	 * @param toTA
	 *            the text annotation to which to transfer annotation information
	 */
	public static void swapAnnotationInfo(TextAnnotation fromTA, TextAnnotation toTA) {
		toTA.setAnnotationID(fromTA.getAnnotationID());

		toTA.setAnnotationSets(fromTA.getAnnotationSets());
		toTA.setAnnotator(fromTA.getAnnotator());
		toTA.setCoveredText(fromTA.getCoveredText());
		toTA.setDocumentCollectionID(fromTA.getDocumentCollectionID());
		toTA.setDocumentID(fromTA.getDocumentID());
		toTA.setDocumentSectionID(fromTA.getDocumentSectionID());
		List<Span> spanList = new ArrayList<Span>();
		for (Span span : fromTA.getSpans()) {
			spanList.add(span.clone());
		}
		toTA.setSpans(spanList);

	}

	public static LinkedList<TextAnnotation> getTokens(Collection<TextAnnotation> annotations) {
		LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
		for (TextAnnotation a : annotations) {
			// System.out.println(a.getClassMention().getMentionName());
			if (isTokenAnnotation(a)) {
				newAnnotations.add(a);
			}
		}
		return newAnnotations;
	}

	public static boolean isTokenAnnotation(TextAnnotation a) {
		// System.out.println(a.getClassMention().getMentionName());
		return a.getClassMention().getMentionName().equals("token");
	}

}
