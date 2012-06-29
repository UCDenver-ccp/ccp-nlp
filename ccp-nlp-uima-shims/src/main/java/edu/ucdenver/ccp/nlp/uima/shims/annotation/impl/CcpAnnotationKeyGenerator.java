/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationKeyGenerator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class CcpAnnotationKeyGenerator implements AnnotationKeyGenerator {

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationKeyGenerator#getAnnotationKey(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getAnnotationKey(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(String.format("This AnnotationKeyGenerator (%s) cannot create annotation keys for the non-CCPTextAnnotation annotation type: ",
					this.getClass().getName(), annotation.getClass().getName()));
		WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		return wrappedTa.getSingleLineRepresentation();
	}

}
