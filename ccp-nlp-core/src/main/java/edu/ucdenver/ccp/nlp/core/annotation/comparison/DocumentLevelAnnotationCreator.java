package edu.ucdenver.ccp.nlp.core.annotation.comparison;

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
 * This class creates document-level annotations for a given type. The user can optionally require a
 * slot to be present in order for creation to occur. This is useful for such tasks as creating
 * document-level protein annotations where you want to require the presence of the entrez_id_slot
 * so that gene name normalization results can be computed. By requiring the presence of the
 * entrez_id_slot, only proteins that have such a slot will have a document-level counterpart
 * created. If no such slot presence is requested, then document-level annotations are generated for
 * all annotations of the specified type.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DocumentLevelAnnotationCreator {
	private static Logger logger = Logger.getLogger(DocumentLevelAnnotationCreator.class);

	private String mentionType;

	private Set<String> requiredSlots;

	private Set<String> documentLevelAnnotationKeys;

	/**
	 * This DocumentLevelAnnotationCreator will generate document-level annotations for all
	 * annotations of type mentionType.
	 * 
	 * @param mentionType
	 */
	public DocumentLevelAnnotationCreator(String mentionType) {
		this(mentionType, null);
	}

	/**
	 * This DocumentLevelAnnotationCreator will generate document-level annotations for only those
	 * annotations that have non-empty slots specified by the requiredSlots Set.
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
		 * This set will hold a key representing the document-level annotations that have already
		 * been created, thus ensuring that each document-level annotation is unique.
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

	public List<TextAnnotation> createDocumentLevelAnnotations(List<TextAnnotation> inputAnnotations,
			Set<Integer> annotatorsToIgnore) {
		List<TextAnnotation> annotationsToKeep = new ArrayList<TextAnnotation>();

		for (TextAnnotation ta : inputAnnotations) {
			if (!annotatorsToIgnore.contains(ta.getAnnotationID())) {
				annotationsToKeep.add(ta);
			}
		}

		return createDocumentLevelAnnotations(annotationsToKeep);

	}

	/**
	 * Convert the input TextAnnotation into a document-level annotation, check to see if this
	 * annotation already exists, if it does not, add it to the list of documentLevelAnnotations
	 * 
	 * @param ta
	 * @param documentLevelAnnotations
	 */
	private void createDocumentLevelAnnotation(TextAnnotation ta, List<TextAnnotation> documentLevelAnnotations) {
		AnnotationSet annotationSet = new AnnotationSet(9999,
				"Document-level " + ta.getClassMention().getMentionName(), "Document-level annotation set.");
		/*
		 * Create a new class mention for this annotation- "DocumentLevel-" is appended to the class
		 * mention name
		 */
		DefaultClassMention documentLevelCM = new DefaultClassMention("DocumentLevel-"
				+ ta.getClassMention().getMentionName());
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
		TextAnnotation documentLevelAnnot = new DefaultTextAnnotation(0, 1, "", ta.getAnnotator(), null, "",
				ta.getDocumentCollectionID(), ta.getDocumentID(), ta.getDocumentSectionID(), documentLevelCM);
		documentLevelAnnot.setAnnotationSets(ta.getAnnotationSets());
		documentLevelAnnot.addAnnotationSet(annotationSet);

		/*
		 * now we need to log the creation of this document-level annotation, and add it to the
		 * documentLevelAnnotations list if it is new
		 */
		String key = getDocumentLevelString(documentLevelAnnot);
		logger.debug("Key = " + key);

		if (!documentLevelAnnotationKeys.contains(key)) {
			documentLevelAnnotations.add(documentLevelAnnot);
			documentLevelAnnotationKeys.add(key);
		} else {
			// do nothing, we don't need to add this document-level annotation since it already
			// exisits
		}

	}

	private String getDocumentLevelString(TextAnnotation ta) {
		return ta.getDocumentID() + ta.getClassMention().getDocumentLevelSingleLineRepresentation();
	}

}
