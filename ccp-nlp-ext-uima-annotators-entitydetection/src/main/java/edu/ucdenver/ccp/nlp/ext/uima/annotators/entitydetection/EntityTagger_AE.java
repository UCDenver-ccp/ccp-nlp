/*
 * EntityTagger_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPSentenceAnnotation;

/**
 * This abstract class represented a named entity tagging analysis engine.
 * 
 * @author Bill Baumgartner
 */
public abstract class EntityTagger_AE extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(EntityTagger_AE.class);

	protected IEntityTagger entityTagger;

	/**
	 * Add entities to the document. If SentenceAnnotations are present, each sentence is processed individually. If no
	 * SentenceAnnotations are present, the entire document is tokenized (and it may be assumed by some implementations
	 * that the document represents a single sentence).
	 */
	public void process(JCas jcas) {
	//public void process(JCas jcas, ResultSpecification arg1)  {

		// String documentID = UIMA_Util.getDocumentID(jcas);

		UIMA_Util uimaUtil = new UIMA_Util();

		String documentID = UIMA_Util.getDocumentID(jcas);
		logger.info("Processing document: " + documentID);
		/*
		 * Check to see if there are any Sentence annotations in this CAS. If there are, then tokenize each sentence
		 * individually. If there are not, then treat the document text as a single sentence and tokenize it.
		 */
		int numSentenceAnnotations = jcas.getJFSIndexRepository().getAnnotationIndex(CCPSentenceAnnotation.type).size();
		if (numSentenceAnnotations > 0) {
			logger.debug("Processing sentences in CAS...");
			Iterator sentIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPSentenceAnnotation.type).iterator();
			while (sentIter.hasNext()) {
				Object possibleSent = sentIter.next();
				if (possibleSent instanceof CCPSentenceAnnotation) {
					CCPSentenceAnnotation ccpSentAnnot = (CCPSentenceAnnotation) possibleSent;
					String sentenceText = ccpSentAnnot.getCoveredText();
					int spanOffset = ccpSentAnnot.getBegin();

					if (sentenceText.trim().length() > 0) {
						/* extract entities from the sentence */
						List<TextAnnotation> annotations = entityTagger.getEntitiesFromText(sentenceText, documentID);

						/* correct the annotations by applying the spanOffset */
						for (TextAnnotation ta : annotations) {
							ta.setAnnotationSpanEnd(ta.getAnnotationSpanEnd() + spanOffset);
							ta.setAnnotationSpanStart(ta.getAnnotationSpanStart() + spanOffset);
						}

						List<TextAnnotation> annotationsToPutInCas = removeEmptyAnnotationsFromList(annotations);

						/* add the TextAnnotations to the JCas */
						uimaUtil.putTextAnnotationsIntoJCas(jcas, annotationsToPutInCas);
					} else {
						// do nothing, empty sentence
					}
				} else {
					logger.error("Expecting CCPSentenceAnnotation, but instead got: " + possibleSent.getClass().getName());
				}

			}
		} else {
			logger.debug("Processing document text directly (No sentences were found).");
			/* treat the entire document text as a single sentence */
			String documentText = jcas.getDocumentText();

			/* tokenize the text */
			List<TextAnnotation> annotations = entityTagger.getEntitiesFromText(documentText, documentID);

			List<TextAnnotation> annotationsToPutInCas = removeEmptyAnnotationsFromList(annotations);

			/* add the TextAnnotations to the JCas */
			uimaUtil.putTextAnnotationsIntoJCas(jcas, annotationsToPutInCas);
		}
	}

	protected List<TextAnnotation> removeEmptyAnnotationsFromList(List<TextAnnotation> annotations) {
		/* remove any empty annotations */
		List<TextAnnotation> annotationsToKeep = new ArrayList<TextAnnotation>();
		for (TextAnnotation ta : annotations) {
			if (ta.getCoveredText().trim().length() > 0) {
				annotationsToKeep.add(ta);
			} else {
				logger.debug("Removing empty annotation: " + ta.getSingleLineRepresentation());
			}
		}
		return annotationsToKeep;
	}

}
