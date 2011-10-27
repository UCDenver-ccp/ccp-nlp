/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@Entity
public class BerkeleyDbGeneNameEntry implements GeneNameDictionaryEntry {

	@PrimaryKey
	private String dictionaryKey;

	private String canonicalEntryName;

	private Set<String> dbIdentifiers;

	@SecondaryKey(relate = Relationship.MANY_TO_MANY)
	private Set<String> entryNameAliases;
	/**
	 * This constant must match the field name for the "entryNameAliases". It's used to access the
	 * Berkeley DB secondary index.
	 */
	public static final String ENTRY_NAME_ALIASES_FIELD_NAME = "entryNameAliases";

	// @SecondaryKey(relate = Relationship.ONE_TO_MANY)
	private Integer ncbiTaxonomyId;
	/**
	 * This constant must match the field name for the "entryNameAliases". It's used to access the
	 * Berkeley DB secondary index.
	 */
	public static final String TAXONOMY_ID_FIELD_NAME = "ncbiTaxonomyId";

	/**
	 * Default constructor needed by Berkeley DB annotation processing
	 */
	private BerkeleyDbGeneNameEntry() {
		this.dictionaryKey = "";
		this.canonicalEntryName = "";
		this.ncbiTaxonomyId = -1;
		this.entryNameAliases = new HashSet<String>();
		this.dbIdentifiers = new HashSet<String>();
	}

	/**
	 * @param dictionaryKey
	 * @param canonicalEntryName
	 */
	public BerkeleyDbGeneNameEntry(String dictionaryKey, String canonicalEntryName, Integer taxonomyId,
			String dbIdentifier) {
		super();
		this.dictionaryKey = dictionaryKey;
		this.canonicalEntryName = canonicalEntryName;
		this.ncbiTaxonomyId = taxonomyId;
		this.entryNameAliases = new HashSet<String>();
		addEntryNameAlias(canonicalEntryName);
		this.dbIdentifiers = new HashSet<String>();
		this.dbIdentifiers.add(dbIdentifier);
	}

	public void addEntryNameAlias(String syn) {
		if (syn != null)
			entryNameAliases.add(syn);
	}

	@Override
	public String getDictionaryKey() {
		return dictionaryKey;
	}

	@Override
	public String getCanonicalEntryName() {
		return canonicalEntryName;
	}

	@Override
	public Set<String> getEntryNameAliases() {
		return entryNameAliases;
	}

	@Override
	public Set<String> getDbIdentifiers() {
		return dbIdentifiers;
	}

	/**
	 * @return the ncbiTaxonomyId
	 */
	@Override
	public Integer getNcbiTaxonomyId() {
		return ncbiTaxonomyId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BerkeleyDbGeneNameEntry [dictionaryKey=" + dictionaryKey + ", canonicalEntryName=" + canonicalEntryName
				+ ", dbIdentifiers=" + dbIdentifiers + ", entryNameAliases=" + entryNameAliases + ", ncbiTaxonomyId="
				+ ncbiTaxonomyId + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry#merge(edu.ucdenver.ccp.nlp.ext.dictionary
	 * .DictionaryEntry)
	 */
	@Override
	public void merge(DictionaryEntry entry) throws IllegalArgumentException {
		if (!(entry instanceof BerkeleyDbGeneNameEntry))
			throw new IllegalArgumentException("Cannot merge BerkeleyDbGeneNameEntry with "
					+ entry.getClass().getName());
		if (!entry.getDictionaryKey().equals(getDictionaryKey()))
			throw new IllegalArgumentException(
					"Cannot merge BerkeleyDbGeneNameEntries. They have different dictionary keys: "
							+ entry.getDictionaryKey() + " != " + getDictionaryKey());

		BerkeleyDbGeneNameEntry geneEntry = (BerkeleyDbGeneNameEntry) entry;
		this.entryNameAliases.addAll(geneEntry.getEntryNameAliases());
		this.dbIdentifiers.addAll(geneEntry.getDbIdentifiers());
	}

}
