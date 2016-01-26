package edu.ucdenver.ccp.nlp.doc2txt;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import edu.ucdenver.ccp.nlp.doc2txt.pmc.PmcDtdClasspathResolver;

public class XsltConverter {

	static Logger logger = Logger.getLogger(XsltConverter.class);

	private EntityResolver er;

	// http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.htmlsax
	// http://stackoverflow.com/questions/2968190/how-to-select-saxon-transformerfactory-in-java
	static final String transformerFactoryPropertyName = "javax.xml.transform.TransformerFactory";

	public XsltConverter(EntityResolver er) {
		this.er = er;
	}

	public String convert(InputStream xmlStream, InputStream xslStream) {
		String retval = "";
		try {
			if (xslStream == null) {
				logger.error("XlstConverter.convert(): couldn't read the xslt file stream. ");
			} else {
				// get an xslt Source
				Source xsltSource = new javax.xml.transform.stream.StreamSource(xslStream);

				// Get a transformer
				// TransformerFactory transFact = TransformerFactory.newInstance();
				TransformerFactory transFact = SAXTransformerFactory.newInstance();
				Transformer trans = transFact.newTransformer(xsltSource);
				// http://docs.oracle.com/javase/6/docs/api/index.html?javax/xml/transform/URIResolver.html
				// If an application wants to set the ErrorHandler or EntityResolver for an
				// XMLReader used during a transformation, it should use a URIResolver to return the
				// SAXSource
				// which provides (with getXMLReader) a reference to the XMLReader.

				// get a source to the input xml
				InputSource xmlSource = new InputSource(xmlStream);

				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setValidating(false);
				spf.setNamespaceAware(true);
				SAXParser parser = spf.newSAXParser();
				XMLReader reader = parser.getXMLReader();

				/**********/

				reader.setEntityResolver(er);
				SAXSource xmlSaxSource = new SAXSource(reader, xmlSource);
				// xmlSaxSource.setXMLReader(reader);

				// output
				StringWriter sw = new StringWriter();
				Result result = new StreamResult(sw);

				// transform
				try {
					trans.transform(xmlSaxSource, result);
				} catch (Exception e) {
					logger.error("error transforming " + xmlSaxSource);
					throw new RuntimeException(e);
				}
				retval = sw.toString();
				sw.close();
			}
		} catch (Exception x) {
			logger.error("XSLTConverter.convert() failed:" + x);
			x.printStackTrace();
			throw new RuntimeException(x);
		}

		try {
			if (xslStream != null) {
				xslStream.close();
			}
		} catch (IOException x) {
			throw new RuntimeException(x);
		}

		// UGLY HACK TODO
//		System.out.println("RETVAL: " + retval + ";;;");
		if (retval.indexOf("<doc>") == -1) {
			retval = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<DOC>" + retval.substring(38) + "</DOC>";
		}

		return retval;

	}

}
