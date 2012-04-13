/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.obo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geneontology.oboedit.dataadapter.OBOParseException;

import edu.ucdenver.ccp.common.file.FileUtil.CleanDirectory;
import edu.ucdenver.ccp.fileparsers.geneontology.GeneOntologyClassIterator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GoDictionaryFactory {

	public enum GoNamespace {
		BP("biological_process"),
		MF("molecular_function"),
		CC("cellular_component");

		private final String namespace;

		private GoNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String namespace() {
			return namespace;
		}
	}

	/**
	 * Creates a dictionary for use by the ConceptMapper that includes GO terms from the namespaces
	 * defined in the namespacesToInclude set.
	 * 
	 * @param namespacesToInclude
	 * @param workDirectory
	 * @param cleanWorkDirectory
	 * @return
	 * @throws IOException
	 * @throws OBOParseException
	 */
	public static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File workDirectory,
			CleanDirectory cleanWorkDirectory) throws IOException, OBOParseException {
		if (namespacesToInclude.isEmpty())
			return null;

		boolean doClean = cleanWorkDirectory.equals(CleanDirectory.YES);
		GeneOntologyClassIterator goIter = new GeneOntologyClassIterator(workDirectory, doClean);

		return buildConceptMapperDictionary(namespacesToInclude, workDirectory, goIter);
	}

	public static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File goOboFile,
			File outputDirectory) throws IOException, OBOParseException {
		if (namespacesToInclude.isEmpty())
			return null;

		GeneOntologyClassIterator goIter = new GeneOntologyClassIterator(goOboFile);

		return buildConceptMapperDictionary(namespacesToInclude, outputDirectory, goIter);
	}

	/**
	 * @param namespacesToInclude
	 * @param outputDirectory
	 * @param goIter
	 * @return
	 * @throws IOException
	 */
	private static File buildConceptMapperDictionary(EnumSet<GoNamespace> namespacesToInclude, File outputDirectory,
			GeneOntologyClassIterator goIter) throws IOException {
		String dictionaryKey = "";
		List<String> nsKeys = new ArrayList<String>();
		for (GoNamespace ns : namespacesToInclude)
			nsKeys.add(ns.name());
		Collections.sort(nsKeys);
		for (String ns : nsKeys)
			dictionaryKey += ns;

		File dictionaryFile = new File(outputDirectory, "cmDict-GO-" + dictionaryKey + ".xml");
		Set<String> namespaces = new HashSet<String>();
		for (GoNamespace ns : namespacesToInclude)
			namespaces.add(ns.namespace());
		OboToDictionary.buildDictionary(dictionaryFile, goIter, new HashSet<String>(namespaces));
		return dictionaryFile;
	}

//	public static void main(String[] args) {
//		BasicConfigurator.configure();
//		File workDirectory = new File("test-output");
//		try {
//			GoDictionaryFactory.buildConceptMapperDictionary(EnumSet.of(GoNamespace.CC), workDirectory, false);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OBOParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
