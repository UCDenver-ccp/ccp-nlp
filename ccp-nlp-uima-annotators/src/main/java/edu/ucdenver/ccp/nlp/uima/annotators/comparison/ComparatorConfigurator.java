/*
 * ComparatorConfigurator.java
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

package edu.ucdenver.ccp.nlp.uima.annotators.comparison;

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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * This is a utility class for configuring an AnnotationComparator. Its main purpose is to read the config file for the
 * AnnotationComparator.
 * 
 * The format for the config file is as follows:
 * 
 * <pre>
 *     &lt;!-- Settings for a single AnnotationComparator run --&gt;
 *     &lt;AnnotationComparatorSettings&gt;
 *    
 *     &lt;!-- AnnotationGroups define the annotations that will be used in the comparison. Each group          --&gt;
 *     &lt;!-- is identified by a unique GroupID, and has annotation members defined by their                   --&gt;
 *     &lt;!-- AnnotationSetID, AnnotatorID, and AnnotationType (which is equivalent to the ClassMention type). --&gt;
 *     &lt;!-- Multiple AnnotationGroups are permitted. --&gt;
 *     &lt;AnnotationGroup&gt;
 *        &lt;GroupID&gt;[Group ID]&lt;/GroupID&gt;
 *        &lt;AnnotationSetID&gt;[Annotation Set ID]&lt;/AnnotationSetID&gt;
 *        &lt;AnnotatorID&gt;[Annotator ID]&lt;/AnnotatorID&gt;
 *        &lt;!-- Multiple AnnotationTypes are permitted --&gt;
 *        &lt;AnnotationType&gt;[Type String]&lt;/AnnotationType&gt;
 *     &lt;/AnnotationGroup&gt;
 *     
 *     &lt;!?ComparisonGroups define how the annotations are compared. Currently, only pairwise comparisons are --&gt;
 *     &lt;!-- enabled. The ComparisonGroup with GoldStandard set to true is used as the gold standard, and all --&gt;
 *     &lt;!-- other groups are compared against it.                                                            --&gt;
 *     &lt;ComparisonGroup&gt;
 *        &lt;!-- If the GoldStandard tag is not present, its value is presumed to be false. --&gt;
 *        &lt;GoldStandard&gt;[true or false]&lt;/GoldStandard&gt;
 *        &lt;ComparisonGroupDescription&gt;[Description String]&lt;/ComparisonGroupDescription&gt;
 *        &lt;!-- Multiple AnnotationGrouPIDs are permitted --&gt;
 *        &lt;AnnotationGroupID&gt;[Annotation Group ID]&lt;/AnnotationGroupID&gt;
 *     &lt;/ComparisonGroup&gt;
 *     
 *     &lt;/AnnotationComparatorSettings&gt;
 * </pre>
 * 
 * 
 * @author Bill Baumgartner
 * 
 */

public class ComparatorConfigurator {
	private static Logger logger = Logger.getLogger(ComparatorConfigurator.class);

	private Map<Integer, AnnotationGroup> annotationGroupID2GroupMap;

	private Map<Integer, ComparisonGroup> comparisonGroupID2GroupMap;

	private Map<Integer, Set<Integer>> annotationGroupID2ComparisonGroupIDMap;

	private final String ANNOTATION_GROUP_TAG = "AnnotationGroup";

	private final String ANNOTATION_GROUP_ID_TAG = "GroupID";

	private final String ANNOTATION_GROUP_ANNOTATORID_TAG = "AnnotatorID";

	private final String ANNOTATION_GROUP_ANNOTATIONSETID_TAG = "AnnotationSetID";

	private final String ANNOTATION_GROUP_ANNOTATIONTYPE_TAG = "AnnotationType";

	private final String ANNOTATION_GROUP_ANNOTATIONREGEX_TAG = "AnnotationTypeRegex";

	private final String COMPARISON_GROUP_TAG = "ComparisonGroup";

	private final String COMPARISON_GROUP_ISGOLDSTANDARD_TAG = "GoldStandard";

	private final String COMPARISON_GROUP_DESCRIPTION_TAG = "ComparisonGroupDescription";

