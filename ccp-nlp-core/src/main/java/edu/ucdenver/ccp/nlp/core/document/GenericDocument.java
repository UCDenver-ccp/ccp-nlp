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
 * @author William A Baumgarnter, Jr.
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