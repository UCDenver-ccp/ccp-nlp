/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * Interface for modifying (decorating) annotations 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface AnnotationDecorator<T> {

	public void addAnnotationAttribute(Annotation annotation, T attribute);
	
}
