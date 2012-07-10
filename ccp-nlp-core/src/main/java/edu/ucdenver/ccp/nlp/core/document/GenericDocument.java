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
package edu.ucdenver.ccp.nlp.core.document;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * This class provides a means to represent a document. Its original intent was to be used as a utility class for
 * grouping annotations and document sections together, therefore it is quite simple in construction.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class GenericDocument {

	private String titleText;

	private String abstractText;

	private String documentID;

	private int documentCollectionID;

	private List<String> secondaryDocumentIDs;

	/*
	 * This map should eventually take the place of the secondaryDocumentIDs list. This way, you can distinguish between
	 * different types of IDs
	 */
	private Map<String, String> otherDocumentIDs;

	private String documentText;

	private List<DocumentSection> documentSections;

	private List<TextAnnotation> annotations;

	public GenericDocument(String documentID) {
		otherDocumentIDs = new HashMap<String, String>();
		secondaryDocumentIDs = new ArrayList<String>();
		this.documentID = documentID;
		annotations = new ArrayList<TextAnnotation>();
	}

	public GenericDocument() {
		otherDocumentIDs = new HashMap<String, String>();
		annotations = new ArrayList<TextAnnotation>();
		secondaryDocumentIDs = new ArrayList<String>();
	}

	/**
	 * Retrieve any other identifiers associated with this document
	 * 
	 * @return
	 */
	public Map<String, String> getOtherDocumentIDs() {
		return otherDocumentIDs;
	}

	/**
	 * Insert a new document ID (other than the primary ID)
	 * 
	 * @param idType
	 * @param id
	 */
	public void addOtherDocumentID(String idType, String id) {
		otherDocumentIDs.put(idType, id);
	}

	public List<TextAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<TextAnnotation> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(TextAnnotation annotation) {
		this.annotations.add(annotation);
	}

	@Deprecated
	public String getAbstractText() {
		return abstractText;
	}

	@Deprecated
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	@Deprecated
	public String getTitleText() {
		return titleText;
	}

	@Deprecated
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public List<DocumentSection> getDocumentSections() {
		return documentSections;
	}

	public void addDocumentSection(DocumentSection ds) {
		if (documentSections != null) {
			documentSections.add(ds);
		} else {
			documentSections = new ArrayList<DocumentSection>();
			documentSections.add(ds);
		}
	}

	public void setDocumentSections(List<DocumentSection> documentSections) {
		this.documentSections = documentSections;
	}

	public String getDocumentText() {
		return documentText;
	}

	public void setDocumentText(String documentText) {
		this.documentText = documentText;
	}

	public int getDocumentCollectionID() {
		return documentCollectionID;
	}

	public void setDocumentCollectionID(int documentCollectionID) {
		this.documentCollectionID = documentCollectionID;
	}

	@Deprecated
	public List<String> getSecondaryDocumentIDs() {
		return secondaryDocumentIDs;
	}

	@Deprecated
	public void setSecondaryDocumentIDs(List<String> secondaryDocumentIDs) {
		this.secondaryDocumentIDs = secondaryDocumentIDs;
	}

	@Deprecated
	public void addSecondaryDocumentID(String secondaryDocID) {
		this.secondaryDocumentIDs.add(secondaryDocID);
	}

	public void printDocument(PrintStream ps) {
		ps.println("==============================================");
		ps.println("DOCUMENT: " + this.documentID);
		ps.println("TITLE: " + this.titleText);
		ps.println("ABSTRACT: " + this.abstractText);
		ps.println("DOCUMENT TEXT: " + this.documentText);
		ps.println("--------- ANNOTATIONS ---------");
		for (TextAnnotation ta : this.annotations) {
			ta.printAnnotation(ps);
		}
	}

}
