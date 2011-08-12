package edu.ucdenver.ccp.rdf.foaf;

import java.net.URI;

import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class FoafUriUtil {
	public static final URI FOAF_ORGANIZATION = RdfUtil.createUri(RdfNamespace.FOAF, "Organization");
}
