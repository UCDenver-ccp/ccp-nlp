package edu.ucdenver.ccp.nlp.uima.collections.termvariant;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * This parser consumes files that contain term/variant pairings. One possible use for this tool is
 * to create test suites of ontology id/ term name pairings to use as a gold standard test suite for
 * NLP tools. The expected format for the input file:<br>
 * # some comment describing a group of test objects. This comment will be used as part of the
 * document ID. <br>
 * termID,termID',termID'' [tab] termName
 * 
 * The resultant document text contains only the term names. Annotations are included for each term
 * ID mentioned on a line covering the entire term name. For example, if the input file consisted of
 * only the following two line:<br>
 * # document 1<br>
 * GO:0004089 [tab] anhydrase activity<br>
 * 
 * Then one document would be produced by the parser containing the text "anhydrase activity" and a
 * single annotation over that text referencing the GO:0004089 class.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TermVariantFileParser {
	private static Logger logger = Logger.getLogger(TermVariantFileParser.class);

	private static final String COMMENT_LINE_INDICATOR = "//";
	private static final String DOCUMENT_TITLE_INDICATOR = "#";

	/**
	 * private constructor, this class should not be instantiated
	 */
	private TermVariantFileParser() {
		// private constructor to prevent class instantiation
	}

	/**
	 * @param termVariantFile
	 * @return an Iterator<GenericDocument> over the term-variant "documents" contained in the input
	 *         File.
	 * @throws FileNotFoundException
	 */
	public static Iterator<GenericDocument> getDocumentIteratorFromFile(File termVariantFile)
			throws FileNotFoundException {
		if (termVariantFile.exists()) {
			return getDocumentIterator(new FileReader(termVariantFile));
		}
		throw new IllegalStateException("Cannot return term-variant iterator. File does not exist: "
				+ termVariantFile.getAbsolutePath());
	}

	/**
	 * @param reader
	 * @return an Iterator<GenericDocument> over the term-variant "documents" contained in the input
	 */
	public static Iterator<GenericDocument> getDocumentIterator(Reader reader) {

		final BufferedReader br = new BufferedReader(reader);
		return new Iterator<GenericDocument>() {
			private GenericDocument nextDocument = null;
			private boolean alreadyEncounteredFirstDocumentTitleLine = false;
			private boolean encounteredLastDocument = false;
			private GenericDocument gd = null;

			@Override
			public boolean hasNext() {
				try {
					if (nextDocument == null) {
						if (encounteredLastDocument) {
							return false;
						}
						String line;
						while ((line = br.readLine()) != null) {
							if (lineIsComment(line)) {
								continue;
							}
							if (lineIsDocumentTitle(line)) {
								if (!alreadyEncounteredFirstDocumentTitleLine) {
									alreadyEncounteredFirstDocumentTitleLine = true;
									gd = initializeNewDocument(line);
									continue;
								}
								nextDocument = gd;
								gd = initializeNewDocument(line);
								return true;
							} else if (lineIsBlank(line) & alreadyEncounteredFirstDocumentTitleLine) {
								addLineToDocumentText(line, gd);
							} else if (alreadyEncounteredFirstDocumentTitleLine) {
								processDocumentContentLine(line, gd);
							}
						}
						nextDocument = gd;
						encounteredLastDocument = true;
						return true;
					}
					return true;
				} catch (IOException ioe) {
					ioe.printStackTrace();
					return false;
				} catch (InvalidSpanException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			public GenericDocument next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				GenericDocument documentToReturn = nextDocument;
				nextDocument = null;
				return documentToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}

		};
	}

	/**
	 * Parses a line containing a test for the test suite. An annotation is generated and added to
	 * the GenericDocument object. The document text field in the GenericDocument is updated as
	 * well.
	 * 
	 * @param line
	 * @param gd
	 * @throws InvalidSpanException
	 */
	protected static void processDocumentContentLine(String line, GenericDocument gd) throws InvalidSpanException {
		String classMentionNames[] = parseClassMentionNameFromLine(line);
		String coveredText = parseCoveredTextfromLine(line);
		Span span = computeSpan(coveredText, gd);
		for (String mentionName : classMentionNames) {
			TextAnnotation ta = createAnnotation(mentionName.trim(), coveredText, span, gd.getDocumentCollectionID(),
					gd.getDocumentID());
			addLineToDocumentText(coveredText, gd);
			gd.addAnnotation(ta);
		}

		/* create complementary sentence annotation */
		TextAnnotation sta = createAnnotation(ClassMentionType.SENTENCE.typeName(), " " + coveredText + " ", new Span(span.getSpanStart()-1, span.getSpanEnd()+1),
				gd.getDocumentCollectionID(), gd.getDocumentID());
		gd.addAnnotation(sta);
	}

	/**
	 * Appends the input String to the end of the document text contained in the GenericDocument
	 * object
	 * 
	 * @param line
	 * @param gd
	 */
	protected static void addLineToDocumentText(String line, GenericDocument gd) {
		String documentText = gd.getDocumentText();
		documentText = documentText + " " + line + " \n";
		gd.setDocumentText(documentText);
	}

	/**
	 * 
	 * @param documentTitleLine
	 * @return an initialized GenericDocument object with the document ID field set to the input
	 *         document title line (trimmed and 1st character (#) removed). The document text field
	 *         is set to be empty.
	 */
	protected static GenericDocument initializeNewDocument(String documentTitleLine) {
		GenericDocument gd = new GenericDocument(documentTitleLine.substring(1).trim());
		gd.setDocumentText("");
		return gd;
	}

	/**
	 * 
	 * @param classMentionName
	 * @param coveredText
	 * @param span
	 * @param documentCollectionID
	 * @param documentID
	 * @return a TextAnnotation object initialized using the input arguments
	 */
	protected static TextAnnotation createAnnotation(String classMentionName, String coveredText, Span span,
			int documentCollectionID, String documentID) {
		Annotator annotator = new Annotator("1212", "TermVariantAnnotator", "CCP");
		return new DefaultTextAnnotation(span.getSpanStart(), span.getSpanEnd(), coveredText, annotator,
				new AnnotationSet(), "-1", documentCollectionID, documentID, -1,
				new DefaultClassMention(classMentionName));
	}

	/**
	 * @param coveredText
	 * @param gd
	 * @return the span for an annotation covering the input covered text if it were appended to the
	 *         document text that is already a part of the input GenericDocument object
	 * 
	 * @throws InvalidSpanException
	 */
	protected static Span computeSpan(String coveredText, GenericDocument gd) throws InvalidSpanException {
		int spanStart = gd.getDocumentText().length() + 1;
		int spanEnd = spanStart + coveredText.length();
		return new Span(spanStart, spanEnd);
	}

	/**
	 * 
	 * @param line
	 * @return the covered text or "variant" (second column) from the input line
	 */
	protected static String parseCoveredTextfromLine(String line) {
		return line.split("\\t")[1].trim();
	}

	/**
	 * 
	 * @param line
	 * @return the class mention name or "term" (first column) from the input line
	 */
	protected static String[] parseClassMentionNameFromLine(String line) {
		return line.split("\\t")[0].split(",");
	}

	/**
	 * Documents consist of a document-title line and all lines until the next document-title line
	 * or EOF. Blank lines are allowed at any point and are included in the documents. Commented
	 * lines are ignored. There must be at least one non-comment, non-blank line between each pair
	 * of document-title lines.
	 * 
	 * @param r
	 * @return true if the input if formatted appropriated, false otherwise
	 */
	public static boolean hasValidFormat(Reader r) {
		boolean inDocument = false;
		int documentLineCount = 0;
		String previousDocumentTitle = null;
		BufferedReader br = null;
		try {
			int lineNumber = 0;
			String line;
			br = new BufferedReader(r);
			while ((line = br.readLine()) != null) {
				lineNumber++;
				if (!inDocument & isMeaningfulLine(line)) {
					logger.error("Invalid input file: Non-blank/commented line found before first documentTitle line ("
							+ lineNumber + "): " + line);
					return false;
				}
				if (lineIsDocumentTitle(line)) {
					if (inDocument) {
						if (documentLineCount == 0) {
							logger.error("Invalid input file: Empty document discovered near line " + lineNumber + ": "
									+ previousDocumentTitle);
							return false;
						}
					}
					inDocument = true;
					previousDocumentTitle = line;
					documentLineCount = 0;
				} else if (isMeaningfulLine(line)) {
					documentLineCount++;
					if (!lineHasValidFormat(line)) {
						logger.error("Invalid input file: Line with an invalid format (" + lineNumber + "): " + line);
						return false;
					}
				}
			}
			if (documentLineCount == 0) {
				logger.error("Invalid input file: Empty document discovered: " + previousDocumentTitle);
				return false;
			}
			if (!inDocument) {
				logger.error("Invalid input file: This input file contains no documents.");
				return false;
			}
			return true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.error("Invalid input file. Exception was thrown while reading it.");
		return false;
	}

	/**
	 * @param inputFile
	 * @return true if the input File is formatted properly, false otherwise
	 */
	public static boolean hasValidFormat(File inputFile) {
		if (!inputFile.exists()) {
			throw new IllegalStateException("Cannot return term-variant iterator. File does not exist: "
					+ inputFile.getAbsolutePath());
		}
		try {
			FileReader fr = new FileReader(inputFile);
			return hasValidFormat(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @param inputStr
	 * @return true if the input String is formatted properly, false otherwise
	 */
	public static boolean hasValidFormat(String inputStr) {
		StringReader sr = new StringReader(inputStr);
		return hasValidFormat(sr);
	}

	/**
	 * @param line
	 * @return true if the input line contains a single tab character, false otherwise
	 */
	private static boolean lineHasValidFormat(String line) {
		String[] toks = line.split("\\t+");
		if (toks.length != 2) {
			logger.error("Invalid test suite input line. Expected 2 tab-delimited tokens on line but found "
					+ toks.length + ".");
			return false;
		}
		return true;
	}

	/**
	 * @param line
	 * @return true if a line is blank
	 */
	private static boolean lineIsBlank(String line) {
		return line.trim().isEmpty();
	}

	/**
	 * @param line
	 * @return true if the input line starts with the COMMENT_LINE_INDICATOR
	 */
	private static boolean lineIsComment(String line) {
		return (line.trim().startsWith(COMMENT_LINE_INDICATOR));
	}

	/**
	 * @param line
	 * @return true if a line starts with the DOCUMENT_TITLE_INDICATOR
	 */
	private static boolean lineIsDocumentTitle(String line) {
		return (line.trim().startsWith(DOCUMENT_TITLE_INDICATOR));
	}

	/**
	 * @param line
	 * @return true if the input line has some meaning/value, i.e. it is not blank, and is not a
	 *         comment
	 */
	private static boolean isMeaningfulLine(String line) {
		return !(lineIsBlank(line) | lineIsComment(line) | lineIsDocumentTitle(line));
	}

}
