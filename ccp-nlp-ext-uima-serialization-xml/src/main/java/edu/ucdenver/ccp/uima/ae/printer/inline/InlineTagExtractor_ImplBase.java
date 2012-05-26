package edu.ucdenver.ccp.uima.ae.printer.inline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Base implementation for {@link InlineTagExtractor} instances. This abstract class provides logic
 * to generate the returned {@link Iterator<InlineTag>}. Extensions of this class must implement the
 * {@link InlineTagExtractor_ImplBase#getInlineTags(Annotation)} method.
 * <p>
 * The annotations used to generate {@link InlineTag} instances are chosen based on the specified
 * annotation type in the constructor.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class InlineTagExtractor_ImplBase implements InlineTagExtractor {

	private static final Logger logger = Logger.getLogger(InlineTagExtractor_ImplBase.class);

	/**
	 * Specifies the types of annotation to process when extracting {@link InlineTag} instances
	 */
	private final Collection<Integer> annotationTypes;

	/**
	 * To be used by extensions of this class to extracts type and span information (and possibly
	 * other things).
	 */
	private final AnnotationDataExtractor annotationDataExtractor;

	/**
	 * A collection of {@link InlineTag} objects that can be used when tags, for example, cannot be
	 * created on a per annotation basis. This is sometimes the case for split span annotations
	 * (e.g. when outputting in GENIA XML format).
	 */
	private Collection<InlineTag> supplementalTags;

	/**
	 * This collection of annotations can be pre-fetched prior to processing each annotation in the
	 * JCas. Use of this collection is optional but comes in handy for dealing with things like
	 * split-span annotations that may be combined with another annotation into a single set of
	 * {@link InlineTag}s (as is the case with GENIA coordinated terms).
	 */
	private Collection<Annotation> preFetchedAnnotations;

	/**
	 * Stores annotations that are excluded from output due to incompatibilities with the inline tag
	 * format. Split-span annotations are one such example (they are sometimes incompatible).
	 */
	private Collection<String> excludedAnnotations;

	/**
	 * Initializes a new {@link InlineTagExtractor} instance to process annotations identified by
	 * the UIMA annotation type integer.
	 * 
	 * @param annotationType
	 *            Specifies the type of annotation to process when extracting {@link InlineTag}
	 *            instances
	 * @param annotationDataExtractor
	 *            To be used by extensions of this class to extracts type and span information (and
	 *            possibly other things).
	 */
	protected InlineTagExtractor_ImplBase(Collection<Integer> annotationTypes,
			AnnotationDataExtractor annotationDataExtractor) {
		this.annotationTypes = annotationTypes;
		this.annotationDataExtractor = annotationDataExtractor;
		this.supplementalTags = new ArrayList<InlineTag>();
		this.preFetchedAnnotations = new ArrayList<Annotation>();
		this.excludedAnnotations = new ArrayList<String>();
	}

	protected InlineTagExtractor_ImplBase(int annotationType, AnnotationDataExtractor annotationDataExtractor) {
		this(CollectionsUtil.createList(annotationType), annotationDataExtractor);
	}

	/**
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor#getInlineTagIterator(org.apache.uima.jcas.JCas)
	 */
	@Override
	public Iterator<InlineTag> getInlineTagIterator(final JCas view) {
		this.excludedAnnotations = new ArrayList<String>();
		preFetchAnnotations(view);
		final FSIterator annotationIter = view.getJFSIndexRepository().getAnnotationIndex().iterator();
		return new Iterator<InlineTag>() {

			private List<InlineTag> tagsToReturn;

			@Override
			public boolean hasNext() {
				if (tagsToReturn != null && tagsToReturn.size() > 0)
					return true;
				while (annotationIter.hasNext()) {
					Annotation annotation = (Annotation) annotationIter.next();
					if (annotationTypes.contains(annotation.getTypeIndexID())) {
						tagsToReturn = getInlineTags(annotation);
						if (tagsToReturn.size() > 0)
							return true;
					}
				}
				generateSupplementalTags(view.getDocumentText());
				if (supplementalTags != null && supplementalTags.size() > 0) {
					tagsToReturn = new ArrayList<InlineTag>(supplementalTags);
					supplementalTags = null;
					return true;
				}
				return false;
			}

			@Override
			public InlineTag next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return tagsToReturn.remove(0);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"This operation not supported for this Iterator implementation.");
			}

		};

	}

	/**
	 * To be overriden by subclasses if pre-fetched annotations are needed
	 */
	protected void preFetchAnnotations(JCas jcas) {
		this.preFetchedAnnotations = new ArrayList<Annotation>();
	}

	/**
	 * @return the pre-fetched annotations
	 */
	protected Collection<Annotation> getPreFetchedAnnotations() {
		return preFetchedAnnotations;
	}

	/**
	 * Adds an annotation to the pre-fetched collection
	 * 
	 * @param annotation
	 */
	protected void addPreFetchedAnnotation(Annotation annotation) {
		preFetchedAnnotations.add(annotation);
	}

	/**
	 * @return the excluded annotations
	 */
	public Collection<String> getExcludedAnnotations() {
		logger.info("RETRIEVING EXCLUDED ANNOTATIONS ("+excludedAnnotations.size()+"): " + excludedAnnotations.toString());
		return excludedAnnotations;
	}

	/**
	 * Adds an annotation to the excluded collection
	 * 
	 * @param annotation
	 */
	protected void addExcludedAnnotation(Annotation annotation) {
		String annotationType = annotationDataExtractor.getAnnotationType(annotation);
		List<Span> annotationSpans = annotationDataExtractor.getAnnotationSpans(annotation);
		String coveredText = annotationDataExtractor.getCoveredText(annotation);
		String outStr = annotationType + "\t" + annotationSpans.toString() + "\t" + coveredText;
		excludedAnnotations.add(outStr);
	}

	/**
	 * @return the {@link AnnotationDataExtractor} instance being used by this
	 *         {@link InlineTagExtractor} implementation
	 */
	public AnnotationDataExtractor getAnnotationDataExtractor() {
		return annotationDataExtractor;
	}

	/**
	 * To be implemented by extensions of this base class to provide full {@link InlineTagExtractor}
	 * functionality. This method should return a {@link List<InlineTag>} of {@link InlineTag}
	 * instances corresponding to the input {@link Annotation}.
	 * 
	 * @param annotation
	 *            the returned {@link InlineTag} instances correspond to the input
	 *            {@link Annotation}
	 * @return a {@link List<InlineTag>} containing the {@link InlineTag} instances that correspond
	 *         to the input annotation
	 */
	protected abstract List<InlineTag> getInlineTags(Annotation annotation);

	/**
	 * Is called after all annotations in a CAS have been processed. This method allows
	 * {@link InlineTags} that depend on multiple annotations (e.g. GENIA coordinated term tags) to
	 * be created.
	 */
	protected void generateSupplementalTags(@SuppressWarnings("unused") String documentText) {
		supplementalTags = new ArrayList<InlineTag>();
	}

	/**
	 * Allows subclasses of the {@link InlineTagExtractor_ImplBase} class to add supplemental
	 * {@link InlineTag}s that will be returned after all annotations have been processed.
	 * 
	 * @param tag
	 */
	protected void addSupplementalTag(InlineTag tag) {
		logger.info("Adding supplemental tag: " + tag.toString());
		supplementalTags.add(tag);
	}

	protected Collection<Integer> getAnnotationTypes() {
		return annotationTypes;
	}
}
