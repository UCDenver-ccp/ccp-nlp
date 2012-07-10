/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */package edu.ucdenver.ccp.nlp.uima.shims.annotation.syntactic.token.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeech;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.PartOfSpeechDecorator;

/**
 * Implementation of the {@link PartOfSpeechDecorator} interface specific for the CCP type system
 * 
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
		return UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.TOKEN.typeName(), span.getSpanStart(),
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
				SlotMentionType.TOKEN_PARTOFSPEECH.typeName());
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
				SlotMentionType.TOKEN_PARTOFSPEECH.typeName());
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
