/**
 * 
 */
package edu.ucdenver.ccp.nlp.core.uima.util;

import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.TypeSystemDescriptionFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TypeSystemUtil {

	/**
	 * 
	 * @return a {@link TypeSystemDescription} for the CCP Type System
	 */
	public static TypeSystemDescription getCcpTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
	}
}