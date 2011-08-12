package edu.ucdenver.ccp.nlp.core.annotation.metadata;

public class AnnotationCommentProperty extends AnnotationMetadataProperty {
	private final String comment;

	public AnnotationCommentProperty(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

}
