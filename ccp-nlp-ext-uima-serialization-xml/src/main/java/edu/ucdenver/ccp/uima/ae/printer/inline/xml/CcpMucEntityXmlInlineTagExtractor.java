package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor;

/**
 * Implementation of a {@link MucEntityXmlInlineTagExtractor} for use with the CCP type system
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpMucEntityXmlInlineTagExtractor extends MucEntityXmlInlineTagExtractor {

	/**
	 * Initializes a {@link MucEntityXmlInlineTagExtractor} instance for use with the CCP type
	 * system
	 */
	public CcpMucEntityXmlInlineTagExtractor() {
		super(CollectionsUtil.createList(CCPTextAnnotation.type), new CcpAnnotationDataExtractor());
	}

}
