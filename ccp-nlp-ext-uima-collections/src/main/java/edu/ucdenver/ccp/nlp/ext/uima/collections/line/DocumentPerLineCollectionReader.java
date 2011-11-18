/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

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
import edu.ucdenver.ccp.nlp.core.uima.util.View;
import edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
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
	protected void skip() throws ResourceInitializationException {
		int numSkipped = 0;
		try {
			while (numSkipped < numberToSkip && reader.readLine() != null)
				numSkipped++;
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
			Class<? extends DocumentMetaDataExtractor> documentMetadataExtractorClass)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(DocumentPerLineCollectionReader.class, tsd,
				PARAM_COLLECTION_FILE, medlineDumpFile.getAbsolutePath(), PARAM_DISABLE_PROGRESS, true,
				PARAM_DOCUMENT_EXTRACTOR_CLASS, documentExtractorClass.getName(),
				PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS, documentMetadataExtractorClass.getName(), PARAM_ENCODING,
				"UTF_8", PARAM_NUM2PROCESS, numToProcess, PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME,
				View.DEFAULT.name());
	}
}
