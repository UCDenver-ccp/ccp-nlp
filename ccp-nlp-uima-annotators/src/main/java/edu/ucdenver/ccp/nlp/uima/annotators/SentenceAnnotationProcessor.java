/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.uima.shims.ShimDefaults;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class SentenceAnnotationProcessor extends JCasAnnotator_ImplBase {

	/* ==== AnnotationDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceAnnotationProcessor.class, "annotationDataExtractorClassName");

	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = ShimDefaults.DEFAULT_ANNOTATION_DATA_EXTRACTOR_CLASS_NAME)
	private String annotationDataExtractorClassName;

	/**
	 * this {@link DocumentMetaDataExtractor} will be initialized based on the class name specified
	 * by the documentMetadataExtractorClassName parameter
	 */
	private AnnotationDataExtractor annotationDataExtractor;

	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_SENTENCE_ANNOTATION_NAME = ConfigurationParameterFactory
			.createConfigurationParameterName(SentenceAnnotationProcessor.class, "sentenceAnnotationName");

	@ConfigurationParameter(description = "name of the sentence annotation type to process", defaultValue = "sentence")
	private String sentenceAnnotationName;

	protected Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		UIMA_Util uimaUtil = new UIMA_Util();

		/*
		 * Check to see if there are any Sentence annotations in this CAS. If there are, then
		 * tokenize each sentence individually. If there are not, then treat the document text as a
		 * single sentence and tokenize it.
		 */
		List<TextAnnotation> annotationsToPutInCas = new ArrayList<TextAnnotation>();
		int sentenceCount = 0;
		for (FSIterator<Annotation> annotIter = jCas.getJFSIndexRepository().getAnnotationIndex().iterator(); annotIter
				.hasNext();) {
			Annotation annot = annotIter.next();
			String type = annotationDataExtractor.getAnnotationType(annot);
			if (type != null && type.toLowerCase().endsWith(sentenceAnnotationName)) {
				sentenceCount++;
				annotationsToPutInCas.addAll(processSentence(annot.getCoveredText().replaceAll("\\n", " "),
						annot.getBegin(), jCas));
			}
		}
		if (sentenceCount == 0) {
			logger.log(Level.INFO, "No sentences in CAS, processing document text as a whole...");
			annotationsToPutInCas.addAll(processSentence(jCas.getDocumentText(), 0, jCas));
		}
		/* add the TextAnnotations to the JCas */
		uimaUtil.putTextAnnotationsIntoJCas(jCas, annotationsToPutInCas);

	}

	protected abstract List<TextAnnotation> processSentence(String sentenceText, int sentenceStartOffset, JCas jCas);

}
