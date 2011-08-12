/*
 * Annotation_Util.java
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

package edu.ucdenver.ccp.nlp.core.annotation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * This class contains some utility methods for dealing with text annotations.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class Annotation_Util {
	private static Logger logger = Logger.getLogger(Annotation_Util.class);
	public final static String UNKNOWN_TAGSET = "Unknown";

	/**
	 * Given a part of speech label, stem, lemma, and token number, create a <code>ClassMention</code> that represents a
	 * token.
	 * 
	 * @param posLabel
	 *            part of speech label
	 * @param stem
	 *            the stem of the token
	 * @param lemma
	 *            the lemma of the token
	 * @param tokenNumber
	 *            the token number of the token
	 * @return a <code>ClassMention</code> representing a token
	 * @throws Exception 
	 */
	public static ClassMention createTokenMention(String posLabel, String posTagSet, String stem, String lemma, Integer tokenNumber) throws Exception {
		ClassMention cm = new DefaultClassMention(ClassMentionTypes.TOKEN);

		if (posLabel != null) {
			/* create POS slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionTypes.TOKEN_PARTOFSPEECH);
			sm.addSlotValue(posLabel);

			if (posTagSet == null) {
				posTagSet = UNKNOWN_TAGSET;
			}
			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionTypes.TAGSET);
			tagSetSlot.addSlotValue(posTagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}

		StringSlotMention sm;
		if (stem != null) {
			/* create stem slot */
			sm = new DefaultStringSlotMention(SlotMentionTypes.TOKEN_STEM);
			sm.addSlotValue(stem);
			cm.addPrimitiveSlotMention(sm);
		}
		if (lemma != null) {
			/* create lemma slot */
			sm = new DefaultStringSlotMention(SlotMentionTypes.TOKEN_LEMMA);
			sm.addSlotValue(lemma);
			cm.addPrimitiveSlotMention(sm);
		}
		if (tokenNumber != null) {
			/* create tokenNumber slot */
			IntegerSlotMention ism = new DefaultIntegerSlotMention(SlotMentionTypes.TOKEN_NUMBER);
			ism.addSlotValue(tokenNumber);
			cm.addPrimitiveSlotMention(ism);
		}
		return cm;
	}

	public static ClassMention createPhraseMention(String phraseTypeLabel, String tagSet) throws Exception {
		ClassMention cm = new DefaultClassMention(ClassMentionTypes.PHRASE);

		if (phraseTypeLabel != null) {
			/* create phraseType slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionTypes.PHRASE_TYPE);
			sm.addSlotValue(phraseTypeLabel);
			if (tagSet == null) {
				tagSet = UNKNOWN_TAGSET;
			}

			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionTypes.TAGSET);
			tagSetSlot.addSlotValue(tagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}
		return cm;
	}

	public static ClassMention createClauseMention(String clauseTypeLabel, String tagSet) throws Exception {
		ClassMention cm = new DefaultClassMention(ClassMentionTypes.CLAUSE);

		if (clauseTypeLabel != null) {
			/* create phraseType slot */
			StringSlotMention sm = new DefaultStringSlotMention(SlotMentionTypes.CLAUSE_TYPE);
			sm.addSlotValue(clauseTypeLabel);
			if (tagSet == null) {
				tagSet = UNKNOWN_TAGSET;
			}
			StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionTypes.TAGSET);
			tagSetSlot.addSlotValue(tagSet);
			cm.addPrimitiveSlotMention(sm);
			cm.addPrimitiveSlotMention(tagSetSlot);
		}
		return cm;
	}

	/**
	 * This utility swaps annotation information from one <code>TextAnnotation</code> to another. The fields that are
	 * transferred are:
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
		for (Span span : fromTA.getSpans()) {
			toTA.addSpan(span.clone());
		}

	}

	/**
	 * For a give <code>TextAnnotation</code> return a list of the slot values for a given slot. The slot is specified
	 * by using the slot name. This name must reference a <code>SlotMention</code> (and not a
	 * <code>ComplexSlotMention</code>).
	 * 
	 * @param ta
	 *            the <code>TextAnnotation</code> that contains the slot
	 * @param slotName
	 *            the name of the <code>SlotMention</code>, whose values will be retrieved.
	 * @return a list of objects stored in the slot. Often these objects are <code>Strings</code>.
	 */
	public static Collection<?> getSlotValuesByName(TextAnnotation ta, String slotName) {
		PrimitiveSlotMention<?> slotMention = ta.getClassMention().getPrimitiveSlotMentionByName(slotName);
		return slotMention.getSlotValues();
	}

