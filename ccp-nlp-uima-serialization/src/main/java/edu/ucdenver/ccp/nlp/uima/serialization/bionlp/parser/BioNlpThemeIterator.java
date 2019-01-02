/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.serialization.bionlp.parser;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
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
		} catch (IOException e) {
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
