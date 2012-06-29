/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.file.medline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.medline.parser.PubmedXmlDeserializerTest;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PubmedXmlFileCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_PUBMED_XML_FILE_NAME = "pmsamp2011.xml";
	private File samplePubmedXmlFile;

	@Before
	public void setUp() throws IOException {
		samplePubmedXmlFile = ClassPathUtil.copyClasspathResourceToDirectory(PubmedXmlDeserializerTest.class,
				SAMPLE_PUBMED_XML_FILE_NAME, folder.getRoot());
	}

	@Test
	public void testPubmedXmlCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReader cr = PubmedXmlFileCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				samplePubmedXmlFile, numToSkip, numToProcess, CcpDocumentMetaDataExtractor.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		int count = 1;
		while (jCasIterable.hasNext()) {
			String documentText = String.format("Title %d\nAbstract %d", count, count);
			if (count == 8)
				documentText = "Title 8\nAbstract 8a\nAbstract 8b\nAbstract 8c\nAbstract 8d\nAbstract 8e\nAbstract 8f\nAbstract 8g";
			if (count == 11)
				documentText = "Book Title 1\nBook Abstract 1a\nBook Abstract 1b\nBook Abstract 1c\nBook Abstract 1d";
			JCas jCas = jCasIterable.next();
			assertEquals(documentText, jCas.getDocumentText());
			count++;
		}
		
		assertFalse(jCasIterable.hasNext());
	}
	
}