	private final String COMPARISON_GROUP_ANNOTATIONGROUPMEMBER_TAG = "AnnotationGroupID";

	private boolean hasGoldGroup = false;

	private int comparisonGroupID = 0;

	public ComparatorConfigurator(File configFile) {
		/* initialize containers for AnnotationGroups and ComparisonGroups */
		annotationGroupID2GroupMap = new HashMap<Integer, AnnotationGroup>();
		comparisonGroupID2GroupMap = new HashMap<Integer, ComparisonGroup>();
		annotationGroupID2ComparisonGroupIDMap = new HashMap<Integer, Set<Integer>>();

		/* parse the configuration file, filling the group containers as you go */
		try {
			parseConfigFile(configFile);
		} catch (IOException e) {
			logger.error("Error while parsing comparator configuration file...\n", e);
		} catch (JDOMException e) {
			logger.error("Error while parsing comparator configuration file...\n", e);
		}

	}

	private void parseConfigFile(File configFile) throws IOException, JDOMException {
		/* check for valid xml */
		if (XMLValidityChecker.validateXML(configFile.getAbsolutePath())) {

			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(configFile);
			Element rootElement = document.getRootElement();

			hasGoldGroup = false;

			Iterator childrenIterator = rootElement.getChildren().iterator();

			while (childrenIterator.hasNext()) {
				Element childElement = (Element) childrenIterator.next();
				if (childElement.getName().equals(ANNOTATION_GROUP_TAG)) {
					processAnnotationGroupElement(childElement);
				} else if (childElement.getName().equals(COMPARISON_GROUP_TAG)) {
					processComparisonGroupElement(childElement);
				} else {
					logger
							.warn("Unexpected element encountered while parsing the comparison configuration file: "
									+ childElement.getName());
				}
			}

		} else {
			logger.error("Comparison configuration file is not well-formed XML. Please fix and try again.");
		}
	}

	/**
	 * Get the attributes of an AnnotationGroup and add a new AnnotationGroup object to the annotationGroupMap
	 */
	private void processAnnotationGroupElement(Element annotationGroupElement) {
		/* Initialize a new AnnotationGroup */
		AnnotationGroup aGroup = new AnnotationGroup();

		try {
			Iterator attributeIter = annotationGroupElement.getChildren().iterator();
			while (attributeIter.hasNext()) {
				Object possibleElement = attributeIter.next();
				if (possibleElement instanceof Element) {
					Element attribute = (Element) possibleElement;
					if (attribute.getName().equals(ANNOTATION_GROUP_ID_TAG)) {
						aGroup.setGroupID(new Integer(attribute.getValue()));
					} else if (attribute.getName().equals(ANNOTATION_GROUP_ANNOTATIONSETID_TAG)) {
						aGroup.setAnnotationSetID(Integer.parseInt(attribute.getValue()));
					} else if (attribute.getName().equals(ANNOTATION_GROUP_ANNOTATORID_TAG)) {
						aGroup.setAnnotatorID(attribute.getValue());
					} else if (attribute.getName().equals(ANNOTATION_GROUP_ANNOTATIONTYPE_TAG)) {
						aGroup.addAnnotationType(attribute.getValue());
					} else if (attribute.getName().equals(ANNOTATION_GROUP_ANNOTATIONREGEX_TAG)) {
						aGroup.addAnnotationTypeRegex(attribute.getValue());
					} else {
						logger.warn("Unexpected Element found in AnnotationGroup: " + attribute.getName()
								+ " This element will be ignored.");
					}
				} else {
					logger
							.error("Error while parsing comparison configuration file. Expected Element in processAnnotationGroupElement but got "
									+ possibleElement.getClass().getName());
				}
			}

			/* If all fields of an annotation group have been filled, then store it in the annotationGroupMap */
			if (aGroup.isValid()) {
				if (annotationGroupID2GroupMap.containsKey(aGroup.getGroupID())) {
					logger.warn("A duplicate AnnotationGroup ID has been detected (" + aGroup.getGroupID()
							+ "). The first AnnotationGroup with this ID will be included in the comparison, "
							+ "others will be ignored. Please fix your comparison configuration file accordingly.");
				} else {
					this.annotationGroupID2GroupMap.put(aGroup.getGroupID(), aGroup);
				}
			} else {
				logger.warn("Invalid AnnotationGroup detected: " + aGroup.toString());
			}
		} catch (NumberFormatException nfe) {
			logger.error("Caught a NumberFormatException. Please check to make sure AnnotatorID and "
					+ "AnnotationSetID fields are integers in all AnnotationGroups.\n", nfe);
		}
	}

