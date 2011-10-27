/*
 * AnnotatorOverrider_AE.java 
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * A simple utility Analysis Engine that enables the user to change the annotator assigned to all
 * <code>CCPTextAnnotations</code> stored in the CAS. For each <code>CCPTextAnnotation</code> in the
 * CAS, the user-specified annotator is assigned.
 * 
 * @author William A Baumgartner Jr
 * 
 */
public class AnnotatorOverrider_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ANNOTATOR_ID = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotatorOverrider_AE.class, "annotatorID");
	@ConfigurationParameter(mandatory = true)
	private int annotatorID = -1;

	public static final String PARAM_FIRST_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotatorOverrider_AE.class, "firstName");
	@ConfigurationParameter()
	private String firstName = null;

	public static final String PARAM_LAST_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotatorOverrider_AE.class, "lastName");
	@ConfigurationParameter()
	private String lastName = null;

	public static final String PARAM_AFFILIATION = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotatorOverrider_AE.class, "affiliation");
	@ConfigurationParameter()
	private String affiliation = null;

	public static final String PARAM_ANNOTATOR_IDS_TO_IGNORE = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotatorOverrider_AE.class, "annotatorIDsToIgnore");
	@ConfigurationParameter()
	private Set<Integer> annotatorIDsToIgnore;

	/**
	 * cycle through all annotations and set the annotation set
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* create an annotation set list */
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		ccpAnnotator.setAnnotatorID(annotatorID);
		ccpAnnotator.setAffiliation(affiliation);
		ccpAnnotator.setFirstName(firstName);
		ccpAnnotator.setLastName(lastName);

		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				CCPAnnotator currentAnnotator = ccpTA.getAnnotator();

				if (currentAnnotator == null) {
					ccpTA.setAnnotator(ccpAnnotator);
				} else {
					try {
						if (!(annotatorIDsToIgnore.contains(currentAnnotator.getAnnotatorID()))) {
							ccpTA.setAnnotator(ccpAnnotator);
						}
					} catch (NullPointerException npe) {
						ccpTA.setAnnotator(ccpAnnotator);
					}
				}
			} else {
				System.err.println("WARNING -- AnnotatorOverrider_AE: CCPTextAnnotation expected but instead got "
						+ possibleAnnot.getClass().getName());
			}
		}

	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			int annotatorId, String firstName, String lastName, String affiliation, int[] ignoreIds)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotatorOverrider_AE.class, tsd, PARAM_ANNOTATOR_ID,
				annotatorId, PARAM_FIRST_NAME, firstName, PARAM_LAST_NAME, lastName, PARAM_AFFILIATION, affiliation,
				PARAM_ANNOTATOR_IDS_TO_IGNORE, ignoreIds);
	}

}
