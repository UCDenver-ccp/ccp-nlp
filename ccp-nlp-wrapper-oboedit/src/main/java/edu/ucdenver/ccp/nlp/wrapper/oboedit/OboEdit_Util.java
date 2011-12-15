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

package edu.ucdenver.ccp.nlp.wrapper.oboedit;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.geneontology.oboedit.dataadapter.DefaultOBOParser;
import org.geneontology.oboedit.dataadapter.OBOParseEngine;
import org.geneontology.oboedit.dataadapter.OBOParseException;
import org.geneontology.oboedit.datamodel.LinkedObject;
import org.geneontology.oboedit.datamodel.OBOClass;
import org.geneontology.oboedit.datamodel.OBOProperty;
import org.geneontology.oboedit.datamodel.OBOSession;
import org.geneontology.oboedit.datamodel.impl.OBORestrictionImpl;
import org.geneontology.oboedit.datamodel.impl.SynonymImpl;

/**
 * A utility program for interfacing with the OBO-Edit API.
 * 
 * @author William A Baumgartner Jr.
 * 
 */
public class OboEdit_Util {

	private OBOSession session;

	/**
	 * Constructor
	 * 
	 * @param oboFilePath
	 *            the location of the obo file to parse
	 */
	public OboEdit_Util(String oboFilePath) {
		/* Initialize a new OBOSession object for the input obo file */
		session = getSession(oboFilePath);
	}

	/**
	 * This method taken straight from the OBO-Edit FAQ webpage:
	 * http://wiki.geneontology.org/index.php/OBO-Edit:_OBO_Parser_-_Getting_Started
	 * 
	 * @param path
	 * @return an OBOSession object containing the contents of the input obo file
	 */
	public OBOSession getSession(String path) {
		DefaultOBOParser parser = new DefaultOBOParser();
		OBOParseEngine engine = new OBOParseEngine(parser);
		// OBOParseEngine can parse several files at once
		// and create one munged-together ontology,
		// so we need to provide a Collection to the setPaths() method
		Collection<String> paths = new LinkedList<String>();
		paths.add(path);
		engine.setPaths(paths);
		try {
			engine.parse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OBOParseException e) {
			e.printStackTrace();
		}
		OBOSession session = parser.getSession();
		return session;
	}

	/**
	 * Determine how many terms do and do not have definitions <br>
	 * checked the results by grepping the file.. similar, but the OBO-Edit parser ignores obsolete nodes
	 */
	public void computeDefinitionDistribution() {
		Map<String, OBOClass> id2termMap = getID2TermMap();
		Collection<OBOClass> terms = id2termMap.values();

		Set<String> definitions = new HashSet<String>();

		int hasDefinitionCount = 0;
		int hasNoDefinitionCount = 0;
		for (OBOClass term : terms) {
			String definition = term.getDefinition();
			if (definition != null & definition.trim().length() > 0) {
				hasDefinitionCount++;
				if (definitions.contains(definition)) {
					System.out.println("Duplicate definition discovered: " + term.getID());
				} else {
					definitions.add(definition);
				}
			} else {
				hasNoDefinitionCount++;
			}
		}

		System.out.println("# terms: " + terms.size());
		System.out.println("Terms with definitions: " + hasDefinitionCount);
		System.out.println("Terms missing definitions: " + hasNoDefinitionCount);

		// Set<String> ids = id2termMap.keySet();
		// List<String> idList = new ArrayList<String>(ids);
		// Collections.sort(idList);
		// for (String id : idList) {
		// System.out.println(id);
		// }
	}

