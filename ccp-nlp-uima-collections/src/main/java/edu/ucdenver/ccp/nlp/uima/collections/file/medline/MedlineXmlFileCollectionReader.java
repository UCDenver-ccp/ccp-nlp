package edu.ucdenver.ccp.nlp.uima.collections.file.medline;

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
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.medline.MedlineCitation;
import org.medline.MedlineDate;
import org.medline.Month;
import org.medline.PubDate;
import org.medline.PubmedArticle;
import org.medline.Season;
import org.medline.Year;

import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.medline.core.PubMedDateUtil;
import edu.ucdenver.ccp.medline.xml.MedlineCitationUtil;
import edu.ucdenver.ccp.medline.xml.MedlineXmlParser;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.uima.collections.BaseTextCollectionReader;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.View;

/**
 * This collection reader takes as input a file using the Medline XML format (as
 * is downloaded as part of the Medline lease for instance) and returns
 * documents containing the title and abstract for each Medline record in the
 * input file. Title and abstract are separated by a line break.
 * 
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class MedlineXmlFileCollectionReader extends BaseTextCollectionReader {

	private static final Logger logger = Logger.getLogger(MedlineXmlFileCollectionReader.class);
	
	/* ==== Input file configuration ==== */
	public static final String PARAM_MEDLINE_XML_FILE = "medlineXmlFile";

	@ConfigurationParameter(mandatory = true, description = "The file containing the Medline XML comprising this document collection")
	protected File medlineXmlFile;

	private MedlineXmlParser medlineXmlParser;

	private GenericDocument nextDocument = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * initializeImplementation (org.apache.uima.UimaContext)
	 */
	@Override
	protected void initializeImplementation(UimaContext context) throws ResourceInitializationException {
		try {
			medlineXmlParser = new MedlineXmlParser(medlineXmlFile);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * countDocumentsInCollection()
	 */
	@Override
	protected int countDocumentsInCollection() throws IOException {
		MedlineXmlParser deserializer;
		try {
			deserializer = new MedlineXmlParser(medlineXmlFile);
			int documentCount = 0;
			while (deserializer.hasNext()) {
				documentCount++;
				deserializer.next();
			}
			return documentCount;
		} catch (JAXBException | XMLStreamException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * skip()
	 */
	@Override
	protected void skip(int numberToSkip) throws ResourceInitializationException {
		int numSkipped = 0;
		while (numSkipped < numberToSkip && medlineXmlParser.hasNext()) {
			numSkipped++;
			medlineXmlParser.next();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * hasNextDocument()
	 */
	@Override
	protected boolean hasNextDocument() throws IOException, CollectionException {
		if (nextDocument == null) {
			while (medlineXmlParser.hasNext() && nextDocument == null) {
				PubmedArticle pubmedArticle = medlineXmlParser.next();
				MedlineCitation nextCitation = pubmedArticle.getMedlineCitation();
				StringBuffer documentText = new StringBuffer();
				documentText.append(nextCitation.getArticle().getArticleTitle().getvalue());
				String abstractText = MedlineCitationUtil.getAbstractText(nextCitation);
				if (abstractText != null) {
					documentText.append(StringConstants.NEW_LINE + abstractText);
				}
				nextDocument = new GenericDocument(nextCitation.getPMID().getvalue());
				nextDocument.setDocumentText(documentText.toString());
				int[] monthYear = getPubDate(nextCitation);
				nextDocument.setPublicationMonth(monthYear[0]);
				nextDocument.setPublicationYear(monthYear[1]);
			}
			if (nextDocument != null) {
				return true;
			}
			return false;
		}
		return true;
	}

	private int[] getPubDate(MedlineCitation nextCitation) {
		PubDate pubDate = nextCitation.getArticle().getJournal().getJournalIssue().getPubDate();
		int month = -1;
		int year = -1;
		for (Object obj : pubDate.getYearOrMonthOrDayOrSeasonOrMedlineDate()) {
			if (Year.class.isInstance(obj)) {
				year = Integer.parseInt(((Year) obj).getvalue());
			}
			if (Month.class.isInstance(obj)) {
				month = PubMedDateUtil.getMonth(((Month) obj).getvalue());
			}
			
			if (Season.class.isInstance(obj)) {
				month = PubMedDateUtil.getMonthForSeason(((Season) obj).getvalue());
			}
			if (MedlineDate.class.isInstance(obj)) {
				int mthYr[] = PubMedDateUtil.parseMedlineDate(((MedlineDate) obj).getvalue());
				month = mthYr[0];
				year = mthYr[1];
			}
		}

		if (month < 0) {
			month = 1;
		}
		if (year < 0) {
			logger.warn("Missing publication year for document: " + nextCitation.getPMID().getvalue());
			year = 0;
		}
		return new int[] { month, year };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.uima.collections.file.BaseTextCollectionReader#
	 * getNextDocument()
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
	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, File medlineXmlFile, int numToSkip,
			int numToProcess, Class<CcpDocumentMetadataHandler> documentMetadataHandlerClass)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createReader(MedlineXmlFileCollectionReader.class, tsd, PARAM_MEDLINE_XML_FILE,
				medlineXmlFile.getAbsolutePath(), PARAM_DISABLE_PROGRESS, true, PARAM_DOCUMENT_METADATA_HANDLER_CLASS,
				documentMetadataHandlerClass.getName(), PARAM_ENCODING, "UTF_8", PARAM_NUM2PROCESS, numToProcess,
				PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME, View.DEFAULT.name());
	}

	public static CollectionReaderDescription createCollectionReaderDescription(TypeSystemDescription tsd,
			File medlineXmlFile, int numToSkip, int numToProcess,
			Class<CcpDocumentMetadataHandler> documentMetadataHandlerClass) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(MedlineXmlFileCollectionReader.class, tsd,
				PARAM_MEDLINE_XML_FILE, medlineXmlFile.getAbsolutePath(), PARAM_DISABLE_PROGRESS, true,
				PARAM_DOCUMENT_METADATA_HANDLER_CLASS, documentMetadataHandlerClass.getName(), PARAM_ENCODING, "UTF_8",
				PARAM_NUM2PROCESS, numToProcess, PARAM_NUM2SKIP, numToSkip, PARAM_VIEWNAME, View.DEFAULT.name());
	}

}
