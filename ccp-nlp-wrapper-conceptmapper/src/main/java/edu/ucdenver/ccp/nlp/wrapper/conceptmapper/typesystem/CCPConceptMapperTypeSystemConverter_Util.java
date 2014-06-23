/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.typesystem;

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
		annotator.setFirstName("ConceptMapper");
		annotator.setLastName("ConceptMapper");
		annotator.setAnnotatorID(999);
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
		annotator.setFirstName("ConceptMapper");
		annotator.setLastName("Tokenizer");
		annotator.setAnnotatorID(990);
		ccpTA.setAnnotator(annotator);

		return ccpTA;
	}

}
