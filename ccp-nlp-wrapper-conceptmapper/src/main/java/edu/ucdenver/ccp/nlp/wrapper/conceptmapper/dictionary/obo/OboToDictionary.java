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

import org.apache.log4j.Logger;
import org.geneontology.oboedit.datamodel.Namespace;
import org.geneontology.oboedit.datamodel.OBOClass;
import org.geneontology.oboedit.datamodel.Synonym;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.xml.XmlUtil;
import edu.ucdenver.ccp.fileparsers.obo.OboClassIterator;
import edu.ucdenver.ccp.nlp.wrapper.oboedit.OboEdit_Util;

/**
 * A utility for building an XML-formatted dictionary of terms in an OBO ontology.
 * 
 * @author Karin Verspoor
 * 
 */
public class OboToDictionary {

	private static final Logger logger = Logger.getLogger(OboToDictionary.class);
	
	private OboEdit_Util oboEditUtil;

	public static String TOKEN_TAG = "token";
	public static String SYNONYM_TAG = "variant";

	private static boolean filterSingleLetterTerms = true;

	public OboToDictionary(String oboFile) {
		System.setProperty("file.encoding", "UTF-8");
		oboEditUtil = new OboEdit_Util(oboFile);
	}

	// /**
	// * Print each element of the ontology out to the XML format.
	// *
	// * @param outFile
	// * The file to which to print the XML dictionary.
	// * @throws IOException
	// */
	// public void XMLPrint(File outputFile) throws IOException {
	// // PrintStream os = GenericTextUtils.openPrintStream(outFile, "UTF-8");
	// BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile,
	// CharacterEncoding.UTF_8);
	// writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
	// writer.newLine();
	// Map<String, OBOClass> id2term = oboEditUtil.getID2TermMap();
	//
	// for (String id : id2term.keySet()) {
	// OBOClass oboObj = id2term.get(id);
	// if (oboObj != null) {
	// writer.write(objToString(id, oboObj));
	// }
	// }
	// writer.write("</synonym>");
	// writer.close();
	// }

	public static void buildDictionary(File outputFile, OboClassIterator oboClsIter, Set<String> namespacesToInclude)
			throws IOException {
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
		writer.newLine();

		while (oboClsIter.hasNext()) {
			OBOClass oboObj = oboClsIter.next().getOboClass();
			if (oboObj != null) {
				if (namespacesToInclude == null || namespacesToInclude.isEmpty()) {
					writer.write(objToString(oboObj.getID(), oboObj));
				} else {
					Namespace objNS = oboObj.getNamespace();
					if (objNS != null) {
						String ns = objNS.toString();
						if (namespacesToInclude.contains(ns)) {
							writer.write(objToString(oboObj.getID(), oboObj));
						}
					}
				}
			}
		}
		writer.write("</synonym>");
		writer.close();
	}

	/**
	 * Represent the OBO object as a XML dictionary string.
	 * 
	 * @param id
	 *            the ID of the OBO object
	 * @param oboObj
	 *            the OBO object itself
	 * @return an XML-formatted string in the ConceptMapper Dictionary format.
	 */
	private static String objToString(String id, OBOClass oboObj) {
		StringBuffer buf = new StringBuffer();

		String name = oboObj.getName();
		if (name == null || name == "" || name == "<new term>") {
			// id without a name. Don't add to dictionary.
			// System.err.println("Name for id " + id + " is not valid:" + name);
			return "";
		}

		if (filterSingleLetterTerms && name.length() <= 1)
			return "";

		name = XmlUtil.convertXmlEscapeCharacters(name);
		// name = fixString(name);

		buf.append("<" + TOKEN_TAG + " id=\"" + id + "\"" + " canonical=\"" + name + "\"" + ">\n");
		buf.append(buildSynonymLine(name));
		// buf.append("\t<"+SYNONYM_TAG +" base=\"" + name + "\"" + "/>\n");
		Set<Synonym> syns = oboObj.getSynonyms();
		for (Synonym syn : syns) {
			String variantStr = XmlUtil.convertXmlEscapeCharacters(syn.getText());
			buf.append(buildSynonymLine(variantStr));
			// buf.append("\t<"+SYNONYM_TAG +" base=\"" + syn.getText() + "\"" + "/>\n");
		}
		buf.append("</" + TOKEN_TAG + ">\n");
		return buf.toString();
	}

	private static String buildSynonymLine(String name) {
		if (filterSingleLetterTerms && name.length() <= 1)
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

	// private static String fixString(String name) {
	// String origName = name;
	// // if ( name.matches(".*[<>&\"'].*" ) ) {
	// if (name.matches(".*[<>&\"].*")) {
	// name = name.replaceAll(" & ", " &amp; ");
	// name = name.replaceAll(">", "&gt;");
	// name = name.replaceAll("<", "&lt;");
	// name = name.replaceAll("\"", "&quot;");
	// // name = name.replaceAll("'", "&apos;");
	// System.err.println("Name changed from: " + origName + " to " + name);
	// // return "";
	// }
	//
	// return name;
	// }

	// /**
	// * Print the elements of the ontology out to the XML format, if they are in the specified
	// * namespace.
	// *
	// * @param outFile
	// * The file to which to print the XML dictionary.
	// * @param namespace
	// * The required namespace.
	// * @throws IOException
	// */
	// public void XMLPrint(File outputFile, String namespace) throws IOException {
	// BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile,
	// CharacterEncoding.UTF_8);
	// writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<synonym>");
	// writer.newLine();
	// Map<String, OBOClass> id2term = oboEditUtil.getID2TermMap();
	//
	// for (String id : id2term.keySet()) {
	// OBOClass oboObj = id2term.get(id);
	// if (oboObj != null) {
	// Namespace objNS = oboObj.getNamespace();
	// if (objNS != null) {
	// String ns = objNS.toString();
	// if (ns.equals(namespace)) {
	// writer.write(objToString(id, oboObj));
	// }
	// }
	// }
	// }
	// writer.write("</synonym>");
	// writer.close();
	// }

	// /**
	// * Run an application that loads in an OBO ontology and outputs an XML-formatted dictionary.
	// *
	// * @param args
	// * args[0] = path to the OBO file args[1] = path to the output file args[2] =
	// * (OPTIONAL) namespace filter
	// */
	// public static void main(String[] args) {
	// if (args.length < 2) {
	// System.err
	// .println("USAGE: java -cp bounce.jar:jhall.jar:oboedit.jar:org.geneontology.jar edu.uchsc.ccp.example.obo.OboToDictionary <OBO FILE> <OUTPUT FILE> <OPTIONAL NAMESPACE FILTER>");
	// } else {
	// try {
	// System.out.println("Load OBO file");
	// OboToDictionary oboToDict = new OboToDictionary(args[0]);
	//
	// if (args.length == 3) {
	// System.out.println("Print dictionary to " + args[1] + ", namespace " + args[2]);
	// oboToDict.XMLPrint(new File(args[1]), args[2]);
	// } else {
	// System.out.println("Print dictionary to " + args[1]);
	// oboToDict.XMLPrint(new File(args[1]));
	// }
	// System.out.println("OboToDictionary mapping complete.");
	// } catch (IOException e) {
	// System.err.println(e.getMessage());
	// }
	// }
	// }
}
