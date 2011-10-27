/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileData;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class EntrezGeneInfoRecordFilter {

	public static boolean ignoreRecord(EntrezGeneInfoFileData dataRecord) {
		if (dataRecord.getSymbol().toString().equalsIgnoreCase("NEWENTRY"))
			return true;
		if (dataRecord.getDescription() != null && dataRecord.getDescription().toLowerCase().contains("hypothetical"))
			return true;
		if (dataRecord.getDescription() != null && dataRecord.getDescription().toLowerCase().contains("putative"))
			return true;

		return false;
	}

}
