/*
 * ClassMentionTypeFilter_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;

/**
 * This utility analysis engine enables the user to remove all annotations that are not a specific
 * class mention.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class ClassMentionTypeRegexFilter_AE extends JCasAnnotator_ImplBase {

	public final static String PARAM_CLASS_MENTION_TYPES_TO_KEEP_REGEXES = ConfigurationParameterFactory
			.createConfigurationParameterName(ClassMentionTypeRegexFilter_AE.class, "mentionTypesToKeepRegexes");
	@ConfigurationParameter(mandatory = true, description = "Describes the location where generated XMI files will be placed.")
	private String[] mentionTypesToKeepRegexes;

	/**
	 * cycle through all annotations and keep only those that have an annotation type in the
	 * class-mention-types-to-keep-list
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();
		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			CCPClassMention ccpCM = ccpTA.getClassMention();
			String classMentionType = ccpCM.getMentionName();
			boolean keep = false;
			for (String regex : mentionTypesToKeepRegexes)
				if (classMentionType.matches(regex)) {
					keep = true;
					break;
				}
			if (!keep) {
				annotationsToRemove.add(ccpTA);
			}
		}

		/*
		 * now remove annotations that had class mention types not in the classMentionTypesToKeep
		 * list
		 */
		for (CCPTextAnnotation ccpTA : annotationsToRemove) {
			ccpTA.removeFromIndexes();
			ccpTA = null;
		}

	}

}
