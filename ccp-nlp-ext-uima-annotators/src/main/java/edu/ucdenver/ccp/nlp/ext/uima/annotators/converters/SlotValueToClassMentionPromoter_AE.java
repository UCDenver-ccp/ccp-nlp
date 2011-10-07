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

/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * @author helen l johnson
 * 
 */
public class SlotValueToClassMentionPromoter_AE extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(SlotValueToClassMentionPromoter_AE.class);

	public final static String PARAM_PROMOTE_SLOT_OF_MENTION_TYPE_REGEX = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "mentionTypeRegexString");
	@ConfigurationParameter(mandatory = true, description = "A regex designed to match the class mention name for annotations that may contain the slot whose value will be promoted to a class mention type")
	private String mentionTypeRegexString;
	private Pattern mentionTypeRegex;

	public final static String PARAM_PROMOTE_SLOT_TYPE = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "slotNameToPromote");
	@ConfigurationParameter(mandatory = true, description = "The name of the slot whose value will be promoted to a class mention type")
	private String slotNameToPromote;

	@Override
	public void initialize(UimaContext uc) throws ResourceInitializationException {
		super.initialize(uc);
		mentionTypeRegex = Pattern.compile(mentionTypeRegexString);
		logger.info("Initialized to promote values from slot <" + slotNameToPromote + "> of classMentionType <"
				+ mentionTypeRegexString + "> to class mention status.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<CCPTextAnnotation> annotationsToAddToJcas = new ArrayList<CCPTextAnnotation>();

		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation annot = (CCPTextAnnotation) annotIter.next();

			// process those annotations that match the regex slotsOfClassMentionsToPromote
			String annotMentionName = annot.getClassMention().getMentionName();
			Matcher matcher = mentionTypeRegex.matcher(annotMentionName);

			// if that CM has a mentionName of the type specified,
			if (matcher.find()) {
				try {
					List<String> slotValuesToPromote = getSlotValuesOfInterest(annot);
					for (String slotValue : slotValuesToPromote) {
						CCPTextAnnotation newCCPTA = UIMA_Util.cloneAnnotation(annot, jcas);
						newCCPTA.getClassMention().setMentionName(slotValue);
						annotationsToAddToJcas.add(newCCPTA);
					}
				} catch (CASException ce) {
					throw new AnalysisEngineProcessException(ce);
				}
			}
		}

		for (CCPTextAnnotation ta : annotationsToAddToJcas) {
			ta.addToIndexes();
		}
	}

	/**
	 * @param slotType
	 *            is the SlotType name that, when found, a SlotValue will be extracted and returned.
	 * @return ArrayList<String> of slotValue to promote, or an ArrayList of size 0 if no slots were
	 *         found.
	 * 
	 */
	private List<String> getSlotValuesOfInterest(CCPTextAnnotation ccpTa) {
		List<String> slotValues = new ArrayList<String>();
		WrappedCCPTextAnnotation wrappedTa = new WrappedCCPTextAnnotation(ccpTa);
		PrimitiveSlotMention<?> slot = wrappedTa.getClassMention().getPrimitiveSlotMentionByName(slotNameToPromote);
		if (slot != null) {
			for (Object slotValue : slot.getSlotValues())
				slotValues.add(slotValue.toString());
		}

		return slotValues;
	}

}
