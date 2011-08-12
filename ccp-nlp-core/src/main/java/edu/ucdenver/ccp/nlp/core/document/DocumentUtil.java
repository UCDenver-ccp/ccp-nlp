/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.core.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.SpanUtils;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * Some utility methods for dealing with <code>GenericDocuments</code>.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class DocumentUtil {

	/**
	 * 
	 * @return a mapping between section ID and section name as defined in <code>DocumentSectionTypes</code>.
	 */
	public static HashMap<Integer, String> getSectionIDNameHash() {
		HashMap<Integer, String> idNameHash = new HashMap<Integer, String>();
		idNameHash.put(DocumentSectionTypes.OTHER_ID, DocumentSectionTypes.OTHER_NAME);
		idNameHash.put(DocumentSectionTypes.TITLE_ID, DocumentSectionTypes.TITLE_NAME);
		idNameHash.put(DocumentSectionTypes.ABSTRACT_ID, DocumentSectionTypes.ABSTRACT_NAME);
		return idNameHash;
	}

	/**
	 * 
	 * @return a mapping between section name and section ID as defined in <code>DocumentSectionTypes</code>.
	 */
	public static HashMap<String, Integer> getSectionNameIDHash() {
		HashMap<String, Integer> nameIDHash = new HashMap<String, Integer>();
		nameIDHash.put(DocumentSectionTypes.OTHER_NAME, DocumentSectionTypes.OTHER_ID);
		nameIDHash.put(DocumentSectionTypes.TITLE_NAME, DocumentSectionTypes.TITLE_ID);
		nameIDHash.put(DocumentSectionTypes.ABSTRACT_NAME, DocumentSectionTypes.ABSTRACT_ID);
		return nameIDHash;
	}

    
    public static List<Span> usedSpans (GenericDocument doc) {
        return usedSpans(doc.getAnnotations());
    }
    
    public static List<Span> usedSpans (Collection<TextAnnotation> annotations) {
        List<Span> mergedSpans = new LinkedList<Span>();
        for (TextAnnotation a : annotations) {
            if (a.getClassMention().getMentionName() != "token") {
                mergedSpans = SpanUtils.mergeSpans(mergedSpans, a.getSpans());
            }
        }
        return mergedSpans;
    }

  
    public static List<Span> identifiedSpans (GenericDocument doc) {
        return identifiedSpans(doc.getAnnotations());
    }

    public static List<Span> identifiedSpans (Collection<TextAnnotation> annotations) {
        List<Span> mergedSpans = new LinkedList<Span>();
        for (TextAnnotation a : annotations) {
            mergedSpans = SpanUtils.mergeSpans(mergedSpans, a.getSpans());
        }
        return mergedSpans;
    }

    
    
}
