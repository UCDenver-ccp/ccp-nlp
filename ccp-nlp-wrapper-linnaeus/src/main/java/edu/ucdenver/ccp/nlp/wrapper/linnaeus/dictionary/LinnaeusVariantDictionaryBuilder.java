package edu.ucdenver.ccp.nlp.wrapper.linnaeus.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

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

	private final File outputDictionaryFile;
	private final CharacterEncoding encoding;

	public LinnaeusVariantDictionaryBuilder(File outputDictionaryFile, CharacterEncoding encoding) {
		this.outputDictionaryFile = outputDictionaryFile;
		this.encoding = encoding;
	}

	public void buildDictionary() throws IOException {
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputDictionaryFile, encoding, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		try {
			for (Iterator<LinnaeusVariantDictionaryItem> itemIter = getDictionaryItemIterator(); itemIter.hasNext();) {
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

	protected abstract Iterator<LinnaeusVariantDictionaryItem> getDictionaryItemIterator() throws IOException;

}
