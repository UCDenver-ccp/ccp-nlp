/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@Entity
public class BerkeleyDbDictionaryEntry implements DictionaryEntry {

	@PrimaryKey
	private String dictionaryKey;

	private String canonicalEntryName;

	@SecondaryKey(relate = Relationship.MANY_TO_MANY)
	private Set<String> entryNameAliases;
	
	
	/**
	 * This constant must match the field name for the "entryNameAliases". It's used to access the
	 * Berkeley DB secondary index.
	 */
	public static final String ENTRY_NAME_ALIASES_FIELD_NAME = "entryNameAliases";

	/**
	 * Default constructor needed by Berkeley DB annotation processing
	 */
	private BerkeleyDbDictionaryEntry() {
		this.dictionaryKey = "";
		this.canonicalEntryName = "";
		this.entryNameAliases = new HashSet<String>();
	}

	/**
	 * @param dictionaryKey
	 * @param canonicalEntryName
	 */
	public BerkeleyDbDictionaryEntry(String dictionaryKey, String canonicalEntryName) {
		super();
		this.dictionaryKey = dictionaryKey;
		this.canonicalEntryName = canonicalEntryName;
		this.entryNameAliases = new HashSet<String>();
		entryNameAliases.add(canonicalEntryName);
	}

	public void addEntryNameAlias(String syn) {
		entryNameAliases.add(syn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.dictionarylookup.DictionaryEntry#getDictionaryKey()
	 */
	@Override
	public String getDictionaryKey() {
		return dictionaryKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.dictionarylookup.DictionaryEntry#getCanonicalEntryName()
	 */
	@Override
	public String getCanonicalEntryName() {
		return canonicalEntryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.dictionarylookup.DictionaryEntry#getEntryNameSynonyms()
	 */
	@Override
	public Set<String> getEntryNameAliases() {
		return entryNameAliases;
	}

	
	/* (non-Javadoc)
	 * @see edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry#merge(edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry)
	 */
	@Override
	public void merge(DictionaryEntry entry) throws IllegalArgumentException {
		this.entryNameAliases.addAll(entry.getEntryNameAliases());
		this.entryNameAliases.add(entry.getCanonicalEntryName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BerkeleyDbDictionaryEntry [dictionaryKey=" + dictionaryKey + ", canonicalEntryName="
				+ canonicalEntryName + ", entryNameAliases=" + entryNameAliases + "]";
	}

	




	

}
