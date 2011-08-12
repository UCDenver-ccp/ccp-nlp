package edu.ucdenver.ccp.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;


import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.rdf.foaf.FoafUriUtil;

public class CcpUriUtil {
	public static final URI CCP_URI = createUri("http://compbio.ucdenver.edu/Hunter_lab/");
	
	
	private static final URI createUri(String uriStr) {
		try {
			return new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static final Collection<Statement> getCcpFoafOrganizationStmts() {
		Collection<Statement> stmts = new ArrayList<Statement>();
		Resource ccpUrl = new URIImpl(CCP_URI.toString());
		stmts.add(new StatementImpl(ccpUrl, new URIImpl(UriFactory.RDF_TYPE.toString()), new URIImpl(FoafUriUtil.FOAF_ORGANIZATION.toString())));
		return stmts;
	}
}