//	/**
//	 * Adds a slot value to a text annotation. If the slot is not present, it is created.
//	 * 
//	 * @param ta
//	 * @param slotName
//	 * @param slotValue
//	 */
//	public static void addSlotValue(TextAnnotation ta, String slotName, String slotValue) {		
//		List<SlotMention> slotMentions = ta.getClassMention().getSlotMentionsByName(slotName);
//		
//		if (slotMentions != null && slotMentions.size() > 0) {
//			if (slotMentions.size() > 1) {
//				SlotMention sm = new SlotMention(slotName);
//				sm.addSlotValue(slotValue);
//				ta.getClassMention().addSlotMention(sm);
//			} else {
//				SlotMention sm = slotMentions.get(0);
//				sm.addSlotValue(slotValue);
//			}
//		} else {
//			SlotMention sm = new SlotMention(slotName);
//			sm.addSlotValue(slotValue);
//			ta.getClassMention().addSlotMention(sm);
//		}
//
//	}

//	/**
//	 * This method loads annotations from a file. The file may have been created by <code>AnnotationToFileOutput</code>.
//	 * The input format for the file is one annotation per line: <br>
//	 * <br>
//	 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText
//	 * 
//	 * @param annotationFile
//	 * @return
//	 */
//	public static Map<String, List<TextAnnotation>> loadAnnotationsFromFile(String annotationFile) throws IOException {
//		Map<String, List<TextAnnotation>> documentID2AnnotationsMap = new HashMap<String, List<TextAnnotation>>();
//
//		BufferedReader br = GenericTextUtils.openReader(annotationFile);
//		String line;
//		while ((line = br.readLine()) != null) {
//			/* parse the line */
//
//			TextAnnotation ta = getAnnotationFromLine(line);
//			/*
//			 * if the documentID is already in the Map, then add this textAnnotation to the List that is already stored,
//			 * otherwise create a new list and add it to the Map
//			 */
//
//			if (documentID2AnnotationsMap.containsKey(ta.getDocumentID())) {
//				documentID2AnnotationsMap.get(ta.getDocumentID()).add(ta);
//			} else {
//				List<TextAnnotation> taList = new ArrayList<TextAnnotation>();
//				taList.add(ta);
//				documentID2AnnotationsMap.put(ta.getDocumentID(), taList);
//			}
//
//		}
//		return documentID2AnnotationsMap;
//	}
//
//	private static TextAnnotation getAnnotationFromLine(String line) {
//		String[] toks = line.split("\\|");
//		if (toks.length > 4) {
//			String documentID = toks[0];
//			int annotatorID = Integer.parseInt(toks[1]);
//			String annotationType = toks[3];
//
//			String[] spanToks = toks[2].split(" ");
//			int spanStart = Integer.parseInt(spanToks[0]);
//			int spanEnd = Integer.parseInt(spanToks[1]);
//			String coveredText = toks[4];
//
//			/* create a new text annotation */
//			TextAnnotation ta = new TextAnnotation();
//			ta.setDocumentID(documentID);
//			ta.setAnnotationSpanEnd(spanEnd);
//			ta.setAnnotationSpanStart(spanStart);
//			ta.setCoveredText(coveredText);
//
//			ClassMention cm = new DefaultClassMention(annotationType);
//			ta.setClassMention(cm);
//
//			Annotator annotator = new Annotator(annotatorID, "", "", "");
//			ta.setAnnotator(annotator);
//
//			AnnotationSet tntAnnotationSet = new AnnotationSet(new Integer(-1), "Default", "Default");
//			ta.addAnnotationSet(tntAnnotationSet);
//			
//			try {
//			if (toks.length > 5) {
//				/* get the slot mention values */
//				for (int i = 5; i< toks.length; i+=2) {
//					String slotName = toks[i];
//					String[] slotValues = toks[i+1].split(",");
//					SlotMention sm = new SlotMention(slotName);
//					for (String slotValue : slotValues) {
//						sm.addSlotValue(slotValue);
//					}
//					cm.addSlotMention(sm);
//				}
//			}
//			} catch (Exception e) {
//				logger.error("\n" + e.getClass().getName() + "("+e.getLocalizedMessage() + ")\n" + e.getStackTrace()[0].toString()
//						+"\nWhile extracting slots from annotation file line: " + line 
//						+"\nSome or all slot values were not added to this annotation. " +
//								"The most likely reason is that a pipe '|' was found as part of the document text or slot name.");
//			
//			}
//			
//			return ta;
//		} else {
//			error("Expected at least 5 items on line in annotation file, but there were only: " + toks.length + "  LINE=" + line);
//		}
//		return null;
//	}

	
	
	
//	public static String printAnnotationToLine(TextAnnotation ta) {
//		StringBuffer outputStr = new StringBuffer();
//
//		ClassMention cm = ta.getClassMention();
//		Annotator annotator = ta.getAnnotator();
//		outputStr.append(ta.getDocumentID() + "|" + annotator.getAnnotatorID() + "|");
//
//		List<Span> spanList = ta.getSpans();
//		if (spanList.size() > 1) {
//			int earliestSpanStart = Integer.MAX_VALUE;
//			int latestSpanEnd = -1;
//			for (Span span : spanList) {
//				if (span.getSpanStart() < earliestSpanStart) {
//					earliestSpanStart = span.getSpanStart();
//				}
//				if (span.getSpanEnd() > latestSpanEnd) {
//					latestSpanEnd = span.getSpanEnd();
//				}
//			}
//			outputStr.append(earliestSpanStart + " " + latestSpanEnd + "|");
//		} else {
//			outputStr.append(ta.getAnnotationSpanStart() + " " + ta.getAnnotationSpanEnd() + "|");
//		}
//
//		String coveredText = ta.getCoveredText().replaceAll("\\n", " ");
//		if (coveredText.length() > 100) {
//			coveredText = coveredText.substring(0,99) + "...";
//		}
//		if (coveredText.contains("|")) {
//			logger.warn("Pipe '|' replaced with [PIPE] in covered text when storing annotation: " + outputStr.toString() + cm.getMentionName() + "|" + coveredText +"\nThis will likely have no downstream affect.");
//			coveredText = coveredText.replaceAll("\\|", "[PIPE]");
//		}
//		outputStr.append(cm.getMentionName() + "|" + coveredText);
//
//		String slotStr = getSlotString(cm);
//
//		outputStr.append(slotStr);
//
//		return outputStr.toString();
//	}
//
//	/**
//	 * Returns a String representation of the slot mention fillers
//	 * 
//	 * @param cm
//	 */
//	private static String getSlotString(ClassMention cm) {
//		StringBuffer sb = new StringBuffer();
//		for (SlotMention sm : cm.getSlotMentions()) {
//			if (sm.getSlotValues().size() > 0) {
//				String mentionName = checkForPipe(sm.getMentionName(), "slot mention name","This is likely to cause downstream processing errors. Please address."); 
//				sb.append("|" + mentionName + "|");
//			
//			for (Object slotFiller : sm.getSlotValues()) {
//				String slotFillerStr = checkForPipe(slotFiller.toString(), "slot value", "This could cause downstream processing errors. You may want to investigate.");
//				sb.append(slotFillerStr + ",");
//			}
//			sb.deleteCharAt(sb.length() - 1);
//			}
//		}
//		return sb.toString();
//	}
//	
//	
//	private static String checkForPipe(String inputStr, String field, String message) {
//		if (inputStr.contains("|")) {
//			logger.warn("Pipe '|' replaced with [PIPE] in "+field+" when storing: " + inputStr +"\n" + message);
//			inputStr = inputStr.replaceAll("\\|", "[PIPE]");
//		}
//		return inputStr;
//	}
	
