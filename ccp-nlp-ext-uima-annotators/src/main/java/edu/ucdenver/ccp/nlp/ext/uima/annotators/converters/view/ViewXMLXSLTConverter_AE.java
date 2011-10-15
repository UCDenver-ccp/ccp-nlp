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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Logger;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

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

	static Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
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

		InputStream xslStream = null;
		String retval = "";
		try {
			TransformerFactory transFact = TransformerFactory.newInstance();
			xslStream = this.getClass().getResourceAsStream(xsltFileName);
			if (xslStream == null) {
				throw new AnalysisEngineProcessException(new IllegalArgumentException(
						"null xslt file stream when reading:" + xsltFileName));
			}
			Source xsltSource = new javax.xml.transform.stream.StreamSource(xslStream);
			Transformer trans = transFact.newTransformer(xsltSource);
			StringReader sr = new StringReader(input);
			Source xmlSource = new javax.xml.transform.stream.StreamSource(sr);
			StringWriter sw = new StringWriter();
			Result result = new javax.xml.transform.stream.StreamResult(sw);
			trans.transform(xmlSource, result);
			retval = sw.toString();
			sw.close();
			return retval;
		} catch (TransformerConfigurationException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (TransformerException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			try {
				if (xslStream != null) {
					xslStream.close();
				}
			} catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
			// return retval;
		}
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String sourceViewName,
			String destinationViewName, String xsltFilename) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(ViewXMLXSLTConverter_AE.class, tsd, PARAM_SOURCE_VIEW_NAME,
				sourceViewName, PARAM_DESTINATION_VIEW_NAME, destinationViewName, PARAM_XSLT_FILE_NAME, xsltFilename);
	}
}
