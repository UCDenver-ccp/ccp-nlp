/**
 * 
 */
package edu.ucdenver.ccp.rdf.ao.ext;

import java.net.URI;

import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class AoExtUriUtil {

	
	public static final URI KIAO_RESOURCE_ANNOTATION = RdfUtil.createUri(RdfNamespace.KIAO, "ResourceAnnotation");
	public static final URI KIAO_STATEMENTSET_ANNOTATION = RdfUtil.createUri(RdfNamespace.KIAO, "StatementSetAnnotation");
	
	public static final URI KIAO_MENTIONS_STATEMENT = RdfUtil.createUri(RdfNamespace.KIAO, "mentionsStatement");
	public static final URI KIAO_DENOTES_RESOURCE = RdfUtil.createUri(RdfNamespace.KIAO, "denotesResource");
	public static final URI KIAO_BASED_ON = RdfUtil.createUri(RdfNamespace.KIAO, "basedOn");;
	
	
}
