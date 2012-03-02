/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;

/**
 * Useful when each line of the input text represents a specific class, e.g. a sentence or a
 * paragraph. This AE allows each line to be annotated to the specified class.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class PerLineExtractorAE extends JCasAnnotator_ImplBase {

	public final static String PARAM_LINE_TYPE = ConfigurationParameterFactory.createConfigurationParameterName(
			PerLineExtractorAE.class, "lineType");
	@ConfigurationParameter(mandatory = true, description = "The type assigned to each annotation of the lines of the input text")
	private String lineType;
	private Logger logger;

	
	
	/* (non-Javadoc)
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger = context.getLogger();
		logger.log(Level.INFO,"Initialized PerLineExtractor for type: " + lineType);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		int index = 0;
		logger.log(Level.INFO,"SPLITTING INTO N PIECES: " + jCas.getDocumentText().split("\\n").length);
		for (String line : jCas.getDocumentText().split("\\n")) {
			logger.log(Level.INFO,"CREATING " + lineType + " [" + index + ".." + (index + line.length()) + "]");
			
			CCPTextAnnotation sentence = UIMA_Annotation_Util.createCCPTextAnnotation(lineType, index, index + line.length(), jCas);
			logger.log(Level.INFO, "SENTENCE = " + sentence.getCoveredText());
			index += (line.length() + 1);
		}
	}
	
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, String lineType)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(PerLineExtractorAE.class, tsd, PARAM_LINE_TYPE, lineType);
	}

}
