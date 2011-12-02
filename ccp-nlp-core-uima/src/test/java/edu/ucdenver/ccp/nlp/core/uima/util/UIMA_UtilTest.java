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

package edu.ucdenver.ccp.nlp.core.uima.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultIntegerSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;


public class UIMA_UtilTest {
	private static Logger logger = Logger.getLogger(UIMA_UtilTest.class);
	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	

	AnnotationSet testAnnotationSet;

	CCPAnnotationSet testCCPAnnotationSet;

	Annotator testAnnotator;

	CCPAnnotator testCCPAnnotator;

	CCPClassMention testCCPClassMention;

	/**
	 * <pre>
	 *     testCCPTextAnnotation1 #################################################:
	 *         ======================= Annotation: 4567 =======================
	 *         Annotator: 5 - TestCCPFirst TestCCPLast -- TestCCPAffiliation
	 *         --- AnnotationSets: 10 - TestCCPAnnotationSet -- TestCCPDescription
	 *         --- Default Span: 11 - 56
	 *         --- Span: 11 - 21  44 - 56
	 *         --- DocumentSection: 2
	 *         --- Covered Text: null
	 *         -CLASS MENTION: ccpClassMentionName &quot;&quot;
	 *         -    SLOT MENTION: NCSM Mention Name with SLOT VALUE(s): value1  value2
	 *         =================================================================================
	 * </pre>
	 */
	CCPTextAnnotation testCCPTextAnnotation1;

	CCPTextAnnotation testCCPTextAnnotation2;

	/**
	 * <pre>
	 *     testTextAnnotation1 #################################################:
	 *         ======================= Annotation: 1010 =======================
	 *         Annotator: 3|TestAnnotatorFirst|TestAnnotatorLast|TestAnnotatorAffiliation
	 *         --- AnnotationSets: 15|TestAnnotationSetName|TestAnnotationSetDescription
	 *         --- Span: 15 - 37
	 *         --- DocCollection: 2  DocID: 33  DocumentSection: 1
	 *         --- Covered Text: coveredText1
	 *         -CLASS MENTION: gated nuclear transport &quot;coveredText1&quot;
	 *         -    COMPLEX SLOT MENTION: transport origin
	 *         -        CLASS MENTION: nucleus &quot;nuclear&quot;
	 *         -    COMPLEX SLOT MENTION: transport location
	 *         -        CLASS MENTION: nucleus &quot;nuclear&quot;
	 *         -    COMPLEX SLOT MENTION: transport participants
	 *         -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *         -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 999999
	 *         -    COMPLEX SLOT MENTION: transported entities
	 *         -        CLASS MENTION: protein &quot;E2F-4&quot;
	 *         -            SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 999999
	 *         =================================================================================
	 * </pre>
	 */
	DefaultTextAnnotation testTextAnnotation1;

	/**
	 * <pre>
	 *     testTextAnnotation2 #################################################:
	 *         ======================= Annotation: 1011 =======================
	 *         Annotator: 3|TestAnnotatorFirst|TestAnnotatorLast|TestAnnotatorAffiliation
	 *         --- AnnotationSets: 15|TestAnnotationSetName|TestAnnotationSetDescription
	 *         --- Span: 0 - 8
	 *         --- DocCollection: 2  DocID: 33  DocumentSection: 1
	 *         --- Covered Text: EBV LMP1
	 *         -CLASS MENTION: protein &quot;EBV LMP1&quot;
	 *         -    SLOT MENTION: entrez_gene_id with SLOT VALUE(s): 888888
	 *         =================================================================================
	 * </pre>
	 */
	DefaultTextAnnotation testTextAnnotation2;

	JCas jcas;

	@After
	public void tearDown() throws Exception {
		/* ready variables for garbage collection */
		testAnnotationSet = null;
		testCCPAnnotationSet = null;
		testAnnotator = null;
		testCCPAnnotator = null;
		testCCPClassMention = null;
		testCCPTextAnnotation1 = null;
		testCCPTextAnnotation2 = null;
		testTextAnnotation1 = null;
		testTextAnnotation2 = null;
		jcas = null;
	}

