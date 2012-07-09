/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
