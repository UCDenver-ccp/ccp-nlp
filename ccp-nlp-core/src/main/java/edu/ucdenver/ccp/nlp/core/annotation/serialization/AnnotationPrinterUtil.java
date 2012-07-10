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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.document.GenericDocument;

/**
 * An interface for applications that print annotations.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
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