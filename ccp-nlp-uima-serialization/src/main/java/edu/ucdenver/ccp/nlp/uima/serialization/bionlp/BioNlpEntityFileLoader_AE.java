/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.bionlp;

import java.io.File;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.bionlp.BioNlpThemeIterator;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpEntityFileLoader_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ENTITY_FILES_DIRECTORY = ConfigurationParameterFactory
			.createConfigurationParameterName(BioNlpEntityFileLoader_AE.class, "entityFilesDirectory");

	@ConfigurationParameter(mandatory = true, description = "The directory where the entity files are to be found")
	private File entityFilesDirectory;

	public static final String PARAM_ENTITY_FILES_ENCODING = ConfigurationParameterFactory
			.createConfigurationParameterName(BioNlpEntityFileLoader_AE.class, "entityFilesEncoding");

	@ConfigurationParameter(mandatory = true, defaultValue = "UTF_8", description = "The encoding to use when reading the entity files")
	private CharacterEncoding entityFilesEncoding;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentId = UIMA_Util.getDocumentID(jcas);
		String documentIdPrefix = documentId.substring(0, documentId.lastIndexOf("."));
		File entityFile = new File(entityFilesDirectory, documentIdPrefix + ".a1");
		UIMA_Util uimaUtil = new UIMA_Util();

		System.out.println("# annotations before: "
				+ jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());

		BioNlpThemeIterator entityIterator = new BioNlpThemeIterator(entityFile, entityFilesEncoding);
		List<TextAnnotation> taList = CollectionsUtil.createList(entityIterator);
		System.out.println("# entity annotations: " + taList.size());
		// for (TextAnnotation ta : taList)
		// System.out.println(ta.toString());

		uimaUtil.putTextAnnotationsIntoJCas(jcas, taList);
		System.out.println("# annotations after: "
				+ jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());
	}
}
