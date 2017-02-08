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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.OpenNlpSentenceDetectorAE.OpenNlpSentenceDetector;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;

public class OpenNlpSentenceDetectorAETest extends DefaultUIMATestCase {

	private String paragraph = "This is a sentence. Here is another sentence. This paragraph has three sentences.";

	@Test
	public void testSentenceDetectionModule() throws IOException {

		OpenNlpSentenceDetector detector = new OpenNlpSentenceDetector();
		List<TextAnnotation> sentenceAnnots = detector.getSentencesFromText(paragraph);
		assertEquals(3, sentenceAnnots.size());
		assertEquals(new Span(0, 19), sentenceAnnots.get(0).getAggregateSpan());
		assertEquals(new Span(20, 45), sentenceAnnots.get(1).getAggregateSpan());
		assertEquals(new Span(46, 81), sentenceAnnots.get(2).getAggregateSpan());

	}

	@Override
	protected void initJCas() throws UIMAException, IOException {
		jcas.setDocumentText(paragraph);
	}

	@Test
	public void testSentenceDetectionAE() throws ResourceInitializationException, AnalysisEngineProcessException {

		AnalysisEngineDescription detectorDesc = OpenNlpSentenceDetectorAE
				.createAnalysisEngineDescription(getTypeSystem(), CcpSentenceCasInserter.class, true);

		int annotCount_before = JCasUtil.select(jcas, CCPTextAnnotation.class).size();

		AnalysisEngine detectorAE = AnalysisEngineFactory.createEngine(detectorDesc);
		detectorAE.process(jcas);

		int annotCount_after = JCasUtil.select(jcas, CCPTextAnnotation.class).size();

		assertEquals(3, annotCount_after - annotCount_before);

	}

}
