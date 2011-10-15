package edu.ucdenver.ccp.rdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdfizer.rdf.RdfId;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

public abstract class UriFactory {

	public static final URI RDF_TYPE = new URIImpl(RdfUtil.createUri(RdfNamespace.RDF, "type").toString());
	public static final URI RDFS_SUBCLASS_OF = new URIImpl(RdfUtil.createUri(RdfNamespace.RDFS, "subclassOf")
			.toString());
	
	
	public static final URI KIAO_MENTIONS_PROTEIN = new URIImpl(RdfUtil.createUri(RdfNamespace.KIAO, "mentionsProtein")
			.toString());
	public static final URI KIAO_MENTIONS_PATHWAY = new URIImpl(RdfUtil.createUri(RdfNamespace.KIAO, "mentionsPathway")
			.toString());
	public static final URI IAO_MENTIONS = new URIImpl(RdfUtil.createUri(RdfNamespace.IAO, "mentions")
			.toString());
	

//	/**
//	 * Returns a URI for the input {@link DataElementIdentifier}
//	 * 
//	 * @param id
//	 * @return
//	 */
//	public Collection<URI> getIaoUri(Collection<? extends DataSourceIdentifier<?>> ids) {
//		Collection<URI> uris = new ArrayList<URI>();
//		for (DataSourceIdentifier<?> id : ids)
//			uris.add(new URIImpl(RdfUtil.createIaoUri(RdfNamespace.KABOB, RdfUtil.getNamespace(id.getDataSource()),
//					new RdfId(id).getICE_ID()).toString()));
//		// uris.add(RdfUtil.createUri(RdfUtil.getNamespace(id.getDataSource()), id.toString()));
//		return uris;
//	}
//
//	public Collection<URI> getUri(Collection<? extends DataSourceIdentifier<?>> ids) {
//		Collection<URI> uris = new ArrayList<URI>();
//		for (DataSourceIdentifier<?> id : ids)
//			if (id.getDataSource().equals(DataSource.NCBI_TAXON))
//				uris.add(new URIImpl(RdfUtil.createUri(RdfUtil.getNamespace(id.getDataSource()),
//						"NCBITaxon_" + id.toString()).toString()));
//			else
//				uris.add(new URIImpl(RdfUtil.createUri(RdfUtil.getNamespace(id.getDataSource()), id.toString())
//						.toString()));
//
//		return uris;
//	}

	/**
	 * An annotation can only denote a single class. If an annotation represents more than one class
	 * then it must be split into multiple annotations.
	 * 
	 * @param annotationDataExtractor
	 * @param annotation
	 * @return
	 */
	public abstract URI getResourceUri(AnnotationDataExtractor annotationDataExtractor, Annotation annotation);

	// public abstract URI getAnnotationUri(String localNamePrefix, long annotationId);

	public URI getAnnotationUri() {
		return new URIImpl(getAnnotationNamespace() + UUID.randomUUID().toString());
	}

	protected abstract URI getAnnotationNamespace();

}
