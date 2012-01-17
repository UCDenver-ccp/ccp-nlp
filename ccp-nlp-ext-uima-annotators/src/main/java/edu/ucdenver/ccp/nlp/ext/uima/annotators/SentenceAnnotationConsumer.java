/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class SentenceAnnotationConsumer extends JCasAnnotator_ImplBase {

	private Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		/*
		 * Check to see if there are any Sentence annotations in this CAS. If there are, then
		 * tokenize each sentence individually. If there are not, then treat the document text as a
		 * single sentence and tokenize it.
		 */
		List<TextAnnotation> annotationsToPutInCas = new ArrayList<TextAnnotation>();
		int sentenceCount = 0;
		for (FSIterator<Annotation> annotIter = jCas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator(); annotIter.hasNext();) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotIter.next();
			if (ccpTa.getClassMention().getMentionName().equalsIgnoreCase(ClassMentionTypes.SENTENCE)) {
				sentenceCount++;
				consumeSentence(ccpTa.getCoveredText(), ccpTa.getBegin(), jCas);
			}
		}
		if (sentenceCount == 0) {
			logger.log(Level.INFO, "No sentences in CAS, processing document text as a single sentence...");
			consumeSentence(jCas.getDocumentText(), 0, jCas);
		}
	}

	protected abstract void consumeSentence(String sentenceText, int sentenceStartOffset, JCas jCas)
			throws AnalysisEngineProcessException;

}
