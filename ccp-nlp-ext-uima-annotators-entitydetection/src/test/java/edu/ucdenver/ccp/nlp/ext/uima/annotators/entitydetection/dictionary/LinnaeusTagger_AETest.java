/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class LinnaeusTagger_AETest extends DefaultUIMATestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		String documentText = "The abc-1 and abc-2 genes interact with xyz56.";
		jcas.setDocumentText(documentText);
		UIMA_Util.setDocumentID(jcas, "docId00004");
	}

	private File initGeneVariantFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("12345\t|abc-1|abc1|a", "56789\txyz56", "7777\tabc-1");
		File geneVariantFile = folder.newFile("geneVariants.ascii");
		FileWriterUtil.printLines(lines, geneVariantFile, CharacterEncoding.US_ASCII);
		return geneVariantFile;
	}

	@Test
	public void testLinnaeusTagger_DefaultSettings() throws IOException, ResourceInitializationException,
			AnalysisEngineProcessException {
		File dictionaryFile = initGeneVariantFile();
		AnalysisEngineDescription aeDesc = LinnaeusTagger_AE.getAnalysisEngineDescription(getTypeSystem(),
				dictionaryFile, CollectionsUtil.createList("noDisambiguation"));
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);

		Iterator<CCPTextAnnotation> annotIter = UIMA_Util.getTextAnnotationIterator(jcas);
		annotIter.hasNext();

		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertTrue(ccpTa.getClassMention().getMentionName().equals("7777")
					|| ccpTa.getClassMention().getMentionName().equals("12345"));
			assertEquals("abc-1", ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");
		
		if (annotIter.hasNext()) {
			CCPTextAnnotation ccpTa = annotIter.next();
			assertEquals("56789", ccpTa.getClassMention().getMentionName());
			assertEquals("xyz56", ccpTa.getCoveredText());
		} else
			fail("There should be another sentence annotation");

		assertFalse(annotIter.hasNext());
	}

}
