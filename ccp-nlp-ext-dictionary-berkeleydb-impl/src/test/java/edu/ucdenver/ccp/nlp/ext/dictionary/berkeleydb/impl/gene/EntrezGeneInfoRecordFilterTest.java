/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.reflection.PrivateAccessor;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.datasource.identifiers.ensembl.EnsemblGeneID;
import edu.ucdenver.ccp.datasource.identifiers.mgi.MgiGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.fileparsers.field.ChromosomeNumber;
import edu.ucdenver.ccp.fileparsers.field.GeneNameOrSymbol;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileData;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class EntrezGeneInfoRecordFilterTest {

	@Test
	public void testGoodRecordPasses() {
		assertFalse(EntrezGeneInfoRecordFilter.ignoreRecord(getValidRecord()));
	}

	@Test
	public void testFiltersNewEntrySymbol() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException {
		EntrezGeneInfoFileData dataRecord = getValidRecord();
		PrivateAccessor.setPrivateFinalFieldValue(dataRecord, "symbol", new GeneNameOrSymbol("NEWENTRY"));
		assertTrue(EntrezGeneInfoRecordFilter.ignoreRecord(dataRecord));
	}

	
	@Test
	public void testFiltersHypotheticalDescription() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException {
		EntrezGeneInfoFileData dataRecord = getValidRecord();
		PrivateAccessor.setPrivateFinalFieldValue(dataRecord, "description", "this description contains hypothetical");
		assertTrue(EntrezGeneInfoRecordFilter.ignoreRecord(dataRecord));
	}
	
	@Test
	public void testFiltersPutativeDescription() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException {
		EntrezGeneInfoFileData dataRecord = getValidRecord();
		PrivateAccessor.setPrivateFinalFieldValue(dataRecord, "description", "this description contains Putative");
		assertTrue(EntrezGeneInfoRecordFilter.ignoreRecord(dataRecord));
	}
	
	private EntrezGeneInfoFileData getValidRecord() {
		Set<GeneNameOrSymbol> expectedSynonyms = new HashSet<GeneNameOrSymbol>();
		expectedSynonyms.add(new GeneNameOrSymbol("AI173996"));
		expectedSynonyms.add(new GeneNameOrSymbol("Abc30"));
		expectedSynonyms.add(new GeneNameOrSymbol("Cmoat"));
		expectedSynonyms.add(new GeneNameOrSymbol("Mrp2"));
		expectedSynonyms.add(new GeneNameOrSymbol("cMRP"));
		Set<DataSourceIdentifier<?>> expectedDBXrefs = new HashSet<DataSourceIdentifier<?>>();
		expectedDBXrefs.add(new MgiGeneID("MGI:1352447"));
		expectedDBXrefs.add(new EnsemblGeneID("ENSMUSG00000025194"));
		Set<GeneNameOrSymbol> expectedOtherDesignations = new HashSet<GeneNameOrSymbol>();
		expectedOtherDesignations.add(new GeneNameOrSymbol("ATP-binding cassette, sub-family C, member 2"));
		expectedOtherDesignations.add(new GeneNameOrSymbol("canalicular multispecific organic anion transporter"));
		expectedOtherDesignations.add(new GeneNameOrSymbol("multidrug resistance protein 2"));

		return new EntrezGeneInfoFileData(new NcbiTaxonomyID(10090), new EntrezGeneID(12780), new GeneNameOrSymbol(
				"Abcc2"), null, expectedSynonyms, expectedDBXrefs, new ChromosomeNumber(19), "19 C3|19 43.0 cM",
				"ATP-binding cassette, sub-family C (CFTR/MRP), member 2", "protein-coding", new GeneNameOrSymbol(
						"Abcc2"), new GeneNameOrSymbol("ATP-binding cassette, sub-family C (CFTR/MRP), member 2"), "O",
				expectedOtherDesignations, "20080827", 0, 0);
	}

}
