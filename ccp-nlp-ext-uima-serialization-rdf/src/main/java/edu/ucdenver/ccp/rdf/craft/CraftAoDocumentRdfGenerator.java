/**
 * 
 */
package edu.ucdenver.ccp.rdf.craft;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.uima.jcas.JCas;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.identifier.publication.PubMedID;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.ao.AoDocumentRdfGenerator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CraftAoDocumentRdfGenerator extends AoDocumentRdfGenerator {

	private URL documentUrl = null;

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
		String pmid = documentMetaDataExtractor.extractDocumentId(jcas);
		pmid = StringUtil.removeSuffix(pmid, ".txt");
		CraftDocument craftDocument = CraftDocument.valueOf(new PubMedID(pmid));
		try {
			documentUrl = new URL(craftDocument.pubMedCentralUri().toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		URI documentUri = new URIImpl("http://craft.ucdenver.edu/document/" + craftDocument.pmcId().toString());
		return super.generateRdf(documentUri, documentUrl);
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
