package edu.ucdenver.ccp.nlp.core.mention;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.tree.Tree;
import edu.ucdenver.ccp.common.collections.tree.TreeNode;

/**
 * The superclass for all mentions, <code>ClassMention</code>,<code>SlotMention</code>,<code>ComplexSlotMention</code>
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class Mention implements Comparable<Mention> {
	private static Logger logger = Logger.getLogger(Mention.class);
//	protected String mentionName;
//	protected long mentionID = -1;
//	protected int mentionLevel = -1;
protected IMentionTraversalTracker traversalTracker = null;
	protected boolean hasWrappedMention = false;

	protected static final int INITIAL_MENTION_ID_OFFSET = 99999999;

//	/**
//	 * The traversalID2MentionIDMap serves as a means for keeping track of parallel traversals of a mention hierarchy.
//	 * The mentionID (values in this map) are necessary because they are used to detect cycles in the hierarchy. Without
//	 * this feedback, cycles would result in an endless traversal due to infinite looping over the cycle. The traversal
//	 * IDs (keys in this map) permit multiple, simultaneous traversals over the same mention structure from interfering
//	 * with one another by overwriting mention IDs.
//	 */
//	protected Map<Integer, Long> traversalID2MentionIDMap;

	/**
	 * This Set<Integer> catalogs the traversal IDs currently in use. This allows/ensures that a unique traversal ID is
	 * generated for each mention hierarchy traversal.
	 */
	protected static Set<UUID> traversalIDsInUse = new HashSet<UUID>();

	protected static Random randomNumberGenerator = new Random();

	public Mention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super();
//		this.mentionID = -1;
//		this.mentionLevel = -1;
//		this.mentionName = mentionName;
		this.traversalTracker = traversalTracker;
