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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;


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

	// /**
	// * Test that it returns the first slot value
	// * @throws Exception
	// */
	// @Test
	// public void testGetFirstSlotValue() throws Exception {
	// /* check that the first slot value is "value1" */
	// assertEquals("value1", slotMention.getFirstSlotValue());
	// }
	//    
	// /**
	// * Set the first slot value of this slot mention.
	// * @throws Exception
	// */
	// @Test
	// public void testSetFirstSlotValue() throws Exception {
	// SlotMention sm = new SlotMention("newMentionType");
	// /* verify that there are no slot values */
	// assertEquals(0, sm.getSlotValues().size());
	// sm.setFirstSlotValue("first value");
	// /* check that there is now one slot value */
	// assertEquals(1, sm.getSlotValues().size());
	// /* check that the first slot value contains "first value" */
	// assertEquals("first value", sm.getFirstSlotValue());
	//        
	// int valueCount = slotMention.getSlotValues().size();
	// /* verify that the first slot value is not "first value" */
	// assertNotSame("first value", slotMention.getFirstSlotValue());
	// slotMention.setFirstSlotValue("first value");
	// /* check that the same number of slot values exist */
	// assertEquals(valueCount, slotMention.getSlotValues().size());
	// /* check that the first slot value is now "first value" */
	// assertEquals("first value", slotMention.getFirstSlotValue());
	//        
	// }
	//
	// /**
	// * Test the single-line representation of a slot mention
	// * @throws Exception
	// */
	// @Test
	// public void testGetSingleLineRepresentation() throws Exception {
	// String expectedRepresentation = " [slotMentionName]:value1:value2:value3";
	// /* check that the single line representation is what is expected */
	// assertEquals(expectedRepresentation, slotMention.getSingleLineRepresentation());
	// }

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
