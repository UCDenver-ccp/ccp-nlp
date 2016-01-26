package edu.ucdenver.ccp.nlp.core.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.AnnotationComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class TextAnnotation implements Comparable<TextAnnotation> {

	protected static Logger logger = Logger.getLogger(TextAnnotation.class);

	protected boolean hasWrappedAnnotation = false;

	public TextAnnotation(Object... wrappedObjectAndGlobalVars) {
		if (wrappedObjectAndGlobalVars != null) {
			hasWrappedAnnotation = true;
			try {
				initializeFromWrappedAnnotation(wrappedObjectAndGlobalVars);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void initializeFromWrappedAnnotation(Object... wrappedObjectAndGlobalVars);

	public abstract AnnotationMetadata getAnnotationMetadata();

	public abstract void setAnnotationMetadata(AnnotationMetadata annotationMetadata);

	/**
	 * This method returns the corresponding annotator ID
	 * 
	 * @return annotator ID
	 */
	public int getAnnotatorID() {
		return getAnnotator().getAnnotatorID();
	}

	/**
	 * Set the annotator
	 * 
	 * @return
	 */
	public abstract Annotator getAnnotator();

	/**
	 * Get the annotator
	 * 
	 * @param annotator
	 */
	public abstract void setAnnotator(Annotator annotator);

	/**
	 * Get the annotation sets
	 * 
	 * @return
	 */
	public abstract Set<AnnotationSet> getAnnotationSets();

	/**
	 * Add an annotation set
	 * 
	 * @param annotationSet
	 */
	public abstract void addAnnotationSet(AnnotationSet annotationSet);

	/**
	 * Return a set containing the AnnotationSet IDs for which this TextAnnotation is a member.
	 * 
	 * @return
	 */
	public Set<Integer> getAnnotationSetIDs() {
		Set<Integer> annotationSetIDs = new HashSet<Integer>();
		for (AnnotationSet annotationSet : getAnnotationSets()) {
			annotationSetIDs.add(annotationSet.getAnnotationSetID());
		}
		return annotationSetIDs;
	}

	/**
	 * Set the AnnotationSet membership for this TextAnnotation
	 * 
	 * @param annotationSets
	 */
	public abstract void setAnnotationSets(Set<AnnotationSet> annotationSets);

	/**
	 * Return true if this TextAnnotation is a member of the inputed annotation set, false
	 * otherwise.
	 * 
	 * @param annotationSetID
	 * @return
	 */
	public boolean isMemberOfAnnotationSet(int annotationSetID) {
		return getAnnotationSetIDs().contains(annotationSetID);
	}

	/**
	 * Returns an annotation commment that is specific to this annotation
	 * 
	 * @return
	 */
	public abstract String getAnnotationComment();

	/**
	 * Sets a comment specific to this annotation
	 * 
	 * @param comment
	 */
	public abstract void setAnnotationComment(String comment);

	/**
	 * Get the annotation ID
	 * 
	 * @return
	 */
	public abstract int getAnnotationID();

	/**
	 * Set the annotation ID
	 * 
	 * @param annotationID
	 */
	public abstract void setAnnotationID(int annotationID);

	/**
	 * Get the document collection ID
	 * 
	 * @return
	 */
	public abstract int getDocumentCollectionID();

	/**
	 * Set the document collection ID
	 * 
	 * @param documentCollectionID
	 */
	public abstract void setDocumentCollectionID(int documentCollectionID);

	/**
	 * Get the document ID
	 * 
	 * @return
	 */
	public abstract String getDocumentID();

	/**
	 * Set the document ID
	 * 
	 * @param documentID
	 */
	public abstract void setDocumentID(String documentID);

	/**
	 * Get the document section ID
	 * 
	 * @return
	 */
	public abstract int getDocumentSectionID();

	/**
	 * Set the document section ID
	 * 
	 * @param documentSectionID
	 */
	public abstract void setDocumentSectionID(int documentSectionID);

	/**
	 * Return the text that is "covered" by this annotation
	 */
	public abstract String getCoveredText();

	/**
	 * Set the text that is "covered" by this annotation
	 * 
	 * @param coveredText
	 */
	public abstract void setCoveredText(String coveredText);

	/**
	 * Get the ordered list of spans for this TextAnnotation
	 * 
	 * @return
	 */
	public abstract List<Span> getSpans();

	/**
	 * Set the span list equal to the inputted span list
	 * 
	 * @param spans
	 */
	public abstract void setSpans(List<Span> spans);

	/**
	 * Set the annotation span to be equal to the single input span.
	 * 
	 * @param span
	 */
	public abstract void setSpan(Span span);

	/**
	 * Add a span to this TextAnnotation. The span will be placed in the proper position in the
	 * ordered span list.
	 * 
	 * @param span
	 */
	public abstract void addSpan(Span span);

	protected abstract void sortSpanList();

	/**
	 * Return the start index of the first span (spans are stored in order)
	 * 
	 * @return
	 */
	public int getAnnotationSpanStart() {
		sortSpanList();
		return getSpans().get(0).getSpanStart();
	}

	/**
	 * Sets the start index for the first annotation span
	 * 
	 * @param spanStart
	 */
	public abstract void setAnnotationSpanStart(int spanStart);

	public abstract void offsetAnnotationSpans(int offset);

	/**
	 * Return the end index of the last span (spans are stored in order)
	 */
	public int getAnnotationSpanEnd() {
		sortSpanList();
		return getSpans().get(getSpans().size() - 1).getSpanEnd();
	}

	/**
	 * Set the end index for the last annotation span
	 * 
	 * @param spanEnd
	 */
	public abstract void setAnnotationSpanEnd(int spanEnd);

	public abstract ClassMention createClassMention(String classMentionName);

	public abstract ClassMention getClassMention();

	/**
	 * When setting the classmention, the a reference to the text annotation is automatically
	 * created inside the classMention object.
	 * 
	 * @param classMention
	 * @throws Exception
	 */
	public void setClassMention(ClassMention classMention) {
		classMention.setTextAnnotation(this);
	}

	public String toString() {
		boolean showDocumentLevelAttributesOnly = false;
		boolean deep = true;
		return getStringRepresentation(showDocumentLevelAttributesOnly, deep);
	}

	private String getStringRepresentation(boolean showDocumentLevelAttributesOnly, boolean deep) {
		StringBuffer sb = new StringBuffer();
		if (!showDocumentLevelAttributesOnly) {
			sb.append("======================= Annotation: " + getAnnotationID() + " =======================\n");
			sb.append("Annotator: ");
			if (getAnnotator() != null) {
				sb.append(getAnnotator().getStorageLine() + "\n");
			} else {
				sb.append("NULL" + "\n");
			}
		} else {
			sb.append("DocID: " + getDocumentID());
		}
		sb.append("--- AnnotationSets: " + getSortedAnnotationSetsStr());
		String commentStr = (getAnnotationComment() != null) ? getAnnotationComment() : "";
		sb.append("\n--- Comment: " + commentStr + "\n");
		if (!showDocumentLevelAttributesOnly) {
			String spansStr = getSpanStr();
			sb.append("\n--- Span: " + spansStr + "\n");
			sb.append("--- DocCollection: " + getDocumentCollectionID() + "  DocID: " + getDocumentID()
					+ "  DocumentSection: " + getDocumentSectionID() + "\n");
			sb.append("--- Covered Text: " + getCoveredText() + "\n");
			if (deep) {
				if (getClassMention() != null) {
					sb.append(getClassMention().toString() + "\n");
				} else {
					sb.append("Null Class Mention\n");
				}
			}
			sb.append("=================================================================================");
		} else {
			if (deep) {
				sb.append(getClassMention().toDocumentLevelString());
			}
		}
		return sb.toString();
	}

	private String getSpanStr() {
		String spansStr = "";
		for (Span span : getSpans()) {
			spansStr += (span.getSpanStart() + " - " + span.getSpanEnd() + "  ");
		}
		return spansStr;
	}

	/**
	 * Returns a consistent (sorted) string representation of the annotation sets to which this text
	 * annotation belongs
	 * 
	 * @return
	 */
	private String getSortedAnnotationSetsStr() {
		String annotationSetStr = "";
		List<String> annotationSetStrs = new ArrayList<String>();
		for (AnnotationSet aSet : getAnnotationSets()) {
			annotationSetStrs.add(aSet.getStorageLine());
		}
		Collections.sort(annotationSetStrs);
		for (String aSetStr : annotationSetStrs) {
			annotationSetStr += (aSetStr + ";");
		}
		if (annotationSetStr.length() > 0) {
			annotationSetStr = annotationSetStr.substring(0, annotationSetStr.length() - 1);
		}
		return annotationSetStr;
	}

	/**
	 * Output a representation of this TextAnnotation to the input PrintStream
	 * 
	 * @param ps
	 */
	public void printAnnotation(PrintStream ps) {
		ps.println(this.toString());
	}

	public String getSingleLineRepresentation() {
		return this.toString().replaceAll("\\n", " ").replaceAll("\\s+", " ").replaceAll("===","");
	}

	/**
	 * simple output on one line.. will not print complex slot mentions
	 * 
	 * @param ps
	 */
	public void printAnnotationOnOneLine(PrintStream ps) {
		ps.println(getSingleLineRepresentation());
	}

	public int compareTo(TextAnnotation textAnnotationToCompare) {
		if (!(textAnnotationToCompare instanceof TextAnnotation)) {
			throw new ClassCastException("A TextAnnotation object expected.");
		} else {
			AnnotationComparator ac = new AnnotationComparator();
			return ac.compare(this, (TextAnnotation) textAnnotationToCompare);
		}
	}

	/**
	 * The default equals() method requires exact span match, as well as identical class mention
	 * match to return true.
	 */
	@Override
	public boolean equals(Object textAnnotationToEquate) {
		if (!(textAnnotationToEquate instanceof TextAnnotation)) {
			throw new ClassCastException("A TextAnnotation object expected.");
		} else {
			TextAnnotation ta = (TextAnnotation) textAnnotationToEquate;

			if (this.compareTo(ta) == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Returns the total span of this annotation (i.e. from the start of the first span to the end
	 * of the last span)
	 */
	public Span getAggregateSpan() {
		try {
			Span totalSpan = new Span(this.getAnnotationSpanStart(), this.getAnnotationSpanEnd());
			return totalSpan;
		} catch (InvalidSpanException e) {
			/*
			 * This should never throw an exception since the spans will have been set previously.
			 * If it does throw an exception here, then the sorting mechanism for the spans is most
			 * likely to blame.
			 */
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * returns true if the total span of this TextAnnotation overlaps with the total span of the
	 * input TextAnnotation
	 * 
	 * @return
	 */
	public boolean overlaps(TextAnnotation ta) {
		return this.getAggregateSpan().overlaps(ta.getAggregateSpan());
	}

	public boolean overlaps(int index) {
		return this.getAggregateSpan().overlaps(index);
	}

	@Override
	public int hashCode() {
		return (getSpanStr() + getDocumentCollectionID() + getDocumentID() + getClassMention().toDocumentLevelString())
				.hashCode();
	}

	/**
	 * Return the length of this TextAnnotation. This method returns the length of the aggregate
	 * span.
	 * 
	 * @return
	 */
	public int length() {
		return this.getAggregateSpan().length();
	}

	/**
	 * Returns a Comparator that compares TextAnnotations based on their respective spans
	 * 
	 * @return
	 */
	public static Comparator<TextAnnotation> BY_SPAN() {
		return new Comparator<TextAnnotation>() {
			public int compare(TextAnnotation ta1, TextAnnotation ta2) {
				if (ta1.getSpans().size() > 0 && ta2.getSpans().size() > 0) {
					return new StrictSpanComparator().compare(ta1.getAggregateSpan(), ta2.getAggregateSpan());
				} else {
					Span ta1Span = null;
					Span ta2Span = null;
					try {
						ta1Span = new Span(0, 0);
						if (ta1.getSpans().size() > 0) {
							ta1Span = ta1.getAggregateSpan();
						} else {
							logger.warn(String.format(
									"Spanless annotation detected during TextAnnotation.BY_SPAN comparison. Type=%s",
									ta1.getClassMention().getMentionName()));
						}
						ta2Span = new Span(0, 0);
						if (ta2.getSpans().size() > 0) {
							ta2Span = ta2.getAggregateSpan();
						} else {
							logger.warn(String.format(
									"Spanless annotation detected during TextAnnotation.BY_SPAN comparison. Type=%s",
									ta2.getClassMention().getMentionName()));
						}
					} catch (InvalidSpanException ise) {
						ise.printStackTrace();
					}
					return new StrictSpanComparator().compare(ta1Span, ta2Span);
				}
			}
		};
	}

	public abstract Object getWrappedObject();
}
