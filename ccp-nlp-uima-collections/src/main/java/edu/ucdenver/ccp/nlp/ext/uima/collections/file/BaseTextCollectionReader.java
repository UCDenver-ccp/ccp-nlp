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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.SofaCapability;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.uima.util.View;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * Contains base functionality required by all Collection Reader implementations.
 * 
 * @author Bill Baumgartner
 * 
 */
@SofaCapability
public abstract class BaseTextCollectionReader extends JCasCollectionReader_ImplBase {
	private static final Logger logger = Logger.getLogger(BaseTextCollectionReader.class);

	private static final String DESCRIPTION_ENCODING = "The encoding parameter should be set to the character encoding of the input "
			+ "collection, e.g. UTF-8.";
	public static final String PARAM_ENCODING = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "encoding");
	@ConfigurationParameter(description = DESCRIPTION_ENCODING)
	protected CharacterEncoding encoding;

	private static final String DESCRIPTION_LANGUAGE = "The encoding parameter should be set to the language of the input collection, "
			+ "e.g. English.";
	public static final String PARAM_LANGUAGE = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "language");
	@ConfigurationParameter(defaultValue = "English", description = DESCRIPTION_LANGUAGE)
	protected String language;

	private static final String DESCRIPTION_NUM2SKIP = "The number to skip parameter enables the user to provide a number of documents "
			+ "to skip before processing begins. This can be useful for testing purposes.";
	public static final String PARAM_NUM2SKIP = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "numberToSkip");
	@ConfigurationParameter(defaultValue = "0", description = DESCRIPTION_NUM2SKIP)
	protected int numberToSkip;

	private static final String DESCRIPTION_NUM2PROCESS = "The number to process parameter allows the user to provide the number of "
			+ "documents that will be processed. This can be useful for testing purposes. Any number < 0 will result in the entire "
			+ "collection being processed. If the number to skip parameter is set, then that number of documents will be skipped before "
			+ "the number of documents to be processed are processed.";
	public static final String PARAM_NUM2PROCESS = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "numberToProcess");
	@ConfigurationParameter(defaultValue = "-1", description = DESCRIPTION_NUM2PROCESS)
	protected int numberToProcess;

	private static final String DESCRIPTION_DOCUMENT_COLLECTION_ID = "This is a user-defined identifier for the particular collection "
			+ "being processed. This optional parameter is often added to annotations as metadata. This could be of use if annotations are "
			+ "being stored outside of the context of the document or collection of documents, e.g. in a database.";
	public static final String PARAM_DOCUMENT_COLLECTION_ID = ConfigurationParameterFactory
			.createConfigurationParameterName(BaseTextCollectionReader.class, "documentCollectionID");
	@ConfigurationParameter(defaultValue = "-1", description = DESCRIPTION_DOCUMENT_COLLECTION_ID)
	protected int documentCollectionID;

	private static final String DESCRIPTION_VIEWNAME = "This parameter enables the user to place the contents of each document into a "
			+ "user-specified view.";
	public static final String PARAM_VIEWNAME = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "viewName");
	@ConfigurationParameter(defaultValue = CAS.NAME_DEFAULT_SOFA, description = DESCRIPTION_VIEWNAME)
	protected String viewName;

	private static final String DESCRIPTION_DISABLE_PROGRESS = "This parameter enables the user to disable progress tracking for this "
			+ "collection reader. Progress tracking requires knowing how many documents in the collection will be processed. For very large "
			+ "collections, simply counting the number of documents can take an inordinate amount of time. If this flag is set to true, then "
			+ "the number of documents in the collection is not computed and the data returned by getProgress() refects simply the number "
			+ "of documents processed.";
	public static final String PARAM_DISABLE_PROGRESS = ConfigurationParameterFactory.createConfigurationParameterName(
			BaseTextCollectionReader.class, "disableProgressTracking");
	@ConfigurationParameter(defaultValue = "false", description = DESCRIPTION_DISABLE_PROGRESS)
	protected boolean disableProgressTracking;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(BaseTextCollectionReader.class, "documentMetadataExtractorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetadataHandler documentMetadataHandler;

	private int processedDocumentCount = 0;

	private int documentsToBeProcessedCount = 0;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			if (!disableProgressTracking)
				documentsToBeProcessedCount = countDocumentsInCollection();
			initializeImplementation(context);
			skip();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		documentMetadataHandler = (DocumentMetadataHandler) ConstructorUtil
				.invokeConstructor(documentMetadataExtractorClassName);
	}

	/**
	 * Implementation-specific initialization
	 * 
	 * @param context
	 * @throws ResourceInitializationException
	 */
	protected abstract void initializeImplementation(UimaContext context) throws ResourceInitializationException;

	/**
	 * Counts the number of documents in the collection that will be processed. If the
	 * disableProgress flag is set to true then the number of documents to be processed is not
	 * computed.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected abstract int countDocumentsInCollection() throws IOException;

	/**
	 * Advances past numberToSkip documents in the collection
	 * 
	 * @throws ResourceInitializationException
	 */
	protected abstract void skip() throws ResourceInitializationException;

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		GenericDocument nextDocument = getNextDocument();
		try {
			initializeJCas(jcas, nextDocument.getDocumentID(), nextDocument.getDocumentText());
		} catch (AnalysisEngineProcessException e) {
			throw new CollectionException(e);
		}
		processedDocumentCount++;
	}

	/**
	 * @return true if the collection reader has read "numbertoProcess" documents
	 */
	protected boolean reachedTargetProcessedDocumentCount() {
		return processedDocumentCount == numberToProcess;
	}
	
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return !reachedTargetProcessedDocumentCount() && hasNextDocument();
	}
	
	protected abstract boolean hasNextDocument() throws IOException, CollectionException;
	

	/**
	 * @return the next document in the collection
	 * @throws IOException
	 * @throws CollectionException
	 */
	protected abstract GenericDocument getNextDocument() throws CollectionException, IOException;

	/**
	 * To be overriden by subclasses for application-specific CAS initialization
	 * 
	 * @param jcas
	 * @param view
	 * @param file
	 * @throws AnalysisEngineProcessException
	 */
	protected void initializeJCas(JCas jcas, String documentId, String text) throws AnalysisEngineProcessException {
		if (this.viewName.equals(View.DEFAULT.name())) {
			jcas.setSofaDataString(text, "text/plain");
			if (this.language != null)
				jcas.setDocumentLanguage(this.language);
		} else {
			JCas view = ViewCreatorAnnotator.createViewSafely(jcas, this.viewName);
			view.setSofaDataString(text, "text/plain");
			if (this.language != null)
				view.setDocumentLanguage(this.language);
		}
		documentMetadataHandler.setDocumentId(jcas, documentId);
		documentMetadataHandler.setDocumentEncoding(jcas, encoding.getCharacterSetName());

		logger.info("Processing document " + processedDocumentCount + " of " + documentsToBeProcessedCount
				+ ".  Loading view: " + this.viewName);

	}

	@Override
	public Progress[] getProgress() {
		if (disableProgressTracking)
			documentsToBeProcessedCount = processedDocumentCount + 1;
		return new Progress[] { new ProgressImpl(processedDocumentCount, documentsToBeProcessedCount, Progress.ENTITIES) };
	}

}
