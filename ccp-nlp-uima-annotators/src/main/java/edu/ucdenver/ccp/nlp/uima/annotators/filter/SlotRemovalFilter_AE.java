/*
 * SlotRemovalFilter_AE.java
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

package edu.ucdenver.ccp.nlp.uima.annotators.filter;

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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.annotation.impl.WrappedCCPTextAnnotation;


/**
 * This filter allows slots to be removed globally by type: REMOVE_ALL, REMOVE_COMPLEX, REMOVE_PRIMITIVE
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class SlotRemovalFilter_AE extends JCasAnnotator_ImplBase {

	public enum SlotRemovalOption {
		REMOVE_NONE,
		REMOVE_ALL,
		REMOVE_COMPLEX,
		REMOVE_PRIMITIVE
	}

	// /* ==== Class/Slot pairs to remove configuration ==== */
	// /**
	// * Parameter name used in the UIMA descriptor file for the array of String specifying which
	// * slots should be removed
	// */
	// public static final String PARAM_CLASS_SLOT_PAIRS_TO_REMOVE = ConfigurationParameterFactory
	// .createConfigurationParameterName(SlotRemovalFilter_AE.class, "classSlotPairsToRemove");
	//
	// /**
	// * the directory where the inlined-annotation files will be written
	// */
	// @ConfigurationParameter(description = "", mandatory = true)
	// private String[] classSlotPairsToRemove;
	//
	// private Map<String, Set<String>> classToSlotNamesToRemoveMap;

	public static final String PARAM_SLOT_REMOVE_OPTION = "removeOption";

	@ConfigurationParameter(description = "", mandatory = true)
	private SlotRemovalOption removeOption = null;

	private Logger logger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		// classToSlotNamesToRemoveMap = new HashMap<String, Set<String>>();
		// /* read in input parameters, and initialize a list of slots to remove */
		// for (String classSlotPair : classSlotPairsToRemove) {
		//
		// if (classSlotPair.equals(REMOVE_ALL)) {
		// removeAllSlots = true;
		// break;
		// }
		// String[] classSlotName = classSlotPair.split("\\|");
		// if (classSlotName.length != 2) {
		// throw new ResourceInitializationException(new
		// IllegalArgumentException("Invalid class/slot pairing: "
		// + classSlotPair));
		// }
		// String classNameRegex = classSlotName[0];
		// String slotName = classSlotName[1];
		// CollectionsUtil.addToOne2ManyUniqueMap(classNameRegex, slotName,
		// classToSlotNamesToRemoveMap);
		// }
		logger = context.getLogger();
		logger.log(Level.INFO, "SlotRemovalFilter_AE initialized. Will remove slots defined by: " + removeOption);
	}

	/**
	 * cycle through all annotations and remove any slots that have been inputted by the user
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();

		for (Iterator<CCPTextAnnotation> annotIter = JCasUtil.iterator(jCas, CCPTextAnnotation.class); annotIter
				.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			TextAnnotation ta = new WrappedCCPTextAnnotation(ccpTa);
			try {
				switch (removeOption) {
				case REMOVE_ALL:
					if (ta.getClassMention().getPrimitiveSlotMentions().size() > 0
							|| ta.getClassMention().getComplexSlotMentions().size() > 0) {
//						logger.log(Level.INFO, "Removing ALL slots from: " + ta.toString());
						ta.getClassMention().setComplexSlotMentions(new ArrayList<ComplexSlotMention>());
						ta.getClassMention().setPrimitiveSlotMentions(new ArrayList<PrimitiveSlotMention>());
					}
					break;
				case REMOVE_COMPLEX:
					if (ta.getClassMention().getComplexSlotMentions().size() > 0) {
//						logger.log(Level.INFO, "Removing complex slots from: " + ta.toString());
						ta.getClassMention().setComplexSlotMentions(new ArrayList<ComplexSlotMention>());
//						logger.log(Level.INFO, "# complex slots remaining: "
//								+ ta.getClassMention().getComplexSlotMentions().size());
					}
					break;
				case REMOVE_PRIMITIVE:
					if (ta.getClassMention().getPrimitiveSlotMentions().size() > 0) {
//						logger.log(Level.INFO, "Removing primitive slots from: " + ta.toString());
						ta.getClassMention().setPrimitiveSlotMentions(new ArrayList<PrimitiveSlotMention>());
					}
					break;
				case REMOVE_NONE:
					// don't do anything
					break;
				default:
					throw new IllegalArgumentException("Unhandled SlotRemoveOption: " + removeOption.name());
				}
			} catch (Exception e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
		// else {
		// throw new UnsupportedOperationException("This needs to be implemented still");
		// String mentionName = ccpCM.getMentionName().toLowerCase();
		// logger.debug("MentionName: " + mentionName + "  MENTION NAMES OF INTEREST: "
		// + classesOfInterest.toString());
		// if (classesOfInterest.contains(mentionName)) {
		// logger.debug("Found class of interest: " + mentionName);
		//
		// FSArray ccpSlots = ccpCM.getSlotMentions();
		//
		// /*
		// * since the FSArray class has no remove() method, we will create a set of
		// * CCPSlotMentions that we will keep. If any slots are removed, they will
		// * not be added to this list, and then this new list will be put into a new
		// * FSArray, and replace the original FSArray. This is a bit of a
		// * work-around... perhaps there's a better way.
		// */
		// List<CCPSlotMention> slotsToKeep = new ArrayList<CCPSlotMention>();
		// boolean removedAtLeastOneSlot = false;
		//
		// if (ccpSlots != null) {
		// for (int i = 0; i < ccpSlots.size(); i++) {
		// CCPSlotMention ccpSM = (CCPSlotMention) ccpSlots.get(i);
		// String slotName = ccpSM.getMentionName().toLowerCase();
		// if (slotsToRemoveList.contains(mentionName + "|" + slotName)) {
		// logger.debug("Found slot of interest: " + slotName);
		// /* then remove this slot */
		// removedAtLeastOneSlot = true;
		// } else {
		// /*
		// * we are not going to remove this slot, so we store it in the
		// * list
		// */
		// slotsToKeep.add(ccpSM);
		// }
		// }
		// }
		//
		// /*
		// * if we removed a slot, then we need to replace the FSArray for this
		// * CCPClassMention
		// */
		// if (removedAtLeastOneSlot) {
		// FSArray keptSlots = new FSArray(jcas, slotsToKeep.size());
		// for (int i = 0; i < keptSlots.size(); i++) {
		// keptSlots.set(i, slotsToKeep.get(i));
		// }
		// ccpCM.setSlotMentions(keptSlots);
		// }
		// }
		// }

		// }
		// }

		// /*
		// * now remove annotations that had class mention types not in the classMentionTypesToKeep
		// * list
		// */
		// for (CCPTextAnnotation ccpTA : annotationsToRemove) {
		// ccpTA.removeFromIndexes();
		// ccpTA = null;
		// }

	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd, SlotRemovalOption removalOption)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(SlotRemovalFilter_AE.class, tsd,
				PARAM_SLOT_REMOVE_OPTION, removalOption.name());
	}
}
