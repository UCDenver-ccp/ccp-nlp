/*
 * AnnotationSetOverrider_AE.java
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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * A simple utility Analysis Engine that enables the user to change the annotation set assigned to
 * <code>CCPTextAnnotations</code> stored in the CAS. For each <code>CCPTextAnnotation</code> in the
 * CAS, the user-specified annotation set is assigned, unless the annotation is associated with an
 * annotation set in the annotation-sets-to-ignore list, in which case nothing is changed. This
 * utility is particularly useful when using the Annotation Comparison machinery.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class AnnotationSetOverrider_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_SETID = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotationSetOverrider_AE.class, "setID");
	@ConfigurationParameter(mandatory = true)
	private int setID = -1;

	public static final String PARAM_SETNAME = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotationSetOverrider_AE.class, "setName");
	@ConfigurationParameter()
	private String setName = null;

	public static final String PARAM_SETDESCRIPTION = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotationSetOverrider_AE.class, "setDescription");
	@ConfigurationParameter()
	private String setDescription = null;

	public static final String PARAM_ANNOTATION_SETS_TO_IGNORE = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationSetOverrider_AE.class, "annotationSetsToIgnore");
	@ConfigurationParameter()
	private Set<Integer> annotationSetsToIgnore;

	/**
	 * cycle through all annotations and set the annotation set
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* create an annotation set list */
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(setID);
		ccpAnnotationSet.setAnnotationSetName(setName);
		ccpAnnotationSet.setAnnotationSetDescription(setDescription);
		FSArray annotationSets = new FSArray(jcas, 1);
		annotationSets.set(0, ccpAnnotationSet);

		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();

		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);

				if (!ignore) {
					ccpTA.setAnnotationSets(annotationSets);
				}
			} else {
				System.err.println("WARNING -- AnnotationSetValidator_AE: CCPTextAnnotation expected but instead got "
						+ possibleAnnot.getClass().getName());
			}
		}

	}

	protected boolean checkForIgnoreBasedOnAnnotationSet(CCPTextAnnotation ccpTA) {
		boolean ignore = false;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		if (annotationSets != null & annotationSetsToIgnore != null) {
			for (int i = 0; i < annotationSets.size(); i++) {
				CCPAnnotationSet aSet = (CCPAnnotationSet) annotationSets.get(i);
				if (annotationSetsToIgnore.contains(aSet.getAnnotationSetID())) {
					ignore = true;
					break;
				}
			}
		}
		return ignore;
	}

	
	
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, int setId,
			String setName, String setDescription, int[] setsToIgnore) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotationSetOverrider_AE.class, tsd, PARAM_SETID, setId, PARAM_SETNAME, setName, PARAM_SETDESCRIPTION, setDescription, PARAM_ANNOTATION_SETS_TO_IGNORE, setsToIgnore);
	}
	
}
