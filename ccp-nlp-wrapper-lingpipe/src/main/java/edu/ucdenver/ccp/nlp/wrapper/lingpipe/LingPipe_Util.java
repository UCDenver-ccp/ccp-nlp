/*
 * LingPipe_Util.java 
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

package edu.ucdenver.ccp.nlp.wrapper.lingpipe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Streams;

import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotation_Util;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.exception.InitializationException;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.core.interfaces.IPOSTagger;
import edu.ucdenver.ccp.nlp.core.interfaces.ISentenceDetector;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * This utility incorporates some of the LingPipe functionality in a standardized output format through the use of the <code>TextAnnotation</code>
 * class.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class LingPipe_Util  implements ISentenceDetector, IPOSTagger, IEntityTagger {
	
	private static Logger logger = Logger.getLogger(LingPipe_Util.class);
	
    private TokenizerFactory TOKENIZER_FACTORY = new IndoEuropeanTokenizerFactory();

    private final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();

    private final SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(TOKENIZER_FACTORY, SENTENCE_MODEL);

    private HmmDecoder decoder = null;

    // corresponds with the CCP Annotation Database
    private final int annotatorID = 22;

    private final boolean DEBUG = false;

    private final Annotator annotator = new Annotator(new Integer(22), "", "LingPipe", "Alias-i");

    private final AnnotationSet annotationSet = new AnnotationSet();

    private Chunker entityChunker;

    private String entityType;

    private boolean posTaggerInitialized = false;

    private boolean entityTaggerInitialized = false;
    
    private String partOfSpeechTagset = Annotation_Util.UNKNOWN_TAGSET;

    /**
     * Use SentenceModel to find sentence boundaries in text LingPipe relevant code used by this method was taken from:
     * http://alias-i.com/lingpipe/demos/tutorial/sentences/src/SentenceChunkerDemo.java
     * 
     * @param inputText
     */
    private List<TextAnnotation> findSentences(int charOffset, String inputText, String documentID) {
        /* this method will return a list of TextAnnotations; one per sentence */
        List<TextAnnotation> annotations = new ArrayList<TextAnnotation>();

        Chunking chunking = SENTENCE_CHUNKER.chunk(inputText.toCharArray(), 0, inputText.length());
        Set sentences = chunking.chunkSet();
        if (sentences.size() < 1) {
            if (inputText.trim().length() > 0) {
                System.out.println("WARNING -- LingPipe_Util: No sentence chunks found for input text: \"" + inputText + "\"");
            }
            return annotations;
        }
        for (Iterator it = sentences.iterator(); it.hasNext();) {
            Chunk sentence = (Chunk) it.next();
            int start = sentence.start();
            int end = sentence.end();

            TextAnnotation ta = new DefaultTextAnnotation(start+ charOffset, end + charOffset);
            ta.setAnnotator(annotator);
            ta.setDocumentID(documentID);
            ta.setCoveredText(inputText.substring(start, end));

            ta.addAnnotationSet(annotationSet);

            ClassMention cm = new DefaultClassMention(ClassMentionTypes.SENTENCE);
            try {
				ta.setClassMention(cm);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

            annotations.add(ta);
        }
        
        /* if the sentence has a line break in it, then split the sentence on the line break */
        List<TextAnnotation> annotationsToRemove = new ArrayList<TextAnnotation>();
        List<TextAnnotation> annotationsToAdd = new ArrayList<TextAnnotation>();
        for (TextAnnotation sentenceAnnot : annotations) {
            if (sentenceAnnot.getCoveredText().contains("\n")) {
                String[] splitSentence = sentenceAnnot.getCoveredText().split("\\n");
                int spanStart = sentenceAnnot.getAnnotationSpanStart();
                for (String sent : splitSentence) {

                    TextAnnotation ta = new DefaultTextAnnotation(spanStart, spanStart + sent.length());
                    ta.setAnnotator(annotator);
                    ta.setDocumentID(documentID);
                    ta.setCoveredText(sent);
//                    ta.setAnnotationSpanEnd(spanStart + sent.length());
//                    ta.setAnnotationSpanStart(spanStart);
                    ta.addAnnotationSet(annotationSet);
                    ClassMention cm = new DefaultClassMention(ClassMentionTypes.SENTENCE);
                    try {
						ta.setClassMention(cm);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

                    spanStart = spanStart + sent.length() + 1;
                    
                    annotationsToAdd.add(ta);
                }
                /* remove the original that has now been split */
                annotationsToRemove.add(sentenceAnnot);
            }
            
        }
        
        for (TextAnnotation ta : annotationsToRemove) {
            annotations.remove(ta);
        }
        
        for (TextAnnotation ta : annotationsToAdd) {
            annotations.add(ta);
        }
        
        return annotations;
    }

    /**
     * Much of this code was taken from: http://alias-i.com/lingpipe/demos/tutorial/posTags/src/RunMedPost.java NOTE: initializeTaggingSystem() must
     * be called before this method.
     * 
     * @param medPostModelFile
     * @param inputText
     */
    private List<TextAnnotation> runPOSTagger(String inputText, String documentID) {
        if (posTaggerInitialized) {
            ArrayList<TextAnnotation> posAnnotations = new ArrayList<TextAnnotation>();
            char[] cs = inputText.toCharArray();
            Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0, cs.length);
            try {
                String[] tokens = tokenizer.tokenize();
                String[] tags = decoder.firstBest(tokens);
                posAnnotations = getTagAnnotations(tokens, tags, inputText, documentID);
            } catch (Exception e) {
                logger.warn("Caught exception while tokenizing text: DOCID=" + documentID + " TEXT=" + inputText);
            }
            return posAnnotations;
        } else {
            System.err
                    .println("LingPipe POS Tagger has not been initialized. Please call initializePOSTaggingSystem() prior to tagging text.");
            return null;
        }
    }

    /**
     * 
     * @param modelFile
     * @param tagset - the tagset used for part of speech tags
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void initializePOSTagger(String modelFile, String tagset) throws ClassNotFoundException, IOException {
        TOKENIZER_FACTORY = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
        if (tagset != null) {
        	this.partOfSpeechTagset = tagset;
        }
        System.out.println("Reading model from file=" + modelFile);
        System.out.println("Tagset = " + tagset);
        FileInputStream fileIn = new FileInputStream(modelFile);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
        Streams.closeInputStream(objIn);
        decoder = new HmmDecoder(hmm);

        posTaggerInitialized = true;
    }

    private ArrayList<TextAnnotation> getTagAnnotations(String[] tokens, String[] tags, String inputText, String documentID) {
        ArrayList<TextAnnotation> returnAnnotations = new ArrayList<TextAnnotation>();
        /* originalIndex keeps track of where you are in the original text */
        int originalIndex = 0;
        Pattern origTextPattern;
        Matcher origTextMatcher;

        /* cycle through each token, find it's span in the original text, and create a new TextAnnotation */
        for (int i = 0; i < tokens.length; i++) {
            TextAnnotation ta = new DefaultTextAnnotation(0,1);
            ta.setAnnotator(annotator);
            ta.setDocumentID(documentID);
            ta.addAnnotationSet(annotationSet);
            
            if (DEBUG) {
                System.out.println(" Looking for: " + tokens[i] + "_" + tags[i] + "\tstarting at originalIndex: " + originalIndex);
            }

            /* look for the tagged text in the input text to get the correct span */
            origTextPattern = Pattern.compile(RegExPatterns.escapeCharacterForRegEx(tokens[i]));
            origTextMatcher = origTextPattern.matcher(inputText);

            if (origTextMatcher.find(originalIndex)) {
                /* set the span end */
                ta.setAnnotationSpanEnd(origTextMatcher.end());
                /* set the span beginning */
                ta.setAnnotationSpanStart(origTextMatcher.start());

                /* set the covered text from the original (untagged) text */
                ta.setCoveredText(origTextMatcher.group());

                /* set the class mention for the annotation to be a token, with a slot for POS (stem and lemma are set to null) */
                try {
					ta.setClassMention(Annotation_Util.createTokenMention(tags[i], partOfSpeechTagset, null, null, i));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

                /* check to make sure span appear legitimate */
                if ((ta.getAnnotationSpanEnd() - ta.getAnnotationSpanStart()) != ta.getCoveredText().length()) {
                    System.err.println("Annotation span lengths do not match. " + (ta.getAnnotationSpanEnd() - ta.getAnnotationSpanStart())
                            + " != " + ta.getCoveredText().length());
                    ta.printAnnotation(System.err);
                }
                /* update originalIndex so that next search starts at end of previous */
                originalIndex = ta.getAnnotationSpanEnd();
                returnAnnotations.add(ta);
            } else {
                /* something is wrong because it should have found the text */
                System.err.println("Error, could not find original text: " + tokens[i] + " starting at: " + originalIndex + " in : "
                        + inputText);
            }
        }
        return returnAnnotations;
    }

    public int getAnnotatorID() {
        return annotatorID;
    }

    /**
     * Given a model file, initializes the entity tagging system
     */
    private void initializeEntityTagger(String modelFile) throws IOException, InitializationException {
        File entityModelFile = new File(modelFile);
        try {
            entityChunker = (Chunker) AbstractExternalizable.readObject(entityModelFile);
        } catch (ClassNotFoundException e) {
        	logger.error(e);
            throw new InitializationException(e);
        }
        entityTaggerInitialized = true;
    }

    /**
     * Extracts entities from text. Known limitation, this extraction routine currently supports only model files that are used to detect a single
     * type of entity, e.g. proteins from the MedTag model file.
     */
    private List<TextAnnotation> extractEntities(String inputText, String documentID) {
        if (entityTaggerInitialized) {
            List<TextAnnotation> entityAnnotations = new ArrayList<TextAnnotation>();

            Chunking chunking = entityChunker.chunk(inputText.toCharArray(), 0, inputText.length());
            Set mentions = chunking.chunkSet();
            for (Iterator it = mentions.iterator(); it.hasNext();) {
                Chunk mention = (Chunk) it.next();
                int start = mention.start();
                int end = mention.end();
                String type = mention.type();

                TextAnnotation ta = new DefaultTextAnnotation(start, end);
                ta.setAnnotator(annotator);
                ta.setDocumentID(documentID);
                ta.setCoveredText(inputText.substring(start, end));
//                ta.setAnnotationSpanEnd(end);
//                ta.setAnnotationSpanStart(start);
                ta.addAnnotationSet(annotationSet);

                ClassMention cm;
//                if (entityType != null) {
//                    cm = new ClassMention(entityType);
//                } else {
                    cm = new DefaultClassMention(mention.type().toLowerCase());
//                }
                try {
					ta.setClassMention(cm);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

                entityAnnotations.add(ta);

            }

            return entityAnnotations;
        } else {
            System.err
                    .println("LingPipe Entity tagger not initialized. Please call initializeEntityTaggingSystem() prior to tagging entities.");
            return null;
        }
    }

//    public void warn(String message) {
//        System.err.println("WARNING -- LingPipe_Util: " + message);
//    }


	public List<TextAnnotation> getSentencesFromText(String inputText, String documentID) {
		return findSentences(0, inputText, documentID);
	}
	
	@Override
	public List<TextAnnotation> getSentencesFromText(String inputText) {
		return findSentences(0, inputText, "-1");
	}
	
	@Override
	public List<TextAnnotation> getSentencesFromText(int charOffset, String inputText) {
		return findSentences(charOffset, inputText, "-1");
	}

	public List<TextAnnotation> getTokensWithPOSTagsFromText(String inputText, String documentID) {
		return runPOSTagger(inputText, documentID);
	}

	public List<TextAnnotation> getTokensWithPOSTagsFromText(String inputText) {
		return runPOSTagger(inputText, "-1");
	}
	
	public List<TextAnnotation> getTokensFromText(String inputText, String documentID) {
		return runPOSTagger(inputText, documentID);
	}
	
	public List<TextAnnotation> getTokensFromText(String inputText) {
		return runPOSTagger(inputText, "-1");
	}

	public List<TextAnnotation> getEntitiesFromText(String inputText, String documentID) {
		return extractEntities(inputText, documentID);
	}

	public List<TextAnnotation> getEntitiesFromText(String inputText) {
		return extractEntities(inputText, "-1");
	}

	/**
	 * This method controls the initialization of LingPipe Applications.
	 */
	public void initialize(int taggerType, String[] args)
	throws InitializationException {
		String className = this.getClass().getName();
		String taggerTypeStr = ITagger.TAGGER_TYPES[taggerType];
		if (taggerType == ITagger.ENTITY_TAGGER) {
			if (args.length == 1) {
				String modelFile = args[0];
				try {
					initializeEntityTagger(modelFile);
				} catch (IOException e) {
					logger.error("Exception while initializing LingPipe Entity Tagger with file: "
							+ modelFile.toString());
					throw new InitializationException(e);
				}
			} else {
				logger.error("Unexpected number of arguments (" + args.length + ") for " + className + " " + taggerTypeStr + " initialization.");
				logger.error("The " + className + " " + taggerTypeStr + " requires a single argument, the path to the entity model file.");
			}
		} else if (taggerType == ITagger.POS_TAGGER) {
			if (args.length == 2) {
				String modelFile = args[0];
				String tagset = args[1];
				try {
					initializePOSTagger(modelFile, tagset);
				} catch (ClassNotFoundException e) {
					logger.error("Exception while initializing LingPipe POS Tagger");
					throw new InitializationException(e);
				} catch (IOException e) {
					logger.error("Exception while initializing LingPipe POS Tagger");
					throw new InitializationException(e);
				}
			} else {
				logger.error("Unexpected number of arguments (" + args.length + ") for " + className + " " + taggerTypeStr + " initialization.");
				logger.error("The " + className + " " + taggerTypeStr + " requires a two arguments, the path to the part-of-speech model file and part-of-speech tagset being used.");
			}
		} else {
			throw new UnsupportedOperationException("Tagger type: " + taggerTypeStr + " not supported by " + className);
		}
		
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	
	
}
