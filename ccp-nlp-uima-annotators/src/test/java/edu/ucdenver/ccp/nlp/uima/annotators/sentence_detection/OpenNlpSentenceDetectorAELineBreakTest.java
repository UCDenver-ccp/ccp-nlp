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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.annotators.sentence_detection.OpenNlpSentenceDetectorAE.OpenNlpSentenceDetector;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;

public class OpenNlpSentenceDetectorAELineBreakTest extends DefaultUIMATestCase {

	/* @formatter:off*/
	                                       /*                                                                                                        1         1         1         1         1         1         1         1         1         1         2         2         2         2         2         2         2         2         2         2         3         3         3         3         3         3         3         3         3         3
	                                       /*           1         2         3         4         5         6         7         8           9          0         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6         7         8         9         1         2         3         4         5         6         7         8         9 */
 	                                       /* 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678 9 012345678 9012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789*/
	private String paragraphWithLineBreaks = "PINK1 Protects against Oxidative Stress by Phosphorylating Mitochondrial Chaperone TRAP1\n\nAbstract\nMutations in the PTEN induced putative kinase 1 (PINK1) gene cause an autosomal recessive form of Parkinson disease (PD). So far, no substrates of PINK1 have been reported, and the mechanism by which PINK1 mutations lead to neurodegeneration is unknown.";
	/* @formatter:on*/

	@Ignore("This test shows that the OpneNlp sentence detection algorithm does not treat line breaks as sentence boundaries inherently.")
	@Test
	public void testSentenceDetectionModule() throws IOException {
		OpenNlpSentenceDetector detector = new OpenNlpSentenceDetector();
		List<TextAnnotation> sentenceAnnots = detector.getSentencesFromText(paragraphWithLineBreaks);
		assertEquals(4, sentenceAnnots.size());
		assertEquals(new Span(0, 88), sentenceAnnots.get(0).getAggregateSpan());
		assertEquals(new Span(90, 98), sentenceAnnots.get(1).getAggregateSpan());
		assertEquals(new Span(99, 220), sentenceAnnots.get(2).getAggregateSpan());
		assertEquals(new Span(221, 352), sentenceAnnots.get(3).getAggregateSpan());

	}

	@Override
	protected void initJCas() throws UIMAException, IOException {
		jcas.setDocumentText(paragraphWithLineBreaks);
	}

	@Test
	public void testSentenceDetectionAE() throws ResourceInitializationException, AnalysisEngineProcessException {

		boolean treatLineBreaksAsSentenceBoundaries = true;
		AnalysisEngineDescription detectorDesc = OpenNlpSentenceDetectorAE.createAnalysisEngineDescription(
				getTypeSystem(), CcpSentenceCasInserter.class, treatLineBreaksAsSentenceBoundaries);

		int annotCount_before = JCasUtil.select(jcas, CCPTextAnnotation.class).size();

		AnalysisEngine detectorAE = AnalysisEngineFactory.createEngine(detectorDesc);
		detectorAE.process(jcas);

		int annotCount_after = JCasUtil.select(jcas, CCPTextAnnotation.class).size();

		assertEquals(4, annotCount_after - annotCount_before);

		Set<Span> expectedSentenceSpans = CollectionsUtil.createSet(new Span(0, 88), new Span(90, 98),
				new Span(99, 220), new Span(221, 352));
		for (CCPTextAnnotation ta : JCasUtil.select(jcas, CCPTextAnnotation.class)) {
			Span span = new Span(ta.getBegin(), ta.getEnd());
			assertTrue(expectedSentenceSpans.remove(span));
		}
		assertTrue(expectedSentenceSpans.isEmpty());

	}

}
