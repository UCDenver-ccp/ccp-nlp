/*
 * LingPipeEntityTagger_AE.java
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
import java.util.UUID;

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

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.wrapper.banner.Banner_Util;

/**
 * This analysis engine wraps the BANNER entity tagging system into the CCP UIMA framework.
 * 
 * @author William A Baumgartner, Jr.
 */
public class BannerEntityTagger_AE extends EntityTagger_AE {
	static Logger logger = Logger.getLogger(BannerEntityTagger_AE.class);

	final static String MODEL_FILE_KEY = "BannerEntityModelFile";
	final static String PROPERTIES_FILE_KEY = "BannerPropertiesFile";
	final static String LEMMATISER_PATH_KEY = "LemmatiserDataDirectory";
	final static String POS_PATH_KEY = "PosTaggerDataDirectory";

	@ExternalResource(key = MODEL_FILE_KEY)
	DataResource modelFileDR;

	@ExternalResource(key = PROPERTIES_FILE_KEY)
	DataResource propertiesFileDR;

	@ExternalResource(key = LEMMATISER_PATH_KEY)
	DataResource lemmatiserPathDR;

	@ExternalResource(key = POS_PATH_KEY)
	DataResource posPathDR;

	/**
	 * The BANNER entity tagger requires a model file and a properties file to run. Both files are
	 * defined in the descriptor for this Analysis Engine. It is loaded here, and the entity tagger
	 * is initialized prior to processing any of the document text.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		entityTagger = new Banner_Util();

		/*
		 * Get the model file as defined by the Resource section of the descriptor for this Analysis
		 * Engine
		 */
		String bannerModelFile = modelFileDR.getUri().getPath();
		String bannerPropertiesFile = propertiesFileDR.getUri().getPath();
		String lemmatiserDataDirectory = lemmatiserPathDR.getUri().getPath();
		String posTaggerDataDirectory = posPathDR.getUri().getPath();

		/* initialize the BANNER tagging system */
		try {
			String[] args = { bannerPropertiesFile, bannerModelFile, lemmatiserDataDirectory, posTaggerDataDirectory };
			entityTagger.initialize(ITagger.ENTITY_TAGGER, args);
		} catch (InitializationException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription_BioCreative(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		try {
			File modelFile = File.createTempFile("gene_model_v02", "bin");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class, "/banner/models/gene_model_v02.bin",
					modelFile);
			return createAnalysisEngineDescription(tsd, modelFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngine createAnalysisEngine_BioCreative(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription_BioCreative(tsd));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription_Disease(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		try {
			File modelFile = File.createTempFile("disease_model_AZDC-mod125", "bin");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/models/disease_model_AZDC-mod125.bin", modelFile);
			return createAnalysisEngineDescription(tsd, modelFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public static AnalysisEngine createAnalysisEngine_Disease(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription_Disease(tsd));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, File model)
			throws ResourceInitializationException {
		AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(BannerEntityTagger_AE.class,
				tsd);
		try {
			// The call to bindResource needs the key and the specifier
			// The xml descriptor files put a name in the middle, so you read
			// from key to name in one section, and from name to specifier in another.
			// comments below are in the order/form: key name specifier
			ExternalResourceFactory.bindResource(aed, MODEL_FILE_KEY, model);
			// BannerEntityModelFile BannerBioCreativeModelFile
			// nlp-tools/BANNER/data/models/gene_model_v02.bin</fileUrl>

			File propertiesFile = File.createTempFile("banner", "properties");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/properties/banner.properties", propertiesFile);
			ExternalResourceFactory.bindResource(aed, PROPERTIES_FILE_KEY, propertiesFile);

			File lemmatiserDirectory = FileUtil.createTemporaryDirectory("lemmatiser");
			File adj_exc = new File(lemmatiserDirectory, "adj.exc");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/adj.exc", adj_exc);
			File adj_index = new File(lemmatiserDirectory, "adj.index");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/adj.index", adj_index);
			File adv_exc = new File(lemmatiserDirectory, "adv.exc");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/adv.exc", adv_exc);
			File adv_index = new File(lemmatiserDirectory, "adv.index");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/adv.index", adv_index);
			File noun_exc = new File(lemmatiserDirectory, "noun.exc");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/noun.exc", noun_exc);
			File stopwordexc_list = new File(lemmatiserDirectory, "stopwordexc.list");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/stopwordexc.list", stopwordexc_list);
			File umlserror_list = new File(lemmatiserDirectory, "umlserror.list");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/umlserror.list", umlserror_list);
			File verb_exc = new File(lemmatiserDirectory, "verb.exc");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/verb.exc", verb_exc);
			File verb_index = new File(lemmatiserDirectory, "verb.index");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/lemmatiser/verb.index", verb_index);
			ExternalResourceFactory.bindResource(aed, LEMMATISER_PATH_KEY, lemmatiserDirectory);

			File taggerDirectory = FileUtil.createTemporaryDirectory("tagger");
			File lexDB_serial = new File(taggerDirectory, "lexDB.serial");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/tagger/lexDB.serial", lexDB_serial);
			File lexicon_all = new File(taggerDirectory, "lexicon_all");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/tagger/lexicon_all", lexicon_all);
			File ngramOne_serial = new File(taggerDirectory, "ngramOne.serial");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class,
					"/banner/nlpdata/tagger/ngramOne.serial", ngramOne_serial);
			File rules_cap = new File(taggerDirectory, "rules_cap");
			ClassPathUtil.copyClasspathResourceToFile(BannerEntityTagger_AE.class, "/banner/nlpdata/tagger/rules_cap",
					rules_cap);
			ExternalResourceFactory.bindResource(aed, POS_PATH_KEY, taggerDirectory);
		} catch (InvalidXMLException x) {
			throw new ResourceInitializationException(x);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		return aed;
	}
}
