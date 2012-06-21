package edu.ucdenver.ccp.wrapper.knowtator.annotation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;
import edu.ucdenver.ccp.wrapper.knowtator.mention.impl.WrappedKnowtatorClassMention;

public class WrappedKnowtatorAnnotation extends TextAnnotation {

	private SimpleInstance knowtatorAnnotation;
	private KnowtatorUtil ku;

	public WrappedKnowtatorAnnotation(SimpleInstance knowtatorAnnotation, KnowtatorUtil ku) {
		super(knowtatorAnnotation, ku);
	}

	@Override
	protected void initializeFromWrappedAnnotation(Object... wrappedObjectAndGlobalVars) {
		if (wrappedObjectAndGlobalVars[0] instanceof SimpleInstance) {
			this.knowtatorAnnotation = (SimpleInstance) wrappedObjectAndGlobalVars[0];
			this.ku = (KnowtatorUtil) wrappedObjectAndGlobalVars[1];
//			this.classMention = new WrappedKnowtatorClassMention(ku.getKnowtatorClassMentionFromAnnotation(knowtatorAnnotation), ku);
		} else {
			throw new KnowledgeRepresentationWrapperException("Expected SimpleInstance. Cannot wrap "
					+ wrappedObjectAndGlobalVars[0].getClass().getName() + " object in a WrappedKnowtatorAnnotation.");
		}

	}

	@Override
	public Set<AnnotationSet> getAnnotationSets() {
		return ku.getUtilAnnotationSetsFromKnowtatorAnnotation(knowtatorAnnotation);
	}

	@Override
	public void addAnnotationSet(AnnotationSet annotationSet) {
		ku.addAnnotationSet(knowtatorAnnotation, annotationSet);
	}

	@Override
	public Annotator getAnnotator() {
		return ku.getUtilAnnotatorFromKnowtatorAnnotation(knowtatorAnnotation);
	}

	@Override
	public void setAnnotationSets(Set<AnnotationSet> annotationSets) {
		ku.setAnnotationSets(knowtatorAnnotation, annotationSets);
	}

	@Override
	public void setAnnotator(Annotator annotator) {
		ku.setAnnotator(knowtatorAnnotation, annotator);
	}

	@Override
	public void addSpan(Span span) {
		List<Span> spanList = getSpans();
		spanList.add(span);
		setSpans(spanList);
	}

	@Override
	public List<Span> getSpans() {
		return ku.getUtilSpanList(knowtatorAnnotation);
	}

	@Override
	public void setSpan(Span span) {
		List<Span> spans = new ArrayList<Span>();
		spans.add(span);
		ku.setSpans(knowtatorAnnotation, spans);
	}

	@Override
	public void setSpans(List<Span> spans) {
		ku.setSpans(knowtatorAnnotation, spans);
		sortSpanList();
	}

	@Override
	public ClassMention createClassMention(String classMentionName) {
		return new WrappedKnowtatorClassMention(ku.createKnowtatorClassMention(classMentionName), ku);
	}

//	@Override
//	public ClassMention getClassMention() {
//		return new WrappedKnowtatorClassMention(ku.getKnowtatorClassMentionFromAnnotation(knowtatorAnnotation), ku);
//	}

	@Override
	public int getAnnotationID() {
//		logger.warn("Annotation IDs are not supported inside of Knowtator.");
		return -1;
		// throw new
		// UnsupportedOperationException("Annotation IDs are not supported inside Knowtator.");
	}

	@Override
	public void setAnnotationID(int annotationID) {
		throw new UnsupportedOperationException("Annotation IDs are not supported inside Knowtator.");
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
//		logger.warn("Annotation MetaData objects are not currently supported inside Knowtator.");
		return new AnnotationMetadata();
//		throw new UnsupportedOperationException(
//				"Annotation MetaData objects are not currently supported inside Knowtator.");
	}

	@Override
	public void setAnnotationMetadata(AnnotationMetadata annotationMetadata) {
		throw new UnsupportedOperationException(
				"Annotation MetaData objects are not currently supported inside Knowtator.");
	}

	@Override
	public String getCoveredText() {
		return ku.getCoveredText(knowtatorAnnotation);
	}

	@Override
	public int getDocumentCollectionID() {
//		logger.warn("Document Collection IDs are not supported inside of Knowtator.");
		return -1;
	}

	@Override
	public String getDocumentID() {
		return ku.getTextSourceNameFromKnowtatorAnnotation(knowtatorAnnotation);
	}

	@Override
	public int getDocumentSectionID() {
//		logger.warn("Document Section IDs are not supported inside of Knowtator.");
		return -1;
	}

	@Override
	public SimpleInstance getWrappedObject() {
		return knowtatorAnnotation;
	}

	@Override
	public void offsetAnnotationSpans(int offset) {
		ku.offsetSpans(knowtatorAnnotation, offset);
	}

	@Override
	public void setAnnotationSpanEnd(int spanEnd) {
		ku.setAnnotationSpanEnd(knowtatorAnnotation, spanEnd);
	}

	@Override
	public void setAnnotationSpanStart(int spanStart) {
		ku.setAnnotationSpanStart(knowtatorAnnotation, spanStart);
	}

	@Override
	public void setCoveredText(String coveredText) {
		ku.setCoveredText(knowtatorAnnotation, coveredText);
	}

	@Override
	public void setDocumentCollectionID(int documentCollectionID) {
		throw new UnsupportedOperationException("Document Collection IDs are not supported inside of Knowtator.");
	}

	@Override
	public void setDocumentID(String documentID) {
		// the document ID is associated through attachment of a knowtator text source. It should
		// not be changed externally.
		logger.warn("Attempt to change document ID for WrappedKnowtatorAnnotation has been ignored. "
				+ "The annotation is associated with a specific Knowtator text source and the "
				+ "text source name should not be changed externally.");
	}

	@Override
	public void setDocumentSectionID(int documentSectionID) {
		throw new UnsupportedOperationException("Document Section IDs are not supported inside of Knowtator.");
	}

	@Override
	protected void sortSpanList() {
		ku.sortSpans(knowtatorAnnotation);
	}

	@Override
	public ClassMention getClassMention() {
		SimpleInstance knowtatorCM = ku.getKnowtatorClassMentionFromAnnotation(knowtatorAnnotation);
		if (knowtatorCM == null) {
			logger.warn(String.format("Null Knowtator CM detected for annotation: [%s] [%d..%d] '%s'", getDocumentID(), getAnnotationSpanStart(), getAnnotationSpanEnd(), getCoveredText()));
			return null;
		}
		return new WrappedKnowtatorClassMention(knowtatorCM, ku);
	}

	@Override
	public String getAnnotationComment() {
		return ku.getAnnotationComment(knowtatorAnnotation);
	}

	@Override
	public void setAnnotationComment(String comment) {
		ku.setAnnotationComment(knowtatorAnnotation, comment);
	}

}
