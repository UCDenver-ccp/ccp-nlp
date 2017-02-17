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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser.Annotation;
import edu.ucdenver.ccp.nlp.doc2txt.XslUtil;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

/**
 * Looks for PubMed Central NXML in the XML view and populates the default UIMA
 * view with a plain text extracted from the nxml. Annotations for documents
 * sections, etc. are also added to the CAS.
 *
 */
public class PmcDocumentConverterAE extends JCasAnnotator_ImplBase {

	/* ==== XML encoding configuration ==== */
	/**
	 * The character encoding to use when parsing the XML file
	 */
	public static final String PARAM_XML_ENCODING = "xmlEncoding";
	@ConfigurationParameter(mandatory = false, description = "The character encoding to use when parsing the XML file", defaultValue = "UTF-8")
	private String xmlEncoding;

	/* ==== XML view configuration ==== */
	/**
	 * The name of the CAS View containing the XML to parse
	 */
	public static final String PARAM_XML_VIEW_NAME = "xmlViewName";
	@ConfigurationParameter(mandatory = false, description = "The name of the CAS View containing the XML to parse", defaultValue = "XML")
	private String xmlViewName;

	private Logger logger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			if (View_Util.viewExists(jCas, xmlViewName)) {
				JCas xmlView = View_Util.getView(jCas, xmlViewName);
				String documentId = UIMA_Util.getDocumentID(xmlView);
				InputStream xmlStream = IOUtils.toInputStream(xmlView.getDocumentText(), xmlEncoding);
				String ccpXml = XslUtil.applyPmcXslt(xmlStream);

				/*
				 * convert CCP XML to plain text and add annotations for
				 * document sections, etc.
				 */
				CcpXmlParser parser = new CcpXmlParser();
				String plainText = parser.parse(ccpXml);
				jCas.setDocumentText(plainText);
				UIMA_Util.setDocumentID(jCas, documentId);

				for (CcpXmlParser.Annotation annot : parser.getAnnotations()) {
					importAnnotationIntoCas(annot, jCas);
				}

			} else {
				logger.log(Level.WARNING, "XML View does not exist in CAS. Cannot populate the default "
						+ "view with plain text because expected XML is not present in the CAS.");
			}
		} catch (CASException | IOException | SAXException | ParserConfigurationException | TransformerException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

	private void importAnnotationIntoCas(Annotation annot, JCas jCas) {
		CCPTextAnnotation ccpTa = UIMA_Annotation_Util.createCCPTextAnnotation(annot.getType().name(), annot.getStart(),
				annot.getEnd(), jCas);
		Annotator annotator = new Annotator(-1, "PMC XML", "", "");
		UIMA_Annotation_Util.setAnnotator(ccpTa, annotator, jCas);
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(PmcDocumentConverterAE.class, tsd);
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd, CharacterEncoding xmlEncoding,
			String xmlViewName) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(PmcDocumentConverterAE.class, tsd, PARAM_XML_ENCODING,
				xmlEncoding.getCharacterSetName(), PARAM_XML_VIEW_NAME, xmlViewName);
	}

}
