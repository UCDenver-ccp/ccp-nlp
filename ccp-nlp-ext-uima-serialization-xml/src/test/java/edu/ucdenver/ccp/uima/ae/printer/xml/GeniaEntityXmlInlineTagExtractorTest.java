package edu.ucdenver.ccp.uima.ae.printer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.InlineXmlPrinterTestBase;
import edu.ucdenver.ccp.uima.ae.printer.inline.GeniaXmlInlinePrinter;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlinePrinter_AE;
import edu.ucdenver.ccp.uima.ae.printer.inline.xml.CcpGeniaEntityXmlInlineTagExtractor;

/**
 * Tests the MUC named entity XML tag extractor
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeniaEntityXmlInlineTagExtractorTest extends InlineXmlPrinterTestBase {

	/**
	 * Tests the GENIA Entity XmlInlinePrinter using overlapping annotations within the document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testGeniaEntityXmlInlineTagExtractor_OverlappingCase() throws ResourceInitializationException,
			IOException, AnalysisEngineProcessException {
		addOverlappingSampleAnnotations();
		String expectedOutput = String
				.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<article>\n<term sem=\"%s\">The <term sem=\"%s\"><term sem=\"%s\">cow</term> jumped</term></term><term sem=\"%s\"> over</term> the <term sem=\"%s\"><term sem=\"%s\"><term sem=\"%s\"><term sem=\"%s\">moon</term></term> &amp;</term> the nai\u0308ve stars</term>, but the cd2 and cd5 receptors were not blocked.</article>\n",
						OUTER_2_CLASS, OUTER_1_CLASS, ANIMAL_CLASS, OUTER_5_CLASS, OUTER_4_CLASS, OUTER_3_CLASS,
						CELESTIAL_BODY_CLASS, MOON_CLASS);
		AnalysisEngine inlineXmlPrinterAe = GeniaXmlInlinePrinter.createAnalysisEngine(tsd, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, CcpGeniaEntityXmlInlineTagExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter_AE.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		System.err.println("EXPCTED: " + expectedOutput);
		System.err.println("INLINED: " + inlinedAnnotationOutput);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}
	
	/**
	 * Tests the GENIA Entity XmlInlinePrinter using split span annotations connected via coordination within the document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Ignore("Need to work on split-span handling, especially for non-coordinated cases")
	@Test
	public void testGeniaEntityXmlInlineTagExtractor_CoordinatedSplitSpanCase() throws ResourceInitializationException,
			IOException, AnalysisEngineProcessException {
		addSplitSpanCoordinatedAnnotations();
		String expectedOutput = String
				.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<article>\nThe cow jumped over the moon &amp; the nai\u0308ve stars, but the <coterm sem=\"(AND %s %s)\"><frag>cd2</frag> and <frag>cd5</frag> <frag>receptors</frag></coterm> were not blocked.</article>\n",SPLIT_CLASS, SPLIT_CLASS);
		AnalysisEngine inlineXmlPrinterAe = GeniaXmlInlinePrinter.createAnalysisEngine(tsd, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, CcpGeniaEntityXmlInlineTagExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter_AE.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		System.err.println("EXPCTED: " + expectedOutput);
		System.err.println("INLINED: " + inlinedAnnotationOutput);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}
	

}
