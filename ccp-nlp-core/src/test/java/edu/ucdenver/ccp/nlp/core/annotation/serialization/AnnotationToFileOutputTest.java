package edu.ucdenver.ccp.nlp.core.annotation.serialization;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultStringSlotMention;

public class AnnotationToFileOutputTest extends DefaultTestCase {

	private static Logger logger = Logger.getLogger(AnnotationToFileOutputTest.class);
	// private static final File TEST_OUTPUT_FILE = new
	// File("data/test/edu.uchsc.ccp.util.nlp.annotation.printer/test.out");

	private File TEST_OUTPUT_FILE;

	@Before
	public void setUp() throws IOException {
		TEST_OUTPUT_FILE = folder.newFile("outputFile.ascii");
	}

	/**
	 * Creates a protein annotation with optional entrez gene id slot fillers
	 * 
	 * @param spanStart
	 * @param spanEnd
	 * @param coveredText
	 * @param annotatorID
	 * @param documentID
	 * @param entrezGeneIDSlotFillers
	 * @return
	 * @throws Exception
	 */
	private static DefaultTextAnnotation createProteinAnnotation(int spanStart, int spanEnd, String coveredText,
			int annotatorID, String documentID, Integer... entrezGeneIDSlotFillers) throws Exception {
		DefaultClassMention proteinCM = new DefaultClassMention(ClassMentionTypes.PROTEIN);
		DefaultTextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd, coveredText, new Annotator(
				annotatorID, "firstname", "lastname", "affiliation"), new AnnotationSet(), 0, 1, documentID, -1,
				proteinCM);

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
	private static void addEntrezGeneSlotFillersToProteinTextAnnotation(Integer[] entrezGeneIDSlotFillers,
			TextAnnotation ta) throws Exception {
		if (entrezGeneIDSlotFillers != null) {
			IntegerSlotMention sm = new DefaultIntegerSlotMention(SlotMentionTypes.PROTEIN_ENTREZGENEID);
			for (Integer entrezGeneID : entrezGeneIDSlotFillers) {
				sm.addSlotValue(entrezGeneID);
			}
			ta.getClassMention().addPrimitiveSlotMention(sm);
		}
	}

	/**
	 * Creates an example document with two annotations, neither with slots
	 * 
	 * @return
	 * @throws Exception
	 */
	public static GenericDocument createTestGenericDocument_NoSlots() throws Exception {
		GenericDocument gd = new GenericDocument("documentID=4");

		gd.addAnnotation(createProteinAnnotation(0, 5, "abc-1", 99, "documentID=4", (Integer[]) null));

		DefaultTextAnnotation ta2 = createProteinAnnotation(10, 15, "def-2", 33, "documentID=4", (Integer[]) null);
		ta2.addSpan(new Span(25, 30));
		gd.addAnnotation(ta2);

		return gd;
	}

	/**
	 * Returns the expected output from the "no slots" document
	 * 
	 * @return
	 */
	private List<String> getExpectedOutputLines_NoSlots() {
		return CollectionsUtil.createList("documentID=4|99|0 5|protein|abc-1", "documentID=4|33|10 30|protein|def-2");
	}

