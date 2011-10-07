/**
 * 
 */
package edu.ucdenver.ccp.rdf.ao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.DocumentRdfGenerator;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdf.pav.PavUriUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class AoDocumentRdfGenerator implements DocumentRdfGenerator {

	private URI documentUri = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.annotation.serialization.rdf.uima.DocumentRdfGenerator#generateRdf()
	 */
	@Override
	public Collection<Statement> generateRdf(URI documentUri, URL documentUrl) {
		this.documentUri = documentUri;
		Collection<Statement> stmts = new ArrayList<Statement>();

		/* documentUri pav:retrievedFrom documentUrl */
		URI pavRetrievedFrom = new URIImpl(PavUriUtil.PAV_RETRIEVED_FROM.toString());
//		documentUri = new URIImpl(uriFactory.getBaseUri() + String.format("document/%s", documentId));
		stmts.add(new StatementImpl(documentUri, pavRetrievedFrom, new URIImpl(documentUrl.toString())));

		/* documentUri rdf:type pav:SourceDocument */
		URI pavSourceDocument = new URIImpl(PavUriUtil.PAV_SOURCE_DOCUMENT.toString());
		stmts.add(new StatementImpl(documentUri, UriFactory.RDF_TYPE, pavSourceDocument));

		// /* documentUri pav:sourceAccessedOn date */
		// URI pavSourceAccessedOn = new URIImpl(PavUriUtil.PAV_SOURCE_ACCESSED_ON.toString());
		// XMLGregorianCalendar calendar;
		// try {
		// calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new
		// GregorianCalendar());
		// calendar.setYear(2007);
		// calendar.setMonth(Calendar.OCTOBER);
		// } catch (DatatypeConfigurationException e) {
		// throw new IllegalStateException("Error while generating calendar");
		// }
		// Value dateUri = new CalendarLiteralImpl(calendar);
		// stmts.add(new StatementImpl(documentUri, pavSourceAccessedOn, dateUri));

		return stmts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.DocumentRdfGenerator#getDocumentUri()
	 */
	@Override
	public URI getDocumentUri() {
		if (documentUri == null)
			throw new IllegalStateException("generateRdf() must be called prior to retrieving the documentUri!");
		return documentUri;
	}

}
