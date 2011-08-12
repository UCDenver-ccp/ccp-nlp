/*
 * DocumentLevelAnnotationCreator.java
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

package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.KnowledgeRepresentationWrapperException;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * This class creates document-level annotations for a given type. The user can optionally require a slot to be present
 * in order for creation to occur. This is useful for such tasks as creating document-level protein annotations where
 * you want to require the presence of the entrez_id_slot so that gene name normalization results can be computed. By
 * requiring the presence of the entrez_id_slot, only proteins that have such a slot will have a document-level
 * counterpart created. If no such slot presence is requested, then document-level annotations are generated for all
 * annotations of the specified type.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class DocumentLevelAnnotationCreator {
private static Logger logger = Logger.getLogger(DocumentLevelAnnotationCreator.class);


	private String mentionType;

	private Set<String> requiredSlots;

	private Set<String> documentLevelAnnotationKeys;

	/**
	 * This DocumentLevelAnnotationCreator will generate document-level annotations for all annotations of type
	 * mentionType.
	 * 
	 * @param mentionType
	 */
	public DocumentLevelAnnotationCreator(String mentionType) {
		this(mentionType, null);
	}

	/**
	 * This DocumentLevelAnnotationCreator will generate document-level annotations for only those annotations that have
	 * non-empty slots specified by the requiredSlots Set.
	 * 
	 * @param mentionType
	 * @param requiredSlots
	 */
	public DocumentLevelAnnotationCreator(String mentionType, Set<String> requiredSlots) {
		this.mentionType = mentionType;
		this.requiredSlots = requiredSlots;
	}

	/**
	 * Given a list of TextAnnotations, return a list of corresponding document-level annotations
	 * 
	 * @param inputAnnotations
	 * @return
	 */
	public List<TextAnnotation> createDocumentLevelAnnotations(List<TextAnnotation> inputAnnotations) {
		List<TextAnnotation> documentLevelAnnotations = new ArrayList<TextAnnotation>();

		logger.debug("Converting annotation list into document level annotations...");
		/*
		 * This set will hold a key representing the document-level annotations that have already been created, thus
		 * ensuring that each document-level annotation is unique.
		 */
		documentLevelAnnotationKeys = new HashSet<String>();

		for (TextAnnotation ta : inputAnnotations) {
			/* check to see if this annotation is of the type of interest */
			if (ta.getClassMention().getMentionName().toLowerCase().equals(mentionType.toLowerCase())) {
				logger.debug("Found a " + mentionType + " annotation");
				if (requiredSlots != null) {
					/* check to see if this annotation has non-empty required slots */
					boolean hasRequiredSlots = true;
					for (String requiredSlot : requiredSlots) {
						PrimitiveSlotMention sm = ta.getClassMention().getPrimitiveSlotMentionByName(requiredSlot);
						int numSlotMentions = 0;
						if (sm != null) {
							numSlotMentions = 1;
						}
						ComplexSlotMention csm = ta.getClassMention().getComplexSlotMentionByName(requiredSlot);
						int numComplexSlotMentions = 0;
						if (csm != null) {
							numComplexSlotMentions = 1;
						}

						// List<SlotMention> slotMentions = ta.getClassMention().getSlotMentionsByName(requiredSlot);
						// int numSlotMentions = 0;
						// if (slotMentions != null) {
						// numSlotMentions = slotMentions.size();
						// }
						// List<ComplexSlotMention> complexSlotMentions =
						// ta.getClassMention().getComplexSlotMentionsByName(requiredSlot);
						// int numComplexSlotMentions = 0;
						// if (complexSlotMentions != null) {
						// numComplexSlotMentions = complexSlotMentions.size();
						// }
						if ((numSlotMentions + numComplexSlotMentions) < 1) {
							hasRequiredSlots = false;
							logger.debug("Annotation does not have required slots");
							break;
						}
					}
					if (hasRequiredSlots) {
						logger.debug("Annotation has required slots, so creating a document-level annotation");
						createDocumentLevelAnnotation(ta, documentLevelAnnotations);
					}
				} else {
					/* no slots are required, so create the document-level annotation */
					logger.debug("No slots required, so creating a document-level annotation");
					createDocumentLevelAnnotation(ta, documentLevelAnnotations);
				}
			} else {
				// do nothing since this is not the mention type of interest
			}
		}

		return documentLevelAnnotations;
	}

	public List<TextAnnotation> createDocumentLevelAnnotations(List<TextAnnotation> inputAnnotations, Set<Integer> annotatorsToIgnore) {
		List<TextAnnotation> annotationsToKeep = new ArrayList<TextAnnotation>();

		for (TextAnnotation ta : inputAnnotations) {
			if (!annotatorsToIgnore.contains(ta.getAnnotationID())) {
				annotationsToKeep.add(ta);
			}
		}

		return createDocumentLevelAnnotations(annotationsToKeep);

	}

	/**
	 * Convert the input TextAnnotation into a document-level annotation, check to see if this annotation already
	 * exists, if it does not, add it to the list of documentLevelAnnotations
	 * 
	 * @param ta
	 * @param documentLevelAnnotations
	 */
	private void createDocumentLevelAnnotation(TextAnnotation ta, List<TextAnnotation> documentLevelAnnotations) {
		AnnotationSet annotationSet = new AnnotationSet(9999, "Document-level " + ta.getClassMention().getMentionName(),
				"Document-level annotation set.");
		/* Create a new class mention for this annotation- "DocumentLevel-" is appended to the class mention name */
		DefaultClassMention documentLevelCM = new DefaultClassMention("DocumentLevel-" + ta.getClassMention().getMentionName());
		try {
			for (PrimitiveSlotMention sm : ta.getClassMention().getPrimitiveSlotMentions()) {
				documentLevelCM.addPrimitiveSlotMention(sm);
			}
		} catch (KnowledgeRepresentationWrapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ComplexSlotMention csm : ta.getClassMention().getComplexSlotMentions()) {
			documentLevelCM.addComplexSlotMention(csm);
		}
		/* create the document-level annotation */
		TextAnnotation documentLevelAnnot = new DefaultTextAnnotation(0, 1, "", ta.getAnnotator(), null, -1, ta.getDocumentCollectionID(), ta
				.getDocumentID(), ta.getDocumentSectionID(), documentLevelCM);
		documentLevelAnnot.setAnnotationSets(ta.getAnnotationSets());
		documentLevelAnnot.addAnnotationSet(annotationSet);

		/*
		 * now we need to log the creation of this document-level annotation, and add it to the documentLevelAnnotations
		 * list if it is new
		 */
		String key = getDocumentLevelString(documentLevelAnnot);
		logger.debug("Key = " + key);

		if (!documentLevelAnnotationKeys.contains(key)) {
			documentLevelAnnotations.add(documentLevelAnnot);
			documentLevelAnnotationKeys.add(key);
		} else {
			// do nothing, we don't need to add this document-level annotation since it already exisits
		}

	}

	private String getDocumentLevelString(TextAnnotation ta) {
		return ta.getDocumentID() + ta.getClassMention().getDocumentLevelSingleLineRepresentation();
	}
	

}
