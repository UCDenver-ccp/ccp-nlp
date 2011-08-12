package edu.ucdenver.ccp.nlp.extension.uima.serialization.knowtator;

import java.io.File;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.wrapper.knowtator.KnowtatorUtil;

public class KnowtatorProjectLoader_AE extends JCasAnnotator_ImplBase {

	private final boolean DEBUG = true;

	/**
	 * Name of configuration parameter that must be set to the path of the file containing the
	 * knowtator project to read from.
	 */
	public static final String PARAM_KNOWTATOR_PROJECT_FILE = "KnowtatorProjectFile";

	// The Validators (below) will be removed somewhere down the line. For now, they will be
	// referenced using the uima
	// resource manager
	// /**
	// * Name of the configuration parameter that must be set to the path of a file containing
	// information regarding the
	// AnnotatorValidator. This
	// * filename should reference one of two types of files, and which it references will depend on
	// the filename
	// itself. <br>
	// * <br>
	// * If the filename ends with ".connection.properties" then a DatabaseAnnotatorValidator will
	// be instantiated,
	// where the database referenced by the
	// * connection.properties file contains the known annotator information. Otherwise, a
	// FileAnnotatorValidator will
	// be instantiated. This file should
	// * contain a list of known annotators, one per line, taking the form: <br>
	// * <br>
	// * annotatorID|firstName|lastName|affiliation
	// */
	// private static final String PARAM_ANNOTATOR_VALIDATOR_FILE = "AnnotatorValidatorFile";

	// /**
	// * Name of the configuration parameter that must be set to the path of a file containing
	// information regarding the
	// AnnotationSetValidator. This
	// * filename should reference one of two types of files, and which it references will depend on
	// the filename
	// itself. <br>
	// * <br>
	// * If the filename ends with ".connection.properties" then a DatabaseAnnotationSetValidator
	// will be instantiated,
	// where the database referenced by
	// * the connection.properties file contains the known annotator information. Otherwise, a
	// FileAnnotationSetValidator will be instantiated. This
	// * file should contain a list of known annotators, one per line, taking the form: <br>
	// * <br>
	// * annotationSetID|annotationSetName|annotationSetDescription
	// */
	// private static final String PARAM_ANNOTATIONSET_VALIDATOR_FILE =
	// "AnnotationSetValidatorFile";

	private KnowtatorUtil knowtatorUtil;

	// private AnnotatorValidator annotatorValidator;
	//
	// private AnnotationSetValidator annotationSetValidator;

	public static AnalysisEngine createKnowtatorProjectLoaderAE(TypeSystemDescription tsd, File knowtatorProjectPprjFile)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(KnowtatorProjectLoader_AE.class, tsd,
				KnowtatorProjectLoader_AE.PARAM_KNOWTATOR_PROJECT_FILE,
				knowtatorProjectPprjFile.getAbsolutePath());
	}

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		try {
			// get the filename for the knowtator project file (.pprj) from the
			// descriptor file
			String knowtatorProjectFile = (String) context.getConfigParameterValue(PARAM_KNOWTATOR_PROJECT_FILE);

			// URI uri = context.getResourceURI("AnnotatorValidatorFile");
			// String annotatorValidatorFile = uri.getPath();
			//
			// uri = context.getResourceURI("AnnotationSetValidatorFile");
			// String annotationSetValidatorFile = uri.getPath();
			//
			// annotatorValidator = null;
			// if ((annotatorValidatorFile != null) && !annotatorValidatorFile.trim().equals("")) {
			// annotatorValidator =
			// Validator_Util.initializeAnnotatorValidator(annotatorValidatorFile);
			// }
			// if ((annotationSetValidatorFile != null) &&
			// !annotationSetValidatorFile.trim().equals("")) {
			// annotationSetValidator =
			// Validator_Util.initializeAnnotationSetValidator(annotationSetValidatorFile);
			// }

			knowtatorUtil = new KnowtatorUtil(knowtatorProjectFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.initialize(context);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentID = UIMA_Util.getDocumentID(jcas);

		/*
		 * annotations coming from a Knowtator project will have annotators and annotations sets
		 * that have names but will most likely have id's of "-1". The annotationSetValidator and
		 * annotatorValidator can be used here to apply the correct identifiers
		 */
		boolean ignoreSpanlessAnnotations = true;
		Collection<TextAnnotation> textAnnotations = knowtatorUtil.getTextAnnotationsFromKnowtatorDocument(documentID,
				ignoreSpanlessAnnotations);
		UIMA_Util uimaUtil = new UIMA_Util();
		uimaUtil.putTextAnnotationsIntoJCas(jcas, textAnnotations);
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String path)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(KnowtatorProjectLoader_AE.class, tsd,
				PARAM_KNOWTATOR_PROJECT_FILE, path);
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		knowtatorUtil.close();
		super.collectionProcessComplete();
	}

}