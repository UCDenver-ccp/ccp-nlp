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

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpThemeIteratorTest extends DefaultTestCase {

	private File entityFile;
	private final CharacterEncoding ENTITY_FILE_ENCODING = CharacterEncoding.UTF_8;

	@Before
	public void setUp() throws IOException {
		entityFile = folder.newFile("entities.a1");
		FileWriterUtil.printLines(getTestEntityLines(), entityFile, ENTITY_FILE_ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
	}

	/**
	 * @return a list of Strings representing BioNlp formatted entities
	 */
	private List<String> getTestEntityLines() {
		return CollectionsUtil.createList("E1", "T3\tProtein 81 90\tTGF-beta1", "E2",
				"T5\tProtein 206 216\tLTGF-beta1", "T6\tProtein 263 289\tLTGF-beta1 binding protein", "E4", "E5");
	}

	@Test
	public void testEntityFileParsing() throws FileNotFoundException {
		BioNlpThemeIterator entityFileReader = new BioNlpThemeIterator(entityFile, ENTITY_FILE_ENCODING);

		if (entityFileReader.hasNext()) {
			TextAnnotation ta = entityFileReader.next();
			assertEquals("type should be as expected", "protein", ta.getClassMention().getMentionName());
			assertEquals("start span should be as expected", 81, ta.getAnnotationSpanStart());
			assertEquals("end span should be as expected", 90, ta.getAnnotationSpanEnd());
			assertEquals("entity ID should be as expected", "T3",
					ta.getClassMention().getPrimitiveSlotMentionByName(BioNlpThemeFactory.THEME_ID_SLOT_NAME)
							.getSingleSlotValue());
		} else
			fail("Expected an entity annotation to be returned");

		if (entityFileReader.hasNext()) {
			TextAnnotation ta = entityFileReader.next();
			assertEquals("type should be as expected", "protein", ta.getClassMention().getMentionName());
			assertEquals("start span should be as expected", 206, ta.getAnnotationSpanStart());
			assertEquals("end span should be as expected", 216, ta.getAnnotationSpanEnd());
			assertEquals("entity ID should be as expected", "T5",
					ta.getClassMention().getPrimitiveSlotMentionByName(BioNlpThemeFactory.THEME_ID_SLOT_NAME)
							.getSingleSlotValue());
		} else
			fail("Expected an entity annotation to be returned");

		if (entityFileReader.hasNext()) {
			TextAnnotation ta = entityFileReader.next();
			assertEquals("type should be as expected", "protein", ta.getClassMention().getMentionName());
			assertEquals("start span should be as expected", 263, ta.getAnnotationSpanStart());
			assertEquals("end span should be as expected", 289, ta.getAnnotationSpanEnd());
			assertEquals("entity ID should be as expected", "T6",
					ta.getClassMention().getPrimitiveSlotMentionByName(BioNlpThemeFactory.THEME_ID_SLOT_NAME)
							.getSingleSlotValue());
		} else
			fail("Expected an entity annotation to be returned");

		assertFalse(entityFileReader.hasNext());
	}

}
