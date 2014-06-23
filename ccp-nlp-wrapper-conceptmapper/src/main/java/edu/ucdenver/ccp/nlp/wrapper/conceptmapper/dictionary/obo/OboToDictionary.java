/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.obo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.geneontology.oboedit.datamodel.Namespace;
import org.geneontology.oboedit.datamodel.OBOClass;
import org.geneontology.oboedit.datamodel.Synonym;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.xml.XmlUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OboClassIterator;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OboUtil;

/**
 * A utility for building an XML-formatted dictionary of terms in an OBO ontology.
 * 
 * @author Karin Verspoor
 * 
 */
public class OboToDictionary {

	/**
	 * 
	 */
	private static final int MINIMUM_TERM_LENGTH = 3;

	public enum SynonymType {
		EXACT_ONLY,
		ALL
	}

	private static final Logger logger = Logger.getLogger(OboToDictionary.class);

	public static String TOKEN_TAG = "token";
	public static String SYNONYM_TAG = "variant";

	private static boolean filterTermsByLength = true;

	public OboToDictionary(String oboFile) {
		System.setProperty("file.encoding", "UTF-8");
	}

	public static void buildDictionary(File outputFile, OboClassIterator oboClsIter, Set<String> namespacesToInclude,
			SynonymType synonymType) throws IOException {
		buildDictionary(outputFile, oboClsIter, namespacesToInclude, synonymType, null, null);
	}

	/**
	 * @param outputFile
	 * @param oboClsIter
	 * @param namespacesToInclude
	 * @param synonymType
	 *            ALL to include all synonyms,EXACT_ONLY to include only exact synonyms
	 * @throws IOException
	 */
	public static void buildDictionary(File outputFile, OboClassIterator oboClsIter, Set<String> namespacesToInclude,
			SynonymType synonymType, Set<String> subTreeRootIdsToExclude, Set<String> subTreeRootIdsToInclude)
			throws IOException {
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, CharacterEncoding.UTF_8,
				WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
		writer.newLine();

		while (oboClsIter.hasNext()) {
			OBOClass oboObj = oboClsIter.next().getOboClass();
			if (oboObj != null && !oboObj.getName().startsWith("obo:")
					&& classNotInExcludedSubtree(oboObj, subTreeRootIdsToExclude)
					&& classInIncludedSubtree(oboObj, subTreeRootIdsToInclude)) {
				if (namespacesToInclude == null || namespacesToInclude.isEmpty()) {
					writer.write(objToString(oboObj.getID(), oboObj, synonymType));
				} else {
					Namespace objNS = oboObj.getNamespace();
					if (objNS != null) {
						String ns = objNS.toString();
						if (namespacesToInclude.contains(ns)) {
							writer.write(objToString(oboObj.getID(), oboObj, synonymType));
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
	private static boolean classInIncludedSubtree(OBOClass oboObj, Set<String> subTreeRootIdsToInclude) {
		if (subTreeRootIdsToInclude == null) {
			return true;
		}
		Set<OBOClass> ancestors = OboUtil.getAncestors(oboObj);
		for (OBOClass ancestor : ancestors) {
			if (subTreeRootIdsToInclude.contains(ancestor.getID())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param oboObj
	 * @param subTreeRootIdsToExclude
	 * @return
	 */
	private static boolean classNotInExcludedSubtree(OBOClass oboObj, Set<String> subTreeRootIdsToExclude) {
		if (subTreeRootIdsToExclude == null) {
			return true;
		}
		Set<OBOClass> ancestors = OboUtil.getAncestors(oboObj);
		for (OBOClass ancestor : ancestors) {
			if (subTreeRootIdsToExclude.contains(ancestor.getID())) {
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
	 * @param oboObj
	 *            the OBO object itself
	 * @param synonymType
	 * @return an XML-formatted string in the ConceptMapper Dictionary format.
	 */
	private static String objToString(String id, OBOClass oboObj, SynonymType synonymType) {
		StringBuffer buf = new StringBuffer();

		String name = oboObj.getName();
		if (name == null || name == "" || name == "<new term>") {
			// id without a name. Don't add to dictionary.
			return "";
		}

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
		Set<Synonym> syns = oboObj.getSynonyms();
		Pattern endsWithActivityPattern = Pattern.compile("(.*)\\sactivity");
		for (Synonym syn : syns) {
			int scope = syn.getScope();
			if (synonymType.equals(SynonymType.ALL) || (synonymType.equals(SynonymType.EXACT_ONLY) && scope == 1)) {
				String variantStr = XmlUtil.convertXmlEscapeCharacters(syn.getText());
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
