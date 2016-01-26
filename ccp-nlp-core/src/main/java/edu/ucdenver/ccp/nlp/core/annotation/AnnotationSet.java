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

/**
 * This class provides a means to arbitrarily classify/group <code>TextAnnotations</code>. Each
 * annotation set has a name, description, and integer identifier that can be used to uniquely
 * identify the set. Some example uses of the annotation set include defining a group of gold
 * standard annotations, or classifying a group of annotations created using different parameters,
 * but the same annotator. <code>TextAnnotations</code> can be assigned to multiple annotations
 * sets.
 * <p>
 * 
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class AnnotationSet implements Comparable {
	private final boolean DEBUG = false;

	private String annotationSetName;

	private String annotationSetDescription;

	private Integer annotationSetID;

	public static final int Art502TextDocumentParser_ANNOTATION_SET_ID = 98;
	public static final int DEFAULT_ANNOTATION_SET_ID = -1;

	public AnnotationSet(Integer annotationSetID, String annotationSetName, String annotationSetDescription) {
		this.annotationSetID = annotationSetID;
		this.annotationSetName = annotationSetName;
		this.annotationSetDescription = annotationSetDescription;
	}

	public AnnotationSet() {
		this.annotationSetID = DEFAULT_ANNOTATION_SET_ID;
		this.annotationSetName = "Default Set";
		this.annotationSetDescription = "Default Set";
	}

	public String getAnnotationSetDescription() {
		return annotationSetDescription;
	}

	public void setAnnotationSetDescription(String annotationSetDescription) {
		this.annotationSetDescription = annotationSetDescription;
	}

	public Integer getAnnotationSetID() {
		return annotationSetID;
	}

	public void setAnnotationSetID(Integer annotationSetID) {
		this.annotationSetID = annotationSetID;
	}

	public String getAnnotationSetName() {
		return annotationSetName;
	}

	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}

	public String getStorageLine() {
		return annotationSetID + "|" + annotationSetName + "|" + annotationSetDescription;
	}

	public void printStorageLine(PrintStream ps) {
		ps.println(getStorageLine());
	}

	/**
	 * Note: this method compares the name field only. The integer ID is not considered here.
	 */
	public int compareTo(Object annotationSet) {
		if (annotationSet instanceof AnnotationSet) {
			String setName = ((AnnotationSet) annotationSet).getAnnotationSetName();

			if (DEBUG) {
				System.out.println("COMPARING ANNOTATION SETS:");
				((AnnotationSet) annotationSet).printStorageLine(System.out);
				this.printStorageLine(System.out);
			}

			if (this.annotationSetName.equalsIgnoreCase(setName)) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	@Override
	public boolean equals(Object annotationSet) {
		if (compareTo(annotationSet) == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		String hashkey = annotationSetName;
		if (hashkey != null) {
			hashkey = hashkey.toLowerCase();
		} else {
			hashkey = "defaultAnnotationSet";
		}
		return hashkey.hashCode();
	}

}
