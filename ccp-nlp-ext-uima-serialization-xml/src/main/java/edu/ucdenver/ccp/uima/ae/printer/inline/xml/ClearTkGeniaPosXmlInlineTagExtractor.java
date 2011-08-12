package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.uima.shim.cleartk.ClearTkAnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shim.cleartk.ClearTkTokenAttributeExtractor;


/**
 * Implementation of a {@link MucEntityXmlInlineTagExtractor} for use with the CCP type system
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClearTkGeniaPosXmlInlineTagExtractor extends GeniaPosXmlInlineTagExtractor {

	/**
	 * Initializes a {@link GeniaEntityXmlInlineTagExtractor} instance for use with the CCP type
	 * system
	 */
	public ClearTkGeniaPosXmlInlineTagExtractor() {
		super(CollectionsUtil.createList(Sentence.type, Token.type), new ClearTkAnnotationDataExtractor(), new ClearTkTokenAttributeExtractor());
	}

}
