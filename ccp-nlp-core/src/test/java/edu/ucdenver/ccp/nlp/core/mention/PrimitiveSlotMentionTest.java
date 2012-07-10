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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultBooleanSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class PrimitiveSlotMentionTest {

	private static Logger logger = Logger.getLogger(PrimitiveSlotMentionTest.class);

	static {
		BasicConfigurator.configure();
	}

	@Test
	public void testBooleanSlotMention() throws Exception {
		BooleanSlotMention bsm = new DefaultBooleanSlotMention("bsm");

		bsm.setBooleanValue(true);
		assertTrue(bsm.getBooleanValue());

		logger.info("Warning message expected here:");
		bsm.addSlotValue(false);
		assertFalse(bsm.getBooleanValue());
		assertEquals(1, bsm.getSlotValues().size());

		Collection<Boolean> multipleValues = new ArrayList<Boolean>();
		multipleValues.add(true);
		multipleValues.add(false);
		multipleValues.add(true);

		// logger.info("Error message expected here:");
		try {
			bsm.setSlotValues(multipleValues);
			fail("Should have thrown an exception here");
		} catch (Exception e) {
			assertTrue(true);
		}

		Collection<Boolean> singleValue = new ArrayList<Boolean>();
		singleValue.add(true);
		bsm.setSlotValues(singleValue);
		assertTrue(bsm.getBooleanValue());

		try {
			bsm.addSlotValues(multipleValues);
			fail("Should have thrown an exception here");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testEquals() throws Exception {
		BooleanSlotMention bsm = new DefaultBooleanSlotMention("bsm");
		bsm.setBooleanValue(true);

		BooleanSlotMention bsm2 = new DefaultBooleanSlotMention("bsm");
		bsm2.setBooleanValue(true);
		assertTrue(bsm.equals(bsm2));

		bsm2.setBooleanValue(false);
		assertFalse(bsm.equals(bsm2));

		IntegerSlotMention ism = new DefaultIntegerSlotMention("ism");
		assertFalse(bsm.equals(ism));

		ism.addSlotValue(55);
		IntegerSlotMention ism2 = new DefaultIntegerSlotMention("ism");
		ism2.addSlotValue(55);

		assertTrue(ism.equals(ism2));

	}
}
