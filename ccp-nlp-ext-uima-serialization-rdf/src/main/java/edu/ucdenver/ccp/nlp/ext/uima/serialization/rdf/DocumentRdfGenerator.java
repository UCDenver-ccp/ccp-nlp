/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.UriFactory;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface DocumentRdfGenerator {

	public Collection<Statement> generateRdf(URI documentUri, URL documentUrl);

	public Collection<Statement> generateRdf(JCas jcas, DocumentMetaDataExtractor documentMetaDataExtractor);

	public URI getDocumentUri();

	public URL getDocumentUrl();

}
