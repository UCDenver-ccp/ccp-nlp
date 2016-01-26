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
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;

/**
 * This class provides an easy way to configure a common, simple use-case of the comparator. Often,
 * all you are doing is setting up 2 groups: gold standard and test, with various identifying
 * attributes. For now, set ID, annotator ID and regex are exposed. This class can be initialized in
 * this restricted form without a configuration file. As an extension of the AnnotationComparator,
 * it requires the same parameters except for the config file.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SimpleAnnotationComparator_AE extends AnnotationComparator_AE {
	private static final Logger logger = Logger.getLogger(SimpleAnnotationComparator_AE.class);

	// Gold Standard Set
	public static final String PARAM_GOLD_SET_ID = ConfigurationParameterFactory.createConfigurationParameterName(
			SimpleAnnotationComparator_AE.class, "goldSetId");
	@ConfigurationParameter(mandatory = true)
	private int goldSetId;

	public static final String PARAM_GOLD_ANNOTATOR_ID = ConfigurationParameterFactory
			.createConfigurationParameterName(SimpleAnnotationComparator_AE.class, "goldAnnotatorId");
	@ConfigurationParameter(mandatory = true)
	private int goldAnnotatorId;

	public static final String PARAM_GOLD_TYPE_REGEX = ConfigurationParameterFactory.createConfigurationParameterName(
			SimpleAnnotationComparator_AE.class, "goldTypeRegexes");
	@ConfigurationParameter(mandatory = true)
	private String[] goldTypeRegexes;

	// Evaluation Set
	public static final String PARAM_EVAL_SET_ID = ConfigurationParameterFactory.createConfigurationParameterName(
			SimpleAnnotationComparator_AE.class, "evalSetId");
	@ConfigurationParameter(mandatory = true)
	private int evalSetId;

	public static final String PARAM_EVAL_ANNOTATOR_ID = ConfigurationParameterFactory
			.createConfigurationParameterName(SimpleAnnotationComparator_AE.class, "evalAnnotatorId");
	@ConfigurationParameter(mandatory = true)
	private int evalAnnotatorId;

	public static final String PARAM_EVAL_TYPE_REGEX = ConfigurationParameterFactory.createConfigurationParameterName(
			SimpleAnnotationComparator_AE.class, "evalTypeRegexes");
	@ConfigurationParameter(mandatory = true)
	private String[] evalTypeRegexes;

	/**
	 * This file configures the annotation and comparison group maps, not by parsing the config
	 * file, but from the extra parameters set_id, annotator_id and regex.
	 */
	@Override
	protected void parseConfigFile() {

		annotationGroupID2GroupMap = new HashMap<Integer, AnnotationGroup>();
		comparisonGroupID2GroupMap = new HashMap<Integer, ComparisonGroup>();

		// Gold Standard
		AnnotationGroup goldGroup = new AnnotationGroup(goldSetId, goldAnnotatorId, goldSetId);
		for (String goldGroupRegex : goldTypeRegexes) {
			goldGroup.addAnnotationTypeRegex(goldGroupRegex);
		}
		annotationGroupID2GroupMap.put(goldSetId, goldGroup);
		comparisonGroupID2GroupMap.put(goldSetId, new ComparisonGroup(true, "Gold Standard", goldSetId));

		// Eval
		AnnotationGroup evalGroup = new AnnotationGroup(evalSetId, evalAnnotatorId, evalSetId);
		for (String evalGroupRegex : evalTypeRegexes) {
			evalGroup.addAnnotationTypeRegex(evalGroupRegex);
		}
		annotationGroupID2GroupMap.put(evalSetId, evalGroup);
		comparisonGroupID2GroupMap.put(evalSetId, new ComparisonGroup(false, "Evaluation Set", evalSetId));

		annotationGroupID2ComparisonGroupIDMap = new HashMap<Integer, Set<Integer>>();
		annotationGroupID2ComparisonGroupIDMap.put(goldSetId, CollectionsUtil.createSet(goldSetId));
		annotationGroupID2ComparisonGroupIDMap.put(evalSetId, CollectionsUtil.createSet(evalSetId));

		goldStandardComparisonGroupID = goldSetId;
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			SpanComparatorType spanComparatorType, MentionComparatorType mentionComparatorType,
			File evaluationResultsOutputFile, AnnotationGroup goldGroup, AnnotationGroup evalGroup)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(SimpleAnnotationComparator_AE.class, tsd,
				PARAM_SPAN_COMPARATOR_TYPE_NAME, spanComparatorType.name(), PARAM_MENTION_COMPARATOR_TYPE_NAME,
				mentionComparatorType.name(), PARAM_ANNOTATION_OUTPUT_FILE,
				(evaluationResultsOutputFile != null) ? evaluationResultsOutputFile.getAbsolutePath() : null,
				PARAM_GOLD_SET_ID, goldGroup.getAnnotationSetID(), PARAM_GOLD_ANNOTATOR_ID, goldGroup.getAnnotatorID(),
				PARAM_GOLD_TYPE_REGEX,
				goldGroup.getAnnotationTypeRegexList().toArray(new String[goldGroup.getAnnotationTypeList().size()]),
				PARAM_EVAL_SET_ID, evalGroup.getAnnotationSetID(), PARAM_EVAL_ANNOTATOR_ID, evalGroup.getAnnotatorID(),
				PARAM_EVAL_TYPE_REGEX,
				evalGroup.getAnnotationTypeRegexList().toArray(new String[evalGroup.getAnnotationTypeList().size()]),
				PARAM_CONFIG_FILE, "not/a/real/file"); // config file parameter set b/c it
																	// is mandatory, but is never
																	// used
	}
}
