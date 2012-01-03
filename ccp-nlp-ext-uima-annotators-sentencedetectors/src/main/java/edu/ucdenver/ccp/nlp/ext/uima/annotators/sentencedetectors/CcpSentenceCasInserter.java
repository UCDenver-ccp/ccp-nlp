/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class CcpSentenceCasInserter implements SentenceCasInserter {

	/**
	 * Creates sentences in the CAS using the CCPTextAnnotation/CCPClassMention structure.
	 */
	@Override
	public Annotation insertSentence(int spanStart, int spanEnd, JCas jCas) {
		return UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionTypes.SENTENCE, spanStart, spanEnd, jCas);
	}

}
