package edu.ucdenver.ccp.uima.ae.printer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.serialization.inline.InlinePrinter;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePostfixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTag.InlinePrefixTag;
import edu.ucdenver.ccp.uima.ae.printer.inline.InlineTagExtractor_ImplBase;
import edu.ucdenver.ccp.uima.ae.printer.inline.XmlInlinePrinter;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * This test case tests the {@link InlinePrinter} {@link AnalysisEngine}
 * 
 * @author bill
 * 
 */
public class InlineXmlPrinterTest extends InlineXmlPrinterTestBase {
	
	/**
	 * Tests the XmlInlinePrinter using a very simple case of two annotations (no overlaps) within
	 * the document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testInlineXmlPrinter_SimpleCase() throws ResourceInitializationException, IOException,
			AnalysisEngineProcessException {
		addSimpleSampleAnnotations();
		String expectedOutput = String
				.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\nThe <%s>cow</%s> jumped over the <%s>moon</%s> &amp; the nai\u0308ve stars, but the cd2 and cd5 receptors were not blocked.",
						ANIMAL_CLASS, ANIMAL_CLASS, CELESTIAL_BODY_CLASS, CELESTIAL_BODY_CLASS);
		AnalysisEngine inlineXmlPrinterAe = XmlInlinePrinter.createAnalysisEngine(TSD, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, SimpleInlineAnnotationExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}

	/**
	 * Tests the XmlInlinePrinter using overlapping annotations within the document text. This is
	 * slightly more complex than the simple test case.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testXmlInlinePrinter_OverlappingCase() throws ResourceInitializationException, IOException,
			AnalysisEngineProcessException {
		addOverlappingSampleAnnotations();
		String expectedOutput = String
				.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<%s>The <%s><%s>cow</%s> jumped</%s></%s><%s> over</%s> the <%s><%s><%s><%s>moon</%s></%s> &amp;</%s> the nai\u0308ve stars</%s>, but the cd2 and cd5 receptors were not blocked.",
						OUTER_2_CLASS, OUTER_1_CLASS, ANIMAL_CLASS, ANIMAL_CLASS, OUTER_1_CLASS, OUTER_2_CLASS,
						OUTER_5_CLASS, OUTER_5_CLASS, OUTER_4_CLASS, OUTER_3_CLASS, CELESTIAL_BODY_CLASS, MOON_CLASS,
						MOON_CLASS, CELESTIAL_BODY_CLASS, OUTER_3_CLASS, OUTER_4_CLASS);
		AnalysisEngine inlineXmlPrinterAe = XmlInlinePrinter.createAnalysisEngine(TSD, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetaDataExtractor.class, SimpleInlineAnnotationExtractor.class);
		inlineXmlPrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}

	/**
	 * A very straightforward extension of the {@link InlineTagExtractor_ImplBase}. This class
	 * returns XML tags whose names are determined by the annotation class mention name.
	 * 
	 * @author bill
	 * 
	 */
	private static class SimpleInlineAnnotationExtractor extends InlineTagExtractor_ImplBase {

		/**
		 * Constructor states that CCPTextAnnotations and the CcpAnnotationDataExtractor will be
		 * used
		 */
		public SimpleInlineAnnotationExtractor() {
			super(CCPTextAnnotation.type, new CcpAnnotationDataExtractor());
		}

		/**
		 * Returns XML tags using the annotation's class mention name, e.g. <mention_name>covered
		 * text</mention_name>
		 * 
		 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlineTagExtractor_ImplBase#getInlineTags(org.apache.uima.jcas.tcas.Annotation)
		 */
		@Override
		protected List<InlineTag> getInlineTags(Annotation annotation) {
			System.err.println("IS CCPTEXTANNOTATION INSTANCE: " + (annotation instanceof CCPTextAnnotation));
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) annotation;
			String type = ccpTa.getClassMention().getMentionName();
			Span span = new Span(ccpTa.getBegin(), ccpTa.getEnd());
			InlineTag openTag = new InlinePrefixTag(String.format("<%s>", type), span);
			InlineTag closeTag = new InlinePostfixTag(String.format("</%s>", type), span);
			return CollectionsUtil.createList(openTag, closeTag);
		}

	}

}
