/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.serialization.bionlp;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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
import edu.ucdenver.ccp.nlp.uima.serialization.bionlp.parser.BioNlpEventIterator;
import edu.ucdenver.ccp.nlp.uima.serialization.bionlp.parser.BioNlpThemeIterator;
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
