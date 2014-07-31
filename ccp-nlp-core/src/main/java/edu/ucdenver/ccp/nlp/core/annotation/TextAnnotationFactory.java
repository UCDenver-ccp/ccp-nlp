package edu.ucdenver.ccp.nlp.core.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
 * builds TextAnnotations from TextAnnotation.toString() output. First version geared towards
 * requirements from OBA annotations, makes a few assumptions: - Lists are assumed for values that
 * are comma seperated. In the future, this may require re-consideration, meta-data, or a flag in
 * the constructor. - slots with no values are not created rather than having a slot with a null
 * value or empty string. TODO: Complex Slots, will require a break from the format output from
 * TextAnnotation.toString() TODO: better error handling.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */
public class TextAnnotationFactory {

	Logger logger = Logger.getLogger(TextAnnotationFactory.class);

	// -CLASS MENTION: 39917/GO:0005623 "cell" [1..4]
	// - SLOT MENTION: localConceptID with SLOT VALUE(s): 39917/GO:0005623
	// - SLOT MENTION: localOntologyID with SLOT VALUE(s): 39917
	// - SLOT MENTION: preferredName with SLOT VALUE(s): cell
	// - SLOT MENTION: synonyms with SLOT VALUE(s):
	// - SLOT MENTION: semanticTypes with SLOT VALUE(s): T999
	// - SLOT MENTION: termName with SLOT VALUE(s): cell
	// - SLOT MENTION: score with SLOT VALUE(s): 10

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
	 * 
	 * @return
	 */
	public static TextAnnotationFactory createFactoryWithDefaults() {
		return createFactoryWithDefaults("");
	}
	
	public static TextAnnotationFactory createFactoryWithDefaults(String documentId) {
		Annotator annotator = new Annotator(1, "Factory", "Test", "CCP");
		AnnotationSet set = new AnnotationSet(1, "set", "test set");
		return new TextAnnotationFactory(annotator, -1, set, 1, 2, documentId, 3);
	}

	public TextAnnotationFactory(Annotator annotator, int annotatorID, AnnotationSet annotationSet, int annotationID,
			int documentCollectionID, String documentID, int documentSectionID) {

		this.annotator = annotator;
		this.annotationSet = annotationSet;
		this.annotationID = annotationID;
		this.documentCollectionID = documentCollectionID;
		this.documentID = documentID;
		this.documentSectionID = documentSectionID;
	}

	public TextAnnotation createAnnotation(int start, int end, String covered, ClassMention cm) {
		return new DefaultTextAnnotation(start, end, covered, annotator, annotationSet, annotatorID, -1, documentID, 1,
				cm);
	}

	public TextAnnotation createFromString(String s) {
		ClassMention classMention = null;
		List<PrimitiveSlotMention> slots = new ArrayList<PrimitiveSlotMention>();

		String[] lines = s.split("\n");
		for (String line : lines) {
			// System.out.println(line);
			if (line.matches(".*SLOT MENTION.*")) {
				PrimitiveSlotMention sm = parseSlotMention(line);
				if (sm != null) {
					slots.add(sm);
				} else {
					logger.error("**** got a null slot mention from : " + line);
					// just not adding it...
				}
			} else if (line.matches(".*CLASS MENTION.*")) {
				classMention = parseClassMention(line);
			}
		}
		if (classMention != null) {
			TextAnnotation ta = new DefaultTextAnnotation(spanFrom, spanTo, coveredText, annotator, annotationSet,
					annotationID, documentCollectionID, documentID, documentSectionID, classMention);

			for (PrimitiveSlotMention sm : slots) {
				try {
					classMention.addPrimitiveSlotMention(sm);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			return ta;
		} else {
			return null;
		}
	}

	ClassMention parseClassMention(String line) {
		// CLASS MENTION: (\\W+)\\w(\\W+)\\s\\[(\\d+)\\.\\.(\\d+)\\]
		ClassMention cm = null;
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
		} else {
			// System.err.println("nope");
		}
		return cm;
	}

	PrimitiveSlotMention parseSlotMention(String line) {

		Pattern pattern = Pattern.compile("-\\s+SLOT\\s+MENTION:(.+)with\\s+SLOT\\s+VALUE\\(s\\):\\s*(\\S+)");
		Matcher matcher = pattern.matcher(line);
		Pattern listPattern = Pattern.compile("^[\\w\\d,]+$");
		Pattern intPattern = Pattern.compile("^(\\d+)$");
		Pattern doublePattern = Pattern.compile("^([\\d\\.E+-]+)$");
		String mentionName = "";
		String value = "";

		if (matcher.find()) {
			mentionName = matcher.group(1);
			mentionName = mentionName.trim(); // the regex above it a little too loose.
			// it was too tight and couldn't deal with slot names that have spaces in them.
			value = matcher.group(2);
			logger.debug("READING SLOT: " + mentionName + "   value: \"" + value + "\"");
			PrimitiveSlotMention sm = null;

			Matcher listMatcher = listPattern.matcher(value);
			Matcher intMatcher = intPattern.matcher(value);
			Matcher doubleMatcher = doublePattern.matcher(value);

			// Q: does the order matter? ...put the most restrictive pattern first
			if (intMatcher.find()) {
				String intString = intMatcher.group(0);
				// No conversion for now...value is of type String, not Integer
				Integer integer = Integer.parseInt(intString);
				sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName, integer);
				logger.debug("got integer" + intString);
			} else if (doubleMatcher.find()) {
				String doubleString = doubleMatcher.group(0);
				// No conversion for now...value is of type String, not Double
				sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName,
						Float.parseFloat(doubleString));
				logger.debug("got double" + doubleString);
			} else if (listMatcher.find()) {
				sm = new DefaultStringSlotMention(mentionName);
				String listString = listMatcher.group(0);
				String[] parts = listString.split(",");
				for (String p : parts) {
					sm.addSlotValue(p);
					logger.debug("got list part:" + p);
				}
			} else {
				// not a integer, double or list, must be a string
				logger.debug("defaulting to string:" + value);
				sm = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(mentionName, value);
			}
			return sm;
		} else {
			logger.error("no mention name parsed from string: \"" + line + "\"");
			return null;
		}
	}

}
