package edu.ucdenver.ccp.nlp.core.mention;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.tree.TreeNode;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MentionTest {

	private static final String FRAME_PLAYS_A_ROLE_IN_PROCESS = "plays role in process";
	private static final String SLOT_PLAYER = "hasPlayer";
	private static final String SLOT_PROCESS = "hasProcess";

	private static final String FRAME_REGULATION_OF_PROCESS = "regulation of process";
	private static final String SLOT_REGULATED_PROCESS = "regulated process";

	private static final String FRAME_PROTEIN_TRANSPORT = "protein transport";
	private static final String SLOT_CARGO = "cargo";
	private static final String SLOT_DESTINATION = "destination";

	private static final String FRAME_PROTEIN = "protein";
	private static final String SLOT_GENE_ID = "gene identifier";
	private static final String SLOT_IS_EQUIVALENT_TO = "is-equivalent-to";

	private static final String FRAME_NUCLEUS = "nucleus";

	private DefaultClassMention proteinXyzCM;
	private DefaultClassMention proteinAbcCM;
	private DefaultClassMention nucleusCM;
	private DefaultClassMention transportCM;
	private DefaultComplexSlotMention transportCargoCSM;
	private DefaultClassMention regulationCM;
	private DefaultClassMention playsARoleInProcessCM;
	private DefaultComplexSlotMention playerCSM;

	@Before
	public void initMentionHierarchy() throws Exception {

		/* __________________________1_________2_________3_________4_________5_________6_________7 */
		/* ________________01234567890123456789012345678901234567890123456789012345678901234567890 */
		@SuppressWarnings("unused")
		String testText = "XYZ-2 plays a role in the regulation of ABC-1 transport to the nucleus.";

		proteinXyzCM = new DefaultClassMention(FRAME_PROTEIN);
		StringSlotMention proteinXyzGeneIdSM = new DefaultStringSlotMention(SLOT_GENE_ID);
		proteinXyzGeneIdSM.addSlotValue("xyzID=12345");
		proteinXyzCM.addPrimitiveSlotMention(proteinXyzGeneIdSM);
		@SuppressWarnings("unused")
		TextAnnotation proteinXyzTA = new DefaultTextAnnotation(0, 5, "XYZ-2", new Annotator(1, "Bob", "The Annotator",
				"The U"), new AnnotationSet(), 1, -1, "1234", -1, proteinXyzCM);

		proteinAbcCM = new DefaultClassMention(FRAME_PROTEIN);
		StringSlotMention proteinAbcGeneIdSM = new DefaultStringSlotMention(SLOT_GENE_ID);
		proteinAbcGeneIdSM.addSlotValue("AbcID=98765");
		proteinAbcCM.addPrimitiveSlotMention(proteinAbcGeneIdSM);
		@SuppressWarnings("unused")
		TextAnnotation proteinAbcTA = new DefaultTextAnnotation(40, 45, "ABC-1", new Annotator(1, "Bob",
				"The Annotator", "The U"), new AnnotationSet(), 1, -1, "1234", -1, proteinAbcCM);

		nucleusCM = new DefaultClassMention(FRAME_NUCLEUS);
		@SuppressWarnings("unused")
		TextAnnotation nucleusTA = new DefaultTextAnnotation(63, 70, "nucleus", new Annotator(1, "Bob",
				"The Annotator", "The U"), new AnnotationSet(), 1, -1, "1234", -1, nucleusCM);

		transportCM = new DefaultClassMention(FRAME_PROTEIN_TRANSPORT);
		transportCargoCSM = new DefaultComplexSlotMention(SLOT_CARGO);
		transportCargoCSM.addClassMention(proteinAbcCM);
		transportCM.addComplexSlotMention(transportCargoCSM);
		DefaultComplexSlotMention transportDestinationCSM = new DefaultComplexSlotMention(SLOT_DESTINATION);
		transportDestinationCSM.addClassMention(nucleusCM);
		transportCM.addComplexSlotMention(transportDestinationCSM);
		@SuppressWarnings("unused")
		TextAnnotation transportTA = new DefaultTextAnnotation(46, 55, "transport", new Annotator(1, "Bob",
				"The Annotator", "The U"), new AnnotationSet(), 1, -1, "1234", -1, transportCM);

		regulationCM = new DefaultClassMention(FRAME_REGULATION_OF_PROCESS);
		DefaultComplexSlotMention regulatedProcessCSM = new DefaultComplexSlotMention(SLOT_REGULATED_PROCESS);
		regulatedProcessCSM.addClassMention(transportCM);
		regulationCM.addComplexSlotMention(regulatedProcessCSM);
		@SuppressWarnings("unused")
		TextAnnotation regultionTA = new DefaultTextAnnotation(26, 36, "regulation", new Annotator(1, "Bob",
				"The Annotator", "The U"), new AnnotationSet(), 1, -1, "1234", -1, regulationCM);

		playsARoleInProcessCM = new DefaultClassMention(FRAME_PLAYS_A_ROLE_IN_PROCESS);
		playerCSM = new DefaultComplexSlotMention(SLOT_PLAYER);
		playerCSM.addClassMention(proteinXyzCM);
		playsARoleInProcessCM.addComplexSlotMention(playerCSM);
		DefaultComplexSlotMention processCSM = new DefaultComplexSlotMention(SLOT_PROCESS);
		processCSM.addClassMention(regulationCM);
		playsARoleInProcessCM.addComplexSlotMention(processCSM);
		@SuppressWarnings("unused")
		TextAnnotation playsARoleInProcessTA = new DefaultTextAnnotation(6, 21, "plays a role in", new Annotator(1,
				"Bob", "The Annotator", "The U"), new AnnotationSet(), 1, -1, "1234", -1, playsARoleInProcessCM);

	}

	private void checkForNucleus(Iterator<TreeNode<Mention>> mentionNodeIter) {
		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(FRAME_NUCLEUS, expectedClassMention.getMentionName());
	}

	@Test
	public void testGetMentionIteratorForNucleus() throws Exception {
		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(nucleusCM);
		checkForNucleus(mentionNodeIter);
		assertFalse(mentionNodeIter.hasNext());
	}

	private void checkForProtein(Iterator<TreeNode<Mention>> mentionNodeIter, String geneID) {
		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(FRAME_PROTEIN, expectedClassMention.getMentionName());

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_GENE_ID, expectedClassMention.getMentionName());
		if (expectedClassMention instanceof StringSlotMention) {
			StringSlotMention sm = (StringSlotMention) expectedClassMention;
			Collection<String> slotValues = sm.getSlotValues();
			assertEquals(1, slotValues.size());
			assertEquals(geneID, Collections.list(Collections.enumeration(slotValues)).get(0).toString());
		} else {
			fail("Expected StringSlotMention but was " + expectedClassMention.getClass().getName());
		}
	}

	@Test
	public void testGetmentionNodeIteratorForProteinXyz() throws Exception {
		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(proteinXyzCM);
		checkForProtein(mentionNodeIter, "xyzID=12345");
		assertFalse(mentionNodeIter.hasNext());
	}

	private void checkForTransportFrame(Iterator<TreeNode<Mention>> mentionNodeIter) {
		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(FRAME_PROTEIN_TRANSPORT, expectedClassMention.getMentionName());

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_CARGO, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "AbcID=98765");

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_DESTINATION, expectedClassMention.getMentionName());

		checkForNucleus(mentionNodeIter);
	}

	@Test
	public void testGetmentionNodeIteratorForTransportFrame() throws Exception {
		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(transportCM);
		checkForTransportFrame(mentionNodeIter);
		assertFalse(mentionNodeIter.hasNext());
	}

	private void checkForPlaysARoleFrame(Iterator<TreeNode<Mention>> mentionNodeIter) {
		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(FRAME_PLAYS_A_ROLE_IN_PROCESS, expectedClassMention.getMentionName());

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_PLAYER, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "xyzID=12345");

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_PROCESS, expectedClassMention.getMentionName());

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(FRAME_REGULATION_OF_PROCESS, expectedClassMention.getMentionName());

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_REGULATED_PROCESS, expectedClassMention.getMentionName());

		checkForTransportFrame(mentionNodeIter);
	}

	@Test
	public void testGetmentionNodeIteratorForPlaysARoleFrame() throws Exception {
		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(playsARoleInProcessCM);
		checkForPlaysARoleFrame(mentionNodeIter);
		assertFalse(mentionNodeIter.hasNext());
	}

	@Test
	public void testComplexSlotWithMultipleClassMentions() throws Exception {
		transportCargoCSM.addClassMention(proteinXyzCM);

		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(transportCargoCSM);

		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_CARGO, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "AbcID=98765");
		checkForProtein(mentionNodeIter, "xyzID=12345");
		assertFalse(mentionNodeIter.hasNext());

		playerCSM.addClassMention(proteinAbcCM);
		mentionNodeIter = Mention.getMentionTreeNodeIterator(playerCSM);

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_PLAYER, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "AbcID=98765");
		checkForProtein(mentionNodeIter, "xyzID=12345");
		assertFalse(mentionNodeIter.hasNext());
	}

	private void checkForCargoSlotWithProteinEquivalenceCycle(Iterator<TreeNode<Mention>> mentionNodeIter) {
		assertTrue(mentionNodeIter.hasNext());
		Mention expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_CARGO, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "AbcID=98765");

		assertTrue(mentionNodeIter.hasNext());
		expectedClassMention = mentionNodeIter.next().getNodeValue();
		assertEquals(SLOT_IS_EQUIVALENT_TO, expectedClassMention.getMentionName());

		checkForProtein(mentionNodeIter, "xyzID=12345");
	}

	private void createProteinCycle() {
		DefaultComplexSlotMention equivSlot = new DefaultComplexSlotMention(SLOT_IS_EQUIVALENT_TO);
		equivSlot.addClassMention(proteinAbcCM);
		proteinXyzCM.addComplexSlotMention(equivSlot);

		equivSlot = new DefaultComplexSlotMention(SLOT_IS_EQUIVALENT_TO);
		equivSlot.addClassMention(proteinXyzCM);
		proteinAbcCM.addComplexSlotMention(equivSlot);
	}

	@Test(timeout = 100)
	public void testmentionNodeIteratorWithCycle() throws Exception {
		createProteinCycle();

		Iterator<TreeNode<Mention>> mentionNodeIter = Mention.getMentionTreeNodeIterator(transportCargoCSM);
		checkForCargoSlotWithProteinEquivalenceCycle(mentionNodeIter);

		if (mentionNodeIter.hasNext()) {
			Mention m = mentionNodeIter.next().getNodeValue();
			System.err.println("REMAINING MENTION: " + m.getMentionName());
			fail();
		}
		assertFalse(mentionNodeIter.hasNext());
	}

	@Test
	public void testToStringNucleus() throws Exception {
		String nucleusStr = nucleusCM.toString();
		String expectedStr = "-CLASS MENTION: nucleus \"nucleus\"\t[63..70]\n";
		assertEquals(expectedStr, nucleusStr);
	}

	@Test
	public void testToStringProteinAbc() throws Exception {
		String proteinStr = proteinAbcCM.toString();
		String expectedStr = "-CLASS MENTION: protein \"ABC-1\"\t[40..45]\n" + "-    SLOT MENTION: " + SLOT_GENE_ID
				+ " with SLOT VALUE(s): AbcID=98765\n";
		assertEquals(expectedStr, proteinStr);
	}

	@Test
	public void testToStringWithProteinCycle() throws Exception {
		createProteinCycle();
		String proteinStr = proteinAbcCM.toString();
		String expectedStr = "-CLASS MENTION: protein \"ABC-1\"\t[40..45]\n" + "-    SLOT MENTION: " + SLOT_GENE_ID
				+ " with SLOT VALUE(s): AbcID=98765\n" + "-    COMPLEX SLOT MENTION: " + SLOT_IS_EQUIVALENT_TO + "\n"
				+ "-        CLASS MENTION: protein \"XYZ-2\"\t[0..5]\n" + "-            SLOT MENTION: " + SLOT_GENE_ID
				+ " with SLOT VALUE(s): xyzID=12345\n";

		assertEquals(expectedStr, proteinStr);
	}

}
