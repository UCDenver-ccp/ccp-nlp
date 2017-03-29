package edu.ucdenver.ccp.nlp.uima.annotators.converter;

import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;

/**
 * If the type of an annotation starts with the OBO namespace, then remove it
 * and replace the underscore with a colon
 */
public class OboNamespaceRemover_AE extends JCasAnnotator_ImplBase {

	private static final String OBO_NAMESPACE = "http://purl.obolibrary.org/obo/";

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		for (Iterator<CCPTextAnnotation> annotIter = JCasUtil.iterator(jCas, CCPTextAnnotation.class); annotIter
				.hasNext();) {
			CCPTextAnnotation ccpTa = annotIter.next();
			String mentionName = ccpTa.getClassMention().getMentionName();
			if (mentionName.startsWith(OBO_NAMESPACE)) {
				mentionName = StringUtil.removePrefix(mentionName, OBO_NAMESPACE);
				mentionName = mentionName.replace("_", ":");
				ccpTa.getClassMention().setMentionName(mentionName);
			}
		}

	}

	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(OboNamespaceRemover_AE.class,
				TypeSystemUtil.getCcpTypeSystem());
	}

}
