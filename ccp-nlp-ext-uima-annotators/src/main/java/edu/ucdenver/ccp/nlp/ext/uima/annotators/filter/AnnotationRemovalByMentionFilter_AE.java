/*
 * ClassMentionRemovalFilter_AE.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.build.XmlDescriptorWriter;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;

/**
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
/**
 * This utility annotator enables the user to remove all annotations of a specific class mention
 * type(s) from the CAS indexes.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class AnnotationRemovalByMentionFilter_AE extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(AnnotationRemovalByMentionFilter_AE.class);

	public static final String PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationRemovalByMentionFilter_AE.class,
					"classMentionNamesToRemoveList");

	private static final String ANNOTATOR_DESCRIPTION = String
			.format("This utility annotator allows the CAS to be filtered based on class mention names. "
					+ "Those annotations with class mention names specified by the \"%s\" parameter will be removed from the CAS.",
					PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST);

	private static final String ANNOTATOR_VENDOR = "UC Denver - CCP";
	
	@ConfigurationParameter
	private List<String> classMentionNamesToRemoveList;

	@Override
	public void initialize(UimaContext ac) throws ResourceInitializationException {
		super.initialize(ac);
		logger.info("Initialized AnnotationRemovalByMentionFilter_AE; types to remove: "
				+ classMentionNamesToRemoveList.toString());
	}

	/**
	 * cycle through all annotations and remove those that have annotation types in the
	 * class-mention-types-to-remove list
	 */
	@Override
	public void process(JCas jcas) {
		List<CCPTextAnnotation> annotationsToRemove = new ArrayList<CCPTextAnnotation>();
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			CCPClassMention ccpCM = ccpTA.getClassMention();
			String classMentionType = ccpCM.getMentionName().toLowerCase();
			if (classMentionNamesToRemoveList.contains(classMentionType)) {
				annotationsToRemove.add(ccpTA);
			}
		}

		/* now remove annotations that had class mention types in the classMentionTypesToRemove list */
		int count = 0;
		for (CCPTextAnnotation ccpTA : annotationsToRemove) {
			ccpTA.removeFromIndexes();
			count++;
		}
		logger.info("AnnotationRemovalByMentionFilter_AE Removed " + count + " annotations.");

	}

	/**
	 * Initializes an {@link AnalysisEngine} that will filter annotations based on class mention
	 * names
	 * 
	 * @param tsd
	 * @param removeMentions
	 * @return an initialized {@link AnalysisEngine}
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, Set<String> removeMentions)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(AnnotationRemovalByMentionFilter_AE.class, tsd,
				PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST, removeMentions.toArray(new String[0]));
	}

	public static void exportXmlDescriptor(File baseDescriptorDirectory, String version) {
		try {
			Class<AnnotationRemovalByMentionFilter_AE> cls = AnnotationRemovalByMentionFilter_AE.class;
			AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(cls,
					TypeSystemUtil.getCcpTypeSystem(), PARAM_CLASS_MENTION_TYPES_TO_REMOVE_LIST, new String[] {
							"Mention-Name-To-Remove-1", "Mention-Name-To-Remove-2" });
			ResourceMetaData metaData = aed.getMetaData();
			metaData.setName(cls.getSimpleName());
			metaData.setDescription(ANNOTATOR_DESCRIPTION);
			metaData.setVendor(ANNOTATOR_VENDOR);
			metaData.setVersion(version);
			aed.setMetaData(metaData);
			XmlDescriptorWriter.exportXmlDescriptor(cls, aed, baseDescriptorDirectory);
		} catch (ResourceInitializationException e) {
			throw new RuntimeException(e);
		}
	}

}
