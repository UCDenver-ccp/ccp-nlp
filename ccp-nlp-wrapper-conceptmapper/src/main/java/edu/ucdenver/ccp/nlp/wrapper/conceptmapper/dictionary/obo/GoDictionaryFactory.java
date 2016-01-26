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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileUtil.CleanDirectory;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;
import edu.ucdenver.ccp.datasource.fileparsers.obo.impl.GeneOntologyClassIterator;

/**
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class GoDictionaryFactory {

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
			CleanDirectory cleanWorkDirectory, SynonymType synonymType) throws IOException, IllegalArgumentException,
			IllegalAccessException, OWLOntologyCreationException {
		if (namespacesToInclude.isEmpty())
			return null;

		boolean doClean = cleanWorkDirectory.equals(CleanDirectory.YES);
		GeneOntologyClassIterator goIter = new GeneOntologyClassIterator(workDirectory, doClean);

		File geneOntologyOboFile = goIter.getGeneOntologyOboFile();
		OntologyUtil ontUtil = new OntologyUtil(geneOntologyOboFile);
		goIter.close();

		return buildConceptMapperDictionary(namespacesToInclude, workDirectory, ontUtil, doClean, synonymType);
	}

	public static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File goOboFile,
			File outputDirectory, boolean cleanDictFile, SynonymType synonymType) throws IOException,
			OWLOntologyCreationException {
		if (namespacesToInclude.isEmpty())
			return null;

		OntologyUtil ontUtil = new OntologyUtil(goOboFile);
		return buildConceptMapperDictionary(namespacesToInclude, outputDirectory, ontUtil, cleanDictFile, synonymType);
	}

	/**
	 * @param namespacesToInclude
	 * @param outputDirectory
	 * @param ontUtil
	 * @param synonymType
	 * @return
	 * @throws IOException
	 */
	private static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File outputDirectory,
			OntologyUtil ontUtil, boolean cleanDictFile, SynonymType synonymType) throws IOException {
		String dictionaryKey = "";
		List<String> nsKeys = new ArrayList<String>();
		for (GoNamespace ns : namespacesToInclude) {
			nsKeys.add(ns.name());
		}
		Collections.sort(nsKeys);
		for (String ns : nsKeys) {
			dictionaryKey += ns;
		}

		File dictionaryFile = new File(outputDirectory, "cmDict-GO-" + dictionaryKey + ".xml");
		if (dictionaryFile.exists()) {
			if (cleanDictFile) {
				FileUtil.deleteFile(dictionaryFile);
			} else {
				return dictionaryFile;
			}
		}
		Set<String> namespaces = new HashSet<String>();
		for (GoNamespace ns : namespacesToInclude)
			namespaces.add(ns.namespace());
		OboToDictionary.buildDictionary(dictionaryFile, ontUtil, new HashSet<String>(namespaces), synonymType);
		return dictionaryFile;
	}

}
