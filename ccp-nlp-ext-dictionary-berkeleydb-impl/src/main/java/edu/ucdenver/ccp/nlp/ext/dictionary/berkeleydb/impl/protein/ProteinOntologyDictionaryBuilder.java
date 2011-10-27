/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.protein;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.geneontology.oboedit.dataadapter.OBOParseException;
import org.geneontology.oboedit.datamodel.OBOClass;
import org.geneontology.oboedit.datamodel.Synonym;

import edu.ucdenver.ccp.fileparsers.DataRecord;
import edu.ucdenver.ccp.fileparsers.obo.OBOClassRecord;
import edu.ucdenver.ccp.fileparsers.pro.ProOntologyClassIterator;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbDictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbGeneNameEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.GeneNameEntryIndexAccessor;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.GeneNameRegularizer;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.HomologeneDictionaryBuilder;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class ProteinOntologyDictionaryBuilder extends BerkeleyDbDictionaryBuilder<BerkeleyDbGeneNameEntry> {

	private static final Logger logger = Logger.getLogger(HomologeneDictionaryBuilder.class);

	public static final String STORE_NAME = "pro-store";

	public static final Class<GeneNameRegularizer> ENTRY_REGULARIZER_CLASS = GeneNameRegularizer.class;

	public static final Class<BerkeleyDbGeneNameEntry> DICTIONARY_ENTRY_CLASS = BerkeleyDbGeneNameEntry.class;

	public static final Class<GeneNameEntryIndexAccessor> ENTRY_INDEX_ACCESSOR_CLASS = GeneNameEntryIndexAccessor.class;

	private final File dataDirectory;

	private final boolean cleanDataFiles;

	/**
	 * @param dictionaryDirectory
	 * @param dataDirectory
	 *            directory where file parsers will look for (or download if necessary) the files
	 *            they need to parse, e.g. the homologene.dat file and the Entrez Gene gene_info
	 *            file.
	 */
	public ProteinOntologyDictionaryBuilder(File dictionaryDirectory, File dataDirectory, boolean cleanDataFiles) {
		super(dictionaryDirectory, STORE_NAME, ENTRY_REGULARIZER_CLASS, ENTRY_INDEX_ACCESSOR_CLASS,
				BerkeleyDbGeneNameEntry.class);
		this.dataDirectory = dataDirectory;
		this.cleanDataFiles = cleanDataFiles;
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
			final ProOntologyClassIterator prClassIter = new ProOntologyClassIterator(dataDirectory, cleanDataFiles);

			return new Iterator<BerkeleyDbGeneNameEntry>() {

				private BerkeleyDbGeneNameEntry nextEntry = null;

				@Override
				public boolean hasNext() {
					if (nextEntry == null) {
						if (prClassIter.hasNext()) {
							OBOClassRecord dataRecord = prClassIter.next();
							nextEntry = extractEntriesFromRecord(dataRecord.getOboClass());
							return true;
						}
						return false;
					}
					return true;
				}

				private BerkeleyDbGeneNameEntry extractEntriesFromRecord(OBOClass oboClass) {
					String prId = oboClass.getID();
					String canonicalName = oboClass.getName();
					BerkeleyDbGeneNameEntry entry = new BerkeleyDbGeneNameEntry(prId, canonicalName, null, prId);
					@SuppressWarnings("unchecked")
					Set<Synonym> synonyms = oboClass.getSynonyms();
					for (Synonym synonym : synonyms) {
						if (synonym.getScope() != Synonym.RELATED_SYNONYM)
							entry.addEntryNameAlias(synonym.getText());
					}
					System.out.println(entry.toString());
					return entry;
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
		} catch (OBOParseException e) {
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

		new ProteinOntologyDictionaryBuilder(dictionaryDirectory, dataDirectory, false)
				.buildDictionary(LoadMode.OVERWRITE);
	}

}
