package edu.ucdenver.ccp.rdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public abstract class UriFactory {

	public static final URI RDF_TYPE = RdfUtil.createUri(RdfNamespace.RDF, "type");
	public static final URI RDFS_SUBCLASS_OF = RdfUtil.createUri(RdfNamespace.RDFS, "subclassOf");

	/**
	 * Returns a URI for the input {@link DataElementIdentifier}
	 * 
	 * @param id
	 * @return
	 */
	public Collection<URI> getUri(Collection<? extends DataSourceIdentifier<?>> ids) {
		Collection<URI> uris = new ArrayList<URI>();
		for (DataSourceIdentifier<?> id : ids)
			uris.add(RdfUtil.createUri(RdfUtil.getNamespace(id.getDataSource()), id.toString()));
		return uris;
	}

	public abstract Collection<URI> getResourceUri(CCPTextAnnotation ccpTa);

	public abstract URI getAnnotationUri(long annotationId);

	/**
	 * @return
	 */
	public abstract String getBaseUri();
}