	/**
	 * Get the attributes of a ComparisonGroup and add a new ComparisonGroup object to the commparisonGroupList
	 */
	private void processComparisonGroupElement(Element comparisonGroupElement) {
		/* Initialize a new ComparisonGroup */
		ComparisonGroup cGroup = new ComparisonGroup();
		cGroup.setID(comparisonGroupID++);

		Iterator attributeIter = comparisonGroupElement.getChildren().iterator();
		while (attributeIter.hasNext()) {
			Object possibleElement = attributeIter.next();
			if (possibleElement instanceof Element) {
				Element attribute = (Element) possibleElement;
				if (attribute.getName().equals(COMPARISON_GROUP_ISGOLDSTANDARD_TAG)) {

					boolean isGoldGroup = Boolean.valueOf(attribute.getValue());
					cGroup.setGoldStandard(isGoldGroup);

					if (isGoldGroup & hasGoldGroup) {
						logger
								.warn("Multiple Gold Standard Comparison groups found. Only one may be used. Please adjust your comparator configuration file accordingly.");
					} else if (isGoldGroup) {
						hasGoldGroup = true;
					}

				} else if (attribute.getName().equals(COMPARISON_GROUP_DESCRIPTION_TAG)) {
					cGroup.setDescription(attribute.getValue());
				} else if (attribute.getName().equals(COMPARISON_GROUP_ANNOTATIONGROUPMEMBER_TAG)) {
					Integer comparisonGroupID = cGroup.getID();
					Integer annotationGroupID = new Integer(attribute.getValue());
					/* add the annotationGroupID to the comparison group */
					cGroup.addAnnotationGroup(annotationGroupID);

					/* create a mapping from annotationgroupID to comparisonGroupID */
					if (annotationGroupID2ComparisonGroupIDMap.containsKey(annotationGroupID)) {
						annotationGroupID2ComparisonGroupIDMap.get(annotationGroupID).add(comparisonGroupID);
					} else {
						Set<Integer> comparisonGroupIDs = new HashSet<Integer>();
						comparisonGroupIDs.add(comparisonGroupID);
						annotationGroupID2ComparisonGroupIDMap.put(annotationGroupID, comparisonGroupIDs);
					}

				} else {
					logger.warn("Unexpected Element found in ComparisonGroup: " + attribute.getName() + " This element will be ignored.");
				}
			} else {
				logger.warn("Error while parsing comparison configuration file. Expected Element in processComparisonGroupElement but got "
						+ possibleElement.getClass().getName());
			}
		}

		/* If all fields of an annotation group have been filled, then store it in the annotationGroupMap */
		if (cGroup.isValid()) {
			this.comparisonGroupID2GroupMap.put(cGroup.getID(), cGroup);
		} else {
			logger.warn("Invalid ComparisonGroup detected: " + cGroup.toString());
		}

	}

	/**
	 * Returns a mapping from AnnotationGroup ID (an Integer) to AnnotationGroup object
	 * 
	 * @return
	 */
	public Map<Integer, AnnotationGroup> getAnnotationGroupMap() {
		return annotationGroupID2GroupMap;
	}

	/**
	 * Returns a list of ComparisonGroup objects
	 * 
	 * @return
	 */
	public Map<Integer, ComparisonGroup> getComparisonGroupList() {
		return comparisonGroupID2GroupMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Integer, Set<Integer>> getAnnotationGroupID2ComparisonGroupIDMap() {
		return annotationGroupID2ComparisonGroupIDMap;
	}
}
