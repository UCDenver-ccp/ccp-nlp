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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class MedlineXmlFileCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_MEDLINE_XML_FILE_NAME = "pubmed_sample_2018.xml.gz";
	private File sampleMedlineXmlFile;

	@Before
	public void setUp() throws IOException {
		sampleMedlineXmlFile = ClassPathUtil.copyClasspathResourceToDirectory(this.getClass(),
				SAMPLE_MEDLINE_XML_FILE_NAME, folder.getRoot());
	}

	private static final String PMID_1 = "973217";
	private static final String TITLE_1 = "Hospital debt management and cost reimbursement.";
	private static final String ABSTRACT_1 = null;
	private static final int PUB_MONTH_1 = 10;
	private static final int PUB_YEAR_1 = 1976;

	private static final String PMID_2 = "1669026";
	private static final String TITLE_2 = "[150th Anniversary Celebration of the Royal Academy of Medicine of Belgium. Part 1. Bruxelles, 26-28 September 1991].";
	private static final String ABSTRACT_2 = null;
	private static final int PUB_MONTH_2 = 1;
	private static final int PUB_YEAR_2 = 1991;

	private static final String PMID_3 = "1875346";
	private static final String TITLE_3 = "3-Hydroxy-3-methylglutaryl-coenzyme a reductase inhibitors. 7. Modification of the hexahydronaphthalene moiety of simvastatin: 5-oxygenated and 5-oxa derivatives.";
	private static final String ABSTRACT_3 = "Modification of the hexahydronaphthalene ring 5-position in simvastatin 2a via oxygenation and oxa replacement afforded two series of derivatives which were evaluated in vitro for inhibition of 3-hydroxy-3-methylglutaryl-coenzyme A reductase and acutely in vivo for oral effectiveness as inhibitors of cholesterogenesis in the rat. Of the compounds selected for further biological evaluation, the 6 beta-methyl-5-oxa 10 and 5 alpha-hydroxy 16 derivatives of 3,4,4a,5-tetrahydro 2a, as well as, the 6 beta-epimer 14 of 16 proved orally active as hypocholesterolemic agents in cholestyramine-primed dogs. Subsequent acute oral metabolism studies in dogs demonstrated that compounds 14 and 16 evoke lower peak plasma drug activity and area-under-the-curve values than does compound 10 and led to the selection of 14 and 16 for toxicological evaluation.";
	private static final int PUB_MONTH_3 = 8;
	private static final int PUB_YEAR_3 = 1991;

	@Test
	public void testMedlineXmlCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReaderDescription cr = MedlineXmlFileCollectionReader.createCollectionReaderDescription(
				TypeSystemUtil.getCcpTypeSystem(), sampleMedlineXmlFile, numToSkip, numToProcess,
				CcpDocumentMetadataHandler.class);

		JCasIterator jCasIterable = new JCasIterable(cr).iterator();
		DocumentMetadataHandler documentMetadataHandler = new CcpDocumentMetadataHandler();

		if (jCasIterable.hasNext()) {
			JCas jCas = jCasIterable.next();
			String documentText = TITLE_1;
			assertEquals(documentText, jCas.getDocumentText());
			assertEquals(PMID_1, documentMetadataHandler.extractDocumentId(jCas));
			assertEquals(PUB_YEAR_1, documentMetadataHandler.getYearPublished(jCas));
			assertEquals(PUB_MONTH_1, documentMetadataHandler.getMonthPublished(jCas));
		}
		if (jCasIterable.hasNext()) {
			JCas jCas = jCasIterable.next();
			String documentText = TITLE_2;
			assertEquals(documentText, jCas.getDocumentText());
			assertEquals(PMID_2, documentMetadataHandler.extractDocumentId(jCas));
			assertEquals(PUB_YEAR_2, documentMetadataHandler.getYearPublished(jCas));
			assertEquals(PUB_MONTH_2, documentMetadataHandler.getMonthPublished(jCas));
		}
		if (jCasIterable.hasNext()) {
			JCas jCas = jCasIterable.next();
			String documentText = TITLE_3 + "\n" + ABSTRACT_3;
			assertEquals(documentText, jCas.getDocumentText());
			assertEquals(PMID_3, documentMetadataHandler.extractDocumentId(jCas));
			assertEquals(PUB_YEAR_3, documentMetadataHandler.getYearPublished(jCas));
			assertEquals(PUB_MONTH_3, documentMetadataHandler.getMonthPublished(jCas));
		}

		assertFalse(jCasIterable.hasNext());

	}

}
