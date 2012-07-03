/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * Interface for modifying (decorating) annotations
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationDecorator {

	/**
	 * Creates a new annotation in the input JCas using the specified type
	 * 
	 * @param jcas
	 * @param type
	 * @param span
	 * @return
	 */
	public Annotation newAnnotation(JCas jcas, String type, Span span);

//	public void addAnnotationAttribute(Annotation annotation, Object attribute);
	
	public void addAnnotationAttribute(Annotation annotation, String attributeType, Object attribute);

}
