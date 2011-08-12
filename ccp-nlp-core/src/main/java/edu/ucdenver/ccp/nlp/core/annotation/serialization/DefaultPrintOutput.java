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
import java.util.HashMap;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.DocumentSection;
import edu.ucdenver.ccp.nlp.core.document.DocumentUtil;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * This annotation printer implements a default output format for printing annotations from a <code>GenericDocument</code>.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class DefaultPrintOutput extends AnnotationPrinterUtil {
    private boolean useOneLineFormat = false;

//    public void printDocumentCollection(List<GenericDocument> documents) {
//        printDocumentCollection(documents, System.out);
//    }
//
//    public void printDocumentCollection(List<GenericDocument> documents, PrintStream ps) {
//        for (GenericDocument gd : documents) {
//            printDocument(gd, ps);
//        }
//    }
//
//    public void printDocument(GenericDocument gd) {
//        printDocument(gd, System.out);
//    }

    public void setUseOneLineFormatStatus(boolean useOneLineFormat) {
        this.useOneLineFormat = useOneLineFormat;
    }

    public void printDocument(GenericDocument gd, PrintStream ps) {
        List<TextAnnotation> annotations = gd.getAnnotations();

        ps.println("==============================================================================");
        // print document information
        String docID = gd.getDocumentID();
        int docSize=0;
        if (gd.getDocumentText() != null) {
        	docSize = gd.getDocumentText().length();
        }
        ps.println("DOCUMENT ID: " + docID);

        // print document section information
        HashMap<Integer, String> docSectionIDNameHash = DocumentUtil.getSectionIDNameHash();
        List<DocumentSection> documentSections = gd.getDocumentSections();
        String sections = "";
        if (documentSections != null) {
            for (DocumentSection ds : documentSections) {
                sections += (docSectionIDNameHash.get(ds.getDocumentSectionID()) + "("
                        + (ds.getSectionEndIndex() - ds.getSectionStartIndex()) + ")  ");
            }
        }

        ps.println("DOCUMENT LENGTH: " + docSize);
        ps.println("DOCUMENT SECTIONS: " + sections);
        ps.println("DOCUMENT: " + gd.getDocumentText());

        // iterate and print annotations
        ps.println("------------------------------------------------------------------------------------");
        ps.println("------------------------------      ANNOTATIONS      -------------------------------");
        ps.println("------------------------------------------------------------------------------------");

        for (TextAnnotation ta : annotations) {
            printTextAnnotation(ta, ps);
        }
        ps.println("==============================================================================");

    }

	@Override
	public void printTextAnnotation(TextAnnotation ta, PrintStream ps) {
		if (useOneLineFormat) {
            ta.printAnnotationOnOneLine(ps);
        } else {
            ta.printAnnotation(ps);
        }
	}

}
