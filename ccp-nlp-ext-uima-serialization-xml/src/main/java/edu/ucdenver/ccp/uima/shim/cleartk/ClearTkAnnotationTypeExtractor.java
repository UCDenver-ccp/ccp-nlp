package edu.ucdenver.ccp.uima.shim.cleartk;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.DefaultAnnotationTypeExtractor;

public class ClearTkAnnotationTypeExtractor extends DefaultAnnotationTypeExtractor{

	@Override
	public String getAnnotationType(Annotation annotation) {
		String annotationType = super.getAnnotationType(annotation);
		return annotationType.substring(annotationType.lastIndexOf(StringConstants.PERIOD)+1);
	}

}
