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

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * This class outputs the document text and documentid into a format that can be read in by the
 * <code>SimpleCollectionReader</code>, <br>
 * namely documentID|documentText
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class SimpleCollectionReaderFormatOutput extends AnnotationPrinterUtil {
//	private boolean useOneLineFormat = false;
//
//	public void printDocumentCollection(List<GenericDocument> documents) {
//		printDocumentCollection(documents, System.out);
//	}
//
//	public void printDocumentCollection(List<GenericDocument> documents, PrintStream ps) {
//		for (GenericDocument gd : documents) {
//			printDocument(gd, ps);
//		}
//	}
//
//	public void printDocument(GenericDocument gd) {
//		printDocument(gd, System.out);
//	}
//
//	public void setUseOneLineFormatStatus(boolean useOneLineFormat) {
//		this.useOneLineFormat = useOneLineFormat;
//	}

	@Override
	public void printDocument(GenericDocument gd, PrintStream ps) {
		String documentID = gd.getDocumentID();
		String documentText = gd.getDocumentText();

		ps.println(documentID + "|" + documentText);
	}

	@Override
	public void printTextAnnotation(TextAnnotation ta, PrintStream ps) {
		// do nothing. Annotations do not get printed for the SimpleCollectionReaderFormat
	}

}
