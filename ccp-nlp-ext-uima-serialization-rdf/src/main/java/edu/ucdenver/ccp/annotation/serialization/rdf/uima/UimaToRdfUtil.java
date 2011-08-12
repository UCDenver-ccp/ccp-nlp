package edu.ucdenver.ccp.annotation.serialization.rdf.uima;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.string.CodePointUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.rdf.CcpUriUtil;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdf.ao.AoUriUtil;
import edu.ucdenver.ccp.rdf.pav.PavUriUtil;

public class UimaToRdfUtil {

	public static final URI rdfType = new URIImpl(UriFactory.RDF_TYPE.toString());
	
	public static Collection<Statement> generateAnnotationRdf(UriFactory uriFactory, URI denotedClass, CCPTextAnnotation ccpTa, long annotationId, String documentId, URI documentUrl, URI documentUri, String documentText) {
		Collection<Statement> stmts = new ArrayList<Statement>();

		/* annotationUri rdf:type aot:ExactQualifier */
		Resource annotationUri = new URIImpl(uriFactory.getAnnotationUri(annotationId).toString());
		Value aotExactQualifier = new URIImpl(AoUriUtil.AOT_EXACT_QUALIFIER.toString());
		stmts.add(new StatementImpl(annotationUri, rdfType, aotExactQualifier));

		/* annotationUri ao:onTopic denotedClass */
		URI hasTopic = new URIImpl(AoUriUtil.AO_HAS_TOPIC.toString());
//		Value denotedClass = new URIImpl(uriFactory.getResourceUri(ccpTa).toString());
		stmts.add(new StatementImpl(annotationUri, hasTopic, denotedClass));
		
		/* annotationUri aof:annotatesDocument documentURL */
		URI annotatesDocument = new URIImpl(AoUriUtil.AOF_ANNOTATES_DOCUMENT.toString());
		stmts.add(new StatementImpl(annotationUri, annotatesDocument, documentUrl));
		
		/* annotationUri ao:onSourceDocument documentUri */
		URI onSourceDocument = new URIImpl(AoUriUtil.AO_ON_SOURCE_DOCUMENT.toString());
		stmts.add(new StatementImpl(annotationUri, onSourceDocument, documentUri));
		
		/* annotationUri pav:createdBy ccp-foaf-organization-uri */
		URI createdBy = new URIImpl(PavUriUtil.PAV_CREATED_BY.toString());
		Value ccpUri = new URIImpl(CcpUriUtil.CCP_URI.toString());
		stmts.add(new StatementImpl(annotationUri, createdBy, ccpUri));
		
		/* annotationUri pav:createdOn date */
		URI createdOn = new URIImpl(PavUriUtil.PAV_CREATED_ON.toString());
		XMLGregorianCalendar calendar;
		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Error while generating calendar");
		}
		Value dateUri = new CalendarLiteralImpl(calendar);
		stmts.add(new StatementImpl(annotationUri, createdOn, dateUri));
		
		/* annotationUri ao:context selectorUris */
		stmts.addAll(getAoSelectorStmts(uriFactory, annotationUri, ccpTa, annotationId, documentUri, documentUrl, documentText));
		
