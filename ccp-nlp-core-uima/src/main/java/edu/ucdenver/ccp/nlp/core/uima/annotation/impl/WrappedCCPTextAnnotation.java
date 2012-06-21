package edu.ucdenver.ccp.nlp.core.uima.annotation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.impl.WrappedCCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class WrappedCCPTextAnnotation extends TextAnnotation {

	private CCPTextAnnotation wrappedCCPTextAnnotation;

	private JCas jcas;

	public WrappedCCPTextAnnotation(CCPTextAnnotation ccpTA) {
		super(ccpTA);
	}

	@Override
	protected void initializeFromWrappedAnnotation(Object... wrappedObject) {
		if (wrappedObject[0] instanceof CCPTextAnnotation) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) wrappedObject[0];
			this.wrappedCCPTextAnnotation = ccpTA;
			try {
				jcas = ccpTA.getCAS().getJCas();
			} catch (CASException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected CCPTextAnnotation. Cannot wrap "
					+ wrappedObject.getClass().getName() + " object in a WrappedCCPTextAnnotation");
		}
	}

	@Override
	public CCPTextAnnotation getWrappedObject() {
		return wrappedCCPTextAnnotation;
	}

	@Override
	public void addAnnotationSet(AnnotationSet annotationSet) {
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		UIMA_Util.swapAnnotationSetInfo(annotationSet, ccpAnnotationSet);
		UIMA_Util.addAnnotationSet(wrappedCCPTextAnnotation, ccpAnnotationSet, jcas);
	}

	@Override
	public void addSpan(Span span) {
		UIMA_Annotation_Util.addSpan(wrappedCCPTextAnnotation, span, jcas);
	}

	@Override
	public ClassMention createClassMention(String classMentionName) {
		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName(classMentionName);
		return new WrappedCCPClassMention(ccpCM);
	}

	// @Override
	// public ClassMention getClassMention() {
	// return new WrappedCCPClassMention(wrappedCCPTextAnnotation.getClassMention());
	// }

	@Override
	public int getAnnotationID() {
		return wrappedCCPTextAnnotation.getAnnotationID();
	}

	@Override
	public void setAnnotationID(int annotationID) {
		wrappedCCPTextAnnotation.setAnnotationID(annotationID);
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return UIMA_Annotation_Util.getUtilAnnotationMetadata(wrappedCCPTextAnnotation, jcas);
	}

	@Override
	public Set<AnnotationSet> getAnnotationSets() {
		return UIMA_Annotation_Util.getAnnotationSets(wrappedCCPTextAnnotation);
	}

	@Override
	public Annotator getAnnotator() {
		return UIMA_Annotation_Util.getAnnotator(wrappedCCPTextAnnotation);
	}

	@Override
	public void setClassMention(ClassMention classMention) throws InvalidInputException {
		if (classMention.getWrappedObject() instanceof CCPClassMention) {
			CCPClassMention ccpCM = (CCPClassMention) classMention.getWrappedObject();
			wrappedCCPTextAnnotation.setClassMention(ccpCM);
			ccpCM.setCcpTextAnnotation(wrappedCCPTextAnnotation);
		} else {
			throw new InvalidInputException("Cannot set CCPClassMention field of a CCPTextAnnotation to a "
					+ classMention.getWrappedObject().getClass().getName());
		}
	}

	@Override
	public String getCoveredText() {
		return wrappedCCPTextAnnotation.getCoveredText();
	}

	@Override
	public int getDocumentCollectionID() {
		return UIMA_Util.getDocumentCollectionID(jcas);
	}

	@Override
	public String getDocumentID() {
		return UIMA_Util.getDocumentID(jcas);
	}

	@Override
	public int getDocumentSectionID() {
		return wrappedCCPTextAnnotation.getDocumentSectionID();
	}

	@Override
	public List<Span> getSpans() {
		try {
			return UIMA_Annotation_Util.getSpanList(wrappedCCPTextAnnotation);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void offsetAnnotationSpans(int offset) {
		try {
			UIMA_Annotation_Util.offsetSpans(wrappedCCPTextAnnotation, offset);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAnnotationMetadata(AnnotationMetadata annotationMetadata) {
		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = new edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata(
				jcas);
		UIMA_Util.swapAnnotationMetadata(annotationMetadata, ccpAnnotationMetadata, jcas);
		wrappedCCPTextAnnotation.setAnnotationMetadata(ccpAnnotationMetadata);
	}

	@Override
	public void setAnnotationSets(Set<AnnotationSet> annotationSets) {
		UIMA_Annotation_Util.setAnnotationSets(wrappedCCPTextAnnotation, annotationSets, jcas);
	}

	@Override
	public void setAnnotationSpanEnd(int spanEnd) {
		try {
			UIMA_Annotation_Util.setAggregateSpanEnd(wrappedCCPTextAnnotation, spanEnd, jcas);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAnnotationSpanStart(int spanStart) {
		try {
			UIMA_Annotation_Util.setAggregateSpanStart(wrappedCCPTextAnnotation, spanStart, jcas);
		} catch (KnowledgeRepresentationWrapperException e) {
			e.printStackTrace();
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAnnotator(Annotator annotator) {
		UIMA_Annotation_Util.setAnnotator(wrappedCCPTextAnnotation, annotator, jcas);
	}

	@Override
	public void setCoveredText(String coveredText) {
		// covered text is not a field in UIMA text annotations, so we don't need to do anything
		// here.
	}

	@Override
	public void setDocumentCollectionID(int documentCollectionID) {
		// the document collection ID is stored in the JCas (CCPDocumentInformation), not the
		// annotation, so we don't
		// need to do anything here
	}

	@Override
	public void setDocumentID(String documentID) {
		// the document ID is stored in the JCas (CCPDocumentInformation), not the annotation, so we
		// don't
		// need to do anything here
	}

	@Override
	public void setDocumentSectionID(int documentSectionID) {
		wrappedCCPTextAnnotation.setDocumentSectionID(documentSectionID);
	}

	@Override
	public void setSpan(Span span) {
		wrappedCCPTextAnnotation.setSpans(null);
		addSpan(span);
	}

	@Override
	public void setSpans(List<Span> spans) {
		wrappedCCPTextAnnotation.setSpans(null);
		for (Span span : spans) {
			addSpan(span);
		}
	}

	@Override
	protected void sortSpanList() {
		UIMA_Annotation_Util.sortSpanList(wrappedCCPTextAnnotation);
	}

	public static List<TextAnnotation> convertAnnotationList(List<CCPTextAnnotation> inputList) {
		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();
		for (CCPTextAnnotation ccpTA : inputList) {
			annotationsToReturn.add(new WrappedCCPTextAnnotation(ccpTA));
		}
		return annotationsToReturn;
	}

	@Override
	public ClassMention getClassMention() {
		return new WrappedCCPClassMention(wrappedCCPTextAnnotation.getClassMention());
	}

	@Override
	public String getAnnotationComment() {
		return getAnnotationMetadata().getAnnotationComment();
	}

	@Override
	public void setAnnotationComment(String comment) {
		UIMA_Annotation_Util.addAnnotationCommentProperty(wrappedCCPTextAnnotation, comment, jcas);
	}

}
