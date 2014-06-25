package edu.ucdenver.ccp.nlp.uima.serialization.bionlp;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;

import org.apache.log4j.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.AnalysisEngineFactory;


import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPStringSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;


/**
 * A UIMA analysis engine for creating output Bionlp ST formatted a1 files.
 * It looks for annotations with class mention names listed below, parses
 * and maps the names to classes and outputs the a1 files.
 * 
 * a1 files are created with an extension ".a1". The basename is from the
 * document id, initialized in the collection reader.
 * The mapping is hard-coded. It includes a GO decoding algorithm so that
 * If your GO entities' ids have the subOntology coded into the id,
 * that gets used. If not, it's benign.  
 * If the GO ids are like GO:CC_0001234 instead of GO:0001234,
 * the CC, the sub-ontology abbreviation is used in the mapping.
 **/
public class BionlpFormatPrinter_AE extends JCasAnnotator_ImplBase {

	public final static String PARAM_OUTPUT_PATH 
   		= ConfigurationParameterFactory.createConfigurationParameterName(
      		BionlpFormatPrinter_AE.class, "outputPath");
	@ConfigurationParameter(mandatory = false, defaultValue = "output/entity",
   		description="The path (relatvie or absolute) to where the output files should be created.")
	String outputPath;

	public final static String PARAM_ADD_NORMALIZATION 
   		= ConfigurationParameterFactory.createConfigurationParameterName(
      		BionlpFormatPrinter_AE.class, "addNormalization");
	@ConfigurationParameter(mandatory = false, defaultValue = "false",
   		description="True if you want the Nx rows that show the normalization ouput.")
	boolean  addNormalization;

