package edu.ucdenver.ccp.nlp.doc2txt.pmc;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2018 Regents of the University of Colorado
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
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.pipelines.log.ProcessingErrorLog;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;

/**
 * populates the CAS with document metadata extracted from the PMC XML
 *
 */
public class PmcMetadataImportAE extends JCasAnnotator_ImplBase {

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
	@ConfigurationParameter(mandatory = false, description = "The name of the CAS View containing the XML to parse", defaultValue = "xmlView")
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

				/* extract metadata elements from the PMC XML here */
				InputSource source = new InputSource(new StringReader(xmlView.getDocumentText()));
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				builderFactory.setValidating(false);
				builderFactory.setNamespaceAware(true);
				builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
				builderFactory.setFeature("http://xml.org/sax/features/validation", false);
				builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
				builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				Document xmlDocument = builder.parse(source);
				XPath xPath = XPathFactory.newInstance().newXPath();
//				String yearPublishedExpression = "/article/front/article-meta/pub-date/year";
//				String monthPublishedExpression = "/article/front/article-meta/pub-date/month";
//				int year = Integer.parseInt(
//						xPath.compile(yearPublishedExpression).evaluate(xmlDocument, XPathConstants.STRING).toString());
//				int month = Integer.parseInt(xPath.compile(monthPublishedExpression)
//						.evaluate(xmlDocument, XPathConstants.STRING).toString());

				/* if multiple years are present, find the earliest publication year */
				int year = 9999;
				int month = 12;
				NodeList years = (NodeList) xPath.evaluate("/article/front/article-meta/pub-date",xmlDocument, XPathConstants.NODESET);
				for (int i = 0; i < years.getLength();i++) {
					Node pubDateNode = years.item(i);
					String yearStr = xPath.evaluate("year", pubDateNode);
					String monthStr = xPath.evaluate("month", pubDateNode);
					
					int y = Integer.parseInt(yearStr);
					if (y <= year) {
						year = y;
						if (monthStr != null && !monthStr.trim().isEmpty()) {
							int m = Integer.parseInt(monthStr);
							if (m < month) {
								month = m;
							}
						} else {
							month = 0;
						}
					}
				}
				
				
				UIMA_Util.setYearPublished(jCas, year);
				UIMA_Util.setMonthPublished(jCas, month);

//				logger.log(Level.INFO, "$$$$$$$$$$$$$$$$$$$$$$$$ DOCUMENT MONTH/YEAR = " + month + "/" + year);
				
				UIMA_Util.setDocumentID(jCas, documentId);

			} else {
				String errorMessage = "XML View does not exist in CAS for document: " + UIMA_Util.getDocumentID(jCas)
						+ ". Cannot populate the document metadata "
						+ " because expected XML is not present in the CAS.";
				ProcessingErrorLog errorLog = new ProcessingErrorLog(jCas);
				errorLog.setErrorMessage(errorMessage);
				errorLog.setComponentAtFault(this.getClass().getName());
				errorLog.addToIndexes();
				logger.log(Level.WARNING, errorMessage);
			}
		} catch (CASException | IOException | SAXException | ParserConfigurationException | NumberFormatException
				| XPathExpressionException e) {
			/*
			 * an error has occurred during the XML processing, so we log the
			 * error in the CAS so that downstream AEs can handle the document
			 * as is appropriate
			 */
			ProcessingErrorLog errorLog = new ProcessingErrorLog(jCas);
			errorLog.setErrorMessage(e.getMessage());
			errorLog.setStackTrace(ExceptionUtils.getStackTrace(e));
			errorLog.setComponentAtFault(this.getClass().getName());
			errorLog.addToIndexes();
			logger.log(Level.WARNING, "Error during XML metadata extraction for document: "
					+ UIMA_Util.getDocumentID(jCas) + " -- " + e.getMessage());
		}

	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription pipelineTypeSystem,
			CharacterEncoding xmlEncoding, String viewName) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(PmcMetadataImportAE.class, pipelineTypeSystem,
				PARAM_XML_ENCODING, xmlEncoding.getCharacterSetName(), PARAM_XML_VIEW_NAME, viewName);
	}

}