	@Before
	public void setUp() throws Exception {
		String documentText = "This is the document text. This is the document text. This is the document text. "
				+ "This is the document text. This is the document text. This is the document text. This is the document text. "
				+ "This is the document text. This is the document text. This is the document text. This is the document text. "
				+ "This is the document text. This is the document text. This is the document text. This is the document text. "
				+ "This is the document text. This is the document text. This is the document text. This is the document text. "
				+ "This is the document text. This is the document text. ";
		String documentID = "-1";
		jcas = initializeJCas(documentID, documentText);

		// // init a new JCas to use for testing
		// // AnalysisEngineDescription aed = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(
		// // new XMLInputSource(TestProperties.SAMPLE_ANALYSISENGINE_DESCRIPTORFILE));
		// // CAS cas = CasCreationUtils.createCas(aed);
		// // jcas = cas.getJCas();
		//
		// XMLParser uimaXMLParser = UIMAFramework.getXMLParser();
		//
		// XMLInputSource inputSource = new XMLInputSource(SAMPLE_ANALYSISENGINE_DESCRIPTORFILE);
		//
		// ResourceSpecifier sampleDescriptor = uimaXMLParser.parseResourceSpecifier(inputSource);
		//
		// TextAnalysisEngine tokAndPOSTagger = UIMAFramework.produceTAE(sampleDescriptor);
		//
		// // create CAS and populate it with initial text.
		// CAS tcas = tokAndPOSTagger.newCAS();
		//
		// jcas = tcas.getJCas();
		// jcas.setDocumentText("");

		// AnnotationSet: ID = 15, SetName = "TestAnnotationSetName",
		// SetDescription = "TestAnnotationSetDescription"
		testAnnotationSet = new AnnotationSet(new Integer(15), "TestAnnotationSetName", "TestAnnotationSetDescription");

		// CCPAnnotationSet: ID = 10, SetName = "TestCCPAnnotationSetName",
		// SetDescription = "TestCCPAnnotationSetDescription"
		testCCPAnnotationSet = new CCPAnnotationSet(jcas);
		testCCPAnnotationSet.setAnnotationSetID(10);
		testCCPAnnotationSet.setAnnotationSetName("TestCCPAnnotationSet");
		testCCPAnnotationSet.setAnnotationSetDescription("TestCCPDescription");

		// Annotator: ID = 3, FirstName = "TestAnnotatorFirst", LastName =
		// "TestAnnotatorLast", Affiliation = "TestAnnotatorAffiliation"
		testAnnotator = new Annotator(new Integer(3), "TestAnnotatorFirst", "TestAnnotatorLast", "TestAnnotatorAffiliation");

		// CCPAnnotator: ID = 5, FirstName = "TestCCPFirst", LastName =
		// "TestCCPLast", Affiliation = "TestCCPAffiliation"
		testCCPAnnotator = new CCPAnnotator(jcas);
		testCCPAnnotator.setAnnotatorID(5);
		testCCPAnnotator.setFirstName("TestCCPFirst");
		testCCPAnnotator.setLastName("TestCCPLast");
		testCCPAnnotator.setAffiliation("TestCCPAffiliation");

		// CCPClassMention: mentionName = "testCCPClassMention"
		testCCPClassMention = new CCPClassMention(jcas);
		testCCPClassMention.setMentionName("testCCPClassMention");

		FSArray ccpAnnotationSets = new FSArray(jcas, 1);
		ccpAnnotationSets.set(0, testCCPAnnotationSet);

		FSArray ccpSpans = new FSArray(jcas, 2);
		CCPSpan ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(11);
		ccpSpan.setSpanEnd(21);
		ccpSpans.set(0, ccpSpan);

		ccpSpan = new CCPSpan(jcas);
		ccpSpan.setSpanStart(44);
		ccpSpan.setSpanEnd(56);
		ccpSpans.set(1, ccpSpan);

		CCPClassMention ccpClassMention = new CCPClassMention(jcas);
		ccpClassMention.setMentionName("ccpClassMentionName");
		CCPStringSlotMention ncsm = new CCPStringSlotMention(jcas);
		ncsm.setMentionName("NCSM Mention Name");
		StringArray valuesArray = new StringArray(jcas, 2);
		valuesArray.set(0, "value1");
		valuesArray.set(1, "value2");
		ncsm.setSlotValues(valuesArray);
		FSArray slotMentions = new FSArray(jcas, 1);
		slotMentions.set(0, ncsm);
		ccpClassMention.setSlotMentions(slotMentions);

		testCCPTextAnnotation1 = new CCPTextAnnotation(jcas);
		testCCPTextAnnotation1.setAnnotationID(4567);
		testCCPTextAnnotation1.setAnnotationSets(ccpAnnotationSets);
		testCCPTextAnnotation1.setAnnotator(testCCPAnnotator);
		testCCPTextAnnotation1.setBegin(11);
		testCCPTextAnnotation1.setEnd(56);
		testCCPTextAnnotation1.setSpans(ccpSpans);
		testCCPTextAnnotation1.setDocumentSectionID(2);
		testCCPTextAnnotation1.setNumberOfSpans(2);
		testCCPTextAnnotation1.setClassMention(ccpClassMention);

		/**
		 * TextAnnotation: <br>
		 * nuclear gated transport <br>
		 * --- transport origin ------- nucleus --- transport location ------- nucleus --- transport participants
		 * ------- e2f4 --- transported entities ------- e2f4
		 */

		// ===== testTextAnnotation1 =====
		/* ClassMention: gated nuclear transport */
		DefaultClassMention transportMention = new DefaultClassMention("gated nuclear transport");
		DefaultComplexSlotMention transportOriginMention = new DefaultComplexSlotMention("transport origin");

		/* ------- ClassMention nucleus */
		DefaultClassMention nucleusMention = new DefaultClassMention("nucleus");
		transportOriginMention.addClassMention(nucleusMention);
		@SuppressWarnings("unused")
		TextAnnotation nucleusAnnotation = new DefaultTextAnnotation(45, 52, "nuclear", testAnnotator, testAnnotationSet, -1, 2, "14635",
				0, nucleusMention);
		transportMention.addComplexSlotMention(transportOriginMention);

		/* --- Complex Slot Mention: transport location ------- nucleus */
		DefaultComplexSlotMention transportLocationMention = new DefaultComplexSlotMention("transport location");
		transportLocationMention.addClassMention(nucleusMention);
		transportMention.addComplexSlotMention(transportLocationMention);

		/* --- Complex Slot Mention: transport participants */
		DefaultComplexSlotMention transportParticipantsMention = new DefaultComplexSlotMention("transport participants");

		/* ------- ClassMention: e2f4 protein */
		DefaultClassMention e2f4ProteinMention = new DefaultClassMention("protein");
		IntegerSlotMention entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(999999);
		e2f4ProteinMention.addPrimitiveSlotMention(entrezIDSlotMention);
		transportParticipantsMention.addClassMention(e2f4ProteinMention);
		@SuppressWarnings("unused")
		TextAnnotation e2f4Annotation = new DefaultTextAnnotation(63, 68, "E2F-4", testAnnotator, testAnnotationSet, -1, 2, "14635", 0,
				e2f4ProteinMention);
		transportMention.addComplexSlotMention(transportParticipantsMention);

		/* --- Complex Slot Mention: transported entities ------- e2f4 protein */
		DefaultComplexSlotMention transportedEntitiesMention = new DefaultComplexSlotMention("transported entities");
		transportedEntitiesMention.addClassMention(e2f4ProteinMention);
		transportMention.addComplexSlotMention(transportedEntitiesMention);
		testTextAnnotation1 = new DefaultTextAnnotation(15, 37, "coveredText1", testAnnotator, testAnnotationSet, 1010, 2, "33", 1,
				transportMention);

		/* ===== testTextAnnotation2 ===== */
		/* Class Mention - EBV LMP1 protein */
		DefaultClassMention proteinMention = new DefaultClassMention("protein");
		entrezIDSlotMention = new DefaultIntegerSlotMention("entrez_gene_id");
		entrezIDSlotMention.addSlotValue(888888);
		proteinMention.addPrimitiveSlotMention(entrezIDSlotMention);
		testTextAnnotation2 = new DefaultTextAnnotation(0, 8, "EBV LMP1", testAnnotator, testAnnotationSet, 1011, 2, "33", 1,
				proteinMention);

		// System.err.println("TEST ANNOTATIONS:");
		// System.err.println("testCCPTextAnnotation1 #################################################:");
		// UIMA_Util.printCCPTextAnnotation(testCCPTextAnnotation1, System.err);
		//
		// System.err.println("testTextAnnotation1 #################################################:");
		// testTextAnnotation1.printAnnotation(System.err);
		// System.err.println("testTextAnnotation2 #################################################:");
		// testTextAnnotation2.printAnnotation(System.err);

	}

