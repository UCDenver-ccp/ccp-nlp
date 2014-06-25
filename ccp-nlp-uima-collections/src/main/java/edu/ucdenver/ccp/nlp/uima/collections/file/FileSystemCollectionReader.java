package edu.ucdenver.ccp.nlp.uima.collections.file;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.SofaCapability;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * A generic Collection Reader that deals with collections on the file system.
 * <ul>
 * <li>Input can be either a directory of files or a single file. The user has the option to recurse
 * into the directory structure.</li>
 * <li>Progress tracking can be toggled. This feature may be used when the number of documents is
 * large, but unknown. Turning off progress tracking will skip execution of code that counts the
 * number of documents in the collection.</li>
 * <li>Documents are processed on a depth-first basis when a directory hierarchy is present.</li>
 * <li>The name of the CAS view that is created can be controlled through a parameter:
 * PARAM_VIEWNAME</li>
 * <ul>
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@SofaCapability
public class FileSystemCollectionReader extends BaseTextCollectionReader {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FileSystemCollectionReader.class);
	private static final String COMPONENT_DESCRIPTION = "A generic Collection Reader that deals with collections on the file system. \n\n"
			+ "* Input can be either a directory of files or a single file. The user has the option to recurse into the directory structure.\n\n"
			+ "* Progress tracking can be toggled. This feature may be used when the number of documents is large, but unknown. "
			+ "Turning off progress tracking will skip execution of code that counts the number of documents in the collection.\n\n"
			+ "* Documents are processed on a depth-first basis when a directory hierarchy is present.\n\n"
			+ "* The name of the CAS view that is created can be controlled through a parameter: PARAM_VIEWNAME";

	private static final String COMPONENT_VENDOR = "UC Denver - CCP";

	private static final String DESCRIPTION_BASE_FILE = "The BaseFileOrDirectory parameter must point to either a single file "
			+ "or a directory from which the collection will be drawn.";
	public static final String PARAM_BASE_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "baseFileOrDirectory");
	@ConfigurationParameter(mandatory = true, description = DESCRIPTION_BASE_FILE)
	protected File baseFileOrDirectory;

	private static final String DESCRIPTION_RECURSE = "If the RecurseIntoDirectory parameter is true and the BaseFileOrDirectory "
			+ "parameter points to a directory, then the CollectionReader will recurse into the directory structure (if there are "
			+ "directories inside the base directory). If false, then the Collection Reader will process only those files in the top "
			+ "level of the base directory.";
	public static final String PARAM_RECURSE = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "recurseIntoDirectory");
	@ConfigurationParameter(defaultValue = "false", description = DESCRIPTION_RECURSE)
	protected boolean recurseIntoDirectory;

	private static final String DESCRIPTION_FILESUFFIXES_TO_PROCESS = "The parameter will filter the documents in the collection based "
			+ "on their file suffix. Only those files whose suffixes are represented by this parameter will be processed. If left empty, "
			+ "then all files are processed regardless of suffix.";
	public static final String PARAM_FILESUFFIXES_TO_PROCESS = ConfigurationParameterFactory
			.createConfigurationParameterName(FileSystemCollectionReader.class, "fileSuffixesToProcess");
	@ConfigurationParameter(description = DESCRIPTION_FILESUFFIXES_TO_PROCESS)
	protected String[] fileSuffixesToProcess;

	private Iterator<File> fileIterator;

	/**
	 * @param tsd
	 * @param baseFileOrDirectory
	 * @param recurse
	 * @param encoding
	 * @param language
	 * @param disableProgress
	 * @param num2process
	 * @param num2skip
	 * @param documentMetadataHandlerClass
	 * @param viewName
	 * @param fileSuffixesToProcess
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(TypeSystemDescription tsd, File baseFileOrDirectory,
			boolean recurse, CharacterEncoding encoding, String language, boolean disableProgress, int num2process,
			int num2skip, Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass, String viewName,
			String... fileSuffixesToProcess) throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(FileSystemCollectionReader.class, tsd, PARAM_BASE_FILE,
				baseFileOrDirectory.getAbsolutePath(), PARAM_ENCODING, encoding.name(), PARAM_RECURSE, recurse,
				PARAM_DISABLE_PROGRESS, disableProgress, PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess,
				PARAM_LANGUAGE, language, PARAM_NUM2PROCESS, num2process, PARAM_NUM2SKIP, num2skip, PARAM_VIEWNAME,
				viewName, PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass.getName());
	}

	/**
	 * This method creates a collection reader that uses the CcpDocumentMetadataHandler class by
	 * default
	 * 
	 * @param tsd
	 * @param baseFileOrDirectory
	 * @param recurse
	 * @param encoding
	 * @param language
	 * @param disableProgress
	 * @param num2process
	 * @param num2skip
	 * @param viewName
	 * @param fileSuffixesToProcess
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(TypeSystemDescription tsd, File baseFileOrDirectory,
			boolean recurse, CharacterEncoding encoding, String language, boolean disableProgress, int num2process,
			int num2skip, String viewName, String... fileSuffixesToProcess) throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(FileSystemCollectionReader.class, tsd, PARAM_BASE_FILE,
				baseFileOrDirectory.getAbsolutePath(), PARAM_ENCODING, encoding.name(), PARAM_RECURSE, recurse,
				PARAM_DISABLE_PROGRESS, disableProgress, PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess,
				PARAM_LANGUAGE, language, PARAM_NUM2PROCESS, num2process, PARAM_NUM2SKIP, num2skip, PARAM_VIEWNAME,
				viewName);
	}

	/**
	 * Counts the number of documents in the collection that will be processed. This is done by
	 * cycling through the directory structure and counting file that will be processed. If the
	 * disableProgress flag is set to true then the number of documents to be processed is not
	 * computed.
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		if (disableProgressTracking) {
			return processedDocumentCount;
		}
		Iterator<File> fileIter = FileUtil.getFileIterator(baseFileOrDirectory, recurseIntoDirectory,
				fileSuffixesToProcess);
		skip(fileIter, numberToSkip);
		int count = 0;
		while (fileIter.hasNext()) {
			fileIter.next();
			count++;
			if (numberToProcess > -1 && count == numberToProcess) {
				break;
			}
		}
		return count;
	}

	/**
	 * Advances the input Iterator<File> forward numberToSkip times.
	 * 
	 * @param fileIter
	 */
	private void skip(Iterator<File> fileIter, int numberToSkip) {
		int numSkipped = 0;
		while (fileIter.hasNext() && numSkipped < numberToSkip) {
			fileIter.next();
			numSkipped++;
		}
	}

	@Override
	public void close() throws IOException {
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
			fileIterator = FileUtil.getFileIterator(baseFileOrDirectory, recurseIntoDirectory, fileSuffixesToProcess);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#skip()
	 */
	@Override
	protected void skip(int numberToSkip) throws ResourceInitializationException {
		skip(fileIterator, numberToSkip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#hasNextDocument()
	 */
	@Override
	protected boolean hasNextDocument() throws IOException, CollectionException {
		return fileIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#getNextDocument()
	 */
	@Override
	protected GenericDocument getNextDocument() throws CollectionException, IOException {
		File file = fileIterator.next();
		String documentId = file.getName();
		String text = FileUtil.copyToString(file, this.encoding);
		GenericDocument gd = new GenericDocument(documentId);
		gd.setDocumentText(text);
		return gd;
	}

}
