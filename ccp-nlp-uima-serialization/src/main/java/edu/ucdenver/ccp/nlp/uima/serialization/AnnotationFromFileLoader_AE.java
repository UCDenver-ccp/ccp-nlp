package edu.ucdenver.ccp.nlp.uima.serialization;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * This AE reads in annotations from a file. The file may have been created by the
 * AnnotationToFileOutputPrinter selection on the AnnotationPrinter AE. The input format for the
 * file is one annotation per line: <br>
 * <br>
 * documentID|annotatorID|spanStart spanEnd|classMentionName|coveredText
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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
			documentID2AnnotationsMap = TextAnnotationUtil.loadAnnotationsFromFile(new File(annotationsFile),
					CharacterEncoding.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.initialize(context);
	}

	/**
	 * For each CAS, lookup the document ID and insert any annotations associated with that document
	 * ID from the hash.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentID = UIMA_Util.getDocumentID(jcas);
		// Iterator it =
		// jcas.getJFSIndexRepository().getAnnotationIndex(CCPDocumentInformation.type).iterator();
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
