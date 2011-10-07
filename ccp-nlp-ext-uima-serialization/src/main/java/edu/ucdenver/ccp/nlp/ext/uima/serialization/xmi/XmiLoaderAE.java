/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.xmi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class XmiLoaderAE extends JCasAnnotator_ImplBase {

	/**
	 * Parameter name (mainly used in descriptor files) for the XMI input directory configuration
	 * parameter
	 */
	public static final String PARAM_XMI_DIRECTORY_NAMES = ConfigurationParameterFactory
			.createConfigurationParameterName(XmiLoaderAE.class, "xmiDirectories");

	/**
	 * The directory where XMI files to load are stored
	 */
	@ConfigurationParameter(mandatory = true, description = "The directory where the XMI files to load are stored.")
	private String[] xmiDirectories;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(XmiLoaderAE.class, "documentMetadataExtractorClassName");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetaDataExtractor documentMetaDataExtractor;

	private Logger logger;

	/**
	 * If true, then an exception is thrown if an unknown UIMA type is observed during the
	 * deserialization process
	 */
	boolean THROW_EXCEPTION_ON_UNKNOWN_TYPE_OBSERVATION = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.
	 * UimaContext)
	 */
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		logger = aContext.getLogger();
		System.out.println("DOCUMENT META DATA EXTRACTOR CLASS: " + documentMetadataExtractorClassName);
		documentMetaDataExtractor = (DocumentMetaDataExtractor) ConstructorUtil
				.invokeConstructor(documentMetadataExtractorClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		XmiSerializationSharedData sharedData = new XmiSerializationSharedData();
		String documentId = documentMetaDataExtractor.extractDocumentId(jcas);
		for (String xmiDirectoryName : xmiDirectories) {
			File xmiDirectory = new File(xmiDirectoryName);
			File xmiFile = new File(xmiDirectory, XmiPrinterAE.getXmiFileName(documentId));
			if (!xmiFile.exists())
				logger.log(Level.WARNING, "Expected XMI file does not exist: " + xmiFile.getAbsolutePath());
			else {
				InputStream xmiStream = null;
				try {
					xmiStream = StreamUtil.getEncodingSafeInputStream(xmiFile, CharacterEncoding.UTF_8);
					XmiCasDeserializer.deserialize(xmiStream, jcas.getCas(),
							!THROW_EXCEPTION_ON_UNKNOWN_TYPE_OBSERVATION, sharedData, sharedData.getMaxXmiId());
				} catch (IOException e) {
					throw new AnalysisEngineProcessException(e);
				} catch (SAXException e) {
					throw new AnalysisEngineProcessException(e);
				} finally {
					try {
						xmiStream.close();
					} catch (IOException e) {
						throw new AnalysisEngineProcessException(
								"Warning, this exception may have covered up the real cause of the error.",
								new Object[0], e);
					}
				}
			}
		}
	}

	/**
	 * Returns an initialized XmiLoader {@link AnalysisEngine}
	 * 
	 * @param documentMetaDataExtractorClass
	 * @param tsd
	 * @param xmiDirectories
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createAnalysisEngine(Class<? extends DocumentMetaDataExtractor> documentMetaDataExtractorClass,
			TypeSystemDescription tsd, File... xmiDirectories) throws ResourceInitializationException {
		String[] xmiDirectoryPaths = new String[xmiDirectories.length];
		int index = 0;
		for (File xmiDirectory : xmiDirectories)
			xmiDirectoryPaths[index++] = xmiDirectory.getAbsolutePath();
		return AnalysisEngineFactory.createPrimitive(XmiLoaderAE.class, tsd,
				XmiLoaderAE.PARAM_DOCUMENT_METADATA_EXTRACTOR_CLASS, documentMetaDataExtractorClass.getName(),
				XmiLoaderAE.PARAM_XMI_DIRECTORY_NAMES, xmiDirectoryPaths);
	}

}
