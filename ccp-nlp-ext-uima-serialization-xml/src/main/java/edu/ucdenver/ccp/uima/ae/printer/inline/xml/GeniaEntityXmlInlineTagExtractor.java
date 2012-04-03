package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.annotation.SpanUtilExtra;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePostfixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePrefixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTagExtractor_ImplBase;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Extension of the {@link InlineTagExtractor_ImplBase} base class to support generation of GENIA
 * Entity XML tags. Description of the format taken from:
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.106.9947&rep=rep1&type=pdf
 * <p>
 * This extension should be subclassed into a type-system specific implementation. The subclass must
 * have a zero-argument constructor.
 * <p>
 * This implementation outputs named entity tags (term) according to the GENIA guidelines.
 * 
 * <pre>
 * The markup will have the following form:
 * 
 * Example:
 * <term sem="GO:00012345"><term sem="DNA_domain_or_region">IL-2 gene</term> transcription</term> in <term sem="Cell_type">T cells</term>
 * </pre>
 * 
 * <p>
 * Note: this format is unable to represent annotations with multiple spans that overlap. If an
 * annotation associated with more than a single span is encountered, a warning is logged and no
 * inline tags are generated for that annotation (so it is essentially ignored).
 * 
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class GeniaEntityXmlInlineTagExtractor extends InlineTagExtractor_ImplBase {
	/**
	 * Used to output warning logs if annotations with multiple spans are encountered.
	 */
	private static final Logger logger = Logger.getLogger(GeniaEntityXmlInlineTagExtractor.class);

	/**
	 * The GENIA format specifies the use of "term" as the tag name for named entity annotations
	 */
	public static final String GENIA_ENTITY_TAG_NAME = "term";

	/**
	 * The GENIA format specifes the use of "sem" as the attribute of a "term" tag, used to store
	 * semantic information about that term
	 */
	public static final String GENIA_ENTITY_TYPE_ATTRIBUTE_NAME = "sem";

	/**
	 * The GENIA format specifies the use of "coterm" to indicated coordinated terms. This is how
	 * GENIA deals with split span annotations (it only does so in the case of coordination)
	 */
	private static final Object GENIA_COTERM_TAG_NAME = "coterm";

	/**
	 * The GENIA format specifies the use of "frag" to designate a member of a coterm group
	 */
	private static final Object GENIA_FRAG_TAG_NAME = "frag";

	/**
	 * Stores all split span annotations and any single span annotation that overlaps a split-span
	 * annotation found during processing. When
	 * {@link GeniaEntityXmlInlineTagExtractor#generateSupplementalTags()} is called,
	 * {@link InlineTag}s are created for those annotations that represent coordinated terms. Log
	 * messages are generated for any split span annotation that is determined to not be a
	 * coordinated term and is therefore excluded from the output.
	 */
	private List<Annotation> overlapsSplitSpanAnnotations;

	/**
	 * Initializes a new instance that will generate inline tags using the GENIA named entity format
	 * 
	 * @param annotationType
	 *            the UIMA annotation type specifying the UIMA annotations to process when
	 *            generating the inline tags
	 * @param annotationDataExtractor
	 *            the {@link AnnotationDataExtractor} implementation to use with the specified
	 *            annotation type
	 */
	protected GeniaEntityXmlInlineTagExtractor(Collection<Integer> annotationTypes, AnnotationDataExtractor annotationDataExtractor) {
		super(annotationTypes, annotationDataExtractor);
		this.overlapsSplitSpanAnnotations = new ArrayList<Annotation>();
	}

	/**
	 * Produces {@link InlineTags} using the GENIA named entity format, e.g.
	 * 
	 * <pre>
	 * <term sem="ORGANIZATION">Taga Co.</term>
	 * </pre>
	 * 
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor_ImplBase#getInlineTags(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	protected List<InlineTag> getInlineTags(Annotation annotation) {
		List<InlineTag> inlineTagList = new ArrayList<InlineTag>();
		String type = getAnnotationDataExtractor().getAnnotationType(annotation);
		if (!type.equals("syntactic context") && !(type.equals("continuant"))) {
		List<Span> annotationSpans = getAnnotationDataExtractor().getAnnotationSpans(annotation);
		if (annotationSpans.size() == 1) {
			// in the future we need to look into how to represent split span annotations
//		if (!overlapsWithSplitSpanAnnotation(annotation)) {
			String startTagContents = String.format("<%s %s=\"%s\">", GENIA_ENTITY_TAG_NAME,
					GENIA_ENTITY_TYPE_ATTRIBUTE_NAME, type);
			String endTagContents = String.format("</%s>", GENIA_ENTITY_TAG_NAME);
			InlineTag startTag = new InlinePrefixTag(startTagContents, annotationSpans.get(0));
			InlineTag endTag = new InlinePostfixTag(endTagContents, annotationSpans.get(0));
			inlineTagList.add(startTag);
			inlineTagList.add(endTag);
		} else {
			overlapsSplitSpanAnnotations.add(annotation);
			UIMA_Util.printCCPTextAnnotation((CCPTextAnnotation) annotation, System.err);
		}
		// logger
		// .warn(String
		// .format(
		// "Although the GENIA inline term annotation representation allows for discontinous annotations, "
		// +
		// "it cannot handle overlapping discontinuous annotations. Due to this limitation, this inline output "
		// + "tool will not output discontinuous annotations in the GENIA format."
		// + "An annotation with multiple spans has been observed and will not be included in the "
		// + "GENIA XML output: type=%s spans=%s", type, annotationSpans.toString()));
		}
		return inlineTagList;
	}

	// get all split-span annotations
	// for each split-span annotation - get all annotations that it overlaps with
	// if any overlapping annotation is the same type, then see if the intervening text is a
	// conjunction or perhaps a comma
	// if so, then create a coterm inline tag

	/**
	 * Returns true if the input annotation overlaps with a split-span annotation (annotation with >
	 * 1 span) in the document. The split-span annotations are gathered during the pre-fetch
	 * annotation stage. See {@link GeniaEntityXmlInlineTagExtractor#preFetchAnnotations(JCas)}.
	 * 
	 * @param annotation
	 * @return
	 */
	private boolean overlapsWithSplitSpanAnnotation(Annotation annotation) {
		List<Span> annotationSpans = getAnnotationDataExtractor().getAnnotationSpans(annotation);
		for (Span span : annotationSpans)
			for (Annotation splitSpanAnnot : getPreFetchedAnnotations()) {
				List<Span> prefetchSpans = getAnnotationDataExtractor().getAnnotationSpans(splitSpanAnnot);
				for (Span prefetchSpan : prefetchSpans)
					if (span.overlaps(prefetchSpan))
						return true;
			}
		return false;
	}

	/**
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor_ImplBase#preFetchAnnotations(org.apache.uima.jcas.JCas)
	 */
	@Override
	protected void preFetchAnnotations(JCas jcas) {
		super.preFetchAnnotations(jcas);
		for (int annotationType : getAnnotationTypes()) 
		for (FSIterator annotationIter = jcas.getJFSIndexRepository().getAnnotationIndex(annotationType)
				.iterator(); annotationIter.hasNext();) {
			Annotation annotation = (Annotation) annotationIter.next();
			if (getAnnotationDataExtractor().getAnnotationSpans(annotation).size() > 1)
				addPreFetchedAnnotation(annotation);
		}
	}

	/**
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor_ImplBase#generateSupplementalTags(java.lang.String)
	 */
	@Override
	protected void generateSupplementalTags(String documentText) {
		System.out.println("# split span annotations skipped in output: " + overlapsSplitSpanAnnotations.size());
//		while (overlapsSplitSpanAnnotations.size() > 0) {
//			Annotation annotation = overlapsSplitSpanAnnotations.get(0);
//			Collection<Annotation> overlappingAnnotations = new ArrayList<Annotation>();
//			for (Annotation annot : overlapsSplitSpanAnnotations)
//				if (overlaps(annotation, annot))
//					overlappingAnnotations.add(annot);
//
//			Collection<Span> interveningSpans = getInterveningSpans(overlappingAnnotations);
//
//			if (overlappingAnnotations.size() == 2) {
//				if (interveningSpans.size() == 1) {
//					Span onlySpan = CollectionsUtil.getSingleElement(interveningSpans);
//					String interveningText = documentText.substring(onlySpan.getSpanStart(), onlySpan.getSpanEnd())
//							.trim();
//					logger.info("checking intervening text: " + interveningText);
//					if (interveningText.equals("and") || interveningText.equals("or")) {
//						Collection<Span> sharedSpans = getSharedSpans(overlappingAnnotations);
//						Map<Span, String> uniqueSpans = getUniqueSpansToMentionNameMap(documentText,
//								overlappingAnnotations);
//						createSupplementalTags(interveningText, sharedSpans, uniqueSpans);
//						for (Annotation annot : overlappingAnnotations)
//							overlapsSplitSpanAnnotations.remove(annot);
//					}
//				} else {
//					logger.info("multiple intervening spans: " + interveningSpans.toString());
//				}
//			} else {
//				logger.info(">2 overlapping annotations");
//			}
//
//		}
	}

	/**
	 * Creates supplemental tags involving coordinated terms. TODO: log terms that aren't part of
	 * coordinations - they will probably cause an infinite loop here
	 * 
	 * @param interveningText
	 * @param sharedSpans
	 * @param uniqueSpansToTypeMap
	 */
	private void createSupplementalTags(String interveningText, Collection<Span> sharedSpans,
			Map<Span, String> uniqueSpansToTypeMap) {
		String coord = null;
		if (interveningText.equals("and"))
			coord = "AND";
		if (interveningText.equals("or"))
			coord = "OR";
		String coTermStartTagContents = String.format("<%s %s=\"(%s", GENIA_COTERM_TAG_NAME,
				GENIA_ENTITY_TYPE_ATTRIBUTE_NAME, coord);
		List<Span> uniqueSpans = new ArrayList<Span>(uniqueSpansToTypeMap.keySet());
		Collections.sort(uniqueSpans, Span.ASCENDING());
		for (Span uniqueSpan : uniqueSpans)
			coTermStartTagContents += (" " + uniqueSpansToTypeMap.get(uniqueSpan));
		coTermStartTagContents += ")\">";
		String coTermEndTagContents = String.format("</%s>", GENIA_COTERM_TAG_NAME);
		String fragStartTagContents = String.format("<%s>", GENIA_FRAG_TAG_NAME);
		String fragEndTagContents = String.format("</%s>", GENIA_FRAG_TAG_NAME);

		Span coTermSpan = computeCoTermSpan(uniqueSpans, sharedSpans);
		addSupplementalTag(new InlinePrefixTag(coTermStartTagContents, coTermSpan));
		addSupplementalTag(new InlinePostfixTag(coTermEndTagContents, coTermSpan));

		for (Span uniqueSpan : uniqueSpans) {
			addSupplementalTag(new InlinePrefixTag(fragStartTagContents, uniqueSpan));
			addSupplementalTag(new InlinePostfixTag(fragEndTagContents, uniqueSpan));
		}

		for (Span commonSpan : sharedSpans) {
			addSupplementalTag(new InlinePrefixTag(fragStartTagContents, commonSpan));
			addSupplementalTag(new InlinePostfixTag(fragEndTagContents, commonSpan));
		}
	}

	/**
	 * Computes the co-term span by looking for the boundary extremes found in the input.
	 * 
	 * @param uniqueSpans
	 * @param sharedSpans
	 * @return
	 */
	private Span computeCoTermSpan(List<Span> uniqueSpans, Collection<Span> sharedSpans) {
		int spanStart = Integer.MAX_VALUE;
		int spanEnd = Integer.MIN_VALUE;

		Collection<Span> spans = new ArrayList<Span>(uniqueSpans);
		spans.addAll(sharedSpans);
		for (Span span : spans) {
			if (span.getSpanStart() < spanStart)
				spanStart = span.getSpanStart();
			if (span.getSpanEnd() > spanEnd)
				spanEnd = span.getSpanEnd();
		}
		return new Span(spanStart, spanEnd);
	}

	/**
	 * Returns a mapping from unique spans to the annotation type for the input annotations
	 * 
	 * @param documentText
	 * 
	 * @param annotations
	 * @return
	 */
	private Map<Span, String> getUniqueSpansToMentionNameMap(String documentText, Collection<Annotation> annotations) {
		Collection<Span> spans = new ArrayList<Span>();
		for (Annotation annotation : annotations)
			spans.addAll(getAnnotationDataExtractor().getAnnotationSpans(annotation));
		Collection<Span> uniqueSpans = SpanUtilExtra.getUniqueSpans(spans);
		Map<Span, String> uniqueSpanToMentionNameMap = new HashMap<Span, String>();
		for (Span span : uniqueSpans)
			for (Annotation annotation : annotations)
				if (overlaps(span, annotation))
					uniqueSpanToMentionNameMap.put(SpanUtilExtra.trim(documentText, span), getAnnotationDataExtractor()
							.getAnnotationType(annotation));
		if (uniqueSpanToMentionNameMap.size() != uniqueSpans.size())
			throw new IllegalStateException("not all spans accounted for");
		return uniqueSpanToMentionNameMap;
	}

	/**
	 * Returns spans that are shared by at least two annotations in the input collection.
	 * 
	 * @param overlappingAnnotations
	 * @return
	 */
	private Collection<Span> getSharedSpans(Collection<Annotation> annotations) {
		Collection<Span> spans = new ArrayList<Span>();
		for (Annotation annotation : annotations)
			spans.addAll(getAnnotationDataExtractor().getAnnotationSpans(annotation));
		return SpanUtilExtra.getCommonSpans(spans);
	}

	/**
	 * Examines the input annotations and returns any intervening spans that are not covered by the
	 * spans in the annotations, e.g. if the annotations covered everything in "blue and red truck"
	 * except for " and " then this method would return the span for " and "
	 * 
	 * @param annotations
	 * @return
	 */
	private Collection<Span> getInterveningSpans(Collection<Annotation> annotations) {
		Collection<Span> spans = new ArrayList<Span>();
		for (Annotation annotation : annotations)
			spans.addAll(getAnnotationDataExtractor().getAnnotationSpans(annotation));
		return SpanUtilExtra.getInterveningSpans(spans);
	}

	/**
	 * Returns true if the input annotations overlap
	 * 
	 * @param annot1
	 * @param annot2
	 * @return
	 */
	private boolean overlaps(Annotation annot1, Annotation annot2) {
		for (Span span1 : getAnnotationDataExtractor().getAnnotationSpans(annot1))
			for (Span span2 : getAnnotationDataExtractor().getAnnotationSpans(annot2))
				if (span1.overlaps(span2))
					return true;
		return false;
	}

	/**
	 * Returns true if the input span overlaps with the input annotation
	 * 
	 * @param span
	 * @param annotation
	 * @return
	 */
	private boolean overlaps(Span span, Annotation annotation) {
		for (Span span1 : getAnnotationDataExtractor().getAnnotationSpans(annotation))
			if (span1.overlaps(span))
				return true;
		return false;
	}

}
