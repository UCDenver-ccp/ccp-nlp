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
	
	private static final Annotator ANNOTATOR = new Annotator("291514121", "BioNLP", "Unknown");
	
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
				new AnnotationSet(), "-1", -1, "", -1, cm);
		
		try {
			TextAnnotationUtil.addSlotValue(ta, THEME_ID_SLOT_NAME, entityIdStr);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding entity Id slot and value.");
		}
		
		return ta;
	}
	
}
