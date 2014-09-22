package edu.ucdenver.ccp.nlp.uima.annotators.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * This utility analysis engine enables the user to remove duplicate annotations from the CAS indexes.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DuplicateAnnotationRemovalFilter_AE extends JCasAnnotator_ImplBase {

	static Logger logger = Logger.getLogger(DuplicateAnnotationRemovalFilter_AE.class);
	
	@Override
	public void initialize(UimaContext ac) throws ResourceInitializationException {
		super.initialize(ac);
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		Collection<CCPTextAnnotation> allAnnotations = new ArrayList<CCPTextAnnotation>();
		for (Iterator<Annotation> annotIter = JCasUtil.iterator(jcas, Annotation.class); annotIter.hasNext();) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				allAnnotations.add((CCPTextAnnotation) possibleAnnot);
			} else {
				logger.warn("CCPTextAnnotation expected but instead got " + possibleAnnot.getClass().getName());
			}
		}
		
		Collection<CCPTextAnnotation> redundantAnnotations = getRedundantAnnotations(allAnnotations);
		
		int count = 0;
		for (CCPTextAnnotation ccpTA : redundantAnnotations) {
			ccpTA.removeFromIndexes();
			count++;
		}
		logger.info("DuplicateAnnotationRemovalFilter Removed " + count + " duplicate annotations.");
		
	}
	
	private Collection<CCPTextAnnotation> getRedundantAnnotations(
			Collection<CCPTextAnnotation> inputAnnotations) {
		Collection<CCPTextAnnotation> redundantAnnotations = new ArrayList<CCPTextAnnotation>();
		Set<String> nonRedundantAnnotations = new HashSet<String>();
		for (CCPTextAnnotation ta : inputAnnotations) {
			if (!nonRedundantAnnotations.contains(ta.getClassMention().getMentionName() + "\t" + ta.getBegin() + "\t" + ta.getEnd())) {
				//System.err.println("Found NON redundant TA: " + ta.getCoveredText());
				nonRedundantAnnotations.add(ta.getClassMention().getMentionName() + "\t" + ta.getBegin() + "\t" + ta.getEnd());
			} else {
				//System.err.println("Found REDUNDANT TA: " + ta.getCoveredText());
				redundantAnnotations.add(ta);
			}
		}
		
		return redundantAnnotations;
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription(tsd));
	}
	
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(DuplicateAnnotationRemovalFilter_AE.class, tsd);
	}
	
}