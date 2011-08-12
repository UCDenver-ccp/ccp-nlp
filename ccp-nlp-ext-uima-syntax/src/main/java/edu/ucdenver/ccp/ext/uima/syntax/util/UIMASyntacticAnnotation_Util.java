/*
 * UIMASyntacticAnnotation_Util.java 
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

package edu.ucdenver.ccp.ext.uima.syntax.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation_Util;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPClauseAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPPhraseAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPSentenceAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPTokenAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPTypedDependency;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.TagSetLabel;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.ClauseAnnotationProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.ClauseTypeProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.LemmaProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.PartOfSpeechProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.PhraseAnnotationProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.PhraseTypeProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.StemProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.TokenAnnotationProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.TokenNumberProperty;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.metadata.TypedDependencyProperty;

/**
 * This class has been constructed to automate the conversion of annotations with class mention types of "token",
 * "phrase", "clause", "sentence" into TokenAnnotations, PhraseAnnotations, ClauseAnnotations, and SentenceAnnotations,
 * respectively.
 * 
 * @author Bill Baumgartner
 * 
 */
public class UIMASyntacticAnnotation_Util {

	private static Logger logger = Logger.getLogger(UIMASyntacticAnnotation_Util.class);

	public static final boolean DEBUG = false;

	/**
	 * Returns the part of speech property from a CCPTokenAnnotation. Warns the user of more than one part of speech
	 * property is detected.
	 * 
	 * @param ccpToken
	 * @return
	 */
	public static String getPartOfSpeechFromTokenAnnotation(CCPTokenAnnotation ccpToken) {
		String partOfSpeech = null;
		boolean foundPOS = false;
		Collection<TokenAnnotationProperty> tokenProperties = UIMASyntacticAnnotation_Util.getTokenAnnotationProperties(ccpToken);
		for (TokenAnnotationProperty tp : tokenProperties) {
			// FSArray tokenProperties = ccpToken.getTokenProperties();
			// if (tokenProperties != null) {
			// for (int i = 0; i < tokenProperties.size(); i++) {
			// TokenAnnotationProperty tp = (TokenAnnotationProperty) tokenProperties.get(i);
			if (tp instanceof PartOfSpeechProperty) {
				if (!foundPOS) {
					foundPOS = true;
					PartOfSpeechProperty posProperty = (PartOfSpeechProperty) tp;
					TagSetLabel partOfSpeechTSL = posProperty.getPartOfSpeech();
					partOfSpeech = partOfSpeechTSL.getLabel();
				} else {
					logger
							.warn("Detected a token with multiple part of speech properties while retrieving the POS. Returning only one of them...");
					UIMA_Util.printCCPTextAnnotation(ccpToken, System.err);
				}
			}
		}
		// } else {
		// logger.error("Cannot return part of speech. Token has not token properties: ");
		// UIMA_Util.printCCPTextAnnotation(ccpToken, System.err);
		// }
		if (!foundPOS) {
			logger.warn("No Part of speech detected for token: ");
			UIMA_Util.printCCPTextAnnotation(ccpToken, System.err);
		}
		return partOfSpeech;
	}

	/**
	 * Adds a phrase type property to a CCPPhraseAnnotation
	 * 
	 * @param phraseAnnot
	 * @param tagset
	 * @param phraseTypeLabel
	 * @param jcas
	 */
	public static void addPhraseTypeAnnotationProperty(CCPPhraseAnnotation phraseAnnot, String tagset, String phraseTypeLabel, JCas jcas) {
		TagSetLabel phraseTypeTSL = new TagSetLabel(jcas);
		phraseTypeTSL.setTagSet(tagset);
		phraseTypeTSL.setLabel(phraseTypeLabel);

		PhraseTypeProperty phraseTypeProperty = new PhraseTypeProperty(jcas);
		phraseTypeProperty.setPhraseType(phraseTypeTSL);

		UIMASyntacticAnnotation_Util.addPhraseAnnotationProperty(phraseAnnot, phraseTypeProperty, jcas);

		// FSArray currentPhraseProperties = phraseAnnot.getPhraseProperties();
		//
		// if (currentPhraseProperties == null) {
		// FSArray phraseAnnotationProperties = new FSArray(jcas, 1);
		// phraseAnnotationProperties.set(0, phraseTypeProperty);
		// phraseAnnot.setPhraseProperties(phraseAnnotationProperties);
		// } else {
		// FSArray phraseAnnotationProperties = new FSArray(jcas, currentPhraseProperties.size() + 1);
		// for (int i = 0; i < currentPhraseProperties.size(); i++) {
		// phraseAnnotationProperties.set(i, currentPhraseProperties.get(i));
		// }
		// phraseAnnotationProperties.set(phraseAnnotationProperties.size() - 1, phraseTypeProperty);
		// phraseAnnot.setPhraseProperties(phraseAnnotationProperties);
		// }

	}

