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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.annotators.pos_tagger.OpenNlpPOSTaggerAE.OpenNlpPOSTagger;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.CcpSentenceCasInserter;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.OpenNlpSentenceDetectorAE;
import edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;

public class OpenNlpPosTaggerAETest extends DefaultUIMATestCase {
	// 012345678901234567890123456789012345678901234567890123456789
	private String[] paragraph = { "This is a sentence.",
			// 012345678901234567890123456789012345678901234567890123456789
			"Here is another sentence.",
			// 012345678901234567890123456789012345678901234567890123456789
			"This paragraph has three sentences." };

	@Test
	public void testPOSTaggerModule() throws IOException {

		OpenNlpPOSTagger detector = new OpenNlpPOSTagger();
		List<TextAnnotation> tokenAnnots = detector.getTokensWithPOSTagsFromSentence(0, paragraph[0]);
		assertEquals(5, tokenAnnots.size());
		assertEquals(new Span(0, 4), tokenAnnots.get(0).getAggregateSpan());
		assertEquals(new Span(5, 7), tokenAnnots.get(1).getAggregateSpan());
		assertEquals(new Span(8, 9), tokenAnnots.get(2).getAggregateSpan());
		assertEquals(new Span(10, 18), tokenAnnots.get(3).getAggregateSpan());
		assertEquals(new Span(18, 19), tokenAnnots.get(4).getAggregateSpan());

		assertNotNull(tokenAnnots.get(0).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(1).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(2).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(3).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(4).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));

		tokenAnnots = detector.getTokensWithPOSTagsFromSentence(0, paragraph[1]);
		assertEquals(5, tokenAnnots.size());
		assertEquals(new Span(0, 4), tokenAnnots.get(0).getAggregateSpan());
		assertEquals(new Span(5, 7), tokenAnnots.get(1).getAggregateSpan());
		assertEquals(new Span(8, 15), tokenAnnots.get(2).getAggregateSpan());
		assertEquals(new Span(16, 24), tokenAnnots.get(3).getAggregateSpan());
		assertEquals(new Span(24, 25), tokenAnnots.get(4).getAggregateSpan());

		assertNotNull(tokenAnnots.get(0).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(1).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(2).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(3).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(4).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));

		// test the sentence character offset here as well
		int offset = 10;
		tokenAnnots = detector.getTokensWithPOSTagsFromSentence(offset, paragraph[2]);
		assertEquals(6, tokenAnnots.size());
		assertEquals(new Span(offset + 0, offset + 4), tokenAnnots.get(0).getAggregateSpan());
		assertEquals(new Span(offset + 5, offset + 14), tokenAnnots.get(1).getAggregateSpan());
		assertEquals(new Span(offset + 15, offset + 18), tokenAnnots.get(2).getAggregateSpan());
		assertEquals(new Span(offset + 19, offset + 24), tokenAnnots.get(3).getAggregateSpan());
		assertEquals(new Span(offset + 25, offset + 34), tokenAnnots.get(4).getAggregateSpan());
		assertEquals(new Span(offset + 34, offset + 35), tokenAnnots.get(5).getAggregateSpan());

		assertNotNull(tokenAnnots.get(0).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(1).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(2).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(3).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(4).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));
		assertNotNull(tokenAnnots.get(5).getClassMention()
				.getPrimitiveSlotMentionByName(SlotMentionType.TOKEN_PARTOFSPEECH.name()));

	}

	@Override
	protected void initJCas() throws UIMAException, IOException {
		jcas.setDocumentText(paragraph[0] + " " + paragraph[1] + " " + paragraph[2]);
	}

	@Test
	public void testPOSTaggerAE() throws ResourceInitializationException, AnalysisEngineProcessException {

		// add sentences - required by the POSTagger_AE
		addSentencesToJCas();

		AnalysisEngineDescription posTaggerDesc = OpenNlpPOSTaggerAE.createAnalysisEngineDescription(getTypeSystem(),
				CcpAnnotationDataExtractor.class, CcpTokenCasInserter.class);
		int annotCount_before = JCasUtil.select(jcas, CCPTextAnnotation.class).size();
		AnalysisEngine posTaggerAE = AnalysisEngineFactory.createEngine(posTaggerDesc);
		posTaggerAE.process(jcas);
		int annotCount_after = JCasUtil.select(jcas, CCPTextAnnotation.class).size();
		assertEquals("16 tokens should have been added to the CAS", 16, annotCount_after - annotCount_before);
	}

	public void addSentencesToJCas() throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngineDescription detectorDesc = OpenNlpSentenceDetectorAE
				.createAnalysisEngineDescription(getTypeSystem(), CcpSentenceCasInserter.class, true);
		int annotCount_before = JCasUtil.select(jcas, CCPTextAnnotation.class).size();
		AnalysisEngine detectorAE = AnalysisEngineFactory.createEngine(detectorDesc);
		detectorAE.process(jcas);
		int annotCount_after = JCasUtil.select(jcas, CCPTextAnnotation.class).size();
		assertEquals("3 sentences annotations should have been added to the CAS", 3,
				annotCount_after - annotCount_before);
	}

}
