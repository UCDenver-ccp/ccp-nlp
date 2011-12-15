package edu.ucdenver.ccp.nlp.wrapper.linnaeus;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.wrapper.linnaeus.LinnaeusUtil;

public class LinnaeusUtilTest extends DefaultTestCase {

	private final File SAMPLE_LINNAEUS_OUTPUT_FILE = new File(
			"data/test/edu.uchsc.ccp.util.nlp.tool.external.linnaeus/example-linnaeus-output.csv");
	private final File ANNOTATION_LOADER_FILE = new File(
			"data/test/edu.uchsc.ccp.util.nlp.tool.external.linnaeus/test-output.txt");

	private final File EXPECTED_ANNOTATION_LOADER_FILE = new File(
			"data/test/edu.uchsc.ccp.util.nlp.tool.external.linnaeus/expected-annotation-loader-file.txt");

	private void removeTestOutputFile() {
		if (ANNOTATION_LOADER_FILE.exists()) {
			ANNOTATION_LOADER_FILE.delete();
		}
	}

	// @Test
	// public void testConvertLinnaeusOutputFileToAnnotationLoaderFormat() throws Exception {
	// removeTestOutputFile();
	// LinnaeusUtil.convertLinnaeusOutputFileToAnnotationLoaderFileFormat(SAMPLE_LINNAEUS_OUTPUT_FILE,
	// ANNOTATION_LOADER_FILE);
	// assertTrue(TestUtil.filesAreEquivalent(EXPECTED_ANNOTATION_LOADER_FILE,
	// ANNOTATION_LOADER_FILE));
	// removeTestOutputFile();
	// }
	//
	// @Test
	// public void convert() throws Exception {
	// LinnaeusUtil
	// .convertLinnaeusOutputFileToAnnotationLoaderFileFormat(
	// new
	// File("/Users/bill/Documents/projects/CRAFT/ner-testing/species/linnaeus/linnaeusCraft.csv"),
	// new File(
	// "/Users/bill/Documents/projects/CRAFT/ner-testing/species/linnaeus/linnaeusCraft.annotationFileLoader.txt"));
	// }

	@Ignore
	@Test
	public void testSearchString() {
		LinnaeusUtil lu = new LinnaeusUtil(new File(
				"/data/toolrepository/ner/species/linnaeus/linnaeus-1.5/species/properties.conf"));

		String inputText = "Human E. coli";

		Collection<TextAnnotation> matches = lu.annotateText("doc1", inputText, 0);

		for (TextAnnotation ta : matches)
			ta.printAnnotation(System.out);

	}

	@Test
	public void testBuildDictionaryFile() throws IOException {
		File geneVariantFile = initGeneVariantFile();
		File supplementaryGeneVariantFile = initSupplementaryGeneVariantFile();
		File stopListFile = initStopListFile();
		File linnaeusGenePropertiesFile = initGenePropertiesFile(geneVariantFile, supplementaryGeneVariantFile,
				stopListFile);
		LinnaeusUtil lu = new LinnaeusUtil(linnaeusGenePropertiesFile);

		String inputText = "Human E. coli abc1 is a gene. So is xyz56. this one should be missed: xyz-56. " +
				"Genes from supplementary file: cab23, and yoyo22.";

		Collection<TextAnnotation> matches = lu.annotateText("doc1", inputText, 0);

		for (TextAnnotation ta : matches)
			ta.printAnnotation(System.out);

		assertEquals("Should only have 4 gene annotations", 4, matches.size());

	}

	private File initGenePropertiesFile(File geneVariantFile, File supplementaryGeneVariantFile, File stopListFile)
			throws IOException {
		List<String> lines = CollectionsUtil.createList(
				String.format("variantMatcher=%s;%s", geneVariantFile.getAbsolutePath(),
						supplementaryGeneVariantFile.getAbsolutePath()),
				String.format("ppStopTerms=%s", stopListFile.getAbsolutePath()), "postProcessing", "noDisambiguation");
		File genePropertiesFile = folder.newFile("properties.conf");
		FileWriterUtil.printLines(lines, genePropertiesFile, CharacterEncoding.US_ASCII, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		return genePropertiesFile;
	}

	private File initGeneVariantFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("12345\tabc|abc-1|abc1|a", "56789\txyz56");
		File geneVariantFile = folder.newFile("geneVariants.ascii");
		FileWriterUtil.printLines(lines, geneVariantFile, CharacterEncoding.US_ASCII);
		return geneVariantFile;
	}

	private File initSupplementaryGeneVariantFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("12345\tcab23", "999\tyoyo22");
		File geneVariantFile = folder.newFile("supplementaryGeneVariants.ascii");
		FileWriterUtil.printLines(lines, geneVariantFile, CharacterEncoding.US_ASCII);
		return geneVariantFile;
	}

	private File initSupplementaryGeneVariantFile_withAmbiguities() throws IOException {
		List<String> lines = CollectionsUtil.createList("12345\tyoyo22", "999\tyoyo22");
		File geneVariantFile = folder.newFile("supplementaryGeneVariants.ascii");
		FileWriterUtil.printLines(lines, geneVariantFile, CharacterEncoding.US_ASCII);
		return geneVariantFile;
	}

	private File initStopListFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("12345\ta");
		File stopListFile = folder.newFile("stopList.ascii");
		FileWriterUtil.printLines(lines, stopListFile, CharacterEncoding.US_ASCII);
		return stopListFile;
	}

	@Test
	public void testBuildDictionaryFile_ambiguitiesExist() throws Exception {
		File geneVariantFile = initGeneVariantFile();
		File supplementaryGeneVariantFile = initSupplementaryGeneVariantFile_withAmbiguities();
		File stopListFile = initStopListFile();
		File linnaeusGenePropertiesFile = initGenePropertiesFile(geneVariantFile, supplementaryGeneVariantFile,
				stopListFile);
		LinnaeusUtil lu = new LinnaeusUtil(linnaeusGenePropertiesFile);

		String inputText = "Human E. coli abc1 is a gene. So is xyz56. this one should be missed: xyz-56. " +
				"Genes from supplementary file: cab23, and yoyo22.";

		Collection<TextAnnotation> matches = lu.annotateText("doc1", inputText, 0);

		for (TextAnnotation ta : matches)
			ta.printAnnotation(System.out);

		assertEquals("Should only have 4 gene annotations", 4, matches.size());

	}

}
