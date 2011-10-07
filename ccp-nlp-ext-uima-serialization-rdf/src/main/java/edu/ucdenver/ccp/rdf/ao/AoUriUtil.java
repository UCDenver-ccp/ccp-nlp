package edu.ucdenver.ccp.rdf.ao;

import java.net.URI;

import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public class AoUriUtil {
	
	public static final URI AOT_EXACT_QUALIFIER = RdfUtil.createUri(RdfNamespace.AOT, "ExactQualifier");
	public static final URI AO_HAS_TOPIC = RdfUtil.createUri(RdfNamespace.AO, "hasTopic");
	public static final URI AO_CONTEXT = RdfUtil.createUri(RdfNamespace.AO, "context");
	public static final URI AOS_EXACT = RdfUtil.createUri(RdfNamespace.AOS, "exact");
	public static final URI AOS_OFFSET = RdfUtil.createUri(RdfNamespace.AOS, "offset");
	public static final URI AOS_RANGE = RdfUtil.createUri(RdfNamespace.AOS, "range");
	public static final URI AOS_PREFIX = RdfUtil.createUri(RdfNamespace.AOS, "prefix");
	public static final URI AOS_POSTFIX = RdfUtil.createUri(RdfNamespace.AOS, "postfix");
	public static final URI AOF_ANNOTATES_DOCUMENT = RdfUtil.createUri(RdfNamespace.AOF,"annotatesDocument");
	public static final URI AOF_ON_DOCUMENT = RdfUtil.createUri(RdfNamespace.AOF,"onDocument");
	public static final URI AO_ON_SOURCE_DOCUMENT = RdfUtil.createUri(RdfNamespace.AO,"onSourceDocument");
	public static final URI AOS_PREFIX_POSTFIX_TEXT_SELECTOR = RdfUtil.createUri(RdfNamespace.AOS,"PrefixPostfixSelector");
	public static final URI AOS_OFFSET_RANGE_TEXT_SELECTOR = RdfUtil.createUri(RdfNamespace.AOS,"OffsetRangeSelector");
	public static final URI AO_HAS_BODY = RdfUtil.createUri(RdfNamespace.AO,"hasBody");
}
