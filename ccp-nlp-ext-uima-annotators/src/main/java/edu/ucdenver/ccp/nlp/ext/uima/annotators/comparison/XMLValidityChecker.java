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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.comparison;

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