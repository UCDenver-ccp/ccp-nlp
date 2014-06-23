package edu.ucdenver.ccp.nlp.wrapper.conceptmapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.UimaContext;
import org.apache.uima.util.Level;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;


import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

import org.apache.log4j.Logger;

/**
 * Take a CCPTextAnnotation and change the ClassMentionName
 * (identified by regex PARAM_ORIG_MENTION)
 * to something generic (PARAM_MENTION_TYPE) and move the 
 * original name into a slot with name "ID".
 * @author Karin Verspoor
 *
 */
public class MapNameToIDSlot_AE extends JCasAnnotator_ImplBase {

	public static Logger logger = Logger.getLogger(MapNameToIDSlot_AE.class);
	public static String PARAM_MENTION_TYPE = "MentionTypeOut";
	public static String PARAM_ORIG_MENTION = "MentionTypeIn_RegExp";
	
	Pattern mentionTypeInPattern;
	String mentionTypeOut;
	
	@Override	
	public void initialize(UimaContext context) 
	throws ResourceInitializationException {
		super.initialize(context);
			String mentionTypeIn = (String) context.getConfigParameterValue(PARAM_ORIG_MENTION);
			mentionTypeInPattern = Pattern.compile(mentionTypeIn);
			if (mentionTypeInPattern == null) {
				logger.error("MapNameToIDSlot_AE error: pattern \"" + mentionTypeIn + "\" doesn't compile. ");
				throw new ResourceInitializationException(new RuntimeException("MapNameToIDSlot_AE error: pattern \"" + mentionTypeIn + "\" doesn't compile. "));
			}
			mentionTypeOut = (String) context.getConfigParameterValue(PARAM_MENTION_TYPE);
	}

	@Override	
	public void process(JCas jcas)
	throws AnalysisEngineProcessException {
		try {
			FSIterator ccptaIterator = jcas.getAnnotationIndex(CCPTextAnnotation.type).iterator();
			while (ccptaIterator.hasNext()) {
					CCPTextAnnotation ccpta = (CCPTextAnnotation) ccptaIterator.next();
				String ccptaMentionName = ccpta.getClassMention().getMentionName();
				if (ccptaMentionName != null) {
					Matcher m = mentionTypeInPattern.matcher(ccptaMentionName);
					if ( m.matches() ) {
						CCPClassMention ccpcm = ccpta.getClassMention();
						ccpcm.setMentionName(mentionTypeOut);
						UIMA_Util.addSlotValue(ccpcm, "ID", ccptaMentionName);
					}
				}
			}
		} catch (CASException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException();
		}
	}


	public static AnalysisEngine createAnalysisEngine(
		TypeSystemDescription tsd,
		String outputType,
		String mentionRegex)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
			MapNameToIDSlot_AE.class, tsd,
			PARAM_MENTION_TYPE,	outputType,
			PARAM_ORIG_MENTION, mentionRegex);
	}


	public static AnalysisEngineDescription createAnalysisEngineDescription(
		TypeSystemDescription tsd,
		String outputType,
		String mentionRegex)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
			MapNameToIDSlot_AE.class, tsd,
			PARAM_MENTION_TYPE,	outputType,
			PARAM_ORIG_MENTION, mentionRegex);
	}


}
