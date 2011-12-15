package edu.ucdenver.ccp.nlp.wrapper.linnaeus.dictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringUtil;

public class LinnaeusVariantDictionaryItem {

	public enum FilterTermVariants {
		ON, OFF;
	}

	private static final String COLUMN_DELIMITER = StringConstants.TAB;
	private static final String VARIANT_DELIMITER = StringConstants.VERTICAL_LINE;
	private static final int DICTIONARY_ENTRY_LENGTH_THRESHOLD = 2;

	private final String termID;
	private Collection<String> termNameVariants;

	public LinnaeusVariantDictionaryItem(String termID, String... termNameVariants) {
		this.termID = termID;
		this.termNameVariants = new HashSet<String>();
		if (termNameVariants != null)
			this.termNameVariants = new HashSet<String>(Arrays.asList(termNameVariants));
	}

	public LinnaeusVariantDictionaryItem(String termID, Collection<String> termNameVariants) {
		this.termID = termID;
		this.termNameVariants = new HashSet<String>(termNameVariants);
	}

	public void addTermNameVariant(String nameVariant) {
		this.termNameVariants.add(nameVariant);
	}

	public void addTermNameVariants(Collection<String> nameVariants) {
		this.termNameVariants.addAll(nameVariants);
	}

	/**
	 * Returns a String containing a properly formatted line for a Linnaeus dictionary: [TERM_ID]
	 * [tab] [TERM_NAME_VARIANT1]|[TERM_NAME_VARIANT2]|...
	 * 
	 * @return
	 */
	public String getDictionaryEntryString(FilterTermVariants filter) {
		if (filter.equals(FilterTermVariants.ON))
			termNameVariants = filterDictionaryEntries(termNameVariants);
		if (termNameVariants.size() > 0) {
			String termNameVariantColumnStr = getTermNameVariantColumnStr();
			return String.format("%s%s%s", termID, COLUMN_DELIMITER, termNameVariantColumnStr);
		}
		return null;
	}

	private Set<String> filterDictionaryEntries(Collection<String> termNameVariants) {
		Set<String> filteredTermNameVariants = new HashSet<String>();
		for (String variant : termNameVariants) {
			if (variant.trim().length() > DICTIONARY_ENTRY_LENGTH_THRESHOLD
					&& !variant.toLowerCase().contains("hypothetical") && !variant.trim().matches("LOC\\d+"))
				filteredTermNameVariants.add(variant);

		}
		return filteredTermNameVariants;
	}

	private String getTermNameVariantColumnStr() {
		StringBuilder sb = new StringBuilder();
		List<String> sortedVariants = CollectionsUtil.createSortedList(termNameVariants);
		for (String nameVariant : sortedVariants) {
			sb.append(nameVariant + VARIANT_DELIMITER);
		}
		return StringUtil.removeLastCharacter(sb.toString());
	}
}
