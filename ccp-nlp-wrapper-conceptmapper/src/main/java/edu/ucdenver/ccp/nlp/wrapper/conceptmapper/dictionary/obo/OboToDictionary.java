package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.obo;

import java.io.BufferedReader;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.StringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.xml.XmlUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;
import lombok.Data;

/**
 * A utility for building an XML-formatted dictionary of terms in an OBO
 * ontology.
 * 
 * @author Karin Verspoor
 * 
 */
public class OboToDictionary {

	private static final Logger logger = Logger.getLogger(OboToDictionary.class);

	private static final int MINIMUM_TERM_LENGTH = 3;

	public static String TOKEN_TAG = "token";
	public static String SYNONYM_TAG = "variant";

	private static boolean filterTermsByLength = true;

	public OboToDictionary(String oboFile) {
		System.setProperty("file.encoding", "UTF-8");
	}

	public static void buildDictionary(File outputFile, OntologyUtil ontUtil, Set<String> namespacesToInclude,
			SynonymType synonymType, DictionaryEntryModifier dictEntryModifier) throws IOException {
		buildDictionary(outputFile, ontUtil, namespacesToInclude, synonymType, null, null, null, dictEntryModifier);
	}

	public static void buildDictionary(File outputFile, OntologyUtil ontUtil, Set<String> namespacesToInclude,
			SynonymType synonymType, Map<String, Set<String>> id2externalSynonymMap,
			DictionaryEntryModifier dictEntryModifier) throws IOException {
		buildDictionary(outputFile, ontUtil, namespacesToInclude, synonymType, null, null, id2externalSynonymMap,
				dictEntryModifier);
	}

