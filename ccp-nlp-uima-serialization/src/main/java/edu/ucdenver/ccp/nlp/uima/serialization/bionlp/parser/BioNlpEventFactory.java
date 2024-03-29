/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.serialization.bionlp.parser;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpEventFactory {

	private static final Annotator ANNOTATOR = new Annotator("291514121", "BioNLP", "Unknown");
	public static final String THEME_SLOT_NAME = "has theme";
	public static final String EVENT_ID_SLOT_NAME = "event ID";
	public static final String CAUSE_SLOT_NAME = "has cause";

	/**
	 * Parses a BioNLP event line and returns a {@link TextAnnotation} for the represented event
	 * 
	 * @param line
	 * @param bionlpIdToAnnotationMap
	 *            contains a mapping from the BioNLP ID, e.g. T3 or E16 to the annotation that
	 *            represents that theme or event
	 * @return
	 */
	public static TextAnnotation createEventAnnotation(String line, Map<String, TextAnnotation> bionlpIdToAnnotationMap) {
		if (!line.startsWith("E"))
			throw new IllegalArgumentException(
					"Cannot create annotation for a BioNLP event from a line that does not start with 'E':" + line);
		String[] toks = line.split("\\p{Space}+");
		String eventIdStr = toks[0];
		String eventTypePlusTriggerStr = toks[1];

		String[] eventTypePlusTrigger = eventTypePlusTriggerStr.split(":");
		String eventType = eventTypePlusTrigger[0].toLowerCase();
		String triggerId = eventTypePlusTrigger[1];
		Set<String> themeIds = getThemeIds(line);
		String causeId = getCauseId(line);

		ClassMention cm = new DefaultClassMention(eventType);
		TextAnnotation triggerAnnot = bionlpIdToAnnotationMap.get(triggerId);
		if (triggerAnnot == null)
			throw new IllegalArgumentException(String.format(
					"Expected trigger annotation (%s) missing from input IdToAnnotation map.", triggerId));
		TextAnnotation ta = new DefaultTextAnnotation(triggerAnnot.getAnnotationSpanStart(),
				triggerAnnot.getAnnotationSpanEnd(), "", ANNOTATOR, new AnnotationSet(), "-1", -1, "", -1, cm);

		addThemeSlotFillers(ta, themeIds, bionlpIdToAnnotationMap);
		addCauseSlotFiller(ta, causeId, bionlpIdToAnnotationMap);

		try {
			TextAnnotationUtil.addSlotValue(ta, EVENT_ID_SLOT_NAME, eventIdStr);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding theme slot and value.");
		}

		return ta;
	}

	/**
	 * @param line
	 * @return
	 */
	private static String getCauseId(String line) {
		Pattern themePattern = Pattern.compile("Cause:([ET]\\d+)");
		Matcher m = themePattern.matcher(line);
		if (m.find())
			return m.group(1);
		return null;
	}

	/**
	 * Extracts all theme Ids, e.g. Theme:T55 from a line
	 * 
	 * @param line
	 * @return
	 */
	private static Set<String> getThemeIds(String line) {
		Set<String> themeIds = new HashSet<String>();
		Pattern themePattern = Pattern.compile("Theme:([ET]\\d+)");
		Matcher m = themePattern.matcher(line);
		while (m.find())
			themeIds.add(m.group(1));
		return themeIds;
	}

	/**
	 * If the cause ID is non-null, then the appropriate annotation is added as a slot filler
	 * 
	 * @param ta
	 * @param causeIds
	 * @param bionlpIdToAnnotationMap
	 * @return
	 */
	private static void addCauseSlotFiller(TextAnnotation ta, String causeId,
			Map<String, TextAnnotation> bionlpIdToAnnotationMap) {
		if (causeId != null)
			addSlotFiller(ta, causeId, CAUSE_SLOT_NAME, bionlpIdToAnnotationMap);
	}

	/**
	 * Adds a theme slot filler annotation for each of the input theme IDs
	 * 
	 * @param ta
	 * @param themeIds
	 * @return
	 */
	private static void addThemeSlotFillers(TextAnnotation ta, Set<String> themeIds,
			Map<String, TextAnnotation> bionlpIdToAnnotationMap) {
		for (String id : themeIds)
			addSlotFiller(ta, id, THEME_SLOT_NAME, bionlpIdToAnnotationMap);
	}

	private static void addSlotFiller(TextAnnotation ta, String id, String slotName,
			Map<String, TextAnnotation> bionlpIdToAnnotationMap) {
		TextAnnotation themeAnnot = bionlpIdToAnnotationMap.get(id);
		if (themeAnnot == null)
			throw new IllegalArgumentException(String.format(
					"Expected annotation ID (%s) missing from input IdToAnnotation map.", id));
		try {
			TextAnnotationUtil.addSlotValue(ta, slotName, themeAnnot.getClassMention());
		} catch (Exception e) {
			throw new RuntimeException("Error while adding theme slot and value.");
		}
	}

}
