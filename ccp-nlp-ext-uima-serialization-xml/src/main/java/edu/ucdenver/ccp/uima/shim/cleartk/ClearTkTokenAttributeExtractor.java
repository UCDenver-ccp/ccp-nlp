package edu.ucdenver.ccp.uima.shim.cleartk;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.token.type.Token;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.ext.syntax.PartOfSpeech;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor;

public class ClearTkTokenAttributeExtractor implements TokenAttributeExtractor {

	// @Override
	// public Collection<Object> getAnnotationAttributes(Annotation annotation, AnnotationAttribute
	// attribute) {
	// switch (attribute) {
	// case PART_OF_SPEECH:
	//
	// break;
	//
	// default:
	// throw new IllegalArgumentException(String.format(
	// "The %s AnnotationAttributeExtractor is unable to return attributes of type: %s", getClass()
	// .getName(), attribute.name()));
	// }
	// }

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor#getPartsOfSpeech(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<PartOfSpeech> getPartsOfSpeech(Annotation annotation) {
		Token token = (Token) annotation;
		return CollectionsUtil.createList(new PartOfSpeech(token.getPos(), PartOfSpeech.UNKNOWN_TAG_SET));
	}

	/**
	 * @see edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor#getTokenType()
	 */
	@Override
	public String getTokenType() {
		return Token.class.getName();
	}

}
