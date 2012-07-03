/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface ComponentAnnotationExtractor {

	public Collection<Annotation> getComponentAnnotations(Annotation annotation);

}
