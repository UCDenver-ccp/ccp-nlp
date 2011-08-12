package edu.ucdenver.ccp.uima.ae.printer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.inline.InlinePrinter;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.InlineXmlPrinterTestBase;
import edu.ucdenver.ccp.uima.ae.printer.inline.XmlInlinePrinter;
import edu.ucdenver.ccp.uima.ae.printer.inline.xml.CcpMucEntityXmlInlineTagExtractor;

/**
 * Tests the MUC named entity XML tag extractor
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MucEntityXmlInlineTagExtractorTest extends InlineXmlPrinterTestBase {

	/**
	 * Tests the MUC Entity XmlInlinePrinter using overlapping annotations within the document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testMucEntityXmlInlineTagExtractor_OverlappingCase() throws ResourceInitializationException,
			IOException, AnalysisEngineProcessException {
		addOverlappingSampleAnnotations();
		String expectedOutput = String
				.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ENAMEX TYPE=\"%s\">The <ENAMEX TYPE=\"%s\"><ENAMEX TYPE=\"%s\">cow</ENAMEX> jumped</ENAMEX></ENAMEX><ENAMEX TYPE=\"%s\"> over</ENAMEX> the <ENAMEX TYPE=\"%s\"><ENAMEX TYPE=\"%s\"><ENAMEX TYPE=\"%s\"><ENAMEX TYPE=\"%s\">moon</ENAMEX></ENAMEX> &amp;</ENAMEX> the nai\u0308ve stars</ENAMEX>, but the cd2 and cd5 receptors were not blocked.",
						OUTER_2_CLASS, OUTER_1_CLASS, ANIMAL_CLASS, OUTER_5_CLASS, OUTER_4_CLASS, OUTER_3_CLASS,
						CELESTIAL_BODY_CLASS, MOON_CLASS);
		AnalysisEngine inlineXmlPrinterAe = XmlInlinePrinter.createAnalysisEngine(tsd, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, CcpMucEntityXmlInlineTagExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		System.err.println("EXPCTED: " + expectedOutput);
		System.err.println("INLINED: " + inlinedAnnotationOutput);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}

}
