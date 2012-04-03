package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.PartOfSpeech;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePostfixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePrefixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTagExtractor_ImplBase;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Extension of the {@link InlineTagExtractor_ImplBase} base class to support generation of GENIA
 * part-of-speech XML tags. Description of the format taken from:
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
public abstract class GeniaPosXmlInlineTagExtractor extends InlineTagExtractor_ImplBase {
	/**
	 * Used to output warning logs if annotations with multiple spans are encountered.
	 */
	private static final Logger logger = Logger.getLogger(GeniaPosXmlInlineTagExtractor.class);

	/**
	 * The GENIA format specifies the use of "sentence" as the tag name for sentence annotations
	 */
	public static final String GENIA_SENTENCE_TAG_NAME = "sentence";

	/**
	 * The GENIA format specifies the use of "tok" as the tag name for token annotations
	 */
	public static final String GENIA_TOKEN_TAG_NAME = "tok";

	/**
	 * The GENIA format specifes the use of "cat" as the attribute of a "tok" tag, used to store
	 * part of speech information about that token
	 */
	public static final String GENIA_PART_OF_SPEECH_ATTRIBUTE_NAME = "cat";

	/**
	 * The GENIA format specifes the use of "cats" as the attribute of a "tok" tag, used to store
	 * multiple part of speech symbols when ambiguity exists
	 */
	public static final String GENIA_AMBIGUOUS_POS_ATTRIBUTE_NAME = "cats";

	private TokenAttributeExtractor tokenAttributeExtractor;

	/**
	 * Initializes a new instance that will generate inline tags using the GENIA token format
	 * 
	 * @param annotationType
	 *            the UIMA annotation type specifying the UIMA annotations to process when
	 *            generating the inline tags
	 * @param annotationDataExtractor
	 *            the {@link AnnotationDataExtractor} implementation to use with the specified
	 *            annotation type
	 */
	protected GeniaPosXmlInlineTagExtractor(Collection<Integer> annotationTypes,
			AnnotationDataExtractor annotationDataExtractor, TokenAttributeExtractor tokenAttributeExtractor) {
		super(annotationTypes, annotationDataExtractor);
		this.tokenAttributeExtractor = tokenAttributeExtractor;
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
		System.out.println("type: " + type);
		List<Span> annotationSpans = getAnnotationDataExtractor().getAnnotationSpans(annotation);
		if (type.equalsIgnoreCase("token")) {
			List<PartOfSpeech> partsOfSpeech = tokenAttributeExtractor.getPartsOfSpeech(annotation);
			if (annotationSpans.size() == 1) {
				String startTagContents;
				if (partsOfSpeech.size() == 1)
					startTagContents = String.format("<%s %s=\"%s\">", GENIA_TOKEN_TAG_NAME,
							GENIA_PART_OF_SPEECH_ATTRIBUTE_NAME, CollectionsUtil.getSingleElement(partsOfSpeech)
									.getPosTag());
				else if (partsOfSpeech.size() > 1) {
					List<String> posTags = new ArrayList<String>();
					for (PartOfSpeech pos : partsOfSpeech)
						posTags.add(pos.getPosTag());
					startTagContents = String.format("<%s %s=\"%s\">", GENIA_TOKEN_TAG_NAME,
							GENIA_AMBIGUOUS_POS_ATTRIBUTE_NAME,
							CollectionsUtil.createDelimitedString(posTags, StringConstants.SPACE));
				} else
					throw new IllegalStateException("Found token without part of speech tag");
				String endTagContents = String.format("</%s>", GENIA_TOKEN_TAG_NAME);
				InlineTag startTag = new InlinePrefixTag(startTagContents, annotationSpans.get(0));
				InlineTag endTag = new InlinePostfixTag(endTagContents, annotationSpans.get(0));
				inlineTagList.add(startTag);
				inlineTagList.add(endTag);
			} else
				logger.warn(String.format("Tokens shouldn't have more than a single span"));
		} else if (type.equalsIgnoreCase("sentence")) {
			InlineTag startTag = new InlinePrefixTag(String.format("<%s>", GENIA_SENTENCE_TAG_NAME),
					annotationSpans.get(0));
			InlineTag endTag = new InlinePostfixTag(String.format("</%s>", GENIA_SENTENCE_TAG_NAME),
					annotationSpans.get(0));
			inlineTagList.add(startTag);
			inlineTagList.add(endTag);
		}
		return inlineTagList;
	}

}
