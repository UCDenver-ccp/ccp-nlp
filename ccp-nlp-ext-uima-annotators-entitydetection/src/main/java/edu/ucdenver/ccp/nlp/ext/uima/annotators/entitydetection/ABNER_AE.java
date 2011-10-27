/*
 * ABNER_AE.java 
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
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.wrapper.abner.Abner_Util;

/**
 * The class incorporates the ABNER entity identification system into the UIMA analysis engine
 * framework. This class outputs 5 distinct annotation types. In the CCPTypeSytem, all five types
 * fall under the SemanticAnnotation classification.
 * <p>
 * ABNER was developed by Burr Settles and is available here:
 * http://pages.cs.wisc.edu/~bsettles/abner/
 * 
 * @author William A. Baumgartner, Jr.
 * 
 */
public class ABNER_AE extends EntityTagger_AE {
	static Logger logger = Logger.getLogger(ABNER_AE.class);
	// public final static String MODEL_FILE_KEY = "AbnerModelFile";

	/**
	 * ExternalResources such as MODEL_FILE_KEY can only be defined once and thus conflict if there
	 * are more than one ABNER instances in a pipeline set up with different models - this may only
	 * be a problem when initializing from AnalysisEngineDescriptors in an Aggregate.
	 */
	// @ExternalResource(key = MODEL_FILE_KEY)
	// DataResource modelFileDR;

	/**
	 * Parameter name used in the UIMA descriptor file for the token attribute extractor
	 * implementation to use
	 */
	public static final String PARAM_MODEL_FILE = ConfigurationParameterFactory.createConfigurationParameterName(
			ABNER_AE.class, "modelFile");

	/**
	 * The name of the TokenAttributeExtractor implementation to use
	 */
	@ConfigurationParameter(mandatory = true, description = "path to the model file to load")
	private File modelFile;

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		entityTagger = new Abner_Util();

		// /*
		// * Get the model file as defined by the Resource section of the descriptor for this
		// Analysis
		// * Engine
		// */
		// String abnerModelFile = modelFileDR.getUri().getPath();

		/* initialize the tagging system */
		try {
			String[] args = { modelFile.getAbsolutePath() };
			entityTagger.initialize(ITagger.ENTITY_TAGGER, args);
		} catch (InitializationException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription_BioCreative(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		InputStream modelStream = ABNER_AE.class.getResourceAsStream("/abner/models/biocreative.crf");
		try {
			File modelFile = File.createTempFile("biocreative", "crf");
			FileUtil.copy(modelStream, modelFile);
			return createAnalysisEngineDescription(tsd, modelFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription_NLPBA(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		InputStream modelStream = ABNER_AE.class.getResourceAsStream("/abner/models/nlpba.crf");
		try {
			File modelFile = File.createTempFile("nlpba", "crf");
			FileUtil.copy(modelStream, modelFile);
			return createAnalysisEngineDescription(tsd, modelFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngine createAnalysisEngine_BioCreative(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription_BioCreative(tsd));
	}

	public static AnalysisEngine createAnalysisEngine_NLPBA(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription_NLPBA(tsd));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, File modelFile)
			throws ResourceInitializationException {
		AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(ABNER_AE.class, tsd,
				PARAM_MODEL_FILE, modelFile.getAbsolutePath());
		// try {
		// ExternalResourceFactory.bindResource(aed, MODEL_FILE_KEY, path);
		// } catch (InvalidXMLException x) {
		// throw new ResourceInitializationException(x);
		// }
		return aed;
	}

}
