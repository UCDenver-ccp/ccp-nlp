package edu.ucdenver.ccp.nlp.uima.collections.termvariant;

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
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This collection reader serves documents as output by the {@link TermVariantFileParser}. See
 * {@link TermVariantFileParser} for details regarding the input format.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class TermVariantCollectionReader extends BaseTextCollectionReader {

	public static final String PARAM_TERM_VARIANT_FILE = "termVariantFile";
	@ConfigurationParameter(mandatory = true, description = "file path to the file containing the term/variant info. For format, see TermVariantFileParser documentation")
	protected File termVariantFile;

	private Iterator<GenericDocument> documentIterator;

	@Override
	protected void initializeImplementation(UimaContext context) throws ResourceInitializationException {
		boolean isInputFileValid = TermVariantFileParser.hasValidFormat(termVariantFile);
		if (!isInputFileValid) {
			throw new ResourceInitializationException(new IllegalStateException(
					"The input term-variant file is not formatted properly. Please fix and re-try."));
		}

		try {
			documentIterator = TermVariantFileParser.getDocumentIteratorFromFile(termVariantFile);
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

	}

	/**
	 * Burn through the file and count the number of documents that will be returned
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		Iterator<GenericDocument> docIter = TermVariantFileParser.getDocumentIteratorFromFile(termVariantFile);
		int count = 0;
		skip(docIter, numberToSkip);
		while (docIter.hasNext()) {
			docIter.next();
			count++;
		}
		return count;
	}

	@Override
	protected void skip(int numberToSkip) throws ResourceInitializationException {
		skip(documentIterator, numberToSkip);
	}

	/**
	 * Advances the input Iterator<GenericDocument> forward numberToSkip times.
	 * 
	 * @param docIter
	 */
	private void skip(Iterator<GenericDocument> docIter, int numberToSkip) {
		int numSkipped = 0;
		while (docIter.hasNext() && numSkipped < numberToSkip) {
			docIter.next();
			numSkipped++;
		}
	}

	@Override
	protected boolean hasNextDocument() throws IOException, CollectionException {
		return documentIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader#getNextDocument()
	 */
	@Override
	protected GenericDocument getNextDocument() throws CollectionException, IOException {
		return documentIterator.next();
	}

	/**
	 * TODO: See if this part of the code can be made type-system-independent by way of the
	 * uima-shims project
	 */
	@Override
	protected void loadAnnotationsIntoCas(JCas jcas, GenericDocument document) {
		UIMA_Util uu = new UIMA_Util();
		uu.putTextAnnotationsIntoJCas(jcas, document.getAnnotations());
	}

	/**
	 * This factory method provides complete flexibility when setting up a
	 * TermVariantCollectionReader
	 * 
	 * @param tsd
	 * @param termVariantFile
	 * @param encoding
	 * @param language
	 * @param disableProgress
	 * @param num2process
	 * @param num2skip
	 * @param documentMetadataHandlerClass
	 * @param viewName
	 * @return a {@link CollectionReaderDescription} set to the input parameters of this method
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(TypeSystemDescription tsd, File termVariantFile,
			CharacterEncoding encoding, String language, boolean disableProgress, int num2process, int num2skip,
			Class<? extends DocumentMetadataHandler> documentMetadataHandlerClass, String viewName)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(TermVariantCollectionReader.class, tsd,
				PARAM_TERM_VARIANT_FILE, termVariantFile.getAbsolutePath(), PARAM_ENCODING, encoding.name(),
				PARAM_DISABLE_PROGRESS, disableProgress, PARAM_LANGUAGE, language, PARAM_NUM2PROCESS, num2process,
				PARAM_NUM2SKIP, num2skip, PARAM_VIEWNAME, viewName, PARAM_DOCUMENT_METADATA_HANDLER_CLASS,
				documentMetadataHandlerClass.getName());
	}

	/**
	 * This factory method provides default settings for the TermVariantCollectionReader:<br>
	 * * writes to the default CAS view<br>
	 * * processes all documents from start to finish (skip = 0, numToProcess = -1)<br>
	 * * uses the CCP type system document metadata handler<br>
	 * * disables progress monitoring to keep from burning through the input file to count the
	 * number of documents<br>
	 * 
	 * @param tsd
	 * @param termVariantFile
	 * @param encoding
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDefaultDescription(TypeSystemDescription tsd, File termVariantFile,
			CharacterEncoding encoding) throws ResourceInitializationException {
		return CollectionReaderFactory.createDescription(TermVariantCollectionReader.class, tsd,
				PARAM_TERM_VARIANT_FILE, termVariantFile.getAbsolutePath(), PARAM_ENCODING, encoding.name(),
				PARAM_DISABLE_PROGRESS, true);
	}

}
