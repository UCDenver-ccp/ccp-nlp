package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;

public interface AnnotationAttributeExtractor {

	public Collection<Object> getAnnotationAttributes(Annotation annotation, AnnotationAttribute attribute);

}
