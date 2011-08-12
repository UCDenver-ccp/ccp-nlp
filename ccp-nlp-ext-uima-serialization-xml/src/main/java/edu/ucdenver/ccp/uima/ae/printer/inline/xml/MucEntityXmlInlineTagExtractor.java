package edu.ucdenver.ccp.uima.ae.printer.inline.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePostfixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePrefixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTagExtractor_ImplBase;


/**
 * Extension of the {@link InlineTagExtractor_ImplBase} base class to support generation of MUC
 * Entity XML tags. Description of the format taken from:
 * http://www-nlpir.nist.gov/related_projects/muc/proceedings/ne_task.html
 * <p>
 * This extension should be subclassed into a type-system specific implementation. The subclass must
 * have a zero-argument constructor.
 * <p>
 * This implementation outputs named entity tags (ENAMEX) according to the MUC guidelines.
 * 
 * <pre>
 * The markup will have the following form:
 * 
 * <ELEMENT-NAME ATTR-NAME="ATTR-VALUE" ...>text-string</ELEMENT-NAME>
 * 
 * Example:
 * 
 * <ENAMEX TYPE="ORGANIZATION">Taga Co.</ENAMEX>
 * </pre>
 * 
 * <p>
 * Note: this format is unable to represent annotations with multiple spans. If an annotation
 * associated with more than a single span is encountered, a warning is logged and no inline tags
 * are generated for that annotation (so it is essentially ignored).
 * 
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class MucEntityXmlInlineTagExtractor extends InlineTagExtractor_ImplBase {
	/**
	 * Used to output warning logs if annotations with multiple spans are encountered.
	 */
	private static final Logger logger = Logger.getLogger(MucEntityXmlInlineTagExtractor.class);

	/**
	 * The MUC format specifies "ENAMEX" to be used as the tag name for named entities
	 */
	public static final String MUC_ENTITY_TAG_NAME = "ENAMEX";

	/**
	 * The XML attribute name used to assign a type or class name to a "ENAMEX" tag
	 */
	public static final String MUC_ENTITY_TYPE_ATTRIBUTE_NAME = "TYPE";

	/**
	 * Initializes a new instance that will generate inline tags using the MUC named entity format
	 * 
	 * @param annotationType
	 *            the UIMA annotation type specifying the UIMA annotations to process when
	 *            generating the inline tags
	 * @param annotationDataExtractor
	 *            the {@link AnnotationDataExtractor} implementation to use with the specified
	 *            annotation type
	 */
	protected MucEntityXmlInlineTagExtractor(Collection<Integer> annotationTypes, AnnotationDataExtractor annotationDataExtractor) {
		super(annotationTypes, annotationDataExtractor);
	}

	/**
	 * Produces {@link InlineTags} using the MUC named entity format, e.g.
	 * 
	 * <pre>
	 * <ENAMEX TYPE="ORGANIZATION">Taga Co.</ENAMEX>
	 * </pre>
	 * 
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor_ImplBase#getInlineTags(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	protected List<InlineTag> getInlineTags(Annotation annotation) {
		List<InlineTag> inlineTagList = new ArrayList<InlineTag>();
		String type = getAnnotationDataExtractor().getAnnotationType(annotation);
		List<Span> annotationSpans = getAnnotationDataExtractor().getAnnotationSpans(annotation);
		if (annotationSpans.size() == 1) {
			String startTagContents = String.format("<%s %s=\"%s\">", MUC_ENTITY_TAG_NAME,
					MUC_ENTITY_TYPE_ATTRIBUTE_NAME, type);
			String endTagContents = String.format("</%s>", MUC_ENTITY_TAG_NAME);
			InlineTag startTag = new InlinePrefixTag(startTagContents, annotationSpans.get(0));
			InlineTag endTag = new InlinePostfixTag(endTagContents, annotationSpans.get(0));
			inlineTagList.add(startTag);
			inlineTagList.add(endTag);
		} else
			logger.warn(String.format(
					"The MUC Entity XML format does not support representation of entities with multiple spans or zero spans. "
							+ "An annotation with multiple spans has been observed and will not be included in the "
							+ "MUC XML output: type=%s spans=%s", type, annotationSpans.toString()));
		return inlineTagList;
	}

}
