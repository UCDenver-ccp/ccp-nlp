/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.ext.uima.types.Sentence;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class ExplicitSentenceCasInserter implements SentenceCasInserter {

	/**
	 * Inserts explicitly defined {@link Sentence} annotations into the CAS
	 */
	@Override
	public Annotation insertSentence(int spanStart, int spanEnd, JCas jCas) {
		Sentence sentence = new Sentence(jCas, spanStart, spanEnd);
		sentence.addToIndexes();
		return sentence;
	}

}