	private static Logger logger = Logger.getLogger(BionlpFormatPrinter_AE.class);
	private BufferedWriter entityWriter;
	int entityCount = 0;

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger.info("Initializing BionlpFormatPrinter_AE ...");
	}

	/**
	 * @throws AnalysisEngineProcessException
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String filename = UIMA_Util.getDocumentID(jcas);
		logger.info("Processing BionlpFormatPrinter_AE " + filename);
		try {
			initializeOutputWriters(filename);
			entityCount=0;
			if (addNormalization) {
				printNormalizedEntityAnnotations(jcas);
System.out.println("DOING NORMALIZED");
			}
			else {
System.out.println("--not-- DOING NORMALIZED" + addNormalization);
				printEntityAnnotations(jcas);
			}
			entityWriter.close();
		} catch (IOException ioe) {
			throw new AnalysisEngineProcessException(ioe);
		}
		logger.info("Processed BionlpFormatPrinter_AE " + entityCount  + " entities for file " + filename );
	}

	private final static HashMap<String,String>  ontologyIdToClassNameMap
		= new HashMap<String,String>() { {

		put("protein", "protein");
		put("polypeptide", "protein");
		put("gene or transcript or polypeptide", "protein");
		put("gene", "protein");

		put("PR", "protein");
		put("SO", "sequence");
		put("GO", "go_term");
		put("CC", "cell_component");
		put("BP", "biological_process");
		put("MF", "molecular_function");
		put("NCBITaxon", "taxon");
			
	} };

	private String mapUimaToBionlp(String mentionName) {
		// UIMA analaysis engines produce (at least) two kinds of annotations for entities:
		// - class names like protein or gene
		// - ontology IDs like GO_0001234, or PR_0000123
		// ----> or GO:CC_0001234, a sub-ontolgoy included
		// GO is tricky because you need the sub-ontology to get a class name suitable for BioNLP.
		// The plan here is to split on underscores if possible and the use the map above,
		// with some additional code to deal with GO. (see below)

		String prefix = mentionName;
		int colonIndex =  mentionName.indexOf(":");
		if (colonIndex != -1) {
			prefix = mentionName.substring(0, colonIndex);
		}

		// Check to see if the GO id's have the sub-onotology embedded in them.
		// You might get GO:0001234 or GO:CC_0001234
		// If the latter, use the sub-ontology instead.
		if (prefix.equals("GO") && colonIndex != -1) {
	    	String suffix = mentionName.substring(colonIndex + 1);		
			int underIndex =  suffix.indexOf("_");
			if (underIndex != -1) {
				prefix = suffix.substring(0, underIndex);
			}
			
		}
	
		if(ontologyIdToClassNameMap.containsKey(prefix)) {
			return ontologyIdToClassNameMap.get(prefix);
		} else {
			return "ontology_term";
		}
		
	}

	/**
	 * outputs the Tx lines from BioNLP. Where in BioNLP '09, they were just labelled
     * "Protein", these may be other classes of things. The ID is mapped to such
     * classes by the function mapUimaToBionlp().
	 * 
	 * Ex. T1<tab>entity-class-name 21 29<tab>covered text
	 *
	 * Ex. T1	Protein 21 29	P41
     */
	private void printEntityAnnotations(JCas jcas) throws IOException {
		FSIterator taIterator = jcas.getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (taIterator.hasNext()) {
			CCPTextAnnotation ta = (CCPTextAnnotation) taIterator.next();
			entityCount++;
			String mentionName = ta.getClassMention().getMentionName();
			String entityClassName = mapUimaToBionlp(mentionName);
			if (entityClassName != null) {
				// T1<tab>class-name 21 29<tab>covered text
				String entityStr = "T" + entityCount + "\t" + entityClassName + " " + ta.getBegin() + " " + ta.getEnd()
						+ "\t" + ta.getCoveredText().trim();
				logger.debug("[" + ta.getBegin() + ".." + ta.getEnd() + "] " + entityClassName + " " + entityCount + " from mentioname: " + mentionName);
				FileWriterUtil.printLines(CollectionsUtil.createList(entityStr), entityWriter);
			}
			else {
				logger.warn("No mapping for class mention name: " + mentionName);
			}
		}

	}

	/**
  	 * As above, outputs the Tx line, but also a normalization line, Nx,
     * for showing the ontology id that the entity class came from.
     *
	 * Ex. T1<tab>entity-class-name 21 29<tab>covered text
     *     N1<tab>Reference T1 GO:xxxxxx<tab>protein_name
     *
	 * Ex. T1	cellular-component 21 29	ECM
     *     N1	Reference T1 GO:xxxxxx	extracellular matrix
	 */
	private void printNormalizedEntityAnnotations(JCas jcas) throws IOException {
        FSIterator taIterator = jcas.getAnnotationIndex(CCPTextAnnotation.type).iterator();
        while (taIterator.hasNext()) {
            CCPTextAnnotation ta = (CCPTextAnnotation) taIterator.next();
            entityCount++;
            String mentionName = ta.getClassMention().getMentionName();
            String entityClassName = mapUimaToBionlp(mentionName);
		 	CCPSlotMention canonicalBaseSlot = UIMA_Util.getSlotMentionByName(ta, 	SlotMentionType.CANONICAL_NAME.typeName());
			String canonicalName = "";
		 	CCPStringSlotMention canonicalNameSlot = (CCPStringSlotMention) canonicalBaseSlot;
			if (canonicalNameSlot != null && canonicalNameSlot.getSlotValues() != null) {
				canonicalName = canonicalNameSlot.getSlotValues().get(0);
			}
            if (entityClassName != null) {

				// T1<tab>entity-class-name 21 29<tab>covered text
                String entityStr = "T" + entityCount + "\t" + entityClassName + " " + ta.getBegin() + " " + ta.getEnd()
                        + "\t" + ta.getCoveredText().trim();
                logger.debug("[" + ta.getBegin() + ".." + ta.getEnd() + "] " + entityClassName + " " + entityCount + " from mentioname: " + mentionName);
                FileWriterUtil.printLines(CollectionsUtil.createList(entityStr), entityWriter);

    			// N1<tab>Reference T1 GO:xxxxxx<tab>protein_name
                String refStr = "N" + entityCount + "\tReference T" + entityCount + " " + mentionName
                        + "\t"  + canonicalName;
                FileWriterUtil.printLines(CollectionsUtil.createList(refStr), entityWriter);
            }
            else {
                logger.warn("No mapping for class mention name: " + mentionName);
            }
        }

	}
		
	private void initializeOutputWriters(String filename) throws IOException {
		String fileID = filename;
		if (filename.lastIndexOf(".") != -1) {
			fileID = filename.substring(0, filename.lastIndexOf("."));
		}
		String entityFileSuffix = ".a1";

		File entityFile = new File(outputPath + "/" + fileID + entityFileSuffix);
		entityWriter = FileWriterUtil.initBufferedWriter(entityFile, CharacterEncoding.UTF_8, 
			WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(
        TypeSystemDescription tsd,
        File outputDirectory)
    throws ResourceInitializationException {
        AnalysisEngineDescription desc = AnalysisEngineFactory.createPrimitiveDescription(
                BionlpFormatPrinter_AE.class,
                tsd,
                PARAM_OUTPUT_PATH,     outputDirectory.getAbsolutePath(),
				PARAM_ADD_NORMALIZATION, false);

        return desc;
    }
	public static AnalysisEngineDescription createAnalysisEngineDescription(
        TypeSystemDescription tsd,
        File outputDirectory, 
		boolean addNormalization)
    throws ResourceInitializationException {
        AnalysisEngineDescription desc = AnalysisEngineFactory.createPrimitiveDescription(
                BionlpFormatPrinter_AE.class,
                tsd,
                PARAM_OUTPUT_PATH,     outputDirectory.getAbsolutePath(),
				PARAM_ADD_NORMALIZATION, addNormalization
				);

        return desc;
    }

	public static AnalysisEngineDescription createAnalysisEngineDescription(
        TypeSystemDescription tsd)
    throws ResourceInitializationException {
        return AnalysisEngineFactory.createPrimitiveDescription(
                BionlpFormatPrinter_AE.class, tsd,
				PARAM_ADD_NORMALIZATION, false);
    }

	public static AnalysisEngineDescription createAnalysisEngineDescription(
        TypeSystemDescription tsd, boolean addNormalization)
    throws ResourceInitializationException {
        return AnalysisEngineFactory.createPrimitiveDescription(
                BionlpFormatPrinter_AE.class, tsd,
				PARAM_ADD_NORMALIZATION, addNormalization
				);
    }

}

