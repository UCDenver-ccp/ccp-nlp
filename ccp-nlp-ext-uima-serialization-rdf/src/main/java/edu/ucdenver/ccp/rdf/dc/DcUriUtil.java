package edu.ucdenver.ccp.rdf.dc;

import java.net.URI;

import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class DcUriUtil {
	public static final URI DC_HAS_VERSION = RdfUtil.createUri(RdfNamespace.DC, "hasVersion");
}
