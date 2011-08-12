package edu.ucdenver.ccp.nlp.core.mention;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultPrimitiveSlotMentionFactory;


public class PrimitiveSlotMentionFactoryTest {
	private static Logger logger = Logger.getLogger(PrimitiveSlotMentionFactoryTest.class);

	@Test
	public void testFactoryMethod() throws Exception {
		PrimitiveSlotMention stringSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", "slot value");
		System.err.println("sm class: " + stringSM.getClass().getName());
		assertTrue(stringSM instanceof StringSlotMention);
		assertEquals(1, stringSM.getSlotValues().size());

		PrimitiveSlotMention integerSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", 99);
		assertTrue(integerSM instanceof IntegerSlotMention);
		assertEquals(1, integerSM.getSlotValues().size());

		PrimitiveSlotMention booleanSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", true);
		assertTrue(booleanSM instanceof BooleanSlotMention);
		assertEquals(1, booleanSM.getSlotValues().size());

		PrimitiveSlotMention floatSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", 1.23f);
		assertTrue(floatSM instanceof FloatSlotMention);
		assertEquals(1, floatSM.getSlotValues().size());

		// PrimitiveSlotMention doubleSM = PrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", 5.43d);
		// assertTrue(doubleSM instanceof DoubleSlotMention);
		// assertEquals(1, doubleSM.getSlotValues().size());

		logger.info("Error message expected here:");
		PrimitiveSlotMention fileSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMention("slot name", new File(""));
		assertNull(fileSM);

	}

	@Test
	public void testFactoryMethodWhenValueIsAsString() throws Exception {
		PrimitiveSlotMention stringSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue("slot name", "slot value");
		assertTrue(stringSM instanceof StringSlotMention);
		assertEquals(1, stringSM.getSlotValues().size());

		PrimitiveSlotMention integerSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue("slot name", "99");
		assertTrue(integerSM instanceof IntegerSlotMention);
		assertEquals(1, integerSM.getSlotValues().size());

		PrimitiveSlotMention booleanSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue("slot name", "true");
		assertTrue(booleanSM instanceof BooleanSlotMention);
		assertEquals(1, booleanSM.getSlotValues().size());

		PrimitiveSlotMention floatSM = DefaultPrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue("slot name", "1.23");
		assertTrue(floatSM instanceof FloatSlotMention);
		assertEquals(1, floatSM.getSlotValues().size());

		// PrimitiveSlotMention doubleSM =
		// PrimitiveSlotMentionFactory.createPrimitiveSlotMentionFromStringValue("slot name", "5.43");
		// assertTrue(doubleSM instanceof DoubleSlotMention);
		// assertEquals(1, doubleSM.getSlotValues().size());

	}
}
