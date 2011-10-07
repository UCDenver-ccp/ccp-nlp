/**
 * 
 */
package edu.ucdenver.ccp.rdf.ao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.ao.AoDocumentRdfGenerator;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

/**
 * Creates AO-format document RDF by extracting a string of numbers presumed to be the PubMed ID
 * from the document ID
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PubMedAoDocumentRdfGenerator extends AoDocumentRdfGenerator {
	private static final Pattern pmidPattern = Pattern.compile("(\\d+)");

	private static final String PUBMED_BASE_URL = "http://www.ncbi.nlm.nih.gov/pubmed/";

	private URL documentUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.annotation.serialization.rdf.uima.DocumentRdfGenerator#generateRdf(org.apache
	 * .uima.jcas.JCas, edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor,
	 * edu.ucdenver.ccp.annotation.serialization.rdf.uima.DocumentUrlGenerator)
	 */
	@Override
	public Collection<Statement> generateRdf(JCas jcas, DocumentMetaDataExtractor documentMetaDataExtractor) {
		String documentId = documentMetaDataExtractor.extractDocumentId(jcas);
		Matcher m = pmidPattern.matcher(documentId);
		if (m.find()) {
			String pmid = m.group(1);
			if (m.find(m.end(1)))
				throw new IllegalArgumentException(
						"Ambiguous document ID, unable to determine which string of numbers is the PubMed ID in: "
								+ documentId);
			try {
				documentUrl = new URL(PUBMED_BASE_URL + pmid);
				URI documentUri = getPubMedUri(new PubMedID(pmid));
				return super.generateRdf(documentUri, documentUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		throw new IllegalArgumentException("Expected PubMed ID to be part of the document ID but could not find one: "
				+ documentId);
	}

	/**
	 * @param pmid
	 * @return
	 */
	protected URI getPubMedUri(PubMedID pmid) {
		return new URIImpl(RdfUtil.createUri(RdfUtil.getNamespace(pmid.getDataSource()), pmid.toString()).toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.DocumentRdfGenerator#getDocumentUrl()
	 */
	@Override
	public URL getDocumentUrl() {
		if (documentUrl == null)
			throw new IllegalStateException("generateRdf() must be called prior to retrieving the documentUrl!");
		return documentUrl;
	}
}