	private JCas initializeJCas(String documentID, String documentText) throws Exception {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(tsd);
		jcas.setDocumentText(documentText);
		UIMA_Util.setDocumentID(jcas, documentID);
		return jcas;
	}

	/**
	 * Test the conversion of a UIMA AnnotationMetadata to its non-UIMA corollary
	 * 
	 */
	@Test
	public void testSwapAnnotationMetadata() {
		final String dmapPattern = "pattern := this is an opendmap pattern";
		final String comment = "this is an annotation comment";
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata annotationMetadata = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata();
		edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty tpProp = new edu.ucdenver.ccp.nlp.core.annotation.metadata.TruePositiveProperty();
//		edu.ucdenver.ccp.nlp.core.annotation.metadata.OpenDMAPPatternProperty dmapProp = new edu.ucdenver.ccp.nlp.core.annotation.metadata.OpenDMAPPatternProperty();
		edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty commentProp = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationCommentProperty(comment);
		
//		dmapProp.setPattern(dmapPattern);
		annotationMetadata.addMetadataProperty(tpProp);
//		annotationMetadata.addMetadataProperty(dmapProp);
		annotationMetadata.addMetadataProperty(commentProp);

		assertEquals(2, annotationMetadata.getMetadataProperties().size());

		edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata ccpAnnotationMetadata = new edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata(
				jcas);
		UIMA_Util.swapAnnotationMetadata(annotationMetadata, ccpAnnotationMetadata, jcas);

		assertEquals(2, ccpAnnotationMetadata.getMetadataProperties().size());
		FSArray ccpMetadataProperties = ccpAnnotationMetadata.getMetadataProperties();
		boolean hasTPProp = false;
//		boolean hasDMAPProp = false;
		boolean hasCommentProp = false;
		for (int i = 0; i < ccpMetadataProperties.size(); i++) {
			if (ccpMetadataProperties.get(i) instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.TruePositiveProperty) {
				hasTPProp = true;
			}
//			if (ccpMetadataProperties.get(i) instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty) {
//				hasDMAPProp = true;
//				edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty ccpDmapProp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.OpenDMAPPatternProperty) ccpMetadataProperties
//						.get(i);
//				assertEquals(dmapProp.getPattern(), ccpDmapProp.getPattern());
//			}
			if (ccpMetadataProperties.get(i) instanceof edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) {
				hasCommentProp = true;
				edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty ccpCommentProp = (edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationCommentProperty) ccpMetadataProperties
				.get(i);
		assertEquals(commentProp.getComment(), ccpCommentProp.getComment());
			}
		}
		assertTrue(hasTPProp);
//		assertTrue(hasDMAPProp);
		assertTrue(hasCommentProp);

		/* Now swap from UIMA to non-UIMa */
		annotationMetadata = new edu.ucdenver.ccp.nlp.core.annotation.metadata.AnnotationMetadata();
		assertEquals(0, annotationMetadata.getMetadataProperties().size());
		UIMA_Util.swapAnnotationMetadata(ccpAnnotationMetadata, annotationMetadata, jcas);
		assertTrue(annotationMetadata.isTruePositive());
		assertFalse(annotationMetadata.isFalseNegative());
		assertFalse(annotationMetadata.isFalsePositive());
//		assertEquals(dmapPattern, annotationMetadata.getOpenDMAPPattern());
		assertEquals(comment, annotationMetadata.getAnnotationComment());
		

	}

