package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.ext.syntax.PartOfSpeech;

public interface TokenAttributeExtractor {
	
	public String getTokenType();
	
	public List<PartOfSpeech> getPartsOfSpeech(Annotation annotation);
}