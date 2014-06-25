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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SlotMentionTest {
	private StringSlotMention slotMention;

	@Before
	public void setUp() throws Exception {
		slotMention = new DefaultStringSlotMention("slotMentionName");
		slotMention.addSlotValue("value1");
		slotMention.addSlotValue("value2");
		slotMention.addSlotValue("value3");
	}

	/**
	 * Test that the constructor sets the mention name and initializes storage for the slot values
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructor() throws Exception {
		String mentionName = "mentionName";
		StringSlotMention sm = new DefaultStringSlotMention(mentionName);
		/* check that the mention name was set */
		assertEquals(mentionName, sm.getMentionName());
		/* check that the slot value container was initialized */
		assertNotNull(sm.getSlotValues());
	}

	/**
	 * Test the adding of a slot value
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddSlotValue() throws Exception {
		int numSlotValues = slotMention.getSlotValues().size();
		slotMention.addSlotValue("new value");
		/* check that there is now one more slot value */
		assertEquals(numSlotValues + 1, slotMention.getSlotValues().size());
	}

	/**
	 * Test the equals method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEquals() throws Exception {
		StringSlotMention sm = new DefaultStringSlotMention("slotMentionName");
		sm.addSlotValue("value1");
		sm.addSlotValue("value2");

		assertFalse(sm.equals(slotMention));
		assertFalse(slotMention.equals(sm));
		assertTrue(sm.equals(sm));
		assertTrue(slotMention.equals(slotMention));

		sm.addSlotValue("value3");
		assertTrue(sm.equals(slotMention));
		assertTrue(slotMention.equals(sm));
	}

	@Test
	public void testSortDuringToString() throws Exception {
		StringSlotMention sm = new DefaultStringSlotMention("slotMentionName");
		sm.addSlotValue("value3");
		sm.addSlotValue("value1");

		String expectedStr = "-SLOT MENTION: slotMentionName with SLOT VALUE(s): value1, value3\n";
		assertEquals(expectedStr, sm.toString());

		sm.addSlotValue("value2");
		expectedStr = "-SLOT MENTION: slotMentionName with SLOT VALUE(s): value1, value2, value3\n";
		assertEquals(expectedStr, sm.toString());

		IntegerSlotMention ism = new DefaultIntegerSlotMention("slotMentionName");
		ism.addSlotValue(99);
		ism.addSlotValue(9);
		ism.addSlotValue(5);
		expectedStr = "-SLOT MENTION: slotMentionName with SLOT VALUE(s): 5, 9, 99\n";
		assertEquals(expectedStr, ism.toString());

	}

}
