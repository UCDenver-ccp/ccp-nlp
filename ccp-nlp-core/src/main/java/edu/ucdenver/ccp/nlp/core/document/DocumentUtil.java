package edu.ucdenver.ccp.nlp.core.document;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.SpanUtils;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * Some utility methods for dealing with <code>GenericDocuments</code>.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DocumentUtil {

	/**
	 * 
	 * @return a mapping between section ID and section name as defined in
	 *         <code>DocumentSectionTypes</code>.
	 */
	public static HashMap<Integer, String> getSectionIDNameHash() {
		HashMap<Integer, String> idNameHash = new HashMap<Integer, String>();
		idNameHash.put(DocumentSectionTypes.OTHER_ID, DocumentSectionTypes.OTHER_NAME);
		idNameHash.put(DocumentSectionTypes.TITLE_ID, DocumentSectionTypes.TITLE_NAME);
		idNameHash.put(DocumentSectionTypes.ABSTRACT_ID, DocumentSectionTypes.ABSTRACT_NAME);
		return idNameHash;
	}

	/**
	 * 
	 * @return a mapping between section name and section ID as defined in
	 *         <code>DocumentSectionTypes</code>.
	 */
	public static HashMap<String, Integer> getSectionNameIDHash() {
		HashMap<String, Integer> nameIDHash = new HashMap<String, Integer>();
		nameIDHash.put(DocumentSectionTypes.OTHER_NAME, DocumentSectionTypes.OTHER_ID);
		nameIDHash.put(DocumentSectionTypes.TITLE_NAME, DocumentSectionTypes.TITLE_ID);
		nameIDHash.put(DocumentSectionTypes.ABSTRACT_NAME, DocumentSectionTypes.ABSTRACT_ID);
		return nameIDHash;
	}

	public static List<Span> usedSpans(GenericDocument doc) {
		return usedSpans(doc.getAnnotations());
	}

	public static List<Span> usedSpans(Collection<TextAnnotation> annotations) {
		List<Span> mergedSpans = new LinkedList<Span>();
		for (TextAnnotation a : annotations) {
			if (a.getClassMention().getMentionName() != "token") {
				mergedSpans = SpanUtils.mergeSpans(mergedSpans, a.getSpans());
			}
		}
		return mergedSpans;
	}

	public static List<Span> identifiedSpans(GenericDocument doc) {
		return identifiedSpans(doc.getAnnotations());
	}

	public static List<Span> identifiedSpans(Collection<TextAnnotation> annotations) {
		List<Span> mergedSpans = new LinkedList<Span>();
		for (TextAnnotation a : annotations) {
			mergedSpans = SpanUtils.mergeSpans(mergedSpans, a.getSpans());
		}
		return mergedSpans;
	}

}
