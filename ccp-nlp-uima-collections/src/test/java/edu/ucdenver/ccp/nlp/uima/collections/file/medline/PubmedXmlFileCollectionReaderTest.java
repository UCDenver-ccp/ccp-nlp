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
package edu.ucdenver.ccp.nlp.uima.collections.file.medline;

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
import edu.ucdenver.ccp.medline.parser.PubmedXmlDeserializerTest;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PubmedXmlFileCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_PUBMED_XML_FILE_NAME = "pmsamp2011.xml";
	private File samplePubmedXmlFile;

	@Before
	public void setUp() throws IOException {
		samplePubmedXmlFile = ClassPathUtil.copyClasspathResourceToDirectory(PubmedXmlDeserializerTest.class,
				SAMPLE_PUBMED_XML_FILE_NAME, folder.getRoot());
	}

	@Test
	public void testPubmedXmlCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReader cr = PubmedXmlFileCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				samplePubmedXmlFile, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		int count = 1;
		while (jCasIterable.hasNext()) {
			String documentText = String.format("Title %d\n\nAbstract %d", count, count);
			if (count == 8)
				documentText = "Title 8\n\nBACKGROUND: Abstract 8a\nOBJECTIVE: Abstract 8b\nMETHODS: Abstract 8c\nMETHODS: Abstract 8d\nMETHODS: Abstract 8e\nRESULTS: Abstract 8f\nCONCLUSIONS: Abstract 8";
			if (count == 11)
				documentText = "Book Title 1\n\nBook Abstract 1a\n\nBook Abstract 1b\n\nBook Abstract 1c\n\nBook Abstract 1d";
			JCas jCas = jCasIterable.next();
			assertEquals(documentText, jCas.getDocumentText());
			count++;
		}

		assertFalse(jCasIterable.hasNext());
	}

}
