/*
 * AnnotationFromFileLoader_AE.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */

package edu.ucdenver.ccp.nlp.ext.uima.serialization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

/**
 * This AE reads in annotations from a file. The file may have been created by the AnnotationToFileOutputPrinter
 * selection on the AnnotationPrinter AE. The input format for the file is one annotation per line: <br>
 * <br>
 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText
 * 
 * @author William A. Baumgartner, Jr.
 * 
 */
public class AnnotationFromFileLoader_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ANNOTATION_FILE = "AnnotationFile";

	private Map<String, List<TextAnnotation>> documentID2AnnotationsMap;

	/**
	 * Initialize the AnnotationFromFileLoader_AE by loading the annotations from file into a hash.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {

		String annotationsFile;
		annotationsFile = (String) context.getConfigParameterValue(PARAM_ANNOTATION_FILE);

		/* load documentid to annotation map */
		try {
			System.err.println("Initializing AnnotationFromFileLoader: Loading annotations from: " + annotationsFile);
			documentID2AnnotationsMap = TextAnnotationUtil.loadAnnotationsFromFile(new File(annotationsFile), CharacterEncoding.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.initialize(context);
	}

	/**
	 * For each CAS, lookup the document ID and insert any annotations associated with that document ID from the hash.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentID = UIMA_Util.getDocumentID(jcas);
		// Iterator it = jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
		// if (it.hasNext()) { /* there will be at most one CCPDocumentInformation annotation */
		// CCPDocumentInformation docInfo = (CCPDocumentInformation) it.next();
		// documentID = docInfo.getDocumentID();
		// }

		if (documentID2AnnotationsMap.containsKey(documentID)) {
			List<TextAnnotation> taList = documentID2AnnotationsMap.get(documentID);
			UIMA_Util uimaUtil = new UIMA_Util();
			uimaUtil.putTextAnnotationsIntoJCas(jcas, taList);
		} else {
			// warn("DocumentID not detected in annotationfile: " + documentID);
		}
	}

}
