package edu.ucdenver.ccp.nlp.uima.serialization.xmi;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.io.StreamUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.uima.shims.ShimDefaults;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This {@link AnalysisEngine} implementation is capable of loading UIMA XMI
 * files. File can be loaded from the file system or classpath. Compressed (.gz)
 * XMI files can be used.
 * 
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class XmiLoaderAE extends JCasAnnotator_ImplBase {

	/**
	 * The XmiLoaderAE can load XMI files from either the classpath or the
	 * filesystem. This enum is used to indicate the location of the XMI files
	 * to load.
	 * 
	 * @author Colorado Computational Pharmacology, UC Denver;
	 *         ccpsupport@ucdenver.edu
	 * 
	 */
	public enum XmiPathType {
		/**
		 * Indicates the XMI files to load are on the classpath, therefore the
		 * xmiDirectoriesOrPaths input parameter represents paths on the
		 * classpath
		 */
		CLASSPATH,
		/**
		 * Indicates the XMI files to load are on the file system, therefore the
		 * xmiDirectoriesOrPaths input parameter represents a file system
		 * directory
		 */
		FILESYSTEM
	}

	public static final String PARAM_XMI_PATH_TYPE = "xmiPathType";

	@ConfigurationParameter(mandatory = true, description = "Indicates the path type for the values in the xmiPaths configuration parameter, CLASSPATH or FILESYSTEM")
	private XmiPathType xmiPathType;

	public enum XmiFileCompressionType {
		/**
		 * Indicates that the XMI files are gzipped
		 */
		GZ,
		/**
		 * Indicates that the XMI files are not compressed
		 */
		NONE
	}

	public static final String PARAM_XMI_FILE_COMPRESSION_TYPE = "xmiFileCompressionType";

	@ConfigurationParameter(defaultValue = "NONE", description = "Indicates the compression type used to store the XMI files, GZ or NONE. This has ramifications on whether they are looked for using a .gz suffix or note, and how they are loaded.")
	private XmiFileCompressionType xmiFileCompressionType;

	/**
	 * Parameter name (mainly used in descriptor files) for the XMI input
	 * directory configuration parameter
	 */
	public static final String PARAM_XMI_PATH_NAMES = "xmiPaths";

	/**
	 * The directory where XMI files to load are stored
	 */
	@ConfigurationParameter(mandatory = false, description = "The directory or classpath where the XMI files to load are stored. "
			+ "If not set, then this AnalysisEngine will attempt to obtain the directory of the source file for this CAS. "
			+ "See DocumentMetadataExtractor for details.")
	private String[] xmiPaths;

	public static final String PARAM_XMI_FILENAME_INFIXES = "infixes";
	@ConfigurationParameter(mandatory = false, description = "An optional array of infixes to use when specifying which XMI files to load.")
	private String[] infixes;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute
	 * extractor implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_HANDLER_CLASS = "documentMetadataHandlerClassName";

	/**
	 * The name of the DocumentMetadataHandler implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetadataHandler implementation to use", defaultValue = ShimDefaults.CCP_DOCUMENT_METADATA_HANDLER_CLASS_NAME)
	private String documentMetadataHandlerClassName;

	/**
	 * this {@link DocumentMetadataHandler} will be initialized based on the
	 * class name specified by the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetadataHandler documentMetaDataHandler;

	private Logger logger;

	/**
	 * If true, then an exception is thrown if an unknown UIMA type is observed
	 * during the deserialization process
	 */
	boolean THROW_EXCEPTION_ON_UNKNOWN_TYPE_OBSERVATION = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(
	 * org.apache.uima. UimaContext)
	 */
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		logger = aContext.getLogger();
		System.out.println("DOCUMENT META DATA EXTRACTOR CLASS: " + documentMetadataHandlerClassName);
		documentMetaDataHandler = (DocumentMetadataHandler) ConstructorUtil
				.invokeConstructor(documentMetadataHandlerClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.
	 * apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		XmiSerializationSharedData sharedData = new XmiSerializationSharedData();
		String documentId = documentMetaDataHandler.extractDocumentId(jcas);

		List<String> filePaths = new ArrayList<String>();
		if (xmiPaths != null) {
			filePaths.addAll(Arrays.asList(xmiPaths));
		} else {
			if (xmiPathType == XmiPathType.FILESYSTEM) {
				/*
				 * extract the source document path from the CAS and use that as
				 * the directory to load from
				 */
				File sourceDocumentPath = documentMetaDataHandler.extractSourceDocumentPath(jcas);
				if (sourceDocumentPath != null) {
					filePaths.add(sourceDocumentPath.getParentFile().getAbsolutePath());
				}
			}
		}

		if (filePaths.isEmpty()) {
			throw new AnalysisEngineProcessException(
					"Unable to determine location path for XMI file loading. Parameters are: xmiPathType: "
							+ xmiPathType.name() + " xmiPaths: "
							+ ((xmiPaths == null) ? "null" : Arrays.toString(xmiPaths)) + " Source document path: "
							+ documentMetaDataHandler.extractSourceDocumentPath(jcas),
					null);
		}

		List<String> infixesToLoad = new ArrayList<String>();
		if (infixes == null) {
			infixesToLoad.add(null);
		} else {
			infixesToLoad.addAll(Arrays.asList(infixes));
		}

		for (String xmiPathBase : filePaths) {
			for (String infix : infixesToLoad) {
				InputStream xmiStream = initializeXmiInputStream(documentId, xmiPathBase, infix);
				if (xmiStream != null) {
					try {
						XmiCasDeserializer.deserialize(xmiStream, jcas.getCas(),
								!THROW_EXCEPTION_ON_UNKNOWN_TYPE_OBSERVATION, sharedData, sharedData.getMaxXmiId());
						xmiStream.close();
					} catch (IOException e) {
						throw new AnalysisEngineProcessException(e);
					} catch (SAXException e) {
						throw new AnalysisEngineProcessException(e);
					}
				}
			}
		}
	}

	/**
	 * @param documentId
	 * @param xmiPathBase
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	private InputStream initializeXmiInputStream(String documentId, String xmiPathBase, String infix)
			throws AnalysisEngineProcessException {
		InputStream xmiStream = null;
		if (xmiPathType.equals(XmiPathType.FILESYSTEM)) {
			xmiStream = getStreamFromFile(documentId, xmiPathBase, infix);
		} else {
			xmiStream = getStreamFromClasspath(documentId, xmiPathBase, infix);
		}
		if (xmiFileCompressionType.equals(XmiFileCompressionType.GZ)) {
			try {
				xmiStream = new GZIPInputStream(xmiStream);
			} catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
		return xmiStream;
	}

	/**
	 * @param documentId
	 * @param xmiPathBase
	 * @return
	 */
	private InputStream getStreamFromClasspath(String documentId, String xmiPathBase, String infix) {
		InputStream xmiStream = null;
		String xmiFilePath = xmiPathBase + StringConstants.FORWARD_SLASH
				+ XmiPrinterAE.getXmiFileName(documentId, infix, xmiFileCompressionType == XmiFileCompressionType.GZ);
		xmiStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), xmiFilePath);
		if (xmiStream == null) {
			logger.log(Level.WARNING, "Unable to load XMI file from classpath: " + xmiFilePath);
		}
		return xmiStream;
	}

	/**
	 * @param documentId
	 * @param xmiPathBase
	 * @param xmiStream
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	private InputStream getStreamFromFile(String documentId, String xmiPathBase, String infix)
			throws AnalysisEngineProcessException {
		InputStream xmiStream = null;
		File xmiDirectory = new File(xmiPathBase);
		String xmiFileName = XmiPrinterAE.getXmiFileName(documentId, infix,
				(xmiFileCompressionType == XmiFileCompressionType.GZ));
		File xmiFile = new File(xmiDirectory, xmiFileName);
		if (!xmiFile.exists()) {
			logger.log(Level.WARNING, "Expected XMI file does not exist: " + xmiFile.getAbsolutePath());
		} else {
			try {
				xmiStream = StreamUtil.getEncodingSafeInputStream(xmiFile, CharacterEncoding.UTF_8);
			} catch (FileNotFoundException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
		return xmiStream;
	}

	/**
	 * Returns an initialized XmiLoader {@link AnalysisEngine}
	 * 
	 * @param documentMetaDataHandlerClass
	 * @param tsd
	 * @param xmiPathType
	 * @param xmiDirectories
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> documentMetaDataHandlerClass, XmiPathType xmiPathType,
			XmiFileCompressionType xmiCompressionType, List<String> infixes, String... xmiPaths)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(XmiLoaderAE.class, tsd,
				XmiLoaderAE.PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetaDataHandlerClass.getName(),
				PARAM_XMI_PATH_TYPE, xmiPathType.name(), PARAM_XMI_FILE_COMPRESSION_TYPE, xmiCompressionType.name(),
				XmiLoaderAE.PARAM_XMI_PATH_NAMES, xmiPaths, XmiLoaderAE.PARAM_XMI_FILENAME_INFIXES,
				infixes.toArray(new String[infixes.size()]));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription_LoadFromSourceFileDirectory(
			TypeSystemDescription tsd, Class<? extends DocumentMetadataHandler> documentMetaDataHandlerClass,
			XmiFileCompressionType xmiCompressionType, String... infixes) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(XmiLoaderAE.class, tsd,
				XmiLoaderAE.PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetaDataHandlerClass.getName(),
				PARAM_XMI_PATH_TYPE, XmiPathType.FILESYSTEM, PARAM_XMI_FILE_COMPRESSION_TYPE, xmiCompressionType.name(),
				XmiLoaderAE.PARAM_XMI_FILENAME_INFIXES, infixes);
	}

}
