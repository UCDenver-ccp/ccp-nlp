package edu.ucdenver.ccp.nlp.uima.collections.file;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClasspathCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_CLASSPATH_COLLECTION_PATH = "random" + File.separator +"classpath" + File.separator +"folder";

	private static final String DOC1_TEXT = "This is sample document 1.";
	private static final String DOC2_TEXT = "This is sample document 2.";
	private static final String DOC3_TEXT = "This is sample document 3.";

	@Test
	public void testClasspathCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1;
		CollectionReaderDescription cr = ClasspathCollectionReader.createCollectionReaderDescription(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterator jCasIterable = new JCasIterable(cr).iterator();

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
	public void testClasspathCollectionReader_SkippingOne() throws UIMAException, IOException {
		int numToSkip = 1;
		int numToProcess = -1;
		CollectionReaderDescription cr = ClasspathCollectionReader.createCollectionReaderDescription(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterator jCasIterable = new JCasIterable(cr).iterator();

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC3_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testClasspathCollectionReader_SkippingAll() throws UIMAException, IOException {
		int numToSkip = 4;
		int numToProcess = -1;
		CollectionReaderDescription cr = ClasspathCollectionReader.createCollectionReaderDescription(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterator jCasIterable = new JCasIterable(cr).iterator();

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testClasspathCollectionReader_ProcessTwo() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = 2;
		CollectionReaderDescription cr = ClasspathCollectionReader.createCollectionReaderDescription(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterator jCasIterable = new JCasIterable(cr).iterator();

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

}
