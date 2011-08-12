package edu.ucdenver.ccp.uima.shim.cleartk;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.DefaultAnnotationSpanExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.DefaultAnnotationTypeExtractor;


public class ClearTkAnnotationDataExtractor extends AnnotationDataExtractor {

	public ClearTkAnnotationDataExtractor() {
		super(new ClearTkAnnotationTypeExtractor(), new DefaultAnnotationSpanExtractor());
	}

}
