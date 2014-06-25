package edu.ucdenver.ccp.nlp.uima.shims.annotation.impl;

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

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationSpanExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Implementation of {@link AnnotationSpanExtractor} for use with the CCP type system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpAnnotationSpanExtractor implements AnnotationSpanExtractor {

	/**
	 * @see AnnotationSpanExtractor#getAnnotationSpans(org.apache.uima.jcas.tcas.Annotation)
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not an instance of {@link CCPTextAnnotation}
	 */
	@Override
	public List<Span> getAnnotationSpans(Annotation annotation) {
		if (annotation instanceof CCPTextAnnotation) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotation;
			List<Span> spans = new ArrayList<Span>();
			FSArray ccpSpans = ccpTa.getSpans();
			if (ccpSpans.size() == 0) {
				Span span = new Span(ccpTa.getBegin(), ccpTa.getEnd());
				spans.add(span);
			} else {
				for (int i = 0; i < ccpSpans.size(); i++) {
					CCPSpan ccpSpan = (CCPSpan) ccpSpans.get(i);
					Span span = new Span(ccpSpan.getSpanStart(), ccpSpan.getSpanEnd());
					spans.add(span);
				}
			}
			return spans;
		}
		throw new IllegalArgumentException("Cannot return spans for a non-CCPTextAnnotation annotation: "
				+ annotation.getClass().getName() + " --" + annotation.getCoveredText() + " -- "
				+ CCPTextAnnotation.class.getName() + " --" + (annotation instanceof CCPTextAnnotation));
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationSpanExtractor#getCoveredText(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getCoveredText(Annotation annotation) {
		List<Span> spans = getAnnotationSpans(annotation);
		String coveredText = annotation.getCoveredText();

		List<String> spanTexts = new ArrayList<String>();
		int minSpanOffset = spans.get(0).getSpanStart();
		for (Span span : spans) {
			spanTexts
					.add(coveredText.substring(span.getSpanStart() - minSpanOffset, span.getSpanEnd() - minSpanOffset));
		}
		return CollectionsUtil.createDelimitedString(spanTexts, " .. ");
	}

}
