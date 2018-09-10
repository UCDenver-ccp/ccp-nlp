package edu.ucdenver.ccp.nlp.uima.annotators.pos_tagger;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2018 Regents of the University of Colorado
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.interfaces.IPOSTagger;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.SentenceProcessor_AE;

public abstract class POSTagger_AE extends SentenceProcessor_AE {

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute
	 * extractor implementation to use
	 */
	public static final String PARAM_TOKEN_CAS_INSERTER_CLASS = "tokenCasInserterClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the TokenCasInserter implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.annotators.pos_tagger.CcpTokenCasInserter")
	private String tokenCasInserterClassName;

	/**
	 * this {@link TokenCasInserter} will be initialized based on the class name
	 * specified by the tokenCasInserterClassName parameter
	 */
	private TokenCasInserter tokenCasInserter;

	private IPOSTagger posTagger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			posTagger = initPosTagger();
			tokenCasInserter = (TokenCasInserter) ConstructorUtil.invokeConstructor(tokenCasInserterClassName);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	protected abstract IPOSTagger initPosTagger() throws IOException;

	@Override
	protected void processSentence(JCas jCas, Annotation sentenceAnnotation) throws CASException {
		String sentenceText = sentenceAnnotation.getCoveredText();
		int characterOffset = sentenceAnnotation.getBegin();
		List<TextAnnotation> tokenAnnots = posTagger.getTokensWithPOSTagsFromSentence(characterOffset, sentenceText);

		for (TextAnnotation tokenAnnot : tokenAnnots) {
			Collection<?> slotValues = TextAnnotationUtil.getSlotValues(tokenAnnot,
					SlotMentionType.TOKEN_PARTOFSPEECH.name());

			// should be a single slot value, if one exists at all
			if (slotValues != null && !slotValues.isEmpty()) {
				tokenCasInserter.insertToken(tokenAnnot.getAnnotationSpanStart(), tokenAnnot.getAnnotationSpanEnd(),
						tokenAnnot.getAnnotator().getFirstName(), slotValues.iterator().next().toString(), jCas);
			} else {
				tokenCasInserter.insertToken(tokenAnnot.getAnnotationSpanStart(), tokenAnnot.getAnnotationSpanEnd(),
						tokenAnnot.getAnnotator().getFirstName(), jCas);
			}
		}

	}

}
