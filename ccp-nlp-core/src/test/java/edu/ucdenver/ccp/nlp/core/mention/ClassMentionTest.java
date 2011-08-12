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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TestTextAnnotationCreatorTest;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

public class ClassMentionTest {

	Map<Integer, DefaultTextAnnotation> testAnnotations;

	@Before
	public void setUp() throws Exception {
		testAnnotations = TestTextAnnotationCreatorTest.getID2TextAnnotationMap();
	}

	/**
	 * Test that the constructor sets the mention name and initializes storage for the slot mentions and text
	 * annotations
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testConstructor() throws Exception {
		String classMentionName = "classMentionName";
		DefaultClassMention cm = new DefaultClassMention(classMentionName);
		/* check that the mention name was set */
		assertEquals(classMentionName, cm.getMentionName());
		/* check that the slot mention containers were initialized */
		assertNotNull(cm.getComplexSlotMentions());
		assertNotNull(cm.getPrimitiveSlotMentions());
		assertNull(cm.getTextAnnotation());

	}

	/**
	 * Test the addition of a slot mention
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testAddSlotMention() throws Exception {
		DefaultClassMention cm = new DefaultClassMention("classMentionName");
		StringSlotMention sm = new DefaultStringSlotMention("slotMentionName");
		/* verify that there are no slot mentions */
		assertEquals(0, cm.getPrimitiveSlotMentions().size());
		cm.addPrimitiveSlotMention(sm);
		/* check that one slot mention was added */
		assertEquals(1, cm.getPrimitiveSlotMentions().size());
	}

	/**
	 * Test the addition of a complex slot mention
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testAddComplexSlotMention() throws Exception {
		DefaultClassMention cm = new DefaultClassMention("classMentionName");
		DefaultComplexSlotMention csm = new DefaultComplexSlotMention("complexSlotMentionName");
		/* verify that there are no complex slot mentions */
		assertEquals(0, cm.getComplexSlotMentions().size());
		cm.addComplexSlotMention(csm);
		/* check that one complex slot mention was added */
		assertEquals(1, cm.getComplexSlotMentions().size());
	}

	/**
	 * Test the addition of a slot mention
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testAddTextAnnotation() throws Exception {
		DefaultClassMention cm = new DefaultClassMention("classMentionName");
		new DefaultTextAnnotation(0, 1, "", new Annotator(-1, "", "", ""), new AnnotationSet(), -1, -1, "", -1, cm);
		/* verify that there are no slot mentions */
		assertNotNull(cm.getTextAnnotation());
	}

	/**
	 * Test that we return all of the slot mention names, and only the unique slot mention names
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testGetSlotMentionNames() throws Exception {
		TextAnnotation ta = testAnnotations.get(-15);
		Set<String> expectedNames = new HashSet<String>();
		expectedNames.add("entrez_gene_id");
		expectedNames.add("processed text");
		assertEquals(expectedNames, ta.getClassMention().getPrimitiveSlotMentionNames());

		/* add another slot and test again */
		StringSlotMention sm = new DefaultStringSlotMention("new_slot_mention");
		ta.getClassMention().addPrimitiveSlotMention(sm);
		expectedNames.add("new_slot_mention");
		assertEquals(expectedNames, ta.getClassMention().getPrimitiveSlotMentionNames());

		/*
		 * add another slot with slot mention name: new_slot_mention. We expect the list of slot mention names to remain
		 * unchanged
		 */
		sm = new DefaultStringSlotMention("new_slot_mention");
		ta.getClassMention().addPrimitiveSlotMention(sm);
		assertEquals(expectedNames, ta.getClassMention().getPrimitiveSlotMentionNames());
	}

	// /**
	// * Test that all slot mentions with a particular name are returned
	// *
	// * @throws Exception
	// */
	// @Test(timeout=10000)
	// public void testGetSlotMentionsByName() throws Exception {
	// TextAnnotation ta = testAnnotations.get(-15);
	// SlotMention sm = new SlotMention("new_slot_mention");
	// ta.getClassMention().addSlotMention(sm);
	// sm = new SlotMention("new_slot_mention");
	// ta.getClassMention().addSlotMention(sm);
	//
	// assertEquals(2, ta.getClassMention().getSlotMentionsByName("new_slot_mention").size());
	// assertEquals(1, ta.getClassMention().getSlotMentionsByName("entrez_gene_id").size());
	// assertEquals(1, ta.getClassMention().getSlotMentionsByName("processed text").size());
	// assertNull(ta.getClassMention().getSlotMentionsByName("no mention of this name exists"));
	// }
	//
	// /**
	// * Test that all slot mentions with a particular name are returned
	// *
	// * @throws Exception
	// */
	// @Test(timeout=10000)
	// public void testGetComplexSlotMentionsByName() throws Exception {
	// TextAnnotation ta = testAnnotations.get(-10);
	// ComplexSlotMention csm = new ComplexSlotMention("new_complex_slot_mention");
	// ta.getClassMention().addComplexSlotMention(csm);
	// csm = new ComplexSlotMention("new_complex_slot_mention");
	// ta.getClassMention().addComplexSlotMention(csm);
	//
	// assertEquals(2, ta.getClassMention().getComplexSlotMentionsByName("new_complex_slot_mention").size());
	// assertEquals(1, ta.getClassMention().getComplexSlotMentionsByName("transport origin").size());
	// assertEquals(1, ta.getClassMention().getComplexSlotMentionsByName("transport participants").size());
	// assertNull(ta.getClassMention().getComplexSlotMentionsByName("no mention of this name exists"));
	// }

	// /**
	// * Test that the first slot mention added is returned
	// *
	// * @throws Exception
	// */
	// @Test(timeout=10000)
	// public void testReturnFirstSlotMention() throws Exception {
	// ClassMention cm = new ClassMention("classMention");
	// SlotMention sm = new SlotMention("slotMention1");
	// cm.addSlotMention(sm);
	// assertEquals("slotMention1", cm.getFirstSlotMention().getMentionName());
	//
	// sm = new SlotMention("slotMention2");
	// cm.addSlotMention(sm);
	// assertEquals("slotMention1", cm.getFirstSlotMention().getMentionName());
	// }

	/**
	 * test equals()
	 * 
	 * @throws Exception
	 */
	@Test()
	public void testEquals() throws Exception {
		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention e2f4ProteinMention = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(65, 70, "E2F-4", annotator2, annotationSet1, -12, -1, "1234", 0,
				e2f4ProteinMention);

		TextAnnotation matchingTA = testAnnotations.get(-12);
		TextAnnotation nonMatchingTA = testAnnotations.get(-15);
		assertTrue(e2f4ProteinMention.equals(matchingTA.getClassMention()));
		assertTrue(matchingTA.getClassMention().equals(e2f4ProteinMention));

		assertFalse(e2f4ProteinMention.equals(nonMatchingTA.getClassMention()));
		assertFalse(nonMatchingTA.getClassMention().equals(e2f4ProteinMention));
	}

	/**
	 * Testing to make sure we can print when inverse slots are present
	 * 
	 * @throws Exception
	 */
	@Test()
	// TIMING OUT!!
	public void testPrintMention() throws Exception {
		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein1CM = new DefaultClassMention("protein");

		// System.err.println("Default mention ID: " + protein1CM.getMentionID());

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		protein1CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein1Annotation = new DefaultTextAnnotation(65, 70, "Protein #1", annotator2, annotationSet1, -12, -1, "1234", 0,
				protein1CM);

		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein2CM = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(12345);
		protein2CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein2Annotation = new DefaultTextAnnotation(75, 80, "Protein #2", annotator2, annotationSet1, -13, -1, "1234", 0,
				protein2CM);

		/* Now link them with an inverse slot */
		DefaultComplexSlotMention isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein2CM);
		protein1CM.addComplexSlotMention(isEquivalentToCSM);

		isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein1CM);
		protein2CM.addComplexSlotMention(isEquivalentToCSM);

		// /* Now try and print and see what happens */
		// ClassMention.printMention(protein1CM, System.out, 0, new HashMap<Long, ClassMention>());
		// ClassMention.printMention(protein2CM, System.out, 0, new HashMap<Long, ClassMention>());

		String expectedOutputStr = "-CLASS MENTION: protein \"Protein #1\"\t[65..70]\n-    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999\n-    COMPLEX SLOT MENTION: is_equivalent_to\n-        CLASS MENTION: protein \"Protein #2\"\t[75..80]\n-            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 12345\n";
		assertEquals(expectedOutputStr.length(), protein1CM.toString().length());
		assertEquals(expectedOutputStr, protein1CM.toString());

	}

	/**
	 * Testing to make sure we can print when inverse slots are present
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 10000)
	// TIMING OUT???
	public void testMentionComparison() throws Exception {
		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein1CM = new DefaultClassMention("protein");

		// System.err.println("Default mention ID: " + protein1CM.getMentionID());

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		protein1CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein1Annotation = new DefaultTextAnnotation(65, 70, "Protein #1", annotator2, annotationSet1, -12, -1, "1234", 0,
				protein1CM);

		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein2CM = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(12345);
		protein2CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein2Annotation = new DefaultTextAnnotation(75, 80, "Protein #2", annotator2, annotationSet1, -13, -1, "1234", 0,
				protein2CM);

		/* Now link them with an inverse slot */
		DefaultComplexSlotMention isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein2CM);
		protein1CM.addComplexSlotMention(isEquivalentToCSM);

		isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein1CM);
		protein2CM.addComplexSlotMention(isEquivalentToCSM);

		/* Now try to compare protein1CM and protein1CM */
		assert (protein1CM.equals(protein1CM));

		assert (protein1CM.compareTo(protein1CM) == 0);

	}

	/**
	 * Testing to make sure we can print when inverse slots are present
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 10000)
	// TIMING OUT???
	public void testNewPrint() throws Exception {
		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein1CM = new DefaultClassMention("protein");

		// System.err.println("Default mention ID: " + protein1CM.getMentionID());

		/* create a slot for the Entrez ID for this protein mention */
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(9999999);
		protein1CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		Annotator annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein1Annotation = new DefaultTextAnnotation(65, 70, "Protein #1", annotator2, annotationSet1, -12, -1, "1234", 0,
				protein1CM);

		/* create a protein mention and annotation to fill the transport participants slot */
		DefaultClassMention protein2CM = new DefaultClassMention("protein");

		/* create a slot for the Entrez ID for this protein mention */
		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(12345);
		protein2CM.addPrimitiveSlotMention(entrezIDSlotMention);

		/* create a text annotation for the e2f3 protein */
		annotator2 = new Annotator(new Integer(-31), "Test Annotator", "#2", "CCP");
		annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation protein2Annotation = new DefaultTextAnnotation(75, 80, "Protein #2", annotator2, annotationSet1, -13, -1, "1234", 0,
				protein2CM);

		/* Now link them with an inverse slot */
		DefaultComplexSlotMention isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein2CM);
		protein1CM.addComplexSlotMention(isEquivalentToCSM);

		isEquivalentToCSM = new DefaultComplexSlotMention("is_equivalent_to");
		isEquivalentToCSM.addClassMention(protein1CM);
		protein2CM.addComplexSlotMention(isEquivalentToCSM);

		/* Now try to compare protein1CM and protein1CM */
		System.out.println("testing singleLineRepresentation");
		System.out.println(protein1CM.getSingleLineRepresentation());
		System.out.println("testing DocumentLevel singleLineRepresentation");
		System.out.println(protein1CM.getDocumentLevelSingleLineRepresentation());

	}

	@Test
	public void testHashCode() throws Exception {
		TextAnnotation ta1 = new DefaultTextAnnotation(0,0);
		DefaultClassMention cm1 = new DefaultClassMention("cm1");
		ta1.setClassMention(cm1);

		Set<DefaultClassMention> cmSet = new HashSet<DefaultClassMention>();
		cmSet.add(cm1);
		assertEquals(1, cmSet.size());

		TextAnnotation ta2 = new DefaultTextAnnotation(0,0);
		DefaultClassMention cm2 = new DefaultClassMention("cm2");
		ta2.setClassMention(cm2);
		cmSet.add(cm2);
		assertEquals(2, cmSet.size());

		TextAnnotation ta1duplicate = new DefaultTextAnnotation(0,0);
		DefaultClassMention cm1duplicate = new DefaultClassMention("cm1");
		ta1duplicate.setClassMention(cm1duplicate);
		cmSet.add(cm1duplicate);
		assertEquals(2, cmSet.size());

		assertEquals(cm1.hashCode(), cm1duplicate.hashCode());
	}

	
	
}
