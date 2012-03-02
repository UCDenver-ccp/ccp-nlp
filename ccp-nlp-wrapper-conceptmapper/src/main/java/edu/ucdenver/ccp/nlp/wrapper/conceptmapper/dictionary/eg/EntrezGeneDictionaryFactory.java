/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.eg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.fileparsers.field.GeneNameOrSymbol;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileData;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileParser;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.ConceptMapperDictionaryBuilder;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class EntrezGeneDictionaryFactory {

	private static final Set<String> SYNONYMS_TO_IGNORE = CollectionsUtil.createSet("NEWENTRY", "hypothetical protein",
			"putative");
	private static final Logger logger = Logger.getLogger(EntrezGeneDictionaryFactory.class);

	private static final int MINIMUM_TERM_LENGTH = 1;

	public static File buildConceptMapperDictionary(File workDirectory, boolean cleanWorkDirectory) throws IOException {

		File dictionaryFile = new File(workDirectory, "cmDict-EntrezGene.xml");
		ConceptMapperDictionaryBuilder dictBuilder = new ConceptMapperDictionaryBuilder(dictionaryFile,
				MINIMUM_TERM_LENGTH);
		int count = 0;
		for (EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(workDirectory, cleanWorkDirectory); parser
				.hasNext();) {
			if (count++ % 100000 == 0)
				logger.info("Progress: " + (count - 1));

			EntrezGeneInfoFileData dataRecord = parser.next();

			List<String> synonymStrs = new ArrayList<String>();

			GeneNameOrSymbol nomSymbol = dataRecord.getSymbolFromNomenclatureAuthority();
			if (nomSymbol != null)
				synonymStrs.add(nomSymbol.getDataElement());
			GeneNameOrSymbol nomName = dataRecord.getFullNameFromNomenclatureAuthority();
			if (nomName != null)
				synonymStrs.add(nomName.getDataElement());
			GeneNameOrSymbol symbol = dataRecord.getSymbol();
			if (symbol != null)
				synonymStrs.add(symbol.getDataElement());
			Set<GeneNameOrSymbol> synonyms = dataRecord.getSynonyms();
			if (synonyms != null)
				for (GeneNameOrSymbol syn : synonyms)
					synonymStrs.add(syn.getDataElement());
			Set<GeneNameOrSymbol> otherDesignations = dataRecord.getOtherDesignations();
			if (otherDesignations != null)
				for (GeneNameOrSymbol desig : otherDesignations)
					synonymStrs.add(desig.getDataElement());

			synonymStrs = filterSynonyms(synonymStrs);

			dictBuilder.addEntry(dataRecord.getGeneID().getDataElement().toString(), synonymStrs);
		}

		dictBuilder.close();
		return dictionaryFile;
	}

	/**
	 * @param synonymStrs
	 * @return
	 */
	private static List<String> filterSynonyms(List<String> synonymStrs) {
		List<String> filteredSyns = new ArrayList<String>();
		for (String syn : synonymStrs) {
			boolean ignore = false;
			for (String ignoreStr : SYNONYMS_TO_IGNORE) {
				if (syn.contains(ignoreStr)) {
					ignore = true;
					break;
				}
			}
			if (!ignore)
				filteredSyns.add(syn);
		}
		return filteredSyns;
	}

	/**
	 * @param args
	 *            args[0] = work directory<br>
	 *            args[1] = clean work directory (true/false)
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		File workDirectory = new File(args[0]);
		boolean cleanWorkDirectory = Boolean.valueOf(args[1]);

		try {
			EntrezGeneDictionaryFactory.buildConceptMapperDictionary(workDirectory, cleanWorkDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
