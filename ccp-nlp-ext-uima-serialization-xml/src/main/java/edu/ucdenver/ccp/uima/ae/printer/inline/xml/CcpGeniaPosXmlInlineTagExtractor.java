package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.impl.CcpTokenAttributeExtractor;
import edu.ucdenver.ccp.uima.shim.ccp.CcpCraftAnnotationDataExtractor;

/**
 * Implementation of a {@link MucEntityXmlInlineTagExtractor} for use with the CCP type system
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpGeniaPosXmlInlineTagExtractor extends GeniaPosXmlInlineTagExtractor {

	/**
	 * Initializes a {@link GeniaEntityXmlInlineTagExtractor} instance for use with the CCP type
	 * system
	 */
	public CcpGeniaPosXmlInlineTagExtractor() {
		super(CollectionsUtil.createList(CCPTextAnnotation.type), new CcpCraftAnnotationDataExtractor(),
				new CcpTokenAttributeExtractor());
	}

}