	/**
	 * Assigns a token number to each token annotation in the CAS. If a token already has a token number, the token
	 * number is overwritten. Token numbers start from zero.
	 * 
	 * @param jcas
	 */
	public static void assignTokenNumbersToTokenAnnotations(JCas jcas) {
		/* get all text annotations in the CAS */
		FSIterator textAnnotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();

		int tokenNumber = 0;
		/*
		 * cycle through all annotations in the cas, and add explicit TokenAnnotations, PhraseAnnotations, etc. when
		 * appropriate
		 */
		while (textAnnotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) textAnnotIter.next();

			/* If this is an explicit CCPTokenAnnotation, set the token number field */
			if (ccpTA instanceof CCPTokenAnnotation) {
				setTokenNumber((CCPTokenAnnotation) ccpTA, tokenNumber, jcas);
			}

			/* All CCPTextAnnotation tokens will have a token number slot, set it here. */
			String mentionName = ccpTA.getClassMention().getMentionName();
			if (mentionName.equals(ClassMentionTypes.TOKEN)) {
				try {
					UIMA_Util.setSlotValue(ccpTA.getClassMention(), SlotMentionTypes.TOKEN_NUMBER, tokenNumber++);
				} catch (CASException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static void normalizeSyntacticAnnotations(JCas jcas) {

		List<CCPTextAnnotation> annotationsToAdd = new ArrayList<CCPTextAnnotation>();

		/* get all text annotations in the CAS */
		FSIterator textAnnotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();

		/*
		 * Initialize a list to store the annotations that will be removed that the end of this process. These
		 * annotation comprise "token" annotations that have been converted to TokenAnnotations, etc.
		 */
		List<CCPTextAnnotation> syntacticAnnotationsToRemove = new ArrayList<CCPTextAnnotation>();

		/*
		 * cycle through all annotations in the cas, and add explicit TokenAnnotations, PhraseAnnotations, etc. when
		 * appropriate
		 */
		while (textAnnotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) textAnnotIter.next();

			String mentionName = ccpTA.getClassMention().getMentionName();

			// debug(ccpTA.getClassMention().getMentionName() + "; " + ccpTA.getBegin() + " -- " + ccpTA.getEnd() + ": "
			// + ccpTA.getCoveredText());

			if (mentionName.equals(ClassMentionTypes.TOKEN)) {
				normalizeTokenAnnotation(ccpTA, annotationsToAdd, syntacticAnnotationsToRemove, jcas);
			} else if (mentionName.equals(ClassMentionTypes.PHRASE)) {
				normalizePhraseAnnotation(ccpTA, annotationsToAdd, syntacticAnnotationsToRemove, jcas);
			} else if (mentionName.equals(ClassMentionTypes.CLAUSE)) {
				normalizeClauseAnnotation(ccpTA, annotationsToAdd, syntacticAnnotationsToRemove, jcas);
			} else if (mentionName.equals(ClassMentionTypes.SENTENCE)) {
				normalizeSentenceAnnotation(ccpTA, annotationsToAdd, syntacticAnnotationsToRemove, jcas);
			}

		}

		/* add the new annotations */
		for (CCPTextAnnotation ccpTA : annotationsToAdd) {
			ccpTA.addToIndexes();
		}

		/* Removed the now unnecessary SyntacticAnnotations */
		for (CCPTextAnnotation ccpTA : syntacticAnnotationsToRemove) {
			ccpTA.removeFromIndexes();
		}

		// /* Add token numbers to tokens */
		// assignTokenNumbersToTokenAnnotations(jcas);
	}

	/**
	 * Resets the token numbering to the tokens in the JCas, starting with zero
	 * 
	 * @param jcas
	 */
	public static void resetTokenNumbering(JCas jcas) {
		assignTokenNumbersToTokenAnnotations(jcas);
	}

	/**
	 * Convert a CCPTextAnnotation of type "token" to a TokenAnnotation
	 * 
	 * @param ccpTA
	 * @param annotationsToAdd
	 * @param syntacticAnnotationsToRemove
	 * @param jcas
	 */
	private static void normalizeTokenAnnotation(CCPTextAnnotation ccpTA, List<CCPTextAnnotation> annotationsToAdd,
			List<CCPTextAnnotation> syntacticAnnotationsToRemove, JCas jcas) {

		logger.debug("Possible token annotation needing conversion to TokenAnnotation...");
		/* if it's already a TokenAnnotation, then we don't have to do anything */
		if (!(ccpTA instanceof CCPTokenAnnotation)) {
			logger.debug("... it was not a TokenAnnotation already.");

			CCPTokenAnnotation tokenAnnotation = new CCPTokenAnnotation(jcas);
			try {
				UIMA_Util.swapAnnotationInfo(ccpTA, tokenAnnotation);
			} catch (CASException ce) {
				ce.printStackTrace();
			}

			// AnnotationMetadata metaData = UIMA_Annotation_Util.getAnnotationMetadata(tokenAnnotation, jcas);
			//			
			// FSArray annotationMetadataProperties = metaData.getMetadataProperties();

			/* Initialize a list to hold TokenAnnotationProperty objects */
//			List<TokenAnnotationProperty> tokenAnnotationPropertyList = new ArrayList<TokenAnnotationProperty>();

			/* set POS */
			CCPStringSlotMention ccpStringSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
					SlotMentionTypes.TOKEN_PARTOFSPEECH);
			if (ccpStringSlotMention != null) {
				String posLabel = UIMA_Util.getFirstSlotValue(ccpStringSlotMention);

				CCPStringSlotMention tagSetSlot = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
						SlotMentionTypes.TAGSET);
				String tagset = DefaultTextAnnotation_Util.UNKNOWN_TAGSET;
				if (tagSetSlot != null) {
					tagset = UIMA_Util.getFirstSlotValue(tagSetSlot);
					if (tagset == null) {
						tagset = DefaultTextAnnotation_Util.UNKNOWN_TAGSET;
					}
				}
				PartOfSpeechProperty posProperty = new PartOfSpeechProperty(jcas);
				TagSetLabel tagSetLabel = new TagSetLabel(jcas);

				tagSetLabel.setLabel(posLabel);
				tagSetLabel.setTagSet(tagset);
				posProperty.setPartOfSpeech(tagSetLabel);

				/* Add PartOfSpeechProperty to the TokenAnnotationProperty list */
				// tokenAnnotationPropertyList.add(posProperty);
				addTokenAnnotationProperty(tokenAnnotation, posProperty, jcas);
			}

			// CCPNonComplexSlotMention posCSM = (CCPNonComplexSlotMention) UIMA_Util
			// .getSlotMentionByName(ccpTA, ComplexSlotMentionTypes.TOKEN_PART_OF_SPEECH);
			// if (posCSM != null) {
			// /* if the pos slot is not null, then look for a pos class inside */
			// FSArray partOfSpeeches = posCSM.getClassMentions();
			// if (partOfSpeeches != null) {
			// for (int i = 0; i < partOfSpeeches.size(); i++) {
			// Object possiblePOSMention = partOfSpeeches.get(i);
			// if (possiblePOSMention instanceof CCPClassMention) {
			// CCPClassMention posMention = (CCPClassMention) possiblePOSMention;
			// /* check to make sure this is a part of speech mention */
			// if (posMention.getMentionName().equals(ClassMentionTypes.TOKEN_PART_OF_SPEECH_LABEL)) {
			//
			// /* get the label and tag set */
			// CCPNonComplexSlotMention labelSM = (CCPNonComplexSlotMention) UIMA_Util.getSlotMentionByName(posMention,
			// SlotMentionTypes.TAGSET_LABEL);
			// String posLabel = UIMA_Util.getFirstSlotValue(labelSM);
			//
			// CCPNonComplexSlotMention tagsetSM = (CCPNonComplexSlotMention) UIMA_Util.getSlotMentionByName(posMention,
			// SlotMentionTypes.TAGSET);
			// String posTagset = UIMA_Util.getFirstSlotValue(tagsetSM);
			//
			// PartOfSpeechProperty posProperty = new PartOfSpeechProperty(jcas);
			// TagSetLabel tagSetLabel = new TagSetLabel(jcas);
			//
			// tagSetLabel.setLabel(posLabel);
			// tagSetLabel.setTagSet(posTagset);
			// posProperty.setPartOfSpeech(tagSetLabel);
			//								
			// /* Add PartOfSpeechProperty to the TokenAnnotationProperty list */
			// tokenAnnotationPropertyList.add(posProperty);
			// } else {
			// error("Token Part of Speech mention expected, but found: " + posMention.getMentionName());
			// }
			// }else {
			// error("Expected token part of speech class mention, but instead found: " +
			// possiblePOSMention.getClass().getName());
			// }
			// }
			// }
			// }

			// CCPNonComplexSlotMention ccpSlotMention;
			/* set token lemma */
			ccpStringSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA, SlotMentionTypes.TOKEN_LEMMA);
			if (ccpStringSlotMention != null) {
				String lemma = UIMA_Util.getFirstSlotValue(ccpStringSlotMention);
				LemmaProperty lemmaProperty = new LemmaProperty(jcas);
				lemmaProperty.setLemma(lemma);

				/* Add LemmaProperty to the TokenAnnotationProperty list */
				// tokenAnnotationPropertyList.add(lemmaProperty);
				addTokenAnnotationProperty(tokenAnnotation, lemmaProperty, jcas);
			}

			/* set token stem */
			ccpStringSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA, SlotMentionTypes.TOKEN_STEM);
			if (ccpStringSlotMention != null) {
				String stem = UIMA_Util.getFirstSlotValue(ccpStringSlotMention);
				StemProperty stemProperty = new StemProperty(jcas);
				stemProperty.setStem(stem);

				/* Add StemProperty to the TokenAnnotationProperty list */
				// tokenAnnotationPropertyList.add(stemProperty);
				addTokenAnnotationProperty(tokenAnnotation, stemProperty, jcas);
			}

