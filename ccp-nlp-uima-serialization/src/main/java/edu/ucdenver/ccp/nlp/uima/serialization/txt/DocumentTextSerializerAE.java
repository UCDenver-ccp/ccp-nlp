package edu.ucdenver.ccp.nlp.uima.serialization.txt;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.pipelines.log.ProcessingErrorLog;
import edu.ucdenver.ccp.nlp.pipelines.log.SerializedFileLog;
import edu.ucdenver.ccp.nlp.uima.shims.ShimDefaults;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * Serializes the CAS document text to a file
 */
public class DocumentTextSerializerAE extends JCasAnnotator_ImplBase {

	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";
	@ConfigurationParameter(mandatory = false, description = "")
	private File outputDirectory;

	/**
	 * If the outputDirectory is not specified, then this view will be used to
	 * search for the source-document-path, which will then be used as the
	 * serialization directory for the output of this annotator.
	 */
	public static final String PARAM_SOURCE_VIEW_NAME = "sourceViewName";
	@ConfigurationParameter(mandatory = false, description = "")
	private String sourceViewName;

	/**
	 * Signifies the view whose document text will be serialized
	 */
	public static final String PARAM_OUTPUT_VIEW_NAME = "outputViewName";
	@ConfigurationParameter(mandatory = false, description = "")
	private String outputViewName;

	/**
	 * If true, the output file will be compressed using gzip
	 */
	public static final String PARAM_COMPRESS_OUTPUT_FLAG = "compressOutput";
	@ConfigurationParameter(mandatory = false, description = "", defaultValue = "true")
	private boolean compressOutput;

	/**
	 * Signifies the file suffix to use when naming the output file. Default =
	 * .txt
	 */
	public static final String PARAM_OUTPUT_FILE_SUFFIX = "outputFileSuffix";
	@ConfigurationParameter(mandatory = false, description = "", defaultValue = ".txt")
	private String outputFileSuffix;

	public static final String PARAM_DOCUMENT_METADATA_HANDLER_CLASS = "documentMetadataHandlerClassName";
	@ConfigurationParameter(mandatory = false, description = "name of the DocumentMetadataHandler implementation to use", defaultValue = ShimDefaults.CCP_DOCUMENT_METADATA_HANDLER_CLASS_NAME)
	private String documentMetadataHandlerClassName;
	private DocumentMetadataHandler documentMetaDataHandler;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		documentMetaDataHandler = (DocumentMetadataHandler) ConstructorUtil
				.invokeConstructor(documentMetadataHandlerClassName);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		/* If an error has been reported, then do not process this CAS. */
		if (JCasUtil.select(jCas, ProcessingErrorLog.class).isEmpty()) {
			String documentId = documentMetaDataHandler.extractDocumentId(jCas);
			String documentText = getDocumentTextToSerialize(jCas);
			File outputFile = getOutputFile(jCas, documentId);
			serializeDocumentText(jCas, documentText, outputFile);
			logSerializedFile(jCas, outputFile);
		}
	}

	private void logSerializedFile(JCas jCas, File outputFile) {
		SerializedFileLog sfLog = new SerializedFileLog(jCas);
		sfLog.setSerializedFile(outputFile.getAbsolutePath());
		sfLog.setFileVersion("LOCAL_TEXT");
		sfLog.addToIndexes();
	}

	/**
	 * Serializes the document text to file and populates the
	 * source-document-path metadata field with the location of the serialized
	 * file
	 * 
	 * @param jCas
	 * @param documentText
	 * @param outputFile
	 * @throws AnalysisEngineProcessException
	 */
	private void serializeDocumentText(JCas jCas, String documentText, File outputFile)
			throws AnalysisEngineProcessException {
		try (BufferedWriter writer = (compressOutput)
				? new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile))))
				: FileWriterUtil.initBufferedWriter(outputFile)) {
			writer.write(documentText);
			if (outputViewName == null) {
				documentMetaDataHandler.setSourceDocumentPath(jCas, outputFile);
				documentMetaDataHandler.setDocumentId(jCas, outputFile.getName());
			} else {
				try {
					JCas view = View_Util.getView(jCas, outputViewName);
					documentMetaDataHandler.setSourceDocumentPath(view, outputFile);
					documentMetaDataHandler.setDocumentId(view, outputFile.getName());
				} catch (CASException e) {
					throw new AnalysisEngineProcessException(e);
				}
			}
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * @param jCas
	 * @param documentId
	 * @return a reference to the output file where the document text will be
	 *         saved
	 * @throws AnalysisEngineProcessException
	 */
	private File getOutputFile(JCas jCas, String documentId) throws AnalysisEngineProcessException {
		String outputFilename = documentId + outputFileSuffix;
		if (compressOutput) {
			outputFilename += ".gz";
		}
		File outputFile = null;
		if (outputDirectory != null) {
			outputFile = new File(outputDirectory, outputFilename);
		} else {
			/*
			 * look for a reference to the source file in the specified source
			 * view and use that directory as the output directory
			 */
			JCas view = null;
			if (sourceViewName == null) {
				view = jCas;
			} else {
				try {
					view = View_Util.getView(jCas, sourceViewName);
				} catch (CASException e) {
					throw new AnalysisEngineProcessException(e);
				}
			}
			File sourceDocumentFile = documentMetaDataHandler.extractSourceDocumentPath(view);
			if (sourceDocumentFile != null) {
				outputFile = new File(sourceDocumentFile.getParentFile(), outputFilename);
			}
		}
		if (outputFile == null) {
			throw new AnalysisEngineProcessException(
					"Unable to determine output directory for document text serialization.", null);
		}
		FileUtil.mkdir(outputFile.getParentFile());
		return outputFile;
	}

	/**
	 * @param jCas
	 * @return the text to be serialized
	 * @throws AnalysisEngineProcessException
	 */
	private String getDocumentTextToSerialize(JCas jCas) throws AnalysisEngineProcessException {
		String documentText = null;
		if (outputViewName == null) {
			documentText = jCas.getDocumentText();
		} else {
			try {
				JCas view = View_Util.getView(jCas, outputViewName);
				documentText = view.getDocumentText();
			} catch (CASException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
		return documentText;
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass, File outputDirectory,
			String outputViewName, boolean compressOutput, String outputFileSuffix)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(DocumentTextSerializerAE.class, tsd,
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass, PARAM_OUTPUT_DIRECTORY,
				outputDirectory.getAbsolutePath(), PARAM_OUTPUT_VIEW_NAME, outputViewName, PARAM_COMPRESS_OUTPUT_FLAG,
				compressOutput, PARAM_OUTPUT_FILE_SUFFIX, outputFileSuffix);
	}

	public static AnalysisEngineDescription getDescription_SaveToSourceFileDirectory(TypeSystemDescription tsd,
			Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass, String sourceViewName,
			String outputViewName, boolean compressOutput, String outputFileSuffix)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(DocumentTextSerializerAE.class, tsd,
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass, PARAM_SOURCE_VIEW_NAME,
				sourceViewName, PARAM_OUTPUT_VIEW_NAME, outputViewName, PARAM_COMPRESS_OUTPUT_FLAG, compressOutput,
				PARAM_OUTPUT_FILE_SUFFIX, outputFileSuffix);
	}

}
