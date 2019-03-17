package edu.ucdenver.ccp.nlp.core.annotation.impl;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty;
import edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * This class is a generalized representation of a text annotation.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */

public class DefaultTextAnnotation extends TextAnnotation {

	protected List<Span> spanList;

	protected Annotator annotator;

	protected Set<AnnotationSet> annotationSets;

	protected String coveredText; // text covered by the span

	protected String annotationID; // annotation ID

	protected String documentID; // document ID

	protected int documentCollectionID; // document collection ID, see

	protected int documentSectionID; // document section ID, see Annotation

	protected ClassMention classMention;

	protected AnnotationMetadata annotationMetadata;

	/**
	 * 
	 * @param beginIndex
	 * @param endIndex
	 * @param coveredText
	 * @param annotator
	 * @param annotationSet
	 * @param annotationID
	 * @param documentCollectionID
	 * @param documentID
	 * @param documentSectionID
	 * @param classMention
	 */
	public DefaultTextAnnotation(int beginIndex, int endIndex, String coveredText, Annotator annotator,
			AnnotationSet annotationSet, String annotationID, int documentCollectionID, String documentID,
			int documentSectionID, ClassMention classMention) {
		super((Object[]) null);

		Span span;
		try {
			span = new Span(beginIndex, endIndex);
			this.spanList = new ArrayList<Span>();
			spanList.add(span);
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}

		this.coveredText = coveredText;
		this.annotator = annotator;

		this.annotationSets = new HashSet<AnnotationSet>();
		annotationSets.add(annotationSet);

		this.annotationID = annotationID;
		this.documentCollectionID = documentCollectionID;
		this.documentID = documentID;
		this.documentSectionID = documentSectionID;
		try {
			setClassMention(classMention);
		} catch (Exception e) {
			e.printStackTrace();
		}
		annotationMetadata = new AnnotationMetadata();
	}

	/**
	 * Default Constructor - creates default TextAnnotation - I would like to phase this out,
	 * therefore the Deprecated flag.
	 * 
	 * Default values: beginIndex = -1 <br>
	 * endIndex = -1 <br>
	 * coveredText = empty<br>
	 * String annotatorID = -1 <br>
	 * annotationSetID = -1 <br>
	 * annotationID = -1 <br>
	 * documentCollectionID = -1 <br>
	 * documentID = -1 <br>
	 * documentSectionID = -1 <br>
	 * classMentionMap = empty HashMap
	 */

	public DefaultTextAnnotation(int spanStart, int spanEnd) {
		super((Object[]) null);
		Span span;
		this.spanList = new ArrayList<Span>();
		try {
			span = new Span(spanStart, spanEnd);
			spanList.add(span);
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}
		this.coveredText = "";
		this.annotator = new Annotator("-1", "", "");
		this.annotationSets = new HashSet<AnnotationSet>();
		this.annotationID = "-1";
		this.documentCollectionID = -1;
		this.documentID = "-1";
		this.documentSectionID = -1;
		this.classMention = null;
		this.annotationMetadata = new AnnotationMetadata();
	}

	public DefaultTextAnnotation(List<Span> spanList) {
		super((Object[]) null);
		this.spanList = new ArrayList<Span>(spanList);
		this.coveredText = "";
		this.annotator = new Annotator("", "", "");
		this.annotationSets = new HashSet<AnnotationSet>();
		this.annotationID = "";
		this.documentCollectionID = -1;
		this.documentID = "-1";
		this.documentSectionID = -1;
		this.classMention = null;
		this.annotationMetadata = new AnnotationMetadata();
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return annotationMetadata;
	}

	@Override
	public void setAnnotationMetadata(AnnotationMetadata annotationMetadata) {
		this.annotationMetadata = annotationMetadata;
	}

	/**
	 * Set the annotator
	 * 
	 * @return
	 */
	@Override
	public Annotator getAnnotator() {
		return annotator;
	}

	/**
	 * Get the annotator
	 * 
	 * @param annotator
	 */
	@Override
	public void setAnnotator(Annotator annotator) {
		this.annotator = annotator;
	}

	/**
	 * Get the annotation sets
	 * 
	 * @return
	 */
	@Override
	public Set<AnnotationSet> getAnnotationSets() {
		return annotationSets;
	}

	/**
	 * Add an annotation set
	 * 
	 * @param annotationSet
	 */
	@Override
	public void addAnnotationSet(AnnotationSet annotationSet) {
		if (annotationSets == null) {
			annotationSets = new HashSet<AnnotationSet>();
		}
		annotationSets.add(annotationSet);
	}

	/**
	 * Set the AnnotationSet membership for this TextAnnotation
	 * 
	 * @param annotationSets
	 */
	@Override
	public void setAnnotationSets(Set<AnnotationSet> annotationSets) {
		this.annotationSets = annotationSets;
	}

	/**
	 * Get the annotation ID
	 * 
	 * @return
	 */
	@Override
	public String getAnnotationID() {
		return annotationID;
	}

	/**
	 * Set the annotation ID
	 * 
	 * @param annotationID
	 */
	@Override
	public void setAnnotationID(String annotationID) {
		this.annotationID = annotationID;
	}

	/**
	 * Get the document collection ID
	 * 
	 * @return
	 */
	@Override
	public int getDocumentCollectionID() {
		return documentCollectionID;
	}

	/**
	 * Set the document collection ID
	 * 
	 * @param documentCollectionID
	 */
	@Override
	public void setDocumentCollectionID(int documentCollectionID) {
		this.documentCollectionID = documentCollectionID;
	}

	/**
	 * Get the document ID
	 * 
	 * @return
	 */
	@Override
	public String getDocumentID() {
		return documentID;
	}