	/**
	 * Test the conversion of an AnnotationSet to its UIMA corollary
	 * 
	 */
	@Test
	public void testSwapAnnotationSet2CCPAnnotationSet() {
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		UIMA_Util.swapAnnotationSetInfo(testAnnotationSet, ccpAnnotationSet);

		assertEquals(testAnnotationSet.getAnnotationSetID().intValue(), ccpAnnotationSet.getAnnotationSetID());
		assertEquals(testAnnotationSet.getAnnotationSetName(), ccpAnnotationSet.getAnnotationSetName());
		assertEquals(testAnnotationSet.getAnnotationSetDescription(), ccpAnnotationSet.getAnnotationSetDescription());
	}

	/**
	 * Test the conversion of a UIMA AnnotationSet (CCPAnnotationSet) to its non-UIMA corrollary
	 * 
	 */
	@Test
	public void testSwapCCPAnnotationSet2AnnotationSet() {
		AnnotationSet annotationSet = new AnnotationSet(new Integer(-1), "", "");
		UIMA_Util.swapAnnotationSetInfo(testCCPAnnotationSet, annotationSet);

		assertEquals(testCCPAnnotationSet.getAnnotationSetID(), annotationSet.getAnnotationSetID().intValue());
		assertEquals(testCCPAnnotationSet.getAnnotationSetName(), annotationSet.getAnnotationSetName());
		assertEquals(testCCPAnnotationSet.getAnnotationSetDescription(), annotationSet.getAnnotationSetDescription());
	}

