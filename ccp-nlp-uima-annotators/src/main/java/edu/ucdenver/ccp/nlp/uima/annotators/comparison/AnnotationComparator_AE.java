/*
 * AnnotationComparator_CC.java 
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
 * Colorado Computational Pharmacology's common module
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.AnnotationComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.IgnoreSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.PRFResult;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedEndSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedStartOrEndSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedStartSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SloppySpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SubSpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.comparison.MentionComparator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.EvaluationResultProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalseNegativeProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.FalsePositiveProperty;
import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This Cas Consumer compares annotations (<code>CCPTextAnnotations</code>) found in the CAS.
 * Annotations are compared both by span, and by type. A configuration file is used to indicate how
 * the annotations should be compared, i.e. which are the gold standard annotations, etc.
 * <p>
 * See ComparatorConfigurator.java for a detailed description of the configuration file format.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class AnnotationComparator_AE extends JCasAnnotator_ImplBase {

	private static Logger logger = Logger.getLogger(AnnotationComparator_AE.class);

	public enum SpanComparatorType {
		STRICT,
		OVERLAP,
		SHARED_START,
		SHARED_END,
		SHARED_START_OR_END,
		SUB_SPAN,
		IGNORE_SPAN
	}

	public enum MentionComparatorType {
		IDENTICAL
	}

	/* comparator configuration file */
	public static final String PARAM_CONFIG_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			AnnotationComparator_AE.class, "configFile");
	@ConfigurationParameter(mandatory = true)
	private File configFile;

	public static final String PARAM_SPAN_COMPARATOR_TYPE_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationComparator_AE.class, "spanComparatorTypeName");
	@ConfigurationParameter(defaultValue = "STRICT")
	private String spanComparatorTypeName;

	public static final String PARAM_MENTION_COMPARATOR_TYPE_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationComparator_AE.class, "mentionComparatorTypeName");
	@ConfigurationParameter(defaultValue = "IDENTICAL")
	private String mentionComparatorTypeName;

	// /* span comparison strategies */
	// public static final String PARAM_USE_STRICT_SPAN_COMPARATOR = ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class, "useStrictSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useStrictSpanComparator;
	//
	// public static final String PARAM_USE_OVERLAP_SPAN_COMPARATOR = ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class, "useOverlapSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useOverlapSpanComparator;
	//
	// public static final String PARAM_USE_SHARED_START_SPAN_COMPARATOR =
	// ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class,
	// "useSharedStartSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useSharedStartSpanComparator;
	//
	// public static final String PARAM_USE_SHARED_END_SPAN_COMPARATOR =
	// ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class,
	// "useSharedEndSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useSharedEndSpanComparator;
	//
	// public static final String PARAM_USE_SHARED_START_OR_END_SPAN_COMPARATOR =
	// ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class,
	// "useSharedStartOrEndSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useSharedStartOrEndSpanComparator;
	//
	// public static final String PARAM_USE_SUB_SPAN_COMPARATOR = ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class, "useSubSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useSubSpanComparator;
	//
	// public static final String PARAM_USE_IGNORE_SPAN_COMPARATOR = ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class, "useIgnoreSpanComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useIgnoreSpanComparator;
	//
	// /* mention comparison strategies */
	// public static final String PARAM_USE_IDENTICAL_MENTION_COMPARATOR =
	// ConfigurationParameterFactory
	// .createConfigurationParameterName(AnnotationComparator_AE.class,
	// "useIdenticalMentionComparator");
	// @ConfigurationParameter(defaultValue = "false")
	// private boolean useIdenticalMentionComparator;

	/* if filled, TP, FP, and FN annotations will be printed to file */
	public static final String PARAM_ANNOTATION_OUTPUT_FILE = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationComparator_AE.class, "annotationOutputFile");
	@ConfigurationParameter()
	private File annotationOutputFile;

	public static final String PARAM_DO_ASSIGN_TPFPFN_META_PROPERTIES = ConfigurationParameterFactory
			.createConfigurationParameterName(AnnotationComparator_AE.class,
					"assignEvaluationResultPropertiesToAnnotations");
	@ConfigurationParameter(defaultValue = "false")
	private boolean assignEvaluationResultPropertiesToAnnotations;

	private SpanComparator spanComparator;

	private MentionComparator mentionComparator;

	private String spanComparatorUsed = "";

	private String mentionComparatorUsed = "";

	protected Integer goldStandardComparisonGroupID = -1;
	protected Map<Integer, PRFResult> comparisonGroupID2ScoreMap;

	protected Map<Integer, ComparisonGroup> comparisonGroupID2GroupMap;

	protected Map<Integer, AnnotationGroup> annotationGroupID2GroupMap;

	protected Map<Integer, Set<Integer>> annotationGroupID2ComparisonGroupIDMap;

	/*
	 * These sets will be used to track the annotation groups that are used and not used to make the
	 * comparisons. This information can be used by the user to debug the configuration file setup.
	 */
	protected Map<String, Integer> annotationGroupProfilesNotUsedDuringComparisons2CountMap;
	protected Map<String, Integer> annotationGroupProfilesUsedDuringComparisons2CountMap;

	private BufferedWriter annotationOutputWriter;

	/**
	 * Initializes this CAS Consumer with the parameters specified in the descriptor.
	 * 
	 * @throws ResourceInitializationException
	 *             if there is error in initializing the resources
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		logger.info("Annotation Comparison Set Up:");

		/* set the comparators to use based on the input parameters */
		assignComparatorsToUse();

		/* Retrieve output file if there is one */
		if (annotationOutputFile != null)
			try {
				annotationOutputWriter = FileWriterUtil.initBufferedWriter(annotationOutputFile,
						CharacterEncoding.UTF_8, WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
			} catch (FileNotFoundException e) {
				throw new ResourceInitializationException(e);
			}

		/* Parse the configuration file */
		parseConfigFile();

		/* Print the configuration for this comparator to the log */
		printConfigurationInformationToLog();

		/*
		 * Initialize the sets that will be used to track the annotations observed during processing
		 */
		annotationGroupProfilesNotUsedDuringComparisons2CountMap = new HashMap<String, Integer>();
		annotationGroupProfilesUsedDuringComparisons2CountMap = new HashMap<String, Integer>();

		/* Initialize the scores list, and a one PRFResult to the list for each comparison group */
		comparisonGroupID2ScoreMap = new HashMap<Integer, PRFResult>();
		for (ComparisonGroup cg : comparisonGroupID2GroupMap.values())
			comparisonGroupID2ScoreMap.put(cg.getID(), new PRFResult(0, 0, 0));

	}

	/**
	 * Assigns the span and mention comparators to use based on the input parameters for this CAS
	 * Consumer
	 * 
	 * @throws ResourceInitializationException
	 */
	protected void assignComparatorsToUse() throws ResourceInitializationException {
		switch (SpanComparatorType.valueOf(spanComparatorTypeName)) {
		case STRICT:
			spanComparator = new StrictSpanComparator();
			break;
		case OVERLAP:
			spanComparator = new SloppySpanComparator();
			break;
		case SHARED_START:
			spanComparator = new SharedStartSpanComparator();
			break;
		case SHARED_END:
			spanComparator = new SharedEndSpanComparator();
			break;
		case SHARED_START_OR_END:
			spanComparator = new SharedStartOrEndSpanComparator();
			break;
		case SUB_SPAN:
			spanComparator = new SubSpanComparator();
			break;
		case IGNORE_SPAN:
			spanComparator = new IgnoreSpanComparator();
			break;
		default:
			throw new IllegalArgumentException("Unhandled span comparator type: " + spanComparatorTypeName);
		}

		switch (MentionComparatorType.valueOf(mentionComparatorTypeName)) {
		case IDENTICAL:
			mentionComparator = new IdenticalMentionComparator();
			break;
		default:
			throw new IllegalArgumentException("Unhandled mention comparator type: " + spanComparatorTypeName);
		}

		spanComparatorUsed = spanComparator.getClass().getName()
				.substring(spanComparator.getClass().getName().lastIndexOf('.') + 1);
		mentionComparatorUsed = mentionComparator.getClass().getName()
				.substring(mentionComparator.getClass().getName().lastIndexOf('.') + 1);

		logger.info("Span Comparator = " + spanComparatorUsed);
		logger.info("Mention comparator = " + mentionComparatorUsed);

	}

	/**
	 * Parses the comparison configuration file, assigns the gold standard index. If no gold
	 * standard comparison group was indicated, then by default the first comparison group is
	 * treated as the gold standard.
	 */
	protected void parseConfigFile() {
		// /* read in input parameter for the configuration file name */
		// String configFileName = (String) context.getConfigParameterValue(PARAM_CONFIG_FILE);
		//
		// File configFile = new File(configFileName);
		if (configFile.exists()) {
			logger.info("Loading Annotation Comparator config file: " + configFile);
			ComparatorConfigurator configurator = new ComparatorConfigurator(configFile);
			annotationGroupID2GroupMap = configurator.getAnnotationGroupMap();
			comparisonGroupID2GroupMap = configurator.getComparisonGroupList();
			annotationGroupID2ComparisonGroupIDMap = configurator.getAnnotationGroupID2ComparisonGroupIDMap();
		} else {
			logger.error("Invalid comparator configuration file detected. File does not exist: "
					+ configFile.getAbsolutePath()
					+ "\nNo comparisons will be made until a valid configuration file is used.");
			annotationGroupID2GroupMap = new HashMap<Integer, AnnotationGroup>();
			comparisonGroupID2GroupMap = new HashMap<Integer, ComparisonGroup>();
			annotationGroupID2ComparisonGroupIDMap = new HashMap<Integer, Set<Integer>>();
		}

		/* Determine gold standard index */
		goldStandardComparisonGroupID = -1;
		for (ComparisonGroup cg : comparisonGroupID2GroupMap.values()) {
			if (cg.isGoldStandard()) {
				goldStandardComparisonGroupID = cg.getID();
			}
		}

		/* check to see that there was a gold standard group provided */
		if (goldStandardComparisonGroupID == -1) {
			logger.warn("No Gold Standard ComparisonGroup provided. "
					+ "By default, the first comparison group will be used as the gold standard.");
			goldStandardComparisonGroupID = 0;
		}

		/*
		 * Check to see that none of the annotation groups in the gold standard are also in other
		 * comparison groups -- this would lead to an unfair/biased comparison
		 */
		Set<Integer> annotationGroupIDsInGoldStandard = new HashSet<Integer>(comparisonGroupID2GroupMap.get(
				goldStandardComparisonGroupID).getAnnotationGroupList());
		for (Integer comparisonGroupID : comparisonGroupID2GroupMap.keySet()) {
			if (!comparisonGroupID.equals(goldStandardComparisonGroupID)) {
				List<Integer> annotationGroupsInCG = comparisonGroupID2GroupMap.get(comparisonGroupID)
						.getAnnotationGroupList();
				for (Integer annotationGroupID : annotationGroupsInCG) {
					if (annotationGroupIDsInGoldStandard.contains(annotationGroupID)) {
						logger.warn("An AnnotationGroup has been detected in both the Gold Standard ComparisonGroup and at least one other ComparisonGroup. This will lead to a biased evaluation for that comparison. Please adjust your comparator configuration file accordingly. The AnnotationGroup in question is: "
								+ annotationGroupID2GroupMap.get(annotationGroupID).toString());
					}
				}
			}
		}
	}

	protected void printConfigurationInformationToLog() {
		/*
		 * Print some useful information to the logger so the user can double-check what is expected
		 * from the configuration file being used.
		 */
		logger.info("============================================================================");
		logger.info("------ Annotation Groups Defined In Config File ------");
		List<Integer> annotationGroupIDList = Collections.list(Collections.enumeration(annotationGroupID2GroupMap
				.keySet()));
		Collections.sort(annotationGroupIDList);
		for (Integer annotationGroupID : annotationGroupIDList) {
			logger.info(annotationGroupID2GroupMap.get(annotationGroupID).toString());
		}

		Set<Integer> annotationGroupIDsAssignedToAComparisonGroup = new HashSet<Integer>();
		logger.info("------ Comparison Groups Defined In Config File ------");
		List<Integer> comparisonGroupIDList = Collections.list(Collections.enumeration(comparisonGroupID2GroupMap
				.keySet()));
		Collections.sort(comparisonGroupIDList);
		for (Integer comparisonGroupID : comparisonGroupIDList) {
			ComparisonGroup cg = comparisonGroupID2GroupMap.get(comparisonGroupID);
			logger.info(cg.toString());
			List<Integer> memberAnnotationGroupIDs = cg.getAnnotationGroupList();
			Collections.sort(memberAnnotationGroupIDs);
			for (Integer annotationGroupID : memberAnnotationGroupIDs) {
				annotationGroupIDsAssignedToAComparisonGroup.add(annotationGroupID);
				logger.info("~~~~ Member " + annotationGroupID2GroupMap.get(annotationGroupID).toString());
			}
		}

		/* Check for annotation groups that were not assigned to a comparison group */
		for (Integer annotationGroupID : annotationGroupIDList) {
			if (!annotationGroupIDsAssignedToAComparisonGroup.contains(annotationGroupID)) {
				logger.warn("An AnnotationGroup is defined in your configuration file but not assigned to a ComparisonGroup: "
						+ annotationGroupID2GroupMap.get(annotationGroupID).toString());
			}
		}
		logger.info("============================================================================");

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* get the documentID */
		String documentID = UIMA_Util.getDocumentID(jcas);

		/*
		 * Number the text annotations so that we can assign tp, fp, and fn properties after the
		 * comparison
		 */
		int count = 0;
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			ccpTA.setAnnotationID(count++);
		}

		/* populate the comparison groups for this JCas */
		Map<Integer, Collection<TextAnnotation>> comparisonGroupID2MemberTextAnnotationsMap = createComparisonGroupID2MemberTextAnnotationsMap(jcas);

		/*
		 * remove duplicate annotations from the comparison groups, otherwise the final numbers can
		 * be unfairly biased
		 */
		removeDuplicateAnnotationsFromComparisonGroups(comparisonGroupID2MemberTextAnnotationsMap);

		/* do annotation comparisons */
		Map<Integer, PRFResult> comparisonGroupID2ScoreForThisCASOnly;
		try {
			comparisonGroupID2ScoreForThisCASOnly = doAnnotationComparisons(documentID,
					comparisonGroupID2MemberTextAnnotationsMap);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

		PRFResult goldResult = comparisonGroupID2ScoreForThisCASOnly.get(goldStandardComparisonGroupID);
		int goldTpFnCount = goldResult.getTruePositiveCount() + goldResult.getFalseNegativeCount();
		for (Entry<Integer, PRFResult> entry : comparisonGroupID2ScoreForThisCASOnly.entrySet()) {
			PRFResult prf = entry.getValue();
			int tpFnCount = prf.getTruePositiveCount() + prf.getFalseNegativeCount();
			SpanComparatorType spanComparatorType = SpanComparatorType.valueOf(spanComparatorTypeName);
			if ((spanComparatorType.equals(SpanComparatorType.STRICT) && goldTpFnCount != tpFnCount)) {
				try {
				} finally {
					try {
						annotationOutputWriter.close();
					} catch (IOException e) {
						throw new AnalysisEngineProcessException(e);
					}
				}
					throw new IllegalStateException("GOLD STATS: " + goldResult.getStatsString() + "\nEVAL STATS: "
							+ prf.getStatsString() + "\nTP+FN count mismatch: " + tpFnCount + "!=" + goldTpFnCount
							+ " for document: " + documentID + "\nGOLD TP's:" + goldResult.getTPAnnotations().toString());
			}
		}

		/* assign tp, fp, and fn properties to the annotations in the JCas */
		if (assignEvaluationResultPropertiesToAnnotations) {
			assignTpFpFnMetaPropertiesToAnnotations(jcas, comparisonGroupID2ScoreForThisCASOnly);
		}
	}

	/**
	 * Assigns metadata properties to each annotation according to its classification as a TP, FP,
	 * or FN. Although an annotation can belong to multiple comparison groups, it will always have
	 * the same classification (TP, FP, or FN) because there is only one gold standard grouped
	 * allowed during the comparison.
	 * 
	 * @param jcas
	 */
	private void assignTpFpFnMetaPropertiesToAnnotations(JCas jcas,
			Map<Integer, PRFResult> comparisonGroupID2ScoreForThisCASOnly) {
		logger.info("Assigning TP, FP, and FN MetaData properties to annotations involved in the comparison for document: "
				+ UIMA_Util.getDocumentID(jcas) + "...");
		/* create a mapping from annotation ID to the comparison result */
		Map<Integer, PRFResult.ResultTypeEnum> annotationIdToResultTypeMap = getAnnotationIdToResultTypeMap(comparisonGroupID2ScoreForThisCASOnly);

		/*
		 * Cycle through each annotation in the CAS and assign the appropriate
		 * EvaluationResultProperty meta data
		 */
		Iterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			int annotID = ccpTA.getAnnotationID();
			if (annotationIdToResultTypeMap.containsKey(annotID)) {
				AnnotationMetadata metaData = ccpTA.getAnnotationMetadata();
				FSArray metaDataProperties;
				if (metaData == null) {
					metaData = new AnnotationMetadata(jcas);
					ccpTA.setAnnotationMetadata(metaData);
					metaDataProperties = new FSArray(jcas, 1);
				} else {
					FSArray oldMetaDataProperties = metaData.getMetadataProperties();
					metaDataProperties = new FSArray(jcas, oldMetaDataProperties.size() + 1);
					for (int i = 0; i < oldMetaDataProperties.size(); i++) {
						metaDataProperties.set(i, oldMetaDataProperties.get(i));
					}
				}
				EvaluationResultProperty erp = null;
				if (annotationIdToResultTypeMap.get(annotID).equals(PRFResult.ResultTypeEnum.TP)) {
					erp = new TruePositiveProperty(jcas);
				} else if (annotationIdToResultTypeMap.get(annotID).equals(PRFResult.ResultTypeEnum.FP)) {
					erp = new FalsePositiveProperty(jcas);
				} else if (annotationIdToResultTypeMap.get(annotID).equals(PRFResult.ResultTypeEnum.FN)) {
					erp = new FalseNegativeProperty(jcas);
				} else {
					logger.error("neither TP nor FP nor FN... should never be here.");
					throw new IllegalStateException("neither TP nor FP nor FN... should never be here.");
				}
				metaDataProperties.set(metaDataProperties.size() - 1, erp);
				metaData.setMetadataProperties(metaDataProperties);
			}
		}
	}

	/**
	 * Returns a mapping from the annotation ID to the result string (TP, FP, or FN).
	 * 
	 * @return
	 */
	private Map<Integer, PRFResult.ResultTypeEnum> getAnnotationIdToResultTypeMap(
			Map<Integer, PRFResult> comparisonGroupID2ScoreForThisCASOnly) {
		Map<Integer, PRFResult.ResultTypeEnum> annotationID2ResultStrMap = new HashMap<Integer, PRFResult.ResultTypeEnum>();
		for (Integer comparisonGroupID : comparisonGroupID2ScoreForThisCASOnly.keySet()) {
			if (!comparisonGroupID.equals(goldStandardComparisonGroupID)) {
				PRFResult prf = comparisonGroupID2ScoreForThisCASOnly.get(comparisonGroupID);

				// check a list of TextAnnotations for each result type
				for (PRFResult.ResultTypeEnum resultType : PRFResult.ResultTypeEnum.values()) {

					// check each annotation, get its type, add it to the map
					for (TextAnnotation ta : prf.getResultTypeAnnotations(resultType)) {
						int annotationID = ta.getAnnotationID();
						if (!annotationID2ResultStrMap.containsKey(annotationID)) {
							annotationID2ResultStrMap.put(annotationID, resultType);
						} else {
							/*
							 * Then this annotation is in multiple comparison groups. We should
							 * check that the current result is the same as the result from the
							 * previous PRFResult. Although an annotation can belong to multiple
							 * comparison groups, it will always have the same classification (TP,
							 * FP, or FN) because there is only one gold standard grouped allowed
							 * during the comparison.
							 */
							PRFResult.ResultTypeEnum previousResultType = annotationID2ResultStrMap.get(annotationID);
							if (!previousResultType.equals(resultType)) {
								logger.error("An annotation has been detected with multiple comparison results (TP, FP, FN). This should not be possible.");
								logger.error("The annotation has been classified as both: " + previousResultType
										+ " and " + resultType + " -- " + ta.getSingleLineRepresentation()
										+ " Its classification remains " + previousResultType
										+ ". There are likely multiple comparators in the pipeline.");
							}
						}
					}
				}
			}
		}

		return annotationID2ResultStrMap;
	}

	/**
	 * Populates a mapping from ComparisonGroupID to a list of the text annotations that belong to
	 * that comparison group.
	 * 
	 * @return
	 */
	private Map<Integer, Collection<TextAnnotation>> createComparisonGroupID2MemberTextAnnotationsMap(JCas jcas) {
		Map<Integer, Collection<TextAnnotation>> comparisonGroupID2MemberTextAnnotationsMap = new HashMap<Integer, Collection<TextAnnotation>>();

		/* get the TextAnnotations out of the CAS and put them in the proper comparison group(s) */
		// UIMA_Util uimaUtil = new UIMA_Util();
		List<TextAnnotation> annotations = UIMA_Util.getAnnotationsFromCas(jcas);

		/* Initialize a new list of TextAnnotation objects for each ComparisonGroup */
		for (ComparisonGroup cg : comparisonGroupID2GroupMap.values()) {
			comparisonGroupID2MemberTextAnnotationsMap.put(cg.getID(), new ArrayList<TextAnnotation>());
		}

		/*
		 * For each text annotation we determine the AnnotationGroups that it belongs to and then
		 * assign it to the ComparisonGroups that the AnnotationGroups belong to
		 */
		for (TextAnnotation ta : annotations) {
			boolean annotationIsAMemberOfAnAnnotationGroup = false;
			for (Integer annotationGroupID : annotationGroupID2GroupMap.keySet()) {
				AnnotationGroup annotationGroup = annotationGroupID2GroupMap.get(annotationGroupID);
				/* check to see if the annotation belongs to this annotation group */
				if (annotationGroup.hasMemberAnnotation(ta)) {
					/*
					 * The text annotation is a member of this annotation group, so assign it to all
					 * comparison groups associated with this annotation group
					 */
					Set<Integer> comparisonGroupIDs = annotationGroupID2ComparisonGroupIDMap.get(annotationGroupID);
					for (Integer comparisonGroupID : comparisonGroupIDs) {
						comparisonGroupID2MemberTextAnnotationsMap.get(comparisonGroupID).add(ta);
					}

					annotationIsAMemberOfAnAnnotationGroup = true;
					String annotationGroupProfile = getAnnotationGroupProfile(ta);
					if (!annotationGroupProfilesUsedDuringComparisons2CountMap.containsKey(annotationGroupProfile)) {
						annotationGroupProfilesUsedDuringComparisons2CountMap.put(annotationGroupProfile,
								new Integer(1));
					} else {
						int count = annotationGroupProfilesUsedDuringComparisons2CountMap.get(annotationGroupProfile);
						count++;
						annotationGroupProfilesUsedDuringComparisons2CountMap.remove(annotationGroupProfile);
						annotationGroupProfilesUsedDuringComparisons2CountMap.put(annotationGroupProfile, new Integer(
								count));
					}
				}
			}

			if (!annotationIsAMemberOfAnAnnotationGroup) {
				/*
				 * Then this TextAnnotation did not end up being a member of any AnnotationGroup, so
				 * we will log its annotation "profile" and report it to the user at the end (This
				 * is not necessarily a problem, but can serve as useful information if the user has
				 * not configured the AnnotationComparator correctly).
				 */
				String annotationGroupProfile = getAnnotationGroupProfile(ta);
				if (!annotationGroupProfilesNotUsedDuringComparisons2CountMap.containsKey(annotationGroupProfile)) {
					annotationGroupProfilesNotUsedDuringComparisons2CountMap
							.put(annotationGroupProfile, new Integer(1));
				} else {
					int count = annotationGroupProfilesNotUsedDuringComparisons2CountMap.get(annotationGroupProfile);
					count++;
					annotationGroupProfilesNotUsedDuringComparisons2CountMap.remove(annotationGroupProfile);
					annotationGroupProfilesNotUsedDuringComparisons2CountMap.put(annotationGroupProfile, new Integer(
							count));
				}
			}
		}

		return comparisonGroupID2MemberTextAnnotationsMap;
	}

	/**
	 * Runs the comparisons for all ComparisonGroups vs. the gold standard ComparisonGroup
	 * 
	 * @param documentID
	 * @param comparisonGroupID2MemberTextAnnotationsMap
	 * @throws IOException
	 */
	private Map<Integer, PRFResult> doAnnotationComparisons(String documentID,
			Map<Integer, Collection<TextAnnotation>> comparisonGroupID2MemberTextAnnotationsMap) throws IOException {
		AnnotationComparator annotationComparator = new AnnotationComparator();

		Map<Integer, PRFResult> comparisonGroupID2ScoreForThisCASOnly = new HashMap<Integer, PRFResult>();

		for (Integer comparisonGroupID : comparisonGroupID2MemberTextAnnotationsMap.keySet()) {
			Collection<TextAnnotation> goldStandardAnnotations = comparisonGroupID2MemberTextAnnotationsMap
					.get(goldStandardComparisonGroupID);
			Collection<TextAnnotation> compareAnnotations = comparisonGroupID2MemberTextAnnotationsMap
					.get(comparisonGroupID);

			PRFResult prf = annotationComparator.compare(goldStandardAnnotations, compareAnnotations, spanComparator,
					mentionComparator);

			/*
			 * Do not output the gold standard vs. gold standard comparison for the incremental
			 * output
			 */
			if (!comparisonGroupID.equals(goldStandardComparisonGroupID)) {
				StringBuffer output = new StringBuffer();
				output.append("\nDocumentID: " + documentID);
				output.append(" -- " + comparisonGroupID2GroupMap.get(goldStandardComparisonGroupID).getDescription()
						+ " (" + goldStandardAnnotations.size() + ") vs. "
						+ comparisonGroupID2GroupMap.get(comparisonGroupID).getDescription() + " ("
						+ compareAnnotations.size() + ")\n");
				if (annotationOutputWriter != null) {
					annotationOutputWriter.write(output.toString());
					annotationOutputWriter.newLine();
					prf.printAnnotations(annotationOutputWriter);
				} else {
					/*
					 * Tokenize on new lines and print each line to the logger separately... it just
					 * looks better this way
					 */
					String[] toks = (output + "\n" + prf.tpFpFnAnnotationsToString()).split("\\n");
					for (String tok : toks) {
						logger.debug(tok);
					}
				}
			}

			/* update the total score with this incremental score */
			comparisonGroupID2ScoreMap.get(comparisonGroupID).add(prf);
			comparisonGroupID2ScoreForThisCASOnly.put(comparisonGroupID, prf);
		}
		return comparisonGroupID2ScoreForThisCASOnly;
	}

	/**
	 * In this context a duplicate annotation is one that has identical span and identical slot
	 * fillers (all meta-data is ignored, e.g. annotator, annotation set, etc except for the
	 * document ID and documentCollectionID).
	 * 
	 * @param textAnnotationList
	 */
	private void removeDuplicateAnnotationsFromComparisonGroups(
			Map<Integer, Collection<TextAnnotation>> comparisonGroupID2MemberTextAnnotationsMap) {
		List<Integer> comparisonGroupIDsToUpdate = new ArrayList<Integer>();
		List<Collection<TextAnnotation>> memberTextAnnotationsForUpdates = new ArrayList<Collection<TextAnnotation>>();
		for (Integer comparisonGroupID : comparisonGroupID2MemberTextAnnotationsMap.keySet()) {
			Collection<TextAnnotation> memberTextAnnotations = comparisonGroupID2MemberTextAnnotationsMap
					.get(comparisonGroupID);
			Collection<TextAnnotation> memberTextAnnotationsWithoutDuplicates = TextAnnotationUtil
					.removeDuplicateAnnotations(memberTextAnnotations);

			if (memberTextAnnotations.size() != memberTextAnnotationsWithoutDuplicates.size()) {
				/*
				 * If there is a difference in sizes then that means duplicate annotations were
				 * found and removed, so we need to update the member annotations for this
				 * comparison group
				 */
				comparisonGroupIDsToUpdate.add(comparisonGroupID);
				memberTextAnnotationsForUpdates.add(memberTextAnnotationsWithoutDuplicates);
			}
		}
		/* Do the updates if there were any duplicate annotations found */
		for (int i = 0; i < comparisonGroupIDsToUpdate.size(); i++) {
			Integer comparisonGroupID = comparisonGroupIDsToUpdate.get(i);
			Collection<TextAnnotation> updatedMemberTextAnnotations = memberTextAnnotationsForUpdates.get(i);
			comparisonGroupID2MemberTextAnnotationsMap.remove(comparisonGroupID);
			comparisonGroupID2MemberTextAnnotationsMap.put(comparisonGroupID, updatedMemberTextAnnotations);
		}

	}

	/**
	 * Returns a String representation of the AnnotationGroups that the input TextAnnotation belongs
	 * to. This String is used to keep track of the TextAnnotations that are used in the comparisons
	 * and those that are not in an effort to give the user some feedback concerning potential
	 * configuration issues.
	 * 
	 * @param ta
	 * @return
	 */
	private String getAnnotationGroupProfile(TextAnnotation ta) {

		List<Integer> annotationSetIDs = Collections.list(Collections.enumeration(ta.getAnnotationSetIDs()));
		Collections.sort(annotationSetIDs);
		String annotationSetIDsStr = "";
		for (Integer annotationSetID : annotationSetIDs) {
			annotationSetIDsStr += (annotationSetID + ", ");
		}
		if (annotationSetIDsStr.length() > 0) {
			annotationSetIDsStr = annotationSetIDsStr.substring(0, annotationSetIDsStr.lastIndexOf(','));
		}
		String annotationGroupKey = "AnnotatorID: " + ta.getAnnotatorID() + " AnnotationSetIDs: " + annotationSetIDsStr
				+ " Type: " + ta.getClassMention().getMentionName();
		return annotationGroupKey;
	}

	/**
	 * Print the comparison results
	 */
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		/* prepare for TP+FN test */
		PRFResult prf = comparisonGroupID2ScoreMap.get(goldStandardComparisonGroupID);
		int gsTpFn = prf.getTruePositiveCount() + prf.getFalseNegativeCount();

		try {
			for (Integer comparisonGroupID : comparisonGroupID2GroupMap.keySet()) {
				/*
				 * We will skip the gold standard vs gold standard sanity check comparison and print
				 * it at the end
				 */
				if (!comparisonGroupID.equals(goldStandardComparisonGroupID)) {
					outputResultsForComparisonGroupID(comparisonGroupID);

					/* do TP+FN test */
					prf = comparisonGroupID2ScoreMap.get(comparisonGroupID);
					int testTpFn = prf.getTruePositiveCount() + prf.getFalseNegativeCount();
					SpanComparatorType spanComparatorType = SpanComparatorType.valueOf(spanComparatorTypeName);
					if ((spanComparatorType.equals(SpanComparatorType.STRICT) && gsTpFn != testTpFn)) {
						String errorMessage = "Gold Standard's TP + FN does not equal the TP+FN sum for id: "
								+ comparisonGroupID + ". values are: GS:" + gsTpFn + " testgroup: " + testTpFn;
						logger.error(errorMessage);
						throw new IllegalStateException(errorMessage);
					}
					logger.info("TP + FN test passed for id: " + comparisonGroupID + " " + gsTpFn + ", " + testTpFn);
				}
			}

			/* Now output the gold standard vs. gold standard sanity check */
			String sanityCheckStr = "=========== SANITY CHECK =========== SANITY CHECK =========== SANITY CHECK ===========";
			if (annotationOutputWriter != null) {
				annotationOutputWriter.write(sanityCheckStr);
				annotationOutputWriter.newLine();
			} else {
				logger.info(sanityCheckStr);
			}
			outputResultsForComparisonGroupID(goldStandardComparisonGroupID);

			printAnnotationProfileUsageSummary();

			printPRFOnLastLine();

			/* close the output file if there is one */
			if (annotationOutputWriter != null) {
				annotationOutputWriter.close();
			}
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Redundant printing of PRF stats on the last line of the file. This is useful for
	 * automatically processing the results, i.e. provides an easy way to extract the PRF stats from
	 * this file.
	 * 
	 * @throws IOException
	 */
	private void printPRFOnLastLine() throws IOException {
		for (Integer comparisonGroupID : comparisonGroupID2GroupMap.keySet()) {
			if (!comparisonGroupID.equals(goldStandardComparisonGroupID)) {
				ComparisonGroup comparisonGroup = comparisonGroupID2GroupMap.get(comparisonGroupID);
				PRFResult prf = comparisonGroupID2ScoreMap.get(comparisonGroupID);

				String outStr = comparisonGroup.getDescription() + StringConstants.TAB + prf.getTruePositiveCount()
						+ StringConstants.TAB + prf.getFalsePositiveCount() + StringConstants.TAB
						+ prf.getFalseNegativeCount() + StringConstants.TAB + "P=" + prf.getPrecision()
						+ StringConstants.TAB + "R=" + prf.getRecall() + StringConstants.TAB + "F=" + prf.getFmeasure();
				if (annotationOutputWriter != null) {
					annotationOutputWriter.write(outStr);
					annotationOutputWriter.newLine();
				}
				logger.info(outStr);
			}
		}
	}

	/**
	 * @param line
	 * @return a {@link PRFResult} parsed from the input {@link String} which uses the format
	 *         produced by the {{@link #printPRFOnLastLine()} method.
	 */
	public static PRFResult deserializeSummaryLine(String line) {
		String[] toks = line.split(RegExPatterns.TAB);
		String description = toks[0];
		int tp = Integer.parseInt(toks[1]);
		int fp = Integer.parseInt(toks[2]);
		int fn = Integer.parseInt(toks[3]);
		PRFResult prf = new PRFResult(tp, fp, fn);
		prf.setTitle(description);
		return prf;
	}

	/**
	 * Print out a log of the annotationGroup keys that were used and those that were not used. This
	 * can be helpful to the user for debugging configuration problems.
	 * 
	 * @throws IOException
	 */
	private void printAnnotationProfileUsageSummary() throws IOException {

		String annotationProfilesUsedStr = "=========== ANNOTATION PROFILES USED DURING THE COMPARISONS ===========";
		String annotationProfilesUsedDescriptionStr = "Listed below are profiles for annotations that were included in the comparisons. Each profile matches at least one AnnotationGroup set in your configuration file.";
		String annotationProfilesNOTUsedStr = "=========== ANNOTATION PROFILES *NOT* USED DURING THE COMPARISONS ===========";
		String annotationProfilesNOTUsedDescriptionStr = "Listed below are profiles for annotations that were not included in the comparisons. Please check this list to make sure everything you wanted to be included in the comparisons was.";
		if (annotationOutputWriter != null) {
			annotationOutputWriter.newLine();
			annotationOutputWriter.write(annotationProfilesUsedStr);
			annotationOutputWriter.newLine();
			annotationOutputWriter.write(annotationProfilesUsedDescriptionStr);
			annotationOutputWriter.newLine();
		} else {
			logger.info("");
			logger.info(annotationProfilesUsedStr);
			logger.info(annotationProfilesUsedDescriptionStr);
		}
		for (String annotationProfile : annotationGroupProfilesUsedDuringComparisons2CountMap.keySet()) {
			if (annotationOutputWriter != null) {
				annotationOutputWriter.write("USED PROFILE: " + annotationProfile + "("
						+ annotationGroupProfilesUsedDuringComparisons2CountMap.get(annotationProfile) + ")");
				annotationOutputWriter.newLine();
			} else {
				logger.info("USED PROFILE: " + annotationProfile + "("
						+ annotationGroupProfilesUsedDuringComparisons2CountMap.get(annotationProfile) + ")");
			}
		}

		if (annotationOutputWriter != null) {
			annotationOutputWriter.newLine();
			annotationOutputWriter.write(annotationProfilesNOTUsedStr);
			annotationOutputWriter.newLine();
			annotationOutputWriter.write(annotationProfilesNOTUsedDescriptionStr);
			annotationOutputWriter.newLine();
		} else {
			logger.info("");
			logger.info(annotationProfilesNOTUsedStr);
			logger.info(annotationProfilesNOTUsedDescriptionStr);
		}
		for (String annotationProfile : annotationGroupProfilesNotUsedDuringComparisons2CountMap.keySet()) {
			if (annotationOutputWriter != null) {
				annotationOutputWriter.write("NOT USED PROFILE: " + annotationProfile + "("
						+ annotationGroupProfilesNotUsedDuringComparisons2CountMap.get(annotationProfile) + ")");
				annotationOutputWriter.newLine();
			}
			logger.info("NOT USED PROFILE: " + annotationProfile + "("
					+ annotationGroupProfilesNotUsedDuringComparisons2CountMap.get(annotationProfile) + ")");

		}
	}

	/**
	 * Prints cumulative results to both the logger and the output stream (if not null)
	 * 
	 * @param comparisonGroupID
	 * @throws IOException
	 */
	private void outputResultsForComparisonGroupID(Integer comparisonGroupID) throws IOException {
		ComparisonGroup comparisonGroup = comparisonGroupID2GroupMap.get(comparisonGroupID);
		ComparisonGroup goldStandardComparisonGroup = comparisonGroupID2GroupMap.get(goldStandardComparisonGroupID);

		String outStr = "Annotation comparison results for: " + goldStandardComparisonGroup.getDescription() + " vs. "
				+ comparisonGroup.getDescription();

		PRFResult prf = comparisonGroupID2ScoreMap.get(comparisonGroupID);
		prf.setTitle("SpanComparator: " + spanComparatorUsed + " -- MentionComparator: " + mentionComparatorUsed);
		outStr += ("\n" + prf.toString());

		// /* Tokenize on new lines because it looks better when printing to the logger */
		// String[] toks = outStr.split("\\n");
		// for (String tok : toks) {
		// logger.info(tok);
		// }

		if (annotationOutputWriter != null) {
			annotationOutputWriter.newLine();
			annotationOutputWriter.write(outStr);
			annotationOutputWriter.newLine();
		} else {
			logger.info(prf.toString());
		}

	}

	public Integer getGoldStandardComparisonGroupID() {
		return goldStandardComparisonGroupID;
	}

	public Map<Integer, PRFResult> getComparisonGroupID2ScoreMap() {
		return comparisonGroupID2ScoreMap;
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File configurationFile,
			SpanComparatorType spanComparatorType, MentionComparatorType mentionComparatorType,
			File evaluationResultsOutputFile) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription(tsd, configurationFile,
				spanComparatorType, mentionComparatorType, evaluationResultsOutputFile));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			File configurationFile, SpanComparatorType spanComparatorType, MentionComparatorType mentionComparatorType,
			File evaluationResultsOutputFile) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotationComparator_AE.class, tsd, PARAM_CONFIG_FILE,
				configurationFile.getAbsolutePath(), PARAM_SPAN_COMPARATOR_TYPE_NAME, spanComparatorType.name(),
				PARAM_MENTION_COMPARATOR_TYPE_NAME, mentionComparatorType.name(), PARAM_ANNOTATION_OUTPUT_FILE,
				(evaluationResultsOutputFile != null) ? evaluationResultsOutputFile.getAbsolutePath() : null);
	}

}
