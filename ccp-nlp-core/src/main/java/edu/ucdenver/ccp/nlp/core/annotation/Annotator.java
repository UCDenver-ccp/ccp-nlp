package edu.ucdenver.ccp.nlp.core.annotation;

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

import java.io.PrintStream;

import lombok.Data;

/**
 * This class provides a means to identify who/what created an annotation. The annotator is used to
 * represent both human annotators as well as programs that generate annotations, e.g. gene taggers,
 * etc. An annotator can be assigned to a <code>TextAnnotation</code> object.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
@Data
public class Annotator implements Comparable<Annotator> {

	public final static String UCDENVER_AFFILIATION = "UCDenver CCP";
	public final static String Art502TextDocumentParser_ANNOTATOR_ID = "98";
	public final static String CCPXMLParser_ANNOTATOR_ID = "97";
	public final static String TREEBANK_ANNOTATOR_ID = "96";

	private String name;

	private String affiliation;

	private String annotatorID;

	public Annotator(String annotatorID, String name, String affiliation) {
		this.annotatorID = annotatorID;
		if (name != null) {
			this.name = name;
		} else {
			this.name = "";
		}
		if (affiliation != null) {
			this.affiliation = affiliation;
		} else {
			this.affiliation = "";
		}
	}

	
	public String getStorageLine() {
		return annotatorID + "|" + name + "|" + affiliation;
	}

	public void printStorageLine(PrintStream ps) {
		ps.println(getStorageLine());
	}

	/**
	 * Note: this method compares the name field only. The integer ID is not considered here.
	 */
	public int compareTo(Annotator annotator) {
		if (annotator instanceof Annotator) {
			String name = ((Annotator) annotator).getName();
			String affiliation = ((Annotator) annotator).getAffiliation();

			if (name == null) {
				name = "";
			}


			if (affiliation == null) {
				affiliation = "";
			}

			if (this.name.equalsIgnoreCase(name) 
					&& this.affiliation.equalsIgnoreCase(affiliation)) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotator other = (Annotator) obj;
		if (affiliation == null) {
			if (other.affiliation != null)
				return false;
		} else if (!affiliation.equalsIgnoreCase(other.affiliation))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((affiliation == null) ? 0 : affiliation.toLowerCase().hashCode());
		result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		return result;
	}

	
}
