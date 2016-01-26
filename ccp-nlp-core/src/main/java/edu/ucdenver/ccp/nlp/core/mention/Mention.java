package edu.ucdenver.ccp.nlp.core.mention;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.tree.Tree;
import edu.ucdenver.ccp.common.collections.tree.TreeNode;

/**
 * The superclass for all mentions, <code>ClassMention</code>,<code>SlotMention</code>,
 * <code>ComplexSlotMention</code>
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class Mention implements Comparable<Mention> {
	private static Logger logger = Logger.getLogger(Mention.class);

	protected boolean hasWrappedMention = false;

	public Mention(String mentionName, Object... wrappedObjectPlusGlobalVars) {
		super();
		if (wrappedObjectPlusGlobalVars != null && wrappedObjectPlusGlobalVars.length > 0
				&& wrappedObjectPlusGlobalVars[0] != null) {
			hasWrappedMention = true;
			try {
				initializeFromWrappedMention(wrappedObjectPlusGlobalVars);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mentionName != null) {
			setMentionName(mentionName);
		}
	}

	public Mention(Object... wrappedObjectPlusGlobalVars) {
		this(null, wrappedObjectPlusGlobalVars);
	}

	public abstract void setMentionName(String mentionName);

	protected abstract void initializeFromWrappedMention(Object... wrappedObjectPlusGlobalVars);

	public abstract Object getWrappedObject();

	public abstract String getMentionName();

	public abstract long getMentionID();

	public abstract void setMentionID(long mentionID);

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
	 * Prints a "document-level" representation, i.e. only the mention stuff is printed, anything
	 * indicating where in the document it was located is disregarded.
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
	 * Returns an Iterator<Mention> over the mention hierarchy that has the input mention as root.
	 * The input mention is the first mention returned by the Iterator.
	 * 
	 * @param <T>
	 * @param mention
	 * @return
	 */
	public static Tree<Mention> getMentionTree(Mention mention) {
		return getMentionTree(mention, new IdentityHashMap<Mention, Integer>());
	}

	public static Iterator<TreeNode<Mention>> getMentionTreeNodeIterator(Mention mention) {
		Tree<Mention> tree = getMentionTree(mention);
		return tree.depthFirstTraversal();
	}

	/**
	 * Returns an Iterator<Mention> over the mention hierarchy that has the input mention as root.
	 * The input mention is the first mention returned by the Iterator.
	 * 
	 * @param <T>
	 * @param mention
	 * @param mentionIDOffset
	 * @param parentMentionIDs
	 * @return
	 */
	private static <E extends Mention> Tree<Mention> getMentionTree(E mention,
			IdentityHashMap<Mention, Integer> visitedMentionMap) {
		Tree<Mention> mentionTree = getSortedMentionTree(mention, visitedMentionMap);
		return mentionTree;
	}

	/**
	 * Returns a list of Mention objects, including the input CM sorted in a reproducible order:
	 * slot mentions before complex slot mentions, each subgroup ordered by mention name.
	 * 
	 * @param cm
	 * @return
	 */
	private static <E extends Mention> Tree<Mention> getSortedMentionTree(E mention,
			IdentityHashMap<Mention, Integer> visitedMentionMap) {
		if (visitedMentionMap.containsKey(mention)) {
			return new Tree<Mention>(null);
		}
		visitedMentionMap.put(mention, 1);
		TreeNode<Mention> rootNode = new TreeNode<Mention>(null, mention);

		if (mention instanceof ClassMention) {
			rootNode = addSlotMentionsToList((ClassMention) mention, rootNode);
			rootNode = addComplexSlotMentionsToList((ClassMention) mention, rootNode, visitedMentionMap);
		} else if (mention instanceof PrimitiveSlotMention) {
			// do nothing, this is a leaf mention
		} else if (mention instanceof ComplexSlotMention) {
			rootNode = addClassMentionsToList((ComplexSlotMention) mention, rootNode, visitedMentionMap);
			// if the complex slot mention has no children, then we don't need to include it as part
			// of the mention heirarchy so we set it to null here
			if (!rootNode.hasChildren()) {
				rootNode = null;
			}
		} else {
			String errorMessage = String.format(
					"Unknown mention subclass encountered: %s. Halting mention iterator...", mention.getClass()
							.getName());
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
			TreeNode<Mention> childNode = new TreeNode<Mention>(parentNode,
					cm.getPrimitiveSlotMentionByName(mentionName));
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
			IdentityHashMap<Mention, Integer> visitedMentionMap) {
		List<String> sortedComplexSlotMentionNames = sortMentionNames(cm.getComplexSlotMentions());
		for (String mentionName : sortedComplexSlotMentionNames) {
			Tree<Mention> mentionTree = getSortedMentionTree(cm.getComplexSlotMentionByName(mentionName),
					visitedMentionMap);
			mentionTree.setRootNode(parentNode);
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
			IdentityHashMap<Mention, Integer> visitedMentionMap) {
		List<ClassMention> sortedClassMentions = sortClassMentions(csm.getClassMentions());
		for (ClassMention cm : sortedClassMentions) {
			Tree<Mention> mentionTree = getSortedMentionTree(cm, visitedMentionMap);
			mentionTree.setRootNode(parentNode);
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
