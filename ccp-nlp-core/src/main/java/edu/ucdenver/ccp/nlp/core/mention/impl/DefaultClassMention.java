package edu.ucdenver.ccp.nlp.core.mention.impl;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DefaultClassMention extends ClassMention {

	private String mentionName;
	private long mentionID;
	protected Map<String, PrimitiveSlotMention> primitiveSlotMentionsMap;
	protected Map<String, ComplexSlotMention> complexSlotMentionsMap;
	protected DefaultTextAnnotation textAnnotation;

	public DefaultClassMention(String mentionName) {
		super((Object[]) null);
		this.mentionName = mentionName;
		primitiveSlotMentionsMap = new HashMap<String, PrimitiveSlotMention>();
		complexSlotMentionsMap = new HashMap<String, ComplexSlotMention>();
	}

	@Override
	public Collection<ComplexSlotMention> getComplexSlotMentions() {
		return complexSlotMentionsMap.values();
	}

	@Override
	public void setComplexSlotMentions(Collection<ComplexSlotMention> complexSlotMentions) {
		this.complexSlotMentionsMap = new HashMap<String, ComplexSlotMention>();
		for (ComplexSlotMention csm : complexSlotMentions) {
			addComplexSlotMention(csm);
		}
	}

	@Override
	public Collection<PrimitiveSlotMention> getPrimitiveSlotMentions() {
		return primitiveSlotMentionsMap.values();
	}

	@Override
	public void setPrimitiveSlotMentions(Collection<PrimitiveSlotMention> primitiveSlotMentions) {
		this.primitiveSlotMentionsMap = new HashMap<String, PrimitiveSlotMention>();
		for (PrimitiveSlotMention psm : primitiveSlotMentions) {
			addPrimitiveSlotMention(psm);
		}
	}

	@Override
	public void addComplexSlotMention(ComplexSlotMention csm) {
		System.out.println("Add CSM: " + csm.getMentionName() + " SIZE: " + complexSlotMentionsMap.size());
		if (complexSlotMentionsMap.containsKey(csm.getMentionName())) {
				System.out.println("Getting value for key: " + csm.getMentionName());
				for (ClassMention cm : csm.getClassMentions()){
					complexSlotMentionsMap.get(csm.getMentionName()).addClassMention(cm);
				}
		} else {
			complexSlotMentionsMap.put(csm.getMentionName(), csm);
		}
	}

	@Override
	public void addPrimitiveSlotMention(PrimitiveSlotMention sm) {
		if (primitiveSlotMentionsMap.containsKey(sm.getMentionName())) {
				primitiveSlotMentionsMap.get(sm.getMentionName()).addSlotValues(sm.getSlotValues());
		} else {
			primitiveSlotMentionsMap.put(sm.getMentionName(), sm);
		}
	}

	@Override
	public Collection<String> getPrimitiveSlotMentionNames() {
		return primitiveSlotMentionsMap.keySet();
	}

	@Override
	public Collection<String> getComplexSlotMentionNames() {
		return complexSlotMentionsMap.keySet();
	}

	@Override
	public PrimitiveSlotMention getPrimitiveSlotMentionByName(String slotMentionName) {
		if (primitiveSlotMentionsMap.containsKey(slotMentionName)) {
			return primitiveSlotMentionsMap.get(slotMentionName);
		}
		return null;
	}

	@Override
	public ComplexSlotMention getComplexSlotMentionByName(String complexSlotMentionName) {
		if (complexSlotMentionsMap.containsKey(complexSlotMentionName)) {
			return complexSlotMentionsMap.get(complexSlotMentionName);
		}
		/* if we didn't find a slot by that name, create one and return it. */
		ComplexSlotMention csm = createComplexSlotMention(complexSlotMentionName);
		this.addComplexSlotMention(csm);
		return csm;
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

	@Override
	public ComplexSlotMention createComplexSlotMention(String slotMentionName) {
		return new DefaultComplexSlotMention(slotMentionName);
	}

	@Override
	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, String slotValue) {
		try {
			return DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slotMentionName, slotValue);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Float slotValue) {
		try {
			return DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slotMentionName, slotValue);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Integer slotValue) {
		try {
			return DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slotMentionName, slotValue);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PrimitiveSlotMention createPrimitiveSlotMention(String slotMentionName, Boolean  slotValue) {
		try {
			return DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention(slotMentionName, slotValue);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public TextAnnotation getTextAnnotation() {
		return textAnnotation;
	}

	@Override
	public void setTextAnnotation(TextAnnotation textAnnotation) throws InvalidInputException {
		if (textAnnotation instanceof DefaultTextAnnotation) {
			this.textAnnotation = (DefaultTextAnnotation) textAnnotation;
		} else {
			throw new InvalidInputException(
					"Input text annotation for DefaultClassMention must be of type DefaultTextAnnotation.");
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
	public void setMentionName(String mentionName) {
		this.mentionName = mentionName;
	}

}
