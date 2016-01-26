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

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClassMentionCycleTest {

	/**
	 * <pre>
	 * proteinA
	 *         is-equivalent-to
	 *             proteinB
	 * </pre>
	 * 
	 * @throws Exception
	 */

	@Test(timeout = 10000)
	public void testToStringWhenMentionStructureIncludesCycle() throws Exception {

		/* ____________012345678901234556789 */
		@SuppressWarnings("unused")
		String text = "ABC1 and its homologues are known to play a role in cancer.";

		DefaultClassMention proteinACM = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		@SuppressWarnings("unused")
		TextAnnotation proteinATA = new DefaultTextAnnotation(0, 4, "ABC1", new Annotator(1, "Bob", "The Annotator",
				"The U"), new AnnotationSet(), 1, -1, "1234", -1, proteinACM);

		DefaultClassMention proteinBCM = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		@SuppressWarnings("unused")
		TextAnnotation proteinBTA = new DefaultTextAnnotation(9, 12, "its", new Annotator(1, "Bob", "The Annotator",
				"The U"), new AnnotationSet(), 2, -1, "1234", -1, proteinBCM);

		DefaultComplexSlotMention csmA = new DefaultComplexSlotMention("is-equivalent-to");
		proteinACM.addComplexSlotMention(csmA);
		csmA.addClassMention(proteinBCM);

		DefaultComplexSlotMention csmB = new DefaultComplexSlotMention("is-equivalent-to");
		proteinBCM.addComplexSlotMention(csmB);
		csmB.addClassMention(proteinACM);

		String expectedOutputStr = "-CLASS MENTION: protein \"ABC1\"\t[0..4]\n-    COMPLEX SLOT MENTION: is-equivalent-to\n-        CLASS MENTION: protein \"its\"\t[9..12]\n";
		assertEquals(expectedOutputStr, proteinACM.toString());
		expectedOutputStr = "-CLASS MENTION: protein \"its\"\t[9..12]\n-    COMPLEX SLOT MENTION: is-equivalent-to\n-        CLASS MENTION: protein \"ABC1\"\t[0..4]\n";
		assertEquals(expectedOutputStr, proteinBCM.toString());
	}

}
