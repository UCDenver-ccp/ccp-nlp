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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Logger;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/*
 * reads the RAW view and converts to a different XML that is then put on 
 * the DEFAULT view
 */
public class ViewXMLXSLTConverter_AE extends ViewConverter_AE {

	/**
	 * This parameter specifies the name of the xsl file to use. This file must exist on the
	 * classpath in the same package structure as this class.
	 */
	public static String PARAM_XSLT_FILE_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			ViewXMLXSLTConverter_AE.class, "xsltFileName");
	@ConfigurationParameter(mandatory = true)
	private String xsltFileName;

	public static String PARAM_ENTITY_RESOLVER_CLASS_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(ViewXMLXSLTConverter_AE.class, "entityResolverClassName");
	@ConfigurationParameter(mandatory = true)
	private String entityResolverClassName;

	private static Logger logger;
	private DocumentBuilder documentBuilder;
	private Transformer transformer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		initializeDocumentBuilder();
		initalizeXsltTransformer();

		logger = context.getLogger();
	}

	/**
	 * @throws ResourceInitializationException
	 */
	private void initalizeXsltTransformer() throws ResourceInitializationException {
		InputStream xslStream = null;
		try {
			xslStream = this.getClass().getResourceAsStream(xsltFileName);
			if (xslStream == null) {
				throw new ResourceInitializationException(new IllegalArgumentException(
						"Null xslt file stream when reading:" + xsltFileName));
			}
			Source xsltSource = new StreamSource(xslStream);
			TransformerFactory transFact = TransformerFactory.newInstance();
			transformer = transFact.newTransformer(xsltSource);
		} catch (TransformerConfigurationException e) {
			throw new ResourceInitializationException(e);

		} finally {
			try {
				if (xslStream != null)
					xslStream.close();
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}

	}

	/**
	 * @throws ResourceInitializationException
	 */
	private void initializeDocumentBuilder() throws ResourceInitializationException {
		EntityResolver entityResolver = (EntityResolver) ConstructorUtil.invokeConstructor(entityResolverClassName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			documentBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ResourceInitializationException(e);
		}
		documentBuilder.setEntityResolver(entityResolver);
	}

	@Override
	protected void convertView(JCas sourceView, JCas destinationView) throws AnalysisEngineProcessException {
		String sourceText = sourceView.getDocumentText();
		String destText = convert(sourceText);
		destinationView.setDocumentText(destText);
		String docID = UIMA_Util.getDocumentID(sourceView);
		// logger.log(Level.INFO,"reading from view: " + sourceView.getViewName() + " " +
		// sourceText.length()
		// + " characters, from docID:  " + docID);
		UIMA_Util.setDocumentID(destinationView, docID);
	}

	protected String convert(String input) throws AnalysisEngineProcessException {

		try {
			Document document = documentBuilder.parse(StreamUtil.getEncodingSafeInputStream(new ByteArrayInputStream(
					input.getBytes()), CharacterEncoding.UTF_8));
			Source xmlSource = new DOMSource(document.getDocumentElement());
//			OutputStream outStream = StreamUtil.getEncodingSafeOutputStream(new ByteArrayOutputStream(),
//					CharacterEncoding.UTF_8);
			
			ByteArrayOutputStream outputStream = null;
			ByteArrayInputStream bis = null;
			try {
			outputStream = new ByteArrayOutputStream();
			transformer.transform(xmlSource, new StreamResult(outputStream));
			bis = new ByteArrayInputStream(outputStream.toByteArray());
			String documentText =  StreamUtil.toString(new InputStreamReader(bis, CharacterEncoding.UTF_8.getDecoder()));
			documentText = removeXmlTags(documentText);
			return documentText;
			
			} finally {
				outputStream.close();
				bis.close();
			}
		} catch (TransformerConfigurationException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (TransformerException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * @return
	 */
	private String removeXmlTags(String input) {
		String transformed = input.replaceAll("</?PARAGRAPH>", "\n");
		transformed = transformed.replaceAll("</?SECTION.*?>", "\n");
		transformed = transformed.replaceAll("</?SUBSECTION.*?>", "\n");
		transformed = transformed.replaceAll("</?FIGURE.*?>", "");
		transformed = transformed.replaceAll("</?ITALICS>", "");
		transformed = transformed.replaceAll("</?ABSTRACT.*?>", "");
		transformed = transformed.replaceAll("</?TITLE>", "");
		transformed = transformed.replaceAll("</?doc>", "");
		transformed = transformed.replaceAll("<\\?xml version=\"1\\.0\" encoding=\"UTF-8\"\\?>", "");
		return transformed;
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String sourceViewName,
			String destinationViewName, String xsltFilename, Class<? extends EntityResolver> entityResolverClass)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription(tsd, sourceViewName,
				destinationViewName, xsltFilename, entityResolverClass));
		
	}
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			String sourceViewName, String destinationViewName, String xsltFilename,
			Class<? extends EntityResolver> entityResolverClass) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ViewXMLXSLTConverter_AE.class, tsd,
				PARAM_SOURCE_VIEW_NAME, sourceViewName, PARAM_DESTINATION_VIEW_NAME, destinationViewName,
				PARAM_XSLT_FILE_NAME, xsltFilename, PARAM_ENTITY_RESOLVER_CLASS_NAME, entityResolverClass.getName());
	}
}
