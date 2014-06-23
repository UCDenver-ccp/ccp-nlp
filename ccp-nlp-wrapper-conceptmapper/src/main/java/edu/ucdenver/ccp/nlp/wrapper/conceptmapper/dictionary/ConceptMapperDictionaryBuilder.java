/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.xml.XmlUtil;

/**
 * This class facilitates dictionary building for the ConceptMapper application
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ConceptMapperDictionaryBuilder {

	private final BufferedWriter writer;
	private final int minimumTermLength;

	/**
	 * Initializes a ConceptMapper dictionary writer by opening the output file and writing the XML
	 * header
	 * 
	 * @param dictionaryFile
	 * @param minimumTermLength
	 * @throws IOException
	 */
	public ConceptMapperDictionaryBuilder(File dictionaryFile, int minimumTermLength) throws IOException {
		this.minimumTermLength = minimumTermLength;
		writer = FileWriterUtil.initBufferedWriter(dictionaryFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
		writer.newLine();
	}

	/**
	 * @param termIdentifier
	 * @param synonyms
	 *            - first term in the list is treated as the canonical name
	 * @throws IOException
	 */
	public void addEntry(String termIdentifier, List<String> synonyms) throws IOException {
		List<String> filteredSynonyms = filterTerms(synonyms);
		if (filteredSynonyms.size() == 0)
			return;

		writer.write("<token id=\"" + termIdentifier + "\" canonical=\"" + transformTerm(filteredSynonyms.get(0)) + "\">");
		writer.newLine();
		for (String syn : filteredSynonyms) {
			writer.write("<variant base=\"" + transformTerm(syn) + "\"/>");
			writer.newLine();
		}
		writer.write("</token>");
		writer.newLine();
	}

	/**
	 * Escapes XML-specific characters and replaces underscores with spaces
	 * 
	 * @param term
	 * @return
	 */
	private String transformTerm(String term) {
		return XmlUtil.convertXmlEscapeCharacters(term.replace('_', ' '));
	}

	/**
	 * Filters out terms that are < minimumTermLength characters in length
	 * 
	 * @param synonyms
	 * @return
	 */
	private List<String> filterTerms(List<String> terms) {
		List<String> filteredTerms = new ArrayList<String>();
		for (String term : terms) {
			if (term.trim().length() >= minimumTermLength) {
				filteredTerms.add(term.trim());
			}
		}
		return filteredTerms;
	}

	/**
	 * Closes the output writer
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.write("</synonym>");
		writer.close();
	}

}
