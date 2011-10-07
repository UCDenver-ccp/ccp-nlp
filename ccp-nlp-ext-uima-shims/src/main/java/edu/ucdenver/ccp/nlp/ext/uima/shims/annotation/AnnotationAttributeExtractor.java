package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation;

import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;

public interface AnnotationAttributeExtractor<T> {

	public Collection<T> getAnnotationAttributes(Annotation annotation);

}
