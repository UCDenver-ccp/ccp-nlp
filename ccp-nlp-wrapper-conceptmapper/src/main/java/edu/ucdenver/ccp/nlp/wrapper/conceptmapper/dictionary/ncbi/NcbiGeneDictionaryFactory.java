/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.ncbi;

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
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileUtil.CleanDirectory;
import edu.ucdenver.ccp.datasource.fileparsers.ncbi.gene.NcbiGeneInfoFileData;
import edu.ucdenver.ccp.datasource.fileparsers.ncbi.gene.NcbiGeneInfoFileParser;
import edu.ucdenver.ccp.datasource.identifiers.impl.bio.NcbiTaxonomyID;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.ConceptMapperDictionaryBuilder;

/**
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class NcbiGeneDictionaryFactory {

	private static final Set<String> SYNONYMS_TO_IGNORE = CollectionsUtil.createSet("NEWENTRY", "hypothetical protein",
			"putative");
	private static final Logger logger = Logger.getLogger(NcbiGeneDictionaryFactory.class);

	private static final int MINIMUM_TERM_LENGTH = 1;

	/* @formatter:off */
	private static final Set<NcbiTaxonomyID> MODEL_ORG_TAX_IDS = CollectionsUtil.createSet(
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

	/**
	 * List of organisms taken from the NCBI Taxonomy home page:
	 * http://www.ncbi.nlm.nih.gov/Taxonomy/
	 * 
	 * @param workDirectory
	 * @param cleanWorkDirectory
	 * @return
	 * @throws IOException
	 */
	public static File buildModelOrganismConceptMapperDictionary(File workDirectory, CleanDirectory cleanWorkDirectory)
			throws IOException {

		return buildConceptMapperDictionary(workDirectory, cleanWorkDirectory, MODEL_ORG_TAX_IDS);
	}

	public static File buildModelOrganismConceptMapperDictionary(File geneInfoFile, File dictionaryFile,
			boolean cleanDictFile) throws IOException {

		return buildConceptMapperDictionary(geneInfoFile, dictionaryFile, MODEL_ORG_TAX_IDS, cleanDictFile);
	}

	public static File buildConceptMapperDictionary(File geneInfoFile, File dictionaryFile,
			Set<NcbiTaxonomyID> taxonomyIdsToInclude, boolean cleanDictFile) throws IOException {
		NcbiGeneInfoFileParser parser = new NcbiGeneInfoFileParser(geneInfoFile, CharacterEncoding.UTF_8);
		return buildConceptMapperDictionary(dictionaryFile, taxonomyIdsToInclude, parser, cleanDictFile);
	}

	public static File buildConceptMapperDictionary(File workDirectory, CleanDirectory cleanWorkDirectory,
			Set<NcbiTaxonomyID> taxonomyIdsToInclude) throws IOException {
		boolean doClean = cleanWorkDirectory.equals(CleanDirectory.YES);
		NcbiGeneInfoFileParser parser = new NcbiGeneInfoFileParser(workDirectory, doClean);
		return buildConceptMapperDictionary(workDirectory, taxonomyIdsToInclude, parser, doClean);
	}

	/**
	 * @param workDirectory
	 * @param taxonomyIdsToInclude
	 * @param parser
	 * @return
	 * @throws IOException
	 */
	private static File buildConceptMapperDictionary(File dictionaryFile, Set<NcbiTaxonomyID> taxonomyIdsToInclude,
			NcbiGeneInfoFileParser parser, boolean cleanDictFile) throws IOException {
		if (dictionaryFile.exists()) {
			if (cleanDictFile) {
				FileUtil.deleteFile(dictionaryFile);
			} else {
				return dictionaryFile;
			}
		}
		ConceptMapperDictionaryBuilder dictBuilder = new ConceptMapperDictionaryBuilder(dictionaryFile,
				MINIMUM_TERM_LENGTH);
		int count = 0;
		while (parser.hasNext()) {
			if (count++ % 100000 == 0)
				logger.info("Progress: " + (count - 1));

			NcbiGeneInfoFileData dataRecord = parser.next();
			NcbiTaxonomyID taxonID = dataRecord.getTaxonID();
			boolean useRecord = true;
			if (taxonomyIdsToInclude != null && !taxonomyIdsToInclude.isEmpty()) {
				useRecord = taxonomyIdsToInclude.contains(taxonID);
			}

			if (useRecord) {
				List<String> synonymStrs = new ArrayList<String>();

				String nomSymbol = dataRecord.getSymbolFromNomenclatureAuthority();
				if (nomSymbol != null)
					synonymStrs.add(nomSymbol);
				String nomName = dataRecord.getFullNameFromNomenclatureAuthority();
				if (nomName != null)
					synonymStrs.add(nomName);
				String symbol = dataRecord.getSymbol();
				if (symbol != null)
					synonymStrs.add(symbol);
				Set<String> synonyms = dataRecord.getSynonyms();
				if (synonyms != null)
					for (String syn : synonyms)
						synonymStrs.add(syn);
				Set<String> otherDesignations = dataRecord.getOtherDesignations();
				if (otherDesignations != null)
					for (String desig : otherDesignations)
						synonymStrs.add(desig);

				synonymStrs = filterSynonyms(synonymStrs);

				dictBuilder.addEntry(dataRecord.getGeneID().getId().toString(), synonymStrs);
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

}
