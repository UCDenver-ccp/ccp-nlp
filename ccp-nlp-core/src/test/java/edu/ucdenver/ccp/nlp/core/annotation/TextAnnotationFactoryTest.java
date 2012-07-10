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
package edu.ucdenver.ccp.nlp.core.annotation;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class TextAnnotationFactoryTest {
	@BeforeClass
	public static void before() {
		BasicConfigurator.configure();
	}
	@Test
	public void tesStringSlot() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		PrimitiveSlotMention sm = taf.parseSlotMention("-    SLOT MENTION: localConceptID with SLOT VALUE(s): 39917/GO:0005623");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 1);
		assertTrue( ((String) CollectionsUtil.getSingleElement(list)).equals("39917/GO:0005623"));
	}
	
	@Test
	public void testIntSlot() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		PrimitiveSlotMention sm = taf.parseSlotMention("-    SLOT MENTION: localOntologyID with SLOT VALUE(s): 39917");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 1);
		Integer val = (Integer) CollectionsUtil.getSingleElement(list);
		assertTrue(val == 39917);
	}
	
	@Test
	public void testDoubleSlot() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		PrimitiveSlotMention sm = taf.parseSlotMention("-    SLOT MENTION: localOntologyID with SLOT VALUE(s): 3.1415");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 1);
		Float val = (Float) CollectionsUtil.getSingleElement(list);
		assertEquals(val,3.1415,0.000001);
	}
	
	@Test
	public void testDoubleSlot2() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		SlotMention sm = taf.parseSlotMention("-    SLOT MENTION: localOntologyID with SLOT VALUE(s): 3.1415E-10");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 1);
		Float val = (Float) CollectionsUtil.getSingleElement(list);
		assertEquals(val, 3.1415E-10,0.0000000000000001); // I know, don't compare real values for exact equality...
	}
	
	@Test
	public void testListSlot() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		SlotMention sm = taf.parseSlotMention("-	SLOT MENTION: synonyms with SLOT VALUE(s): foo,bar");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 2);
		assertTrue(list.contains("foo"));
		assertTrue(list.contains("bar"));
	}
	// List must not match plain strings, there must be a comma in there, but...
	// This only works because a one-item list has no commas and the result
	// is the same for just a single value. It doesn't matter if it gets
	// handled by the fall-through or the list code.
	@Test
	public void testListSlot2() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		PrimitiveSlotMention sm = taf.parseSlotMention("-	SLOT MENTION: synonyms with SLOT VALUE(s): foobar");
		Collection list = sm.getSlotValues();
		assertTrue(list.size() == 1);
		assertTrue( ((String) CollectionsUtil.getSingleElement(list)).equals("foobar"));
	}
	
	
	static String annotation1 = "======================= Annotation: 1 =======================\n" 
		+ "Annotator: 1|Factory|Test|CCP\n"
		+ "--- AnnotationSets: 1|set|test set\n"
		+ "--- Span: 1 - 4 \n" 
		+ "--- DocCollection: 2  DocID:   DocumentSection: 3\n"
		+ "--- Covered Text: cell\n"
		+ "-CLASS MENTION: 39917/GO:0005623 \"cell\"	[1..4]\n"
		+ "-	SLOT MENTION: localConceptID with SLOT VALUE(s): 39917/GO:0005623\n"
		+ "-	SLOT MENTION: localOntologyID with SLOT VALUE(s): 39917\n"
		+ "-	SLOT MENTION: preferredName with SLOT VALUE(s): cell\n"
		+ "-	SLOT MENTION: synonyms with SLOT VALUE(s): foo,bar\n"
		+ "-	SLOT MENTION: semanticTypes with SLOT VALUE(s): T999\n"
		+ "-	SLOT MENTION: termName with SLOT VALUE(s): cell\n" 
		+ "-	SLOT MENTION: score with SLOT VALUE(s): 10\n"
		+ "================================================================================="	;
	@Test
	public void testOBAMention() {
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		TextAnnotation ta = taf.createFromString(annotation1);
		// visually inspect the following 2 outputs:
		System.out.println(ta.toString());
		System.out.println(annotation1);
	}
	static String bogusString1 = "======================= Annotation: 1 =======================\n" 
		+ "Annotator: 1|Factory|Test|CCP\n"
		+ "--- AnnotationSets: 1|set|test set\n"
		+ "--- Span: 1 - 4 \n" 
		+ "--- DocCollection: 2  DocID:   DocumentSection: 3\n"
		+ "--- Covered Text: cell\n"
		+ "-CLASS MENTION: 39917/GO:0005623 \"cell\"	[1..4]\n"
		+ "-	SLOT MENTION: synonyms with SLOT VALUE(s):foo,bar\n"
		+ "================================================================================="	;
	@Test 
	public void testBogusString1() {
		// it doesn't have a space between the colon and "foo,bar"
		// arguable about what's right here. The toString() function puts a space there,
		//but it's quite easy to be flexible in that regard.
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		TextAnnotation ta = taf.createFromString(bogusString1);
		assertEquals(ta.getClassMention().getPrimitiveSlotMentions().size(), 1);
	}
	static String emptySlot1 = "======================= Annotation: 1 =======================\n" 
		+ "Annotator: 1|Factory|Test|CCP\n"
		+ "--- AnnotationSets: 1|set|test set\n"
		+ "--- Span: 1 - 4 \n" 
		+ "--- DocCollection: 2  DocID:   DocumentSection: 3\n"
		+ "--- Covered Text: cell\n"
		+ "-CLASS MENTION: 39917/GO:0005623 \"cell\"	[1..4]\n"
		+ "-	SLOT MENTION: synonyms with SLOT VALUE(s): \n"
		+ "================================================================================="	;
	@Test 
	public void testemptySlot1() {
		// it doesn't have a space between the colon and "foo,bar"
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		TextAnnotation ta = taf.createFromString(emptySlot1);
		assertEquals(ta.getClassMention().getPrimitiveSlotMentions().size(), 0);
	}
	static String emptySlot2 = "======================= Annotation: 1 =======================\n" 
		+ "Annotator: 1|Factory|Test|CCP\n"
		+ "--- AnnotationSets: 1|set|test set\n"
		+ "--- Span: 1 - 4 \n" 
		+ "--- DocCollection: 2  DocID:   DocumentSection: 3\n"
		+ "--- Covered Text: cell\n"
		+ "-CLASS MENTION: 39917/GO:0005623 \"cell\"	[1..4]\n"
		+ "-	SLOT MENTION: synonyms with SLOT VALUE(s):\n"
		+ "================================================================================="	;
	@Test 
	public void testemptySlot2() {
		// it doesn't have a space between the colon and "foo,bar"
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		TextAnnotation ta = taf.createFromString(emptySlot2);
		assertEquals(ta.getClassMention().getPrimitiveSlotMentions().size(), 0);
		// visually inspect the following 2 outputs:
	}
	
	static String annotation2 = "======================= Annotation: 1 =======================\n" 
		+ "Annotator: 1|Factory|Test|CCP\n"
		+ "--- AnnotationSets: 1|set|test set\n"
		+ "--- Span: 1 - 4 \n" 
		+ "--- DocCollection: 2  DocID:   DocumentSection: 3\n"
		+ "--- Covered Text: cell\n"
		+ "-CLASS MENTION: 39917/GO:0005623 \"cell\"	[1..4]\n"
		+ "-	COMPLEX SLOT MENTION: <not sure what goes here but it refers to annotation1>\n"

		+ "================================================================================="	;
	
	@Ignore
	@Test 
	public void testLinkedMentions() {
		assertTrue(false);
		TextAnnotationFactory taf = TextAnnotationFactory.createFactoryWithDefaults();
		TextAnnotation referencedTA = taf.createFromString(annotation1);
		TextAnnotation referringTA = taf.createFromString(annotation2);
		// ideally...
		ClassMention referringCM = referringTA.getClassMention();
		ClassMention referencedCM = CollectionsUtil.getSingleElement(CollectionsUtil.getSingleElement(referringCM.getComplexSlotMentions()).getClassMentions());
		ClassMention referencedCM2 = referencedTA.getClassMention();
		assertEquals(referencedCM, referencedCM2);
		assertTrue(referencedCM.equals(referencedCM2));
		
		// make sure its the same object, not just equivalent objects
		assertTrue(referencedCM == referencedCM2);
	}
	
	@Test
	public void EquivalenceVSIdentity() {
		Integer one = new Integer(1);
		Integer two = new Integer(1);
		Integer reallyTwo = new Integer(2);
//		assertTrue(one == one); commented out to avoid a high priority findbugs warning
		assertFalse(one == two);
		assertTrue( (int)one == (int) two);
		assertFalse(one == reallyTwo);
		assertFalse((int)one == (int) reallyTwo);
		assertTrue(one.equals(two));
		assertFalse(one.equals(reallyTwo));
	}

}
