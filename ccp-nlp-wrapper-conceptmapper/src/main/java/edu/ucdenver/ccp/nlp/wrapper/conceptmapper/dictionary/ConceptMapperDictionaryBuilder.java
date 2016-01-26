/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary;

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
