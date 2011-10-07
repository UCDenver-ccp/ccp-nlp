/**
 * 
 */
package edu.ucdenver.ccp.rdf;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdfizer.rdf.RdfId;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DataSourceIdentifierUriFactory extends UriFactory {

	private static final String KABOB_ANNOTATION_NAMESPACE = RdfNamespace.KABOB.longName() + "annotations/annotation";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.rdf.UriFactory#getResourceUri(edu.ucdenver.ccp.nlp.core.uima.annotation.
	 * CCPTextAnnotation)
	 */
	@Override
	public URI getResourceUri(AnnotationDataExtractor annotationDataExtractor, Annotation annotation) {
		String type = annotationDataExtractor.getAnnotationType(annotation);
		return getUri(DataSourceIdResolver.resolveId(type));
	}

	/**
	 * Returns a URI for the input {@link DataElementIdentifier}
	 * 
	 * @param id
	 * @return
	 */
	public URI getIaoUri(DataSourceIdentifier<?> id) {
		return new URIImpl(RdfUtil.createIaoUri(RdfNamespace.KABOB, RdfUtil.getNamespace(id.getDataSource()),
				new RdfId(id).getICE_ID()).toString());
	}

	public static URI getUri(DataSourceIdentifier<?> id) {
		if (id.getDataSource().equals(DataSource.NCBI_TAXON))
			return new URIImpl(RdfUtil
					.createUri(RdfUtil.getNamespace(id.getDataSource()), "NCBITaxon_" + id.toString()).toString());
		else
			return new URIImpl(RdfUtil.createUri(RdfUtil.getNamespace(id.getDataSource()), id.toString()).toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.rdf.UriFactory#getAnnotationNamespace()
	 */
	@Override
	protected org.openrdf.model.URI getAnnotationNamespace() {
		return new URIImpl(KABOB_ANNOTATION_NAMESPACE);
	}

}
