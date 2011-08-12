package edu.ucdenver.ccp.nlp.core.mention;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;

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

		DefaultClassMention proteinACM = new DefaultClassMention(ClassMentionTypes.PROTEIN);
		@SuppressWarnings("unused")
		TextAnnotation proteinATA = new DefaultTextAnnotation(0, 4, "ABC1", new Annotator(1, "Bob", "The Annotator", "The U"),
				new AnnotationSet(), 1, -1, "1234", -1, proteinACM);

		DefaultClassMention proteinBCM = new DefaultClassMention(ClassMentionTypes.PROTEIN);
		@SuppressWarnings("unused")
		TextAnnotation proteinBTA = new DefaultTextAnnotation(9, 12, "its", new Annotator(1, "Bob", "The Annotator", "The U"),
				new AnnotationSet(), 2, -1, "1234", -1, proteinBCM);

		DefaultComplexSlotMention csmA = new DefaultComplexSlotMention("is-equivalent-to");
		proteinACM.addComplexSlotMention(csmA);
		csmA.addClassMention(proteinBCM);

		DefaultComplexSlotMention csmB = new DefaultComplexSlotMention("is-equivalent-to");
		proteinBCM.addComplexSlotMention(csmB);
		csmB.addClassMention(proteinACM);

		// ClassMention.printMention(proteinACM, System.out, 0, new HashMap<Long, ClassMention>());

		System.out.println(proteinACM.toString());

		String expectedOutputStr = "-CLASS MENTION: protein \"ABC1\"\t[0..4]\n-    COMPLEX SLOT MENTION: is-equivalent-to\n-        CLASS MENTION: protein \"its\"\t[9..12]\n";
		assertEquals(expectedOutputStr, proteinACM.toString());
		expectedOutputStr = "-CLASS MENTION: protein \"its\"\t[9..12]\n-    COMPLEX SLOT MENTION: is-equivalent-to\n-        CLASS MENTION: protein \"ABC1\"\t[0..4]\n";
		assertEquals(expectedOutputStr, proteinBCM.toString());
	}

}