package edu.ucdenver.ccp.nlp.core.annotation.impl;

/**
 * This exception is thrown when an incorrect representation of the CCPTextAnnotation or CCPMention is observed.
 * 
 * @author Bill Baumgartner
 * 
 */
public class KnowledgeRepresentationWrapperException extends RuntimeException {
	/**
	 * @param message
	 */
	public KnowledgeRepresentationWrapperException(String message) {
		super(message);
	}
}
