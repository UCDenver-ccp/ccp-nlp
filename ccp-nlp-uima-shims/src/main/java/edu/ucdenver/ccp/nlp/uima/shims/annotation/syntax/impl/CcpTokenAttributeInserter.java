/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.Lemma;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.PartOfSpeech;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.Stem;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeInserter;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpTokenAttributeInserter implements TokenAttributeInserter {


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.core.uima.shim.annotation.syntax.TokenAttributeInserter#insertPartOfSpeech
	 * (org.apache.uima.jcas.tcas.Annotation, java.lang.String, java.lang.String)
	 */
	@Override
	public void insertPartOfSpeech(Annotation annotation,PartOfSpeech pos) {
		if (annotation instanceof CCPTextAnnotation) {
			WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
			StringSlotMention posSlot = (StringSlotMention) wrappedCcpTa.getClassMention()
					.getPrimitiveSlotMentionByName(SlotMentionTypes.TOKEN_PARTOFSPEECH);
			posSlot.addSlotValue(pos.serializeToString());
		} else
			throw new IllegalArgumentException(String.format(
					"Unable to insert part of speech into a non-CCPTextAnnotation. Expected %s but was %s",
					CCPTextAnnotation.class.getName(), annotation.getClass().getName()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.core.uima.shim.annotation.syntax.TokenAttributeInserter#insertStem(org
	 * .apache.uima.jcas.tcas.Annotation, java.lang.String)
	 */
	@Override
	public void insertStem(Annotation annotation, Stem stem) {
		if (annotation instanceof CCPTextAnnotation) {
			WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
			StringSlotMention stemSlot = (StringSlotMention) wrappedCcpTa.getClassMention()
					.getPrimitiveSlotMentionByName(SlotMentionTypes.TOKEN_STEM);
			stemSlot.addSlotValue(stem.serializeToString());
		} else
			throw new IllegalArgumentException(String.format(
					"Unable to insert part of speech into a non-CCPTextAnnotation. Expected %s but was %s",
					CCPTextAnnotation.class.getName(), annotation.getClass().getName()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.core.uima.shim.annotation.syntax.TokenAttributeInserter#insertLemma(
	 * org.apache.uima.jcas.tcas.Annotation, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void insertLemma(Annotation annotation, Lemma lemma) {
		if (annotation instanceof CCPTextAnnotation) {
			WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
			StringSlotMention lemmaSlot = (StringSlotMention) wrappedCcpTa.getClassMention()
					.getPrimitiveSlotMentionByName(SlotMentionTypes.TOKEN_LEMMA);
			lemmaSlot.addSlotValue(lemma.serializeToString());
		} else
			throw new IllegalArgumentException(String.format(
					"Unable to insert part of speech into a non-CCPTextAnnotation. Expected %s but was %s",
					CCPTextAnnotation.class.getName(), annotation.getClass().getName()));

	}

}
