/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public interface TokenAttributeInserter {

	public void insertPartOfSpeech(Annotation annotation, PartOfSpeech pos);
	
	public void insertStem(Annotation annotation, Stem stem);
	
	public void insertLemma(Annotation annotation, Lemma lemma);
	
}
