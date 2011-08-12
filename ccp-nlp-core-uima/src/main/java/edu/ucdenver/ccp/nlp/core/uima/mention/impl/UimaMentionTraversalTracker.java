package edu.ucdenver.ccp.nlp.core.uima.mention.impl;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import edu.ucdenver.ccp.nlp.core.mention.IMentionTraversalTracker;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPMention;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class UimaMentionTraversalTracker implements IMentionTraversalTracker {
private static Logger logger = Logger.getLogger(UimaMentionTraversalTracker.class);
	private CCPMention ccpMention;
	private JCas jcas;
	
	
	
	public UimaMentionTraversalTracker(CCPMention ccpMention) {
//		super();
logger.debug("Initializing UimaMentionTraversalTracker for mention: " + ccpMention.getMentionName());		
		this.ccpMention = ccpMention;
		try {
			this.jcas = ccpMention.getCAS().getJCas();
		} catch (CASException e) {
			e.printStackTrace();
		}
	}

	public UUID getMentionIDForTraversal(UUID traversalID) {
		logger.debug("Getting mentionID for traversalID: " + traversalID);
		return UIMA_Util.getMentionIDForTraversal(ccpMention, traversalID);
	}

	public void removeMentionIDForTraversal(UUID traversalID) {
		logger.debug("Removing mentionID for traversalID: " + traversalID);
		UIMA_Util.removeMentionIDForTraversal(ccpMention, traversalID, jcas);
	}

	public void setMentionIDForTraversal(UUID mentionID, UUID traversalID) {
		logger.debug("Setting mentionID: " + mentionID + " for traversalID: " + traversalID);
		UIMA_Util.setMentionIDForTraversal(ccpMention, mentionID, traversalID, jcas);
	}

}
