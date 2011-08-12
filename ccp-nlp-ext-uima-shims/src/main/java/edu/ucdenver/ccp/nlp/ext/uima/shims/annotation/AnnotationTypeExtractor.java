package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * Interface to be used for extracting type information from a UIMA {@link Annotation} instance
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationTypeExtractor {
	/**
	 * Retrieves the annotation type from the input UIMA {@link Annotation} instance
	 * 
	 * @param annotation
	 *            the annotation whose type is returned
	 * @return a {@link String} representation of the input {@link Annotation} type
	 */
	public String getAnnotationType(Annotation annotation);
}
