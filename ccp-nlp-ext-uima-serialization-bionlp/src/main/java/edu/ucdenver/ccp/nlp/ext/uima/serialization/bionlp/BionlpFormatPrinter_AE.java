package edu.ucdenver.ccp.nlp.ext.uima.serialization.bionlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;

public class BionlpFormatPrinter_AE extends JCasAnnotator_ImplBase {

	public static String SLOT_NAME = "Protein";

	public static int ANNOTATOR_ID = 195;
	public static int ANNOTATION_SET_ID = ANNOTATOR_ID;

	public static String PROTEIN_CLASS_MENTION_NAME = "protein";
	public static String PROTEIN_CLASS_MENTION_NAME_1 = "polypeptide";
	public static String PROTEIN_CLASS_MENTION_NAME_2 = "gene or transcript or polypeptide";
	public static String PROTEIN_CLASS_MENTION_NAME_3 = "gene";

	public static final String OUTPUT_FILE_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			BionlpFormatPrinter_AE.class, "entityOutputFile");

	@ConfigurationParameter(mandatory = true)
	private File entityOutputFile;

	private BufferedWriter outPS;

	private static Logger logger = Logger.getLogger(BionlpFormatPrinter_AE.class);

	private BufferedWriter entityWriter;

	// private BufferedWriter tokenWriter;

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		logger.info("Initializing Protein entity detection ...");
	}

	/**
	 * Analysis engine that cycle through every sentences and associate annotations within the
	 * sentences
	 * 
	 * @throws AnalysisEngineProcessException
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String filename = UIMA_Util.getDocumentID(jcas);
		String fileID = filename;

		if (filename.lastIndexOf(".") != -1) {
			fileID = filename.substring(0, filename.lastIndexOf("."));
		}

		try {
			initializeOutputWriters(fileID);
			printEntityAnnotations(jcas);
			closeOutputWriters();
		} catch (IOException ioe) {
			throw new AnalysisEngineProcessException(ioe);
		}
	}

	/**
	 * @param jcas
	 * @throws IOException
	 * 
	 */
	private void printEntityAnnotations(JCas jcas) throws IOException {
		int entityCount = 0;
		FSIterator ccptaIterator = jcas.getAnnotationIndex(CCPTextAnnotation.type).iterator();
		while (ccptaIterator.hasNext()) {
			CCPTextAnnotation ccpTa = (CCPTextAnnotation) ccptaIterator.next();
			entityCount++;
			String ccptaMentionName = ccpTa.getClassMention().getMentionName();
			if (ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME)
					|| ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_1)
					|| ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_2)
					|| ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_3)) {

				String spanIndex = Integer.toString(ccpTa.getBegin()) + ".." + Integer.toString(ccpTa.getEnd());
				logger.info("[" + spanIndex + "] " + "P" + entityCount);
				
				String entityStr = "T" + entityCount + "\t" + "Protein " + ccpTa.getBegin() + " " + ccpTa.getEnd()
						+ "\t" + ccpTa.getCoveredText().trim();
				FileWriterUtil.printLines(CollectionsUtil.createList(entityStr), entityWriter);
			}
		}

	}

	// private void printEntityAnnotation(String fileID) throws IOException {
	// // printIndex++;
	// String entityFileSuffix = ".a" + Integer.toString(printIndex);
	// String entityFilePath = "output/entity/";
	//
	// String tokenFileSuffix = ".token";
	// String tokenFilePath = "output/token/";
	//
	// File entityFile = new File(entityFilePath + fileID + entityFileSuffix);
	// BufferedWriter entityWriter = FileWriterUtil.initBufferedWriter(entityFile,
	// CharacterEncoding.UTF_8,
	// WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
	// FileWriterUtil.printLines(protAnnot, entityWriter);
	// // logger.info("EntityAnnotation :"+entitiesAnnotation.toString()) ;
	// entityWriter.close();
	//
	// File tokenFile = new File(tokenFilePath + fileID + tokenFileSuffix);
	// BufferedWriter tokenWriter = FileWriterUtil.initBufferedWriter(tokenFile,
	// CharacterEncoding.UTF_8,
	// WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
	// FileWriterUtil.printLines(tokensList, tokenWriter);
	// // logger.info("CoMentAnnotation :"+tokensList.toString()) ;
	// tokenWriter.close();
	// }

	/**
	 * initializes the output file writer(s)
	 * 
	 * @param fileID
	 * @throws IOException
	 */
	private void initializeOutputWriters(String fileID) throws IOException {
		// printIndex++;
		String entityFileSuffix = ".a1";
		String entityFilePath = "output/entity/";

		// String tokenFileSuffix = ".token";
		// String tokenFilePath = "output/token/";

		File entityFile = new File(entityFilePath + fileID + entityFileSuffix);
		entityWriter = FileWriterUtil.initBufferedWriter(entityFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);

		// File tokenFile = new File(tokenFilePath + fileID + tokenFileSuffix);
		// tokenWriter = FileWriterUtil.initBufferedWriter(tokenFile, CharacterEncoding.UTF_8,
		// WriteMode.OVERWRITE,
		// FileSuffixEnforcement.OFF);
	}

	/**
	 * Closes the output file writers
	 * 
	 * @throws IOException
	 */
	private void closeOutputWriters() throws IOException {
		entityWriter.close();
	}

	// /**
	// * Gets the target annotation within the sentence. This includes recursively pulling out slot
	// * spans as well.
	// *
	// * @param jcas
	// * ,
	// * @param ccpta
	// * @return result (non-Javadoc) Returns a List containing the annotations mentioned within the
	// * Span
	// * @throws AnalysisEngineProcessException
	// */
	//
	// private ArrayList<CCPTextAnnotation> getAnnotationsInWindow(JCas jcas, CCPTextAnnotation
	// ccpta)
	// throws AnalysisEngineProcessException {
	//
	// ArrayList<CCPTextAnnotation> result = new ArrayList<CCPTextAnnotation>();
	//
	// FSIterator completeIt = jcas.getFSIndexRepository().getAnnotationIndex().iterator();
	//
	// completeIt.moveTo(ccpta);
	//
	// while (completeIt.isValid() && ((Annotation) completeIt.get()).getBegin() >=
	// ccpta.getBegin()) {
	// completeIt.moveToPrevious();
	// }
	//
	// if (completeIt.isValid()) {
	// completeIt.moveToNext();
	// } else {
	// completeIt.moveToFirst();
	// }
	//
	// while (completeIt.isValid() && ((Annotation) completeIt.get()).getBegin() < ccpta.getBegin())
	// {
	// completeIt.moveToNext();
	// }
	//
	// while (completeIt.isValid() && ((Annotation) completeIt.get()).getBegin() >=
	// ccpta.getBegin()) {
	// Annotation annotation = (Annotation) completeIt.get();
	// if (annotation.getEnd() <= ccpta.getEnd() && annotation instanceof CCPTextAnnotation) {
	// entityIndex++;
	// String ccptaMentionName = ((CCPTextAnnotation)
	// annotation).getClassMention().getMentionName();
	// // logger.info(ccptaMentionName+" : "+((CCPTextAnnotation)annotation).getCoveredText());
	// /*
	// * if (ccptaMentionName.equals("sentence")) {
	// * logger.info("Sentences : "+((CCPTextAnnotation
	// * )annotation).getCoveredText().trim()) ; }
	// */
	// if (ccptaMentionName.equals("token")) {
	// // logger.info("Tokens : "+((CCPTextAnnotation)annotation).getCoveredText().trim())
	// // ;
	// tokensList.add(((CCPTextAnnotation) annotation).getCoveredText().trim());
	// }
	// if (ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME)
	// || ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_1)
	// || ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_2)
	// || ccptaMentionName.equals(PROTEIN_CLASS_MENTION_NAME_3)) {
	// // System.out.println("Protein : "+((CCPTextAnnotation)annotation).getCoveredText());
	//
	// int protSize = protAnnot.size();
	// protSize++;
	//
	// int beginIndex = 0;
	// int endIndex = 0;
	//
	// beginIndex = ((CCPTextAnnotation) annotation).getBegin();
	// endIndex = ((CCPTextAnnotation) annotation).getBegin()
	// + ((CCPTextAnnotation) annotation).getCoveredText().trim().length();
	//
	// String spanIndex = Integer.toString(beginIndex) + Integer.toString(endIndex);
	// logger.info("Span Index Prot :" + spanIndex + ":" + "P" + protAnnot.size());
	// /*
	// * logger.info( "T"+ protAnnot.size() +"\t"+ccptaMentionName+" "+beginIndex+" "+
	// * endIndex +"\t"+ ((CCPTextAnnotation)annotation).getCoveredText().trim() );
	// */
	// protAnnot.add("T" + protSize + "\t" + "Protein " + beginIndex + " " + endIndex + "\t"
	// + ((CCPTextAnnotation) annotation).getCoveredText().trim());
	// logger.info("T" + protSize + "\t" + ccptaMentionName + " " + beginIndex + " " + endIndex +
	// "\t"
	// + ((CCPTextAnnotation) annotation).getCoveredText().trim());
	// result.add((CCPTextAnnotation) annotation);
	// }
	// }
	// completeIt.moveToNext();
	// }
	// tokensList.add(".\n");
	// return result;
	// }

}