	/**
	 * Test the conversion of an Annotator to its UIMA corollary
	 * 
	 */
	@Test
	public void testSwapAnnotator2CCPAnnotator() {
		CCPAnnotator ccpAnnotator = new CCPAnnotator(jcas);
		UIMA_Util.swapAnnotatorInfo(testAnnotator, ccpAnnotator);

		assertEquals(testAnnotator.getAnnotatorID().intValue(), ccpAnnotator.getAnnotatorID());
		assertEquals(testAnnotator.getFirstName(), ccpAnnotator.getFirstName());
		assertEquals(testAnnotator.getLastName(), ccpAnnotator.getLastName());
		assertEquals(testAnnotator.getAffiliation(), ccpAnnotator.getAffiliation());
	}

	/**
	 * Test the conversion of a UIMA Annotator (CCPAnnotator) to its non-UIMA corollary
	 * 
	 */
	@Test
	public void testSwapCCPAnnotator2Annotator() {
		Annotator annotator = new Annotator(new Integer(-1), "", "", "");
		UIMA_Util.swapAnnotatorInfo(testCCPAnnotator, annotator);

		assertEquals(testCCPAnnotator.getAnnotatorID(), annotator.getAnnotatorID().intValue());
		assertEquals(testCCPAnnotator.getFirstName(), annotator.getFirstName());
		assertEquals(testCCPAnnotator.getLastName(), annotator.getLastName());
		assertEquals(testCCPAnnotator.getAffiliation(), annotator.getAffiliation());
	}

	/**
	 * Test the transfer of information from one UIMA TextAnnotation (CCPTextAnnotation) to another
	 * 
	 */
	@Test
	public void testSwapCCPTextAnnotation2CCPTextAnnotation() {
		CCPTextAnnotation swapToAnnotation = new CCPTextAnnotation(jcas);
		try {
			UIMA_Util.swapAnnotationInfo(testCCPTextAnnotation1, swapToAnnotation);
		} catch (CASException ce) {
			ce.printStackTrace();
			fail("CASException while swapping annotation info");
		}

		assertEquals(testCCPTextAnnotation1.getAnnotationID(), swapToAnnotation.getAnnotationID());
		assertEquals(testCCPTextAnnotation1.getBegin(), swapToAnnotation.getBegin());
		assertEquals(testCCPTextAnnotation1.getEnd(), swapToAnnotation.getEnd());
		assertEquals(testCCPTextAnnotation1.getDocumentSectionID(), swapToAnnotation.getDocumentSectionID());
		assertEquals(testCCPTextAnnotation1.getNumberOfSpans(), swapToAnnotation.getNumberOfSpans());
		assertEquals(testCCPTextAnnotation1.getSpans().size(), swapToAnnotation.getSpans().size());
		assertEquals(testCCPTextAnnotation1.getClassMention().getMentionName(), swapToAnnotation.getClassMention().getMentionName());
		assertEquals(testCCPTextAnnotation1.getClassMention().getSlotMentions().size(), swapToAnnotation.getClassMention()
				.getSlotMentions().size());
	}

	/**
	 * Test the inter-conversion of TextAnnotations and CCPTextAnnotations
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSwapAnnotationInfo() throws Exception {
		CCPTextAnnotation emptyCCPTA = new CCPTextAnnotation(jcas);
		/* put the contents of the transport annotation into this empty CCPTextAnnotation */
		// System.err.println("### FROM: ");
		// testTextAnnotation1.printAnnotation(System.err);
		UIMA_Util.swapAnnotationInfo(testTextAnnotation1, emptyCCPTA, jcas);

		// System.err.println("### TO: ");
		// UIMA_Util.printCCPTextAnnotation(emptyCCPTA, System.err);

		assertEquals(testTextAnnotation1.getAnnotationID(), emptyCCPTA.getAnnotationID());
		assertEquals(testTextAnnotation1.getAnnotationSpanStart(), emptyCCPTA.getBegin());
		assertEquals(testTextAnnotation1.getAnnotationSpanEnd(), emptyCCPTA.getEnd());
		assertEquals(testTextAnnotation1.getDocumentSectionID(), emptyCCPTA.getDocumentSectionID());
		assertEquals(testTextAnnotation1.getSpans().size(), emptyCCPTA.getNumberOfSpans());
		assertEquals(testTextAnnotation1.getSpans().size(), emptyCCPTA.getSpans().size());

		/* ensure that the spans are the same */
		Set<String> expectedSpans = new HashSet<String>();
		for (Span span : testTextAnnotation1.getSpans()) {
			expectedSpans.add(span.toString());
		}

