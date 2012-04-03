/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.PartOfSpeech;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax.TokenAttributeExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpTokenAttributeExtractor implements TokenAttributeExtractor {

	private static final String TOKEN_TYPE = ClassMentionTypes.TOKEN;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.core.uima.shim.annotation.syntax.TokenAttributeExtractor#getTokenType()
	 */
	@Override
	public String getTokenType() {
		return TOKEN_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.core.uima.shim.annotation.syntax.TokenAttributeExtractor#getPartsOfSpeech
	 * (org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<PartOfSpeech> getPartsOfSpeech(Annotation annotation) {
		if (annotation instanceof CCPTextAnnotation) {
			List<PartOfSpeech> partsOfSpeech = new ArrayList<PartOfSpeech>();
			WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
			StringSlotMention posSlot = (StringSlotMention) wrappedCcpTa.getClassMention()
					.getPrimitiveSlotMentionByName(SlotMentionTypes.TOKEN_PARTOFSPEECH);
			for (String serializedPos : posSlot.getSlotValues())
				partsOfSpeech.add(PartOfSpeech.deserializeFromString(serializedPos));
			return partsOfSpeech;
		}
		throw new IllegalArgumentException(String.format(
				"Unable to return part of speech for a non-CCPTextAnnotation. Expected %s but was %s",
				CCPTextAnnotation.class.getName(), annotation.getClass().getName()));
	}

}
