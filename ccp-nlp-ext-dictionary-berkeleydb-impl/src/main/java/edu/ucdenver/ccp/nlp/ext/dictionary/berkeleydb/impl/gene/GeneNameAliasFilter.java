/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GeneNameAliasFilter {

	public static Set<String> filter(Set<String> aliases) {
		Set<String> filteredAliases = new HashSet<String>();
		for (String alias : aliases) {
			if (alias.length() < 3)
				continue; // if less than 3 characters
			if (alias.matches("\\d+"))
				continue; // if all numbers
			filteredAliases.add(alias);
		}
		return filteredAliases;
	}

}
