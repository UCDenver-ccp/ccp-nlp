package edu.ucdenver.ccp.nlp.uima.annotators.pos_tagger;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.IPOSTagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.SentenceProcessor_AE;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenNlpPOSTaggerAE extends POSTagger_AE {

	private static final Annotator annotator = new Annotator(77777, "OpenNLP", "OpenNLP", "OpenNLP");

	@Override
	protected IPOSTagger initPosTagger() throws IOException {
		return new OpenNlpPOSTagger();
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			Class<? extends AnnotationDataExtractor> sentenceDataExtractorClass,
			Class<? extends TokenCasInserter> tokenCasInserterClass) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(OpenNlpPOSTaggerAE.class, tsd,
				SentenceProcessor_AE.PARAM_SENTENCE_DATA_EXTRACTOR_CLASS, sentenceDataExtractorClass.getName(),
				POSTagger_AE.PARAM_TOKEN_CAS_INSERTER_CLASS, tokenCasInserterClass.getName());
	}

	public static class OpenNlpPOSTagger implements IPOSTagger {

		private static final String TOKEN = ClassMentionType.TOKEN.name();
		private static final String POS_SLOT_NAME = SlotMentionType.TOKEN_PARTOFSPEECH.name();
		private POSTaggerME posTagger;
		private Tokenizer tokenizer;

		public OpenNlpPOSTagger() throws IOException {
			try (InputStream modelStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(),
					"/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/token-en-maxent.bin")) {
				TokenizerModel model = new TokenizerModel(modelStream);
				tokenizer = new TokenizerME(model);
			}

			try (InputStream modelStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(),
					"/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/tagger-en-maxent.bin")) {
				POSModel model = new POSModel(modelStream);
				posTagger = new POSTaggerME(model);
			}
		}

		@Override
		public void initialize(int taggerType, String[] args) throws InitializationException {
			throw new UnsupportedOperationException("This initialize method has no content.");
		}

		@Override
		public List<TextAnnotation> getTokensWithPOSTagsFromSentence(int characterOffset, String inputText) {
			String[] tokenizedSentence = tokenizer.tokenize(inputText);
			List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
			String[] tags = posTagger.tag(tokenizedSentence);
			Span[] spans = tokenizer.tokenizePos(inputText);
			for (int i = 0; i < spans.length; i++) {
				Span span = spans[i];
				DefaultTextAnnotation annot = new DefaultTextAnnotation(span.getStart() + characterOffset,
						span.getEnd() + characterOffset);
				annot.setCoveredText(span.getCoveredText(inputText).toString());
				DefaultClassMention cm = new DefaultClassMention(TOKEN);
				annot.setClassMention(cm);
				cm.addPrimitiveSlotMention(cm.createPrimitiveSlotMention(POS_SLOT_NAME, tags[i]));
				annot.setAnnotator(annotator);
				annots.add(annot);
			}
			return annots;
		}

		@Override
		public List<TextAnnotation> getTokensFromText(int characterOffset, String inputText) {
			return getTokensWithPOSTagsFromSentence(characterOffset, inputText);
		}

	}

}
