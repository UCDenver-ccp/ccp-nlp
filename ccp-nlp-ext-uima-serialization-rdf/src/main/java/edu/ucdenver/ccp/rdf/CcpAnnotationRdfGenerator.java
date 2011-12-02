/**
 * 
 */
package edu.ucdenver.ccp.rdf;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.AnnotationRdfGenerator;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf.shims.RdfAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.ao.AoAnnotationRdfGenerator;
import edu.ucdenver.ccp.rdf.ao.AoSelectorType;
import edu.ucdenver.ccp.rdf.ao.AoUriUtil;
import edu.ucdenver.ccp.rdf.ao.ext.AoExtUriUtil;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class CcpAnnotationRdfGenerator implements AnnotationRdfGenerator {

	private static final Logger logger = Logger.getLogger(CcpAnnotationRdfGenerator.class);

	private static final URI AO_CONTEXT = new URIImpl(AoUriUtil.AO_CONTEXT.toString());
	private static final URI AO_HAS_BODY = new URIImpl(AoUriUtil.AO_HAS_BODY.toString());

	public static class CcpAnnotationOffsetRangeRdfGenerator extends CcpAnnotationRdfGenerator {

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

	public static class CcpAnnotationPrefixPostfixRdfGenerator extends CcpAnnotationRdfGenerator {

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

	/**
	 * @return
	 */
	protected abstract AoSelectorType getAoSelectorType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.annotation.serialization.rdf.uima.AnnotationRdfGenerator#generateRdf(org
	 * .apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<? extends Statement> generateRdf(RdfAnnotationDataExtractor annotationDataExtractor,
			Annotation annotation, UriFactory uriFactory, URI documentUri, URL documentUrl, String documentText,
			Map<String, URI> annotationKeyToUriMap, Map<String, Set<URI>> annotationKeyToSelectorUrisMap,
			Map<String, URI> annotationKeyToSemanticInstanceUriMap) {

		List<Statement> stmts = new ArrayList<Statement>();
		String annotationKey = annotationDataExtractor.getAnnotationKey(annotation);
		if (!annotationKeyToUriMap.containsKey(annotationKey)) {

			URI denotedClassUri = uriFactory.getResourceUri(annotationDataExtractor, annotation);
			if (denotedClassUri != null) {
				URI annotationUri = uriFactory.getAnnotationUri();

				stmts.add(setAnnotationTypeResourceAnnotation(annotationUri));
				stmts.add(setAnnotationDenotedResource(annotationUri, denotedClassUri));
				stmts.addAll(AoAnnotationRdfGenerator.linkAnnotationToSourceDocument(annotationUri, documentUrl,
						documentUri));
				stmts.addAll(AoAnnotationRdfGenerator.linkAnnotationToCreator(annotationUri,
						annotationDataExtractor.getAnnotator(annotation)));
				stmts.add(AoAnnotationRdfGenerator.linkAnnotationToCreatedDate(annotationUri));

				stmts.add(createDocumentToDenotedClassShortcutLink(documentUri, denotedClassUri));

				annotationKeyToUriMap.put(annotationDataExtractor.getAnnotationKey(annotation), annotationUri);

				/* annotationUri kiao:based_on annotationUri */
				Collection<? extends Statement> componentAnnotationStmts = getComponentAnnotationStmts(annotationUri,
						annotationDataExtractor, annotation, uriFactory, documentUri, documentUrl, documentText,
						annotationKeyToUriMap, annotationKeyToSelectorUrisMap, annotationKeyToSemanticInstanceUriMap);

				/* ADD SPANS FROM COMPONENT ANNOTATIONS TO THIS ANNOTATION */
				// stmts.addAll(linkAnnotationToComponentSpans(annotationUri,
				// componentAnnotationStmts));
				/* annotationUri ao:context selectorUris */
				stmts.addAll(AoAnnotationRdfGenerator.getAoSelectorStmts(annotationUri, annotation,
						annotationDataExtractor, documentUri, documentUrl, documentText, getAoSelectorType(),
						annotationKeyToSelectorUrisMap));

				stmts.addAll(linkAnnotationToComponentSpans(annotationUri, annotation, annotationDataExtractor,
						annotationKeyToSelectorUrisMap));

				stmts.addAll(generateAnnotationSemanticsStmts(annotationUri, annotation, annotationDataExtractor,
						annotationKeyToSemanticInstanceUriMap));

				stmts.addAll(componentAnnotationStmts);
			} else {
				logger.warn("Skipped RDF generation for an annotation. Unable to determine proper URI for annotation of type: "
						+ annotationDataExtractor.getAnnotationType(annotation));
			}
		}

		return stmts;
	}

	private static final Set<String> bionlpEventGoTerms = CollectionsUtil.createSet("GO_0008104", "GO_0065007",
			"GO_0010629", "GO_0010628", "GO_0010468", "GO_0010467", "GO_0032880",

			"GO_0006351", "GO_0006355", "GO_0045893", "GO_0045892",

			"GO_0030163", "GO_0009894", "GO_0009896", "GO_0009895",

			"GO_0005488", "GO_0051098", "GO_0051099", "GO_0051100",

			"GO_0016310", "GO_0042325", "GO_0042327", "GO_0042326");

	/**
	 * Adds a "shortcut" link from the document to the denoted class. This was added to improve
	 * query performance when using biojigsaw. TODO -- this is BTRC demo specific
	 * 
	 * @param documentUri
	 * @param denotedClassUri
	 * @return
	 */
	private Statement createDocumentToDenotedClassShortcutLink(URI documentUri, URI denotedClassUri) {
		URI mentionsPredicate;
		String localName = denotedClassUri.getLocalName();
		if (isBioNlpEvent(localName))
			mentionsPredicate = UriFactory.KIAO_MENTIONS_EVENT;
		else if (localName.contains("PR_") || localName.contains("GO_") || localName.contains("CHEBI_")
				|| localName.contains("SO_") || localName.contains("MOD_"))
			mentionsPredicate = UriFactory.KIAO_MENTIONS_PROTEIN;
		else if (localName.contains("KEGG_PATHWAY_"))
			mentionsPredicate = UriFactory.KIAO_MENTIONS_PATHWAY;
		else
			mentionsPredicate = UriFactory.IAO_MENTIONS;

		return new StatementImpl(documentUri, mentionsPredicate, denotedClassUri);
	}

	/**
	 * @param localName
	 * @return
	 */
	private boolean isBioNlpEvent(String localName) {
		for (String eventClass : bionlpEventGoTerms) {
			if (localName.contains(eventClass))
				return true;
		}
		return false;
	}

	/**
	 * @param annotationUri
	 * @param annotation
	 * @param annotationDataExtractor
	 * @return
	 */
	private Collection<? extends Statement> generateAnnotationSemanticsStmts(URI annotationUri, Annotation annotation,
			RdfAnnotationDataExtractor annotationDataExtractor, Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		URI graphUri = createGraphUri(annotationUri);
		if (annotationDataExtractor.getSemanticStatementGenerator() == null)
			logger.warn("The RdfAnnotationDataExtractor implementation being used lacks a non-null SemanticStatementGenerator. No semantic statements will be added to the annotation-specific graph.");
		else
			stmts.addAll(annotationDataExtractor.getSemanticStatements(annotation, graphUri,
					annotationKeyToSemanticInstanceUriMap));
		if (stmts.size() > 0) {
			stmts.add(new StatementImpl(annotationUri, AO_HAS_BODY, graphUri));
		}
		return stmts;
	}

	/**
	 * Returns a URI representing the graph that will store the semantics of the annotation.
	 * 
	 * @param annotationUri
	 * @return
	 */
	private URI createGraphUri(URI annotationUri) {
		return new URIImpl(annotationUri.toString() + "-semantics-graph");
	}

	/**
	 * @param annotationUri
	 * @param annotation
	 * @param annotationDataExtractor
	 * @param annotationKeyToSelectorUrisMap
	 * @return
	 */
	private Collection<? extends Statement> linkAnnotationToComponentSpans(URI annotationUri, Annotation annotation,
			AnnotationDataExtractor annotationDataExtractor, Map<String, Set<URI>> annotationKeyToSelectorUrisMap) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		for (Annotation componentAnnotation : annotationDataExtractor.getComponentAnnotations(annotation)) {
			String annotationKey = annotationDataExtractor.getAnnotationKey(componentAnnotation);
			if (!annotationKeyToSelectorUrisMap.containsKey(annotationKey))
				throw new IllegalArgumentException(
						"Selector URIs for a component annotation are not present when they should be. This should not be possible.");
			for (URI selectorUri : annotationKeyToSelectorUrisMap.get(annotationKey)) {
				/* annotationUri ao:context selectorUri */
				stmts.add(new StatementImpl(annotationUri, AO_CONTEXT, selectorUri));
			}
		}
		return stmts;
	}

	/**
	 * Searches the input Collection of statements for span
	 * 
	 * @param annotationUri
	 * @param componentAnnotationStmts
	 * @return
	 */
	private Collection<? extends Statement> linkAnnotationToComponentSpans(URI annotationUri,
			Collection<? extends Statement> componentAnnotationStmts) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a statement of the form -- annotationUri kiao:denotesResource denotedClass
	 * 
	 * @param annotationUri
	 * @param denotedClass
	 * @return
	 */
	public static Statement setAnnotationDenotedResource(URI annotationUri, URI denotedClass) {
		URI denotesResource = new URIImpl(AoExtUriUtil.KIAO_DENOTES_RESOURCE.toString());
		return new StatementImpl(annotationUri, denotesResource, denotedClass);
	}

	/**
	 * Returns a statement of the form -- annotationUri rdf:type kiao:ResourceAnnotation
	 * 
	 * @param annotationUri
	 * @return
	 */
	public static Statement setAnnotationTypeResourceAnnotation(URI annotationUri) {
		Value kiaoResourceAnnotation = new URIImpl(AoExtUriUtil.KIAO_RESOURCE_ANNOTATION.toString());
		return new StatementImpl(annotationUri, UriFactory.RDF_TYPE, kiaoResourceAnnotation);
	}

	/**
	 * @param annotationUri
	 * @param annotationDataExtractor
	 * @param annotation
	 * @param uriFactory
	 * @param documentUri
	 * @param documentUrl
	 * @param documentText
	 * @param annotationKeyToUriMap
	 * @return
	 */
	private Collection<? extends Statement> getComponentAnnotationStmts(URI annotationUri,
			RdfAnnotationDataExtractor annotationDataExtractor, Annotation annotation, UriFactory uriFactory,
			URI documentUri, URL documentUrl, String documentText, Map<String, URI> annotationKeyToUriMap,
			Map<String, Set<URI>> annotationKeyToSelectorUrisMap, Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
		Collection<Statement> stmts = new ArrayList<Statement>();
		Collection<Annotation> componentAnnotations = annotationDataExtractor.getComponentAnnotations(annotation);
		for (Annotation componentAnnotation : componentAnnotations) {
			String annotationKey = annotationDataExtractor.getAnnotationKey(componentAnnotation);
			URI componentAnnotationUri = null;
			if (annotationKeyToUriMap.containsKey(annotationKey)) {
				componentAnnotationUri = annotationKeyToUriMap.get(annotationKey);
			} else {
				List<? extends Statement> basedOnAnnotationRdf = generateRdf(annotationDataExtractor,
						componentAnnotation, uriFactory, documentUri, documentUrl, documentText, annotationKeyToUriMap,
						annotationKeyToSelectorUrisMap, annotationKeyToSemanticInstanceUriMap);
				componentAnnotationUri = new URIImpl(basedOnAnnotationRdf.get(0).getSubject().stringValue());
				annotationKeyToUriMap.put(annotationDataExtractor.getAnnotationKey(annotation), componentAnnotationUri);
				stmts.addAll(basedOnAnnotationRdf);
			}
			stmts.add(new StatementImpl(annotationUri, new URIImpl(AoExtUriUtil.KIAO_BASED_ON.toString()),
					componentAnnotationUri));
		}
		return stmts;
	}
}
