/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation.syntactic.token.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeech;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeechDecorator;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpPartOfSpeechDecorator implements PartOfSpeechDecorator {

	/**
	 * @return an initialized {@link CCPTextAnnotation} with a mention name of "token"
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#newAnnotation(org.apache.uima.jcas.JCas,
	 *      java.lang.String, edu.ucdenver.ccp.uima.shims.annotation.Span)
	 */
	@Override
	public Annotation newAnnotation(JCas jcas, @SuppressWarnings("unused") String type, Span span) {
		return UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.TOKEN, span.getSpanStart(),
				span.getSpanEnd(), jcas);
	}

	/**
	 * Inserts information representing the input {@link PartOfSpeech} into the input
	 * {@link Annotation} which is assumed to be of type {@link CCPTextAnnotation} in this instance.
	 * 
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not a {@link CCPTextAnnotation}
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeechDecorator#insertPartOfSpeech
	 *      (org.apache.uima.jcas.tcas.Annotation,
	 *      edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeech)
	 */
	@Override
	public void insertPartOfSpeech(Annotation annotation, PartOfSpeech pos) {
		checkAnnotationType(annotation);
		WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		StringSlotMention posSlot = (StringSlotMention) wrappedCcpTa.getClassMention().getPrimitiveSlotMentionByName(
				SlotMentionTypes.TOKEN_PARTOFSPEECH);
		posSlot.addSlotValue(pos.serializeToString());
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeechDecorator#extractPartsOfSpeech
	 *      (org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<PartOfSpeech> extractPartsOfSpeech(Annotation annotation) {
		checkAnnotationType(annotation);
		List<PartOfSpeech> partsOfSpeech = new ArrayList<PartOfSpeech>();
		WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		StringSlotMention posSlot = (StringSlotMention) wrappedCcpTa.getClassMention().getPrimitiveSlotMentionByName(
				SlotMentionTypes.TOKEN_PARTOFSPEECH);
		for (String serializedPos : posSlot.getSlotValues()) {
			partsOfSpeech.add(PartOfSpeech.deserializeFromString(serializedPos));
		}
		return partsOfSpeech;
	}

	/**
	 * In the case of the {@link CcpPartOfSpeechDecorator}, the annotation to decorate is simple the
	 * input token annotation. A slot for the part-of-speech will be added to the token annotation.
	 * 
	 * @param tokenAnnotation
	 *            in this case, the input annotation represents the token annotation whose covered
	 *            text was used as input to a part-of-speech tagger
	 * 
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#getAnnotationToDecorate(org.apache
	 *      .uima.jcas.tcas.Annotation,
	 *      edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor)
	 */
	@Override
	public Annotation getAnnotationToDecorate(Annotation tokenAnnotation,
			@SuppressWarnings("unused") AnnotationDataExtractor annotationDataExtractor) {
		return tokenAnnotation;
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#decorateAnnotation(org.apache.
	 *      uima.jcas.tcas.Annotation, java.lang.String, java.lang.Object)
	 */
	@Override
	public void decorateAnnotation(Annotation annotation, @SuppressWarnings("unused") String attributeType,
			PartOfSpeech pos) {
		insertPartOfSpeech(annotation, pos);

	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#extractAttribute(org.apache.uima
	 *      .jcas.tcas.Annotation, java.lang.String)
	 */
	@Override
	public List<PartOfSpeech> extractAttribute(Annotation annotation, @SuppressWarnings("unused") String attributeType) {
		return extractPartsOfSpeech(annotation);
	}

	/**
	 * Checks that the input {@link Annotation} is a {@link CCPTextAnnotation}
	 * 
	 * @param annotation
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not a {@link CCPTextAnnotation}
	 * 
	 */
	private static void checkAnnotationType(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation)) {
			throw new IllegalArgumentException(
					"Expecting CCPTextAnnotation class. Unable to assign lemma information to annotation of type: "
							+ annotation.getClass().getName());
		}
	}

}
