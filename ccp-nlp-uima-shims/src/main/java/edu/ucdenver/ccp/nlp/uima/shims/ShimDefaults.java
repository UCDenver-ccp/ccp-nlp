/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims;

import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This interface provides constants for various shim default values. The {@link String} constants
 * are particularly useful when setting default values in UIMA AE configuration parameters.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface ShimDefaults {

	/**
	 * default class name for a {@link DocumentMetadataHandler} that complies with the CCP type
	 * system (good for use as default values in AE configuration parameters)
	 */
	public static final String CCP_DOCUMENT_METADATA_HANDLER_CLASS_NAME = "edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler";

	/**
	 * default class for a {@link DocumentMetadataHandler} that complies with the CCP type system
	 */
	public static final Class<? extends DocumentMetadataHandler> CCP_DOCUMENT_METADATA_HANDLER_CLASS = CcpDocumentMetadataHandler.class;

}