			/* set token number */
			CCPIntegerSlotMention ccpIntegerSlotMention = (CCPIntegerSlotMention) UIMA_Util.getSlotMentionByName(ccpTA, SlotMentionTypes.TOKEN_NUMBER);
			if (ccpIntegerSlotMention != null) {
				setTokenNumber(tokenAnnotation, UIMA_Util.getFirstSlotValue(ccpIntegerSlotMention), jcas);// tokenAnnotation.setTokenNumber(Integer.parseInt(UIMA_Util.getFirstSlotValue(ccpSlotMention)));
			}

			/* set typed dependencies */
			List<CCPSlotMention> tdSlotMentions = UIMA_Util.getMultipleSlotMentionsByName(ccpTA, SlotMentionTypes.TOKEN_TYPED_DEPENDENCY);

			// logger.warn("#### td slot mentions for token: " + tdSlotMentions.size());

			if (tdSlotMentions != null) {
				/* initialize a new FSArray to hold the CCPTypedDependencies */
				for (int i = 0; i < tdSlotMentions.size(); i++) {
					CCPStringSlotMention tdSM = (CCPStringSlotMention) tdSlotMentions.get(i);
					for (String tdStr : tdSM.getSlotValues().toArray()) {
						// String tdStr = UIMA_Util.getFirstSlotValue(tdSM);

						TypedDependencyCls tdc;
						try {
							tdc = TypedDependencyCls_Util.parseTypedDependencyClsString(tdStr);
							CCPTypedDependency ccpTD = new CCPTypedDependency(jcas);
							ccpTD.setDependentTokenNum(tdc.getDependentTokenNum());
							ccpTD.setGovernorTokenNum(tdc.getGovernerTokenNum());
							ccpTD.setRelation(tdc.getRelation());
							ccpTD.addToIndexes();

							/* add it to the FSArray */
							TypedDependencyProperty typedDependencyProperty = new TypedDependencyProperty(jcas);
							typedDependencyProperty.setTypedDependency(ccpTD);

							/* Add TypedDependencyProperty to the TokenAnnotationProperty list */
							// tokenAnnotationPropertyList.add(typedDependencyProperty);
							addTokenAnnotationProperty(tokenAnnotation, typedDependencyProperty, jcas);
						} catch (InvalidTypedDependencyException e) {
							e.printStackTrace();
						}
					}
				}
			}

