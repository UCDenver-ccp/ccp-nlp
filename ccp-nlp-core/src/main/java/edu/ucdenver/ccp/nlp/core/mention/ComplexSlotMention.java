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

import java.util.Collection;

/**
 * A slot mention is deemed "complex" when its slot filler is a class mention as opposed to an Object, which is
 * typically a String.
 * <p>
 * An example of a complex slot mention is the "transported entity" slot for the protein-transport class which would be
 * filled with a protein class mention.
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class ComplexSlotMention extends SlotMention<ClassMention> {

//	public ComplexSlotMention(String mentionName, IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars ) {
//		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars);
//	}
	
	public ComplexSlotMention(Object... wrappedObjectPlusGlobalVars ) {
		super(wrappedObjectPlusGlobalVars);
	}

	public abstract ClassMention createClassMention(String classMentionName);

	/**
	 * Get the class mentions that fill this complex slot
	 * 
	 * @return
	 */
	public Collection<ClassMention> getClassMentions() {
		return getSlotValues();
	}

	/**
	 * Set the class mentions that fill this complex slot
	 * 
	 * @param classMentions
	 * @throws InvalidInputException 
	 */
	public void setClassMentions(Collection<ClassMention> classMentions) throws InvalidInputException {
		setSlotValues(classMentions);
	}

	/**
	 * Add a class mention as a slot filler for this complex slot
	 * 
	 * @param cm
	 */
	public void addClassMention(ClassMention cm) {
		try {
			addSlotValue(cm);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	public void addClassMentions(Collection<ClassMention> classMentions) throws InvalidInputException {
		addSlotValues(classMentions);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		String csmStr = getIndentString(indentLevel) + "COMPLEX SLOT MENTION: " + getMentionName();
		return csmStr;
	}

	// public String getHashKey() {
	// String key = this.getMentionName();
	// for (ClassMention cm : classMentions) {
	// key += (" " + cm.getHashKey());
	// }
	// return key;
	// }

	@Override
	public int compareTo(Mention m) {
		if (m instanceof ComplexSlotMention) {
			ComplexSlotMention compareCSM = (ComplexSlotMention) m;
			return toString().compareTo(compareCSM.toString());
		} else {
			logger.warn("Unexpected object when comparing to ComplexSlotMention: object = " + m.getClass().getName());
			return -1;
		}
	}

	@Override
	/* Two complex slot mentions are equal if they have the same mention name, and their class mentions are also equal */
	public boolean equals(Object obj) {
		if (obj instanceof ComplexSlotMention) {
			ComplexSlotMention csm = (ComplexSlotMention) obj;
			if (this.getClassMentions().size() == csm.getClassMentions().size()) {
				/* if the cms's do not have an equal number of class mentions then return false */
				if (this.getMentionName().toLowerCase().equals(csm.getMentionName().toLowerCase())) {
					for (ClassMention cm : this.getClassMentions()) {
						boolean cmHasMatch = false;
						for (ClassMention cmToCompare : csm.getClassMentions()) {
							if (cm.equals(cmToCompare)) {
								cmHasMatch = true;
							}
						}
						if (!cmHasMatch) {
							return false;
						}
					}
					/* if we get to this point, then all class mentions have a match, so return true */
					return true;
				} else {
					/* the csm's have different mention names */
					return false;
				}
			} else {
				/* the csm's have an unequal number of class mentions */
				return false;
			}
		} else {
			logger.warn("Cannot directly compare a ComplexSlotMention to " + obj.getClass().getName());
			return false;
		}

	}

}
