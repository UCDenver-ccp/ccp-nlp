package edu.ucdenver.ccp.nlp.uima.serialization.rdf;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
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
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.datasource.rdfizer.rdf.ice.RdfUtil.RdfFormat;
import edu.ucdenver.ccp.nlp.uima.shims.ShimDefaults;
import edu.ucdenver.ccp.nlp.uima.util.View_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

public class RdfSerialization_AE extends JCasAnnotator_ImplBase {

	/**
	 * Parameter name used in the UIMA descriptor file for the document metadata
	 * extractor implementation to use
	 */
	public static final String PARAM_DOCUMENT_METADATA_HANDLER_CLASS = "documentMetadataHandlerClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentMetadataHandler implementation to use", defaultValue = ShimDefaults.CCP_DOCUMENT_METADATA_HANDLER_CLASS_NAME)
	private String documentMetadataHandlerClassName;
	private DocumentMetadataHandler documentMetadataHandler;

	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data
	 * extractor implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = "annotationDataExtractorClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor")
	private String annotationDataExtractorClassName;
	private AnnotationDataExtractor annotationDataExtractor;

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute
	 * extractor implementation to use
	 */
	public static final String PARAM_DOCUMENT_RDF_GENERATOR_CLASS = "documentRdfGeneratorClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the DocumentRdfGenerator implementation to use")
	private String documentRdfGeneratorClassName;
	private DocumentRdfGenerator documentRdfGenerator;

	/**
	 * Parameter name used in the UIMA descriptor file for the annotation RDF
	 * generator implementation to use
	 */
	public static final String PARAM_ANNOTATION_RDF_GENERATOR_CLASS = "annotationRdfGeneratorClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationRdfGenerator implementation to use")
	private String annotationRdfGeneratorClassName;
	private AnnotationRdfGenerator annotationRdfGenerator;

	// useful when we are serializing relations
	// /**
	// * Parameter name used in the UIMA descriptor file for the semantic
	// statement (RDF) generator
	// * implementation to use
	// */
	// public static final String PARAM_SEMANTIC_STATEMENT_GENERATOR_CLASS =
	// "semanticStatementGeneratorClassName";
	// @ConfigurationParameter(mandatory = false, description = "name of the
	// SemanticStatementGenerator implementation to use")
	// private String semanticStatementGeneratorClassName;
	// private SemanticStatementGenerator semanticStatementGenerator;

	/**
	 * Parameter name used in the UIMA descriptor file for the URI Factory
	 * implementation to use
	 */
	public static final String PARAM_URI_FACTORY_CLASS = "uriFactoryClassName";
	@ConfigurationParameter(mandatory = true, description = "name of the UriFactory implementation to use")
	private String uriFactoryClassName;
	private UriFactory uriFactory;

	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";
	@ConfigurationParameter(mandatory = false, description = "")
	private File outputDirectory;

	/**
	 * Signifies the file infix to use when naming the output file.
	 */
	public static final String PARAM_OUTPUT_FILE_INFIX = "outputFileInfix";
	@ConfigurationParameter(mandatory = true, description = "")
	private String outputFileInfix;

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

	public final static String PARAM_RDF_FORMAT = "rdfFormat";
	@ConfigurationParameter(mandatory = true, description = "This string specifies the RDF format to use")
	private RdfFormat rdfFormat;

	private Logger logger;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		logger = context.getLogger();
		super.initialize(context);

		documentMetadataHandler = (DocumentMetadataHandler) ConstructorUtil
				.invokeConstructor(documentMetadataHandlerClassName);

		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);

		// if (semanticStatementGeneratorClassName != null) {
		// semanticStatementGenerator = (SemanticStatementGenerator)
		// ConstructorUtil
		// .invokeConstructor(semanticStatementGeneratorClassName);
		// }

		documentRdfGenerator = (DocumentRdfGenerator) ConstructorUtil.invokeConstructor(documentRdfGeneratorClassName);

		annotationRdfGenerator = (AnnotationRdfGenerator) ConstructorUtil
				.invokeConstructor(annotationRdfGeneratorClassName);

		uriFactory = (UriFactory) ConstructorUtil.invokeConstructor(uriFactoryClassName);

	}

	private void writeStatements(Collection<? extends Statement> stmts, RDFWriter writer) {
		for (Statement s : stmts) {
			try {
				writer.handleStatement(s);
			} catch (RDFHandlerException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private File getOutputFile(JCas jCas, String documentId) throws AnalysisEngineProcessException {
		String outputFilename = String.format("%s-%s-annots.%s", documentId, outputFileInfix,
				rdfFormat.defaultFileExtension());
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
			File sourceDocumentFile = documentMetadataHandler.extractSourceDocumentPath(view);
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

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		String documentId = documentMetadataHandler.extractDocumentId(jcas);
		File outputFile = getOutputFile(jcas, documentId);

		try (BufferedWriter writer = (compressOutput)
				? new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile))))
				: FileWriterUtil.initBufferedWriter(outputFile)) {
			RDFWriter rdfWriter = rdfFormat.createWriter(writer);
			rdfWriter.startRDF();

			JCas view = View_Util.getView(jcas, outputViewName);
			/* Write the document RDF */
			Collection<Statement> documentStmts = documentRdfGenerator.generateRdf(jcas, documentMetadataHandler);
			writeStatements(documentStmts, rdfWriter);

			/* Write the annotation RDF */
			URI documentUri = documentRdfGenerator.getDocumentUri(view, documentMetadataHandler);
			for (Annotation annot : JCasUtil.select(view, Annotation.class)) {
				/*
				 * by checking for a null type here we are checking that the
				 * annotation is handled by the AnnotationDataExtractor
				 * implementation.
				 */
				String type = annotationDataExtractor.getAnnotationType(annot);
				if (type != null) {
					Collection<? extends Statement> stmts = annotationRdfGenerator.generateRdf(annotationDataExtractor,
							annot, uriFactory, documentUri, view.getDocumentText());
					writeStatements(stmts, rdfWriter);
				}
			}
			rdfWriter.endRDF();
		} catch (FileNotFoundException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (RDFHandlerException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

	public static AnalysisEngineDescription createDescription(TypeSystemDescription tsd, File outputDirectory,
			String outputFileInfix, RdfFormat format, boolean compressOutput,
			Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass,
			Class<? extends AnnotationRdfGenerator> annotationRdfGeneratorClass,
			Class<? extends DocumentRdfGenerator> documentRdfGeneratorClass,
			Class<? extends UriFactory> uriFactoryClass, String sourceViewName, String outputViewName)
			throws ResourceInitializationException {
		// @formatter:off
		return AnalysisEngineFactory.createEngineDescription(RdfSerialization_AE.class, tsd, 
				PARAM_ANNOTATION_RDF_GENERATOR_CLASS, annotationRdfGeneratorClass.getName(),
				PARAM_DOCUMENT_RDF_GENERATOR_CLASS, documentRdfGeneratorClass.getName(), 
				PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS, annotationDataExtractorClass.getName(),
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetaDataExtractorClass.getName(),
				PARAM_URI_FACTORY_CLASS, uriFactoryClass.getName(), 
				PARAM_RDF_FORMAT, format.name(),
				PARAM_COMPRESS_OUTPUT_FLAG, compressOutput,
				PARAM_OUTPUT_FILE_INFIX, outputFileInfix,
				PARAM_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath(),
				PARAM_SOURCE_VIEW_NAME, sourceViewName,
				PARAM_OUTPUT_VIEW_NAME, outputViewName
				);
		// @formatter:on
	}

	public static AnalysisEngineDescription createDescription_SaveToSourceFileDirectory(TypeSystemDescription tsd,
			String outputFileInfix, RdfFormat format, boolean compressOutput,
			Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass,
			Class<? extends AnnotationRdfGenerator> annotationRdfGeneratorClass,
			Class<? extends DocumentRdfGenerator> documentRdfGeneratorClass,
			Class<? extends UriFactory> uriFactoryClass, String sourceViewName, String outputViewName)
			throws ResourceInitializationException {
		// @formatter:off
		return AnalysisEngineFactory.createEngineDescription(RdfSerialization_AE.class, tsd, 
				PARAM_ANNOTATION_RDF_GENERATOR_CLASS, annotationRdfGeneratorClass.getName(),
				PARAM_DOCUMENT_RDF_GENERATOR_CLASS, documentRdfGeneratorClass.getName(), 
				PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS, annotationDataExtractorClass.getName(),
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetaDataExtractorClass.getName(),
				PARAM_URI_FACTORY_CLASS, uriFactoryClass.getName(), 
				PARAM_RDF_FORMAT, format.name(),
				PARAM_COMPRESS_OUTPUT_FLAG, compressOutput,
				PARAM_OUTPUT_FILE_INFIX, outputFileInfix,
				PARAM_SOURCE_VIEW_NAME, sourceViewName,
				PARAM_OUTPUT_VIEW_NAME, outputViewName
				);
		// @formatter:on
	}

}
