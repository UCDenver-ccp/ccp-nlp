/**
 * 
 */
package edu.ucdenver.ccp.rdf.ao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.string.CodePointUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.AnnotationRdfGenerator;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.RdfAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationCreatorExtractor.Annotator;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.UriFactory;
import edu.ucdenver.ccp.rdf.dc.DcUriUtil;
import edu.ucdenver.ccp.rdf.pav.PavUriUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class AoAnnotationRdfGenerator implements AnnotationRdfGenerator {

	public static class AoAnnotationOffsetRangeRdfGenerator extends AoAnnotationRdfGenerator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.ucdenver.ccp.rdf.ao.AoAnnotationRdfGenerator#getAoSelectorType()
		 */
		@Override
		protected AoSelectorType getAoSelectorType() {
			return AoSelectorType.OFFSET_RANGE_TEXT_SELECTOR;
		}

	}

	public static class AoAnnotationPrefixPostfixRdfGenerator extends AoAnnotationRdfGenerator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.ucdenver.ccp.rdf.ao.AoAnnotationRdfGenerator#getAoSelectorType()
		 */
		@Override
		protected AoSelectorType getAoSelectorType() {
			return AoSelectorType.PREFIX_POSTFIX_TEXT_SELECTOR;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.annotation.serialization.rdf.uima.AnnotationRdfGenerator#generateRdf(edu
	 * .ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor,
	 * org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public Collection<? extends Statement> generateRdf(RdfAnnotationDataExtractor annotationDataExtractor,
			Annotation annotation, UriFactory uriFactory, URI documentUri, URL documentUrl, String documentText,
			@SuppressWarnings("unused") Map<String, URI> annotationKeyToUriMap,
			@SuppressWarnings("unused") Map<String, Set<URI>> annotationKeyToSelectorUrisMap,
			@SuppressWarnings("unused") Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
		Collection<Statement> stmts = new ArrayList<Statement>();

		if (annotationDataExtractor.getComponentAnnotations(annotation).size() > 0)
			throw new IllegalArgumentException(
					"The AO format does not currently handle complex annotation types, i.e. annotations "
							+ "that are comprised of other annotations. A complex annotation type was observed.");

		URI denotedClass = uriFactory.getResourceUri(annotationDataExtractor, annotation);
		URI annotationUri = uriFactory.getAnnotationUri();
		stmts.add(setAnnotationTypeExactQualifer(annotationUri));
		stmts.add(setAnnotationTopic(annotationUri, denotedClass));
		stmts.addAll(linkAnnotationToSourceDocument(annotationUri, documentUrl, documentUri));
		stmts.addAll(linkAnnotationToCreator(annotationUri, annotationDataExtractor.getAnnotator(annotation)));
		stmts.add(linkAnnotationToCreatedDate(annotationUri));

		/* annotationUri ao:context selectorUris */
		stmts.addAll(getAoSelectorStmts(annotationUri, annotation, annotationDataExtractor, documentUri, documentUrl,
				documentText, getAoSelectorType(), annotationKeyToSelectorUrisMap));

		return stmts;
	}

	/**
	 * @return
	 */
	protected abstract AoSelectorType getAoSelectorType();

	/**
	 * Returns a statement of the form -- annotationUri pav:createdOn date
	 * 
	 * @param annotationUri
	 * @return
	 */
	public static Statement linkAnnotationToCreatedDate(URI annotationUri) {
		URI createdOn = new URIImpl(PavUriUtil.PAV_CREATED_ON.toString());
		XMLGregorianCalendar calendar;
		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Error while generating calendar");
		}
		Value dateUri = new CalendarLiteralImpl(calendar);
		return new StatementImpl(annotationUri, createdOn, dateUri);
	}

	/**
	 * Returns two statements of the form -- <br>
	 * annotationUri pav:createdBy annotatorUri<br>
	 * annotatorUri dc:hasVersion literal:annotator-version
	 * 
	 * TODO: dc:hasVersion is not recommended for use with literals - find an alternative<br>
	 * http://www.dublincore.org/documents/dcmi-terms/#terms-hasVersion<br>
	 * "This term is intended to be used with non-literal values as defined in the DCMI Abstract
	 * Model (http://dublincore.org/documents/abstract-model/)."
	 * 
	 * @param annotationUri
	 * @param annotator
	 * @return
	 */
	public static Collection<Statement> linkAnnotationToCreator(URI annotationUri, Annotator annotator) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		/* annotationUri pav:createdBy annotatorUri */
		URI createdBy = new URIImpl(PavUriUtil.PAV_CREATED_BY.toString());
		stmts.add(new StatementImpl(annotationUri, createdBy, new URIImpl(annotator.getAnnotatorUri().toString())));
		// /* annotatorUri dc:hasVersion literal:annotator-version */
		// stmts.add(new StatementImpl(new URIImpl(annotator.getAnnotatorUri().toString()), new
		// URIImpl(
		// DcUriUtil.DC_HAS_VERSION.toString()), new LiteralImpl(annotator.getVersion())));
		return stmts;
	}

	/**
	 * Returns a statement of the form -- annotationUri rdf:type aot:ExactQualifier
	 * 
	 * @param annotationUri
	 * @return
	 */
	public static Statement setAnnotationTypeExactQualifer(URI annotationUri) {
		Value aotExactQualifier = new URIImpl(AoUriUtil.AOT_EXACT_QUALIFIER.toString());
		return new StatementImpl(annotationUri, UriFactory.RDF_TYPE, aotExactQualifier);
	}

	/**
	 * Returns a statement of the form -- annotationUri ao:hasTopic denotedClass
	 * 
	 * @param annotationUri
	 * @param denotedClass
	 * @return
	 */
	public static Statement setAnnotationTopic(URI annotationUri, URI denotedClass) {
		URI hasTopic = new URIImpl(AoUriUtil.AO_HAS_TOPIC.toString());
		return new StatementImpl(annotationUri, hasTopic, denotedClass);
	}

	/**
	 * Returns two statements of the form -- <br>
	 * annotationUri aof:annotatesDocument documentURL<br>
	 * annotationUri ao:onSourceDocument documentUri
	 * 
	 * @param annotationUri
	 * @param documentUrl
	 *            a valid URL that displays the document via HTTP
	 * @param documentUri
	 *            the URI used to represent the document
	 * 
	 * @return
	 */
	public static Collection<Statement> linkAnnotationToSourceDocument(URI annotationUri, URL documentUrl,
			URI documentUri) {
		Collection<Statement> stmts = new ArrayList<Statement>();

		/* annotationUri aof:annotatesDocument documentURL */
		URI annotatesDocument = new URIImpl(AoUriUtil.AOF_ANNOTATES_DOCUMENT.toString());
		stmts.add(new StatementImpl(annotationUri, annotatesDocument, new URIImpl(documentUrl.toString())));

		/* annotationUri ao:onSourceDocument documentUri */
		URI onSourceDocument = new URIImpl(AoUriUtil.AO_ON_SOURCE_DOCUMENT.toString());
		stmts.add(new StatementImpl(annotationUri, onSourceDocument, documentUri));

		return stmts;
	}

	/**
	 * @param uriFactory
	 * @param annotationUri
	 * @param annotation
	 * @param annotationDataExtractor
	 * @param annotationId
	 * @param documentUri
	 * @param documentURL
	 * @param documentText
	 * @param selectorType
	 * @param annotationUriLocalNamePrefix
	 * @return
	 */
	public static Collection<? extends Statement> getAoSelectorStmts(URI annotationUri, Annotation annotation,
			AnnotationDataExtractor annotationDataExtractor, URI documentUri, URL documentURL, String documentText,
			AoSelectorType selectorType, Map<String, Set<URI>> annotationKeyToSelectorUrisMap) {
		String annotationKey = annotationDataExtractor.getAnnotationKey(annotation);
		Collection<Statement> stmts = new ArrayList<Statement>();
		List<Span> spans = annotationDataExtractor.getAnnotationSpans(annotation);
		int selectorCount = 0;
		for (Span span : spans) {
			/* annotationUri ao:context selectorUri */
			URI hasContext = new URIImpl(AoUriUtil.AO_CONTEXT.toString());
			URI selectorUri = new URIImpl(annotationUri.toString() + String.format("-selector-%d", selectorCount++));
			stmts.add(new StatementImpl(annotationUri, hasContext, selectorUri));
			CollectionsUtil.addToOne2ManyUniqueMap(annotationKey, selectorUri, annotationKeyToSelectorUrisMap);

			/* selectorUri ao:onSourceDocument documentUri */
			URI onSourceDocument = new URIImpl(AoUriUtil.AO_ON_SOURCE_DOCUMENT.toString());
			stmts.add(new StatementImpl(selectorUri, onSourceDocument, documentUri));

			/* selectorUri aof:onDocument documentURL */
			URI onDocument = new URIImpl(AoUriUtil.AOF_ON_DOCUMENT.toString());
			stmts.add(new StatementImpl(selectorUri, onDocument, new URIImpl(documentURL.toString())));

			switch (selectorType) {
			case PREFIX_POSTFIX_TEXT_SELECTOR:
				stmts.addAll(createPrefixPostfixTextSelectorStatements(selectorUri, span, documentText));
				break;
			case OFFSET_RANGE_TEXT_SELECTOR:
				stmts.addAll(createOffsetRangeTextSelectorStatements(selectorUri, span, documentText));
				break;
			default:
				throw new IllegalArgumentException("Unhandled AO Selector type: " + selectorType.name());
			}

		}
		return stmts;
	}

	/**
	 * @param documentText
	 * @param span
	 * @param selectorUri
	 * @return
	 */
	private static Collection<? extends Statement> createOffsetRangeTextSelectorStatements(URI selectorUri, Span span,
			String documentText) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		/* selectorUri rdf:type aos:PrefixPostfixTextSelector */
		Value offsetRangeSelectorType = new URIImpl(AoUriUtil.AOS_OFFSET_RANGE_TEXT_SELECTOR.toString());
		stmts.add(new StatementImpl(selectorUri, UriFactory.RDF_TYPE, offsetRangeSelectorType));

		String coveredText;
		try {
			coveredText = documentText.substring(span.getSpanStart(), span.getSpanEnd());
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: This error indicates an annotation outside the span of the document text (I
			// think). Once CRAFT QA is complete, this try/catch should be removed. We want an
			// exception thrown here if this happens.
			coveredText = "";
		}

		/* selectorUri aos:exact coveredText */
		URI aosExact = new URIImpl(AoUriUtil.AOS_EXACT.toString());
		Value coveredTextLiteral = new LiteralImpl(coveredText);
		stmts.add(new StatementImpl(selectorUri, aosExact, coveredTextLiteral));

		/* selectorUri aos:offset offset */
		URI aosOffset = new URIImpl(AoUriUtil.AOS_OFFSET.toString());
		Value offsetLiteral = new NumericLiteralImpl(getSpanCodePointStart(span, documentText));
		stmts.add(new StatementImpl(selectorUri, aosOffset, offsetLiteral));

		/* selectorUri aos:range range */
		URI aosRange = new URIImpl(AoUriUtil.AOS_RANGE.toString());
		Value rangeLiteral = new NumericLiteralImpl(getSpanCodePointRange(span, documentText));
		stmts.add(new StatementImpl(selectorUri, aosRange, rangeLiteral));

		return stmts;
	}

	/**
	 * @param span
	 * @param documentText
	 * @return
	 */
	private static int getSpanCodePointRange(Span span, String documentText) {
		try {
			// TODO: This error indicates an annotation outside the span of the document text (I
			// think). Once CRAFT QA is complete, this try/catch should be removed. We want an
			// exception thrown here if this happens.
			int codePointStart = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText,
					span.getSpanStart());
			int codePointEnd = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, span.getSpanEnd());
			return codePointEnd - codePointStart;
		} catch (IndexOutOfBoundsException e) {
			// TODO: This error indicates an annotation outside the span of the document text (I
			// think). Once CRAFT QA is complete, this try/catch should be removed. We want an
			// exception thrown here if this happens.
			return 0;
		}
	}

	/**
	 * @param span
	 * @param documentText
	 * @return
	 */
	private static int getSpanCodePointStart(Span span, String documentText) {
		try {
			return CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, span.getSpanStart());
		} catch (IndexOutOfBoundsException e) {
			// TODO: This error indicates an annotation outside the span of the document text (I
			// think). Once CRAFT QA is complete, this try/catch should be removed. We want an
			// exception thrown here if this happens.
			return 0;
		}
	}

	/**
	 * @param selectorUri
	 * @return
	 */
	private static Collection<? extends Statement> createPrefixPostfixTextSelectorStatements(Resource selectorUri,
			Span span, String documentText) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		/* selectorUri rdf:type aos:PrefixPostfixTextSelector */
		Value prefixPostfixSelectorType = new URIImpl(AoUriUtil.AOS_PREFIX_POSTFIX_TEXT_SELECTOR.toString());
		stmts.add(new StatementImpl(selectorUri, UriFactory.RDF_TYPE, prefixPostfixSelectorType));

		String[] prefixExactPostfix;
		try {
			prefixExactPostfix = getSpanCoveredText(span, documentText);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: This error indicates an annotation outside the span of the document text (I
			// think). Once CRAFT QA is complete, this try/catch should be removed. We want an
			// exception thrown here if this happens.
			prefixExactPostfix = new String[] { "", "", "" };
		}

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

		return stmts;
	}

	private static String[] getSpanCoveredText(Span span, String documentText) {
		String coveredTextForSpan = documentText.substring(span.getSpanStart(), span.getSpanEnd());

		int infixLength = 50;

		String prefix, postfix;

		int spanCodePointStart = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText,
				span.getSpanStart());
		if (spanCodePointStart > infixLength)
			prefix = CodePointUtil.substringByCodePoint(documentText, spanCodePointStart - infixLength,
					spanCodePointStart);
		else
			prefix = CodePointUtil.substringByCodePoint(documentText, 0, spanCodePointStart);

		int spanCodePointEnd = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText, span.getSpanEnd());
		int documentCodePointEnd = CodePointUtil.convertCharacterOffsetToCodePointOffset(documentText,
				documentText.length());
		if (spanCodePointEnd < documentCodePointEnd - infixLength)
			postfix = CodePointUtil
					.substringByCodePoint(documentText, spanCodePointEnd, spanCodePointEnd + infixLength);
		else
			postfix = CodePointUtil.substringByCodePoint(documentText, spanCodePointEnd, documentCodePointEnd);

		return new String[] { prefix.replaceAll("\\n", " "), coveredTextForSpan.replaceAll("\\n", " "),
				postfix.replaceAll("\\n", " ") };
	}

}
