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

package edu.ucdenver.ccp.nlp.core.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultPrimitiveSlotMentionFactory;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * builds TextAnnotations from TextAnnotation.toString() output.
 * First version geared towards requirements from OBA annotations,
 * makes a few assumptions:
 * - Lists are assumed for values that are comma seperated.
 *   In the future, this may require re-consideration, meta-data, or
 *   a flag in the constructor.
 * - slots with no values are not created rather than having a slot with a
 *   null value or empty string.
 *   TODO: Complex Slots, will require a break from the format 
 *         output from TextAnnotation.toString()
 *   TODO: better error handling.
 */
public class TextAnnotationFactory {
	
	Logger logger = Logger.getLogger(TextAnnotationFactory.class);
	
//	-CLASS MENTION: 39917/GO:0005623 "cell"	[1..4]
//	-    SLOT MENTION: localConceptID with SLOT VALUE(s): 39917/GO:0005623  
//	-    SLOT MENTION: localOntologyID with SLOT VALUE(s): 39917  
//	-    SLOT MENTION: preferredName with SLOT VALUE(s): cell  
//	-    SLOT MENTION: synonyms with SLOT VALUE(s): 
//	-    SLOT MENTION: semanticTypes with SLOT VALUE(s): T999  
//	-    SLOT MENTION: termName with SLOT VALUE(s): cell  
//	-    SLOT MENTION: score with SLOT VALUE(s): 10 

	

	AnnotationSet annotationSet;
	Annotator annotator;
	int annotatorID;
	int annotationID;
	int documentCollectionID;
	String documentID;
	int documentSectionID;
	
	// "plumbing" between the parse functions and createFromString()
	String coveredText;
	int spanFrom;
	int spanTo;

	/**
	 * create a factory with default values. Intended for testing.
	 * @return
	 */
	public static TextAnnotationFactory createFactoryWithDefaults() {
		Annotator annotator = new Annotator(1,"Factory", "Test","CCP");
		AnnotationSet set = new AnnotationSet(1,"set","test set");
		return new TextAnnotationFactory(annotator, -1, set, 1, 2, "", 3);
	}
	
	public TextAnnotationFactory(Annotator annotator, 
			int annotatorID,
			AnnotationSet annotationSet,
			int annotationID,
			int documentCollectionID,
			String documentID,
			int documentSectionID)  {
		
		this.annotator = annotator;
		this.annotationSet = annotationSet;
		this.annotationID = annotationID;
		this.documentCollectionID = documentCollectionID;
		this.documentID = documentID;
		this.documentSectionID = documentSectionID;
	}
	
	public TextAnnotation createAnnotation(int start, int end, String covered, ClassMention cm) {
		return new DefaultTextAnnotation(start, end, covered,
				annotator, annotationSet, annotatorID, -1, documentID, 1, cm);
	}
	
	public TextAnnotation createFromString(String s) {
		ClassMention classMention=null;
		List<PrimitiveSlotMention> slots = new ArrayList<PrimitiveSlotMention>();
		
		String[] lines = s.split("\n");
		for (String line : lines ) {
			//System.out.println(line);
			if (line.matches(".*SLOT MENTION.*")) {
				PrimitiveSlotMention sm = parseSlotMention(line);
				if (sm != null) {
					slots.add(sm);
				}
				else {
					logger.error("**** got a null slot mention from : " + line);
					// just not adding it...
				}
			}
			else if (line.matches(".*CLASS MENTION.*")) {
				classMention = parseClassMention(line);
			}
		}
		if (classMention != null) {
			TextAnnotation ta = new DefaultTextAnnotation(spanFrom, spanTo, coveredText,
				annotator, annotationSet,
				annotationID, documentCollectionID, documentID, documentSectionID, classMention);
		
			for (PrimitiveSlotMention sm : slots) {
				try {
					classMention.addPrimitiveSlotMention(sm);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			return ta;
		}
		else {
			return null;
		}
	}
	
	ClassMention parseClassMention(String line) {
		// CLASS MENTION: (\\W+)\\w(\\W+)\\s\\[(\\d+)\\.\\.(\\d+)\\]
		ClassMention cm=null;
        Pattern pattern = Pattern.compile("-CLASS MENTION: (\\S+)\\s+\\\"(\\S+)\\\"\\s+\\[(\\d+)\\.\\.(\\d+)\\]");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
        	String id = matcher.group(1);
        	coveredText = matcher.group(2);
        	String from = matcher.group(3);
        	String to = matcher.group(4);
        	spanFrom = Integer.parseInt(from);
        	spanTo = Integer.parseInt(to);
   		 	cm = new DefaultClassMention(id);	
        }
        else {
        	//System.err.println("nope");
        }
		return cm;
	}
	

	PrimitiveSlotMention parseSlotMention(String line) {

        //Pattern pattern = Pattern.compile("-\\s+SLOT\\s+MENTION:\\s+(\\S+)\\s+with\\s+SLOT\\s+VALUE\\(s\\):\\s*(\\S+)");
        Pattern pattern = Pattern.compile("-\\s+SLOT\\s+MENTION:(.+)with\\s+SLOT\\s+VALUE\\(s\\):\\s*(\\S+)");
        Matcher matcher = pattern.matcher(line);
        Pattern listPattern = Pattern.compile("^[\\w\\d,]+$");
        Pattern intPattern = Pattern.compile("^(\\d+)$");
        Pattern doublePattern = Pattern.compile("^([\\d\\.E+-]+)$");
        String mentionName="";
        String value="";

        if (matcher.find()) {
        	mentionName = matcher.group(1);
        	mentionName = mentionName.trim(); // the regex above it a little too loose.
        	// it was too tight and couldn't deal with slot names that have spaces in them.
        	value = matcher.group(2);	
            logger.debug("READING SLOT: " + mentionName + "   value: \"" + value + "\"");
    		PrimitiveSlotMention sm =  null;
    		
            Matcher listMatcher 	=   listPattern.matcher(value);
            Matcher intMatcher 		=    intPattern.matcher(value);
            Matcher doubleMatcher 	= doublePattern.matcher(value);
    		
            // Q: does the order matter? ...put the most restrictive pattern first
            if (intMatcher.find()) {
            	String intString = intMatcher.group(0);
            	// No conversion for now...value is of type String, not Integer
            	Integer integer = Integer.parseInt(intString);
            	sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName, integer);
        		logger.debug("got integer" + intString);
            }
            else if (doubleMatcher.find()) {
            	String doubleString = doubleMatcher.group(0);
               	// No conversion for now...value is of type String, not Double
            	sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName, Float.parseFloat(doubleString));
        		logger.debug("got double" + doubleString);
            }
            else if (listMatcher.find()) {
            	sm = new DefaultStringSlotMention(mentionName);
            	String listString = listMatcher.group(0);
            	String[] parts = listString.split(",");
            	for (String p : parts) {
            		sm.addSlotValue(p);
            		logger.debug("got list part:" + p);
            	}
            }
            else {
            	// not a integer, double or list, must be a string
            	logger.debug("defaulting to string:" + value);
            	sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName, value);
            }
            return sm;
        }
        else {
        	logger.error("no mention name parsed from string: \"" + line + "\"");
        	return null; 
        }
	}

}
