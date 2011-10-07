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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.View;

/*
 * reads the RAW view and converts to a different XML that is then put on 
 * the DEFAULT view
 */
public class ViewXMLXSLTConverter_AE extends ViewConverter_AE {
	
	public static String PARAM_XSLT_FILE_NAME 
		= ConfigurationParameterFactory.createConfigurationParameterName(
			ViewXMLXSLTConverter_AE.class, "xsltFileName");
	@ConfigurationParameter(mandatory = true)
	private String xsltFileName;
	
	static Logger logger=Logger.getLogger(ViewXMLXSLTConverter_AE.class);
	

	@Override
	protected void convertView(JCas sourceView, JCas destinationView)
			throws AnalysisEngineProcessException {
		String sourceText = sourceView.getDocumentText();
		String destText = convert(sourceText);
		destinationView.setDocumentText(destText);
		String docID = UIMA_Util.getDocumentID(sourceView);
		logger.info("reading from view: " + sourceView.getViewName() + " " + sourceText.length() + " characters, from docID:  " + docID);	
		UIMA_Util.setDocumentID(destinationView, docID);
		//System.err.println("DEST VIEW NAME:" + destinationView.getViewName());
	}
	
	public static AnalysisEngine createAnalysisEngine(
			TypeSystemDescription tsd, 
			String sourceViewName,
			String destinationViewName,
			String xsltFilename) 
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				ViewXMLXSLTConverter_AE.class, tsd, 
				PARAM_SOURCE_VIEW_NAME, sourceViewName, 
				PARAM_DESTINATION_VIEW_NAME, destinationViewName,
				PARAM_XSLT_FILE_NAME, xsltFilename);
	}
	
	protected String convert(String input) 
	throws AnalysisEngineProcessException {

		InputStream xslStream =null;
		String retval="";
		try {
			TransformerFactory transFact = TransformerFactory.newInstance();
			xslStream = this.getClass().getClassLoader().getResourceAsStream(xsltFileName);
			// does not work: xslStream = this.getClass().getResourceAsStream(xsltFileName);
			if (xslStream == null) {
				logger.error("null xslt stream when trying to read: \"" + xsltFileName + "\", check classpath for file.");
				throw new AnalysisEngineProcessException(new Exception("null xslt file stream when reading:" + xsltFileName));
			}
			Source xsltSource = new javax.xml.transform.stream.StreamSource(
				xslStream);
			Transformer trans = transFact.newTransformer(xsltSource);
			
			StringReader sr = new StringReader(input);
			
			// javax.xml.transform.StreamSource deals with UTF8 automatically
			// but I can't get the URIResolver to work with it. This works 
			// when all the dtd docs are copied into the top level.
			Source xmlSource = new javax.xml.transform.stream.StreamSource(sr);
			
			// use a org.sax....instead
			//SAXSource xmlSource = getSaxSource();
			//xmlSource.setInputSource(new InputSource(sr));
			// different way to get input doesn't change things on non-sax
			//xmlSource.setInputSource(new InputSource(new FileReader("/Users/roederc/subversion_workspace/BioCreativeIII/data/gn/BC3GNTraining/xmls/1892574.nxml")));
			// to get this to work with the resolver requires use of hte
			// saxon9he.jar file (saxonica.com)
			
			StringWriter sw = new StringWriter();
			Result result = new javax.xml.transform.stream.StreamResult(sw);
			
			// different way to write output doesn't change things on non-sax
			//Result result = new javax.xml.transform.stream.StreamResult(new OutputStreamWriter(new FileOutputStream("/Users/roederc/foo"), "UTF-8"));
		
			trans.transform(xmlSource, result);
			retval = sw.toString();
			sw.close();
		}

		catch (TransformerConfigurationException e) {
			logger.error("Error " + e);
			logger.error("    stylesheet is: " + xsltFileName);
			throw new AnalysisEngineProcessException(e);
		} catch (TransformerException e) {
			logger.error("Error " + e);
			logger.error("   stylesheet is: " + xsltFileName);
			throw new AnalysisEngineProcessException(e);
		}  
		finally {
			try {
				if (xslStream != null) {xslStream.close();}
			}
			catch (IOException x) {
				
			}
			return retval;
		}
	}
	
	
	/**
	 * create a SAXSource, still need to do something like:
	 * 	 //xmlSource.setInputSource(new InputSource(new FileReader(xmlFile)));
	 * to it to set the actual source.
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	/*
	public SAXSource getSaxSource() 
	throws SAXException, ParserConfigurationException {
		
		  SAXParserFactory saxParserfactory= SAXParserFactory.newInstance();
		  SAXSource xmlSource = new SAXSource();
		  SAXParser parser = saxParserfactory.newSAXParser();
		  parser.getXMLReader().setEntityResolver(
				 new  edu.uchsc.ccp.util.nlp.parser.pmc.PMCDTDClasspathResolver());
		  xmlSource.setXMLReader(parser.getXMLReader());

		  return xmlSource;
	}
	*/
	
	public static String createXmlDescriptor(TypeSystemDescription tsd) 
	throws ResourceInitializationException, SAXException, IOException {
		AnalysisEngineDescription aeDesc 
		= AnalysisEngineFactory.createPrimitiveDescription(
				ViewXMLXSLTConverter_AE.class, tsd, 
				PARAM_SOURCE_VIEW_NAME, View.XML.viewName(), 
				PARAM_DESTINATION_VIEW_NAME, View.DEFAULT.viewName());
		StringWriter sw = new StringWriter();
		aeDesc.toXML(sw);
		sw.close();
		return sw.toString();
	}
	
	
	/* for use with the StreamSource, but this doesn't appear to work
	static class MyResolver implements javax.xml.transform.URIResolver {
		
		public MyResolver() {
			System.err.println("crating a resolver");
		}

		@Override
		public Source resolve(String href, String systemId)
				throws TransformerException {
			System.err.println("XXXXXXXXX: " + href + ", " + systemId);
			 if (systemId.contains("mathml")) {
		            systemId = systemId.substring(systemId.lastIndexOf("mathml") - 1);
		        } else if (systemId.contains("iso8879")) {
		            systemId = systemId.substring(systemId.lastIndexOf("iso8879") - 1);
		        } else if (systemId.contains("iso9573-13")) {
		            systemId = systemId.substring(systemId.lastIndexOf("iso9573-13") - 1);
		        } else if (systemId.contains("xmlchars")) {
		            systemId = systemId.substring(systemId.lastIndexOf("xmlchars") - 1);
		        } else {
		            systemId = systemId.substring(systemId.lastIndexOf(File.separatorChar));
		        }
		        // System.err.println("RESOLVING ENTITY: publicID: " + publicId + "   systemID: " +
		        // systemId);
		        InputStream stream = getClass().getResourceAsStream(systemId);
		        if (stream == null) {
		            return null;
		        } else {
		            return new StreamSource(stream);
		        }

		}
		
	}
	*/

}

