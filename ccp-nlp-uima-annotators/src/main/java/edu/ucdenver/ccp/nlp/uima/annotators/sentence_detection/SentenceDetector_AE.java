/*
 * LingPipeSentenceDetector_AE.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.ConfigurationParameterFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.ISentenceDetector;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This abstract class is the foundation for a sentence detector analysis
 * engine. It processes the document text and adds
 * <code>SentenceAnnotation</code> objects to the CAS.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public abstract class SentenceDetector_AE extends JCasAnnotator_ImplBase {

	protected ISentenceDetector sentenceDetector;

	public static final String PARAM_TREAT_LINE_BREAKS_AS_SENTENCE_BOUNDARIES = "treatLineBreaksAsSentenceBoundaries";
	@ConfigurationParameter(description = "If set to true, then line breaks will be treated as sentence boundaries")
	boolean treatLineBreaksAsSentenceBoundaries = false;

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute
	 * extractor implementation to use
	 */
	public static final String PARAM_SENTENCE_CAS_INSERTER_CLASS = "sentenceCasInserterClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the SentenceCasInserter implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.annotators.sentence_detection.CcpSentenceCasInserter")
	private String sentenceCasInserterClassName;

	/**
	 * this {@link SentenceCasInserter} will be initialized based on the class
	 * name specified by the sentenceCasInserterClassName parameter
	 */
	private SentenceCasInserter sentenceCasInserter;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		context.getLogger().log(Level.INFO,
				"Sentence detector - treat line breaks as sentence boundaries: " + treatLineBreaksAsSentenceBoundaries);
		sentenceCasInserter = (SentenceCasInserter) ConstructorUtil.invokeConstructor(sentenceCasInserterClassName);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String documentText = jCas.getDocumentText();
		String documentID = UIMA_Util.getDocumentID(jCas);

		int charOffset = 0;
		String[] chunks = new String[] { documentText };
		if (treatLineBreaksAsSentenceBoundaries) {
			chunks = documentText.split("\\n");
		}
		List<TextAnnotation> sentenceAnnotations = new ArrayList<TextAnnotation>();
		for (String textChunk : chunks) {
			List<TextAnnotation> sentencesFromText = sentenceDetector.getSentencesFromText(charOffset, textChunk);
			for (TextAnnotation sentence : sentencesFromText) {
				if (!sentence.getCoveredText().equals(
						documentText.substring(sentence.getAnnotationSpanStart(), sentence.getAnnotationSpanEnd()))) {
					throw new RuntimeException("Sentence offsets incorrect. for document: " + documentID + " Expected: "
							+ sentence.getCoveredText() + " but was covering: '"
							+ documentText.substring(sentence.getAnnotationSpanStart(), sentence.getAnnotationSpanEnd())
							+ "'");
				}
			}
			sentenceAnnotations.addAll(sentencesFromText);
			charOffset = charOffset + textChunk.length() + 1;
		}

		for (TextAnnotation sentence : sentenceAnnotations) {
			sentenceCasInserter.insertSentence(sentence.getAnnotationSpanStart(), sentence.getAnnotationSpanEnd(),
					jCas);
		}

	}

}
