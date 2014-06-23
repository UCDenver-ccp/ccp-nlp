/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.bionlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpEventIteratorTest extends DefaultTestCase {

	private File entityFile;
	private File eventFile;
	private static final CharacterEncoding FILE_ENCODING = CharacterEncoding.UTF_8;

	@Before
	public void setUp() throws IOException {
		entityFile = folder.newFile("entities.a1");
		eventFile = folder.newFile("events.a2");
		FileWriterUtil.printLines(getTestEntityLines(), entityFile, FILE_ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(getTestEventLines(), eventFile, FILE_ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
	}

	/**
	 * @return a list of String representing BioNlp-formatted events and trigger words
	 */
	private List<?> getTestEventLines() {
		return CollectionsUtil.createList("T30\tGene_expression 62 72\texpression",
				"T36\tPositive_regulation 798 811\tup-regulation", "E1\tGene_expression:T30 Theme:T3",
				"E13\tPositive_regulation:T36 Theme:E1");
	}

	/**
	 * @return a list of Strings representing BioNlp-formatted entities
	 */
	private List<String> getTestEntityLines() {
		return CollectionsUtil.createList("T3\tProtein 81 90\tTGF-beta1", "T5\tProtein 206 216\tLTGF-beta1",
				"T6\tProtein 263 289\tLTGF-beta1 binding protein");
	}

	@Test
	public void testEventFileParsing() throws FileNotFoundException {

		BioNlpEventIterator eventIterator = new BioNlpEventIterator(eventFile, entityFile, FILE_ENCODING);

		if (eventIterator.hasNext()) {
			TextAnnotation ta = eventIterator.next();
			assertEquals("type should be as expected", "gene_expression", ta.getClassMention().getMentionName());
			assertEquals("start span should be as expected", 62, ta.getAnnotationSpanStart());
			assertEquals("end span should be as expected", 72, ta.getAnnotationSpanEnd());
			assertEquals("event ID should be as expected", "E1",
					ta.getClassMention().getPrimitiveSlotMentionByName(BioNlpEventFactory.EVENT_ID_SLOT_NAME)
							.getSingleSlotValue());
			ClassMention proteinMention = ta.getClassMention()
					.getComplexSlotMentionByName(BioNlpEventFactory.THEME_SLOT_NAME).getSingleSlotValue();
			assertEquals("event theme should be a protein", "protein", proteinMention.getMentionName());
			assertEquals("protein start span should be as expected", 81, proteinMention.getTextAnnotation()
					.getAnnotationSpanStart());
			assertEquals("protein end span should be as expected", 90, proteinMention.getTextAnnotation()
					.getAnnotationSpanEnd());
		} else
			fail("Expected an entity annotation to be returned");

		if (eventIterator.hasNext()) {
			TextAnnotation ta = eventIterator.next();
			assertEquals("type should be as expected", "positive_regulation", ta.getClassMention().getMentionName());
			assertEquals("start span should be as expected", 798, ta.getAnnotationSpanStart());
			assertEquals("end span should be as expected", 811, ta.getAnnotationSpanEnd());
			assertEquals("event ID should be as expected", "E13",
					ta.getClassMention().getPrimitiveSlotMentionByName(BioNlpEventFactory.EVENT_ID_SLOT_NAME)
							.getSingleSlotValue());
			ClassMention expressionMention = ta.getClassMention()
					.getComplexSlotMentionByName(BioNlpEventFactory.THEME_SLOT_NAME).getSingleSlotValue();
			assertEquals("event theme should be gene expression", "gene_expression", expressionMention.getMentionName());
			assertEquals("expression mention start span should be as expected", 62, expressionMention
					.getTextAnnotation().getAnnotationSpanStart());
			assertEquals("expression mention end span should be as expected", 72, expressionMention.getTextAnnotation()
					.getAnnotationSpanEnd());
		} else
			fail("Expected an entity annotation to be returned");

		assertFalse(eventIterator.hasNext());
	}

}
