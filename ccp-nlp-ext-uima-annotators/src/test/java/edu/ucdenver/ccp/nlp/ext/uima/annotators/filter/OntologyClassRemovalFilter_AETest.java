/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.filter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class OntologyClassRemovalFilter_AETest extends DefaultUIMATestCase {

	private static final String ANIMAL_TERM_ID = "AN:0001";
	private static final String FOX_TERM_ID = "AN:0002";
	private static final String DOG_TERM_ID = "AN:0003";
	private static final String POODLE_TERM_ID = "AN:0004";
	private File oboFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		/* 012345678901234567890123456789012345678901234567890123456789 */
		jcas.setDocumentText("The quick brown fox jumped over the lazy dog and the poodle.");
		addTextAnnotationToJCas(16, 19, FOX_TERM_ID);
		addTextAnnotationToJCas(41, 44, DOG_TERM_ID);
		addTextAnnotationToJCas(53, 59, POODLE_TERM_ID);
		assertAnnotationCount(3);
		oboFile = folder.newFile("animal.obo");
		FileWriterUtil.printLines(getOboFileLines(), oboFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
	}

	@Test
	public void testRemoveOntologyClass_RemoveAllAnimalAnnotations() throws ResourceInitializationException,
			AnalysisEngineProcessException {
		String idToRemove = ANIMAL_TERM_ID;
		Set<String> expectedAnnotationClasses = CollectionsUtil.createSet();
		checkAnnotationRemoval(idToRemove, expectedAnnotationClasses);
	}

	@Test
	public void testRemoveOntologyClass_RemoveAllDogAnnotations() throws ResourceInitializationException,
			AnalysisEngineProcessException {
		String idToRemove = DOG_TERM_ID;
		Set<String> expectedAnnotationClasses = CollectionsUtil.createSet(FOX_TERM_ID);
		checkAnnotationRemoval(idToRemove, expectedAnnotationClasses);
	}
	
	@Test
	public void testRemoveOntologyClass_RemovePoodleAnnotationOnly() throws ResourceInitializationException,
			AnalysisEngineProcessException {
		String idToRemove = POODLE_TERM_ID;
		Set<String> expectedAnnotationClasses = CollectionsUtil.createSet(FOX_TERM_ID, DOG_TERM_ID);
		checkAnnotationRemoval(idToRemove, expectedAnnotationClasses);
	}

	/**
	 * @param idToRemove
	 * @param expectedAnnotationClasses
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 */
	private void checkAnnotationRemoval(String idToRemove, Set<String> expectedAnnotationClasses)
			throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngineDescription aeDesc = OntologyClassRemovalFilter_AE.getDescription(tsd,
				CcpAnnotationDataExtractor.class, idToRemove, oboFile, CharacterEncoding.UTF_8);
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aeDesc);
		ae.process(jcas);
		assertAnnotationCount(expectedAnnotationClasses.size());
		for (CCPTextAnnotation ccpTa : JCasUtil.select(jcas, CCPTextAnnotation.class)) {
			expectedAnnotationClasses.remove(ccpTa.getClassMention().getMentionName());
		}
		assertEquals(0, expectedAnnotationClasses.size());
	}

	private void assertAnnotationCount(int expectedCount) {
		assertEquals(expectedCount, JCasUtil.select(jcas, CCPTextAnnotation.class).size());
	}

	private static List<String> getOboFileLines() {
		/* @formatter:off */
		return CollectionsUtil.createList(
				"[Term]",
				"id: " + ANIMAL_TERM_ID,
				"name: animal",
				"namespace: animal_ontology",
				"def: \"The definition of an animal\" []",
				"",
				"[Term]",
				"id: " + FOX_TERM_ID,
				"name: fox",
				"namespace: animal_ontology",
				"def: \"The definition of a fox\" []",
				"is_a: "+ ANIMAL_TERM_ID +" ! animal",
				"",
				"[Term]",
				"id: " + DOG_TERM_ID,
				"name: dog",
				"namespace: animal_ontology",
				"def: \"The definition of a dog\" []",
				"is_a: "+ ANIMAL_TERM_ID +" ! animal",
				"",
				"[Term]",
				"id: " + POODLE_TERM_ID,
				"name: poodle",
				"namespace: animal_ontology",
				"def: \"The definition of a poodle\" []",
				"is_a: "+ DOG_TERM_ID +" ! animal");
		/* @formatter:on */
	}

}
