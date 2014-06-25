package edu.ucdenver.ccp.nlp.uima.annotators.entitydetection;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
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

import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.uima.annotators.SentenceAnnotationProcessor;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This abstract class represented a named entity tagging analysis engine.
 * 
 * @author Bill Baumgartner
 */
public abstract class EntityTagger_AE extends SentenceAnnotationProcessor {

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
				logger.log(Level.FINEST, "Removing empty annotation: " + ta.getSingleLineRepresentation());
			}
		}
		return annotationsToKeep;
	}

}
