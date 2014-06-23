/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.bionlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioNlpEventIterator implements Iterator<TextAnnotation> {

	private final BufferedReader eventReader;

	private TextAnnotation nextEventAnnotation = null;

	private Map<String, TextAnnotation> bionlpIdToAnnotationMap;

	public BioNlpEventIterator(File eventFile, File entityFile, CharacterEncoding encoding) {
		initThemeIdToAnnotationMap(entityFile, encoding);
		try {
			eventReader = FileReaderUtil.initBufferedReader(eventFile, encoding);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public BioNlpEventIterator(File eventFile, CharacterEncoding encoding,
			Map<String, TextAnnotation> bionlpIdToAnnotationMap) throws FileNotFoundException {
		this.bionlpIdToAnnotationMap = new HashMap<String, TextAnnotation>(bionlpIdToAnnotationMap);
		eventReader = FileReaderUtil.initBufferedReader(eventFile, encoding);
	}

	/**
	 * Populates a mapping from theme ID to annotation from the entities in the input entity file
	 * 
	 * @param eventFile
	 * @param entityFile
	 * @param encoding
	 * @return
	 */
	private void initThemeIdToAnnotationMap(File entityFile, CharacterEncoding encoding) {
		bionlpIdToAnnotationMap = new HashMap<String, TextAnnotation>();
		BioNlpThemeIterator themeIter = new BioNlpThemeIterator(entityFile, encoding);
		while (themeIter.hasNext())
			addAnnotationToMap(themeIter.next(), BioNlpThemeFactory.THEME_ID_SLOT_NAME);
	}

	/**
	 * Adds an annotation to the bionlpIdToAnnotationMap
	 * 
	 * @param ta
	 * @param idSlotName
	 */
	private void addAnnotationToMap(TextAnnotation ta, String idSlotName) {
		String annotId = ta.getClassMention().getPrimitiveSlotMentionByName(idSlotName).getSingleSlotValue().toString();
		bionlpIdToAnnotationMap.put(annotId, ta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (nextEventAnnotation == null) {
			String line;
			try {
				do {
					line = eventReader.readLine();
					if (line != null && line.startsWith("T"))
						addAnnotationToMap(BioNlpThemeFactory.createThemeAnnotation(line),
								BioNlpThemeFactory.THEME_ID_SLOT_NAME);
				} while (line != null && !line.startsWith("E"));
			} catch (IOException e) {
				throw new RuntimeException("Unrecoverable error while reading BioNLP entity line.");
			}
			if (line != null) {
				nextEventAnnotation = BioNlpEventFactory.createEventAnnotation(line, bionlpIdToAnnotationMap);
				addAnnotationToMap(nextEventAnnotation, BioNlpEventFactory.EVENT_ID_SLOT_NAME);
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
		TextAnnotation entityAnnotationToReturn = nextEventAnnotation;
		nextEventAnnotation = null;
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
