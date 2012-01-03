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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.ISentenceDetector;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * This abstract class is the foundation for a sentence detector analysis engine. It processes the
 * document text and adds <code>SentenceAnnotation</code> objects to the CAS.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public abstract class SentenceDetector_AE extends JCasAnnotator_ImplBase {

	protected ISentenceDetector sentenceDetector;

	public static final String PARAM_TREAT_LINE_BREAKS_AS_SENTENCE_BOUNDARIES = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceDetector_AE.class, "treatLineBreaksAsSentenceBoundaries");

	@ConfigurationParameter(description = "If set to true, then line breaks will be treated as sentence boundaries")
	boolean treatLineBreaksAsSentenceBoundaries = false;

	/* ==== SentenceCasInserter configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_SENTENCE_CAS_INSERTER_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceDetector_AE.class, "sentenceCasInserterClassName");

	/**
	 * The name of the SentenceCasInserter implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the SentenceCasInserter implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.CcpSentenceCasInserter")
	private String sentenceCasInserterClassName;

	/**
	 * this {@link SentenceCasInserter} will be initialized based on the class name specified by the
	 * sentenceCasInserterClassName parameter
	 */
	private SentenceCasInserter sentenceCasInserter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		context.getLogger().log(Level.INFO,
				"Sentence detector - treat line breaks as sentence boundaries: " + treatLineBreaksAsSentenceBoundaries);
		sentenceCasInserter = (SentenceCasInserter) ConstructorUtil.invokeConstructor(sentenceCasInserterClassName);
	}

	/**
	 * Add <code>SentenceAnnotations</code> to the document.
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		/* get document text */
		String documentText = jCas.getDocumentText();
		/* get the document ID */
		String documentID = UIMA_Util.getDocumentID(jCas);

		/* parse into sentences using LingPipe Sentence Chunker */
		int charOffset = 0;
		String[] chunks = new String[] { documentText };
		if (treatLineBreaksAsSentenceBoundaries) {
			chunks = documentText.split("\\n");
		}
		List<TextAnnotation> sentenceAnnotations = new ArrayList<TextAnnotation>();
		for (String textChunk : chunks) {
			List<TextAnnotation> sentencesFromText = sentenceDetector.getSentencesFromText(charOffset, textChunk);
			for (TextAnnotation sentence : sentencesFromText)
				if (!sentence.getCoveredText().equals(
						documentText.substring(sentence.getAnnotationSpanStart(), sentence.getAnnotationSpanEnd())))
					throw new RuntimeException(
							"Sentence offsets incorrect. for document: "
									+ documentID
									+ " Expected: "
									+ sentence.getCoveredText()
									+ " but was covering: '"
									+ documentText.substring(sentence.getAnnotationSpanStart(),
											sentence.getAnnotationSpanEnd()) + "'");
			sentenceAnnotations.addAll(sentencesFromText);
			charOffset = charOffset + textChunk.length() + 1;
		}
		
		for (TextAnnotation sentence: sentenceAnnotations)
			sentenceCasInserter.insertSentence(sentence.getAnnotationSpanStart(), sentence.getAnnotationSpanEnd(), jCas);

	}

}
