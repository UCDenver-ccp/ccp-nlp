/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.collections.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ClasspathCollectionReaderTest extends DefaultTestCase {

	private static final String SAMPLE_CLASSPATH_COLLECTION_PATH = "random/classpath/folder";

	private static final String DOC1_TEXT = "This is sample document 1.";
	private static final String DOC2_TEXT = "This is sample document 2.";
	private static final String DOC3_TEXT = "This is sample document 3.";

	@Test
	public void testClasspathCollectionReader() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = -1;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC3_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}

	@Test
	public void testClasspathCollectionReader_SkippingOne() throws UIMAException, IOException {
		int numToSkip = 1;
		int numToProcess = -1;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC3_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}
	
	
	@Test
	public void testClasspathCollectionReader_SkippingAll() throws UIMAException, IOException {
		int numToSkip = 4;
		int numToProcess = -1;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertFalse(jCasIterable.hasNext());
	}
	
	@Test
	public void testClasspathCollectionReader_ProcessTwo() throws UIMAException, IOException {
		int numToSkip = 0;
		int numToProcess = 2;
		CollectionReader cr = ClasspathCollectionReader.createCollectionReader(TypeSystemUtil.getCcpTypeSystem(),
				SAMPLE_CLASSPATH_COLLECTION_PATH, numToSkip, numToProcess, CcpDocumentMetadataHandler.class);

		JCasIterable jCasIterable = new JCasIterable(cr);

		assertTrue(jCasIterable.hasNext());
		JCas jCas = jCasIterable.next();
		assertEquals(DOC1_TEXT, jCas.getDocumentText());

		assertTrue(jCasIterable.hasNext());
		jCas = jCasIterable.next();
		assertEquals(DOC2_TEXT, jCas.getDocumentText());

		assertFalse(jCasIterable.hasNext());
	}
	
}
