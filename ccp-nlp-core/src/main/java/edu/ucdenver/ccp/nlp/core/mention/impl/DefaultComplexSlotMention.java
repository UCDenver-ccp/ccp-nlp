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
package edu.ucdenver.ccp.nlp.core.mention.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;

/**
 * A slot mention is deemed "complex" when its slot filler is a class mention as opposed to an Object, which is
 * typically a String.
 * <p>
 * An example of a complex slot mention is the "transported entity" slot for the protein-transport class which would be
 * filled with a protein class mention.
 * 
 *@author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DefaultComplexSlotMention extends ComplexSlotMention {
	private String mentionName;
	private long mentionID;
	protected Collection<ClassMention> classMentions;
//	protected Map<Integer, Long> traversalID2MentionIDMap;

	public DefaultComplexSlotMention(String mentionName) {
		super((Object[])null);
		this.mentionName = mentionName;
		classMentions = new ArrayList<ClassMention>();
	}

	@Override
	public ClassMention createClassMention(String classMentionName) {
		return new DefaultClassMention(classMentionName);
	}

	@Override
	protected void initializeFromWrappedMention(Object... wrappedObject) {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

	@Override
	public Object getWrappedObject() {
		throw new UnsupportedOperationException("The " + this.getClass().getSimpleName()
				+ " class does not support wrapping of another object.");
	}

//	@Override
//	protected void initializeMention() {
////		traversalID2MentionIDMap = new HashMap<Integer, Long>();
//	}

	public void addSlotValue(ClassMention classMention) throws InvalidInputException {
		if (classMention instanceof DefaultClassMention) {
			classMentions.add(classMention);
		} else {
			throw new InvalidInputException("Slot fillers for DefaultComplexSlotMentions can only be DefaultClassMentions!");
		}
	}

	public void addSlotValues(Collection<ClassMention> classMentions) throws InvalidInputException {
		for (ClassMention cm : classMentions) {
			classMentions.add(cm);
		}
	}

	public Collection<ClassMention> getSlotValues() {
		return classMentions;
	}

	public void overwriteSlotValues(ClassMention classMention) throws InvalidInputException {
		classMentions = new ArrayList<ClassMention>();
		addSlotValue(classMention);

	}

	public void setSlotValues(Collection<ClassMention> classMentions) throws InvalidInputException {
		this.classMentions = new ArrayList<ClassMention>();
		for (ClassMention cm : classMentions) {
			this.classMentions.add(cm);
		}
	}

	@Override
	public long getMentionID() {
return mentionID;
}

	@Override
	public String getMentionName() {
return mentionName;
}

	@Override
	public void setMentionID(long mentionID) {
		this.mentionID = mentionID;
	}
	
	@Override
	protected void setMentionName(String mentionName) {
		this.mentionName = mentionName;
	}
	
//	@Override
//	protected Long getMentionIDForTraversal(int traversalID) {
//		return traversalID2MentionIDMap.get(traversalID);
//	}
//
//	@Override
//	protected void setMentionIDForTraversal(long mentionID, int traversalID) {
//		traversalID2MentionIDMap.put(traversalID, mentionID);
//	}
//
//	@Override
//	protected void removeMentionIDForTraversal(int traversalID) {
//		traversalID2MentionIDMap.remove(traversalID);
//	}
}
