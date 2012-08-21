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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SlotValueToClassMentionPromoter_AE extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(SlotValueToClassMentionPromoter_AE.class);

	public final static String PARAM_CLASS_MENTION_NAME_REGEX = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "mentionTypeRegexString");
	@ConfigurationParameter(mandatory = true, description = "A regex designed to match the class mention name for annotations that may contain the slot whose value will be promoted to a class mention type")
	private String mentionTypeRegexString;

	public final static String PARAM_SLOT_NAME_TO_PROMOTE = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "slotNameToPromote");
	@ConfigurationParameter(mandatory = true, description = "The name of the slot whose value will be promoted to a class mention type")
	private String slotNameToPromote;

	public final static String PARAM_SLOT_VALUE_PREFIX_TO_ADD = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "slotValuePrefixToAdd");
	@ConfigurationParameter(mandatory = false, description = "This prefix, if specified, will be added to the slot value when creating the promoted class name. For example, a prefix of \"NCBITaxon:\" might be useful when promoting taxonomy ID slot fillers that are simply integers to full-fledged annotations, e.g. \"NCBITaxon:9606\".", defaultValue="")
	private String slotValuePrefixToAdd;

	public final static String PARAM_TRANSFER_SLOT_VALUES = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "transferSlotValues");
	@ConfigurationParameter(description = "If true, then the new annotations that are created will have the same slot values as the original. If false, then the slot values are not transferred and the new annotations are linked to ClassMentions with no slots.", defaultValue = "true")
	private boolean transferSlotValues;

	public final static String PARAM_DELETE_SOURCE_ANNOTATION = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "deleteSourceAnnotation");
	@ConfigurationParameter(description = "If true, then the annotation that contains the slot value being promoted is removed from the CAS after the slot value has been promoted.", defaultValue = "false")
	private boolean deleteSourceAnnotation;

	@Override
	public void initialize(UimaContext uc) throws ResourceInitializationException {
		super.initialize(uc);
		logger.info("Initialized to promote values from slot <" + slotNameToPromote + "> of classMentionType <"
				+ mentionTypeRegexString + "> to class mention status.");
	}

	/**
	 * Cycles through all CCPTextAnnotations in the CAS and promotes slot values to full annotations
	 * as specified by the AE configuration
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<CCPTextAnnotation> annotationsToAddToJcas = new ArrayList<CCPTextAnnotation>();
		List<CCPTextAnnotation> annotationsToDeleteFromJcas = new ArrayList<CCPTextAnnotation>();
		try {
			for (Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas); annotIter.hasNext();) {
				CCPTextAnnotation ccpTa = annotIter.next();
				if (ccpTa.getClassMention().getMentionName().matches(mentionTypeRegexString)) {
					List<String> slotValuesToPromote = getSlotValuesOfInterest(ccpTa);
					if (slotValuesToPromote.size() > 0) {
						for (String slotValue : slotValuesToPromote) {
							String newMentionName = slotValuePrefixToAdd + slotValue;
							CCPTextAnnotation newCCPTA = UIMA_Util.cloneAnnotation(ccpTa, jcas);
							if (transferSlotValues) {
								newCCPTA.getClassMention().setMentionName(newMentionName);
							} else {
								CCPClassMention cm = new CCPClassMention(jcas);
								cm.setMentionName(newMentionName);
								cm.setCcpTextAnnotation(newCCPTA);
								newCCPTA.setClassMention(cm);
							}
							annotationsToAddToJcas.add(newCCPTA);
						}
					}
				}
				if (deleteSourceAnnotation) {
					annotationsToDeleteFromJcas.add(ccpTa);
				}
			}
		} catch (CASException ce) {
			throw new AnalysisEngineProcessException(ce);
		}

		for (CCPTextAnnotation ta : annotationsToAddToJcas) {
			ta.addToIndexes();
		}

		for (CCPTextAnnotation ta : annotationsToDeleteFromJcas) {
			ta.removeFromIndexes();
			ta = null;
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
			for (Object slotValue : slot.getSlotValues()) {
				slotValues.add(slotValue.toString());
			}
		}
		return slotValues;
	}

	/**
	 * @param tsd
	 * @param slotNameToPromote
	 * @param classMentionNameRegex
	 * @param transferSlotValues
	 * @param deleteSourceAnnotation
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			String slotNameToPromote, String classMentionNameRegex, boolean transferSlotValues,
			boolean deleteSourceAnnotation, String slotValuePrefixToAdd) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(SlotValueToClassMentionPromoter_AE.class, tsd,
				PARAM_SLOT_NAME_TO_PROMOTE, slotNameToPromote, PARAM_CLASS_MENTION_NAME_REGEX, classMentionNameRegex,
				PARAM_TRANSFER_SLOT_VALUES, transferSlotValues, PARAM_DELETE_SOURCE_ANNOTATION, deleteSourceAnnotation, PARAM_SLOT_VALUE_PREFIX_TO_ADD, slotValuePrefixToAdd);
	}

}
