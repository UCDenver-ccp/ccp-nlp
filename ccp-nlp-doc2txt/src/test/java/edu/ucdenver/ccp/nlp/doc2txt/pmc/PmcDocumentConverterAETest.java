package edu.ucdenver.ccp.nlp.doc2txt.pmc;

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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

public class PmcDocumentConverterAETest extends DefaultUIMATestCase {

	@Override
	protected void initJCas() throws IOException, CASException {
		String nxml = ClassPathUtil.getContentsFromClasspathResource(getClass(), "/sample_pmc.xml",
				CharacterEncoding.UTF_8);
		JCas xmlView = View_Util.getView(jcas, View.XML);
		xmlView.setDocumentText(nxml);
		UIMA_Util.setDocumentID(xmlView, "12345");

	}

	@Test
	public void testNxmlToTxtConversion()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException, SAXException {
		String expectedPlainText = ClassPathUtil.getContentsFromClasspathResource(getClass(), "/sample.txt",
				CharacterEncoding.UTF_8);

		AnalysisEngineDescription converterDesc = PmcDocumentConverterAE.getDescription(getTypeSystem());
		AnalysisEngine converterEngine = AnalysisEngineFactory.createEngine(converterDesc);
		converterEngine.process(jcas);

		assertNotNull(jcas.getDocumentText());
		assertEquals(expectedPlainText, jcas.getDocumentText());
		assertEquals("12345", UIMA_Util.getDocumentID(jcas));

		/* make sure the CAS annotation count is correct */
		CcpXmlParser parser = new CcpXmlParser();
		parser.parse(new InputSource(this.getClass().getResourceAsStream("/sample_ccp.xml")));
		assertEquals(parser.getAnnotations().size(), JCasUtil.select(jcas, CCPTextAnnotation.class).size());
	}

}
