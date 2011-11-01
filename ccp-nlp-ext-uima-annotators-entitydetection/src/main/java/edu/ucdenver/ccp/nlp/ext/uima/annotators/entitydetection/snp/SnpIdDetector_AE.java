/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.snp;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.EntityTagger_AE;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class SnpIdDetector_AE extends EntityTagger_AE {

	/* (non-Javadoc)
	 * @see org.uimafit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		entityTagger = new SnpIdDetector();
	}
	
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(SnpIdDetector_AE.class, tsd);
	}


}
