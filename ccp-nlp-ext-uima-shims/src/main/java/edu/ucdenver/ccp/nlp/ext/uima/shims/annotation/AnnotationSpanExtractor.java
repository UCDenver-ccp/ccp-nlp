package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * Interface for extracting {@link Span} instances corresponding to UIMA {@link Annotation}
 * instances
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationSpanExtractor {
	/**
	 * Returns a {@link List}<Span> corresponding to the span of the input {@link Annotation}
	 * instance
	 * 
	 * @param annotation
	 *            the {@link Annotation} whose spans will be returned
	 * @return the {@link Span} instances associated with the input {@link Annotation}
	 */
	public List<Span> getAnnotationSpans(Annotation annotation);

	/**
	 * Returns a {@link String} representation of the text that is covered by the input annotation.
	 * This method allows annotation types that may allow more than a single span to customize their
	 * covered text.
	 * 
	 * @param annotation
	 *            the {@link Annotation} whose covered text will be returned
	 * @return a {@link String} representation of the text that is covered by the input annotation
	 */
	public String getCoveredText(Annotation annotation);
}
