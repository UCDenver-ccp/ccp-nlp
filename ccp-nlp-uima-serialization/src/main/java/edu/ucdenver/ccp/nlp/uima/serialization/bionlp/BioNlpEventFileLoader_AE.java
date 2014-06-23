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
import edu.ucdenver.ccp.nlp.ext.bionlp.BioNlpEventIterator;
import edu.ucdenver.ccp.nlp.ext.bionlp.BioNlpThemeIterator;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpEventFileLoader_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_ENTITY_FILES_DIRECTORY = ConfigurationParameterFactory
			.createConfigurationParameterName(BioNlpEventFileLoader_AE.class, "entityFilesDirectory");

	@ConfigurationParameter(mandatory = true, description = "The directory where the entity files are to be found")
	private File entityFilesDirectory;

	public static final String PARAM_EVENT_FILES_DIRECTORY = ConfigurationParameterFactory
			.createConfigurationParameterName(BioNlpEventFileLoader_AE.class, "eventFilesDirectory");

	@ConfigurationParameter(mandatory = true, description = "The directory where the event files are to be found")
	private File eventFilesDirectory;

	public static final String PARAM_FILE_ENCODING = ConfigurationParameterFactory.createConfigurationParameterName(
			BioNlpEventFileLoader_AE.class, "characterEncoding");

	@ConfigurationParameter(mandatory = true, defaultValue = "UTF_8", description = "The encoding to use when reading the entity and event files")
	private CharacterEncoding characterEncoding;

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
		File eventFile = new File(eventFilesDirectory, documentIdPrefix + ".a2");

		UIMA_Util uimaUtil = new UIMA_Util();

		System.out.println("# annotations before: "
				+ jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());

		BioNlpThemeIterator entityIterator = new BioNlpThemeIterator(entityFile, characterEncoding);
		List<TextAnnotation> taList = CollectionsUtil.createList(entityIterator);
		BioNlpEventIterator eventIterator = new BioNlpEventIterator(eventFile, entityFile, characterEncoding);
		taList.addAll(CollectionsUtil.createList(eventIterator));

		System.out.println("# entity + event annotations: " + taList.size());
		// for (TextAnnotation ta : taList)
		// System.out.println(ta.toString());

		uimaUtil.putTextAnnotationsIntoJCas(jcas, taList);
		System.out.println("# entity + event annotations after: "
				+ jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());
	}
}
