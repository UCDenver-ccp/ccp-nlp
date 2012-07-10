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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TestTextAnnotationCreatorTest;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class ComplexSlotMentionTest {

	private ComplexSlotMention complexSlotMention;

	private ComplexSlotMention complexSlotMention2;

	/**
	 * To test the ComplexSlotMention class, we will make use of some test annotations, in particular this one: <br>
	 * 
	 * <pre>
	 *     ======================= Annotation: -10 =======================
	 *      Annotator: -30|Test Annotator|#1|CCP
	 *      --- AnnotationSets: -20|Test Set #1|This is a test annnotation set.
	 *      -21|Test Set #2|This is another test annotation set.
	 *      --- Span: 45 - 49  53 - 61
	 *      --- DocCollection: -1  DocID: 1234  DocumentSection: 0
	 *      --- Covered Text: gated..transport
	 *      -CLASS MENTION: gated nuclear transport &quot;gated..transport&quot;
	 *      -    COMPLEX SLOT MENTION: transport origin
	 *      -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *      -    COMPLEX SLOT MENTION: transport location
	 *      -        CLASS MENTION: nucleus &quot;nucl&quot;
	 *      -    COMPLEX SLOT MENTION: transport participants
	 *      -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *      -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999
	 *      -    COMPLEX SLOT MENTION: transported entities
	 *      -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *      -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999
	 * </pre>
	 */
	@Before
	public void setUp() throws Exception {
		Map<Integer, DefaultTextAnnotation> id2annotationMap = TestTextAnnotationCreatorTest.getID2TextAnnotationMap();

		TextAnnotation annotation_10 = id2annotationMap.get(-10);
		complexSlotMention = annotation_10.getClassMention().getComplexSlotMentionByName("transport participants");
		complexSlotMention2 = annotation_10.getClassMention().getComplexSlotMentionByName("transport location");
	}

	/**
	 * Test that the constructor sets the mention name and initializes storage for the class mentions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructor() throws Exception {
		String mentionName = "mentionName";
		DefaultComplexSlotMention csm = new DefaultComplexSlotMention(mentionName);
		/* check that the mention name was set */
		assertEquals(mentionName, csm.getMentionName());
		/* check that the slot value container was initialized */
		assertNotNull(csm.getClassMentions());
	}

	/**
	 * Test the adding of a class mention
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddClassMention() throws Exception {
		int numClassMentions = complexSlotMention.getClassMentions().size();
		DefaultClassMention cm = new DefaultClassMention("another protein");
		complexSlotMention.addClassMention(cm);
		/* check that there is now one more slot value */
		assertEquals(numClassMentions + 1, complexSlotMention.getClassMentions().size());
	}

	/**
	 * Test the single-line representation of a complex slot mention
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleLineRepresentation() throws Exception {
		String expectedRepresentation = "-COMPLEX SLOT MENTION: transport participants - CLASS MENTION: protein \"E2F-4\" [65..70] - SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 9999999";
		/* check that the single line representation is what is expected */
		assertEquals(expectedRepresentation, complexSlotMention.getSingleLineRepresentation().trim());
	}

	/**
	 * Test the equals method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEquals() throws Exception {
		/* the transport location and transport participants complex slot mentions should not return true */
		assertFalse(complexSlotMention2.equals(complexSlotMention));
		assertFalse(complexSlotMention.equals(complexSlotMention2));
		/* the transport location and transport participants complex slot mentions should equal themselves */
		assertTrue(complexSlotMention2.equals(complexSlotMention2));
		assertTrue(complexSlotMention.equals(complexSlotMention));

		/* create a "nucleus" annotation to fill the transport origin slot */
		/* first create the nucleus class mention */
		DefaultClassMention nucleusMention = new DefaultClassMention("nucleus");

		/* create the nucleus annotation */
		Annotator annotator1 = new Annotator(new Integer(-30), "Test Annotator", "#1", "CCP");
		AnnotationSet annotationSet1 = new AnnotationSet(new Integer(-20), "Test Set #1", "This is a test annnotation set.");

		@SuppressWarnings("unused")
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(50, 53, "nucl", annotator1, annotationSet1, -11, -1, "1234", 0,
				nucleusMention);

		DefaultComplexSlotMention csm = new DefaultComplexSlotMention("transport location");
		/* test that equals() returns false for csm's with identical mention names but unequal number of class mentions */
		assertFalse(csm.equals(complexSlotMention2));
		csm.addClassMention(nucleusMention);
		/* test that equals() returns true for identical csm's */
		assertTrue(csm.equals(complexSlotMention2));
		assertTrue(complexSlotMention2.equals(csm));
	}

}
