/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.RdfAnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.UriFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface AnnotationRdfGenerator {

	public Collection<? extends Statement> generateRdf(RdfAnnotationDataExtractor annotationDataExtractor,
			Annotation annotation, UriFactory uriFactory, URI documentUri, URL documentUrl, String documentText,
			Map<String, URI> annotationKeyToUriMap, Map<String, Set<URI>> annotationKeyToSelectorUrisMap,
			Map<String, URI> annotationKeyToSemanticInstanceUriMap);
}
