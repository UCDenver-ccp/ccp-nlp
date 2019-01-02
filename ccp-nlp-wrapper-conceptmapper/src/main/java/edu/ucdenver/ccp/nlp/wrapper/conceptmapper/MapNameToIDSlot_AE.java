package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * Take a CCPTextAnnotation and change the ClassMentionName
 * (identified by regex PARAM_ORIG_MENTION)
 * to something generic (PARAM_MENTION_TYPE) and move the 
 * original name into a slot with name "ID".
 * @author Karin Verspoor
 *
 */
public class MapNameToIDSlot_AE extends JCasAnnotator_ImplBase {

	public static Logger logger = Logger.getLogger(MapNameToIDSlot_AE.class);
	public static String PARAM_MENTION_TYPE = "MentionTypeOut";
	public static String PARAM_ORIG_MENTION = "MentionTypeIn_RegExp";
	
	Pattern mentionTypeInPattern;
	String mentionTypeOut;
	
	@Override	
	public void initialize(UimaContext context) 
	throws ResourceInitializationException {
		super.initialize(context);
			String mentionTypeIn = (String) context.getConfigParameterValue(PARAM_ORIG_MENTION);
			mentionTypeInPattern = Pattern.compile(mentionTypeIn);
			if (mentionTypeInPattern == null) {
				logger.error("MapNameToIDSlot_AE error: pattern \"" + mentionTypeIn + "\" doesn't compile. ");
				throw new ResourceInitializationException(new RuntimeException("MapNameToIDSlot_AE error: pattern \"" + mentionTypeIn + "\" doesn't compile. "));
			}
			mentionTypeOut = (String) context.getConfigParameterValue(PARAM_MENTION_TYPE);
	}

	@Override	
	public void process(JCas jcas)
	throws AnalysisEngineProcessException {
		try {
			FSIterator ccptaIterator = jcas.getAnnotationIndex(CCPTextAnnotation.type).iterator();
			while (ccptaIterator.hasNext()) {
					CCPTextAnnotation ccpta = (CCPTextAnnotation) ccptaIterator.next();
				String ccptaMentionName = ccpta.getClassMention().getMentionName();
				if (ccptaMentionName != null) {
					Matcher m = mentionTypeInPattern.matcher(ccptaMentionName);
					if ( m.matches() ) {
						CCPClassMention ccpcm = ccpta.getClassMention();
						ccpcm.setMentionName(mentionTypeOut);
						UIMA_Util.addSlotValue(ccpcm, "ID", ccptaMentionName);
					}
				}
			}
		} catch (CASException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException();
		}
	}


	public static AnalysisEngine createAnalysisEngine(
		TypeSystemDescription tsd,
		String outputType,
		String mentionRegex)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
			MapNameToIDSlot_AE.class, tsd,
			PARAM_MENTION_TYPE,	outputType,
			PARAM_ORIG_MENTION, mentionRegex);
	}


	public static AnalysisEngineDescription createAnalysisEngineDescription(
		TypeSystemDescription tsd,
		String outputType,
		String mentionRegex)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
			MapNameToIDSlot_AE.class, tsd,
			PARAM_MENTION_TYPE,	outputType,
			PARAM_ORIG_MENTION, mentionRegex);
	}


}
