/* Copyright (C) 2006-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.extension.uima.serialization.knowtator;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

/**
 * This class provides a means for importing UIMA annotations into a Knowtator project.
 * 
 * Knowtator project must be manually initialized; must have text sources
 * 
 * @author William A. Baumgartner, Jr.
 * 
 */
public class KnowtatorProjectPipe extends JCasAnnotator_ImplBase {
	private static Logger logger = Logger.getLogger(KnowtatorProjectPipe.class);
	private final boolean DEBUG = false;

	/**
	 * Represented by "KnowtatorProject" in the descriptor file, this String specifies the path to
	 * the Knowtator project that will be used as a sink for the UIMA annotations.
	 */
	public static final String PARAM_KNOWTATORPROJECT = "KnowtatorProjectFile";

	private KnowtatorUtil knowtatorUtil;

	// private AnnotatorValidator annotatorValidator;
	//
	// private AnnotationSetValidator annotationSetValidator;

	private int count = 0;

	/**
	 * Initializes the CAS Consumer by reading in parameters from the descriptor file and
	 * initalizing a new KnowtatorUtil object.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		String knowtatorProjectFilename = (String) context.getConfigParameterValue(PARAM_KNOWTATORPROJECT);

		try {
			this.knowtatorUtil = new KnowtatorUtil(knowtatorProjectFilename);
		} catch (Exception e) {
			System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.err.println("Exception while initializing KnowtatorUtil in KnowtatorProjectPipe...");
			System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			e.printStackTrace();
			throw new ResourceInitializationException();
		}

	}

	/**
	 * For each annotation in the CAS, create a corresponding annotation in the assigned Knowtator
	 * project.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// JCas jcas;
		// try {
		// jcas = aCAS.getJCas();
		// } catch (CASException e) {
		// System.out.println("Exception getting CAS in KnowtatorProjectPipe");
		// e.printStackTrace();
		// throw new ResourceProcessException(e);
		// }

		// /* ********************* *
		// * Document level fields *
		// * ********************* */
		// String docID;
		// int docCollectionID;
		//
		//
		// Iterator docInfoIter =
		// jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		// //Iterator docSectionIter =
		// jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentSection.type).iterator();
		// Iterator syntacticAnnotationIter =
		// jcas.getJFSIndexRepository().getAnnotationIndex(SyntacticAnnotation.type).iterator();
		// Iterator semanticAnnotationIter =
		// jcas.getJFSIndexRepository().getAnnotationIndex(SemanticAnnotation.type).iterator();
		//
		// // get the document ID and document collection ID
		// if (docInfoIter.hasNext()) {
		// CCPDocumentInformation docInfo = (CCPDocumentInformation) docInfoIter.next();
		// docID = docInfo.getDocumentID();
		// docCollectionID = docInfo.getDocumentCollectionID();
		// } else {
		// System.err.println("CAS is missing CCPDocumentInformation. Document ID and DocumentCollection ID have been set to -1.");
		// docID = "-1";
		// docCollectionID = -1;
		// }

		// convert the UIMA annotations into the TextAnnotations utility class
		UIMA_Util uimaUtil = new UIMA_Util();
		List<TextAnnotation> textAnnotations = uimaUtil.getAnnotationsFromCas(jcas);

		if (DEBUG) {
			System.out
					.println("************************************************************************************************************");
			System.out
					.println("***********************  ADDING THE FOLLOWING TEXTANNOTATIONS TO A KNOWTATOR PROJECT ***********************");
			System.out
					.println("************************************************************************************************************");

			for (TextAnnotation ta : textAnnotations) {
				ta.printAnnotation(System.out);
			}

		}

		// logger.info(String.format("Inserting annotations into knowtator project. Document length: %d",
		// jcas.getDocumentText().length()));

		// insert TextAnnotations into the Knowtator project
		knowtatorUtil.addTextAnnotationsToKnowtatorProject(textAnnotations);
		System.err.println(count++);
	}

	/**
	 * When all CASes have been processed, and all annotations added to the Knowtator project, then
	 * save the Knowtator project.
	 */
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		System.out.println("CollectionProcessingComplete... saving Knowtator Project");
		knowtatorUtil.saveProject();
		knowtatorUtil.close();
		super.collectionProcessComplete();
	}

	public static AnalysisEngineDescription getDescription(File outputPprjFile) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(KnowtatorProjectPipe.class, PARAM_KNOWTATORPROJECT,
				outputPprjFile.getAbsolutePath());
	}

	// @Override
	// public void batchProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
	// IOException {
	// System.out.println("BatchProcessComplete... saving Knowtator Project");
	// knowtatorUtil.saveProject();
	// super.batchProcessComplete(arg0);
	// }

}
