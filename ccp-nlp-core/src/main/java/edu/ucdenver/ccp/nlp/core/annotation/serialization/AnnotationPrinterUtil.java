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

package edu.ucdenver.ccp.nlp.core.annotation.serialization;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * An interface for applications that print annotations.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public abstract class AnnotationPrinterUtil {

	public void printAnnotations(Collection<TextAnnotation> textAnnotations, PrintStream ps) {
		printTextAnnotations(textAnnotations, ps);
	}

	public void printDocumentCollection(List<GenericDocument> documents) {
		printDocumentCollection(documents, System.out);
	}

	public void printDocumentCollection(List<GenericDocument> documents, PrintStream ps) {
		for (GenericDocument gd : documents) {
			printDocument(gd, ps);
		}
	}

	public void printDocument(GenericDocument gd) {
		printDocument(gd, System.out);
	}

	public void printDocument(GenericDocument gd, PrintStream ps) {
		List<TextAnnotation> annotations = gd.getAnnotations();
		printTextAnnotations(annotations, ps);
	}

	protected void printTextAnnotations(Collection<TextAnnotation> annotations, PrintStream ps) {
		List<TextAnnotation> taList = new ArrayList<TextAnnotation>(annotations);
		Collections.sort(taList, TextAnnotation.BY_SPAN());
		for (TextAnnotation ta : annotations) {
			printTextAnnotation(ta, ps);
		}
	}

	public abstract void printTextAnnotation(TextAnnotation ta, PrintStream ps);

}