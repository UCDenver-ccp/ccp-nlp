/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
	
	public ComplexSlotMention( IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars ) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
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
