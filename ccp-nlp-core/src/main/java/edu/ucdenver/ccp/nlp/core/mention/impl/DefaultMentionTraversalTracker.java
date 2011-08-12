package edu.ucdenver.ccp.nlp.core.mention.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.ucdenver.ccp.nlp.core.mention.IMentionTraversalTracker;

public class DefaultMentionTraversalTracker implements IMentionTraversalTracker{

	private Map<UUID, UUID> traversalID2MentionIDMap;
	
	public DefaultMentionTraversalTracker() {
		traversalID2MentionIDMap = new HashMap<UUID, UUID>();
	}
	
	public UUID getMentionIDForTraversal(UUID traversalID) {
		return traversalID2MentionIDMap.get(traversalID);
	}

	public void setMentionIDForTraversal(UUID mentionID, UUID traversalID) {
		traversalID2MentionIDMap.put(traversalID, mentionID);
	}

	
	public void removeMentionIDForTraversal(UUID traversalID) {
		traversalID2MentionIDMap.remove(traversalID);
	}

}