	/**
	 * Looks at the term names and synonyms and detects ambiguous term names and/or synonyms
	 * 
	 */
	public void checkForAmbiguousSynonyms() {

		Map<String, OBOClass> id2termMap = getID2TermMap();
		Collection<OBOClass> terms = id2termMap.values();

		Map<String, Set<String>> termName2idMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> synonymName2idMap = new HashMap<String, Set<String>>();

		for (OBOClass term : terms) {

			String termName = term.getName().toLowerCase();
			String id = term.getID();

			/* check to see if the term name matches any synonyms found previously */
			if (synonymName2idMap.containsKey(termName)) {
				System.out.println("Ambiguous term-synonym pairing detected: " + termName + " id: " + id + " -- "
						+ synonymName2idMap.get(termName));
			}

			/* check to see if the term name has been used before */
			if (termName2idMap.containsKey(termName)) {
				// System.out.println("Ambiguous term name detected: " + termName);
				termName2idMap.get(termName).add(id);
			} else {
				Set<String> idSet = new HashSet<String>();
				idSet.add(id);
				termName2idMap.put(termName, idSet);
			}

			Set synonyms = term.getSynonyms();
			for (Object synonym : synonyms) {
				if (synonym instanceof SynonymImpl) {
					SynonymImpl synonymImpl = (SynonymImpl) synonym;

					String synonymName = synonymImpl.getText().toLowerCase();
					int synonymType = synonymImpl.getScope();

					if (synonymType == 1) {
						/* check to see if the synonym name is ambiguous with a previous term name */
						if (termName2idMap.containsKey(synonymName)) {
							System.out.println("Ambiguous synonym-term pairing detected: " + synonymName + " id: " + id + " -- "
									+ termName2idMap.get(synonymName));
						}

						/* check to see if the synonym name is ambiguous with other synonym names */
						if (synonymName2idMap.containsKey(synonymName)) {
							// System.out.println("Ambiguous synonym name detected: " + synonymName);
							synonymName2idMap.get(synonymName).add(id);
						} else {
							Set<String> idSet = new HashSet<String>();
							idSet.add(id);
							synonymName2idMap.put(synonymName, idSet);
						}
					}
				}
			}
		}

		/* go through the term and synonym hash and look for terms and synonyms with multiple ids */
		for (String term : termName2idMap.keySet()) {
			if (termName2idMap.get(term).size() > 1) {
				System.out.println("Ambiguous term names: " + termName2idMap.get(term));
			}
		}

		for (String syn : synonymName2idMap.keySet()) {
			if (synonymName2idMap.get(syn).size() > 1) {
				System.out.println("Ambiguous syn names: " + synonymName2idMap.get(syn));
			}
		}

	}

	/**
	 * Prints to standard output the root classes of the ontology that is currently loaded
	 * 
	 */
	public void printRootClasses() {
		/* Grab the root terms */
		Set roots = session.getRoots();

		/* For each root term, print it to standard output */
		for (Object root : roots) {
			System.out.print("Root Class: ");
			if (root instanceof OBOClass) {
				OBOClass rootClass = (OBOClass) root;
				printOboClass(rootClass, false, 0);
			} else {
				warn("Unexpected class found in root set: " + root.getClass().getName());
			}
			System.out.println("-----------------------------------");
		}

	}

	/**
	 * Returns a mapping from Term ID to Term
	 * 
	 * @return a Map where the keys are Term IDs and the values are the terms themselves
	 */
	public Map<String, OBOClass> getID2TermMap() {
		/* All we are doing here is adding generics to the Map for convenience of use */
		Map<String, OBOClass> id2termMap = session.getAllTermsHash();
		return id2termMap;
	}

