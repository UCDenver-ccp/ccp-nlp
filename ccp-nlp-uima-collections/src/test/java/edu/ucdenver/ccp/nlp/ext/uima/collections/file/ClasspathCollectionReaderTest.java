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
 */package edu.ucdenver.ccp.nlp.ext.uima.collections.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClasspathCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_CLASSPATH_COLLECTION_PATH = "random/classpath/folder";

	private static final String DOC1_TEXT = "This is sample document 1.";
	private static final String DOC2_TEXT = "This is sample document 2.";
	private static final String DOC3_TEXT = "This is sample document 3.";

	@Test
	public void testClasspathCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

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
	public void testClasspathCollectionReader_SkippingOne() throws UIMAException, IOException {
		int numToSkip = 1;
		int numToProcess = -1;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

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
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertFalse(jCasIterable.hasNext());
	}
	
	@Test
	public void testClasspathCollectionReader_ProcessTwo() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = 2;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}
	
}
