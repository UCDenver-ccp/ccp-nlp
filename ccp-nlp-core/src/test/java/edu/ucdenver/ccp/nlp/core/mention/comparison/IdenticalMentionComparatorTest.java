package edu.ucdenver.ccp.nlp.core.mention.comparison;

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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.TestTextAnnotationCreatorTest;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedEndSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedStartOrEndSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SharedStartSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SloppySpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class IdenticalMentionComparatorTest {

	Map<String, DefaultTextAnnotation> id2annotationMap;

	IdenticalMentionComparator icmc;

	@Before
	public void setUp() throws Exception {
		icmc = new IdenticalMentionComparator();
		id2annotationMap = TestTextAnnotationCreatorTest.getID2TextAnnotationMap();

	}

	/**
	 * Test the compare(ClassMention, ClassMention,...) method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpecificCompare() throws Exception {
		SpanComparator spanComparator = new StrictSpanComparator();

		ClassMention nucleusMention = id2annotationMap.get("-11").getClassMention();
		/* the nucleus mention should equal itself on all levels */
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, 0));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, -1));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, 5));

		ClassMention proteinMention = id2annotationMap.get("-12").getClassMention();
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, 0) == 0);
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, -1) == 0);
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, 5) == 0);

		/*
		 * Compare two nucleus mentions, whose annotations overlap. Using the strict span
		 * comparator, the mentions should match when the linked annotations are not compared, and
		 * should fail to match when the annotations are compared.
		 */
		ClassMention nucleusMentionWithOverlappingSpan = TestTextAnnotationCreatorTest
				.getAnnotationToMatch11WithOverlappingSpan().getClassMention();
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0) == 0);
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1) == 0);
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5) == 0);
		/*
		 * using the sloppy span comparator, the mentions should now match because the annotations
		 * overlap
		 */
		spanComparator = new SloppySpanComparator();
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5));
		/* they should also match using the right boundary span comparator */
		spanComparator = new SharedEndSpanComparator();
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5));
		/* and they should also match when using the either boundary span comparator */
		spanComparator = new SharedStartOrEndSpanComparator();
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5));
		/* but they should not match when using the left boundary comparator */
		spanComparator = new SharedStartSpanComparator();
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0) == 0);
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1) == 0);
		assertFalse(icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5) == 0);

	}

	/**
	 * Check that the compare method works properly given different max comparison levels as input
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareAnnotationsThatDifferOnLevels() throws Exception {

		ClassMention regOfActOfTransportMention = id2annotationMap.get("-18").getClassMention();
		SpanComparator spanComparator = new StrictSpanComparator();

		ClassMention exactMatch = TestTextAnnotationCreatorTest.getAnnotationToMatch18Exactly().getClassMention();
		/* these mentions should match at all levels */
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 5));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 6));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, 7));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMatch, spanComparator, -1));

		ClassMention almostMatchButDifferentTransEntitiesMention = TestTextAnnotationCreatorTest
				.getAnnotationToMatch18ThruLevel3().getClassMention();
		/* test that the mentions match up until a point, after level 3 they should not match */
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 5));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 6));
		assertFalse(icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, 7) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention,
				spanComparator, -1) == 0);

		ClassMention level1MatchOnly = TestTextAnnotationCreatorTest.getAnnotationToMatch18ThruLevel1Only()
				.getClassMention();
		/* test that the mentions match up until a point, after level 0 they should not match */
		assertEquals(0, icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 1));
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 2) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 3) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 4) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 5) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 6) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 7) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, -1) == 0);

	}

	/**
	 * Test that the different span comparators result in the expected results
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareAnnotationsThatDifferInSpans() throws Exception {
		ClassMention regOfActOfTransportMention = id2annotationMap.get("-18").getClassMention();
		SpanComparator spanComparator = new StrictSpanComparator();

		ClassMention exactMentionMatchButOverlappingSpans = TestTextAnnotationCreatorTest
				.getAnnotationToMatch18ExactlyButHasOverlappingSpans().getClassMention();
		/* using the strict span comparator, these mentions should match through level 1. */
		System.err.println(exactMentionMatchButOverlappingSpans.toString());
		System.err.println(regOfActOfTransportMention.toString());
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1) == 0);

		/* using the right boundary span comparator, these mentions should match through level 3. */
		spanComparator = new SharedEndSpanComparator();
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1) == 0);

		/* using the sloppy span comparator, these mentions should match completely. */
		spanComparator = new SloppySpanComparator();
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1));

		/* using the left span comparator, these mentions should match through level 2. */
		spanComparator = new SharedStartSpanComparator();
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7) == 0);
		assertFalse(icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1) == 0);

		/* using the either boundary span comparator, these mentions should match completely. */
		spanComparator = new SharedStartOrEndSpanComparator();
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7));
		assertEquals(0,
				icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1));

	}

}
