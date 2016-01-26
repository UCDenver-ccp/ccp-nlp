package edu.ucdenver.ccp.nlp.uima.annotators.comparison;

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

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class utilizes the XMLReader class to validate XML.
 * <p>
 * The code in this class was modified from: http://www.cafeconleche.org/books/xmljava/chapters/ch09s06.html
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class XMLValidityChecker {

	/**
	 * Validates the input XML file.
	 * 
	 * @param xmlFile
	 *            XML file to be validated
	 * @return true is XML is valid, false otherwise
	 */
	public static boolean validateXML(String xmlFile) {

		try {
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.parse(xmlFile);
			/* xml file is well-formed so return true */
			return true;
		} catch (SAXParseException e) {
			System.err.print("ERROR -- XMLValidityChecker: " + xmlFile + " is not well-formed at Line " + e.getLineNumber() + ", column "
					+ e.getColumnNumber() + " in the entity " + e.getSystemId());
			return false;
		} catch (SAXException e) {
			System.err.println("ERROR -- XMLValidityChecker: Could not check document because " + e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println("ERROR -- XMLValidityChecker: Due to an IOException, the parser could not check " + xmlFile);
			e.printStackTrace();
			return false;
		}

	}

}
