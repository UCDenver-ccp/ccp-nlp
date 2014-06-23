/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.bionlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * Parses BioNLP-formatted lines starting with "T" and returns annotations representing the themes -
 * which are entities or target words
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpThemeIterator implements Iterator<TextAnnotation> {

	private final BufferedReader entityReader;

	private TextAnnotation nextEntityAnnotation = null;

	public BioNlpThemeIterator(InputStream entityFileStream, CharacterEncoding encoding) {
		entityReader = FileReaderUtil.initBufferedReader(entityFileStream, encoding);
	}

	public BioNlpThemeIterator(File entityFile, CharacterEncoding encoding) {
		try {
			entityReader = FileReaderUtil.initBufferedReader(entityFile, encoding);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (nextEntityAnnotation == null) {
			String line;
			try {
				do {
					line = entityReader.readLine();
				} while (line != null && !line.startsWith("T"));
			} catch (IOException e) {
				throw new RuntimeException("Unrecoverable error while reading BioNLP entity line.");
			}
			if (line != null) {
				nextEntityAnnotation = BioNlpThemeFactory.createThemeAnnotation(line);
				return true;
			}
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public TextAnnotation next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		TextAnnotation entityAnnotationToReturn = nextEntityAnnotation;
		nextEntityAnnotation = null;
		return entityAnnotationToReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
	}

}
