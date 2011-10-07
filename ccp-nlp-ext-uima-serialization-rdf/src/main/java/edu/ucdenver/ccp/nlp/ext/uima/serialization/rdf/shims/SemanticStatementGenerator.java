/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface SemanticStatementGenerator {

	/**
	 * @param annotation
	 * @param graphUri
	 * @return
	 */
	Collection<? extends Statement> getSemanticStatements(Annotation annotation, URI graphUri, Map<String, URI> annotationKeyToSemanticInstanceUriMap);

}
