/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.annotators.filter;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.reflection.ConstructorUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OboUtil;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;

/**
 * Given an input ontology (OBO file) and the identifier for a term in that ontology, all
 * annotations to the specified term or subclasses of that term are removed from the CAS.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class OntologyClassRemovalFilter_AE extends JCasAnnotator_ImplBase {

	/* ==== AnnotationDataExtractor configuration ==== */
	/**
	 * Parameter name used in the UIMA descriptor file for the annotation data extractor
	 * implementation to use
	 */
	public static final String PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS = ConfigurationParameterFactory
			.createConfigurationParameterName(OntologyClassRemovalFilter_AE.class, "annotationDataExtractorClassName");

	/**
	 * The name of the {@link AnnotationDataExtractor} implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "name of the AnnotationDataExtractor implementation to use", defaultValue = "edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.impl.CcpAnnotationDataExtractor")
	private String annotationDataExtractorClassName;

	/**
	 * this {@link AnnotationDataExtractor} will be initialized based on the class name specified by
	 * the annotationDataExtractorClassName parameter
	 */
	private AnnotationDataExtractor annotationDataExtractor;

	/* ==== OBO file configuration ==== */
	/**
	 * The OBO file containing the ontology
	 */
	public static final String PARAM_OBO_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			OntologyClassRemovalFilter_AE.class, "oboFile");

	@ConfigurationParameter(mandatory = true, description = "path to the OBO file containing the ontology")
	private File oboFile;

	/* ==== OBO file encoding configuration ==== */
	/**
	 * The encoding used by the input OBO file
	 */
	public static final String PARAM_OBO_FILE_ENCODING = ConfigurationParameterFactory
			.createConfigurationParameterName(OntologyClassRemovalFilter_AE.class, "oboEncoding");

	@ConfigurationParameter(mandatory = true, description = "encoding used by the OBO file containing the ontology", defaultValue = "UTF_8")
	private CharacterEncoding oboEncoding;

	/* ==== Ontology term id configuration ==== */
	/**
	 * The ontology ID to remove (including all of its subclasses)
	 */
	public static final String PARAM_ANNOTATION_TYPE_OF_INTEREST = ConfigurationParameterFactory
			.createConfigurationParameterName(OntologyClassRemovalFilter_AE.class, "termIdToRemove");

	@ConfigurationParameter(mandatory = true, description = "identifer for the term to remove from the CAS. All subclasses of this term will also be removed.")
	private String termIdToRemove;

	private OboUtil oboUtil;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			oboUtil = new OboUtil(oboFile, oboEncoding);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		annotationDataExtractor = (AnnotationDataExtractor) ConstructorUtil
				.invokeConstructor(annotationDataExtractorClassName);
	}

	/**
	 * Cycles through all annotations in the CAS, removing any that match the specified
	 * termIdToRemove or that are subclasses of that term identifier
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Collection<Annotation> annotationsToRemove = new ArrayList<Annotation>();
		for (Iterator<Annotation> annotIter = JCasUtil.iterator(jCas, Annotation.class); annotIter.hasNext();) {
			Annotation annotation = annotIter.next();
			String annotationType = annotationDataExtractor.getAnnotationType(annotation);
			if (annotationType != null) {
				if (annotationType.equals(termIdToRemove) || oboUtil.isDescendent(annotationType, termIdToRemove)
						|| oboUtil.isObsolete(annotationType)) {
					annotationsToRemove.add(annotation);
				} 
			}
		}

		for (Annotation annotation : annotationsToRemove) {
			annotation.removeFromIndexes();
		}
	}

	public static AnalysisEngineDescription getDescription(TypeSystemDescription tsd,
			Class<? extends AnnotationDataExtractor> annotationDataExtractorClass, String idToRemove, File oboFile,
			CharacterEncoding oboFileEncoding) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(OntologyClassRemovalFilter_AE.class, tsd,
				PARAM_ANNOTATION_DATA_EXTRACTOR_CLASS, annotationDataExtractorClass.getName(),
				PARAM_ANNOTATION_TYPE_OF_INTEREST, idToRemove, PARAM_OBO_FILE, oboFile.getAbsolutePath(),
				PARAM_OBO_FILE_ENCODING, oboFileEncoding.name());
	}

}
