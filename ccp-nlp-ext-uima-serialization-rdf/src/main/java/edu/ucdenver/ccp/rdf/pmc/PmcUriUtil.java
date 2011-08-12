package edu.ucdenver.ccp.rdf.pmc;

import java.net.URI;
import java.net.URISyntaxException;

public class PmcUriUtil {

	public static final String PMC_BASE_URI = "http://www.ncbi.nlm.nih.gov/pmc/articles/";

	/**
	 * Returns a URI for a specific PubMed Central document
	 * 
	 * @param pmcId
	 * @return
	 * @throws URISyntaxException
	 */
	public static URI createPmcUri(String pmcId) throws URISyntaxException {
		return new URI(PMC_BASE_URI + pmcId);
	}

}
