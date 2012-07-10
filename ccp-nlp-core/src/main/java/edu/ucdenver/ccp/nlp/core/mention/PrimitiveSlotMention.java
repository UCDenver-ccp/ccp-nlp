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
package edu.ucdenver.ccp.nlp.core.mention;

import java.util.List;

import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;

/**
 * The slot mention has slot values which are constrained to be <code>Objects</code>, but are typically filled with
 * <code>Strings</code>. An example of a non-complex slot mention would be the Entrez_Gene_ID slot for a gene class
 * mention that might be filled with the <code>String</code> "12345".
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class PrimitiveSlotMention<E> extends SlotMention<E> implements IPrimitiveSlotMention<E> {

//	public PrimitiveSlotMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
//		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars );
//	}
	
	public PrimitiveSlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars );
	}

//	public abstract void addSlotValue(String slotValue);

	/**
	 * Returns the string representation for a primitive slot mention. The input list is assumed to be ordered
	 * appropriately.
	 * 
	 * @param indentLevel
	 * @param sortedSlotValues
	 */
	protected String getStringRepresentation(int indentLevel, List<E> sortedSlotValues) {

		String returnStr = getIndentString(indentLevel) + "SLOT MENTION: " + getMentionName() + " with SLOT VALUE(s): ";
		for (E value : sortedSlotValues) {
			returnStr += (value + ", ");
		}
		returnStr = returnStr.substring(0, returnStr.length() - 2);

		return returnStr;
	}

	/**
	 * Compares two slot mentions by comparing their single line representations. By default we use the
	 * IdenticalSlotMentionComparator
	 */
	@Override
	public int compareTo(Mention m) {
		if (m instanceof PrimitiveSlotMention) {
			PrimitiveSlotMention smToCompare = (PrimitiveSlotMention) m;
			IdenticalMentionComparator ismc = new IdenticalMentionComparator();
			return ismc.compare(this, smToCompare);
			// return getSingleLineRepresentation().compareTo(compareSM.getSingleLineRepresentation());
		} else {
			logger.warn("Unexpected object when comparing to " + this.getClass().getName() + ": object = " + m.getClass().getName());
			return -1;
		}
	}

	/**
	 * Two slot mentions are equal if their slot mention name is equal, and their slot values are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PrimitiveSlotMention) {
			PrimitiveSlotMention sm = (PrimitiveSlotMention) obj;
		
			if (compareTo(sm)==0) {
				return true;
			}
			return false;
//			return this.equals(sm, new IdenticalMentionComparator(), null, -1, 0);
		} else {
			logger.warn("Cannot directly compare a SlotMention to " + obj.getClass().getName());
			return false;
		}
	}

//	public boolean equals(PrimitiveSlotMention smToCompare, SlotMentionComparator slotMentionComparator, SpanComparator spanComparator,
//			int maximumComparisonDepth, int depth) {
//		if (slotMentionComparator.compare(this, smToCompare, spanComparator, maximumComparisonDepth, depth) == 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}

public abstract void addSlotValueAsString(String slotValue) throws InvalidInputException;
	
	
}
