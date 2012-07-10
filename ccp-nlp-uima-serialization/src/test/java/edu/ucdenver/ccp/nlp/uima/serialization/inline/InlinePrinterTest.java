/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.serialization.inline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.serialization.inline.InlineTag.InlinePostfixTag;
import edu.ucdenver.ccp.nlp.uima.serialization.inline.InlineTag.InlinePrefixTag;
import edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This test case tests the {@link InlinePrinter} {@link AnalysisEngine}
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class InlinePrinterTest extends DefaultUIMATestCase {

	protected static final TypeSystemDescription TSD = TypeSystemDescriptionFactory
			.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");

	/**
	 * The document text that is used in this test case
	 */
	/*
	 * 012345678901234567890123456789012345678
	 * 90123456789012345678901234567890123456789012345678901234567890
	 */
	private static final String DOCUMENT_TEXT = "The cow jumped over the moon & the nai\u0308ve stars, but the cd2 and cd5 receptors were not blocked.";

	/**
	 * sample class name for animals for use in this test case
	 */
	public static final String ANIMAL_CLASS = "animal";

	/**
	 * sample class name for celestial bodies used in this test case
	 */
	public static final String CELESTIAL_BODY_CLASS = "celestial body";

	/**
	 * sample class name for the moon used in this test case
	 */
	public static final String MOON_CLASS = "moon";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_1_CLASS = "outer1";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_2_CLASS = "outer2";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_3_CLASS = "outer3";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_4_CLASS = "outer4";

	/**
	 * sample class name used for testing nested annotations
	 */
	public static final String OUTER_5_CLASS = "outer5";

	/**
	 * sample class name used for testing split span annotations
	 */
	public static final String SPLIT_CLASS = "split";

	/**
	 * the document ID used in this test case
	 */
	public static final String SAMPLE_DOCUMENT_ID = "12345.utf8";

	/**
	 * 
	 */
	public static final CharacterEncoding SAMPLE_DOCUMENT_ENCODING = CharacterEncoding.UTF_8;

	/**
	 * Temporary folder to place inlined-annotation output files
	 */
	protected File outputDirectory;

	/**
	 * Sets up a temporary output directory and initializes the {@link JCas}
	 * 
	 * @see edu.uchsc.ccp.uima.test.DefaultUIMATestCase#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		// super.setUp();
		jcas = JCasFactory.createJCas(TSD);
		initJCas();
		outputDirectory = folder.newFolder("inline-output");
	}

	/**
	 * Initializes the document text, id, and encoding for the {@link JCas}
	 * 
	 * @see edu.uchsc.ccp.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {

		jcas.setDocumentText(DOCUMENT_TEXT);
		UIMA_Util.setDocumentID(jcas, SAMPLE_DOCUMENT_ID);
		UIMA_Util.setDocumentEncoding(jcas, SAMPLE_DOCUMENT_ENCODING);
	}

	/**
	 * Added simple annotations (no overlaps) to the sample jcas to be used in this test. <br>
	 * cow [4..7] = animal moon [24..28] = celestial body
	 */
	private void addSimpleSampleAnnotations() {
		addTextAnnotationToJCas(4, 7, ANIMAL_CLASS);
		addTextAnnotationToJCas(24, 28, CELESTIAL_BODY_CLASS);
	}

	/**
	 * Added simple annotations (no overlaps) to the sample jcas to be used in this test. <br>
	 * cow [4..7] = animal<br>
	 * moon [24..28] = celestial body<br>
	 * moon [24..28] = moon<br>
	 * cow jumped [4..14] = outer1<br>
	 * The cow jumped [0..14] = outer2<br>
	 * moon & [24..30] = outer3<br>
	 * moon & the nai\u0308ve stars [24..47]= outer4 <br>
	 * _over [14..19] = outer5
	 */
	protected void addOverlappingSampleAnnotations() {
		addTextAnnotationToJCas(4, 7, ANIMAL_CLASS);
		addTextAnnotationToJCas(24, 28, CELESTIAL_BODY_CLASS);
		addTextAnnotationToJCas(24, 28, MOON_CLASS);
		addTextAnnotationToJCas(4, 14, OUTER_1_CLASS);
		addTextAnnotationToJCas(0, 14, OUTER_2_CLASS);
		addTextAnnotationToJCas(24, 30, OUTER_3_CLASS);
		addTextAnnotationToJCas(24, 47, OUTER_4_CLASS);
		addTextAnnotationToJCas(14, 19, OUTER_5_CLASS);
	}

	protected void addSplitSpanCoordinatedAnnotations() {
		CCPTextAnnotation ccpTa = addTextAnnotationToJCas(57, 60, SPLIT_CLASS); // cd2
		UIMA_Annotation_Util.addSpan(ccpTa, new Span(69, 78), jcas); // receptors
		assertEquals(String.format(""), "cd2 and cd5 receptors", ccpTa.getCoveredText());
		ccpTa = addTextAnnotationToJCas(65, 78, SPLIT_CLASS); // cd5 receptors
		assertEquals(String.format(""), "cd5 receptors", ccpTa.getCoveredText());
	}

	/**
	 * Tests the InlinePrinter using a very simple case of two annotations (no overlaps) within the
	 * document text.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testInlinePrinter_SimpleCase() throws ResourceInitializationException, IOException,
			AnalysisEngineProcessException {
		addSimpleSampleAnnotations();
		String expectedOutput = String
				.format("The <%s>cow</%s> jumped over the <%s>moon</%s> & the nai\u0308ve stars, but the cd2 and cd5 receptors were not blocked.",
						ANIMAL_CLASS, ANIMAL_CLASS, CELESTIAL_BODY_CLASS, CELESTIAL_BODY_CLASS);
		AnalysisEngine inlinePrinterAe = InlinePrinter.createAnalysisEngine(TSD, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetadataHandler.class, SimpleInlineAnnotationExtractor.class);
		inlinePrinterAe.process(jcas);
		File expectedOutputFile = new File(outputDirectory, SAMPLE_DOCUMENT_ID + InlinePrinter.OUTPUT_FILE_SUFFIX);
		assertTrue("output file should exist", expectedOutputFile.exists());
		String inlinedAnnotationOutput = FileUtil.copyToString(expectedOutputFile, SAMPLE_DOCUMENT_ENCODING);
		assertEquals(String.format("Inlined output should be as expected"), expectedOutput, inlinedAnnotationOutput);
	}

	/**
	 * Tests the InlinePrinter using overlapping annotations within the document text. This is
	 * slightly more complex than the simple test case.
	 * 
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 */
	@Test
	public void testInlinePrinter_OverlappingCase() throws ResourceInitializationException, IOException,
			AnalysisEngineProcessException {
		addOverlappingSampleAnnotations();
		String expectedOutput = String
				.format("<%s>The <%s><%s>cow</%s> jumped</%s></%s><%s> over</%s> the <%s><%s><%s><%s>moon</%s></%s> &</%s> the nai\u0308ve stars</%s>, but the cd2 and cd5 receptors were not blocked.",
						OUTER_2_CLASS, OUTER_1_CLASS, ANIMAL_CLASS, ANIMAL_CLASS, OUTER_1_CLASS, OUTER_2_CLASS,
						OUTER_5_CLASS, OUTER_5_CLASS, OUTER_4_CLASS, OUTER_3_CLASS, CELESTIAL_BODY_CLASS, MOON_CLASS,
						MOON_CLASS, CELESTIAL_BODY_CLASS, OUTER_3_CLASS, OUTER_4_CLASS);
		AnalysisEngine inlinePrinterAe = InlinePrinter.createAnalysisEngine(TSD, outputDirectory,
				CAS.NAME_DEFAULT_SOFA, CcpDocumentMetadataHandler.class, SimpleInlineAnnotationExtractor.class);
		inlinePrinterAe.process(jcas);
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