//	/**
//	 * Removes duplicate annotations (based on identical span and slot fillers). All meta-data is ignored, e.g.
//	 * annotator, annotation set, etc except for the document ID and documentCollectionID. See TextAnnotation.equals()
//	 * for details.
//	 * 
//	 * @param textAnnotationList
//	 * @return
//	 */
//	public static List<TextAnnotation> removeDuplicateAnnotations(List<TextAnnotation> textAnnotationList) {
//		Set<TextAnnotation> annotationsToKeep = new HashSet<TextAnnotation>();
//
//		for (TextAnnotation ta : textAnnotationList) {
//			if (!annotationsToKeep.contains(ta)) {
//				annotationsToKeep.add(ta);
//			} else {
//				logger.info("Duplicate Annotation Removed: " + ta.getSingleLineRepresentation(true, true));
//			}
//		}
//
//		List<TextAnnotation> annotationsToReturn = Collections.list(Collections.enumeration(annotationsToKeep));
//		Collections.sort(annotationsToReturn);
//		return annotationsToReturn;
//
//	}

	
    public static Set<TextAnnotation> intersectAnnotations(Collection<TextAnnotation> a1, Collection<TextAnnotation> a2) {
        Set<TextAnnotation> iSet = new HashSet<TextAnnotation>(a1);
        iSet.retainAll(a2);
        return iSet;
    }

    public static Set<TextAnnotation> newAnnotations(Collection<TextAnnotation> a1, Collection<TextAnnotation> a2) {
        Set<TextAnnotation> newSet = new HashSet<TextAnnotation>(a2);
        newSet.removeAll(a1);
        return newSet;
    }

    public static Set<TextAnnotation> aggregateAnnotations(Collection<Collection<TextAnnotation>> l) {
        Set<TextAnnotation> aggregate = new HashSet<TextAnnotation>();
        for(Iterator<Collection<TextAnnotation>> i = l.iterator(); i.hasNext(); ) {
            aggregate.addAll(i.next());
        }
        return aggregate;
    }
   
    public static boolean isTokenAnnotation (TextAnnotation a) {
        //System.out.println(a.getClassMention().getMentionName());
        return a.getClassMention().getMentionName().equals("token");
    }
    
    public static boolean isAnnotationTypeMember (TextAnnotation a, String... types) {
        String aType = a.getClassMention().getMentionName();
        return Arrays.asList(types).contains(aType);
    }
    
    public static LinkedList<TextAnnotation> removeTypes (Collection<TextAnnotation> annotations,
                                                          String... types) {
        LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
        for (TextAnnotation a : annotations) {
            if (!isAnnotationTypeMember(a, types)) {
                newAnnotations.add(a);
            }
        }
        return newAnnotations;
    }

    public static LinkedList<TextAnnotation> keepTypes (Collection<TextAnnotation> annotations,
                                                        String... types) {
        LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
        for (TextAnnotation a : annotations) {
            if (isAnnotationTypeMember(a, types)) {
                newAnnotations.add(a);
            }
        }
        return newAnnotations;
    }
    
    
    public static LinkedList<TextAnnotation> removeTokens (Collection<TextAnnotation> annotations) {
        LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
        for (TextAnnotation a : annotations) {
            if (!isTokenAnnotation(a)) {
                newAnnotations.add(a);
            }
        }
        return newAnnotations;
    }
    
    public static LinkedList<TextAnnotation> getTokens (Collection<TextAnnotation> annotations) {
        LinkedList<TextAnnotation> newAnnotations = new LinkedList<TextAnnotation>();
        for (TextAnnotation a : annotations) {
            //System.out.println(a.getClassMention().getMentionName());
            if (isTokenAnnotation(a)) {
                newAnnotations.add(a);
            }
        }
        return newAnnotations;
    }
    
    public static class SpanStartsComparator implements Comparator<Span> {
        public int compare(Span s1, Span s2) {
            return s1.getSpanStart() - s2.getSpanStart();
        }
    }
    
    public static class AnnotationSpanStartsComparator implements Comparator<TextAnnotation> {
        SpanStartsComparator comparator;
        
        public AnnotationSpanStartsComparator() {
            super();
            this.comparator = new SpanStartsComparator();
        }

        public int compare(TextAnnotation a1, TextAnnotation a2) {
            return comparator.compare(a1.getSpans().get(0), a2.getSpans().get(0));
        }

    }
    
    
	
	
	
	
}
