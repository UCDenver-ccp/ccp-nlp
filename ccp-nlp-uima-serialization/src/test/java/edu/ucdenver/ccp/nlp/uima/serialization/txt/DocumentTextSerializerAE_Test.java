package edu.ucdenver.ccp.nlp.uima.serialization.txt;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

public class DocumentTextSerializerAE_Test extends DefaultUIMATestCase {

	private static final String DOCUMENT_TEXT = "This is the document text.";
	private static final String ALTERNATE_DOCUMENT_TEXT = "This is the alternate document text.";
	private static final String DOCUMENT_ID = "123456";

	@Override
	protected void initJCas() throws UIMAException, IOException {
		jcas.setDocumentText(DOCUMENT_TEXT);
		UIMA_Util.setDocumentID(jcas, DOCUMENT_ID);

	}

	@Test
	public void testSaveToOutputDirectory()
			throws ResourceInitializationException, AnalysisEngineProcessException, FileNotFoundException, IOException {
		File outputDirectory = folder.newFolder("output");

		AnalysisEngineDescription aeDesc = DocumentTextSerializerAE.getDescription(getTypeSystem(),
				CcpDocumentMetadataHandler.class, outputDirectory, View.DEFAULT.viewName(), true, ".ttt");

		AnalysisEngine engine = AnalysisEngineFactory.createEngine(aeDesc);
		engine.process(jcas);

		File expectedOutputFile = new File(outputDirectory, DOCUMENT_ID + ".ttt.gz");
		assertTrue(expectedOutputFile.exists());

		List<String> lines = FileReaderUtil.loadLinesFromFile(
				new GZIPInputStream(new FileInputStream(expectedOutputFile)), CharacterEncoding.UTF_8);
		assertEquals(1, lines.size());
		assertEquals(DOCUMENT_TEXT, lines.get(0));
	}

	@Test
	public void testSaveFromAlternateViewToSourceViewParentDirectory() throws ResourceInitializationException,
			AnalysisEngineProcessException, FileNotFoundException, IOException, CASException {
		File sourceFolder = folder.newFolder("source");
		File sourceDocumentFile = new File(sourceFolder, "source.xml");
		JCas view = View_Util.getView(jcas, View.XML.viewName());
		view.setDocumentText(ALTERNATE_DOCUMENT_TEXT);
		UIMA_Util.setSourceDocumentPath(view, sourceDocumentFile);
		UIMA_Util.setDocumentID(view, DOCUMENT_ID);

		AnalysisEngineDescription aeDesc = DocumentTextSerializerAE.getDescription_SaveToSourceFileDirectory(
				getTypeSystem(), CcpDocumentMetadataHandler.class, View.XML.viewName(), View.DEFAULT.viewName(), true,
				".ttt");

		AnalysisEngine engine = AnalysisEngineFactory.createEngine(aeDesc);
		engine.process(jcas);

		File expectedOutputFile = new File(sourceFolder, DOCUMENT_ID + ".ttt.gz");
		assertTrue(expectedOutputFile.exists());

		List<String> lines = FileReaderUtil.loadLinesFromFile(
				new GZIPInputStream(new FileInputStream(expectedOutputFile)), CharacterEncoding.UTF_8);
		assertEquals(1, lines.size());
		assertEquals(DOCUMENT_TEXT, lines.get(0));
	}

}
