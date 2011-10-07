/*
 * LingPipeSentenceDetector_AE.java
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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.nlp.wrapper.lingpipe.LingPipe_Util;

/**
 * This class uses the LingPipe sentence detection algorithm to split the document text of the CAS into sentences. A
 * <code>SentenceAnnotation</code> is created for each sentence that is detected in the document text.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class LingPipeSentenceDetector_AE extends SentenceDetector_AE {
	private static final Logger logger = Logger
			.getLogger(LingPipeSentenceDetector_AE.class.getName());
	
    @Override
	public void initialize(UimaContext context) throws ResourceInitializationException  {	
    	sentenceDetector = new LingPipe_Util();
		super.initialize(context); 
		logger.debug("in initialize()");
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				LingPipeSentenceDetector_AE.class, tsd);
	}
}
