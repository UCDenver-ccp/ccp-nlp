package edu.ucdenver.ccp.uima.ae.printer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.InlineXmlPrinterTestBase;
import edu.ucdenver.ccp.uima.ae.printer.inline.GeniaXmlInlinePrinter;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlinePrinter_AE;
import edu.ucdenver.ccp.uima.ae.printer.inline.xml.ClearTkGeniaPosXmlInlineTagExtractor;

/**
 * Tests the MUC named entity XML tag extractor
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeniaPosXmlInlineTagExtractorTest extends InlineXmlPrinterTestBase {

	protected void addClearTkSentenceAnnotations() {
		Sentence sentence = new Sentence(jcas,0, DOCUMENT_TEXT.length());
		sentence.addToIndexes();
	}
	
	protected void addClearTkTokenAnnotations() {
		createClearTkToken(0,3,"DT","The");
		createClearTkToken(4,7,"NN","cow");
		createClearTkToken(8,14,"VBZ","jumped");
		createClearTkToken(15,19,"IN","over");
		createClearTkToken(20,23,"DT","the");
		createClearTkToken(24,28,"NN","moon");
		createClearTkToken(29,30,"CC","&");
		createClearTkToken(31,34,"DT","the");
		createClearTkToken(35,41,"JJ","nai\u0308ve");
		createClearTkToken(42,47,"NN","stars");
		createClearTkToken(47,48,"COMMA",",");
		createClearTkToken(49,52,"CC","but");
		createClearTkToken(53,56,"DT","the");
		createClearTkToken(57,60,"NN","cd2");
		createClearTkToken(61,64,"CC","and");
		createClearTkToken(65,68,"NN","cd5");
		createClearTkToken(69,78,"NN","receptors");
		createClearTkToken(79,83,"VBZ","were");
		createClearTkToken(84,87,"RB","not");
		createClearTkToken(88,95,"VBZ","blocked");
		createClearTkToken(95,96,"PERIOD",".");
	}
	
	private void createClearTkToken(int begin, int end, String posTag, String expectedCoveredText) {
		Token token = new Token(jcas, begin, end);
		token.setPos(posTag);
		token.addToIndexes();
		assertEquals("Token covered text not as expected", expectedCoveredText, token.getCoveredText());
	}
	
	
	/**
	 * Tests the GENIA Entity XmlInlinePrinter using overlapping annotations within the document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 * @throws InvalidXMLException 
	 */
	@Test
	public void testGeniaPosXmlInlineTagExtractor() throws ResourceInitializationException,
			IOException, AnalysisEngineProcessException, InvalidXMLException {
		addClearTkSentenceAnnotations();
		addClearTkTokenAnnotations();
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<article>\n<sentence><tok cat=\"DT\">The</tok> <tok cat=\"NN\">cow</tok> <tok cat=\"VBZ\">jumped</tok> <tok cat=\"IN\">over</tok> <tok cat=\"DT\">the</tok> <tok cat=\"NN\">moon</tok> <tok cat=\"CC\">&amp;</tok> <tok cat=\"DT\">the</tok> <tok cat=\"JJ\">nai\u0308ve</tok> <tok cat=\"NN\">stars</tok><tok cat=\"COMMA\">,</tok> <tok cat=\"CC\">but</tok> <tok cat=\"DT\">the</tok> <tok cat=\"NN\">cd2</tok> <tok cat=\"CC\">and</tok> <tok cat=\"NN\">cd5</tok> <tok cat=\"NN\">receptors</tok> <tok cat=\"VBZ\">were</tok> <tok cat=\"RB\">not</tok> <tok cat=\"VBZ\">blocked</tok><tok cat=\"PERIOD\">.</tok></sentence></article>\n";
		AnalysisEngine inlineXmlPrinterAe = GeniaXmlInlinePrinter.createAnalysisEngine(TSD, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, ClearTkGeniaPosXmlInlineTagExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter_AE.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		System.err.println("EXPCTED: " + expectedOutput);
		System.err.println("INLINED: " + inlinedAnnotationOutput);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}

}
