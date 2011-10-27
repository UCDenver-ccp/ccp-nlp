/*
 * Abner_Util.java
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

package edu.ucdenver.ccp.nlp.wrapper.abner;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import edu.ucdenver.ccp.common.io.LoggingOutputStream;
import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.nlp.abner.ext.Tagger;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.interfaces.IEntityTagger;
import edu.ucdenver.ccp.nlp.core.interfaces.ITagger;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionTypes;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;


/**
 * This is an interface to Burr Settles' named entity recognizing application, ABNER. <br>
 * <a href="http://pages.cs.wisc.edu/~bsettles/abner/">http://pages.cs.wisc.edu/~bsettles/abner/</a></br>
 * <p>
 * Currently, this interface allows the user to specify the two models that 
 * come prepackaged in the abner.jar file. By default the ABNER tokenization
 * routine is used.
 * <p>
 * Extracted entities are returned as instances of 
 * <code>edu.uchsc.ccp.util.nlp.annotation.TextAnnotation</code>.
 * <p>
 * When the built-in BioCreative model is used, only protein annotations are returned. 
 * When the NLPBA model is used, protein, RNA, DNA, cell line, and
 * cell type annotations are returned.
 * 
 * @author William A. Baumgartner Jr.
 * 
 */
public class Abner_Util implements IEntityTagger {

