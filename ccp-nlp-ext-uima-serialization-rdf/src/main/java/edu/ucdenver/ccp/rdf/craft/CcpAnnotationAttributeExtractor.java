/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class CcpAnnotationAttributeExtractor {

	protected CCPTextAnnotation checkInputType(Annotation annotation, String methodName) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(this.getClass().getName() + "." + methodName
					+ " is unable to handle annotation classes other than CCPTextAnnotations. Use was attempted with: "
					+ annotation.getClass().getName());
		return (CCPTextAnnotation) annotation;
	}
	
	

}
