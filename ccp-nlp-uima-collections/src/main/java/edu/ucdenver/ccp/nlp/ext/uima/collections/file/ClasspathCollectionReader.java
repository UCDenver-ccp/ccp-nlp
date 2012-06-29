/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.FileArchiveUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.uima.util.View;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClasspathCollectionReader extends BaseTextCollectionReader {

	/* ==== classpath directory configuration ==== */
	public static final String PARAM_COLLECTION_PATH = ConfigurationParameterFactory.createConfigurationParameterName(
			ClasspathCollectionReader.class, "collectionPath");

	@ConfigurationParameter(mandatory = true, description = "The path on the classpath to the directory containing the collection. This path should not start with a forward slash.")
	protected String collectionPath;

	/**
	 * Paths on the classpath to all documents in the collection
	 */
	private List<String> documentPaths;

	private int documentIndex = 0;

	private GenericDocument nextDocument = null;

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
			documentPaths = ClassPathUtil.listResourceDirectory(getClass(), collectionPath);
			if (documentPaths.size() == 0)
				throw new ResourceInitializationException("No class path documents were found using path: "
						+ collectionPath, new Object[0]);
			Collections.sort(documentPaths);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} catch (URISyntaxException e) {
			throw new ResourceInitializationException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * countDocumentsInCollection()
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		return documentPaths.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#skip()
	 */
	@Override
	protected void skip() throws ResourceInitializationException {
		documentIndex = numberToSkip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#hasNextDocument()
	 */
	@Override
	protected boolean hasNextDocument() throws IOException, CollectionException {
		if (nextDocument == null) {
			if (documentIndex++ < documentPaths.size()) {
				String documentPath = documentPaths.get(documentIndex - 1);
				String documentId = documentPath.substring(documentPath.lastIndexOf(StringConstants.FORWARD_SLASH) + 1);
				if (FileArchiveUtil.isZippedFile(new File(documentId)))
					documentId = FileArchiveUtil.getUnzippedFileName(documentId);
				String documentText = getTextFromClasspathResource(documentPath);
				nextDocument = new GenericDocument(documentId);
				nextDocument.setDocumentText(documentText);
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * @param documentPath
	 * @return
	 * @throws IOException
	 */
	private String getTextFromClasspathResource(String documentPath) throws IOException {
		InputStream is = ClassPathUtil.getResourceStreamFromClasspath(getClass(), documentPath);
		if (FileArchiveUtil.isGzipFile(new File(documentPath)))
			is = new GZIPInputStream(is);
		return StreamUtil.toString(new InputStreamReader(is, encoding.getDecoder()));
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
	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, String collectionPath,
			int numToSkip, int numToProcess, Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(ClasspathCollectionReader.class, tsd,
				PARAM_COLLECTION_PATH, collectionPath, PARAM_DISABLE_PROGRESS, true,
				PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS, documentMetadataHandlerClass.getName(), PARAM_ENCODING,
				"UTF_8", PARAM_NUM2PROCESS, numToProcess, PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME,
				View.DEFAULT.name());
	}

}
