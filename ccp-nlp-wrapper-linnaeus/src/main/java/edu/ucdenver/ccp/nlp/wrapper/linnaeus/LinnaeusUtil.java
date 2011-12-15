package edu.ucdenver.ccp.nlp.wrapper.linnaeus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import martin.common.ArgParser;
import uk.ac.man.entitytagger.EntityTagger;
import uk.ac.man.entitytagger.Mention;
import uk.ac.man.entitytagger.matching.Matcher;
import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

public class LinnaeusUtil {

	private static final Logger logger = Logger.getLogger(LinnaeusUtil.class.getName());
	public static final Annotator LINNAEUS_ANNOTATOR = new Annotator(1000, "Linnaeus", "Linnaeus", "Manchester");
	public static final String ID_SLOT_NAME = "has_id";

	private static final String LINNAEUS_PROPERTIES_FILE_PREFIX = "linnaeus.";
	private static final String LINNAEUS_PROPERTIES_FILE_SUFFIX = ".properties";

	private final Matcher matcher;

	public LinnaeusUtil(File linnaeusPropertiesFile) {
		this.matcher = getMatcher(linnaeusPropertiesFile, logger);
	}

	/**
	 * Uses the initialized Linnaeus tagger to search the input text for matches. Matches are
	 * returned as a Collection of {@link TextAnnotation} objects
	 * 
	 * @param documentId
	 * @param text
	 * @return
	 */
	public Collection<TextAnnotation> annotateText(String documentId, String text, int offset) {
		Collection<TextAnnotation> matchedTAs = new ArrayList<TextAnnotation>();
		for (Mention mention : matcher.match(text, documentId))
			matchedTAs.addAll(convertToTextAnnotation(mention, offset));
		return matchedTAs;
	}

	public List<Mention> searchText(String documentId, String text) {
		return matcher.match(text, documentId);
	}

	private Set<TextAnnotation> convertToTextAnnotation(Mention mention, int offset) {
		Set<TextAnnotation> taSet = new HashSet<TextAnnotation>();
		for (String idStr : mention.getIds())
			for (String id : idStr.split("\\|")) {
				ClassMention cm = new DefaultClassMention(id);
				TextAnnotation ta = new DefaultTextAnnotation(mention.getStart() + offset, mention.getEnd() + offset,
						mention.getText(), LINNAEUS_ANNOTATOR, new AnnotationSet(), -1, -1, mention.getDocid(), -1, cm);
				taSet.add(ta);
			}
		return taSet;
	}

	/**
	 * Returns a Linnaeus matcher based on the input properties file.
	 * 
	 * @param linnaeusPropertiesFile
	 * @param logger
	 * @param tag
	 * @return
	 */
	public static Matcher getMatcher(File linnaeusPropertiesFile, Logger logger) {
		ArgParser ap = new ArgParser(new String[] { "--properties", linnaeusPropertiesFile.getAbsolutePath() });
		return EntityTagger.getMatcher(ap, logger);
	}

	/**
	 * Creates a temporary file containing Linnaeus configuration properties
	 * 
	 * @param dictionaryFile
	 * @param stopWordListFile
	 * @param synonymsAcronymsFile
	 * @param frequencyFile
	 * @param supplementaryDictionaryFile
	 * @param extraPropertyLines
	 * @return a reference to the generated config file
	 * @throws IOException
	 */
	public static File createLinnaeusPropertiesFile(File dictionaryFile, File stopWordListFile,
			File synonymsAcronymsFile, File frequencyFile, File supplementaryDictionaryFile,
			List<String> extraPropertyLines) throws IOException {
		File propertiesFile = File.createTempFile(LINNAEUS_PROPERTIES_FILE_PREFIX, LINNAEUS_PROPERTIES_FILE_SUFFIX);
		List<String> lines = new ArrayList<String>();
		if (supplementaryDictionaryFile != null) {
			FileUtil.validateFile(dictionaryFile);
			FileUtil.validateFile(supplementaryDictionaryFile);
			lines.add(String.format("variantMatcher=%s;%s", dictionaryFile.getAbsolutePath(),
					supplementaryDictionaryFile.getAbsolutePath()));
		} else {
			FileUtil.validateFile(dictionaryFile);
			lines.add(String.format("variantMatcher=%s", dictionaryFile.getAbsolutePath()));
		}
		if (stopWordListFile != null) {
			FileUtil.validateFile(stopWordListFile);
			lines.add(String.format("ppStopTerms=%s", stopWordListFile.getAbsolutePath()));
		}
		if (synonymsAcronymsFile != null) {
			FileUtil.validateFile(synonymsAcronymsFile);
			lines.add(String.format("ppAcrProbs=%s", synonymsAcronymsFile.getAbsolutePath()));
		}
		if (frequencyFile != null) {
			FileUtil.validateFile(frequencyFile);
			lines.add(String.format("ppSpeciesFreqs=%s", frequencyFile.getAbsolutePath()));
		}
		lines.add("postProcessing");
		if (extraPropertyLines != null)
			lines.addAll(extraPropertyLines);
		FileWriterUtil.printLines(lines, propertiesFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		return propertiesFile;
	}

	public static File createVariantFile(Map<String, Set<String>> idToVariantsMap, CharacterEncoding encoding)
			throws IOException {
		File variantFile = File.createTempFile("linnaeus-variant", encoding.getFileSuffix());
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(variantFile, encoding);
		try {
			for (Entry<String, Set<String>> entry : idToVariantsMap.entrySet()) {
				writer.write(entry.getKey() + "\t"
						+ CollectionsUtil.createDelimitedString(entry.getValue(), StringConstants.VERTICAL_LINE));
				writer.newLine();
			}
		} finally {
			writer.close();
		}
		return variantFile;
	}
}
