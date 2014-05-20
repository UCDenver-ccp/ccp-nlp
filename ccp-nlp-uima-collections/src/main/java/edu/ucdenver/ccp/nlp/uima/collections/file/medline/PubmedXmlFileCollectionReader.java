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
package edu.ucdenver.ccp.nlp.uima.collections.file.medline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.medline.parser.MedlineCitation.AbstractText;
import edu.ucdenver.ccp.medline.parser.PubmedArticleBase;
import edu.ucdenver.ccp.medline.parser.PubmedXmlDeserializer;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.View;

/**
 * This collection reader takes as input a file using the Medline XML format (as is returned from a
 * PubMed query for instance) and returns documents containing the title and abstract for each
 * Medline record in the input file. Title and abstract are separated by a line break.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PubmedXmlFileCollectionReader extends BaseTextCollectionReader {

	private static final Logger logger = Logger.getLogger(PubmedXmlFileCollectionReader.class);

	/* ==== Input file configuration ==== */
	public static final String PARAM_MEDLINE_XML_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			PubmedXmlFileCollectionReader.class, "pubmedXmlFile");

	@ConfigurationParameter(mandatory = true, description = "The file containing the Pubmed XML comprising this document collection")
	protected File pubmedXmlFile;

	private PubmedXmlDeserializer pubmedXmlDeserializer;

	private GenericDocument nextDocument = null;

	/**
	 * This method overriden so that the year can be appended to the document id
	 */
	@Override
	protected void initializeJCas(JCas jcas, GenericDocument document) throws AnalysisEngineProcessException {
		if (this.viewName.equals(View.DEFAULT.name())) {
			jcas.setSofaDataString(document.getDocumentText(), "text/plain");
			if (this.language != null) {
				jcas.setDocumentLanguage(this.language);
			}
			loadAnnotationsIntoCas(jcas, document);
		} else {
			JCas view = ViewCreatorAnnotator.createViewSafely(jcas, this.viewName);
			view.setSofaDataString(document.getDocumentText(), "text/plain");
			if (this.language != null) {
				view.setDocumentLanguage(this.language);
			}
			loadAnnotationsIntoCas(view, document);
		}
		getDocumentMetadataHandler().setDocumentId(jcas,
				document.getDocumentID() + "||" + document.getOtherDocumentIDs().get("year"));
		getDocumentMetadataHandler().setDocumentEncoding(jcas, encoding.getCharacterSetName());

		if (processedDocumentCount % 100 == 0) {
			logger.info("Processing document " + processedDocumentCount + ".  Loading view: " + this.viewName);
		}

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
			InputStream is = null;
			if (pubmedXmlFile.getName().endsWith(".gz")) {
				is = new GZIPInputStream(new FileInputStream(pubmedXmlFile));
			} else {
				is = new FileInputStream(pubmedXmlFile);
			}
			pubmedXmlDeserializer = new PubmedXmlDeserializer(is);
		} catch (IOException e) {
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
		PubmedXmlDeserializer deserializer = new PubmedXmlDeserializer(new FileInputStream(pubmedXmlFile));
		int documentCount = 0;
		while (deserializer.hasNext()) {
			documentCount++;
			deserializer.next();
		}
		return documentCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#skip()
	 */
	@Override
	protected void skip(int numberToSkip) throws ResourceInitializationException {
		int numSkipped = 0;
		while (numSkipped < numberToSkip && pubmedXmlDeserializer.hasNext()) {
			numSkipped++;
			pubmedXmlDeserializer.next();
		}
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
			if (pubmedXmlDeserializer.hasNext()) {
				PubmedArticleBase nextArticle = pubmedXmlDeserializer.next();
				StringBuffer documentText = new StringBuffer();
				documentText.append(nextArticle.getArticleTitle());
				String abstractText = nextArticle.getArticleAbstractText();
				if (abstractText != null) {
					documentText.append(StringConstants.NEW_LINE + StringConstants.NEW_LINE + abstractText);
				}
				nextDocument = new GenericDocument(nextArticle.getPubmedId().getPmid());
				nextDocument.addOtherDocumentID("year", nextArticle.getDate());
				nextDocument.setDocumentText(documentText.toString());
				return true;
			}
			return false;
		}
		return true;
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
	 * @param medlineXmlFile2
	 * @param numToSkip
	 * @param numToProcess
	 * @param class1
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File medlineXmlFile,
			int numToSkip, int numToProcess, Class<CcpDocumentMetadataHandler> documentMetadataHandlerClass)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(PubmedXmlFileCollectionReader.class, tsd,
				PARAM_MEDLINE_XML_FILE, medlineXmlFile.getAbsolutePath(), PARAM_DISABLE_PROGRESS, true,
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass.getName(), PARAM_ENCODING, "UTF_8",
				PARAM_NUM2PROCESS, numToProcess, PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME, View.DEFAULT.name());
	}

}
