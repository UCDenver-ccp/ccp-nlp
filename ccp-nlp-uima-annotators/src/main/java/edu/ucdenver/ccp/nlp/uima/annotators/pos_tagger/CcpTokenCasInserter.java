/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.annotators.pos_tagger;

import org.apache.uima.cas.CASException;

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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPPrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 *
 */
public class CcpTokenCasInserter implements TokenCasInserter {

	@Override
	public Annotation insertToken(int spanStart, int spanEnd, String annotatorName, JCas jCas) throws CASException {
		return UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.TOKEN.typeName(), spanStart, spanEnd,
				annotatorName, jCas);
	}

	@Override
	public Annotation insertToken(int spanStart, int spanEnd, String annotatorName, String pos, JCas jCas)
			throws CASException {
		CCPTextAnnotation ccpTa = UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.TOKEN.typeName(),
				spanStart, spanEnd, annotatorName, jCas);
		CCPPrimitiveSlotMention posSlot = new CCPPrimitiveSlotMention(jCas);
		posSlot.setMentionName(SlotMentionType.TOKEN_PARTOFSPEECH.name());
		UIMA_Util.addSlotValue(ccpTa.getClassMention(), SlotMentionType.TOKEN_PARTOFSPEECH.name(), pos);
		return ccpTa;
	}

}
