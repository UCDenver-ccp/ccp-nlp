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
