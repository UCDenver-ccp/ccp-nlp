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

package edu.ucdenver.ccp.nlp.core.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.AnnotationComparator;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation_Util;
import edu.ucdenver.ccp.nlp.core.annotation.serialization.AnnotationToFileOutput;
import edu.ucdenver.ccp.nlp.core.annotation.serialization.AnnotationToFileOutputTest;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.InvalidInputException;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

public class Annotation_UtilTest extends DefaultTestCase {
	public static final String NO_SLOTS_DOCUMENT_ID = "documentID=4";
	public static final String WITH_SLOTS_DOCUMENT_ID = "documentID=5";

	private File TEST_ANNOTATION_STORAGE_FILE;

	@Before
	public void setUp() throws IOException {
		TEST_ANNOTATION_STORAGE_FILE = folder.newFile("test.ascii");
	}

	@Test
	public void testCreateTokenMention() throws Exception {
		/*
		 * Expected Token: POS = NN, TAGSET = PENN, STEM = wa, LEMMA = is, TOKEN_NUMBER = 5
		 */
		String posLabel = "NN";
		String tagSet = "PENN";
		String stem = "wa";
		String lemma = "is";
		int tokenNumber = 5;

		DefaultClassMention expectedTokenMention = createTokenMention(posLabel, tagSet, stem, lemma, tokenNumber);
		DefaultClassMention testTokenMention = DefaultTextAnnotation_Util.createTokenMention(posLabel, tagSet, stem,
				lemma, tokenNumber);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedTokenMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testTokenMention);

		System.err.println("Expected\n" + expectedAnnotation.toString());
		System.err.println("Test\n" + testAnnotation.toString());
		assertTrue(expectedTokenMention.equals(testTokenMention));

