/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.filter;

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
