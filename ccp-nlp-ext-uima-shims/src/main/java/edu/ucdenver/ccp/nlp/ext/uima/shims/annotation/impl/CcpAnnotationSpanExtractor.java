package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

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
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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