		DefaultClassMention expectedTokenMentionWithUnknownTagSet = createTokenMention(posLabel,
				DefaultTextAnnotation_Util.UNKNOWN_TAGSET, stem, lemma, tokenNumber);
		DefaultClassMention testTokenMentionWithNullTagSet = DefaultTextAnnotation_Util.createTokenMention(posLabel,
				null, stem, lemma, tokenNumber);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotationWithNullTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				expectedTokenMentionWithUnknownTagSet);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotationWithNullTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				testTokenMentionWithNullTagSet);

		assertTrue(expectedTokenMentionWithUnknownTagSet.equals(testTokenMentionWithNullTagSet));
	}

	private DefaultClassMention createTokenMention(String posLabel, String tagSet, String stem, String lemma,
			int tokenNumber) throws InvalidInputException {
		DefaultClassMention expectedTokenMention = new DefaultClassMention(ClassMentionType.TOKEN.typeName());

		/* Set expected part of speech */
		StringSlotMention posSlot = new DefaultStringSlotMention(SlotMentionType.TOKEN_PARTOFSPEECH.typeName());
		posSlot.addSlotValue(posLabel);
		expectedTokenMention.addPrimitiveSlotMention(posSlot);
		StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
		tagSetSlot.addSlotValue(tagSet);
		expectedTokenMention.addPrimitiveSlotMention(tagSetSlot);

		/* Set expected stem */
		StringSlotMention stemSlot = new DefaultStringSlotMention(SlotMentionType.TOKEN_STEM.typeName());
		stemSlot.addSlotValue(stem);
		expectedTokenMention.addPrimitiveSlotMention(stemSlot);

		/* Set expected lemma */
		StringSlotMention lemmaSlot = new DefaultStringSlotMention(SlotMentionType.TOKEN_LEMMA.typeName());
		lemmaSlot.addSlotValue(lemma);
		expectedTokenMention.addPrimitiveSlotMention(lemmaSlot);

		/* Set expected tokenNumber */
		IntegerSlotMention tokenNumberSlot = new DefaultIntegerSlotMention(SlotMentionType.TOKEN_NUMBER.typeName());
		tokenNumberSlot.addSlotValue(tokenNumber);
		expectedTokenMention.addPrimitiveSlotMention(tokenNumberSlot);

		return expectedTokenMention;
	}

	@Test
	public void testCreateTokenMention_PartialNullInput() throws Exception {
		/*
		 * Expected Token: POS = NULL, TAGSET = NULL, STEM = wa, LEMMA = NULL, TOKEN_NUMBER = 5
		 */
		String posLabel = null;
		String tagSet = null;
		String stem = "wa";
		String lemma = null;
		int tokenNumber = 5;

		DefaultClassMention expectedTokenMention = new DefaultClassMention(ClassMentionType.TOKEN.typeName());

		/* Set expected stem */
		StringSlotMention stemSlot = new DefaultStringSlotMention(SlotMentionType.TOKEN_STEM.typeName());
		stemSlot.addSlotValue(stem);
		expectedTokenMention.addPrimitiveSlotMention(stemSlot);

		/* Set expected tokenNumber */
		IntegerSlotMention tokenNumberSlot = new DefaultIntegerSlotMention(SlotMentionType.TOKEN_NUMBER.typeName());
		tokenNumberSlot.addSlotValue(tokenNumber);
		expectedTokenMention.addPrimitiveSlotMention(tokenNumberSlot);

		DefaultClassMention testTokenMention = DefaultTextAnnotation_Util.createTokenMention(posLabel, tagSet, stem,
				lemma, tokenNumber);

		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedTokenMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testTokenMention);

		assertTrue(expectedTokenMention.equals(testTokenMention));

	}

	@Test
	public void testCreateTokenMention_FullNullInput() throws Exception {
		/*
		 * Expected Token: POS = NULL, TAGSET = NULL, STEM = NULL, LEMMA = NULL, TOKEN_NUMBER = NULL
		 */
		String posLabel = null;
		String tagSet = null;
		String stem = null;
		String lemma = null;
		Integer tokenNumber = null;

		DefaultClassMention expectedTokenMention = new DefaultClassMention(ClassMentionType.TOKEN.typeName());

		DefaultClassMention testTokenMention = DefaultTextAnnotation_Util.createTokenMention(posLabel, tagSet, stem,
				lemma, tokenNumber);

		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedTokenMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testTokenMention);

		assertTrue(expectedTokenMention.equals(testTokenMention));
	}

	@Test
	public void testCreatePhraseMention() throws Exception {
		String phraseType = "NP";
		String tagSet = "PENN";

		DefaultClassMention expectedPhraseMention = createPhraseMention(phraseType, tagSet);
		DefaultClassMention testPhraseMention = DefaultTextAnnotation_Util.createPhraseMention(phraseType, tagSet);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedPhraseMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testPhraseMention);

		assertTrue(expectedPhraseMention.equals(testPhraseMention));

		DefaultClassMention expectedPhraseMentionWithUnknownTagSet = createPhraseMention(phraseType,
				DefaultTextAnnotation_Util.UNKNOWN_TAGSET);
		DefaultClassMention testPhraseMentionWithNullTagSet = DefaultTextAnnotation_Util.createPhraseMention(
				phraseType, null);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotationWithUnknownTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				expectedPhraseMentionWithUnknownTagSet);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotationWithNullTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				testPhraseMentionWithNullTagSet);

		assertTrue(expectedPhraseMentionWithUnknownTagSet.equals(testPhraseMentionWithNullTagSet));
	}

	private DefaultClassMention createPhraseMention(String phraseType, String tagSet) throws InvalidInputException {
		DefaultClassMention expectedPhraseMention = new DefaultClassMention(ClassMentionType.PHRASE.typeName());
		StringSlotMention phraseTypeSlot = new DefaultStringSlotMention(SlotMentionType.PHRASE_TYPE.typeName());
		phraseTypeSlot.addSlotValue(phraseType);
		expectedPhraseMention.addPrimitiveSlotMention(phraseTypeSlot);
		StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
		tagSetSlot.addSlotValue(tagSet);
		expectedPhraseMention.addPrimitiveSlotMention(tagSetSlot);
		return expectedPhraseMention;
	}

	@Test
	public void testCreatePhraseMention_NullInput() throws Exception {
		String phraseType = null;
		String tagSet = "PENN";

		DefaultClassMention expectedPhraseMention = new DefaultClassMention(ClassMentionType.PHRASE.typeName());

		DefaultClassMention testPhraseMention = DefaultTextAnnotation_Util.createPhraseMention(phraseType, tagSet);

		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedPhraseMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testPhraseMention);

		assertTrue(expectedPhraseMention.equals(testPhraseMention));
	}

	@Test
	public void testCreateClauseMention() throws Exception {
		String clauseType = "NP";
		String tagSet = "PENN";

		DefaultClassMention expectedClauseMention = createClauseMention(clauseType, tagSet);
		DefaultClassMention testClauseMention = DefaultTextAnnotation_Util.createClauseMention(clauseType, tagSet);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedClauseMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testClauseMention);

		assertTrue(expectedClauseMention.equals(testClauseMention));

		DefaultClassMention expectedClauseMentionWithUnknownTagSet = createClauseMention(clauseType,
				DefaultTextAnnotation_Util.UNKNOWN_TAGSET);
		DefaultClassMention testClauseMentionWithNullTagSet = DefaultTextAnnotation_Util.createClauseMention(
				clauseType, null);
		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotationWithUnknownTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				expectedClauseMentionWithUnknownTagSet);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotationWithNullTagSet = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1,
				"first", "last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1,
				testClauseMentionWithNullTagSet);

		assertTrue(expectedClauseMentionWithUnknownTagSet.equals(testClauseMentionWithNullTagSet));
	}

	private DefaultClassMention createClauseMention(String clauseType, String tagSet) throws InvalidInputException {
		DefaultClassMention expectedClauseMention = new DefaultClassMention(ClassMentionType.CLAUSE.typeName());
		StringSlotMention clauseTypeSlot = new DefaultStringSlotMention(SlotMentionType.CLAUSE_TYPE.typeName());
		clauseTypeSlot.addSlotValue(clauseType);
		expectedClauseMention.addPrimitiveSlotMention(clauseTypeSlot);
		StringSlotMention tagSetSlot = new DefaultStringSlotMention(SlotMentionType.TAGSET.typeName());
		tagSetSlot.addSlotValue(tagSet);
		expectedClauseMention.addPrimitiveSlotMention(tagSetSlot);
		return expectedClauseMention;
	}

	@Test
	public void testCreateClauseMention_NullInput() throws Exception {
		String clauseType = null;
		String tagSet = null;

		DefaultClassMention expectedClauseMention = new DefaultClassMention(ClassMentionType.CLAUSE.typeName());

		DefaultClassMention testClauseMention = DefaultTextAnnotation_Util.createClauseMention(clauseType, tagSet);

		/* dummy annotations are necessary for mention comparison machinery to function */
		@SuppressWarnings("unused")
		TextAnnotation expectedAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedClauseMention);
		@SuppressWarnings("unused")
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, testClauseMention);

		System.out.println(expectedClauseMention.toString());
		System.out.println(testClauseMention.toString());
		assertTrue(expectedClauseMention.equals(testClauseMention));
	}

	@Test
	public void testSwapAnnotationInfo() throws Exception {
		TextAnnotation testAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, new DefaultClassMention("test type"));
		TextAnnotation emptyTA = new DefaultTextAnnotation(0, 0);
		emptyTA.setClassMention(new DefaultClassMention("test type"));

		assertFalse(testAnnotation.equals(emptyTA));

		TextAnnotationUtil.swapAnnotationInfo(testAnnotation, emptyTA);
		assertTrue(testAnnotation.equals(emptyTA));
	}

	@Test
	public void testAddSlotValue() throws Exception {
		DefaultClassMention proteinMention = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		TextAnnotation proteinAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, proteinMention);
		TextAnnotationUtil.addSlotValue(proteinAnnotation, SlotMentionType.PROTEIN_ENTREZGENEID.typeName(), 12345);

		DefaultClassMention expectedProteinMention = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		IntegerSlotMention sm = new DefaultIntegerSlotMention(SlotMentionType.PROTEIN_ENTREZGENEID.typeName());
		sm.addSlotValue(new Integer(12345));
		expectedProteinMention.addPrimitiveSlotMention(sm);
		TextAnnotation expectedproteinAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first",
				"last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedProteinMention);

		assertEquals(expectedproteinAnnotation, proteinAnnotation);

		TextAnnotationUtil.addSlotValue(proteinAnnotation, SlotMentionType.PROTEIN_ENTREZGENEID.typeName(), 6789);
		sm.addSlotValue(new Integer(6789));

		assertEquals(expectedproteinAnnotation, proteinAnnotation);

		/* now add a complex slot */
		DefaultClassMention proteinMention2 = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		TextAnnotation proteinAnnotation2 = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first", "last",
				"affiliation"), new AnnotationSet(), -1, -1, "1234", -1, proteinMention2);
		TextAnnotationUtil.addSlotValue(proteinAnnotation, "is-equivalent-to", proteinMention2);

		DefaultComplexSlotMention csm = new DefaultComplexSlotMention("is-equivalent-to");
		DefaultClassMention expectedProteinMention2 = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		TextAnnotation expectedproteinAnnotation2 = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first",
				"last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1, expectedProteinMention2);
		csm.addClassMention(expectedProteinMention2);
		expectedProteinMention.addComplexSlotMention(csm);

		assertEquals(expectedproteinAnnotation, proteinAnnotation);

	}

	@Test
	public void testRemoveDuplicateAnnotations() throws Exception {
		DefaultClassMention proteinMention = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		DefaultTextAnnotation proteinAnnotation = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first",
				"last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1, proteinMention);

		DefaultClassMention proteinMention2 = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		DefaultTextAnnotation proteinAnnotation2 = new DefaultTextAnnotation(0, 5, "dummy", new Annotator(-1, "first",
				"last", "affiliation"), new AnnotationSet(), -1, -1, "1234", -1, proteinMention2);

		Collection<TextAnnotation> textAnnotations = new ArrayList<TextAnnotation>();
		textAnnotations.add(proteinAnnotation);
		textAnnotations.add(proteinAnnotation2);
		Collection<TextAnnotation> annotationsAfterDuplicateRemoval = TextAnnotationUtil
				.removeDuplicateAnnotations(textAnnotations);
		assertEquals(1, annotationsAfterDuplicateRemoval.size());

		TextAnnotationUtil.addSlotValue(proteinAnnotation, SlotMentionType.PROTEIN_ENTREZGENEID.typeName(), 12345);
		textAnnotations = new ArrayList<TextAnnotation>();
		textAnnotations.add(proteinAnnotation);
		textAnnotations.add(proteinAnnotation2);
		annotationsAfterDuplicateRemoval = TextAnnotationUtil.removeDuplicateAnnotations(textAnnotations);
		assertEquals(2, annotationsAfterDuplicateRemoval.size());

		TextAnnotationUtil.addSlotValue(proteinAnnotation2, SlotMentionType.PROTEIN_ENTREZGENEID.typeName(), 12345);
		annotationsAfterDuplicateRemoval = TextAnnotationUtil.removeDuplicateAnnotations(textAnnotations);

		assertEquals(1, annotationsAfterDuplicateRemoval.size());

		textAnnotations = new ArrayList<TextAnnotation>();
		textAnnotations.add(proteinAnnotation);
		textAnnotations.add(proteinAnnotation2);
		proteinAnnotation2.setAnnotator(new Annotator(124, "no name", "no name", "no affiliation"));
		annotationsAfterDuplicateRemoval = TextAnnotationUtil.removeDuplicateAnnotations(textAnnotations);
		System.out.println("SIZE: " + annotationsAfterDuplicateRemoval.size());
		// assertEquals(1, annotationsAfterDuplicateRemoval.size());

		AnnotationComparator ac = new AnnotationComparator();
		int result = ac.compare(proteinAnnotation, proteinAnnotation2);
		assertEquals(0, result);
		assertEquals(0, proteinAnnotation.compareTo(proteinAnnotation2));
		assertTrue(proteinAnnotation.equals(proteinAnnotation2));

		assertEquals(proteinAnnotation.hashCode(), proteinAnnotation2.hashCode());

		Set<DefaultTextAnnotation> set = new HashSet<DefaultTextAnnotation>();
		set.add(proteinAnnotation);
		assertTrue(set.contains(proteinAnnotation2));

	}

	/**
	 * Creates an expected protein annotation
	 * 
	 * @param spanStart
	 * @param spanEnd
	 * @param coveredText
	 * @param annotatorID
	 * @param documentID
	 * @return
	 * @throws Exception
	 */
	private DefaultTextAnnotation createExpectedProteinAnnotation(int spanStart, int spanEnd, String coveredText,
			int annotatorID, String documentID, Integer... entrezGeneIDSlotFillers) throws Exception {
		DefaultClassMention proteinCM = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());
		DefaultTextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd, coveredText, new Annotator(
				annotatorID, "", "", ""), new AnnotationSet(), -1, -1, documentID, -1, proteinCM);

		addEntrezGeneSlotFillersToProteinTextAnnotation(entrezGeneIDSlotFillers, ta);
		return ta;
	}

	/**
	 * Adds slot filler values and a slot to the protein class mention
	 * 
	 * @param entrezGeneIDSlotFillers
	 * @param ta
	 * @throws Exception
	 */
	private void addEntrezGeneSlotFillersToProteinTextAnnotation(Integer[] entrezGeneIDSlotFillers, TextAnnotation ta)
			throws Exception {
		if (entrezGeneIDSlotFillers != null) {
			IntegerSlotMention sm = new DefaultIntegerSlotMention(SlotMentionType.PROTEIN_ENTREZGENEID.typeName());
			for (Integer entrezGeneID : entrezGeneIDSlotFillers) {
				sm.addSlotValue(entrezGeneID);
			}
			ta.getClassMention().addPrimitiveSlotMention(sm);
		}
	}

	private List<DefaultTextAnnotation> getExpectedAnnotations_NoSlots() throws Exception {
		List<DefaultTextAnnotation> taList = new ArrayList<DefaultTextAnnotation>();
		taList.add(createExpectedProteinAnnotation(0, 5, "abc-1", 99, "documentID=4", (Integer[]) null));
		taList.add(createExpectedProteinAnnotation(10, 30, "def-2", 33, "documentID=4", (Integer[]) null));
		return taList;
	}

	/**
	 * Creates a file containing protein annotations with no slots
	 * 
	 * @throws Exception
	 */
	public void createFileContainingAnnotationsToLoad_NoSlots() throws Exception {
		PrintStream ps = new PrintStream(TEST_ANNOTATION_STORAGE_FILE);
		(new AnnotationToFileOutput())
				.printDocument(AnnotationToFileOutputTest.createTestGenericDocument_NoSlots(), ps);
		ps.close();
	}

	/**
	 * Creates a file containing protein annotations with slots
	 * 
	 * @throws Exception
	 */
	public void createFileContainingAnnotationsToLoad_WithSlots() throws Exception {
		PrintStream ps = new PrintStream(TEST_ANNOTATION_STORAGE_FILE);
		(new AnnotationToFileOutput()).printDocument(AnnotationToFileOutputTest.createTestGenericDocument_WithSlots(),
				ps);
		ps.close();
	}

	@Test
	public void testLoadAnnotationsFromFile_NoSlots() throws Exception {
		System.err.println("Test File exists: " + TEST_ANNOTATION_STORAGE_FILE.exists());
		PrintStream ps = new PrintStream(TEST_ANNOTATION_STORAGE_FILE);
		(new AnnotationToFileOutput())
				.printDocument(AnnotationToFileOutputTest.createTestGenericDocument_NoSlots(), ps);
		ps.close();
		assertTrue(TEST_ANNOTATION_STORAGE_FILE.exists());
		Map<String, List<TextAnnotation>> documentID2TextAnnotationsMap = TextAnnotationUtil.loadAnnotationsFromFile(
				TEST_ANNOTATION_STORAGE_FILE, CharacterEncoding.US_ASCII);
		assertEquals(getExpectedAnnotations_NoSlots(), documentID2TextAnnotationsMap.get("documentID=4"));
	}

	private List<DefaultTextAnnotation> getExpectedAnnotations_WithSlots() throws Exception {
		List<DefaultTextAnnotation> taList = new ArrayList<DefaultTextAnnotation>();
		taList.add(createExpectedProteinAnnotation(0, 5, "abc-1", 99, "documentID=5", 123));
		taList.add(createExpectedProteinAnnotation(5, 10, "def-2", 33, "documentID=5", 456, 789));
		DefaultTextAnnotation ta = createExpectedProteinAnnotation(15, 20, "ghi-2", 33, "documentID=5", 157, 987);
		StringSlotMention sm = new DefaultStringSlotMention("anotherSlot");
		sm.addSlotValue("value1");
		sm.addSlotValue("value2");
		sm.addSlotValue("value3");
		ta.getClassMention().addPrimitiveSlotMention(sm);
		taList.add(ta);
		return taList;
	}

	@Test
	public void testLoadAnnotationsFromFile_WithSlots() throws Exception {
		PrintStream ps = new PrintStream(TEST_ANNOTATION_STORAGE_FILE);
		(new AnnotationToFileOutput()).printDocument(AnnotationToFileOutputTest.createTestGenericDocument_WithSlots(),
				ps);
		ps.close();
		assertTrue(TEST_ANNOTATION_STORAGE_FILE.exists());
		Map<String, List<TextAnnotation>> documentID2TextAnnotationsMap = TextAnnotationUtil.loadAnnotationsFromFile(
				TEST_ANNOTATION_STORAGE_FILE, CharacterEncoding.US_ASCII);
		// assertEquals(getExpectedAnnotations_WithSlots(),
		// documentID2TextAnnotationsMap.get("documentID=5"));

		assertEquals(getExpectedAnnotations_WithSlots().get(0), documentID2TextAnnotationsMap.get("documentID=5")
				.get(0));
		assertEquals(getExpectedAnnotations_WithSlots().get(1), documentID2TextAnnotationsMap.get("documentID=5")
				.get(1));
		assertEquals(getExpectedAnnotations_WithSlots().get(2), documentID2TextAnnotationsMap.get("documentID=5")
				.get(2));

	}

}
