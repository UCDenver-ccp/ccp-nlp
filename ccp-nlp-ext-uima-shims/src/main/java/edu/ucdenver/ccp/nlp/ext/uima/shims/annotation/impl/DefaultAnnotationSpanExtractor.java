package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationSpanExtractor;

/**
 * Default implementation of the {@link AnnotationSpanExtractor} that works with the default UIMA
 * annotation type, so there is always only a single span and {@link #getCoveredText(Annotation)}
 * simply returns {@link Annotation#getCoveredText()}.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DefaultAnnotationSpanExtractor implements AnnotationSpanExtractor {

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationSpanExtractor#getAnnotationSpans(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<Span> getAnnotationSpans(Annotation annotation) {
		Span span = new Span(annotation.getBegin(), annotation.getEnd());
		return CollectionsUtil.createList(span);
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationSpanExtractor#getCoveredText(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getCoveredText(Annotation annotation) {
		return annotation.getCoveredText();
	}

}
