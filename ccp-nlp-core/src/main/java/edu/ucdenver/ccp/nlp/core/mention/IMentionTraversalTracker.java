package edu.ucdenver.ccp.nlp.core.mention;

import java.util.UUID;

public interface IMentionTraversalTracker {

	
	public UUID getMentionIDForTraversal(UUID traversalID);

	
	public void setMentionIDForTraversal(UUID mentionID, UUID traversalID);

	
	public void removeMentionIDForTraversal(UUID traversalID);

	
}
