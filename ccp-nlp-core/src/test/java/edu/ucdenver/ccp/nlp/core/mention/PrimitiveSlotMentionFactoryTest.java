package edu.ucdenver.ccp.nlp.core.mention;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultPrimitiveSlotMentionFactory;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PrimitiveSlotMentionFactoryTest {
	private static Logger logger = Logger.getLogger(PrimitiveSlotMentionFactoryTest.class);

	@Test
	public void testFactoryMethod() throws Exception {
		PrimitiveSlotMention stringSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name",
				"slot value");
		System.err.println("sm class: " + stringSM.getClass().getName());
		assertTrue(stringSM instanceof StringSlotMention);
		assertEquals(1, stringSM.getSlotValues().size());

		PrimitiveSlotMention integerSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", 99);
		assertTrue(integerSM instanceof IntegerSlotMention);
		assertEquals(1, integerSM.getSlotValues().size());

		PrimitiveSlotMention booleanSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name",
				true);
		assertTrue(booleanSM instanceof BooleanSlotMention);
		assertEquals(1, booleanSM.getSlotValues().size());

		PrimitiveSlotMention floatSM = DefaultPrimitiveSlotMentionFactory
				.createPrimitiveSlotMention("slot name", 1.23f);
		assertTrue(floatSM instanceof FloatSlotMention);
		assertEquals(1, floatSM.getSlotValues().size());

		//logger.info("Error message expected here:");
		//PrimitiveSlotMention fileSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name",
		//		new File(""));
		//assertNull(fileSM);

	}

	@Test
	public void testFactoryMethodCollectionString() throws Exception {
		Collection<String> slotValues = new ArrayList<String>();
		slotValues.add(new String("string value 1"));
		slotValues.add(new String("string value 2"));
		PrimitiveSlotMention psm = DefaultPrimitiveSlotMentionFactory.
			createPrimitiveSlotMentionWithStringCollection(
				"testSlotName", slotValues);
	}
	@Test
	public void testFactoryMethodCollectionInteger() throws Exception {
		Collection<Integer> slotValues = new ArrayList<Integer>();
		slotValues.add(new Integer(1));
		slotValues.add(new Integer(2));
		PrimitiveSlotMention psm = DefaultPrimitiveSlotMentionFactory.
			createPrimitiveSlotMentionWithIntegerCollection(
				"testSlotName", slotValues);
	}
	@Test
	public void testFactoryMethodCollectionFloat() throws Exception {
		Collection<Float> slotValues = new ArrayList<Float>();
		slotValues.add(new Float(3.14F));
		slotValues.add(new Float(2.7F));
		PrimitiveSlotMention psm = DefaultPrimitiveSlotMentionFactory.
			createPrimitiveSlotMentionWithFloatCollection(
				"testSlotName", slotValues);
	}
	@Test
	public void testFactoryMethodCollectionBoolean() throws Exception {
		Collection<Boolean> slotValues = new ArrayList<Boolean>();
		slotValues.add(new Boolean(true));
		slotValues.add(new Boolean(false));
		PrimitiveSlotMention psm = DefaultPrimitiveSlotMentionFactory.
			createPrimitiveSlotMentionWithBooleanCollection(
				"testSlotName", slotValues);
	}

	/* compiler won't ever let you get close to mixing types...*/
	/***
	@Test(expected=java.lang.ClassCastException.class)
	public void testFactoryMethodListVariousTypes() 
	throws Exception {
		Collection<String> slotValues = new ArrayList<String>();
		slotValues.add(new String("string value"));
		slotValues.add(new Integer(1));
		slotValues.add(new Float(3.14));
		slotValues.add(new Boolean(true));
		PrimitiveSlotMention psm = DefaultPrimitiveSlotMentionFactory.
			createPrimitiveSlotMentionWithStringCollection(
				"testSlotName", slotValues);
	}
	***/

	@Test
	public void testFactoryMethodWhenValueIsAsString() throws Exception {
		PrimitiveSlotMention stringSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue(
				"slot name", "slot value");
		assertTrue(stringSM instanceof StringSlotMention);
		assertEquals(1, stringSM.getSlotValues().size());

		PrimitiveSlotMention integerSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue(
				"slot name", "99");
		assertTrue(integerSM instanceof IntegerSlotMention);
		assertEquals(1, integerSM.getSlotValues().size());

		PrimitiveSlotMention booleanSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue(
				"slot name", "true");
		assertTrue(booleanSM instanceof BooleanSlotMention);
		assertEquals(1, booleanSM.getSlotValues().size());

		PrimitiveSlotMention floatSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue(
				"slot name", "1.23");
		assertTrue(floatSM instanceof FloatSlotMention);
		assertEquals(1, floatSM.getSlotValues().size());

	}
}
