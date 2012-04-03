package edu.ucdenver.ccp.uima.shim.ccp;

import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationSpanExtractor;


public class CcpCraftAnnotationDataExtractor extends AnnotationDataExtractor{

	public CcpCraftAnnotationDataExtractor() {
		super(new CcpCraftAnnotationTypeExtractor(), new CcpAnnotationSpanExtractor());
	}

}
