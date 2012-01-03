/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface SentenceCasInserter {

	/**
	 * Inserts a sentence annotation into the CAS
	 * 
	 * @param spanStart
	 * @param spanEnd
	 * @param jCas
	 * @return the sentence annotation that was inserted
	 */
	public Annotation insertSentence(int spanStart, int spanEnd, JCas jCas);

}
