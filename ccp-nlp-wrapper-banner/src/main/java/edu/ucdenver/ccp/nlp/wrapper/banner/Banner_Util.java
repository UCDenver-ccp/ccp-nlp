/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.wrapper.banner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import banner.BannerProperties;
import banner.Sentence;
import banner.processing.PostProcessor;
import banner.tagging.CRFTagger;
import banner.tagging.Mention;
import banner.tokenization.Tokenizer;
import bc2.Base;
import dragon.nlp.tool.HeppleTagger;
import dragon.nlp.tool.MedPostTagger;
import dragon.nlp.tool.Tagger;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.umass.cs.mallet.base.fst.CRF;
import edu.umass.cs.mallet.base.util.MalletLogger;

public class Banner_Util extends Base implements IEntityTagger {

	private BannerProperties properties;
	private File modelFile;
	private Tokenizer tokenizer;
	private CRFTagger tagger;
	private PostProcessor postProcessor;
	private final Annotator annotator = new Annotator(123, "BANNER", "BANNER", "ASU");
	org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Banner_Util.class);

	/**
	 * Because relative paths can be tricky, we ignore the lemmatiserDataDirectory and
	 * postaggerDataDirectory input from the parameter file and use passed in values instead.
	 * 
	 * @param bannerPropertiesFile
	 * @param bannerModelFile
	 * @param lemmatiserDataDirectory
	 * @param postaggerDataDirectory
	 */
	private void initialize(String bannerPropertiesFilename, String bannerModelFile, 
			String lemmatiserDataDirectory, String posTaggerDataDirectory) {
		properties = BannerProperties.load(bannerPropertiesFilename);
		modelFile = new File(bannerModelFile);
		tokenizer = properties.getTokenizer();
		Logger.getLogger(CRF.class.getName()).setLevel(Level.OFF);
		MalletLogger.getLogger(CRF.class.getName()).setLevel(Level.OFF);
		Tagger posTagger;

		/*
		 * Instead of using BannerProperties, we need to look directly at the 
		 * properties file in order to extract the name of the pos tagger to be used
		 */
		try {
			Properties bannerProperties = new Properties();
			bannerProperties.load(new FileInputStream(bannerPropertiesFilename));

			String posTaggerName = bannerProperties.getProperty("posTagger", HeppleTagger.class.getName());
			if (posTaggerName.equals(HeppleTagger.class.getName())) {
				posTagger = new HeppleTagger(posTaggerDataDirectory);
			} else if (posTaggerName.equals(MedPostTagger.class.getName())) {
				posTagger = new MedPostTagger(posTaggerDataDirectory);
			} else {
				throw new IllegalArgumentException("Unknown POS tagger type: " + posTaggerName);
			}

			tagger = CRFTagger.load(modelFile, new EngLemmatiser(lemmatiserDataDirectory, false, true), posTagger);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		postProcessor = properties.getPostProcessor();
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private List<TextAnnotation> findEntities(String line, String documentID) {
		List<TextAnnotation> entityAnnotations = new ArrayList<TextAnnotation>();

		HashMap<String, LinkedList<Base.Tag>> tags = new HashMap<String, LinkedList<Base.Tag>>();
		Set<Mention> mentionsTest = new HashSet<Mention>();
		// commented out this line bc it spills too much info to the console.
		// properties.log();
		Sentence sentence = null;
		if (line != null && !line.equals("")) {
			String sentenceText = line;
			sentence = getSentence(null, sentenceText, properties.getTokenizer(), tags);
			mentionsTest.addAll(sentence.getMentions());
			sentenceText = sentence.getText();
			Sentence sentence2 = new Sentence(sentence.getTag(), sentenceText);
			tokenizer.tokenize(sentence2);

			try {
				tagger.tag(sentence2);
			} catch (Exception x) {
				logger.warn("BANNER failure to process (" + documentID + ") sentence: \"" + sentence2.getText() + "\"");
				return entityAnnotations;
			}

			if (postProcessor != null)
				postProcessor.postProcess(sentence2);
			// For training text sentence2.getTrainingText(properties.getTagFormat());
			List<Mention> mentions = sentence2.getMentions();
			for (Mention mention : mentions) {
				int start = mention.getStartChar();
				int end = mention.getEndChar();
				String str = mention.getText();
				String type = mention.getType().getText();

				TextAnnotation ta = new DefaultTextAnnotation(start, end);
				ta.setAnnotator(annotator);
				ta.setDocumentID(documentID);
				ta.setCoveredText(str);
				ta.addAnnotationSet(new AnnotationSet());

				ClassMention cm;
				cm = new DefaultClassMention(type.toLowerCase());
				ta.setClassMention(cm);

				entityAnnotations.add(ta);
			}
		}
		return entityAnnotations;
	}

	public List<TextAnnotation> getEntitiesFromText(String inputText, String documentID) {
		return findEntities(inputText, documentID);
	}

	public List<TextAnnotation> getEntitiesFromText(String inputText) {
		return getEntitiesFromText(inputText, "-1");
	}

	public void initialize(int taggerType, String[] args) {
		String className = this.getClass().getName();
		String taggerTypeStr = ITagger.TAGGER_TYPES[taggerType];
		if (taggerType == ITagger.ENTITY_TAGGER) {
			if (args.length == 4) {
				String bannerPropertiesFile = args[0];
				String bannerModelFile = args[1];
				String lemmatiserDataDirectory = args[2];
				String posTaggerDataDirectory = args[3];
				initialize(bannerPropertiesFile, bannerModelFile, lemmatiserDataDirectory, posTaggerDataDirectory);
			} else {
				error("Unexpected number of arguments (" + args.length + ") for " + className + " " + taggerTypeStr
						+ " initialization.");
				usage("The "
						+ className
						+ " "
						+ taggerTypeStr
						+ " requires four arguments, the path to the properties file, the path to the entity model file, the lemmatiser data directory, and the POS tagger data directory");
			}
		} else {
			throw new UnsupportedOperationException("Tagger type: " + taggerTypeStr + " not supported by " + className);
		}

	}

	/**
	 * Print an error message to System.err
	 * 
	 * @param message
	 */
	protected void error(String message) {
		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.'));
		System.err.println("ERROR -- " + className + ": " + message);
	}

	/**
	 * Print a usage message to System.err
	 * 
	 * @param message
	 */
	protected void usage(String message) {
		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.'));
		System.err.println("USAGE -- " + className + ": " + message);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}
}
