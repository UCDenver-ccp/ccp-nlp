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

import edu.ucdenver.ccp.identifier.publication.PubMedCentralID;
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
public class PmcOaBulkAoDocumentRdfGenerator extends AoDocumentRdfGenerator {
	private static final Pattern pmcidPattern = Pattern.compile("-(\\d+)\\.nxml");

	private static final String PUBMED_CENTRAL_BASE_URL = "http://www.ncbi.nlm.nih.gov/pmc/articles/";

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
		Matcher m = pmcidPattern.matcher(documentId);
		if (m.find()) {
			String pmcid = "PMC" + m.group(1);
			if (m.find(m.end(1)))
				throw new IllegalArgumentException(
						"Ambiguous document ID, unable to determine which string of numbers is the PubMed Central ID in: "
								+ documentId);
			try {
				documentUrl = new URL(PUBMED_CENTRAL_BASE_URL + pmcid);
				URI documentUri = getPubMedCentralUri(new PubMedCentralID(pmcid));
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
	protected URI getPubMedCentralUri(PubMedCentralID pmcid) {
		return new URIImpl(RdfUtil.createUri(RdfUtil.getNamespace(pmcid.getDataSource()), pmcid.toString()).toString());
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
