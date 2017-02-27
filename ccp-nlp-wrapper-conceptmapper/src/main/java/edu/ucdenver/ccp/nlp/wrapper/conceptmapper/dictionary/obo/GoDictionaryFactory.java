/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.obo;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileUtil.CleanDirectory;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;
import edu.ucdenver.ccp.datasource.fileparsers.obo.impl.GeneOntologyClassIterator;

/**
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class GoDictionaryFactory {

	private static final Logger logger = Logger.getLogger(GoDictionaryFactory.class);

	public enum GoNamespace {
		BP("biological_process"), MF("molecular_function"), CC("cellular_component");

		private final String namespace;

		private GoNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String namespace() {
			return namespace;
		}
	}

	public enum IncludeFunkSynonyms {
		YES, NO
	}

	/**
	 * Creates a dictionary for use by the ConceptMapper that includes GO terms
	 * from the namespaces defined in the namespacesToInclude set.
	 * 
	 * @param namespacesToInclude
	 * @param workDirectory
	 * @param cleanWorkDirectory
	 * @param synonymType
	 * @return
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws OBOParseException
	 */
	public static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File workDirectory,
			CleanDirectory cleanWorkDirectory, SynonymType synonymType, IncludeFunkSynonyms includeFunkSyns)
			throws IOException, IllegalArgumentException, IllegalAccessException, OWLOntologyCreationException {
		if (namespacesToInclude.isEmpty()) {
			return null;
		}

		boolean doClean = cleanWorkDirectory.equals(CleanDirectory.YES);
		GeneOntologyClassIterator goIter = new GeneOntologyClassIterator(workDirectory, doClean);

		File geneOntologyOboFile = goIter.getGeneOntologyOboFile();
		goIter.close();

		return buildConceptMapperDictionary(namespacesToInclude, workDirectory, geneOntologyOboFile, doClean,
				synonymType, includeFunkSyns);
	}

	/**
	 * @param namespacesToInclude
	 * @param outputDirectory
	 * @param ontUtil
	 * @param synonymType
	 * @return
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 */
	public static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File outputDirectory,
			File ontFile, boolean cleanDictFile, SynonymType synonymType, IncludeFunkSynonyms includeFunkSyns)
			throws IOException, OWLOntologyCreationException {

		Map<String, Set<String>> externalGoId2SynMap = new HashMap<String, Set<String>>();
		if (includeFunkSyns == IncludeFunkSynonyms.YES) {
			externalGoId2SynMap = loadFunkSynonymMap();
		}

		String dictionaryKey = "";
		List<String> nsKeys = new ArrayList<String>();
		for (GoNamespace ns : namespacesToInclude) {
			nsKeys.add(ns.name());
		}
		Collections.sort(nsKeys);
		for (String ns : nsKeys) {
			dictionaryKey += ns;
		}

		File dictionaryFile = new File(outputDirectory, "cmDict-"
				+ ((includeFunkSyns == IncludeFunkSynonyms.YES) ? "FUNK_" : "") + "GO_" + dictionaryKey + ".xml");
		if (dictionaryFile.exists()) {
			if (cleanDictFile) {
				FileUtil.deleteFile(dictionaryFile);
			} else {
				return dictionaryFile;
			}
		}
		Set<String> namespaces = new HashSet<String>();
		for (GoNamespace ns : namespacesToInclude) {
			namespaces.add(ns.namespace());
		}
		logger.info("Dictionary file does not yet exist. Generating dictionary: " + dictionaryFile);
		OntologyUtil ontUtil = new OntologyUtil(ontFile);
		OboToDictionary.buildDictionary(dictionaryFile, ontUtil, new HashSet<String>(namespaces), synonymType,
				externalGoId2SynMap);

		return dictionaryFile;
	}

	/**
	 * The synonyms generated in Funk et al 2016 are available in
	 * src/main/resources/funk2016
	 * 
	 * @return a mapping from GO ID (e.g. GO:0001234) to synonym sets parsed
	 *         from the supplementary data file associated with Funk et al 2016
	 *         (J Biomed Semantics. 2016; 7(1): 52).
	 * @throws IOException
	 */
	static Map<String, Set<String>> loadFunkSynonymMap() throws IOException {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		InputStream stream = new GZIPInputStream(ClassPathUtil.getResourceStreamFromClasspath(GoDictionaryFactory.class,
				"/funk2016/13326_2016_96_MOESM5_ESM.txt.gz"));
		int count = 0;
		for (StreamLineIterator lineIter = new StreamLineIterator(stream, CharacterEncoding.US_ASCII, null); lineIter
				.hasNext();) {
			if (count++ % 100000 == 0) {
				logger.info("Loading Funk Synonyms: " + (count - 1));
			}
			String[] toks = lineIter.next().getText().split("\\t+");
			String id = toks[0];
			String syn = toks[1];
			CollectionsUtil.addToOne2ManyUniqueMap(id, syn, map);
		}

		return map;
	}

}
