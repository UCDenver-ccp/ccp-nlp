package edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.datasource.rdfizer.rdf.ice.RdfUtil.RdfFormat;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.AnnotationRdfGenerator;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.DocumentRdfGenerator;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.RdfSerialization_AE;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.UriFactory;
import edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.uima.util.View;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

public class WebAnnotationRdfGeneratorTest extends DefaultUIMATestCase {

	private final String DOC_ID = "12345";
	private static final String DOCUMENT_TEXT = "This is some document text.";
	private File sourceFile;

	@Override
	protected void initJCas() throws UIMAException, IOException {
		UIMA_Util.setDocumentID(jcas, DOC_ID);
		jcas.setDocumentText(DOCUMENT_TEXT);

		/* add annotation to cas */
		CCPTextAnnotation annot = UIMA_Annotation_Util.createCCPTextAnnotation("http://example/token", 0, 4, jcas);
		Annotator annotator = new Annotator(-1, "annotator name", "", "");
		UIMA_Annotation_Util.setAnnotator(annot, annotator, jcas);
		annot.addToIndexes();

		sourceFile = new File(folder.newFolder("source"), "12345.txt");
		UIMA_Util.setSourceDocumentPath(jcas, sourceFile);
	}

	@Test
	public void testAnnotationRdfGen()
			throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		// edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot.WebAnnotationRdfGenerator.TextPositionWebAnnotationRdfGenerator
		File outputDirectory = folder.newFolder("output");
		String outputFileInfix = "all";
		RdfFormat format = RdfFormat.NTRIPLES;
		boolean compressOutput = false;

		Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass = CcpDocumentMetadataHandler.class;
		Class<? extends AnnotationDataExtractor> annotationDataExtractorClass = CcpAnnotationDataExtractor.class;
		Class<? extends AnnotationRdfGenerator> annotationRdfGeneratorClass = edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot.WebAnnotationRdfGenerator.TextPositionWebAnnotationRdfGenerator.class;
		Class<? extends DocumentRdfGenerator> documentRdfGeneratorClass = SampleDocumentRdfGenerator.class;
		Class<? extends UriFactory> uriFactoryClass = SampleUriFactory.class;
		String sourceViewName = View.DEFAULT.viewName();
		String outputViewName = View.DEFAULT.viewName();

		AnalysisEngineDescription desc = RdfSerialization_AE.createDescription(getTypeSystem(), outputDirectory,
				outputFileInfix, format, compressOutput, documentMetaDataExtractorClass, annotationDataExtractorClass,
				annotationRdfGeneratorClass, documentRdfGeneratorClass, uriFactoryClass, sourceViewName,
				outputViewName);

		AnalysisEngine engine = AnalysisEngineFactory.createEngine(desc);

		engine.process(jcas);

		File expectedOutputFile = new File(outputDirectory, DOC_ID + "-" + outputFileInfix + "-annots.nt");
		assertTrue(expectedOutputFile.exists());

		List<String> lines = FileReaderUtil.loadLinesFromFile(expectedOutputFile, CharacterEncoding.UTF_8);
		assertEquals(9, lines.size());

		lines.forEach(l -> System.out.println(l));

	}

	@Test
	public void testAnnotationRdfGen_saveToSourcePath()
			throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		// edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot.WebAnnotationRdfGenerator.TextPositionWebAnnotationRdfGenerator
		File outputDirectory = folder.newFolder("output");
		String outputFileInfix = "all";
		RdfFormat format = RdfFormat.NTRIPLES;
		boolean compressOutput = false;

		Class<? extends DocumentMetadataHandler> documentMetaDataExtractorClass = CcpDocumentMetadataHandler.class;
		Class<? extends AnnotationDataExtractor> annotationDataExtractorClass = CcpAnnotationDataExtractor.class;
		Class<? extends AnnotationRdfGenerator> annotationRdfGeneratorClass = edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot.WebAnnotationRdfGenerator.TextPositionWebAnnotationRdfGenerator.class;
		Class<? extends DocumentRdfGenerator> documentRdfGeneratorClass = SampleDocumentRdfGenerator.class;
		Class<? extends UriFactory> uriFactoryClass = SampleUriFactory.class;
		String sourceViewName = View.DEFAULT.viewName();
		String outputViewName = View.DEFAULT.viewName();

		AnalysisEngineDescription desc = RdfSerialization_AE.createDescription_SaveToSourceFileDirectory(
				getTypeSystem(), outputFileInfix, format, compressOutput, documentMetaDataExtractorClass,
				annotationDataExtractorClass, annotationRdfGeneratorClass, documentRdfGeneratorClass, uriFactoryClass,
				sourceViewName, outputViewName);

		AnalysisEngine engine = AnalysisEngineFactory.createEngine(desc);

		engine.process(jcas);

		File expectedOutputFile = new File(sourceFile.getParentFile(), DOC_ID + "-" + outputFileInfix + "-annots.nt");
		assertTrue(expectedOutputFile.exists());

		List<String> lines = FileReaderUtil.loadLinesFromFile(expectedOutputFile, CharacterEncoding.UTF_8);
		assertEquals(9, lines.size());

		lines.forEach(l -> System.out.println(l));

	}

	public static class SampleDocumentRdfGenerator implements DocumentRdfGenerator {

		@Override
		public URI getDocumentUri(JCas jCas, DocumentMetadataHandler documentMetadataHandler) {
			String documentId = documentMetadataHandler.extractDocumentId(jCas);
			return new URIImpl("http://example/document/" + documentId);
		}

		@Override
		public Collection<Statement> generateRdf(JCas jCas, DocumentMetadataHandler documentMetadataHandler) {
			return Collections.emptyList();
		}
	}

	public static class SampleUriFactory implements UriFactory {

		@Override
		public URI getResourceUri(AnnotationDataExtractor annotationDataExtractor, Annotation annotation) {
			return new URIImpl(annotationDataExtractor.getAnnotationType(annotation));
		}

	}

}
