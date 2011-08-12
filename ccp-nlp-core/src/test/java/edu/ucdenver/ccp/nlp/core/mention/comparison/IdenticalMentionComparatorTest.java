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

package edu.ucdenver.ccp.nlp.core.mention.comparison;

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

public class IdenticalMentionComparatorTest {

	Map<Integer, DefaultTextAnnotation> id2annotationMap;

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

		ClassMention nucleusMention = id2annotationMap.get(-11).getClassMention();
		/* the nucleus mention should equal itself on all levels */
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, 0));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, -1));
		assertEquals(0, icmc.compare(nucleusMention, nucleusMention, spanComparator, 5));

		ClassMention proteinMention = id2annotationMap.get(-12).getClassMention();
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, 0)==0);
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, -1)==0);
		assertFalse(icmc.compare(nucleusMention, proteinMention, spanComparator, 5)==0);

		/*
		 * Compare two nucleus mentions, whose annotations overlap. Using the strict span comparator, the mentions
		 * should match when the linked annotations are not compared, and should fail to match when the annotations are
		 * compared.
		 */
		ClassMention nucleusMentionWithOverlappingSpan = TestTextAnnotationCreatorTest.getAnnotationToMatch11WithOverlappingSpan()
				.getClassMention();
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0)==0);
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1)==0);
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5)==0);
		/* using the sloppy span comparator, the mentions should now match because the annotations overlap */
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
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 0)==0);
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, -1)==0);
		assertFalse( icmc.compare(nucleusMention, nucleusMentionWithOverlappingSpan, spanComparator, 5)==0);

	}

	/**
	 * Check that the compare method works properly given different max comparison levels as input
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareAnnotationsThatDifferOnLevels() throws Exception {

		ClassMention regOfActOfTransportMention = id2annotationMap.get(-18).getClassMention();
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

		ClassMention almostMatchButDifferentTransEntitiesMention = TestTextAnnotationCreatorTest.getAnnotationToMatch18ThruLevel3()
				.getClassMention();
		/* test that the mentions match up until a point, after level 3 they should not match */
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 5));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 6));
		assertFalse( icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, 7)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, almostMatchButDifferentTransEntitiesMention, spanComparator, -1)==0);

		ClassMention level1MatchOnly = TestTextAnnotationCreatorTest.getAnnotationToMatch18ThruLevel1Only().getClassMention();
		/* test that the mentions match up until a point, after level 0 they should not match */
		assertEquals(0, icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 1));
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 2)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 3)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 4)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 5)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 6)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, 7)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, level1MatchOnly, spanComparator, -1)==0);

	}

	/**
	 * Test that the different span comparators result in the expected results
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareAnnotationsThatDifferInSpans() throws Exception {
		ClassMention regOfActOfTransportMention = id2annotationMap.get(-18).getClassMention();
		SpanComparator spanComparator = new StrictSpanComparator();

		ClassMention exactMentionMatchButOverlappingSpans = TestTextAnnotationCreatorTest
				.getAnnotationToMatch18ExactlyButHasOverlappingSpans().getClassMention();
		/* using the strict span comparator, these mentions should match through level 1. */
		System.err.println(exactMentionMatchButOverlappingSpans.toString());
		System.err.println(regOfActOfTransportMention.toString());
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1)==0);

		/* using the right boundary span comparator, these mentions should match through level 3. */
		spanComparator = new SharedEndSpanComparator();
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1)==0);

		/* using the sloppy span comparator, these mentions should match completely. */
		spanComparator = new SloppySpanComparator();
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1));

		/* using the left span comparator, these mentions should match through level 2. */
		spanComparator = new SharedStartSpanComparator();
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7)==0);
		assertFalse( icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1)==0);

		/* using the either boundary span comparator, these mentions should match completely. */
		spanComparator = new SharedStartOrEndSpanComparator();
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 0));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 1));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 2));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 3));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 4));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 5));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 6));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, 7));
		assertEquals(0, icmc.compare(regOfActOfTransportMention, exactMentionMatchButOverlappingSpans, spanComparator, -1));

	}

}
