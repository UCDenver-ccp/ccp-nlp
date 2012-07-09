/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.core.mention.comparison;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.tree.TreeNode;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;

/**
 * This is an abstract class for mention comparators. Each level of comparison stops at the class mention level.<br>
 * Level 0: comparison of the classmention names.<br>
 * Level 1: comparison of the classmention names, and optionally their text annotation spans, and if they match, a
 * comparison of the slotmentions and their slot values, and a comparison of complexslotmentions and their
 * classmentionnames<br>
 * Level 2: Comparison of the class mentions in level in the previous level that were only compared by mention name<br>
 * ...<br>
 * Level N: Comparison of the class mentions in level in the previous level that were only compared by mention name<br>
 * <br>
 * Example:<br>
 * TA1<br>
 * --- CM1<br>
 * ------ SM1a<br>
 * ---------SV1a<br>
 * ------ SM1b<br>
 * ---------SV1b<br>
 * ------ CSM1a<br>
 * --------- CM2 ==> TA2<br>
 * ------------ SM2a<br>
 * --------------- SV2a<br>
 * ------------ CSM2a<br>
 * --------------- CM3 ==> TA3<br>
 * ------ CSM1b<br>
 * --------- CM4 ==> TA4<br>
 * ------------ CSM4a<br>
 * -------------- CM5 ==> TA5<br>
 * <br>
 * Level 0: TA1 span and CM1 mention name compared<br>
 * Level 1: SM1a name and values, SM1b name and values, CM2 name, TA2 span, CM4 name, and TA4 span are also compared<br>
 * Level 2: SM2a name and values, CM3 name, TA3 span, CM5 name, TA5 span<br>
 * Level 3: CM3 and CM5 slots if there are any <br>
 * 
 * 
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public abstract class MentionComparator implements Comparator<Mention> {
	private static Logger logger = Logger.getLogger(MentionComparator.class);
	/**
	 * So that differences found in mentions can be distinguished between differences found in spans, the mention
	 * comparators should return -3, 0, or 3, therefore the MULTIPLIER variable is used to scale the compare() output.
	 */
	protected final int MULTIPLIER = 3;

	/**
	 * The default use of compare() will compare all depth levels of the mention heirarchy, and will compare any linked
	 * TextAnnotations using the StrictSpanComparator. This is specified by a maximum depth of -1.<br>
	 * <br>
	 * So that differences found in mentions can be distinguished between differences found in spans, the mention
	 * comparators should return -3, 0, or 3 (instead of -1, 0, or 1)
	 * 
	 * @param mention1
	 * @param mention2
	 */
	public int compare(Mention mention1, Mention mention2) {
		return compare(mention1, mention2, new StrictSpanComparator(), -1);
	}

	public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth) {
		Iterator<TreeNode<Mention>> nodesForMention1Iter = Mention.getMentionTreeNodeIterator(mention1);
		Iterator<TreeNode<Mention>> nodesForMention2Iter = Mention.getMentionTreeNodeIterator(mention2);

		do {
			TreeNode<Mention> nodeForMention1 = getNextNodeAboveDesiredComparisonDepth(nodesForMention1Iter, maximumComparisonDepth);
			TreeNode<Mention> nodeForMention2 = getNextNodeAboveDesiredComparisonDepth(nodesForMention2Iter, maximumComparisonDepth);

			if (nodeForMention1 == null & nodeForMention2 == null) {
				break;
			}

			if ((nodeForMention1 == null & nodeForMention2 != null) | (nodeForMention1 != null & nodeForMention2 == null)) {
				return -1*MULTIPLIER;
			}

			if (nodeForMention1.getDepth() != nodeForMention2.getDepth()) {
//				System.err.println("Returning -1 0");
				return -1*MULTIPLIER;
			}

//			System.err.println("TEsting at depth: " + nodeForMention1.getDepth());
			if (compareMentionTreeNodes(nodeForMention1, nodeForMention2, spanComparator) != 0) {
//				System.err.println("Returning -1 1");
				return -1*MULTIPLIER;
			}

		} while (true);

		return 0;
	}

	// while (nodesForMention1Iter.hasNext()) {
	// /* Get the next two nodes that are at or above the desired comparison depth */
	// TreeNode<Mention> nodeForMention1 = nodesForMention1Iter.next();
	// if (maximumComparisonDepth > -1 & nodeForMention1.getDepth() > maximumComparisonDepth) {
	// /* cycle until we get a node above the desired comparison depth */
	// continue;
	// }
	// TreeNode<Mention> nodeForMention2 = null;
	// while (nodesForMention2Iter.hasNext()) {
	// /* cycle other mention iterator until we get a node above the desired comparison depth */
	// nodeForMention2 = nodesForMention2Iter.next();
	// if (maximumComparisonDepth == -1 || (maximumComparisonDepth > -1 & nodeForMention2.getDepth() <=
	// maximumComparisonDepth)) {
	// break;
	// }
	// System.err.println("Returning -1 0");
	// return -1;
	// }
	//
	// // if (!nodesForMention2Iter.hasNext()) {
	// // System.err.println("Returning -1 1");
	// // return -1;
	// // }
	//
	// if (nodeForMention1.getDepth() != nodeForMention2.getDepth()) {
	// /* If the nodes aren't at the same level, then cycle */
	// System.err.println("Returning -1 2");
	// return -1;
	// }
	//
	// // if (maximumComparisonDepth == -1 || (maximumComparisonDepth > -1 & nodeForMention1.getDepth() <=
	// // maximumComparisonDepth)) {
	// System.err.println("TEsting at depth: " + nodeForMention1.getDepth());
	// if (compareMentionTreeNodes(nodeForMention1, nodeForMention2, spanComparator) != 0) {
	// System.err.println("Returning -1 3");
	// return -1;
	// }
	// // }
	// }
	// /* The other list may have other nodes, but as long as they are under the requested comparison depth we are ok */
	// while (nodesForMention2Iter.hasNext()) {
	// // if (nodesForMention2Iter.hasNext()) {
	// if (maximumComparisonDepth == -1) {
	// System.err.println("Returning -1 4");
	// return -1;
	// }
	// TreeNode<Mention> node = nodesForMention2Iter.next();
	// if (node.getDepth() <= maximumComparisonDepth) {
	// System.err.println("Returning -1 5");
	// return -1;
	// }
	// }
	// System.err.println("Returning 0");
	// return 0;
	// }

	/**
	 * Returns the next node in the input Iterator<TreeNode> that is at or above the maximumComparisonDepth level.
	 * 
	 * @param nodeIter
	 * @param maximumComparisonDepth
	 * @return
	 */
	private TreeNode<Mention> getNextNodeAboveDesiredComparisonDepth(Iterator<TreeNode<Mention>> nodeIter, int maximumComparisonDepth) {
		while (nodeIter.hasNext()) {
			TreeNode<Mention> node = nodeIter.next();
			if (maximumComparisonDepth == -1 || (maximumComparisonDepth > -1 & node.getDepth() <= maximumComparisonDepth)) {
				return node;
			}
		}
		return null;
	}

	// public abstract int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int
	// maximumComparisonDepth, int depth);

	protected abstract boolean hasEquivalentMentionNames(Mention mention1, Mention mention2);

	private int compareMentionTreeNodes(TreeNode<Mention> nodeForMention1, TreeNode<Mention> nodeForMention2, SpanComparator spanComparator) {
		Mention mention1 = nodeForMention1.getNodeValue();
		Mention mention2 = nodeForMention2.getNodeValue();

		boolean mentionsAreSameType = mentionsAreSameType(mention1, mention2);
		boolean mentionsHaveSameNumberOfChildren = (getIteratorCount(nodeForMention1.getChildren()) == getIteratorCount(nodeForMention2
				.getChildren()));
		boolean mentionsHaveSameName = hasEquivalentMentionNames(mention1, mention2);

		boolean primitiveSlotValuesAreIdentical = true;
		if (mentionsAreSameType && mention1 instanceof PrimitiveSlotMention<?>) {
			Collection<?> slotValues1 = ((PrimitiveSlotMention<?>) mention1).getSlotValues();
			Collection<?> slotValues2 = ((PrimitiveSlotMention<?>) mention2).getSlotValues();

			if (slotValues1.size() != slotValues2.size()) {
				primitiveSlotValuesAreIdentical = false;
			}

			List<String> slotValues1Strs = new ArrayList<String>();
			for (Object obj : slotValues1) {
				slotValues1Strs.add(obj.toString());
			}
			List<String> slotValues2Strs = new ArrayList<String>();
			for (Object obj : slotValues2) {
				slotValues2Strs.add(obj.toString());
			}
			
			Collections.sort(slotValues1Strs);
			Collections.sort(slotValues2Strs);
			if (!slotValues1Strs.equals(slotValues2Strs)) {
				primitiveSlotValuesAreIdentical = false;
			}
		}

		boolean referencedTextAnntationSpansAreSame = true;
		if (mentionsAreSameType & mention1 instanceof ClassMention) {
			referencedTextAnntationSpansAreSame = (spanComparator.compare(((ClassMention) mention1).getTextAnnotation().getSpans(),
					((ClassMention) mention2).getTextAnnotation().getSpans()) == 0);
		}

//		System.err.println("Same type: " + mentionsAreSameType);
//		System.err.println("Same #children: " + mentionsHaveSameNumberOfChildren);
//		System.err.println("Same name: " + mentionsHaveSameName);
//		System.err.println("Same primitive slots: " + primitiveSlotValuesAreIdentical);
//		System.err.println("Same spans: " + referencedTextAnntationSpansAreSame);

		if (mentionsAreSameType & mentionsHaveSameNumberOfChildren & mentionsHaveSameName & primitiveSlotValuesAreIdentical
				& referencedTextAnntationSpansAreSame) {
			return 0;
		}
		return -1;
	}

	private boolean mentionsAreSameType(Mention mention1, Mention mention2) {
		if (mention1 instanceof ClassMention && mention2 instanceof ClassMention) {
			return true;
		} else if(mention1 instanceof PrimitiveSlotMention<?> && mention2 instanceof PrimitiveSlotMention<?>) {
			return true;
		}else if(mention1 instanceof ComplexSlotMention && mention2 instanceof ComplexSlotMention) {
			return true;
		}else {
//			System.err.println("MENTION TYPES DIFFER: " + mention1.getMentionName() + " (" + mention1.getClass().getName() + ") -- not equal to -- " + mention2.getMentionName() + " ("+ mention2.getClass().getName() +")");
			return false;
		}
	}
	
	/**
	 * Returns the # of entries for the input Iterator
	 * 
	 * @param iter
	 * @return
	 */
	protected int getIteratorCount(Iterator<TreeNode<Mention>> iter) {
		int count = 0;
		while (iter.hasNext()) {
			count++;
			iter.next();
		}
		return count;
	}

	// public abstract boolean hasEquivalentMentionNames(Mention mention1, Mention mention2);
	//
	// /**
	// * This method compares two lists of slot mentions by iterating through them. A quick check of the number of slot
	// * mentions in each list is made at first to see if the individual comparisons can be avoided.
	// */
	// protected boolean equalSlotMentions(Collection<PrimitiveSlotMention> smList1, Collection<PrimitiveSlotMention>
	// smList2,
	// SpanComparator spanComparator, SlotMentionComparator slotMentionComparator, int maximumComparisonDepth, int
	// depth) {
	// if (smList1.size() != smList2.size()) {
	// /* A quick check of the count of slot mentions */
	// return false;
	// } else {
	// /*
	// * iterate through each slot mention and see if it has a match in the other list. We must iterate because it
	// * is possible that there could be two slot mentions with identical names. This is not ideal, but it is
	// * possible -- this is no longer possible since the internal slot storage has been moved to a Map structure,
	// * replacing the previous List structure
	// */
	// for (PrimitiveSlotMention sm : smList1) {
	// boolean slotMentionHasAMatch = false;
	// for (PrimitiveSlotMention smToCompare : smList2) {
	// if (slotMentionComparator.compare(sm, smToCompare, spanComparator, maximumComparisonDepth, depth) == 0) {
	// slotMentionHasAMatch = true;
	// }
	// }
	// if (!slotMentionHasAMatch) {
	// return false;
	// }
	// }
	// /* if we have made it this far, then all slot mentions have a match */
	// return true;
	// }
	// }
	//
	// /**
	// * This method compares two lists of complex slot mentions by iterating through them.
	// */
	// protected boolean equalComplexSlotMentions(Collection<ComplexSlotMention> csmList1,
	// Collection<ComplexSlotMention> csmList2,
	// SpanComparator spanComparator, ComplexSlotMentionComparator csmComparator, int maximumComparisonDepth, int depth)
	// {
	// /* CAn't compare sizes as there may be empty complex slot mentions */
	// /* Remove empties */
	// List<ComplexSlotMention> nonEmptyCSMList1 = new ArrayList<ComplexSlotMention>();
	// int index = 0;
	// for (ComplexSlotMention csm : csmList1) {
	// Collection<ClassMention> cmList = csm.getClassMentions();
	// if (cmList != null) {
	// // System.err.println("CMLIST SIZE: " + cmList.size());
	// }
	//
	// if (cmList != null && cmList.size() > 0) {
	// // System.err.println("NON EMPTY CSM: " + csm.getMentionName());
	// nonEmptyCSMList1.add(csm);
	// }
	// }
	//
	// // System.err.println("NEW LIST SIZE: " + nonEmptyCSMList1.size());
	//
	// List<ComplexSlotMention> nonEmptyCSMList2 = new ArrayList<ComplexSlotMention>();
	// for (ComplexSlotMention csm : csmList2) {
	// Collection<ClassMention> cmList = csm.getClassMentions();
	// if (cmList != null && cmList.size() > 0) {
	// nonEmptyCSMList2.add(csm);
	// }
	// }
	//
	// csmList1 = nonEmptyCSMList1;
	// csmList2 = nonEmptyCSMList2;
	//
	// if (csmList1.size() != csmList2.size()) {
	// // System.err.println("CSM COMPARE: UNEQUAL # of CSMS: " + csmList1.size() + " != " + csmList2.size());
	// /* a quick check of the count of the complex slot mentions */
	// return false;
	// } else {
	// /* iterate through each CSM and see if it has a match */
	// for (ComplexSlotMention csm : csmList1) {
	// boolean csmHashAMatch = false;
	// for (ComplexSlotMention csmToCompare : csmList2) {
	// logger.debug("Comparing CSMs: " + csm.getMentionName() + " -to- " + csmToCompare.getMentionName());
	// if (csmComparator.compare(csm, csmToCompare, spanComparator, maximumComparisonDepth, depth) == 0) {
	// csmHashAMatch = true;
	// }
	// }
	// if (!csmHashAMatch) {
	// return false;
	// }
	// }
	// /* if we have made it this far, then all complex slot mentions have a match */
	// return true;
	// }
	//
	// }
}
