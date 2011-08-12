package edu.ucdenver.ccp.wrapper.knowtator.mention.impl;

import java.util.UUID;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.ucdenver.ccp.nlp.core.mention.IMentionTraversalTracker;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class KnowtatorMentionTraversalTracker implements IMentionTraversalTracker {

	private final SimpleInstance knowtatorMention;
	private final KnowtatorUtil ku;
	
	
	
	public KnowtatorMentionTraversalTracker(SimpleInstance knowtatorMention,
			KnowtatorUtil ku) {
		super();
		if (knowtatorMention == null) {
			throw new RuntimeException("Trying to create KnowtatorMentionTraversalTracker for null knowtator mention!!!");
		}
		this.knowtatorMention = knowtatorMention;
		this.ku = ku;
	}

	public UUID getMentionIDForTraversal(UUID traversalID) {
		return ku.getMentionIDForTraversal(knowtatorMention, traversalID);
	}

	public void removeMentionIDForTraversal(UUID traversalID) {
		ku.removeTraversalID(traversalID, knowtatorMention);

	}

	public void setMentionIDForTraversal(UUID mentionID, UUID traversalID) {
		ku.setMentionIDForTraversal(mentionID, traversalID, knowtatorMention);

	}

}
