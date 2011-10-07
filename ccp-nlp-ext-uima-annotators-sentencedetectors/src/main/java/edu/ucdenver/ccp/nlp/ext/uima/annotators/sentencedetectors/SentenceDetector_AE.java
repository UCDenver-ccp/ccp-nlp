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
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.ext.uima.syntax.util.UIMASyntacticAnnotation_Util;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		System.out.println("Sentence detector - treat line breaks as sentence boundaries: "
				+ treatLineBreaksAsSentenceBoundaries);
	}

	/**
	 * Add <code>SentenceAnnotations</code> to the document.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* get document text */
		String documentText = jcas.getDocumentText();
		/* get the document ID */
		String documentID = UIMA_Util.getDocumentID(jcas);

		/* parse into sentences using LingPipe Sentence Chunker */
		int charOffset = 0;
		if (documentText != null) {
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
						throw new RuntimeException("Sentence offsets incorrect. for document: "
								+ documentID
								+ " Expected: "
								+ sentence.getCoveredText()
								+ " but was covering: '"
								+ documentText.substring(sentence.getAnnotationSpanStart(),
										sentence.getAnnotationSpanEnd()) + "'");
				sentenceAnnotations.addAll(sentencesFromText);
				charOffset = charOffset + textChunk.length() + 1;
			}

			/* add sentence annotations to the CAS */
			UIMA_Util uimaUtil = new UIMA_Util();
			uimaUtil.putTextAnnotationsIntoJCas(jcas, sentenceAnnotations);

			/* convert annotations with class mention name = "sentence" to CCPSentenceAnnotation */
			UIMASyntacticAnnotation_Util.normalizeSyntacticAnnotations(jcas);

		} else {
			warn("There is no document text associated with document ID [" + documentID + "]");
		}

	}

	private void warn(String message) {
		System.err.println("WARNING --  "
				+ this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")) + message);
	}

}
