/*
 * LingPipePOSTagger_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.postaggers;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.ext.uima.syntax.util.UIMASyntacticAnnotation_Util;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IPOSTagger;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPSentenceAnnotation;

/**
 * 
 * This abstract class represents a tokenizer analysis engine. If SentenceAnnotations are present,
 * they are tagged sequentially, otherwise the entire document text is assumed to be a single
 * sentence.
 * 
 * @author William A. Baumgartner, Jr.
 * 
 */

public abstract class POSTagger_AE extends org.uimafit.component.JCasAnnotator_ImplBase {

	protected IPOSTagger posTagger;
	protected final boolean DEBUG = false;

	/**
	 * Add tokens to the document with POS tags attached in a slot. If SentenceAnnotations are
	 * present, each sentence is processed individually. If no SentenceAnnotations are present, the
	 * entire document is tokenized (and it may be assumed by some implementations that the document
	 * represents a single sentence).
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		// String documentID = UIMA_Util.getDocumentID(jcas);

		UIMA_Util uimaUtil = new UIMA_Util();

		/*
		 * Check to see if there are any Sentence annotations in this CAS. If there are, then
		 * tokenize each sentence individually. If there are not, then treat the document text as a
		 * single sentence and tokenize it.
		 */
		int numSentenceAnnotations = jcas.getJFSIndexRepository().getAnnotationIndex(CCPSentenceAnnotation.type).size();
		if (numSentenceAnnotations > 0) {
			debug("Processing sentences...");
			FSIterator sentIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPSentenceAnnotation.type)
					.iterator();
			while (sentIter.hasNext()) {
				Object possibleSent = sentIter.next();
				if (possibleSent instanceof CCPSentenceAnnotation) {
					CCPSentenceAnnotation ccpSentAnnot = (CCPSentenceAnnotation) possibleSent;
					String sentenceText = ccpSentAnnot.getCoveredText();
					int spanOffset = ccpSentAnnot.getBegin();

					/* tokenize the sentence */
					List<TextAnnotation> annotations = posTagger.getTokensWithPOSTagsFromText(sentenceText);

					/* correct the annotations by applying the spanOffset */
					for (TextAnnotation ta : annotations) {
						ta.setAnnotationSpanEnd(ta.getAnnotationSpanEnd() + spanOffset);
						ta.setAnnotationSpanStart(ta.getAnnotationSpanStart() + spanOffset);
					}

					if (DEBUG) {
						debug("Extracted Entities: ");
						for (TextAnnotation ta : annotations) {
							ta.printAnnotation(System.err);
						}
					}

					/* add the TextAnnotations to the JCas */
					uimaUtil.putTextAnnotationsIntoJCas(jcas, annotations);
				} else {
					error("Expecting CCPSentenceAnnotation, but instead got: " + possibleSent.getClass().getName());
				}

			}
		} else {
			debug("Processing document text as a whole...");
			/* treat the entire document text as a single sentence */
			String documentText = jcas.getDocumentText();

			/* tokenize the text */
			List<TextAnnotation> annotations = posTagger.getTokensWithPOSTagsFromText(documentText);

			/* add the TextAnnotations to the JCas */
			uimaUtil.putTextAnnotationsIntoJCas(jcas, annotations);
		}

		/*
		 * Normalize syntactic tokens into TokenAnnotations, PhraseAnnotations, ClauseAnnotations,
		 * and SentenceAnnotations
		 */
		UIMASyntacticAnnotation_Util.normalizeSyntacticAnnotations(jcas);
		UIMASyntacticAnnotation_Util.resetTokenNumbering(jcas);
	}

	private void error(String message) {
		System.err.println("ERROR --  "
				+ this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")) + message);
	}

	private void debug(String message) {
		if (DEBUG) {
			System.err.println("DEBUG -- "
					+ this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1) + ": "
					+ message);
		}
	}
}
