/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.collections.line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.uima.util.View;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * A collection reader that reads documents from a single file, where each line represents an
 * individual document. This collection reader makes use of the {@link DocumentExtractor} interface
 * to control how a line from the collection file is transformed into a document.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class DocumentPerLineCollectionReader extends BaseTextCollectionReader {

	/* ==== Input file configuration ==== */
	public static final String PARAM_COLLECTION_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			DocumentPerLineCollectionReader.class, "collectionFile");

	@ConfigurationParameter(mandatory = true, description = "The file containing the document collection")
	protected File collectionFile;

	/* ==== DocumentFromLineExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(DocumentPerLineCollectionReader.class, "documentExtractorClassName");

	/**
	 * The name of the DocumentFromLineExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentExtractor implementation to use")
	private String documentExtractorClassName;

	/**
	 * this {@link DocumentExtractor} will be initialized based on the class name specified by the
	 * documentFromLineExtractorClassName parameter
	 */
	private DocumentExtractor documentExtractor;

	private BufferedReader reader;

	private GenericDocument nextDocument = null;

	@Override
	public boolean hasNextDocument() throws IOException, CollectionException {
		if (nextDocument == null) {
			String line = reader.readLine();
			while (line != null && nextDocument == null) {
				nextDocument = documentExtractor.extractDocument(line);
				if (nextDocument != null)
					break;
				line = reader.readLine();
			}
			if (nextDocument != null)
				return true;

			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#initializeImplementation
	 * (org.apache.uima.UimaContext)
	 */
	@Override
	protected void initializeImplementation(UimaContext context) throws ResourceInitializationException {
		try {
			reader = FileReaderUtil.initBufferedReader(collectionFile, encoding);
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
		documentExtractor = (DocumentExtractor) ConstructorUtil.invokeConstructor(documentExtractorClassName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * countDocumentsInCollection()
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		BufferedReader br = FileReaderUtil.initBufferedReader(collectionFile, encoding);
		int lineCount = 0;
		while (br.readLine() != null)
			lineCount++;
		return lineCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#skip()
	 */
	@Override
	protected void skip(int numberToSkip) throws ResourceInitializationException {
		int numSkipped = 0;
		try {
			String line;
			while (numSkipped < numberToSkip && (line = reader.readLine()) != null) {
				if (documentExtractor.extractDocument(line) != null)
					numSkipped++;
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#getNextDocument()
	 */
	@Override
	protected GenericDocument getNextDocument() throws CollectionException, IOException {
		if (!hasNext())
			throw new NoSuchElementException();
		GenericDocument gd = nextDocument;
		nextDocument = null;
		return gd;
	}

	private static final Logger logger = Logger.getLogger(DocumentPerLineCollectionReader.class);

	/**
	 * @param tsd
	 * @param medlineDumpFile
	 * @param numToSkip
	 * @param numToProcess
	 * @param class1
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File medlineDumpFile,
			int numToSkip, int numToProcess, Class<? extends DocumentExtractor> documentExtractorClass,
			Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass)
			throws ResourceInitializationException {
		logger.info("medline dump file is null: " + (medlineDumpFile == null));
		return CollectionReaderFactory.createCollectionReader(DocumentPerLineCollectionReader.class, tsd,
				PARAM_COLLECTION_FILE, medlineDumpFile.getAbsolutePath(), PARAM_DISABLE_PROGRESS, true,
				PARAM_DOCUMENT_EXTRACTOR_CLASS, documentExtractorClass.getName(),
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass.getName(), PARAM_ENCODING, "UTF_8",
				PARAM_NUM2PROCESS, numToProcess, PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME, View.DEFAULT.name());
	}
}