		return stmts;
	}
	
	
	private static Collection<? extends Statement> getAoSelectorStmts(UriFactory uriFactory,
			Resource annotationUri, CCPTextAnnotation ccpTa, long annotationId, URI documentUri, Value documentURL, String documentText) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		for (int i = 0; i < ccpTa.getSpans().size(); i++) {
			/* annotationUri ao:context selectorUri */
			URI hasContext = new URIImpl(AoUriUtil.AO_CONTEXT.toString());
			URI selectorUri = new URIImpl(uriFactory.getBaseUri() + String.format("annotation/selector%d-%d",annotationId, i));
			stmts.add(new StatementImpl(annotationUri, hasContext, selectorUri));
			
			/* selectorUri ao:onSourceDocument documentUri */
			URI onSourceDocument = new URIImpl(AoUriUtil.AO_ON_SOURCE_DOCUMENT.toString());
			stmts.add(new StatementImpl(selectorUri, onSourceDocument, documentUri));
			
			/* selectorUri aof:onDocument documentURL */
			URI onDocument = new URIImpl(AoUriUtil.AOF_ON_DOCUMENT.toString());
			stmts.add(new StatementImpl(selectorUri, onDocument, documentURL));
			
			/* selectorUri rdf:type aos:PrefixPostfixTextSelector */
			Value prefixPostfixSelectorType = new URIImpl(AoUriUtil.AOS_PREFIX_POSTFIX_TEXT_SELECTOR.toString());
			stmts.add(new StatementImpl(selectorUri, rdfType, prefixPostfixSelectorType));

			
			CCPSpan span = (CCPSpan) ccpTa.getSpans().get(i);
			String[] prefixExactPostfix = getSpanCoveredText(span, documentText);

			/* selectorUri aos:exact coveredText */
			URI aosExact = new URIImpl(AoUriUtil.AOS_EXACT.toString());
			Value coveredTextLiteral = new LiteralImpl(prefixExactPostfix[1]);
			stmts.add(new StatementImpl(selectorUri, aosExact, coveredTextLiteral));
			
			/* selectorUri aos:postfix postfix */
			URI aosPrefix = new URIImpl(AoUriUtil.AOS_PREFIX.toString());
			Value prefixLiteral = new LiteralImpl(prefixExactPostfix[0]);
			stmts.add(new StatementImpl(selectorUri, aosPrefix, prefixLiteral));
			
			/* selectorUri aos:postfix postfix */
			URI aosPostfix = new URIImpl(AoUriUtil.AOS_POSTFIX.toString());
			Value postfixLiteral = new LiteralImpl(prefixExactPostfix[2]);
			stmts.add(new StatementImpl(selectorUri, aosPostfix, postfixLiteral));
			
		}
		return stmts;
	}


	private static String[] getSpanCoveredText(CCPSpan span, String documentText) {
		String coveredTextForSpan = documentText.substring(span.getSpanStart(), span.getSpanEnd());
		
		int infixLength = 50;
		
		String prefix, postfix;
		
		int spanCodePointStart = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, span.getSpanStart());
		if (spanCodePointStart > infixLength)
			prefix = CodePointUtil.substringByCodePoint(documentText, spanCodePointStart - infixLength, spanCodePointStart);
		else
			prefix = CodePointUtil.substringByCodePoint(documentText, 0, spanCodePointStart);
		
		int spanCodePointEnd = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, span.getSpanEnd());
		int documentCodePointEnd = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, documentText.length());
		if (spanCodePointEnd < documentCodePointEnd-infixLength)
			postfix = CodePointUtil.substringByCodePoint(documentText, spanCodePointEnd, spanCodePointEnd + infixLength);
		else
			postfix = CodePointUtil.substringByCodePoint(documentText, spanCodePointEnd, documentCodePointEnd);
		
		return new String[] {prefix.replaceAll("\\n", " "), coveredTextForSpan.replaceAll("\\n", " "), postfix.replaceAll("\\n", " ")};
	}


	public static Map<URI, Collection<Statement>> generateDocumentRdf(UriFactory uriFactory, String documentId, java.net.URI documentUrl) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		
		/* documentUri pav:retrievedFrom documentUrl */
		URI pavRetrievedFrom = new URIImpl(PavUriUtil.PAV_RETRIEVED_FROM.toString());
		URI documentUri = new URIImpl(uriFactory.getBaseUri() + String.format("document/%s",documentId));
		stmts.add(new StatementImpl(documentUri, pavRetrievedFrom, new URIImpl(documentUrl.toString())));
		
		/* documentUri rdf:type pav:SourceDocument */
		URI pavSourceDocument = new URIImpl(PavUriUtil.PAV_SOURCE_DOCUMENT.toString());
		stmts.add(new StatementImpl(documentUri, rdfType, pavSourceDocument));
		
//		/* documentUri pav:sourceAccessedOn date */
//		URI pavSourceAccessedOn = new URIImpl(PavUriUtil.PAV_SOURCE_ACCESSED_ON.toString());
//		XMLGregorianCalendar calendar;
//		try {
//			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
//			calendar.setYear(2007);
//			calendar.setMonth(Calendar.OCTOBER);
//		} catch (DatatypeConfigurationException e) {
//			throw new IllegalStateException("Error while generating calendar");
//		}
//		Value dateUri = new CalendarLiteralImpl(calendar);
//		stmts.add(new StatementImpl(documentUri, pavSourceAccessedOn, dateUri));
		
		
		Map<URI, Collection<Statement>> returnMap = new HashMap<URI, Collection<Statement>>();
		returnMap.put(documentUri, stmts);
		return returnMap;
	}
	
	
}