//		traversalID2MentionIDMap = new HashMap<Integer, Long>();
//		initializeMention();
		if (wrappedObjectPlusGlobalVars != null && wrappedObjectPlusGlobalVars.length > 0 && wrappedObjectPlusGlobalVars[0] != null) {
			hasWrappedMention = true;
			try {
				initializeFromWrappedMention(wrappedObjectPlusGlobalVars);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setMentionName(mentionName);
	}

	public Mention(IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super();
//		this.mentionID = -1;
//		this.mentionLevel = -1;
//		this.mentionName = mentionName;
		this.traversalTracker = traversalTracker;
//		traversalID2MentionIDMap = new HashMap<Integer, Long>();
//		initializeMention();
		if (wrappedObjectPlusGlobalVars != null && wrappedObjectPlusGlobalVars.length > 0 && wrappedObjectPlusGlobalVars[0] != null) {
			hasWrappedMention = true;
			try {
				initializeFromWrappedMention(wrappedObjectPlusGlobalVars);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//	
	// public Mention(String mentionName) {
	// super();
	// this.mentionID = -1;
	// this.mentionLevel = -1;
	// this.mentionName = mentionName;
	// traversalID2MentionIDMap = new HashMap<Integer, Long>();
	// initializeMention();
	// }

	// public Mention(String mentionName, int mentionLevel) {
	// super();
	// this.mentionID = -1;
	// this.mentionLevel = mentionLevel;
	// this.mentionName = mentionName;
	// traversalID2MentionIDMap = new HashMap<Integer, Long>();
	// }

	// protected abstract void initializeMention(Object wrappedObject) {
	// classSpecificInitialization(wrappedObject);
	// }

	protected abstract void setMentionName(String mentionName);
	
//	protected abstract void initializeMention();

	protected abstract void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars) throws Exception;
	
	public abstract Object getWrappedObject();

//	public int getMentionLevel() {
//		return mentionLevel;
//	}
//
//	public void setMentionLevel(int mentionLevel) {
//		this.mentionLevel = mentionLevel;
//	}

	public abstract String getMentionName();
//	{
//		return mentionName;
//	}

	public UUID getMentionIDForTraversal(UUID traversalID) {
		return traversalTracker.getMentionIDForTraversal(traversalID);
	}

	public  void setMentionIDForTraversal(UUID mentionID, UUID traversalID) {
		 traversalTracker.setMentionIDForTraversal(mentionID, traversalID);
	}

	public  void removeMentionIDForTraversal(UUID traversalID)  {
		 traversalTracker.removeMentionIDForTraversal(traversalID);
	}
	

	public abstract long getMentionID();
//	{
//		return mentionID;
//	}

	public abstract void setMentionID(long mentionID);
//	{
//		this.mentionID = mentionID;
//	}

	public String getSingleLineRepresentation() {
		return toString().replaceAll("\\n", " ").replaceAll("\\s+", " ").replaceAll("\\t", " ");
	}

	public String getDocumentLevelSingleLineRepresentation() {
		return toDocumentLevelString().replaceAll("\\n", " ").replaceAll("\\s+", " ");
	}

	@Override
	public String toString() {
		boolean showReferencingAnnotationInfo = true;
		return mentionHierarchyToString(showReferencingAnnotationInfo);
	}

	/**
	 * Prints a "document-level" representation, i.e. only the mention stuff is printed, anything indicating where in
	 * the document it was located is disregarded.
	 * 
	 * @return
	 */
	public String toDocumentLevelString() {
		boolean showReferencingAnnotationInfo = false;
		return mentionHierarchyToString(showReferencingAnnotationInfo);
	}

	private String mentionHierarchyToString(boolean showReferencingAnnotationInfo) {
		StringBuffer sb = new StringBuffer();
		Tree<Mention> mentionTree = getMentionTree(this);
		Iterator<TreeNode<Mention>> treeNodeIter = mentionTree.depthFirstTraversal();
		while (treeNodeIter.hasNext()) {
			TreeNode<Mention> treeNode = treeNodeIter.next();
			Mention mention = treeNode.getNodeValue();
			sb.append(mention.getStringRepresentation(treeNode.getDepth(), showReferencingAnnotationInfo) + "\n");
		}
		return sb.toString();
	}

	public abstract String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo);

	/**
	 * Returns a String of dashes, length = indentLevel * 4
	 * 
	 * @param indentLevel
	 * @return
	 */
	protected String getIndentString(int indentLevel) {
		String indentStr = "-";
		for (int i = 0; i < indentLevel * 4; i++) {
			indentStr += " ";
		}
		return indentStr;
	}

	/**
	 * Returns an Iterator<Mention> over the mention hierarchy that has the input mention as root. The input mention is
	 * the first mention returned by the Iterator.
	 * 
	 * @param <T>
	 * @param mention
	 * @return
	 */
	public static Tree<Mention> getMentionTree(Mention mention) {
		return getMentionTree(mention, new HashSet<UUID>());
	}

	public static Iterator<TreeNode<Mention>> getMentionTreeNodeIterator(Mention mention) {
		Tree<Mention> tree = getMentionTree(mention);
		return tree.depthFirstTraversal();
	}

	/**
	 * Returns an Iterator<Mention> over the mention hierarchy that has the input mention as root. The input mention is
	 * the first mention returned by the Iterator.
	 * 
	 * @param <T>
	 * @param mention
	 * @param mentionIDOffset
	 * @param parentMentionIDs
	 * @return
	 */
	private static <E extends Mention> Tree<Mention> getMentionTree(E mention, Set<UUID> parentMentionIDs) {

		// if the mention iterator returns a Tree structure, then the mentionLevel will be stored inherently (the depth
		// of the tree node)
		// the iterator should return a depth first search of the tree (I think)

		// System.err.println("New Mention Iter: " + mention.getMentionName() + " -- " + parentMentionIDs);
		Tree<Mention> mentionTree = getCleanedSortedMentionList(mention, parentMentionIDs, generateTraversalID());
		return mentionTree;

		// return new Iterator<Mention>() {
		//
		// private Mention nextMention = null;
		// private int index = 0;
		//
		// public boolean hasNext() {
		// if (nextMention == null) {
		// if (index < mentions.size()) {
		// nextMention = mentions.get(index++);
		// return true;
		// }
		// return false;
		// }
		// return true;
		// }
		//
		// public Mention next() {
		// if (!hasNext()) {
		// throw new NoSuchElementException();
		// }
		// Mention mentionToReturn = nextMention;
		// nextMention = null;
		// return mentionToReturn;
		// }
		//
		// public void remove() {
		// throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
		// }
		//
		// @Override
		// public int hashCode() {
		// /* This has been intentionally left as the Object.hashCode() method */
		// return super.hashCode();
		// }
		// };

	}

	/**
	 * Produces a randomly generated traversal ID. This method checks the traversalIDsInUse Set to make sure it is
	 * unique, and then adds the new traversal ID to the set.
	 * 
	 * @return
	 */
	private static UUID generateTraversalID() {
		UUID id = UUID.randomUUID();
		while (traversalIDsInUse.contains(id)) {
			id = UUID.randomUUID();
		}
		traversalIDsInUse.add(id);
		return id;
	}

	private static void removeTraversalID(UUID traversalID) {
		traversalIDsInUse.remove(traversalID);
	}

	/**
	 * Returns a list of Mention objects, including the input CM sorted in a reproducible order: slot mentions before
	 * complex slot mentions, each subgroup ordered by mention name. "Clean" implies that the mention IDs used to track
	 * the traversal have been removed from each mention returned.
	 * 
	 * @param <E>
	 * @param mention
	 * @param mentionIdOffset
	 * @param parentMentionIDs
	 * @param traversalID
	 * @return
	 */
	private static <E extends Mention> Tree<Mention> getCleanedSortedMentionList(E mention,
			Set<UUID> parentMentionIDs, UUID traversalID) {

		Tree<Mention> sortedMentionsTree = getSortedMentionTree(mention, parentMentionIDs, traversalID);
		for (TreeNode<Mention> treeNode : sortedMentionsTree.getNodes()) {
			treeNode.getNodeValue().removeMentionIDForTraversal(traversalID);
		}

		removeTraversalID(traversalID);

		return sortedMentionsTree;
	}

	/**
	 * Returns a list of Mention objects, including the input CM sorted in a reproducible order: slot mentions before
	 * complex slot mentions, each subgroup ordered by mention name.
	 * 
	 * @param cm
	 * @return
	 */
	private static <E extends Mention> Tree<Mention> getSortedMentionTree(E mention, Set<UUID> parentMentionIDs,
			UUID traversalID) {
//		logger.debug("In getSortedMentionTree...");
		// System.err.println("Mention has an ID for traversal (" + traversalID + "): "
		// + (mention.getMentionIDForTraversal(traversalID) != null) + " -- " + parentMentionIDs);

		if (mention.getMentionIDForTraversal(traversalID) != null
				&& parentMentionIDs.contains(mention.getMentionIDForTraversal(traversalID))) {
			// System.err.println("MentionID in parent mention IDs... CYCLE DETECTED!!!");
			return new Tree<Mention>(null);
		}

		UUID mentionID = UUID.randomUUID();
		mention.setMentionIDForTraversal(mentionID, traversalID);

//		logger.debug("In getSortedMentionTree.. just set mentionID: " + mentionID + " for traversalID: " + traversalID);
		// System.err.println("Assigning MentionID: " + mentionID + " in Traversal: " + traversalID + " for " +
		// mention.getMentionName());
		// System.err
		// .println("Just assigned mention ID for traversal (" + traversalID + "): " +
		// mention.getMentionIDForTraversal(traversalID));
		TreeNode<Mention> rootNode = new TreeNode<Mention>(null, mention);

		// mentionsToReturn.add(mention);

		if (mention instanceof ClassMention) {
			rootNode = addSlotMentionsToList((ClassMention) mention, rootNode);
			rootNode = addComplexSlotMentionsToList((ClassMention) mention, rootNode, parentMentionIDs, traversalID);
		} else if (mention instanceof PrimitiveSlotMention) {
			// do nothing, this is a leaf mention
		} else if (mention instanceof ComplexSlotMention) {
			rootNode = addClassMentionsToList((ComplexSlotMention) mention, rootNode, parentMentionIDs, traversalID);
			// if the complex slot mention has no children, then we don't need to include it as part of the mention heirarchy so we set it to null here
			if (!rootNode.hasChildren()) 
				rootNode = null;
		} else {
			String errorMessage = String.format("Unknown mention subclass encountered: %s. Halting mention iterator...", mention.getClass().getName());
			throw new IllegalArgumentException(errorMessage);
		}
		Tree<Mention> mentionTreeToReturn = new Tree<Mention>(rootNode);
		return mentionTreeToReturn;
	}

	/**
	 * Adds slot mentions to a list of mentions in a reproducible order (alpha-order)
	 * 
	 * @param cm
	 * @param mentionList
	 * @return
	 */
	private static TreeNode<Mention> addSlotMentionsToList(ClassMention cm, TreeNode<Mention> parentNode) {
		List<String> sortedSlotMentionNames;
			sortedSlotMentionNames = sortMentionNames(cm.getPrimitiveSlotMentions());
		
		for (String mentionName : sortedSlotMentionNames) {
			TreeNode<Mention> childNode = new TreeNode<Mention>(parentNode, cm.getPrimitiveSlotMentionByName(mentionName));
			// mentionList.add(cm.getSlotMentionByName(mentionName));
		}
		return parentNode;
	}

	/**
	 * Adds complex slot mentions to a list of mentions in a reproducible order (alpha-order)
	 * 
	 * @param cm
	 * @param mentionList
	 * @return
	 */
	private static TreeNode<Mention> addComplexSlotMentionsToList(ClassMention cm, TreeNode<Mention> parentNode,
			Set<UUID> parentMentionIDs, UUID traversalID) {

		Set<UUID> parentMentionIDsClone = new HashSet<UUID>(parentMentionIDs);
		// System.err.println("CM Mention ID for Traversal (" + traversalID + "): " +
		// cm.getMentionIDForTraversal(traversalID));
		parentMentionIDsClone.add(cm.getMentionIDForTraversal(traversalID));

		List<String> sortedComplexSlotMentionNames = sortMentionNames(cm.getComplexSlotMentions());
		for (String mentionName : sortedComplexSlotMentionNames) {
			Tree<Mention> mentionTree = getSortedMentionTree(cm.getComplexSlotMentionByName(mentionName),
					parentMentionIDsClone, traversalID);
			mentionTree.setRootNode(parentNode);
			// mentionList.addAll(getSortedMentionList(cm.getComplexSlotMentionByName(mentionName), mentionIdOffset -
			// 1000,
			// parentMentionIDsClone, traversalID));
		}

		return parentNode;
	}

	/**
	 * Adds class mentions to a list of mentions in a reproducible order
	 * 
	 * @param csm
	 * @param mentionList
	 * @param mentionIdOffset
	 * @param parentMentionIDs
	 * @return
	 */
	private static TreeNode<Mention> addClassMentionsToList(ComplexSlotMention csm, TreeNode<Mention> parentNode,
			Set<UUID> parentMentionIDs, UUID traversalID) {
		Set<UUID> parentMentionIDsClone = new HashSet<UUID>(parentMentionIDs);
		// System.err.println("CSM Mention ID for Traversal (" + traversalID + "): " +
		// csm.getMentionIDForTraversal(traversalID));
		parentMentionIDsClone.add(csm.getMentionIDForTraversal(traversalID));

		List<ClassMention> sortedClassMentions = sortClassMentions(csm.getClassMentions());
		for (ClassMention cm : sortedClassMentions) {
			Tree<Mention> mentionTree = getSortedMentionTree(cm, parentMentionIDsClone, traversalID);
			mentionTree.setRootNode(parentNode);
			// mentionList.addAll(getSortedMentionList(cm, mentionIdOffset - 1000, parentMentionIDsClone, traversalID));
		}
		return parentNode;
	}

	/**
	 * Sorts class mentions alphabetically based on their toString() methods
	 * 
	 * @param classMentions
	 * @return
	 */
	private static List<ClassMention> sortClassMentions(Collection<ClassMention> classMentions) {
		List<ClassMention> cmList = Collections.list(Collections.enumeration(classMentions));
		Collections.sort(cmList, Mention.BY_ALPHA());
		return cmList;
	}

	/**
	 * A Comparator that compares 2 mentions based on their toString() output
	 * 
	 * @param <T>
	 * @return
	 */
	public static Comparator<Mention> BY_ALPHA() {
		return new Comparator<Mention>() {
			public int compare(Mention mention1, Mention mention2) {
				return mention1.toString().compareTo(mention2.toString());
			}

		};
	}

	/**
	 * Returns a sorted list of mention names
	 * 
	 * @param mentions
	 * @return
	 */
	private static <T extends Mention> List<String> sortMentionNames(Collection<T> mentions) {
		List<String> mentionNames = new ArrayList<String>();
		for (Mention mention : mentions) {
			mentionNames.add(mention.getMentionName());
		}
		Collections.sort(mentionNames);
		return mentionNames;
	}

	public abstract int compareTo(Mention m);

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}