			// /* Add any generated TokenAnnotationProperties to the token annotation */
			// if (tokenAnnotationPropertyList.size() > 0) {
			// FSArray tokenAnnotationProperties = new FSArray(jcas, tokenAnnotationPropertyList.size());
			// for (int i = 0; i < tokenAnnotationPropertyList.size(); i++) {
			// tokenAnnotationProperties.set(i, tokenAnnotationPropertyList.get(i));
			// }
			// tokenAnnotation.setTokenProperties(tokenAnnotationProperties);
			// }

			/* Add the new TokenAnnotation to the CAS, and flag the old version for removal */
			syntacticAnnotationsToRemove.add(ccpTA);
			annotationsToAdd.add(tokenAnnotation);
		}
	}

	/**
	 * Adds a TokenAnnotationProperty to the FSArray containing token properties for the input CCPTokenAnnotation
	 * 
	 * @param tokenAnnotation
	 * @param tap
	 * @param jcas
	 */
	public static void addTokenAnnotationProperty(CCPTokenAnnotation tokenAnnotation, TokenAnnotationProperty tap, JCas jcas) {
		UIMA_Annotation_Util.addMetaDataProperty(tokenAnnotation, tap, jcas);
		// Collection<TOP> propertiesToAdd = new ArrayList<TOP>();
		// propertiesToAdd.add(tap);
		// FSArray properties = tokenAnnotation.getTokenProperties();
		// FSArray updatedProperties = UIMA_Util.addToFSArray(properties, propertiesToAdd, jcas);
		// tokenAnnotation.setTokenProperties(updatedProperties);
		// logger.warn("Added td token property.");
	}

	
	public static Collection<TypedDependencyProperty> getTokenTypedDependencyProperties(CCPTokenAnnotation tokenAnnotation) {
		Collection<TypedDependencyProperty> tdpList = new ArrayList<TypedDependencyProperty>();
		FSArray tokenProperties = tokenAnnotation.getAnnotationMetadata().getMetadataProperties();
		for (int i = 0; i < tokenProperties.size(); i++) {
			FeatureStructure tp = tokenProperties.get(i);
			if (tp instanceof TypedDependencyProperty) {
				tdpList.add((TypedDependencyProperty) tp);
			}
		}
		
		return tdpList;
	}
	
	/**
	 * Adds a PhraseAnnotationProperty to the FSArray containing phrase properties for the input CCPPhraseAnnotation
	 * 
	 * @param phraseAnnotation
	 * @param tap
	 * @param jcas
	 */
	public static void addPhraseAnnotationProperty(CCPPhraseAnnotation phraseAnnotation, PhraseAnnotationProperty tap, JCas jcas) {
		UIMA_Annotation_Util.addMetaDataProperty(phraseAnnotation, tap, jcas);
		// Collection<TOP> propertiesToAdd = new ArrayList<TOP>();
		// propertiesToAdd.add(tap);
		// FSArray properties = phraseAnnotation.getPhraseProperties();
		// FSArray updatedProperties = UIMA_Util.addToFSArray(properties, propertiesToAdd, jcas);
		// phraseAnnotation.setPhraseProperties(updatedProperties);
	}

	/**
	 * Adds a ClauseAnnotationProperty to the FSArray containing clause properties for the input CCPClauseAnnotation
	 * 
	 * @param clauseAnnotation
	 * @param tap
	 * @param jcas
	 */
	public static void addClauseAnnotationProperty(CCPClauseAnnotation clauseAnnotation, ClauseAnnotationProperty tap, JCas jcas) {
		UIMA_Annotation_Util.addMetaDataProperty(clauseAnnotation, tap, jcas);
		// Collection<TOP> propertiesToAdd = new ArrayList<TOP>();
		// propertiesToAdd.add(tap);
		// FSArray properties = clauseAnnotation.getClauseProperties();
		// FSArray updatedProperties = UIMA_Util.addToFSArray(properties, propertiesToAdd, jcas);
		// clauseAnnotation.setClauseProperties(updatedProperties);
	}

	/**
	 * Returns all TokenAnnotationProperty objects belonging to the input CCPTokenAnnotation
	 * 
	 * @param tokenAnnotation
	 * @param jcas
	 * @return
	 */
	public static Collection<TokenAnnotationProperty> getTokenAnnotationProperties(CCPTokenAnnotation tokenAnnotation) {
		return UIMA_Annotation_Util.getAnnotationProperties(tokenAnnotation, TokenAnnotationProperty.class);
	}

	/**
	 * Returns all PhraseAnnotationProperty objects belonging to the input CCPPhraseAnnotation
	 * 
	 * @param phraseAnnotation
	 * @param jcas
	 * @return
	 */
	public static Collection<PhraseAnnotationProperty> getPhraseAnnotationProperties(CCPPhraseAnnotation phraseAnnotation) {
		return UIMA_Annotation_Util.getAnnotationProperties(phraseAnnotation, PhraseAnnotationProperty.class);
	}

	/**
	 * Returns all ClauseAnnotationProperty objects belonging to the input CCPClauseAnnotation
	 * 
	 * @param clauseAnnotation
	 * @param jcas
	 * @return
	 */
	public static Collection<ClauseAnnotationProperty> getClauseAnnotationProperties(CCPClauseAnnotation clauseAnnotation) {
		return UIMA_Annotation_Util.getAnnotationProperties(clauseAnnotation, ClauseAnnotationProperty.class);
	}

	/**
	 * Returns the token number as stored by a TokenNumberProperty for the input CCPTokenAnnotation
	 * 
	 * @param tokenAnnotation
	 * @return
	 */
	public static Integer getTokenNumber(CCPTokenAnnotation tokenAnnotation) {
		Collection<TokenNumberProperty> tokenNumberProperties = UIMA_Annotation_Util.getAnnotationProperties(tokenAnnotation,
				TokenNumberProperty.class);
		if (tokenNumberProperties.size() == 1) {
			return Collections.list(Collections.enumeration(tokenNumberProperties)).get(0).getTokenNumber();
		} else {
			logger.error("Cannot return token number. The token annotation has an invalid number (" + tokenNumberProperties.size()
					+ ") of token number properties.");
			return null;
		}
	}

	/**
	 * Sets the token number property for the input CCPTokenAnnotation
	 * 
	 * @param tokenAnnotation
	 * @param tokenNumber
	 * @param jcas
	 */
	public static void setTokenNumber(CCPTokenAnnotation tokenAnnotation, int tokenNumber, JCas jcas) {
		Collection<TokenNumberProperty> tokenNumberProperties = UIMA_Annotation_Util.getAnnotationProperties(tokenAnnotation,
				TokenNumberProperty.class);
		if (tokenNumberProperties.size() == 0) {
			TokenNumberProperty tnp = new TokenNumberProperty(jcas);
			tnp.setTokenNumber(tokenNumber);
			addTokenAnnotationProperty(tokenAnnotation, tnp, jcas);
		} else if (tokenNumberProperties.size() == 1) {
			Collections.list(Collections.enumeration(tokenNumberProperties)).get(0).setTokenNumber(tokenNumber);
		} else {
			logger.error("Cannot set token number. The token annotation has an invalid number (" + tokenNumberProperties.size()
					+ ") of token number properties.");
		}
	}

	/**
	 * Convert a CCPTextAnnotation of type "phrase" to a PhraseAnnotation
	 * 
	 * @param ccpTA
	 * @param annotationsToAdd
	 * @param syntacticAnnotationsToRemove
	 * @param jcas
	 */
	private static void normalizePhraseAnnotation(CCPTextAnnotation ccpTA, List<CCPTextAnnotation> annotationsToAdd,
			List<CCPTextAnnotation> syntacticAnnotationsToRemove, JCas jcas) {
		/* if it's already a PhraseAnnotation, then we don't have to do anything */
		if (!(ccpTA instanceof CCPPhraseAnnotation)) {
			CCPPhraseAnnotation phraseAnnotation = new CCPPhraseAnnotation(jcas);
			try {
				UIMA_Util.swapAnnotationInfo(ccpTA, phraseAnnotation);
			} catch (CASException ce) {
				ce.printStackTrace();
			}

			/* Initialize a list to hold TokenAnnotationProperty objects */
			List<PhraseAnnotationProperty> phraseAnnotationPropertyList = new ArrayList<PhraseAnnotationProperty>();

			/* set phrase type */
			CCPStringSlotMention ccpSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
					SlotMentionTypes.PHRASE_TYPE);
			if (ccpSlotMention != null) {
				String phraseTypeLabel = UIMA_Util.getFirstSlotValue(ccpSlotMention);

				CCPStringSlotMention tagSetSlot = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
						SlotMentionTypes.TAGSET);

				String tagset = DefaultTextAnnotation_Util.UNKNOWN_TAGSET;
				if (tagSetSlot != null) {
					tagset = UIMA_Util.getFirstSlotValue(tagSetSlot);
					if (tagset == null) {
						tagset = DefaultTextAnnotation_Util.UNKNOWN_TAGSET;
					}
				}

				PhraseTypeProperty phraseTypeProperty = new PhraseTypeProperty(jcas);
				TagSetLabel tagSetLabel = new TagSetLabel(jcas);

				tagSetLabel.setLabel(phraseTypeLabel);
				tagSetLabel.setTagSet(tagset);
				phraseTypeProperty.setPhraseType(tagSetLabel);

				/* Add PartOfSpeechProperty to the TokenAnnotationProperty list */
				phraseAnnotationPropertyList.add(phraseTypeProperty);
			}

			// CCPComplexSlotMention phraseTypeCSM = (CCPComplexSlotMention) UIMA_Util
			// .getSlotMentionByName(ccpTA, ComplexSlotMentionTypes.PHRASE_TYPE);
			// if (phraseTypeCSM != null) {
			// /* if the phrase type slot is not null, then look for a phrase type class inside */
			// FSArray phraseTypes = phraseTypeCSM.getClassMentions();
			// if (phraseTypes != null) {
			// for (int i = 0; i < phraseTypes.size(); i++) {
			// Object possiblePhraseTypeMention = phraseTypes.get(i);
			// if (possiblePhraseTypeMention instanceof CCPClassMention) {
			// CCPClassMention phraseTypeMention = (CCPClassMention) possiblePhraseTypeMention;
			// /* check to make sure this is a phraseType mention */
			// if (phraseTypeMention.getMentionName().equals(ClassMentionTypes.PHRASE_TYPE_LABEL)) {
			//
			// /* get the label and tag set */
			// CCPNonComplexSlotMention labelSM = (CCPNonComplexSlotMention)
			// UIMA_Util.getSlotMentionByName(phraseTypeMention,
			// SlotMentionTypes.TAGSET_LABEL);
			// String typeLabel = UIMA_Util.getFirstSlotValue(labelSM);
			//
			// CCPNonComplexSlotMention tagsetSM = (CCPNonComplexSlotMention)
			// UIMA_Util.getSlotMentionByName(phraseTypeMention,
			// SlotMentionTypes.TAGSET);
			// String tagset = UIMA_Util.getFirstSlotValue(tagsetSM);
			//
			// PhraseTypeProperty phraseTypeProperty = new PhraseTypeProperty(jcas);
			// TagSetLabel tagSetLabel = new TagSetLabel(jcas);
			//
			// tagSetLabel.setLabel(typeLabel);
			// tagSetLabel.setTagSet(tagset);
			// phraseTypeProperty.setPhraseType(tagSetLabel);
			//								
			// /* Add PartOfSpeechProperty to the TokenAnnotationProperty list */
			// phraseAnnotationPropertyList.add(phraseTypeProperty);
			// } else {
			// error("Phrase type mention expected, but found: " + phraseTypeMention.getMentionName());
			// }
			// }else {
			// error("Expected phrase type class mention, but instead found: " +
			// possiblePhraseTypeMention.getClass().getName());
			// }
			// }
			// }
			// }

			/* Add any generated PhraseAnnotationProperties to the phrase annotation */
			// if (phraseAnnotationPropertyList.size() > 0) {
			for (PhraseAnnotationProperty pap : phraseAnnotationPropertyList) {
				UIMA_Annotation_Util.addMetaDataProperty(phraseAnnotation, pap, jcas);
			}
			// FSArray phraseAnnotationProperties = new FSArray(jcas, phraseAnnotationPropertyList.size());
			// for (int i = 0; i < phraseAnnotationPropertyList.size(); i++) {
			// phraseAnnotationProperties.set(i, phraseAnnotationPropertyList.get(i));
			// }
			// phraseAnnotation.setPhraseProperties(phraseAnnotationProperties);
			// }

			/* Add the new PhraseAnnotation to the CAS, and flag the old version for removal */
			syntacticAnnotationsToRemove.add(ccpTA);
			// phraseAnnotation.addToIndexes();
			annotationsToAdd.add(phraseAnnotation);
		}
	}

	/**
	 * Convert a CCPTextAnnotation of type "clause" to a ClauseAnnotation
	 * 
	 * @param ccpTA
	 * @param annotationsToAdd
	 * @param syntacticAnnotationsToRemove
	 * @param jcas
	 */
	private static void normalizeClauseAnnotation(CCPTextAnnotation ccpTA, List<CCPTextAnnotation> annotationsToAdd,
			List<CCPTextAnnotation> syntacticAnnotationsToRemove, JCas jcas) {
		/* if it's already a ClauseAnnotation, then we don't have to do anything */
		if (!(ccpTA instanceof CCPClauseAnnotation)) {
			CCPClauseAnnotation clauseAnnotation = new CCPClauseAnnotation(jcas);
			try {
				UIMA_Util.swapAnnotationInfo(ccpTA, clauseAnnotation);
			} catch (CASException ce) {
				ce.printStackTrace();
			}

			/* Initialize a list to hold TokenAnnotationProperty objects */
			List<ClauseAnnotationProperty> clauseAnnotationPropertyList = new ArrayList<ClauseAnnotationProperty>();

			/* set clause type */
			CCPStringSlotMention ccpSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
					SlotMentionTypes.CLAUSE_TYPE);
			if (ccpSlotMention != null) {
				String clauseTypeLabel = UIMA_Util.getFirstSlotValue(ccpSlotMention);

				CCPStringSlotMention tagSetSlot = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(ccpTA,
						SlotMentionTypes.TAGSET);
				String tagset = UIMA_Util.getFirstSlotValue(tagSetSlot);
				if (tagset == null) {
					tagset = DefaultTextAnnotation_Util.UNKNOWN_TAGSET;
				}
				ClauseTypeProperty clauseTypeProperty = new ClauseTypeProperty(jcas);
				TagSetLabel tagSetLabel = new TagSetLabel(jcas);

				tagSetLabel.setLabel(clauseTypeLabel);
				tagSetLabel.setTagSet(tagset);
				clauseTypeProperty.setClauseType(tagSetLabel);

				clauseAnnotationPropertyList.add(clauseTypeProperty);
			}
			// CCPComplexSlotMention clauseTypeCSM = (CCPComplexSlotMention) UIMA_Util
			// .getSlotMentionByName(ccpTA, ComplexSlotMentionTypes.CLAUSE_TYPE);
			// if (clauseTypeCSM != null) {
			// /* if the clause type slot is not null, then look for a clause type class inside */
			// FSArray clauseTypes = clauseTypeCSM.getClassMentions();
			// if (clauseTypes != null) {
			// for (int i = 0; i < clauseTypes.size(); i++) {
			// Object possibleClauseTypeMention = clauseTypes.get(i);
			// if (possibleClauseTypeMention instanceof CCPClassMention) {
			// CCPClassMention clauseTypeMention = (CCPClassMention) possibleClauseTypeMention;
			// /* check to make sure this is a clauseType mention */
			// if (clauseTypeMention.getMentionName().equals(ClassMentionTypes.CLAUSE_TYPE_LABEL)) {
			//
			// /* get the label and tag set */
			// CCPNonComplexSlotMention labelSM = (CCPNonComplexSlotMention)
			// UIMA_Util.getSlotMentionByName(clauseTypeMention,
			// SlotMentionTypes.TAGSET_LABEL);
			// String typeLabel = UIMA_Util.getFirstSlotValue(labelSM);
			//
			// CCPNonComplexSlotMention tagsetSM = (CCPNonComplexSlotMention)
			// UIMA_Util.getSlotMentionByName(clauseTypeMention,
			// SlotMentionTypes.TAGSET);
			// String tagset = UIMA_Util.getFirstSlotValue(tagsetSM);
			//
			// ClauseTypeProperty clauseTypeProperty = new ClauseTypeProperty(jcas);
			// TagSetLabel tagSetLabel = new TagSetLabel(jcas);
			//
			// tagSetLabel.setLabel(typeLabel);
			// tagSetLabel.setTagSet(tagset);
			// clauseTypeProperty.setClauseType(tagSetLabel);
			//								
			// /* Add PartOfSpeechProperty to the TokenAnnotationProperty list */
			// clauseAnnotationPropertyList.add(clauseTypeProperty);
			// } else {
			// error("Clause type mention expected, but found: " + clauseTypeMention.getMentionName());
			// }
			// } else {
			// error("Expected clause type class mention, but instead found: " +
			// possibleClauseTypeMention.getClass().getName());
			// }
			// }
			// }
			// }

			/* Add any generated ClauseAnnotationProperties to the clause annotation */
			for (ClauseAnnotationProperty cap : clauseAnnotationPropertyList) {
				addClauseAnnotationProperty(clauseAnnotation, cap, jcas);
				// UIMA_Annotation_Util.addMetaDataProperty(clauseAnnotation, cap, jcas);
			}

			// if (clauseAnnotationPropertyList.size() > 0) {
			// FSArray clauseAnnotationProperties = new FSArray(jcas, clauseAnnotationPropertyList.size());
			// for (int i = 0; i < clauseAnnotationPropertyList.size(); i++) {
			// clauseAnnotationProperties.set(i, clauseAnnotationPropertyList.get(i));
			// }
			// clauseAnnotation.setClauseProperties(clauseAnnotationProperties);
			// }

			/* Add the new ClauseAnnotation to the CAS, and flag the old version for removal */
			syntacticAnnotationsToRemove.add(ccpTA);
			// clauseAnnotation.addToIndexes();
			annotationsToAdd.add(clauseAnnotation);
		}
	}

	/**
	 * Convert a CCPTextAnnotation of type "sentence" to a SentenceAnnotation
	 * 
	 * @param ccpTA
	 * @param annotationsToAdd
	 * @param syntacticAnnotationsToRemove
	 * @param jcas
	 */
	private static void normalizeSentenceAnnotation(CCPTextAnnotation ccpTA, List<CCPTextAnnotation> annotationsToAdd,
			List<CCPTextAnnotation> syntacticAnnotationsToRemove, JCas jcas) {
		/* if it's already a SentenceAnnotation, then we don't have to do anything */
		if (!(ccpTA instanceof CCPSentenceAnnotation)) {
			CCPSentenceAnnotation sentenceAnnotation = new CCPSentenceAnnotation(jcas);
			try {
				UIMA_Util.swapAnnotationInfo(ccpTA, sentenceAnnotation);
			} catch (CASException ce) {
				ce.printStackTrace();
			}

			/* Add the new SentenceAnnotation to the CAS, and flag the old version for removal */
			syntacticAnnotationsToRemove.add(ccpTA);
			// sentenceAnnotation.addToIndexes();
			annotationsToAdd.add(sentenceAnnotation);
		}

	}
	

	
	private static void addAnnotatorAndSet(CCPTextAnnotation ccpTA, JCas jcas) {
		/* set annotator and annotation set */
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAffiliation("N/A");
		ccpAnnotator.setFirstName("Default Annotator");
		ccpAnnotator.setAnnotatorID(-1);
		ccpAnnotator.setLastName("Default Annotator");
	
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(-1);
		ccpAnnotationSet.setAnnotationSetName("Default Set");
		ccpAnnotationSet.setAnnotationSetDescription("");
	
		ccpTA.setAnnotator(ccpAnnotator);
		FSArray asets = new FSArray(jcas, 1);
		asets.set(0, ccpAnnotationSet);
		ccpTA.setAnnotationSets(asets);
	
		ccpTA.setDocumentSectionID(-1);
	}
	public static void setSpan(CCPTextAnnotation ccpTA, int startIndex, int endIndex, JCas jcas) {
		ccpTA.setBegin(startIndex);
		ccpTA.setEnd(endIndex);
	
		/* create span */
		FSArray ccpSpans = new FSArray(jcas, 1);
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(startIndex);
		ccpSpan.setSpanEnd(endIndex);
		ccpSpans.set(0, ccpSpan);
		ccpTA.setSpans(ccpSpans);
	}
	
	public static CCPSentenceAnnotation createCCPSentenceAnnotation(int startIndex, int endIndex, JCas jcas) {
		CCPSentenceAnnotation ccpTA = new CCPSentenceAnnotation(jcas);
		setSpan(ccpTA, startIndex, endIndex, jcas);
		addAnnotatorAndSet(ccpTA, jcas);
		ccpTA.addToIndexes();

		return ccpTA;
	}
	public static CCPPhraseAnnotation createCCPPhraseAnnotation(int startIndex, int endIndex, JCas jcas) {
		CCPPhraseAnnotation ccpTA = new CCPPhraseAnnotation(jcas);
		setSpan(ccpTA, startIndex, endIndex, jcas);
		addAnnotatorAndSet(ccpTA, jcas);
		ccpTA.addToIndexes();

		return ccpTA;
	}
	public static CCPClauseAnnotation createCCPClauseAnnotation(int startIndex, int endIndex, JCas jcas) {
		CCPClauseAnnotation ccpTA = new CCPClauseAnnotation(jcas);
		setSpan(ccpTA, startIndex, endIndex, jcas);
		addAnnotatorAndSet(ccpTA, jcas);
		ccpTA.addToIndexes();

		return ccpTA;
	}
	public static CCPTokenAnnotation createCCPTokenAnnotation(int startIndex, int endIndex, JCas jcas) {
		CCPTokenAnnotation ccpTA = new CCPTokenAnnotation(jcas);
		setSpan(ccpTA, startIndex, endIndex, jcas);
		addAnnotatorAndSet(ccpTA, jcas);
		ccpTA.addToIndexes();

		return ccpTA;
	}

	
}
