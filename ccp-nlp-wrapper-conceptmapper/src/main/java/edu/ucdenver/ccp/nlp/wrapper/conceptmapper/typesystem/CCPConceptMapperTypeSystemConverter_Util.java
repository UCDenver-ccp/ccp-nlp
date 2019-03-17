package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.typesystem;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.OntologyTerm;

/**
 * This class serves as a conversion utility to transform the output of the UIMA Sandbox ConceptMapper AE
 * (OntologyTerms) to the equivalent CCPTextAnnotation object(s).
 * 
 *  (more detail here)
 * 
 * @author Bill Baumgartner
 * 
 */
public class CCPConceptMapperTypeSystemConverter_Util {

	public static CCPTextAnnotation convertOntologyTerm(OntologyTerm ot, JCas jcas) throws AnalysisEngineProcessException {
		return convertOntologyTerm(ot, jcas, false); 
	}

	public static CCPTextAnnotation convertOntologyTerm(OntologyTerm ot, JCas jcas, boolean addSlotForCanonicalName) 
	throws AnalysisEngineProcessException {

		String type = ot.getDictCanon(); // canonical name
		String id = ot.getID();

		CCPTextAnnotation ccpTA = new CCPTextAnnotation(jcas);
		ccpTA.setBegin(ot.getBegin());
		ccpTA.setEnd(ot.getEnd());
		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName(id);

		CCPAnnotator annotator = new CCPAnnotator(jcas);
		annotator.setAffiliation("UIMA Sandbox");
		annotator.setName("ConceptMapper");
		annotator.setAnnotatorID("999");
		ccpTA.setAnnotator(annotator);

		/* Add a slot for the type */
		if (addSlotForCanonicalName) {
			CCPStringSlotMention slot = new CCPStringSlotMention(jcas);
			slot.setMentionName(SlotMentionType.CANONICAL_NAME.typeName());
			StringArray slotValues = new StringArray(jcas,1);
			slotValues.set(0,type);
			slot.setSlotValues(slotValues);
			       
			FSArray slotMentions = new FSArray(jcas,1);
			slotMentions.set(0, slot);
			ccpCM.setSlotMentions(slotMentions);
		}
		       
		try {
			UIMA_Util.setCCPClassMentionForCCPTextAnnotation(ccpTA, ccpCM);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

		FSArray spans = new FSArray(jcas, 1);
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(ot.getBegin());
		ccpSpan.setSpanEnd(ot.getEnd());
		spans.set(0, ccpSpan);
		ccpTA.setSpans(spans);

		return ccpTA;
	}

	public static CCPTextAnnotation convertToken(TokenAnnotation token, JCas jcas, int tokenNumber)
			throws AnalysisEngineProcessException {

		CCPTextAnnotation ccpTA = new CCPTextAnnotation(jcas);
		ccpTA.setBegin(token.getBegin());
		ccpTA.setEnd(token.getEnd());

		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName(ClassMentionType.TOKEN.typeName());

		CCPIntegerSlotMention ccpSM = new CCPIntegerSlotMention(jcas);
		ccpSM.setMentionName(SlotMentionType.TOKEN_NUMBER.typeName());
		IntegerArray slotValues = new IntegerArray(jcas, 1);
		slotValues.set(0, tokenNumber);
		ccpSM.setSlotValues(slotValues);

		FSArray ccpSlotMentions = new FSArray(jcas, 1);
		ccpSlotMentions.set(0, ccpSM);
		ccpCM.setSlotMentions(ccpSlotMentions);
		ccpTA.setClassMention(ccpCM);

		try {
			UIMA_Util.setCCPClassMentionForCCPTextAnnotation(ccpTA, ccpCM);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

		FSArray spans = new FSArray(jcas, 1);
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(token.getBegin());
		ccpSpan.setSpanEnd(token.getEnd());
		spans.set(0, ccpSpan);
		ccpTA.setSpans(spans);

		CCPAnnotator annotator = new CCPAnnotator(jcas);
		annotator.setAffiliation("UIMA Sandbox");
		annotator.setName("ConceptMapper-Tokenizer");
		annotator.setAnnotatorID("990");
		ccpTA.setAnnotator(annotator);

		return ccpTA;
	}

}
