package edu.ucdenver.ccp.rdf.pav;

import java.net.URI;

import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class PavUriUtil {
	public static final URI PAV_CREATED_BY = RdfUtil.createUri(RdfNamespace.PAV, "createdBy");
	public static final URI PAV_CREATED_ON = RdfUtil.createUri(RdfNamespace.PAV, "createdOn");
	public static final URI PAV_RETRIEVED_FROM = RdfUtil.createUri(RdfNamespace.PAV, "retrievedFrom");
	public static final URI PAV_SOURCE_DOCUMENT = RdfUtil.createUri(RdfNamespace.PAV, "SourceDocument");
	public static final URI PAV_SOURCE_ACCESSED_ON = RdfUtil.createUri(RdfNamespace.PAV, "sourceAccessedOn");;
}
