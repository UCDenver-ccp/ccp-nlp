/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationCreatorExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class CcpAnnotationCreatorExtractor implements AnnotationCreatorExtractor {

	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationCreatorExtractor#getAnnotator(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public Annotator getAnnotator(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(String.format("This AnnotationCreatorExtractor (%s) cannot extract annotators from the non-CCPTextAnnotation annotation: ",
					this.getClass().getName(), annotation.getClass().getName()));
		WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		edu.ucdenver.ccp.nlp.core.annotation.Annotator annotator = wrappedTa.getAnnotator();
		try {
			Annotator ann =  new Annotator(new URI("http://kabob.ucdenver.edu/annotator" + annotator.getLastName().replaceAll("\\s", "")), "version goes here");
			return ann;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Annotator for input annotation resulted in malformed URI", e);
		}
	}

}
