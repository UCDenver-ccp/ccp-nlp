package edu.ucdenver.ccp.nlp.wrapper.linnaeus.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.nlp.wrapper.linnaeus.dictionary.LinnaeusVariantDictionaryItem.FilterTermVariants;

/**
 * Abstract class for building name variant dictionaries usable by the Linnaeus tool
 * 
 * @author bill
 * 
 */
public abstract class LinnaeusVariantDictionaryBuilder {

	private static final Logger logger = Logger.getLogger(LinnaeusVariantDictionaryBuilder.class);
	
	private final File outputDictionaryFile;
	private final CharacterEncoding encoding;
	private final boolean caseSensitive;

	public LinnaeusVariantDictionaryBuilder(File outputDictionaryFile, CharacterEncoding encoding, boolean caseSensitive) {
		this.outputDictionaryFile = outputDictionaryFile;
		this.encoding = encoding;
		this.caseSensitive = caseSensitive;
	}

	public void buildDictionary() throws IOException {
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputDictionaryFile, encoding, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		try {
			int count = 0;
			for (Iterator<LinnaeusVariantDictionaryItem> itemIter = getDictionaryItemIterator(caseSensitive); itemIter.hasNext();) {
				if (count++ % 100000 == 0)
					logger.info("Dictionary build progress: " + (count-1) + " entries processed.");
				LinnaeusVariantDictionaryItem dictEntry = itemIter.next();
				String dictionaryEntryString = dictEntry.getDictionaryEntryString(FilterTermVariants.ON);
				if (dictionaryEntryString != null) {
					writer.write(dictionaryEntryString);
					writer.newLine();
				}
			}
		} finally {
			writer.close();
		}
	}

	protected abstract Iterator<LinnaeusVariantDictionaryItem> getDictionaryItemIterator(boolean caseSensitive) throws IOException;

}
