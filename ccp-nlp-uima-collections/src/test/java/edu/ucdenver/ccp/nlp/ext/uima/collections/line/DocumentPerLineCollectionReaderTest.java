/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DocumentPerLineCollectionReaderTest extends DefaultTestCase {

	private static final String DOC1_TEXT = "doc1Text";
	private static final String DOC2_TEXT = "doc2Text";
	private static final String DOC3_TEXT = "doc3Text";

	@Test
	public void testCollectionReader() throws IOException, UIMAException {
		File collectionFile = createSampleCollectionFile();
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReader cr = DocumentPerLineCollectionReader
				.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(), collectionFile, numToSkip, numToProcess,
						TabDocumentExtractor.class, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC3_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testCollectionReader_LimitingNumberProcessed() throws IOException, UIMAException {
		File collectionFile = createSampleCollectionFile();
		int numToSkip = 0;
		int numToProcess = 1; // process one
		CollectionReader cr = DocumentPerLineCollectionReader
				.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(), collectionFile, numToSkip, numToProcess,
						TabDocumentExtractor.class, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testCollectionReader_SkippingOneAndLimitingNumberProcessed() throws IOException, UIMAException {
		File collectionFile = createSampleCollectionFile();
		int numToSkip = 1;
		int numToProcess = 1; // process one
		CollectionReader cr = DocumentPerLineCollectionReader
				.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(), collectionFile, numToSkip, numToProcess,
						TabDocumentExtractor.class, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testCollectionReader_SkippingAll() throws IOException, UIMAException {
		File collectionFile = createSampleCollectionFile();
		int numToSkip = 3;
		int numToProcess = 1; // process one
		CollectionReader cr = DocumentPerLineCollectionReader
				.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(), collectionFile, numToSkip, numToProcess,
						TabDocumentExtractor.class, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);
		assertFalse(jCasIterable.hasNext());
	}
	
	/**
	 * Creates a sample collection file that contains 3 valid records (tab-delimited) and some
	 * excess lines that you might get from a SQL query
	 * 
	 * @return
	 * @throws IOException
	 */
	private File createSampleCollectionFile() throws IOException {
		File collectionFile = folder.newFile("collection.utf8");
		/* @formatter:off */
		List<String> lines = CollectionsUtil.createList(
				"",
				"SQL*Plus: Release 11.2.0.1.0 Production on Tue Nov 8 12:31:43 2011",
				"",	
				"Copyright (c) 1982, 2009, Oracle.  All rights reserved.",
				"",
				"",
				"Connected to:",
				"Oracle Database 11g Release 11.2.0.1.0 - 64bit Production",
				"",
				"PMID COUNT: 20717354",
				"",
				"12305424\t" + DOC1_TEXT,
				"12255535\t" + DOC2_TEXT,
				"12255683\t" + DOC3_TEXT,
				"",
				"20717354 rows selected.",
				"",
				"Disconnected from Oracle Database 11g Release 11.2.0.1.0 - 64bit Production");
		/* @formatter:on */
		FileWriterUtil.printLines(lines, collectionFile, CharacterEncoding.UTF_8);
		return collectionFile;
	}

	public static class TabDocumentExtractor implements DocumentExtractor {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * edu.ucdenver.ccp.nlp.ext.uima.collections.line.DocumentExtractor#extractDocument(java
		 * .lang.String)
		 */
		@Override
		public GenericDocument extractDocument(String line) {
			String[] toks = line.split("\\t");
			if (line.isEmpty() || toks.length < 2) {
				return null;
			}
			GenericDocument gd = new GenericDocument(toks[0]);
			gd.setDocumentText(toks[1]);
			return gd;
		}
	}

}
