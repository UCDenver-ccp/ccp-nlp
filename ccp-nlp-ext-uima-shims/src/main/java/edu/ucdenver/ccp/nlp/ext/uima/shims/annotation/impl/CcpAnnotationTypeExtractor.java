package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationTypeExtractor;

/**
 * Implementation of {@link AnnotationTypeExtractor} specific to the CCP type system
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpAnnotationTypeExtractor implements AnnotationTypeExtractor {

	/**
	 * Returns the class mention name for the input {@link Annotation} which must be an instance of
	 * {@link CCPTextAnnotation}
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.shim.annotation.uchsc.ccp.uima.shim.AnnotationTypeExtractor#getAnnotationType(org.apache.uima.jcas.tcas.Annotation)
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not an instance of {@link CCPTextAnnotation}
	 */
	@Override
	public String getAnnotationType(Annotation annotation) {
		if (annotation instanceof CCPTextAnnotation)
			return ((CCPTextAnnotation) annotation).getClassMention().getMentionName();
		throw new IllegalArgumentException("Cannot return type for a non-CCPTextAnnotation annotation: "
				+ annotation.getClass().getName());
	}

}
