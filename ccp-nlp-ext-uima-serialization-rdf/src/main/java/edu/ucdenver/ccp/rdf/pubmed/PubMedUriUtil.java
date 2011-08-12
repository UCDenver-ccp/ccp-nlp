package edu.ucdenver.ccp.rdf.pubmed;

import java.net.URI;
import java.net.URISyntaxException;

public class PubMedUriUtil {

	public static final String PUBMED_BASE_URI = "http://www.ncbi.nlm.nih.gov/pubmed/";

	/**
	 * Returns a URI for a specific PubMed document
	 * 
	 * @param pmid
	 * @return
	 * @throws URISyntaxException
	 */
	public static URI createPubMedUri(String pmid) throws URISyntaxException {
		return new URI(PUBMED_BASE_URI + pmid);
	}

}
