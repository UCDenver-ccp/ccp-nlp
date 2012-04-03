/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.eg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
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

	/**
	 * List of organisms taken from the NCBI Taxonomy home page:
	 * http://www.ncbi.nlm.nih.gov/Taxonomy/
	 * 
	 * @param workDirectory
	 * @param cleanWorkDirectory
	 * @return
	 * @throws IOException
	 */
	public static File buildModelOrganismConceptMapperDictionary(File workDirectory, boolean cleanWorkDirectory)
			throws IOException {
		/* @formatter:off */
		Set<NcbiTaxonomyID> taxonomyIdsToInclude = CollectionsUtil.createSet(
				new NcbiTaxonomyID(3702), // Arabidopsis thaliana
				new NcbiTaxonomyID(9913), // Bos taurus
				new NcbiTaxonomyID(6239), // Caenorhabditis elegans
				new NcbiTaxonomyID(3055), // Chlamydomonas reinhardtii
				new NcbiTaxonomyID(7955), // Danio rerio
				new NcbiTaxonomyID(44689), // Dictyostelium discoideum
				new NcbiTaxonomyID(7227), // Drosophila melanogaster
				new NcbiTaxonomyID(562), // Escherichia coli
				new NcbiTaxonomyID(11103), // Hepatitis C virus
				new NcbiTaxonomyID(9606), // Homo sapiens
				new NcbiTaxonomyID(10090), // Mus musculus
				new NcbiTaxonomyID(2104), // Mycoplasma pneumoniae
				new NcbiTaxonomyID(4530), // Oryza sativa
				new NcbiTaxonomyID(5833), // Plasmodium falciparum
				new NcbiTaxonomyID(4754), // Pneumocystis carinii
				new NcbiTaxonomyID(10116), // Rattus norvegicus
				new NcbiTaxonomyID(4932), // Saccharomyces cerevisiae
				new NcbiTaxonomyID(4896), // Schizosaccharomyces pombe
				new NcbiTaxonomyID(31033), // Takifugu rubripes
				new NcbiTaxonomyID(8355), // Xenopus laevis
				new NcbiTaxonomyID(4577) // Zea mays
				);
		/* @formatter:on */
		return buildConceptMapperDictionary(workDirectory, cleanWorkDirectory, taxonomyIdsToInclude);
	}

	public static File buildConceptMapperDictionary(File workDirectory, boolean cleanWorkDirectory,
			Set<NcbiTaxonomyID> taxonomyIdsToInclude) throws IOException {

		File dictionaryFile = new File(workDirectory, "cmDict-EntrezGene.xml");
		ConceptMapperDictionaryBuilder dictBuilder = new ConceptMapperDictionaryBuilder(dictionaryFile,
				MINIMUM_TERM_LENGTH);
		int count = 0;
		for (EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(workDirectory, cleanWorkDirectory); parser
				.hasNext();) {
			if (count++ % 100000 == 0)
				logger.info("Progress: " + (count - 1));

			EntrezGeneInfoFileData dataRecord = parser.next();
			NcbiTaxonomyID taxonID = dataRecord.getTaxonID();
			boolean useRecord = true;
			if (taxonomyIdsToInclude != null && !taxonomyIdsToInclude.isEmpty()) {
				useRecord = taxonomyIdsToInclude.contains(taxonID);
			}

			if (useRecord) {
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
	 *            args[1] = clean work directory (true/false) <br>
	 *            args[2+] = taxonomy Ids to include
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		File workDirectory = new File(args[0]);
		boolean cleanWorkDirectory = Boolean.valueOf(args[1]);
		Set<NcbiTaxonomyID> taxonomyIdsToInclude = new HashSet<NcbiTaxonomyID>();
		for (int i = 2; i < args.length; i++) {
			taxonomyIdsToInclude.add(new NcbiTaxonomyID(args[i]));
		}

		try {
			EntrezGeneDictionaryFactory.buildConceptMapperDictionary(workDirectory, cleanWorkDirectory,
					taxonomyIdsToInclude);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
