package edu.ucdenver.ccp.nlp.uima.collections.file.medline;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.medline.parser.MedlineXmlDeserializerTest;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MedlineXmlFileCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_MEDLINE_XML_FILE_NAME = "medsamp2011.xml";
	private File sampleMedlineXmlFile;

	@Before
	public void setUp() throws IOException {
		sampleMedlineXmlFile = ClassPathUtil.copyClasspathResourceToDirectory(MedlineXmlDeserializerTest.class,
				SAMPLE_MEDLINE_XML_FILE_NAME, folder.getRoot());
	}

	@Test
	public void testMedlineXmlCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReader cr = MedlineXmlFileCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				sampleMedlineXmlFile, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		int count = 1;
		while (jCasIterable.hasNext() && count < 11) {
			String documentText = String.format("Title %d\nAbstract %d", count, count);
			if (count == 8)
				documentText = "Title 8\nAbstract 8a\nAbstract 8b\nAbstract 8c\nAbstract 8d\nAbstract 8e\nAbstract 8f\nAbstract 8g";
			JCas jCas = jCasIterable.next();
			assertEquals(documentText, jCas.getDocumentText());
			count++;
		}

		assertFalse(jCasIterable.hasNext());
	}

}
