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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IPOSTagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.SentenceAnnotationProcessor;

/**
 * 
 * This abstract class represents a tokenizer analysis engine. If SentenceAnnotations are present,
 * they are tagged sequentially, otherwise the entire document text is assumed to be a single
 * sentence.
 * 
 * @author William A. Baumgartner, Jr.
 * 
 */

public abstract class POSTagger_AE extends SentenceAnnotationProcessor {

	protected IPOSTagger posTagger;
	private Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
	}

	/**
	 * Add tokens to the document with POS tags attached in a slot. If SentenceAnnotations are
	 * present, each sentence is processed individually. If no SentenceAnnotations are present, the
	 * entire document is tokenized (and it may be assumed by some implementations that the document
	 * represents a single sentence).
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		super.process(jCas);
		resetTokenNumbering(jCas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.annotators.SentenceAnnotationProcessor#processSentence(java
	 * .lang.String, int, org.apache.uima.jcas.JCas)
	 */
	@Override
	protected List<TextAnnotation> processSentence(String sentenceText, int sentenceStartOffset, JCas jCas) {

		/* tokenize the sentence */
		List<TextAnnotation> annotations = posTagger.getTokensWithPOSTagsFromText(sentenceText);

		/* correct the annotations by applying the spanOffset */
		if (sentenceStartOffset != 0)
			for (TextAnnotation ta : annotations) {
				ta.setAnnotationSpanEnd(ta.getAnnotationSpanEnd() + sentenceStartOffset);
				ta.setAnnotationSpanStart(ta.getAnnotationSpanStart() + sentenceStartOffset);
			}

		return annotations;
	}

	/**
	 * Iterates through all token annotations in the cas and re-assigns a token number starting at
	 * zero.
	 * 
	 * @param jcas
	 * @throws AnalysisEngineProcessException
	 */
	private void resetTokenNumbering(JCas jcas) throws AnalysisEngineProcessException {
		int tokenNumber = 0;
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator(); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotIter.next();
			if (ccpTa.getClassMention().getMentionName().equalsIgnoreCase(ClassMentionTypes.TOKEN)) {
				try {
					UIMA_Util.setSlotValue(ccpTa.getClassMention(), SlotMentionTypes.TOKEN_NUMBER, tokenNumber++);
				} catch (CASException e) {
					throw new AnalysisEngineProcessException(e);
				}
			}
		}
	}

}
