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
package edu.ucdenver.ccp.nlp.ext.uima.collections.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.SofaCapability;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.nlp.core.uima.build.XmlDescriptorWriter;

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
 * TODO: add capability to handle gz files?
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@SofaCapability
public class FileSystemCollectionReader extends JCasCollectionReader_ImplBase {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FileSystemCollectionReader.class);
	private static final String COMPONENT_DESCRIPTION = "A generic Collection Reader that deals with collections on the file system. \n\n"
			+ "* Input can be either a directory of files or a single file. The user has the option to recurse into the directory structure.\n\n"
			+ "* Progress tracking can be toggled. This feature may be used when the number of documents is large, but unknown. "
			+ "Turning off progress tracking will skip execution of code that counts the number of documents in the collection.\n\n"
			+ "* Documents are processed on a depth-first basis when a directory hierarchy is present.\n\n"
			+ "* The name of the CAS view that is created can be controlled through a parameter: PARAM_VIEWNAME";

	private static final String COMPONENT_VENDOR = "UC Denver - CCP";
	// public enum Parameter {
	// INPUT_FILE_OR_DIRECTORY, HAS_FILE_NAMES_TO_PROCESS, HAS_FILE_NAMES_FILE
	// }

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

	private static final String DESCRIPTION_ENCODING = "The encoding parameter should be set to the character encoding of the input "
			+ "collection, e.g. UTF-8.";
	public static final String PARAM_ENCODING = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "encoding");
	@ConfigurationParameter(description = DESCRIPTION_ENCODING)
	protected String encoding;

	private static final String DESCRIPTION_LANGUAGE = "The encoding parameter should be set to the language of the input collection, "
			+ "e.g. English.";
	public static final String PARAM_LANGUAGE = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "language");
	@ConfigurationParameter(defaultValue = "English", description = DESCRIPTION_LANGUAGE)
	protected String language;

	private static final String DESCRIPTION_NUM2SKIP = "The number to skip parameter enables the user to provide a number of documents "
			+ "to skip before processing begins. This can be useful for testing purposes.";
	public static final String PARAM_NUM2SKIP = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "numberToSkip");
	@ConfigurationParameter(defaultValue = "0", description = DESCRIPTION_NUM2SKIP)
	protected int numberToSkip;

	private static final String DESCRIPTION_NUM2PROCESS = "The number to process parameter allows the user to provide the number of "
			+ "documents that will be processed. This can be useful for testing purposes. Any number < 0 will result in the entire "
			+ "collection being processed. If the number to skip parameter is set, then that number of documents will be skipped before "
			+ "the number of documents to be processed are processed.";
	public static final String PARAM_NUM2PROCESS = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "numberToProcess");
	@ConfigurationParameter(defaultValue = "-1", description = DESCRIPTION_NUM2PROCESS)
	protected int numberToProcess;

	private static final String DESCRIPTION_FILESUFFIXES_TO_PROCESS = "The parameter will filter the documents in the collection based "
			+ "on their file suffix. Only those files whose suffixes are represented by this parameter will be processed. If left empty, "
			+ "then all files are processed regardless of suffix.";
	public static final String PARAM_FILESUFFIXES_TO_PROCESS = ConfigurationParameterFactory
			.createConfigurationParameterName(FileSystemCollectionReader.class, "fileSuffixesToProcess");
	@ConfigurationParameter(description = DESCRIPTION_FILESUFFIXES_TO_PROCESS)
	protected String[] fileSuffixesToProcess;

	private static final String DESCRIPTION_DOCUMENT_COLLECTION_ID = "This is a user-defined identifier for the particular collection "
			+ "being processed. This optional parameter is often added to annotations as metadata. This could be of use if annotations are "
			+ "being stored outside of the context of the document or collection of documents, e.g. in a database.";
	public static final String PARAM_DOCUMENT_COLLECTION_ID = ConfigurationParameterFactory
			.createConfigurationParameterName(FileSystemCollectionReader.class, "documentCollectionID");
	@ConfigurationParameter(defaultValue = "-1", description = DESCRIPTION_DOCUMENT_COLLECTION_ID)
	protected int documentCollectionID;

	private static final String DESCRIPTION_VIEWNAME = "This parameter enables the user to place the contents of each document into a "
			+ "user-specified view.";
	public static final String PARAM_VIEWNAME = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "viewName");
	@ConfigurationParameter(defaultValue = CAS.NAME_DEFAULT_SOFA, description = DESCRIPTION_VIEWNAME)
	protected String viewName;

	private static final String DESCRIPTION_DISABLE_PROGRESS = "This parameter enables the user to disable progress tracking for this "
			+ "collection reader. Progress tracking requires knowing how many documents in the collection will be processed. For very large "
			+ "collections, simply counting the number of documents can take an inordinate amount of time. If this flag is set to true, then "
			+ "the number of documents in the collection is not computed and the data returned by getProgress() refects simply the number "
			+ "of documents processed.";
	public static final String PARAM_DISABLE_PROGRESS = ConfigurationParameterFactory.createConfigurationParameterName(
			FileSystemCollectionReader.class, "disableProgressTracking");
	@ConfigurationParameter(defaultValue = "false", description = DESCRIPTION_DISABLE_PROGRESS)
	protected boolean disableProgressTracking;

	// private static final String DESCRIPTION_FILE_NAMES_TO_PROCESS_FILE =
	// "This parameter references a file that contains the names of documents "
	// +
	// "to include in the collection. Files must be under the base directory to be included. The file suffixes parameter can be used in "
	// +
	// "conjunction with this parameter. The recurse parameter has no effect when used in conjunction with this parameter.";
	// public static final String PARAM_FILE_NAMES_TO_PROCESS_FILE = ConfigurationParameterFactory
	// .createConfigurationParameterName(FileSystemCollectionReader.class, "disableProgress");
	// @ConfigurationParameter(description = DESCRIPTION_FILE_NAMES_TO_PROCESS_FILE)
	// private File fileNamesToProcessFile;
	//
	// private static final String DESCRIPTION_FILE_NAMES_TO_PROCESS =
	// "This parameter allows the user to explicitly define the names of files "
	// +
	// "to include in the collection. Files must exist under the base directory to be included. The file suffixes parameter can be used in "
	// +
	// "conjunction with this parameter. The recurse parameter has no effect when used in conjunction with this parameter.";
	// public static final String PARAM_FILE_NAMES_TO_PROCESS = ConfigurationParameterFactory
	// .createConfigurationParameterName(FileSystemCollectionReader.class, "disableProgress");
	// @ConfigurationParameter(description = DESCRIPTION_FILE_NAMES_TO_PROCESS)
	// private String[] fileNamesToProcess;

	private int processedDocumentCount = 0;

	private int documentsToBeProcessedCount = -1;

	private Iterator<File> fileIterator;

	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File baseFileOrDirectory,
			boolean recurse, CharacterEncoding encoding, String language, boolean disableProgress,
			int documentCollectionID, int num2process, int num2skip, String viewName, String... fileSuffixesToProcess)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(FileSystemCollectionReader.class, tsd, PARAM_BASE_FILE,
				baseFileOrDirectory.getAbsolutePath(), PARAM_ENCODING, encoding.getCharacterSetName(), PARAM_RECURSE,
				recurse, PARAM_DISABLE_PROGRESS, disableProgress, PARAM_DOCUMENT_COLLECTION_ID, documentCollectionID,
				PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess, PARAM_LANGUAGE, language, PARAM_NUM2PROCESS,
				num2process, PARAM_NUM2SKIP, num2skip, PARAM_VIEWNAME, viewName);
	}

	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File baseFileOrDirectory,
			CharacterEncoding encoding, boolean recurse) throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(FileSystemCollectionReader.class, tsd, PARAM_BASE_FILE,
				baseFileOrDirectory.getAbsolutePath(), PARAM_ENCODING, encoding.getCharacterSetName(), PARAM_RECURSE,
				recurse);
	}

	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File baseFileOrDirectory,
			CharacterEncoding encoding, boolean recurse, int numToSkip, String... fileSuffixesToProcess)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(FileSystemCollectionReader.class, tsd, PARAM_BASE_FILE,
				baseFileOrDirectory.getAbsolutePath(), PARAM_ENCODING, encoding.getCharacterSetName(), PARAM_RECURSE,
				recurse, PARAM_NUM2SKIP, numToSkip, PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess);
	}

	public static void exportXmlDescriptor(File baseDescriptorDirectory, String version) {
		try {
			Class<FileSystemCollectionReader> cls = FileSystemCollectionReader.class;
			CollectionReaderDescription crd = CollectionReaderFactory.createDescription(cls, PARAM_BASE_FILE,
					"[BASE FILE OR DIRECTORY GOES HERE]", PARAM_ENCODING, String.format(
							"[FILE ENCODING GOES HERE - %s, %s, or %s]", CharacterEncoding.UTF_8,
							CharacterEncoding.US_ASCII, CharacterEncoding.ISO_8859_1), PARAM_RECURSE, true,
					PARAM_DISABLE_PROGRESS, true, PARAM_DOCUMENT_COLLECTION_ID, -1, PARAM_FILESUFFIXES_TO_PROCESS,
					new String[] { ".suffix1", ".suffix2" }, PARAM_LANGUAGE, "english", PARAM_NUM2PROCESS, -1,
					PARAM_NUM2SKIP, 0, PARAM_VIEWNAME, CAS.NAME_DEFAULT_SOFA);
			ResourceMetaData metaData = crd.getMetaData();
			metaData.setName(cls.getSimpleName());
			metaData.setDescription(COMPONENT_DESCRIPTION);
			metaData.setVendor(COMPONENT_VENDOR);
			metaData.setVersion(version);
			crd.setMetaData(metaData);
			XmlDescriptorWriter.exportXmlDescriptor(cls, crd, baseDescriptorDirectory);
		} catch (ResourceInitializationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		validateParameterSet();
		try {
			documentsToBeProcessedCount = countDocumentsInCollection();
			fileIterator = FileUtil.getFileIterator(baseFileOrDirectory, recurseIntoDirectory, fileSuffixesToProcess);
			skip(fileIterator);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	private void validateParameterSet() {
		// Set<Parameter> parameterSet = null;
		//
		// EnumSet.of(Parameter.INPUT_FILE_OR_DIRECTORY);

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
	private int countDocumentsInCollection() throws IOException {
		if (disableProgressTracking)
			return processedDocumentCount;
		Iterator<File> fileIter = FileUtil.getFileIterator(baseFileOrDirectory, recurseIntoDirectory,
				fileSuffixesToProcess);
		skip(fileIter);
		int count = 0;
		while (fileIter.hasNext()) {
			fileIter.next();
			count++;
			if (numberToProcess > -1 && count == numberToProcess)
				break;
		}
		return count;
	}

	/**
	 * Advances the input Iterator<File> forward numberToSkip times.
	 * 
	 * @param fileIter
	 */
	private void skip(Iterator<File> fileIter) {
		int numSkipped = 0;
		while (fileIter.hasNext() && numSkipped < numberToSkip) {
			fileIter.next();
			numSkipped++;
		}
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if (numberToProcess < 0) {
			return fileIterator.hasNext();
		}
		return fileIterator.hasNext() && processedDocumentCount < numberToProcess;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		JCas view;
		try {
			view = ViewCreatorAnnotator.createViewSafely(jcas, this.viewName);
		} catch (AnalysisEngineProcessException e) {
			throw new CollectionException(e);
		}
		File file = fileIterator.next();
		logger.info("Processing document " + processedDocumentCount + " of " + documentsToBeProcessedCount
				+ ".  Loading view: " + view.getViewName() + " with contents of file:" + file);
		String text = FileUtil.copyToString(file, CharacterEncoding.valueOf(this.encoding.replaceAll("-", "_")));
		// TODO sofa data string should depend on the view, e.g. xml for the xmlView
		view.setSofaDataString(text, "text/plain");

		if (this.language != null)
			view.setDocumentLanguage(this.language);

		initializeJCas(jcas, view, file);

		processedDocumentCount++;
	}

	/**
	 * To be overriden by subclasses for application-specific CAS initialization
	 * 
	 * @param jcas
	 * @param view
	 * @param file
	 */
	protected void initializeJCas(JCas jcas, JCas view, File file) {
		String documentID = file.getName();
		CCPDocumentInformation srcDocInfo = new CCPDocumentInformation(view);
		srcDocInfo.setDocumentID(documentID);
		// srcDocInfo.setUri("file:" + documentID);
		srcDocInfo.setDocumentCollectionID(documentCollectionID);
		srcDocInfo.setDocumentSize(view.getDocumentText().length());
		srcDocInfo.setEncoding(encoding);
		srcDocInfo.addToIndexes();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public Progress[] getProgress() {
		if (disableProgressTracking)
			documentsToBeProcessedCount = processedDocumentCount + 1;
		return new Progress[] { new ProgressImpl(processedDocumentCount, documentsToBeProcessedCount, Progress.ENTITIES) };
	}

}
