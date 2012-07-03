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
import edu.ucdenver.ccp.medline.parser.MedlineXmlDeserializerTest;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MedlineXmlFileCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_MEDLINE_XML_FILE_NAME = "medsamp2011.xml";
	private File sampleMedlineXmlFile;

	@Before
	public void setUp() throws IOException {
		sampleMedlineXmlFile = ClassPathUtil.copyClasspathResourceToDirectory(MedlineXmlDeserializerTest.class,
				SAMPLE_MEDLINE_XML_FILE_NAME, folder.getRoot());
	}

	@Test
	public void testMedlineXmlCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1; // process all
		CollectionReader cr = MedlineXmlFileCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				sampleMedlineXmlFile, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		int count = 1;
		while (jCasIterable.hasNext() && count < 11) {
			String documentText = String.format("Title %d\nAbstract %d", count, count);
			if (count == 8)
				documentText = "Title 8\nAbstract 8a\nAbstract 8b\nAbstract 8c\nAbstract 8d\nAbstract 8e\nAbstract 8f\nAbstract 8g";
			JCas jCas = jCasIterable.next();
			assertEquals(documentText, jCas.getDocumentText());
			count++;
		}
		
		assertFalse(jCasIterable.hasNext());
	}
	
}
