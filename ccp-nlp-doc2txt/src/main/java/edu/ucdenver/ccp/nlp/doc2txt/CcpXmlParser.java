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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/*
 * See the Test class for an example of what an input doc. looks like.
 * 
 * Basic Strategy is to create a ClassMention named "Section", 
 * with a SlotMention named "Type" whose value is the the tag name.
 * An optional SlotMention named "Name" has a value for any names or titles.
 * The associated TextAnnotation has the span.

 New strategy is  to use a local class to hold these attributes.
 */

public class CcpXmlParser {

	private static final String[] tags = { "DOC", "SECTION", "SUBSECTION", "PARAGRAPH", "KEYWORD", "DEFINITION",
			"ABSTRACT", "FIGURE", "TITLE", "ITALICS" };

	static final String CLASS_MENTION_NAME = "Section";
	static final String TYPE_SLOT_NAME = "Type";
	static final String NAME_SLOT_NAME = "Name";
	static final String TITLE_SLOT_NAME = "Name";
	static final String NAME_ATTRIBUTE_NAME = "name";
	static final String TITLE_ATTRIBUTE_NAME = "name";
	static final String PARSER_NAME = "CCP XML Parser";

	private XMLReader parser;
	private HashSet<String> tagSet;
	private Stack<Annotation> stack = new Stack<Annotation>();
	private List<Annotation> annotations = new ArrayList<Annotation>();
	private StringBuffer documentText = new StringBuffer();
	private String docID;

	public CcpXmlParser() throws IOException, SAXException {
		parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		tagSet = new HashSet<String>();
		tagSet.addAll(Arrays.asList(tags));
		for (String t : tags) {
			tagSet.add(t.toLowerCase());
		}
	}

	/**
	 * Returns the parsed version of the XML string input
	 * 
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(String xml, String docID) throws IOException, SAXException {
		return parse(new InputSource(new StringReader(xml)), docID);
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * Returns the parsed version of the input PMC NXML File
	 * 
	 * @param nxmlFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parsePmcNxml(File nxmlFile, String docID) throws IOException, SAXException {
		return parse(new InputSource(new FileReader(nxmlFile)), docID);
	}

	/**
	 * General method for parsing PMC xml that takes an InputSource as input. This method should
	 * replace the deprecated parsePMCXML(String) method.
	 * 
	 * @param inputSource
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(InputSource inputSource, String docID) throws IOException, SAXException {
		this.docID = docID;
		PubMedCentralXMLContentHandler contentHandler = new PubMedCentralXMLContentHandler();

		parser.setContentHandler(contentHandler);
		// parser.setEntityResolver(new PMCDTDClasspathResolver());
		parser.parse(inputSource);

		return documentText.toString();
	}

	class PubMedCentralXMLContentHandler implements ContentHandler {

		private final Logger logger = Logger.getLogger(PubMedCentralXMLContentHandler.class);

		public void startDocument() throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		public void processingInstruction(String target, String data) throws SAXException {
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

		public void endPrefixMapping(String prefix) throws SAXException {
		}

		public void setDocumentLocator(Locator locator) {
		}

		public void skippedEntity(String name) throws SAXException {
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		}

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (tagSet.contains(localName.toLowerCase())) {

				if (localName.equals("ABSTRACT")) {
					documentText.append("Abstract\n\n");
				}
				
				if (localName.equals("KEYWORD")) {
					documentText.append("Keyword: ");
				}
				
				Annotation ta = new Annotation();
				ta.start = documentText.length();
				ta.end = 1000000;

				ta.type = localName;
				if (atts.getLength() > 0) {
					if (atts.getValue(NAME_ATTRIBUTE_NAME) != null) {
						ta.name = atts.getValue(0);
						if (atts.getValue(0).trim().length() > 0) {
							documentText.append(" " + atts.getValue(0) + " ");
						}
					}
					/*
					 * if (atts.getValue(TITLE_ATTRIBUTE_NAME) != null) { ta.name=atts.getValue(0);
					 * if (atts.getValue(0).trim().length() > 0) { documentText.append(" " +
					 * atts.getValue(0) + " "); System.out.println("title attribute name: \"" +
					 * atts.getValue(0) + "\""); } }
					 */
				}
				stack.push(ta);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (!stack.isEmpty()) {
				Annotation ta = stack.peek();
				if (ta != null) {
					String taType = ta.type;
					logger.debug("TA TYPE: " + taType);
					if (taType.toLowerCase().equals(localName.toLowerCase())) {
						ta.end = documentText.length();
						annotations.add(ta);
						if (taType.equals("PARAGRAPH") || taType.equals("TITLE") || taType.equals("ABSTRACT") || taType.equals("SECTION")) {
							documentText.append("\n\n");
						} else 
						if (taType.equals("KEYWORD")) {
							documentText.append("\n");
						}
					}
				}
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			String s = new String(ch, start, length);
			if (s.trim().length() > 0) {
				documentText.append(s);
			}
//			if (documentText.toString().lastIndexOf("\n") > 60) {
//				documentText.append("\n");
//			}

		}

	}

	public class Annotation {
		public String type;
		public String name;
		public int start;
		public int end;

		public String toString() {
			return name + ":" + type + ", " + start + ":" + end;
		}
	}
}