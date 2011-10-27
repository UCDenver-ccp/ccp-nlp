/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDecorator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public abstract class CcpAnnotationDecorator<T> implements AnnotationDecorator<T> {

	
	/**
	 * @param annotation
	 */
	protected void checkType(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation))
			throw new IllegalArgumentException(
					String.format(
							"This AnnotationDecorator (%s) cannot decorate annotations from the non-CCPTextAnnotation annotation type: ",
							this.getClass().getName(), annotation.getClass().getName()));
	}

	
	protected void addPrimitiveSlot(Annotation annotation, String slotName, Object primitiveSlotValue) {
		checkType(annotation);
		WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		try {
			TextAnnotationUtil.addSlotValue(wrappedTa, slotName, primitiveSlotValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	

}
