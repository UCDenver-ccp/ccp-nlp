/*
 * LingPipeTokenizer_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.postaggers;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ExternalResourceFactory;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.wrapper.lingpipe.LingPipe_Util;

/**
 * 
 * This class uses the LingPipe POS tagger to create CCPTokenAnnotations. <br>
 * If SentenceAnnotations are present, they are tagged sequentially, otherwise the entire document text is assumed to be a single sentence.
 * 
 * @author Bill Baumgartner
 * 
 */

public class LingPipePOSTagger_AE extends POSTagger_AE {
	
	static final String FILE_KEY = "LingPipePOSModelFile";
	@ExternalResource(key=FILE_KEY)
	DataResource modelFileDR;

	static final String TAG_SET = "POSTagSet";
	@ExternalResource(key=TAG_SET)
	DataResource tagSetDR;
	
	static Logger logger = Logger.getLogger(LingPipePOSTagger_AE.class);
	
	/**
	 * The LingPipe POS tagger requires a model file to run. The model is defined in the descriptor for this Analysis Engine. It is loaded here, and
	 * the POS tagger is initialized prior to processing any of the document text.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		posTagger = new LingPipe_Util();

		/*
		 * Get the model file and tagset as defined by the Resource section of the descriptor for this Analysis Engine. We are using the Resources
		 * section to define the tagset. The URL for the tagset is set to file. This is a workaround so we can avoid placing an input parameter in
		 * this descriptor.
		 */

			URI u =  tagSetDR.getUri();
			String tagset =  u.getPath();
			String lingPipeModelFile = modelFileDR.getUri().getPath();
 
			/* initialize the LingPipe POS tagging system */
			try {
				String[] args = { lingPipeModelFile, tagset };
				posTagger.initialize(ITagger.POS_TAGGER, args);
			} catch (Exception e) {
				e.printStackTrace();
			}

		super.initialize(context);
	}
	
	
	public static AnalysisEngine createAnalysisEngine_Brown(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		AnalysisEngine retval=null;
		try {
			File modelFile = File.createTempFile("brown_model_v02", "bin");
			ClassPathUtil.copyClasspathResourceToFile(LingPipePOSTagger_AE.class, 
					"/nlp-tools/lingpipe-3.1.2/demos/models/pos-en-general-brown.HiddenMarkovModel", 
					modelFile);
			
			File tagsetFile = File.createTempFile("brown_tags", "bin");
			ClassPathUtil.copyClasspathResourceToFile(LingPipePOSTagger_AE.class, 
					"/nlp-tools/tagsets/BrownCorpus", 
					tagsetFile);
			
			retval = createAnalysisEngine(tsd, modelFile.getAbsolutePath(), tagsetFile.getAbsolutePath());

		}
		catch (IOException x ) {
			throw new ResourceInitializationException(x);
		}
		return retval;
	}
	
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, String modelPath, String tagsetPath) 
	throws ResourceInitializationException {
		AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(
				LingPipePOSTagger_AE.class, tsd); 
		try {
			ExternalResourceFactory.bindResource(aed, FILE_KEY, modelPath);
			ExternalResourceFactory.bindResource(aed, TAG_SET, tagsetPath);
		}
		catch (InvalidXMLException x) {
			throw new ResourceInitializationException(x);
		}
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(aed);
		return ae;
	}

}
