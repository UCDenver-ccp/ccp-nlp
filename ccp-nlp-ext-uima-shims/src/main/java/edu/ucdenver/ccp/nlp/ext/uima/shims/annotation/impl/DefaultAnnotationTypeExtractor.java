package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationTypeExtractor;

public class DefaultAnnotationTypeExtractor implements AnnotationTypeExtractor {

	@Override
	public String getAnnotationType(Annotation annotation) {
		return annotation.getType().getName();
	}

}
