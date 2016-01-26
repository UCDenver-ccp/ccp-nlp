package edu.ucdenver.ccp.nlp.uima.annotators.converter;

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
 * Allows slot values to be promoted into full annotations. This may be useful for example if you
 * have annotations of type "protein" with slots containing a protein identifier and you instead
 * would like annotations where the type is the identifier. This AE allows the identifier to be
 * promoted up to be the annotation type.
 * 
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
	@ConfigurationParameter(mandatory = false, description = "This prefix, if specified, will be added to the slot value when creating the promoted class name. For example, a prefix of \"NCBITaxon:\" might be useful when promoting taxonomy ID slot fillers that are simply integers to full-fledged annotations, e.g. \"NCBITaxon:9606\".", defaultValue = "")
	private String slotValuePrefixToAdd;

	public final static String PARAM_TRANSFER_SLOT_VALUES = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "transferSlotValues");
	@ConfigurationParameter(description = "If true, then the new annotations that are created will have the same slot values as the original. If false, then the slot values are not transferred and the new annotations are linked to ClassMentions with no slots.", defaultValue = "true")
	private boolean transferSlotValues;

	public final static String PARAM_DELETE_SOURCE_ANNOTATION = ConfigurationParameterFactory
			.createConfigurationParameterName(SlotValueToClassMentionPromoter_AE.class, "deleteSourceAnnotation");
	@ConfigurationParameter(description = "If true, then the annotation that contains the slot value being promoted is removed from the CAS after the slot value has been promoted. Note that all annotations with a type that matches the mentionTypeRegexString will be deleted if this parameter is set to true, regardless of whether or not they contained a slot value that got promoted.", defaultValue = "false")
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
					if (deleteSourceAnnotation) {
						annotationsToDeleteFromJcas.add(ccpTa);
					}
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
				PARAM_TRANSFER_SLOT_VALUES, transferSlotValues, PARAM_DELETE_SOURCE_ANNOTATION, deleteSourceAnnotation,
				PARAM_SLOT_VALUE_PREFIX_TO_ADD, slotValuePrefixToAdd);
	}

}
