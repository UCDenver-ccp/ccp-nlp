/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation.impl;

import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpAnnotationDataExtractor extends AnnotationDataExtractor {

	/**
	 * @param typeExtractor
	 * @param spanExtractor
	 */
	public CcpAnnotationDataExtractor() {
		super(new CcpAnnotationTypeExtractor(), new CcpAnnotationSpanExtractor());
	}

}