	/**
	 * Prints some information about a given OBOClass object to standard output.
	 * 
	 * @param oboClass
	 *            the OBOClass to print
	 * @param recurse
	 *            if true, all children of this OBOClass will also be printed
	 * @param indent
	 *            controls the indenting of each successive child level, 0=no indent. For each level after 0, spacing is
	 *            incremented by 2.
	 */
	public static void printOboClass(OBOClass oboClass, boolean recurse, int indent) {
		/* Initialize the indent variable "space" */
		String space = "";
		for (int i = 0; i < indent; i++) {
			space += "_";
		}

		/* Print the class name, id, isRoot, isObsolete, and definition */
		System.out.println(space + oboClass.getName() + "[" + oboClass.getID() + "]  isRoot:" + oboClass.isRoot() + "  isObsolete:"
				+ oboClass.isObsolete());
		System.out.println(space + "Definition: " + oboClass.getDefinition());

		/* Print the synonym(s) */
		Set synonyms = oboClass.getSynonyms();
		for (Object synonym : synonyms) {
			if (synonym instanceof SynonymImpl) {
				SynonymImpl synonymImpl = (SynonymImpl) synonym;
				System.out.println(space + "Synonym: " + synonymImpl.getText() + " type:" + synonymImpl.getScope());
			}
		}

		/* Print the parent(s) */
		Set parents = oboClass.getParents();
		for (Object parent : parents) {
			if (parent instanceof OBORestrictionImpl) {
				OBORestrictionImpl parentImpl = (OBORestrictionImpl) parent;
				LinkedObject linkedObject = parentImpl.getParent();
				OBOProperty property = parentImpl.getType();
				// System.out.println("Property name: " + property.getName());
				if (linkedObject instanceof OBOClass) {
					OBOClass parentClass = (OBOClass) linkedObject;
					System.out.println(space + property.getName() + " --> " + parentClass.getName() + " [" + parentClass.getID() + "]");
				}
			}
		}

		/* If the flag to recurse is set to true, then process each child of the input OBOClass */
		if (recurse) {
			Set childrenOfRoot = oboClass.getChildren();
			for (Object child : childrenOfRoot) {
				if (child instanceof OBORestrictionImpl) {
					OBORestrictionImpl childImpl = (OBORestrictionImpl) child;
					LinkedObject linkedObject = childImpl.getChild();
					if (linkedObject instanceof OBOClass) {
						OBOClass childClass = (OBOClass) linkedObject;
						printOboClass(childClass, recurse, indent += 2);
					}
				} else {
					warn("Unexpected class found in children set: " + child.getClass().getName());
				}
			}
		}
	}
	
	/**
	 * @param id
	 * @return the OBOClass for id
	 */
	public OBOClass getClassForID(String id) {
		return getID2TermMap().get(id);
	}

	/**
	 * Prints a warning message to standard error
	 * 
	 * @param message
	 */
	private static void warn(String message) {
		System.err.println("WARNING -- OboEdit_Util: " + message);
	}

	/**
	 * Run an example application that loads in an ontology (must be the cell ontology for part of this example to work)
	 * and exercises the OboEdit_Util class.
	 * 
	 * @param args
	 *            args[0] = path to the cell.obo file
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("USAGE: java -cp bounce.jar:jhall.jar:oboedit.jar:org.geneontology.jar edu.uchsc.ccp.example.obo.OboEdit_Util cell.obo");
		} else {
			OboEdit_Util oboEditUtil = new OboEdit_Util(args[0]);

			oboEditUtil.computeDefinitionDistribution();
			oboEditUtil.checkForAmbiguousSynonyms();

			// System.out.println("=====================");
			// System.out.println("PRINTING ROOT CLASSES");
			// System.out.println("=====================");
			// oboEditUtil.printRootClasses();
			//		
			// Map<String, OBOClass> id2termMap = oboEditUtil.getID2TermMap();
			//
			// System.out.println("==========================");
			// System.out.println("PRINTING MUSCLE CELL CLASS");
			// System.out.println("==========================");
			// /* Get the T cell term [CL:0000084] */
			// OBOClass muscleClass;
			// String id = "CL:0000187";
			// if (id2termMap.containsKey(id)) {
			// muscleClass = id2termMap.get(id);
			//
			// OboEdit_Util.printOboClass(muscleClass, true, 0);
			// } else {
			// System.err.println("Class not found for ID: " + id);
			// }
		}
	}
}