		Set<String> retrievedSpans = new HashSet<String>();
		FSArray ccpSpans = emptyCCPTA.getSpans();
		for (int i = 0; i < ccpSpans.size(); i++) {
			CCPSpan ccpSpan = (CCPSpan) ccpSpans.get(i);
			retrievedSpans.add("[" + ccpSpan.getSpanStart() + ".." + ccpSpan.getSpanEnd() + "]");
		}

		assertEquals(expectedSpans, retrievedSpans);

		// System.err.println("CONVERTED UIMA TA");
		// UIMA_Util.printCCPTextAnnotation(emptyCCPTA, System.err);

		// System.err.println("SWAPPING TO REGULAR TA...");
		/* now test the conversion from the UIMA TextAnnotation to a non-UIMA TextAnnotation */
		TextAnnotation emptyTA = new DefaultTextAnnotation(0, 1, "nocoveredText", new Annotator(-1, "noname", "noname", "noaffiliation"),
				new AnnotationSet(-1, "nonameset", "nonameset"), -1, -1, "-1", -1, new DefaultClassMention("nonamemention"));
		UIMA_Util.swapAnnotationInfo(emptyCCPTA, emptyTA, jcas);
		// System.err.println("DONE SWAPPING TO REGULAR TA...");
		// System.err.println("REGULAR TA: ");
		// emptyTA.printAnnotation(System.err);
		assertEquals(emptyCCPTA.getAnnotationID(), emptyTA.getAnnotationID());
		assertEquals(emptyCCPTA.getBegin(), emptyTA.getAnnotationSpanStart());
		assertEquals(emptyCCPTA.getEnd(), emptyTA.getAnnotationSpanEnd());
		assertEquals(emptyCCPTA.getDocumentSectionID(), emptyTA.getDocumentSectionID());
		assertEquals(emptyCCPTA.getNumberOfSpans(), emptyTA.getSpans().size());
		assertEquals(emptyCCPTA.getSpans().size(), emptyTA.getSpans().size());

		/* ensure that the spans are the same */
		retrievedSpans = new HashSet<String>();
		for (Span span : emptyTA.getSpans()) {
			retrievedSpans.add(span.toString());
		}

		expectedSpans = new HashSet<String>();
		ccpSpans = emptyCCPTA.getSpans();
		for (int i = 0; i < ccpSpans.size(); i++) {
			CCPSpan ccpSpan = (CCPSpan) ccpSpans.get(i);
			expectedSpans.add("[" + ccpSpan.getSpanStart() + ".." + ccpSpan.getSpanEnd() + "]");
		}

