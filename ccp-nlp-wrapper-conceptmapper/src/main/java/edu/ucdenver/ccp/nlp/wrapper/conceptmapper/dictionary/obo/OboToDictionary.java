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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.util.StringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.xml.XmlUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;

/**
 * A utility for building an XML-formatted dictionary of terms in an OBO
 * ontology.
 * 
 * @author Karin Verspoor
 * 
 */
public class OboToDictionary {

	/**
	 * 
	 */
	private static final int MINIMUM_TERM_LENGTH = 3;

	public static String TOKEN_TAG = "token";
	public static String SYNONYM_TAG = "variant";

	private static boolean filterTermsByLength = true;

	public OboToDictionary(String oboFile) {
		System.setProperty("file.encoding", "UTF-8");
	}

	public static void buildDictionary(File outputFile, OntologyUtil ontUtil, Set<String> namespacesToInclude,
			SynonymType synonymType) throws IOException {
		buildDictionary(outputFile, ontUtil, namespacesToInclude, synonymType, null, null);
	}

	/**
	 * @param outputFile
	 * @param ontClsIter
	 * @param namespacesToInclude
	 * @param synonymType
	 *            ALL to include all synonyms,EXACT_ONLY to include only exact
	 *            synonyms
	 * @throws IOException
	 */
	public static void buildDictionary(File outputFile, OntologyUtil ontUtil, Set<String> namespacesToInclude,
			SynonymType synonymType, Set<OWLClass> subTreeRootIdsToExclude, Set<OWLClass> subTreeRootIdsToInclude)
			throws IOException {
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, CharacterEncoding.UTF_8,
				WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
		writer.newLine();
		for (Iterator<OWLClass> ontClsIter = ontUtil.getClassIterator(); ontClsIter.hasNext();) {
			OWLClass owlClass = ontClsIter.next();
			if (owlClass != null // && !oboObj.getName().startsWith("obo:")
					&& classNotInExcludedSubtree(owlClass, subTreeRootIdsToExclude, ontUtil)
					&& classInIncludedSubtree(owlClass, subTreeRootIdsToInclude, ontUtil)) {
				if (namespacesToInclude == null || namespacesToInclude.isEmpty()) {
					writer.write(objToString(owlClass.toStringID(), owlClass, synonymType, ontUtil));
				} else {
					String ns = ontUtil.getNamespace(owlClass);
					if (ns != null) {
						if (namespacesToInclude.contains(ns)) {
							writer.write(objToString(owlClass.toStringID(), owlClass, synonymType, ontUtil));
						}
					}
				}
			}
		}
		writer.write("</synonym>");
		writer.close();
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
	private static String objToString(String id, OWLClass owlClass, SynonymType synonymType, OntologyUtil ontUtil) {
		StringBuffer buf = new StringBuffer();

		String name = ontUtil.getLabel(owlClass);
		if (name == null || name == "" || name == "<new term>") {
			// id without a name. Don't add to dictionary.
			return "";
		}
		/* often seen when parsing OWL string literals */
		if (name.endsWith("\"@en")) {
			name = StringUtils.removeSuffix(name, "\"@en");
		}
		System.out.println("label: " + name);

		if (filterTermsByLength && name.length() < MINIMUM_TERM_LENGTH)
			return "";

		name = XmlUtil.convertXmlEscapeCharacters(name);

		buf.append("<" + TOKEN_TAG + " id=\"" + id + "\"" + " canonical=\"" + name + "\"" + ">\n");

		{
			Pattern endsWithActivityPattern = Pattern.compile("(.*)\\sactivity");
			Matcher m = endsWithActivityPattern.matcher(name);
			if (m.matches()) {
				String enzyme = m.group(1);
				buf.append(buildSynonymLine(enzyme));
			}
		}

		buf.append(buildSynonymLine(name));
		Set<String> syns = ontUtil.getSynonyms(owlClass, synonymType);
		Pattern endsWithActivityPattern = Pattern.compile("(.*)\\sactivity");
		for (String syn : syns) {
			if (syn.endsWith("\"@en")) {
				syn = StringUtils.removeSuffix(syn, "\"@en");
			}
			if (!syn.equals(name)) {
				String variantStr = XmlUtil.convertXmlEscapeCharacters(syn);
				buf.append(buildSynonymLine(variantStr));

				Matcher m = endsWithActivityPattern.matcher(variantStr);
				if (m.matches()) {
					String enzyme = m.group(1);
					buf.append(buildSynonymLine(enzyme));
				}
			}
		}
		buf.append("</" + TOKEN_TAG + ">\n");
		return buf.toString();
	}

	private static String buildSynonymLine(String name) {
		if (filterTermsByLength && name.length() < MINIMUM_TERM_LENGTH)
			return "";

		StringBuffer buf = new StringBuffer();
		buf.append("\t<");
		buf.append(SYNONYM_TAG);
		buf.append(" base=\"");
		buf.append(name);
		buf.append("\"");
		buf.append("/>\n");

		// check for term_like_this
		String namevar = name.replace('_', ' ');
		if (!namevar.equals(name)) {
			buf.append(buildSynonymLine(namevar));
		}

		return buf.toString();
	}
}
