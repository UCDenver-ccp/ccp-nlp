/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.bionlp;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class BioNlpThemeFactory {

	public static final String THEME_ID_SLOT_NAME = "entity ID";
	
	private static final Annotator ANNOTATOR = new Annotator(291514121, "BioNLP", "T-line Input", "Unknown");
	
	/**
	 * Parses a BioNLP entity line and returns a {@link TextAnnotation} for the represented entity
	 * 
	 * @param line
	 * @return
	 */
	public static TextAnnotation createThemeAnnotation(String line) {
		if (!line.startsWith("T"))
			throw new IllegalArgumentException(
					"Cannot create annotation for a BioNLP event from a line that does not start with 'T':" + line);
		String[] toks = line.split("\\p{Space}+");
		String entityIdStr = toks[0];
		String entityTypeStr = toks[1].toLowerCase();
		int entitySpanStart = Integer.parseInt(toks[2]);
		int entitySpanEnd = Integer.parseInt(toks[3]);

		ClassMention cm = new DefaultClassMention(entityTypeStr);
		TextAnnotation ta = new DefaultTextAnnotation(entitySpanStart, entitySpanEnd, "", ANNOTATOR,
				new AnnotationSet(), -1, -1, "", -1, cm);
		
		try {
			TextAnnotationUtil.addSlotValue(ta, THEME_ID_SLOT_NAME, entityIdStr);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding entity Id slot and value.");
		}
		
		return ta;
	}
	
}
