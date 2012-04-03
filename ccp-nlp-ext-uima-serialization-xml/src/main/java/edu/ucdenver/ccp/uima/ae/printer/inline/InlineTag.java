package edu.ucdenver.ccp.uima.ae.printer.inline;

import java.util.Comparator;

import edu.ucdenver.ccp.uima.shims.annotation.Span;



/**
 * This is a base class designed to represent an inline tag. No assumptions are made as to the
 * content of this tag, that is left to be created/controlled elsewhere.
 * 
 * @author bill
 * 
 */
public abstract class InlineTag implements Comparable<InlineTag> {
	/**
	 * Stores the tag contents, e.g. "<protein>" or perhaps "/NN"
	 */
	private final String tagContents;

	/**
	 * The span for the annotation that this tag corresponds to
	 */
	private final Span annotationSpan;

	/**
	 * Constructs a new {@link InlineTag} object
	 * 
	 * @param tagContents
	 *            the tag contents, e.g. "<protein>" or perhaps "/NN"
	 * @param annotationSpan
	 *            the span for the annotation that this tag corresponds to
	 */
	public InlineTag(String tagContents, Span annotationSpan) {
		super();
		this.tagContents = tagContents;
		this.annotationSpan = annotationSpan;
	}

	/**
	 * @return the tag contents
	 */
	public String getTagContents() {
		return tagContents;
	}

	/**
	 * @return the span for the annotation that this tag corresponds to
	 */
	public Span getAnnotationSpan() {
		return annotationSpan;
	}

	/**
	 * Comparison of InlineTags will differ depending on context. This method is therefore not
	 * implemented. Please use the implemented comparators instead when sorting. See
	 * getInlinePrefixTagComparator() and getInlinePostfixTagComparator().
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @throws UnsupportedOperationException
	 *             if this method is called. See comment above.
	 */
	@Override
	public int compareTo(@SuppressWarnings("unused") InlineTag o) {
		throw new UnsupportedOperationException(
				"Comparison of InlineTags will differ depending on context. This method is therefore not implemented. Please use the implemented comparators instead when sorting. See getInlinePrefixTagComparator() and getInlinePostfixTagComparator().");
	}

	/**
	 * Represents an inline tag that occurs before the annotation it is associated with, e.g.
	 * "<protein>"
	 * 
	 * @author bill
	 * 
	 */
	public static class InlinePrefixTag extends InlineTag {

		/**
		 * Constructs a new {@link InlinePrefixTag} object
		 * 
		 * @param tagContents
		 *            the tag contents, e.g. "<protein>" or perhaps "/NN"
		 * @param annotationSpan
		 *            the span for the annotation that this tag corresponds to
		 */
		public InlinePrefixTag(String tagContents, Span annotationSpan) {
			super(tagContents, annotationSpan);
		}

	}

	/**
	 * Represents an inline tag that occurs after the annotation it is associated with, e.g.
	 * "</protein>" or "/NN"
	 * 
	 * @author bill
	 * 
	 */
	public static class InlinePostfixTag extends InlineTag {

		/**
		 * Constructs a new {@link InlinePostfixTag} object
		 * 
		 * @param tagContents
		 *            the tag contents, e.g. "<protein>" or perhaps "/NN"
		 * @param annotationSpan
		 *            the span for the annotation that this tag corresponds to
		 */
		public InlinePostfixTag(String tagContents, Span annotationSpan) {
			super(tagContents, annotationSpan);
		}
	}

	/**
	 * Compares {@link InlinePrefixTag} instances. If the end spans are not the same, then the tags
	 * are ordered such that those with a larger end span appear first. If the end spans are
	 * identical then the sort order defaults to a comparison of the tag content {@link String}. It
	 * is assumed that the tag span starts are identical.
	 * 
	 * @return an initialized {@link Comparator} suitable for sorting {@link InlinePrefixTag}
	 *         instances that have identical start span values
	 */
	public static Comparator<? super InlinePrefixTag> getInlinePrefixTagComparator() {
		return new Comparator<InlinePrefixTag>() {

			@Override
			public int compare(InlinePrefixTag tag1, InlinePrefixTag tag2) {
				return tag1.getAnnotationSpan().getSpanEnd() == tag2.getAnnotationSpan().getSpanEnd() ? tag1
						.getTagContents().compareTo(tag2.getTagContents()) : tag2.getAnnotationSpan().getSpanEnd()
						- tag1.getAnnotationSpan().getSpanEnd();
			}

		};
	}

	/**
	 * Compares {@link InlinePostfix} instances. If the start spans are not the same, then the tags
	 * are ordered such that those with a small start span offset appear first. If the start spans
	 * are identical then the sort order defaults to a comparison of the tag content {@link String}.
	 * It is assumed that the tag span end offsets are identical.
	 * 
	 * @return an initialized {@link Comparator} suitable for sorting {@link InlinePrefixTag}
	 *         instances that have identical start span values
	 */
	public static Comparator<? super InlinePostfixTag> getInlinePostfixTagComparator() {
		return new Comparator<InlinePostfixTag>() {

			@Override
			public int compare(InlinePostfixTag tag1, InlinePostfixTag tag2) {
				return tag1.getAnnotationSpan().getSpanStart() == tag2.getAnnotationSpan().getSpanStart() ? tag2
						.getTagContents().compareTo(tag1.getTagContents()) : tag2.getAnnotationSpan().getSpanStart()
						- tag1.getAnnotationSpan().getSpanStart();
			}

		};
	}
}
