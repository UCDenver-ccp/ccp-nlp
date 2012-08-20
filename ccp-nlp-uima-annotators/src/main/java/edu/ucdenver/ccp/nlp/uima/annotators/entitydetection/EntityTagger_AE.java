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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.SentenceAnnotationProcessor;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This abstract class represented a named entity tagging analysis engine.
 * 
 * @author Bill Baumgartner
 */
public abstract class EntityTagger_AE extends SentenceAnnotationProcessor {
	private static Logger logger = Logger.getLogger(EntityTagger_AE.class);

	protected IEntityTagger entityTagger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.annotators.SentenceAnnotationProcessor#processSentence(java
	 * .lang.String, int, org.apache.uima.jcas.JCas)
	 */
	@Override
	protected List<TextAnnotation> processSentence(String sentenceText, int sentenceStartOffset, JCas jCas) {
		String documentID = UIMA_Util.getDocumentID(jCas);
		List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();
		if (sentenceText.trim().length() > 0) {
			/* extract entities from the sentence */
			List<TextAnnotation> annotations = entityTagger.getEntitiesFromText(sentenceText, documentID);

			/* correct the annotations by applying the spanOffset */
			if (sentenceStartOffset != 0)
				for (TextAnnotation ta : annotations) {
					ta.setAnnotationSpanEnd(ta.getAnnotationSpanEnd() + sentenceStartOffset);
					ta.setAnnotationSpanStart(ta.getAnnotationSpanStart() + sentenceStartOffset);
				}

			annotationsToReturn = removeEmptyAnnotationsFromList(annotations);
		}
		return annotationsToReturn;
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
