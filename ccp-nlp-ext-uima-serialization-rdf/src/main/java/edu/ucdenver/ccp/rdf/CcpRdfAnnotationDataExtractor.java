/**
 * 
 */
package edu.ucdenver.ccp.rdf;

import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.RdfAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationCreatorExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationKeyGenerator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationSpanExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationTypeExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpComponentAnnotationExctractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpRdfAnnotationDataExtractor extends RdfAnnotationDataExtractor {

	public CcpRdfAnnotationDataExtractor() {
		super(new CcpAnnotationTypeExtractor(), new CcpAnnotationSpanExtractor());
		setAnnotationCreatorExtractor(new CcpAnnotationCreatorExtractor());
		setComponentAnnotationExtractor(new CcpComponentAnnotationExctractor());
		setAnnotationKeyGenerator(new CcpAnnotationKeyGenerator());
	}

}
