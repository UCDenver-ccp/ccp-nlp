/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.homologene.HomologeneGroupID;
import edu.ucdenver.ccp.fileparsers.DataRecord;
import edu.ucdenver.ccp.fileparsers.field.GeneNameOrSymbol;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileData;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneInfoFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.homologene.HomoloGeneDataFileData;
import edu.ucdenver.ccp.fileparsers.ncbi.homologene.HomoloGeneDataFileParser;
import edu.ucdenver.ccp.nlp.ext.dictionary.DictionaryEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.EntryRegularizer;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;

/**
 * TODO: Dictionary build takes 3 hours. Speed it up if necessary by loading non-homologene group
 * genes second using overwrite mode. searches should be quicker for homologene groups when they are
 * the only thing in the dictionary, plus it will avoid the search for most genes.<br>
 * TODO: Log cleaning every 30000 inserts is probably overkill<br>
 * <br>
 * Builds a dictionary of gene names. Names are grouped by Homologene Group ID if the gene is a
 * member of a Homologene Group. If not, the Entrez Gene ID is used as the entry primary key.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class HomologeneDictionaryBuilder extends BerkeleyDbDictionaryBuilder<BerkeleyDbGeneNameEntry> {

	private static final Logger logger = Logger.getLogger(HomologeneDictionaryBuilder.class);

	public static final String STORE_NAME = "homologene-store";

	public static final Class<GeneNameRegularizer> ENTRY_REGULARIZER_CLASS = GeneNameRegularizer.class;

	public static final Class<BerkeleyDbGeneNameEntry> DICTIONARY_ENTRY_CLASS = BerkeleyDbGeneNameEntry.class;

	public static final Class<GeneNameEntryIndexAccessor> ENTRY_INDEX_ACCESSOR_CLASS = GeneNameEntryIndexAccessor.class;

	private Map<EntrezGeneID, HomologeneGroupID> egIdToHomologeneGroupIdMap;

	private final File dataDirectory;

	private final boolean cleanDataFiles;

	/**
	 * @param dictionaryDirectory
	 * @param dataDirectory
	 *            directory where file parsers will look for (or download if necessary) the files
	 *            they need to parse, e.g. the homologene.dat file and the Entrez Gene gene_info
	 *            file.
	 */
	public HomologeneDictionaryBuilder(File dictionaryDirectory, File dataDirectory, boolean cleanDataFiles) {
		super(dictionaryDirectory, STORE_NAME, ENTRY_REGULARIZER_CLASS, ENTRY_INDEX_ACCESSOR_CLASS,
				BerkeleyDbGeneNameEntry.class);
		this.dataDirectory = dataDirectory;
		this.cleanDataFiles = cleanDataFiles;
		try {
			loadEgIdToHomologeneGroupIdMap();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void loadEgIdToHomologeneGroupIdMap() throws IOException {
		egIdToHomologeneGroupIdMap = new HashMap<EntrezGeneID, HomologeneGroupID>();
		for (HomoloGeneDataFileParser parser = new HomoloGeneDataFileParser(dataDirectory, cleanDataFiles); parser
				.hasNext();) {
			HomoloGeneDataFileData dataRecord = parser.next();
			EntrezGeneID entrezGeneID = dataRecord.getEntrezGeneID();
			HomologeneGroupID homologeneGroupID = dataRecord.getHomologeneGroupID();
			if (egIdToHomologeneGroupIdMap.containsKey(entrezGeneID)) {
				logger.error("Multiple entries for single gene..  " + entrezGeneID);
			} else {
				egIdToHomologeneGroupIdMap.put(entrezGeneID, homologeneGroupID);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbDictionaryBuilder#createEntry
	 * (edu.ucdenver.ccp.fileparsers.DataRecord)
	 */
	@Override
	protected BerkeleyDbGeneNameEntry createEntry(DataRecord dataRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbDictionaryBuilder#
	 * getEntryIterator()
	 */
	@Override
	protected Iterator<BerkeleyDbGeneNameEntry> getEntryIterator() {
		try {
			final EntrezGeneInfoFileParser parser = new EntrezGeneInfoFileParser(dataDirectory, cleanDataFiles);

			return new Iterator<BerkeleyDbGeneNameEntry>() {

				private BerkeleyDbGeneNameEntry nextEntry = null;
				private List<BerkeleyDbGeneNameEntry> entryList = new ArrayList<BerkeleyDbGeneNameEntry>();

				@Override
				public boolean hasNext() {
					if (nextEntry == null) {
						if (!entryList.isEmpty()) {
							nextEntry = entryList.remove(0);
							return true;
						}
						while (entryList.isEmpty() && parser.hasNext()) {
							EntrezGeneInfoFileData dataRecord = parser.next();
							extractEntriesFromRecord(dataRecord);
						}
						if (entryList.isEmpty())
							return false;
						nextEntry = entryList.remove(0);
					}
					return true;
				}

				/**
				 * Adds at least one and sometime two entries to the entryList.<br>
				 * For each gene, an entry containing the EG ID as a primary key, official symbol,
				 * taxonomy ID, and EG ID is created. If the gene is part of a homologene group,
				 * then an entry for the homologene group is also created. Homologene entries are
				 * keyed by the homologene group ID, and store the gene IDs and aliases for all
				 * genes that are part of the group. If the gene is not part of a homologene group,
				 * then the aliases get attached to it directly.
				 * 
				 * @param dataRecord
				 */
				private void extractEntriesFromRecord(EntrezGeneInfoFileData dataRecord) {
					if (EntrezGeneInfoRecordFilter.ignoreRecord(dataRecord))
						return;
					String geneId = "EG_" + dataRecord.getGeneID().toString();
					String canonicalName = null;
					if (dataRecord.getSymbolFromNomenclatureAuthority() != null)
						canonicalName = dataRecord.getSymbolFromNomenclatureAuthority().toString();
					else if (dataRecord.getSymbol() != null)
						canonicalName = dataRecord.getSymbol().toString();
					Integer taxonomyId = dataRecord.getTaxonID().getDataElement();

					BerkeleyDbGeneNameEntry geneEntry = new BerkeleyDbGeneNameEntry(geneId, canonicalName, taxonomyId,
							geneId);

					Set<String> aliases = new HashSet<String>();
					addIfNotNull(aliases, dataRecord.getFullNameFromNomenclatureAuthority());
					addIfNotNull(aliases, dataRecord.getDescription());
					for (GeneNameOrSymbol alias : dataRecord.getOtherDesignations())
						addIfNotNull(aliases, alias);
					addIfNotNull(aliases, dataRecord.getSymbol());
					addIfNotNull(aliases, dataRecord.getSymbolFromNomenclatureAuthority());
					for (GeneNameOrSymbol alias : dataRecord.getSynonyms())
						addIfNotNull(aliases, alias);

					aliases = GeneNameAliasFilter.filter(aliases);

					if (egIdToHomologeneGroupIdMap.containsKey(dataRecord.getGeneID())) {
						String homologeneGroupId = "HOMOLOGENE_GROUP_"
								+ egIdToHomologeneGroupIdMap.get(dataRecord.getGeneID()).toString();
						BerkeleyDbGeneNameEntry homologeneEntry = new BerkeleyDbGeneNameEntry(homologeneGroupId, null,
								null, geneId);
						for (String alias : aliases)
							homologeneEntry.addEntryNameAlias(alias);
						entryList.add(homologeneEntry);
					} else {
						for (String alias : aliases)
							geneEntry.addEntryNameAlias(alias);
					}

					entryList.add(geneEntry);
				}

				private void addIfNotNull(Set<String> aliases, Object alias) {
					if (alias != null)
						aliases.add(alias.toString());
				}

				@Override
				public BerkeleyDbGeneNameEntry next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}

					BerkeleyDbGeneNameEntry entryToReturn = nextEntry;
					nextEntry = null;
					return entryToReturn;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
				}

			};
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param args
	 *            args[0] - dictionary directory<br>
	 *            args[1] - data directory
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		File dictionaryDirectory = new File(args[0]);
		File dataDirectory = new File(args[1]);

		new HomologeneDictionaryBuilder(dictionaryDirectory, dataDirectory, false).buildDictionary(LoadMode.UPDATE);
	}

}