	@Test
	public void testAnnotationToFileOutput() throws Exception {
		PrintStream ps = new PrintStream(TEST_OUTPUT_FILE);
		(new AnnotationToFileOutput()).printDocument(createTestGenericDocument_NoSlots(), ps);
		ps.close();
		assertTrue(TEST_OUTPUT_FILE.exists());
		assertTrue(FileComparisonUtil.hasExpectedLines(TEST_OUTPUT_FILE, CharacterEncoding.US_ASCII,
				getExpectedOutputLines_NoSlots(), null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
	}

	/**
	 * Creates an example document with two annotations, both with slots and slot fillers
	 * 
	 * @return
	 * @throws Exception
	 */
	public static GenericDocument createTestGenericDocument_WithSlots() throws Exception {
		GenericDocument gd = new GenericDocument("documentID=5");

		gd.addAnnotation(createProteinAnnotation(0, 5, "abc-1", 99, "documentID=5", 123));
		gd.addAnnotation(createProteinAnnotation(5, 10, "def-2", 33, "documentID=5", 456, 789));
		DefaultTextAnnotation ta = createProteinAnnotation(15, 20, "ghi-2", 33, "documentID=5", 157, 987);
		StringSlotMention sm = new DefaultStringSlotMention("anotherSlot");
		sm.addSlotValue("value1");
		sm.addSlotValue("value2");
		sm.addSlotValue("value3");
		ta.getClassMention().addPrimitiveSlotMention(sm);
		gd.addAnnotation(ta);

		return gd;
	}

	/**
	 * Creates an example document with two annotations, both with slots and slot fillers, and with
	 * pipes in slot names and slot filler.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static GenericDocument createTestGenericDocument_WithSlotsWithPipes() throws Exception {
		GenericDocument gd = new GenericDocument("documentID=5");

		gd.addAnnotation(createProteinAnnotation(0, 5, "abc-1", 99, "documentID=5", 123));
		gd.addAnnotation(createProteinAnnotation(5, 10, "def-2", 33, "documentID=5", 456, 789));
		DefaultTextAnnotation ta = createProteinAnnotation(15, 20, "ghi-2|||", 33, "documentID=5", 157, 987);
		StringSlotMention sm = new DefaultStringSlotMention("another|Slot");
		sm.addSlotValue("value1");
		sm.addSlotValue("value2|");
		sm.addSlotValue("value3");
		ta.getClassMention().addPrimitiveSlotMention(sm);
		gd.addAnnotation(ta);

		return gd;
	}

	/**
	 * Returns the expected output from the "with slots" document
	 * 
	 * @return
	 */
	private List<String> getExpectedOutputLines_WithSlots() {
		return CollectionsUtil.createList("documentID=5|99|0 5|protein|abc-1|entrez_gene_id|123",
				"documentID=5|33|5 10|protein|def-2|entrez_gene_id|456,789",
				"documentID=5|33|15 20|protein|ghi-2|anotherSlot|value1,value2,value3|entrez_gene_id|157,987");
	}

	/**
	 * Returns the expected output from the "with slots" document
	 * 
	 * @return
	 */
	private List<String> getExpectedOutputLines_WithSlotsWithPipes() {
		return CollectionsUtil
				.createList(
						"documentID=5|99|0 5|protein|abc-1|entrez_gene_id|123",
						"documentID=5|33|5 10|protein|def-2|entrez_gene_id|456,789",
						"documentID=5|33|15 20|protein|ghi-2[PIPE][PIPE][PIPE]|another[PIPE]Slot|value1,value2[PIPE],value3|entrez_gene_id|157,987");
	}

	@Test
	public void testAnnotationToFileOutputWithSlots() throws Exception {
		PrintStream ps = new PrintStream(TEST_OUTPUT_FILE);
		(new AnnotationToFileOutput()).printDocument(createTestGenericDocument_WithSlots(), ps);
		ps.close();
		assertTrue(TEST_OUTPUT_FILE.exists());
		assertTrue(FileComparisonUtil.hasExpectedLines(TEST_OUTPUT_FILE, CharacterEncoding.US_ASCII,
				getExpectedOutputLines_WithSlots(), null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
	}

	@Test
	public void testAnnotationToFileOutputWithSlotsWithPipes() throws Exception {
		BasicConfigurator.configure();
		logger.info("Three warning messages expected...");
		PrintStream ps = new PrintStream(TEST_OUTPUT_FILE);
		(new AnnotationToFileOutput()).printDocument(createTestGenericDocument_WithSlotsWithPipes(), ps);
		ps.close();
		assertTrue(TEST_OUTPUT_FILE.exists());
		assertTrue(FileComparisonUtil.hasExpectedLines(TEST_OUTPUT_FILE, CharacterEncoding.US_ASCII,
				getExpectedOutputLines_WithSlotsWithPipes(), null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
	}

}
