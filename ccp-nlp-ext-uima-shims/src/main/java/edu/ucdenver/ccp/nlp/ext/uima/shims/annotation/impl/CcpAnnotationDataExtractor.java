package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;

/**
 * Implementation of the {@link AnnotationDataExtractor} interface specific to the CCP type system
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpAnnotationDataExtractor extends AnnotationDataExtractor {

	/**
	 * Initializes a {@link AnnotationDataExtractor} implementation specific to the CCP type system
	 */
	public CcpAnnotationDataExtractor() {
		super(new CcpAnnotationTypeExtractor(), new CcpAnnotationSpanExtractor());
	}

}
