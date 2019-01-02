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
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import lombok.Data;
import lombok.Getter;

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

	public enum DocumentElement {
		DOCUMENT, ARTICLE_TITLE, ABSTRACT, KEYWORD, SECTION, TITLE, PARAGRAPH, CAPTION, ITALIC, BOLD, SUB, SUP, COPYRIGHT, SOURCE
	}

	// static final String CLASS_MENTION_NAME = "Section";
	// static final String TYPE_SLOT_NAME = "Type";
	// static final String NAME_SLOT_NAME = "Name";
	// static final String TITLE_SLOT_NAME = "Name";
	// static final String NAME_ATTRIBUTE_NAME = "name";
	// static final String TITLE_ATTRIBUTE_NAME = "name";
	// static final String PARSER_NAME = "CCP XML Parser";

	@Getter
	private List<Annotation> annotations = new ArrayList<Annotation>();

	private XMLReader parser;
	private Stack<Annotation> stack = new Stack<Annotation>();
	private StringBuffer documentText = new StringBuffer();

	public CcpXmlParser() throws IOException, SAXException {
		parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	}

	/**
	 * Returns the parsed version of the XML string input
	 * 
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(String xml) throws IOException, SAXException {
		return parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Returns the parsed version of the input PMC NXML File
	 * 
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(File xmlFile) throws IOException, SAXException {
		return parse(new InputSource(new FileReader(xmlFile)));
	}

	/**
	 * General method for parsing PMC xml that takes an InputSource as input.
	 * This method should replace the deprecated parsePMCXML(String) method.
	 * 
	 * @param inputSource
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(InputSource inputSource) throws IOException, SAXException {
		PubMedCentralXMLContentHandler contentHandler = new PubMedCentralXMLContentHandler();

		parser.setContentHandler(contentHandler);
		// parser.setEntityResolver(new PMCDTDClasspathResolver());
		parser.parse(inputSource);

		return documentText.toString();
	}

	class PubMedCentralXMLContentHandler implements ContentHandler {

		private static final String ABSTRACT_TYPE_ATT_NAME = "abstract-type";
		private static final String ABSTRACT_TYPE_SUMMARY = "summary";
		private static final String CAPTION_LABEL_ATT_NAME = "label";
		private static final String CAPTION_TYPE_ATT_NAME = "type";

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
			DocumentElement docElement = null;
			try {
				docElement = DocumentElement.valueOf(localName.toUpperCase().replaceAll("-", "_"));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Unhandled XML element: " + localName + ". Code changes required. ",
						e);
			}

			switch (docElement) {
			case DOCUMENT:
				break;
			case ARTICLE_TITLE:
				break;
			case ABSTRACT:
				documentText.append("\n\n");
				if (atts.getValue(ABSTRACT_TYPE_ATT_NAME) == null) {
					/*
					 * then this is the main abstract, so we add a "Abstract"
					 * title annotation
					 */
					addTitle("Abstract");
				} else {
					String abstractType = atts.getValue(ABSTRACT_TYPE_ATT_NAME);
					if (!abstractType.equals(ABSTRACT_TYPE_SUMMARY)) {
						/*
						 * The Author Summary abstract already has a title, so
						 * we don't need to add another
						 */
						addTitle("Abstract:" + abstractType.toUpperCase());
					}
				}
				break;
			case KEYWORD:
				documentText.append("Keyword: ");
				break;
			case PARAGRAPH:
				documentText.append("\n");
				break;
			case SECTION:
				documentText.append("\n\n");
				break;
			case TITLE:
				break;
			case CAPTION:
				documentText.append("\n\n");
				String captionTitle = "Caption";
				String captionType = atts.getValue(CAPTION_TYPE_ATT_NAME);
				if (captionType != null) {
					captionTitle = captionTitle + " (" + captionType.toUpperCase() + ")";
				}
				String captionLabel = atts.getValue(CAPTION_LABEL_ATT_NAME);
				if (captionLabel != null) {
					captionTitle = captionTitle + ": " + captionLabel.toUpperCase();
				}
				addTitle(captionTitle);
				documentText.append("\n");
				break;
			case COPYRIGHT:
				documentText.append("\n\n");
				break;
			case SOURCE:
				documentText.append("\n\n");
				break;
			case ITALIC:
				break;
			case BOLD:
				break;
			case SUB:
				break;
			case SUP:
				break;
			default:
				throw new IllegalArgumentException(
						"Unhandled document element: " + docElement.name() + ". Code changes required.");
			}

			Annotation ta = new Annotation();
			ta.setType(docElement);
			ta.setStart(documentText.length());
			stack.push(ta);
		}

		/**
		 * Add a TITLE annotation to the document text. This method is used to
		 * add the "Abstract" title to the document.
		 * 
		 * @param title
		 */
		private void addTitle(String title) {
			Annotation ta = new Annotation();
			ta.setStart(documentText.length());
			documentText.append(title);
			ta.setEnd(documentText.length());
			ta.setType(DocumentElement.TITLE);
			annotations.add(ta);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			Annotation ta = stack.pop();
			DocumentElement docElement = DocumentElement.valueOf(localName.toUpperCase().replaceAll("-", "_"));

			switch (docElement) {
			case DOCUMENT:
				break;
			case ARTICLE_TITLE:
				break;
			case ABSTRACT:
				break;
			case KEYWORD:
				break;
			case PARAGRAPH:
				break;
			case SECTION:
				break;
			case TITLE:
				break;
			case CAPTION:
				break;
			case COPYRIGHT:
				break;
			case SOURCE:
				break;
			case ITALIC:
				break;
			case BOLD:
				break;
			case SUB:
				break;
			case SUP:
				break;
			default:
				throw new IllegalArgumentException(
						"Unhandled document element: " + docElement.name() + ". Code changes required.");
			}

			ta.setEnd(documentText.length());

			annotations.add(ta);

			// /* add some whitespace in the form of line breaks */
			// if (EnumSet.of(DocumentElement.ARTICLE_TITLE,
			// DocumentElement.TITLE, DocumentElement.ABSTRACT,
			// DocumentElement.SECTION).contains(docElement)) {
			// documentText.append("\n\n");
			// } else if (docElement == DocumentElement.KEYWORD) {
			// documentText.append("\n");
			// }
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			String s = new String(ch, start, length);
			if (s.trim().length() > 0) {
				// while (s.endsWith("\n")) {
				// s = StringUtil.removeLastCharacter(s);
				// System.err.println("Removing line break!!");
				// }
				documentText.append(s);

			}
		}

	}

	@Data
	public static class Annotation {
		private DocumentElement type;
		private int start;
		private int end;
	}
}