	/**
	 * Set the document ID
	 * 
	 * @param documentID
	 */
	@Override
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	/**
	 * Get the document section ID
	 * 
	 * @return
	 */
	@Override
	public int getDocumentSectionID() {
		return documentSectionID;
	}

	/**
	 * Set the document section ID
	 * 
	 * @param documentSectionID
	 */
	@Override
	public void setDocumentSectionID(int documentSectionID) {
		this.documentSectionID = documentSectionID;
	}

	/**
	 * Return the text that is "covered" by this annotation
	 */
	@Override
	public String getCoveredText() {
		return coveredText;
	}

	/**
	 * Set the text that is "covered" by this annotation
	 * 
	 * @param coveredText
	 */
	@Override
	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}

	/**
	 * Get the ordered list of spans for this TextAnnotation
	 * 
	 * @return
	 */
	@Override
	public List<Span> getSpans() {
		return spanList;
	}

	/**
	 * Set the span list equal to the inputted span list
	 * 
	 * @param spans
	 */
	@Override
	public void setSpans(List<Span> spans) {
		spanList = new ArrayList<Span>();
		for (Span span : spans) {
			addSpan(span);
		}
	}

	/**
	 * Set the annotation span to be equal to the single input span.
	 * 
	 * @param span
	 */
	@Override
	public void setSpan(Span span) {
		spanList = new ArrayList<Span>();
		addSpan(span);
	}

	/**
	 * Add a span to this TextAnnotation. The span will be placed in the proper position in the
	 * ordered span list.
	 * 
	 * @param span
	 */
	@Override
	public void addSpan(Span span) {
		if (!spanList.contains(span)) {
			spanList.add(span);
		}
		sortSpanList();

	}

	@Override
	protected void sortSpanList() {
		Collections.sort(spanList, Span.ASCENDING());
	}

	/**
	 * Sets the start index for the first annotation span
	 * 
	 * @param spanStart
	 */
	@Override
	public void setAnnotationSpanStart(int spanStart) {
		sortSpanList();
		int currentSpanStart = this.getAnnotationSpanStart();
		try {
			this.spanList.get(0).setSpanStart(spanStart);
		} catch (InvalidSpanException ise) {
			logger.error("Invalid span. ["
					+ spanStart
					+ ".."
					+ this.spanList.get(0).getSpanEnd()
					+ "] --- this error is often caused by setting the span start prior to setting the span end, please check your code to see if this is the case. --- Annotation span reverted to last known safe state.");
			ise.printStackTrace();
			/* return the span to it's previous state */
			try {
				this.spanList.get(0).setSpanStart(currentSpanStart);
			} catch (InvalidSpanException ise2) {
				ise2.printStackTrace();
				/*
				 * This exception should never be thrown as we are setting the start index back to
				 * the previous state (which should have been safe)
				 */
			}
		}

	}

	/**
	 * Set the end index for the last annotation span
	 * 
	 * @param spanEnd
	 */
	@Override
	public void setAnnotationSpanEnd(int spanEnd) {
		sortSpanList();
		int currentSpanEnd = this.getAnnotationSpanEnd();
		try {
			this.spanList.get(spanList.size() - 1).setSpanEnd(spanEnd);
		} catch (InvalidSpanException ise) {
			logger.error("Invalid span. [" + this.spanList.get(spanList.size() - 1).getSpanStart() + ".." + spanEnd
					+ "] --- Annotation span reverted to last known safe state.");
			ise.printStackTrace();
			/* return the span to it's previous state */
			try {
				this.spanList.get(spanList.size() - 1).setSpanEnd(currentSpanEnd);
			} catch (InvalidSpanException ise2) {
				ise2.printStackTrace();
				/*
				 * This exception should never be thrown as we are setting the start index back to
				 * the previous state (which should have been safe)
				 */
			}
		}
	}

	@Override
	public void offsetAnnotationSpans(int offset) {
		try {
			for (Span span : this.spanList) {
				if (offset > 0) {
					span.setSpanEnd(span.getSpanEnd() + offset);
					span.setSpanStart(span.getSpanStart() + offset);
				} else {
					span.setSpanStart(span.getSpanStart() + offset);
					span.setSpanEnd(span.getSpanEnd() + offset);
				}
			}
		} catch (InvalidSpanException ise) {
			ise.printStackTrace();
		}
	}

	// @Override
	// public ClassMention getClassMention() {
	// return classMention;
	// }

	/**
	 * When setting the classmention, the a reference to the text annotation is automatically
	 * created inside the classMention object.
	 * 
	 * @param classMention
	 * @throws Exception
	 */
	@Override
	public void setClassMention(ClassMention classMention) {
		if (classMention instanceof DefaultClassMention) {
			this.classMention = classMention;
			super.setClassMention(classMention);
		} else {
			throw new InvalidInputException(
					"The ClassMention for a DefaultTextAnnotation must be a DefaultClassMention!!");
		}
	}

	@Override
	public ClassMention createClassMention(String classMentionName) {
		return new DefaultClassMention(classMentionName);
	}

	@Override
	public TextAnnotation getWrappedObject() {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

	@Override
	protected void initializeFromWrappedAnnotation(Object... wrappedObject) {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

	@Override
	public ClassMention getClassMention() {
		return classMention;
	}

	@Override
	public String getAnnotationComment() {
		return (annotationMetadata != null) ? annotationMetadata.getAnnotationComment() : null;
	}

	@Override
	public void setAnnotationComment(String comment) {
		AnnotationCommentProperty prop = new AnnotationCommentProperty(comment);
		if (annotationMetadata == null) {
			throw new RuntimeException("metadata is null...");
		}
		annotationMetadata.addMetadataProperty(prop);
	}

}
