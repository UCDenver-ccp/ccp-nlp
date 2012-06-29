/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationKeyGenerator {
	public String getAnnotationKey(Annotation annotation);
}