		assertEquals(expectedSpans, retrievedSpans);

	}

	/**
	 * Test the getSlotMentionByName() method
	 * 
	 * @throws CASException
	 */
	@Test
	public void testGetSlotMentionByName() throws CASException {
		// if the slotMention does not exist, it should return null
		assertNull(UIMA_Util.getSlotMentionByName(testCCPClassMention, "slotMentionName"));

		// add a slot value - and thereby add a slot
		UIMA_Util.addSlotValue(testCCPClassMention, "slotMentionName", "slotValue");

		// it should now return a slot mention
		CCPStringSlotMention ccpNonComplexSlotMention = (CCPStringSlotMention) UIMA_Util.getSlotMentionByName(testCCPClassMention,
				"slotMentionName");
		assertNotNull(ccpNonComplexSlotMention);

		// and the value of the returned slot should equal "slotValue"
		assertEquals("slotValue", UIMA_Util.getFirstSlotValue(ccpNonComplexSlotMention));

		// add another slot value
		UIMA_Util.addSlotValue(testCCPClassMention, "slotMentionName", "slotValue2");
		// and now there should be two slot values
		assertEquals(2, ccpNonComplexSlotMention.getSlotValues().size());

		// add another slot value - and thereby add a new slot
		UIMA_Util.addSlotValue(testCCPClassMention, "slot2MentionName", "slot2Value");
		assertEquals(2, testCCPClassMention.getSlotMentions().size());
	}

	/**
	 * Test the insertion and extraction of TextAnnotations into the CAS
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void test_Util_UIMA_Interface() throws Exception {
		// add test TextAnnotations to a new list
		List<TextAnnotation> textAnnotations = new ArrayList<TextAnnotation>();
		textAnnotations.add(testTextAnnotation1);
		textAnnotations.add(testTextAnnotation2);

		// ensure there are no annotations in the JCas
		FSIterator annotationIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotationIter.hasNext()) {
			annotationIter.next();
			fail("The JCas should be empty here as it has just been initialized!!!");
		}

		UIMA_Util uimaUtil = new UIMA_Util();
		uimaUtil.putTextAnnotationsIntoJCas(jcas, textAnnotations);

		// now there should be 4 annotations in the JCas
		// 1. gated nuclear transport
		// 2. nucleus
		// 3. e2f4 protein
		// 4. EBV LMP1
		annotationIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		int count = 0;
		while (annotationIter.hasNext()) {
			annotationIter.next();
			count++;
		}
		assertEquals(4, count);

		// now test to see if we can get the annotations back out of the JCas
		List<TextAnnotation> retrievedTextAnnotations = UIMA_Util.getAnnotationsFromCas(jcas);

		// there should be four annotations returned
		assertEquals(4, retrievedTextAnnotations.size());

		// for (TextAnnotation ta : retrievedTextAnnotations) {
		// ta.printAnnotation(System.out);
		// }

	}

	// /**
	// * Test the method that links a CCPClassMention with a CCPTextAnnotation
	// *
	// */
	// @Test
	// public void testAddCCPTextAnnotationToCCPClassMention() {
	// testCCPClassMention.setCcpTextAnnotations(null);
	// assertNull(testCCPClassMention.getCcpTextAnnotations());
	//
	// try {
	// UIMA_Util.addCCPTextAnnotationToCCPClassMention(testCCPTextAnnotation1, testCCPClassMention);
	// } catch (CASException e) {
	// e.printStackTrace();
	// fail("Exception while testing the addition of a CCPTextAnnotation to a CCPClassMention...");
	// }
	// assertNotNull(testCCPClassMention.getCcpTextAnnotations());
	//
	// }

	/**
	 * This test exercises the methods designed to detect invalud mention structures.<br>
	 * validateCCPTextAnnotation()<br>
	 * validateCCPClassMention()<br>
	 * validateCCPSlotMention()<br>
	 * validateCCPNonComplexSlotMention()<br>
	 * validateCCPComplexSlotMention()<br>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMentionStructureValidationCode() throws Exception {
		List<TextAnnotation> textAnnotations = new ArrayList<TextAnnotation>();
		textAnnotations.add(testTextAnnotation1);
		textAnnotations.add(testTextAnnotation2);

		UIMA_Util uimaUtil = new UIMA_Util();
		uimaUtil.putTextAnnotationsIntoJCas(jcas, textAnnotations);

		/* test that properly constructed CCPTextAnnotations return true */
		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			assertTrue(UIMA_Util.validateCCPTextAnnotation(ccpTA));
			if (ccpTA.getClassMention().getMentionName().equals("gated nuclear transport")) {
				testCCPTextAnnotation1 = ccpTA;
			}
		}

		/*
		 * for testCCPTextAnnotation1, make the mention structure invalid by placing the nucleus mention in the
		 * transport slotmention list
		 */
		CCPComplexSlotMention originSlot = (CCPComplexSlotMention) UIMA_Util.getSlotMentionByName(testCCPTextAnnotation1,
				"transport origin");
		FSArray originSlotValues = originSlot.getClassMentions();
		testCCPTextAnnotation1.getClassMention().getSlotMentions().set(0, originSlotValues);

		assertFalse(UIMA_Util.validateCCPTextAnnotation(testCCPTextAnnotation1));
		logger.info("ERROR EXPECTED HERE.");
		CCPClassMention nucleusMention = (CCPClassMention) originSlotValues.get(0);
		testCCPTextAnnotation1.getClassMention().getSlotMentions().set(0, nucleusMention);
		assertFalse(UIMA_Util.validateCCPTextAnnotation(testCCPTextAnnotation1));
		logger.info("ERROR EXPECTED HERE.");
		/* now put it back to normal */
		testCCPTextAnnotation1.getClassMention().getSlotMentions().set(0, originSlot);
		assertTrue(UIMA_Util.validateCCPTextAnnotation(testCCPTextAnnotation1));

	}

	@Test
	public void testGetComplexSlotValues() throws Exception {
		final String SAME_AS_SLOT = "sameAs";
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
		JCas jcas = JCasFactory.createJCas(typeSystemDescription);
		/*
		 * ______________________________1_________2_________3_________4_________5_________6_________7_________8_________9
		 * /___________________
		 * 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567
		 */
		jcas.setDocumentText("This test mimics a coreference annotation. A is the same as B is the same as C is the same as D.");
		CCPClassMention ccpCM = new CCPClassMention(jcas);
		ccpCM.setMentionName("mainCM");
		CCPTextAnnotation ccpTA_A = UIMA_Annotation_Util.createCCPTextAnnotation("class-A", new int[] { 43, 44 }, jcas);
		CCPTextAnnotation ccpTA_B = UIMA_Annotation_Util.createCCPTextAnnotation("class-B", new int[] { 60, 61 }, jcas);
		CCPTextAnnotation ccpTA_C = UIMA_Annotation_Util.createCCPTextAnnotation("class-C", new int[] { 77, 78 }, jcas);
		CCPTextAnnotation ccpTA_D = UIMA_Annotation_Util.createCCPTextAnnotation("class-D", new int[] { 94, 95 }, jcas);

		List<CCPClassMention> slotValues = UIMA_Util.getComplexSlotValues(ccpTA_A, SAME_AS_SLOT);
		assertEquals(0, slotValues.size());

		UIMA_Util.addSlotValue(ccpTA_A.getClassMention(), SAME_AS_SLOT, ccpTA_B.getClassMention());
		slotValues = UIMA_Util.getComplexSlotValues(ccpTA_A, SAME_AS_SLOT);
		assertEquals(1, slotValues.size());

		UIMA_Util.addSlotValue(ccpTA_A.getClassMention(), SAME_AS_SLOT, ccpTA_C.getClassMention());
		slotValues = UIMA_Util.getComplexSlotValues(ccpTA_A, SAME_AS_SLOT);
		assertEquals(2, slotValues.size());

		UIMA_Util.addSlotValue(ccpTA_A.getClassMention(), SAME_AS_SLOT, ccpTA_D.getClassMention());
		slotValues = UIMA_Util.getComplexSlotValues(ccpTA_A, SAME_AS_SLOT);
		assertEquals(3, slotValues.size());

	}

	@Test
	public void testMentionTraversalIDs() throws Exception {
		UUID traversalId = UUID.randomUUID();
		UUID mentionId = UUID.randomUUID();
		UUID traversalId2 = UUID.randomUUID();
		UUID mentionId2 = UUID.randomUUID();
		
		assertNull(UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId));

		UIMA_Util.setMentionIDForTraversal(testCCPClassMention, mentionId, traversalId, jcas);
		assertEquals(mentionId, UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId));
		UIMA_Util.setMentionIDForTraversal(testCCPClassMention, mentionId2, traversalId2, jcas);
		assertEquals(mentionId, UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId));
		assertEquals(mentionId2, UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId2));

		UIMA_Util.removeMentionIDForTraversal(testCCPClassMention, traversalId2, jcas);
		assertNull(UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId2));

		UUID mentionId3 = UUID.randomUUID();
		UIMA_Util.setMentionIDForTraversal(testCCPClassMention, mentionId3, traversalId, jcas);
		assertEquals(mentionId3, UIMA_Util.getMentionIDForTraversal(testCCPClassMention, traversalId));

	}

	@Test
	public void testArrayUtils() throws Exception {
		IntegerArray intArray = new IntegerArray(jcas, 3);
		intArray.set(0, 0);
		intArray.set(1, 1);
		intArray.set(2, 2);

		IntegerArray updatedArray = UIMA_Util.removeArrayIndex(intArray, 1, jcas);
		assertEquals(2, updatedArray.size());
		assertEquals(0, updatedArray.get(0));
		assertEquals(2, updatedArray.get(1));

		updatedArray = UIMA_Util.removeArrayIndex(intArray, 0, jcas);
		assertEquals(2, updatedArray.size());
		assertEquals(1, updatedArray.get(0));
		assertEquals(2, updatedArray.get(1));

		updatedArray = UIMA_Util.removeArrayIndex(intArray, 2, jcas);
		assertEquals(2, updatedArray.size());
		assertEquals(0, updatedArray.get(0));
		assertEquals(1, updatedArray.get(1));

		updatedArray = UIMA_Util.removeArrayIndex(intArray, 99, jcas);
		assertEquals(3, updatedArray.size());

		updatedArray = UIMA_Util.addToIntegerArray(intArray, 4, jcas);
		assertEquals(4, updatedArray.size());
		assertEquals(2, UIMA_Util.indexOf(intArray, 2));
		
		updatedArray = UIMA_Util.removeArrayIndex(intArray, 0, jcas);
		updatedArray = UIMA_Util.removeArrayIndex(updatedArray, 0, jcas);
		updatedArray = UIMA_Util.removeArrayIndex(updatedArray, 0, jcas);
		assertEquals(0, updatedArray.size());
		

	}

}
