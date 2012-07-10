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
package edu.ucdenver.ccp.nlp.uima.serialization.xmi;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLSerializer;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * An {@link AnalysisEngine} implementation that outputs the CAS to an XMI file. This class is
 * loosely based on a class from the Apache UIMA examples project:
 * org.apache.uima.examples.xmi.XmiWriterCasConsumer
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class XmiPrinterAE extends JCasAnnotator_ImplBase {

	/**
	 * Parameter name (mainly used in descriptor files) for the output directory configuration
	 * parameter
	 */
	public static final String PARAM_OUTPUT_DIRECTORY = ConfigurationParameterFactory.createConfigurationParameterName(
			XmiPrinterAE.class, "outputDirectory");

	/**
	 * File suffix appended to the XMI output files
	 */
	public static final String XMI_FILE_SUFFIX = ".xmi";

	/**
	 * The directory where generated XMI files will be stored
	 */
	@ConfigurationParameter(mandatory = true, description = "The directory where generated XMI files will be stored.")
	private File outputDirectory;

	/* ==== DocumentMetaDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_HANDLER_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(XmiPrinterAE.class, "documentMetadataHandlerClassName");

	/**
	 * The name of the DocumentMetaDataExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetaDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor")
	private String documentMetadataHandlerClassName;

	/**
	 * this {@link DocumentMetadataHandler} will be initialized based on the class name specified by
	 * the documentMetadataExtractorClassName parameter
	 */
	private DocumentMetadataHandler documentMetaDataExtractor;

	/**
	 * This method returns an initialized {@link AnalysisEngine} capable of persisting a CAS as an
	 * XMI file
	 * 
	 * @param tsd
	 *            the type system used by this AE. Must at least include the CCPTypeSystem.
	 * @param outputDirectory
	 *            the directory where the generated XMI files will be stored.
	 * @return an initialized {@link AnalysisEngine} capable of outputing CAS contents to an XMI
	 *         file
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File outputDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(XmiPrinterAE.class, tsd, PARAM_OUTPUT_DIRECTORY,
				outputDirectory.getAbsolutePath());
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass, File outputDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(XmiPrinterAE.class, tsd,
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetaDataExtractorClass.getName(),
				PARAM_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath());
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass, File outputDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(getDescription(tsd, documentMetaDataExtractorClass,
				outputDirectory));
	}

	/**
	 * Creates the output directory if it doesn't exist.
	 * 
	 * @param context
	 * @throws ResourceInitializationException
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		documentMetaDataExtractor = (DocumentMetadataHandler) ConstructorUtil
				.invokeConstructor(documentMetadataHandlerClassName);
		FileUtil.mkdir(outputDirectory);
	}

	/**
	 * Each CAS is output as XMI to a file. The output file name is the document ID + ".xmi" and is
	 * located in the output directory specified by the configuration parameter.
	 * 
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentId = documentMetaDataExtractor.extractDocumentId(jcas);
		File xmiFile = new File(outputDirectory, getXmiFileName(documentId));
		try {
			serializeCasToXmi(jcas, xmiFile);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Given a document Id, this method returns the name of the corresponding XMI file that will be
	 * created
	 * 
	 * @param documentId
	 * @return
	 */
	public static String getXmiFileName(String documentId) {
		return documentId + XMI_FILE_SUFFIX;
	}

	/**
	 * Utility method that handles the XMI serialization
	 * 
	 * @param jcas
	 *            the CAS that will be serialized
	 * @param xmiFile
	 *            the output file where the XMI will be stored
	 * @throws IOException
	 *             if there's an issue writing to the output file
	 * @throws SAXException
	 *             if there's an issue serializing the CAS
	 */
	private void serializeCasToXmi(JCas jcas, File xmiFile) throws IOException, SAXException {
		Writer writer = FileWriterUtil.initBufferedWriter(xmiFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		try {
			XmiCasSerializer serializer = new XmiCasSerializer(jcas.getTypeSystem());
			XMLSerializer xmlSerializer = new XMLSerializer(writer, false);
			serializer.serialize(jcas.getCas(), xmlSerializer.getContentHandler());
		} finally {
			if (writer != null)
				writer.close();
		}

	}

}