	/**
	 * @param outputFile
	 * @param ontClsIter
	 * @param namespacesToInclude
	 * @param synonymType
	 *            ALL to include all synonyms,EXACT_ONLY to include only exact
	 *            synonyms
	 * @param dictEntryModifier
	 * @throws IOException
	 */
	public static void buildDictionary(File outputFile, OntologyUtil ontUtil, Set<String> namespacesToInclude,
			SynonymType synonymType, Set<OWLClass> subTreeRootIdsToExclude, Set<OWLClass> subTreeRootIdsToInclude,
			Map<String, Set<String>> id2externalSynonymMap, DictionaryEntryModifier dictEntryModifier)
			throws IOException {
		long startTime = System.currentTimeMillis();
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath(outputFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
			writer.newLine();
			logger.info("Initialized dictionary writer: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
			int count = 0;
			startTime = System.currentTimeMillis();
			for (Iterator<OWLClass> ontClsIter = ontUtil.getClassIterator(); ontClsIter.hasNext();) {
				OWLClass owlClass = ontClsIter.next();
				if (owlClass != null // && !oboObj.getName().startsWith("obo:")
						&& classNotInExcludedSubtree(owlClass, subTreeRootIdsToExclude, ontUtil)
						&& classInIncludedSubtree(owlClass, subTreeRootIdsToInclude, ontUtil)) {
					if (count++ % 100 == 0) {
						logger.info("Ontology processing progress: " + (count - 1) + " in "
								+ ((System.currentTimeMillis() - startTime) / 1000) + "s");
						startTime = System.currentTimeMillis();
					}
					String objToString = objToString(owlClass, synonymType, ontUtil, id2externalSynonymMap,
							dictEntryModifier);
					if (objToString != null) {
						if (namespacesToInclude == null || namespacesToInclude.isEmpty()) {
							writer.write(objToString);
						} else {
							String ns = ontUtil.getNamespace(owlClass);
							if (ns != null) {
								if (namespacesToInclude.contains(ns)) {
									writer.write(objToString);
								}
							}
						}
					}
				}
			}
			writer.write("</synonym>");
		}
	}

	/**
	 * @param oboObj
	 * @param subTreeRootIdsToInclude
	 * @return
	 */
	private static boolean classInIncludedSubtree(OWLClass oboObj, Set<OWLClass> subTreeRootIdsToInclude,
			OntologyUtil ontUtil) {
		if (subTreeRootIdsToInclude == null) {
			return true;
		}
		Set<OWLClass> ancestors = ontUtil.getAncestors(oboObj);
		for (OWLClass ancestor : ancestors) {
			if (subTreeRootIdsToInclude.contains(ancestor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param owlClass
	 * @param subTreeRootIdsToExclude
	 * @return
	 */
	private static boolean classNotInExcludedSubtree(OWLClass owlClass, Set<OWLClass> subTreeRootIdsToExclude,
			OntologyUtil ontUtil) {
		if (subTreeRootIdsToExclude == null) {
			return true;
		}
		Set<OWLClass> ancestors = ontUtil.getAncestors(owlClass);
		for (OWLClass ancestor : ancestors) {
			if (subTreeRootIdsToExclude.contains(ancestor)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Represent the OBO object as a XML dictionary string.
	 * 
	 * @param id
	 *            the ID of the OBO object
	 * @param owlClass
	 *            the OBO object itself
	 * @param synonymType
	 * @return an XML-formatted string in the ConceptMapper Dictionary format.
	 */
	private static String objToString(OWLClass owlClass, SynonymType synonymType, OntologyUtil ontUtil,
			Map<String, Set<String>> id2externalSynonymMap, DictionaryEntryModifier dictionaryEntryModifier) {
		// StringBuffer buf = new StringBuffer();

		String name = ontUtil.getLabel(owlClass);
		if (name == null || name == "" || name == "<new term>") {
			// id without a name. Don't add to dictionary.
			return "";
		}
		/* often seen when parsing OWL string literals */
		if (name.endsWith("\"@en")) {
			name = StringUtils.removeSuffix(name, "\"@en");
		}

		if (filterTermsByLength && name.length() < MINIMUM_TERM_LENGTH) {
			return "";
		}

		// name = XmlUtil.convertXmlEscapeCharacters(name);
		// buf.append("<" + TOKEN_TAG + " id=\"" + owlClass.getIRI().toString()
		// + "\"" + " canonical=\"" + name + "\""
		// + ">\n");

		// Set<String> alreadyAddedSyns = new HashSet<String>();
		// buf.append(buildSynonymLine(name, alreadyAddedSyns));
		Set<String> syns = CollectionsUtil.createSet(name);
		syns.addAll(ontUtil.getSynonyms(owlClass, synonymType));
		syns.forEach(syn -> {
			if (syn.endsWith("\"@en")) {
				syn = StringUtils.removeSuffix(syn, "\"@en");
			}
		});

		for (String syn : new HashSet<String>(syns)) {
			syns.add(syn.replace('_', ' '));
		}

		/* check for external synonyms here and add any if they exist */
		Set<String> dynamicallyGeneratedSyns = null;
		String idToLookUp = owlClass.getIRI().getShortForm().replace("_", ":");
		if (id2externalSynonymMap != null && id2externalSynonymMap.containsKey(idToLookUp)) {
			dynamicallyGeneratedSyns = id2externalSynonymMap.get(idToLookUp);
			dynamicallyGeneratedSyns.forEach(syn -> {
				if (syn.endsWith("\"@en")) {
					syn = StringUtils.removeSuffix(syn, "\"@en");
				}
			});

			for (String syn : new HashSet<String>(dynamicallyGeneratedSyns)) {
				dynamicallyGeneratedSyns.add(syn.replace('_', ' '));
			}
		}

		String identifier = owlClass.getIRI().toString();
		Concept c = new Concept(identifier, name, syns, dynamicallyGeneratedSyns);

		if (dictionaryEntryModifier != null) {
			c = dictionaryEntryModifier.modifyConcept(c);
		}
		/*
		 * to remove a concept entirely from the dictionary, the
		 * DictionaryEntryModifier can return null
		 */
		return (c == null) ? null : c.getConceptMapperDictionaryString();
	}

	private static String buildSynonymLine(String name, Set<String> alreadyAddedSyns) {

		if ((filterTermsByLength && name.length() < MINIMUM_TERM_LENGTH) || alreadyAddedSyns.contains(name)) {
			return "";
		}

		StringBuffer buf = new StringBuffer();
		buf.append("\t<");
		buf.append(SYNONYM_TAG);
		buf.append(" base=\"");
		buf.append(name);
		buf.append("\"");
		buf.append("/>\n");

		alreadyAddedSyns.add(name);

		// // check for term_like_this
		// String namevar = name.replace('_', ' ');
		// if (!namevar.equals(name) && !alreadyAddedSyns.contains(namevar)) {
		// buf.append(buildSynonymLine(namevar, alreadyAddedSyns));
		// }

		return buf.toString();
	}

	@Data
	public static class Concept {
		private final String identifier;
		private final String name;
		private final Set<String> officialSynonyms;
		private final Set<String> dynamicallyGeneratedSynonyms;

		public String getConceptMapperDictionaryString() {
			String conceptName = XmlUtil.convertXmlEscapeCharacters(name);

			StringBuilder builder = new StringBuilder();
			builder.append(
					"<" + TOKEN_TAG + " id=\"" + identifier + "\"" + " canonical=\"" + conceptName + "\"" + ">\n");

			Set<String> alreadyAddedSyns = new HashSet<String>();
			builder.append(buildSynonymLine(conceptName, alreadyAddedSyns));

			if (officialSynonyms != null) {
				officialSynonyms.forEach(syn -> builder
						.append(buildSynonymLine(XmlUtil.convertXmlEscapeCharacters(syn), alreadyAddedSyns)));
			}

			if (dynamicallyGeneratedSynonyms != null) {
				dynamicallyGeneratedSynonyms.forEach(syn -> builder
						.append(buildSynonymLine(XmlUtil.convertXmlEscapeCharacters(syn), alreadyAddedSyns)));
			}

			builder.append("</" + TOKEN_TAG + ">\n");
			return builder.toString();

		}
	}
}
