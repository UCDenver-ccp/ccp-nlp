/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.snp;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class SnpIdDetector_AETest extends DefaultUIMATestCase {

	private static final TypeSystemDescription TSD = TypeSystemDescriptionFactory.createTypeSystemDescription(
			"edu.ucdenver.ccp.nlp.core.uima.TypeSystem");

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		jcas = JCasFactory.createJCas(TSD);
		jcas.setDocumentText(SnpIdDetectorTest.DOCUMENT_TEXT);
	}

	@Test
	public void testReferenceSnpIdDetection() throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngine snpDetector = AnalysisEngineFactory.createPrimitive(SnpIdDetector_AE
				.createAnalysisEngineDescription(TSD));
		snpDetector.process(jcas);
		int annotCount = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size();
		assertEquals(10, annotCount);
		
		printAnnotations();
	}
	
	/**
	 * 
	 */
	private void printAnnotations() {
		System.out.println("########################\n########################\n########################\n########################");
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (annotIter.hasNext())
			System.out.println(new WrappedCCPTextAnnotation((CCPTextAnnotation) annotIter.next()).toString());
	}

	@Test
	public void testReferenceSnpIdDetectionAAE() throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngine snpDetector = AnalysisEngineFactory.createAggregate(SnpIdDetector_AAE.getAggregateDescription(TSD));
		snpDetector.process(jcas);
		int annotCount = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size();
		printAnnotations();
		assertEquals("10 snp ids + 8 sentences",18, annotCount);
	}
	

}
