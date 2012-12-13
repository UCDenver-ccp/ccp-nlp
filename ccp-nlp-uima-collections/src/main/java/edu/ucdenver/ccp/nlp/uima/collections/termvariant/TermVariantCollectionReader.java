/*
 * SimpleFileCollectionReader.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */

package edu.ucdenver.ccp.nlp.uima.collections.termvariant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This class is a UIMA Collection Reader for reading a collection of documents contained in a
 * single file. see edu.uchsc.ccp.util.nlp.parser.testsuite.TestSuiteInputFileParser.java for a
 * description of the file format. The file contains both source text and Gold Standard data as
 * described in the paser, and the reader populates the CAS with both.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TermVariantCollectionReader extends BaseTextCollectionReader {

	public static final String PARAM_TERM_VARIANT_FILE = ConfigurationParameterFactory
			.createConfigurationParameterName(TermVariantCollectionReader.class, "termVariantFile");
	@ConfigurationParameter(mandatory = true, description = "file path to the file containing the term/variant info. For format, see TermVariantFileParser documentation")
	protected File termVariantFile;

	private Iterator<GenericDocument> documentIterator;

	@Override
	protected void initializeImplementation(UimaContext context) throws ResourceInitializationException {
		boolean isInputFileValid = TermVariantFileParser.hasValidFormat(termVariantFile);
		if (!isInputFileValid) {
			throw new ResourceInitializationException(new IllegalStateException(
					"The input term-variant file is not formatted properly. Please fix and re-try."));
		}

		try {
			documentIterator = TermVariantFileParser.getDocumentIteratorFromFile(termVariantFile);
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

	}

	/**
	 * Burn through the file and count the number of documents that will be returned
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		Iterator<GenericDocument> docIter = TermVariantFileParser.getDocumentIteratorFromFile(termVariantFile);
		int count = 0;
		while (docIter.hasNext()) {
			docIter.next();
			count++;
		}
		return count;
	}

	@Override
	protected void skip() throws ResourceInitializationException {
		skip(documentIterator);
	}

	/**
	 * Advances the input Iterator<GenericDocument> forward numberToSkip times.
	 * 
	 * @param docIter
	 */
	private void skip(Iterator<GenericDocument> docIter) {
		int numSkipped = 0;
		while (docIter.hasNext() && numSkipped < numberToSkip) {
			docIter.next();
			numSkipped++;
		}
	}

	@Override
	protected boolean hasNextDocument() throws IOException, CollectionException {
		return documentIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader#getNextDocument()
	 */
	@Override
	protected GenericDocument getNextDocument() throws CollectionException, IOException {
		return documentIterator.next();
	}

	/**
	 * TODO: See if this part of the code can be made type-system-independent by way of the
	 * uima-shims project
	 */
	@Override
	protected void loadAnnotationsIntoCas(JCas jcas, GenericDocument document) {
		UIMA_Util uu = new UIMA_Util();
		uu.putTextAnnotationsIntoJCas(jcas, document.getAnnotations());
	}

	/**
	 * This factory method provides complete flexibility when setting up a
	 * TermVariantCollectionReader
	 * 
	 * @param tsd
	 * @param termVariantFile
	 * @param encoding
	 * @param language
	 * @param disableProgress
	 * @param num2process
	 * @param num2skip
	 * @param documentMetadataHandlerClass
	 * @param viewName
	 * @return a {@link CollectionReaderDescription} set to the input parameters of this method
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(TypeSystemDescription tsd, File termVariantFile,
			CharacterEncoding encoding, String language, boolean disableProgress, int num2process, int num2skip,
			Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass, String viewName)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(TermVariantCollectionReader.class, tsd,
				PARAM_TERM_VARIANT_FILE, termVariantFile.getAbsolutePath(), PARAM_ENCODING, encoding.name(),
				PARAM_DISABLE_PROGRESS, disableProgress, PARAM_LANGUAGE, language, PARAM_NUM2PROCESS, num2process,
				PARAM_NUM2SKIP, num2skip, PARAM_VIEWNAME, viewName, PARAM_DOCUMENT_METADATA_HANDLER_CLASS,
				documentMetadataHandlerClass.getName());
	}

	/**
	 * This factory method provides default settings for the TermVariantCollectionReader:<br>
	 * * writes to the default CAS view<br>
	 * * processes all documents from start to finish (skip = 0, numToProcess = -1)<br>
	 * * uses the CCP type system document metadata handler<br>
	 * * disables progress monitoring to keep from burning through the input file to count the
	 * number of documents<br>
	 * 
	 * @param tsd
	 * @param termVariantFile
	 * @param encoding
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDefaultDescription(TypeSystemDescription tsd, File termVariantFile,
			CharacterEncoding encoding) throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(TermVariantCollectionReader.class, tsd,
				PARAM_TERM_VARIANT_FILE, termVariantFile.getAbsolutePath(), PARAM_ENCODING, encoding.name(),
				PARAM_DISABLE_PROGRESS, true);
	}

	// /**
	// * Input file takes the form of 1 document per line:<br< DOC_ID|DOC_TEXT
	// */
	// public static final String PARAM_INPUT_FILE = "InputFile";
	//
	// /**
	// * The Document Collection Identifier for this GeneRIFs collection must be included in the
	// * descriptor under "DocumentCollectionID"
	// */
	// public static final String PARAM_DOCUMENTCOLLECTIONID = "DocumentCollectionID";
	//
	// /**
	// * The number of GeneRIF records to return must be included in the descriptor under
	// "EndIndex."
	// * This parameter allows for ease of testing by limiting the number of records read.
	// */
	// public static final String PARAM_ENDINDEX = "EndIndex";
	//
	// /**
	// * Allows the program to skip a certain number of records prior to processing. This is helpful
	// * in cases where a run is interrupted unexpected, and you wish to restart in the middle.
	// */
	// public static final String PARAM_DOCUMENTS_TO_SKIP = "DocumentsToSkip";
	//
	// private int currentDocument = 0;
	//
	// private int numDocuments = 0;
	//
	// private int documentCollectionID = -1;
	//
	// private int endIndex = -1;
	//
	// private Integer linesToSkip;
	//
	// private Iterator<GenericDocument> testSuiteDocumentIterator;
	//
	// /**
	// * Initializes the GRIFsFileCollectionReader by initializing a new GeneRIFFileParser.
	// */
	// public void initialize() throws ResourceInitializationException {
	// String inputFileName = (String) getConfigParameterValue(PARAM_INPUT_FILE);
	// importOptionalInputParameters();
	// numDocuments = 0;
	//
	// File f = new File(inputFileName);
	// if (!TermVariantInputFileParser.checkForValidInputFileFormat(f)) {
	// throw new ResourceInitializationException(new Exception(
	// "Invalid input file format for the TestSuiteGenerator_CR component."));
	// }
	//
	// try {
	// testSuiteDocumentIterator =
	// TermVariantInputFileParser.getDocumentIteratorFromFile(inputFileName,
	// documentCollectionID);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// if (linesToSkip > 0) {
	// System.err.println("Skipping " + linesToSkip + " lines...");
	// for (int i = 0; i < linesToSkip; i++) {
	// if (testSuiteDocumentIterator.hasNext()) {
	// testSuiteDocumentIterator.next();
	// }
	// }
	// }
	// }
	//
	// private void importOptionalInputParameters() {
	// try {
	// documentCollectionID = Integer.parseInt((String)
	// getConfigParameterValue(PARAM_DOCUMENTCOLLECTIONID));
	// } catch (Exception e) {
	// documentCollectionID = -1;
	// }
	//
	// try {
	// endIndex = Integer.parseInt((String) getConfigParameterValue(PARAM_ENDINDEX));
	// if (endIndex < 0) {
	// endIndex = Integer.MAX_VALUE;
	// }
	// } catch (Exception e) {
	// endIndex = Integer.MAX_VALUE;
	// }
	//
	// try {
	// linesToSkip = (Integer) getConfigParameterValue(PARAM_DOCUMENTS_TO_SKIP);
	// if (linesToSkip == null) {
	// linesToSkip = new Integer(0);
	// }
	// } catch (Exception e) {
	// linesToSkip = new Integer(0);
	// }
	// }
	//
	// /**
	// * @see com.ibm.uima.collection.CollectionReader#getNext(com.ibm.uima.cas.CAS)
	// */
	// public void getNext(CAS aCAS) throws IOException, CollectionException {
	// JCas jcas;
	// try {
	// jcas = aCAS.getJCas();
	// } catch (CASException e) {
	// throw new CollectionException(e);
	// }
	//
	// GenericDocument gd = testSuiteDocumentIterator.next();
	//
	// String documentID = gd.getDocumentID();
	// String documentText = gd.getDocumentText();
	// jcas.setDocumentText(documentText);
	//
	// UIMA_Util uu = new UIMA_Util();
	// uu.putTextAnnotationsIntoJCas(jcas, gd.getAnnotations());
	//
	// CCPDocumentInformation srcDocInfo = new CCPDocumentInformation(jcas);
	// srcDocInfo.setDocumentID(documentID);
	// srcDocInfo.setDocumentSize(documentText.length());
	// srcDocInfo.setDocumentCollectionID(documentCollectionID);
	// srcDocInfo.addToIndexes();
	//
	// currentDocument++;
	//
	// if (currentDocument % 100 == 0) {
	// logger.info("TestSuiteGenerator_CR PROGRESS: " + currentDocument + " -- " + documentID);
	// }
	//
	// }
	//
	// /**
	// * @see com.ibm.uima.collection.base_cpm.BaseCollectionReader#hasNext()
	// */
	// public boolean hasNext() throws IOException, CollectionException {
	// return (testSuiteDocumentIterator.hasNext() && currentDocument < endIndex);
	// }
	//
	// public Progress[] getProgress() {
	// return new Progress[] { new ProgressImpl(currentDocument, numDocuments, Progress.ENTITIES) };
	// }
	//
	// public void close() throws IOException {
	// // do nothing
	// }

}
