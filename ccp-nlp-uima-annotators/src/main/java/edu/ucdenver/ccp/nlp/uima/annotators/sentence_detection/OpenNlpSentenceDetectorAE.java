package edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection;

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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.ISentenceDetector;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class OpenNlpSentenceDetectorAE extends SentenceDetector_AE {

	private static final Annotator annotator = new Annotator(77777, "OpenNLP", "OpenNLP", "OpenNLP");

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		try {
			sentenceDetector = new OpenNlpSentenceDetector();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		super.initialize(context);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			Class<? extends SentenceCasInserter> sentenceCasInserterClass, boolean treatLineBreaksAsSentenceBoundaries)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(OpenNlpSentenceDetectorAE.class, tsd,
				SentenceDetector_AE.PARAM_SENTENCE_CAS_INSERTER_CLASS, sentenceCasInserterClass.getName(),
				SentenceDetector_AE.PARAM_TREAT_LINE_BREAKS_AS_SENTENCE_BOUNDARIES,
				treatLineBreaksAsSentenceBoundaries);
	}

	public static class OpenNlpSentenceDetector implements ISentenceDetector {

		private static final String SENTENCE = "sentence";
		private SentenceDetectorME sentenceDetector;

		public OpenNlpSentenceDetector() throws IOException {
			try (InputStream modelStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(),
					"/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/sentence-en-maxent.bin")) {
				SentenceModel model = new SentenceModel(modelStream);
				sentenceDetector = new SentenceDetectorME(model);
			}
		}

		@Override
		public void initialize(int taggerType, String[] args) throws InitializationException {
			throw new UnsupportedOperationException("This initialize method has no content.");
		}

		@Override
		public List<TextAnnotation> getSentencesFromText(String inputText) {
			return getSentencesFromText(0, inputText);
		}

		/*
		 * the character offset is the offset to be applied to the generated
		 * annotations. It is not the offset from which to substring the text.
		 */
		@Override
		public List<TextAnnotation> getSentencesFromText(int characterOffset, String inputText) {
			List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
			Span[] spans = sentenceDetector.sentPosDetect(inputText);
			for (Span span : spans) {
				span.getStart();
				span.getEnd();
				span.getType();
				DefaultTextAnnotation annot = new DefaultTextAnnotation(span.getStart() + characterOffset,
						span.getEnd() + characterOffset);
				annot.setCoveredText(span.getCoveredText(inputText).toString());
				DefaultClassMention cm = new DefaultClassMention(SENTENCE);
				annot.setClassMention(cm);
				annot.setAnnotator(annotator);
				annots.add(annot);
			}
			return annots;
		}

	}

}