	Logger logger = Logger.getLogger(Abner_Util.class);
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		;
	}
	
    private final Annotator annotator = new Annotator(2, "", "ABNER", "WISC");
    public enum TagMappingName  { CRAFT, NLPBA };
    TagMappingName tagMapping;
    
    static final String NLPBAMapping[] = { 
        	"protein", 		ClassMentionTypes.PROTEIN,
        	"dna", 			ClassMentionTypes.DNA, 
        	"rna",			ClassMentionTypes.RNA,
        	"cell_line", 	ClassMentionTypes.CELL_LINE,
        	"cell_type",	ClassMentionTypes.CELL_TYPE
    };
    
    static final String CRAFTMapping[] = {
    		"gene", ClassMentionTypes.GENE,
    		"polypeptide", 		ClassMentionTypes.POLYPEPTIDE,
    		"probe",			ClassMentionTypes.PROBE,
    		"transcript",		ClassMentionTypes.TRANSCRIPT ,
    		"mRNA", 			ClassMentionTypes.M_RNA ,
    		"cDNA", 			ClassMentionTypes.C_DNA ,
    		"transgene",		ClassMentionTypes.TRANSGENE ,
    		"engineered_region", ClassMentionTypes.ENGINEERED_REGION ,
    		"pseudogene", 		ClassMentionTypes.PSEUDOGENE ,
    		"cDNA_clone", 		ClassMentionTypes.C_DNA_CLONE ,
    		"clone", 			ClassMentionTypes.CLONE ,
    		"RNA", 				ClassMentionTypes.RNA ,
    		"promoter",			ClassMentionTypes.PROMOTER ,
    		"vector_replicon", 	ClassMentionTypes.VECTOR_REPLICON ,
    		"antisense_probe", 	ClassMentionTypes.ANTISENSE_PROBE ,
    		"fusion",			ClassMentionTypes.FUSION ,
    		"QTL", 				ClassMentionTypes.QTL ,
    		"plasmid",			ClassMentionTypes.PLASMID ,
    		"gene_cassette", 	ClassMentionTypes.GENE_CASSETTE ,
    		"cDNA_probe", 		ClassMentionTypes.C_DNA_PROBE ,
    		"RNAi_plasmid_vector",ClassMentionTypes.RNA_I_PLASMID_VECTOR ,
    		"siRNA_vector", 	ClassMentionTypes.S_I_RNA_VECTOR ,
    		"RNA_probe",		ClassMentionTypes.RNA_PROBE ,
    		
    		"primer", 			ClassMentionTypes.PRIMER ,
    		"mRNA_probe", 		ClassMentionTypes.M_RNA_PROBE ,
    		"siRNA", 			ClassMentionTypes.S_I_RNA ,
    		"RNAi_vector", 		ClassMentionTypes.RNA_I_VECTOR ,
    		"mRNA_probe", 		ClassMentionTypes.M_RNA_PROBE 
    };
    
    

    /*
     * The startLookingForMatchesAt variable keeps track of where we are looking 
     * for entities in the original text. This is used for determining the
     * entity spans (see below).
     */
    private int startLookingForMatchesAt;

    private Tagger abnerTagger = null;
    HashMap<String, String> mentionMap = null;

    HashSet<String> badTagSet = new HashSet<String>();

    private void initialize(String modelFilename, TagMappingName tagMapping) {        
        if (modelFilename != null) {
        	System.out.println("Initializing ABNER:" + modelFilename);
            File modelFile = new File(modelFilename);
            abnerTagger = new Tagger(modelFile);
            this.tagMapping = tagMapping;
        } else {
        	System.out.println("null uri");
            logger.error("Not sure what this URI points to, URI is null. "
            		+ "Check location of Abner model file."
            		+ modelFilename);
        }

        abnerTagger.setTokenization(true);
        
        if (tagMapping == TagMappingName.CRAFT) {
        	mentionMap = initMap(CRAFTMapping, true);
        }
        else if (tagMapping == TagMappingName.NLPBA) {
        	mentionMap = initMap(NLPBAMapping, true);
        }
    }
    
    public void initialize(int taggerType, String[] args) {
    	initialize(taggerType, TagMappingName.NLPBA, args);
    }
    public void initialize(int taggerType, TagMappingName mappingName, String[] args) { 	
    	
        String className = this.getClass().getName();
        String taggerTypeStr = ITagger.TAGGER_TYPES[taggerType];
        if (taggerType == ITagger.ENTITY_TAGGER) {
            if (args.length == 1) {
                String modelFile = args[0];
                initialize(modelFile, mappingName);
            } else {
                logger.error("Unexpected number of arguments (" + args.length + ") for " 
                		+ className + " " + taggerTypeStr + " initialization.");
                logger.error("The " + className + " " + taggerTypeStr 
                		+ " requires a single argument, the path to the entity model file.");
            }
        } else {
            throw new UnsupportedOperationException("Tagger type: " + taggerTypeStr 
            		+ " not supported by " + className);
        }

    }
    
    
    /**
     * Retrieve the entities extracted by ABNER from text as instances of 
     * <code>edu.uchsc.ccp.util.nlp.annotation.TextAnnotation</code>.
     * 
     * @param text
     *            The text to search for entities.
     * @param documentCollectionID
     *            This unique identifier for a document collection will be 
     *            assigned to any annotations returned by this method.
     * @param documentID
     *            This unqiue identifier for a document will be assigned to any 
     *            annotations returned by this method.
     * @return a List of <code>edu.uchsc.ccp.util.nlp.annotation.TextAnnotation</code> 
     * instances, one per entity detected in the text.
     */
    private List<TextAnnotation> getEntities(String text, String documentID) {
        List<TextAnnotation> annotationsToReturn = new ArrayList<TextAnnotation>();
        HashMap<String, String> annotMap = new HashMap<String, String>();

        /* Use ABNER to detect the entities 
         * http://pages.cs.wisc.edu/~bsettles/abner/javadoc/
         * from the page:
         * 
         * Similar to getSegments, but returns all segments in the entire document that 
         * correspond to entities (e.g. "DNA," "protein," etc.). 
         * Segment text is stored in result[0][...] 
         * and entity tags (minus "B-" and "I-" prefixes) are stored in result[1][...]. */
        
//        PrintStream oldOut = System.out;
//        PrintStream oldErr = System.err;

        //redirecting System.out/err to log4j appender
//        System.setOut(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.DEBUG),true));
//        System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.INFO),true));

//        System.out.println(abnerTagger.tagSGML(text));
//        System.out.println(abnerTagger.tagABNER(text));
//        System.out.println(abnerTagger.tagIOB(text));
        // calling abner
        String[][] entities=abnerTagger.getEntities(text);
        
        //restoring original System.out/err
//        System.setOut(oldOut);
//        System.setErr(oldErr);
        
        
        /* The rest of this method is devoted to determining the spans of the 
         * extracted entities, and creating TextAnnotations to return. */

        int annotationCount = 0;
        
        
        AnnotationSet annotationSet = new AnnotationSet();

        /* reset the index we will start looking for matches back to the beginning of the text */
        startLookingForMatchesAt = 0;

        /* hashmap to keep track of annotations that have already been stored... 
         * this prevents duplicate annotations from being stored. */
 
        for (int i = 0; i < entities[0].length; i++) {

	            String entity = entities[0][i];
	            String tag = entities[1][i];
	
	            /* find start/end indexes */
	            Span span = findSpan(entity, text);
	
	            if (span != null) {
	                String key = span.getSpanStart() + " " + span.getSpanEnd() + " " + tag;
	                if (!annotMap.containsKey(key.toLowerCase())) { 
	                	/* make certain no duplicate annotations are stored */
	                    annotMap.put(key, "");
	                    String mentionName = "";
	                    mentionName = mentionMap.get(tag.toLowerCase());
	                    if (mentionName == null) {
	                    	if (!badTagSet.contains(tag.toLowerCase())) {
	                    		logger.warn("Tag name not found in mention name map: \"" 
	                    			+ tag 
	                    			+ "\". Used tag name directly."
	                    			+ " Further uses of this tag will not create another warning.");
		                    	badTagSet.add(tag.toLowerCase());
	                    	}
	                    	mentionName = tag.toLowerCase();
	                    }

	                    ClassMention cm = new DefaultClassMention(mentionName);
	                    String coveredText = text.substring(span.getSpanStart(), span.getSpanEnd());
	                    TextAnnotation ta = new DefaultTextAnnotation(span.getSpanStart(), span.getSpanEnd(), 
	                    		coveredText, annotator, annotationSet,
	                            annotationCount, -1, documentID, -1, cm);
	                    annotationsToReturn.add(ta);
	                    annotationCount++;
	                } 
	                else {
	                	logger.warn("duplicate annotation? " + key);
	                }
	          
	            } else {
	                logger.error("Null span, Could not find: \"" + entity + "\" in documentText: \"" + text + "\"");
	                // TODO throw?!
	            }
	            
        }

        if (annotationCount != entities[0].length) {
            logger.error(entities[0].length + " annotations should have been created, however, only " + annotationCount++ + " were.");
        }

        return annotationsToReturn;
    }
    
    /**
     * inits a hash map from a string array. The even indexed strings are keys, (0,2,4...).
     * The odd indexed strings are values (1,3,5...)
     * @return
     */
    private HashMap<String, String> initMap(String[] values, boolean noValues) {
        HashMap<String,String> map = new HashMap<String,String>();
    	for (int i=0; i< values.length; i += 2) {
    		if (noValues) {
    			map.put(values[i], values[i]);
    		}
    		else {
    			map.put(values[i], values[i+1]);
    		}
    	}
    	return map;
    }

    /**
     * <p>
     * This method searches for a String of text in a different String of text using regular expressions.
     * <p>
     * There is a known error with this method as entities with '' are sometimes not detected. This error may have something to do with ABNER's
     * tokenization mechanism, as sometimes '' gets converted into ", and sometimes it does not.
     * 
     * @param subStr
     * @param wholeStr
     * @return
     */
    private Span findSpan(String subStr, String wholeStr) {

        /*
         * One idiosyncracy of ABNER is that it converts '' to ", so we must 
         * do the same in "wholeStr". We add a space after the " so that the
         * character offsets will remain consistent.
         */
        String processedWholeStr = wholeStr.replaceAll("''", "\" ");

        /* preprocess the string we are looking for by escaping any characters 
         * that need to be escaped in a regular expression */
        subStr = RegExPatterns.escapeCharacterForRegEx(subStr);

        String[] toks = subStr.split(" ");
        String regex = "";
        for (int i = 0; i < toks.length; i++) {
            regex += toks[i];
            if (i < toks.length - 1) {
                regex += "\\s*";
            } else {
                regex += "";
            }
        }

        Pattern subStrPattern = Pattern.compile(regex);
        Matcher subStrMatcher = subStrPattern.matcher(processedWholeStr);

        if (subStrMatcher.find(startLookingForMatchesAt)) {
            Span span = null;
            try {
                span = new Span(subStrMatcher.start(), subStrMatcher.end());
            } catch (InvalidSpanException e) {
                e.printStackTrace();
            }
            startLookingForMatchesAt = subStrMatcher.end();
            return span;
        } else {
            return null;
        }

    }

    public List<TextAnnotation> getEntitiesFromText(String inputText, String documentID) {
        return getEntities(inputText, documentID);
    }

    public List<TextAnnotation> getEntitiesFromText(String inputText) {
        return getEntities(inputText, "-1");
    }




}
