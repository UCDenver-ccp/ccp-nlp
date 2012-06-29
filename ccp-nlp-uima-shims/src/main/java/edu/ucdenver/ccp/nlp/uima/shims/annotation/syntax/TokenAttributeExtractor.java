package edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.syntax;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

public interface TokenAttributeExtractor {
	
	public String getTokenType();
	
	public List<PartOfSpeech> getPartsOfSpeech(Annotation annotation);
}
