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
package edu.ucdenver.ccp.nlp.core.annotation;

import java.io.PrintStream;

/**
 * This class provides a means to identify who/what created an annotation. The annotator is used to represent both human annotators as well as
 * programs that generate annotations, e.g. gene taggers, etc. An annotator can be assigned to a <code>TextAnnotation</code> object.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Annotator implements Comparable {

	public final static String UCDENVER_AFFILIATION = "UCDenver CCP";
	public final static int Art502TextDocumentParser_ANNOTATOR_ID = 98;
	public final static int CCPXMLParser_ANNOTATOR_ID=97;
	public final static int TREEBANK_ANNOTATOR_ID=96;
	
    private String firstName;

    private String lastName;

    private String affiliation;

    private Integer annotatorID;

    public Annotator(Integer annotatorID, String firstName, String lastName, String affiliation) {
        this.annotatorID = annotatorID;
        if (firstName != null) {
            this.firstName = firstName;
        } else {
            this.firstName = "";
        }
        if (lastName != null) {
            this.lastName = lastName;
        } else {
            this.lastName = "";
        }
        if (affiliation != null) {
            this.affiliation = affiliation;
        } else {
            this.affiliation = "";
        }
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        if (affiliation != null) {
            this.affiliation = affiliation;
        } else {
            this.affiliation = "";
        }
    }

    public Integer getAnnotatorID() {
        return annotatorID;
    }

    public void setAnnotatorID(Integer annotatorID) {
        this.annotatorID = annotatorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName != null) {
            this.firstName = firstName;
        } else {
            this.firstName = "";
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName != null) {
            this.lastName = lastName;
        } else {
            this.lastName = "";
        }
    }

    public String getStorageLine() {
        return annotatorID + "|" + firstName + "|" + lastName + "|" + affiliation;
    }

    public void printStorageLine(PrintStream ps) {
        ps.println(getStorageLine());
    }

    /**
     * Note: this method compares the name field only. The integer ID is not considered here.
     */
    public int compareTo(Object annotator) {
        if (annotator instanceof Annotator) {
            String firstName = ((Annotator) annotator).getFirstName();
            String lastName = ((Annotator) annotator).getLastName();
            String affiliation = ((Annotator) annotator).getAffiliation();

            if (firstName == null) {
                firstName = "";
            }

            if (lastName == null) {
                lastName = "";
            }

            if (affiliation == null) {
                affiliation = "";
            }

            if (this.firstName.equalsIgnoreCase(firstName) & this.lastName.equalsIgnoreCase(lastName)
                    & this.affiliation.equalsIgnoreCase(affiliation)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object annotator) {
        if (compareTo(annotator) == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String hashkey = firstName + "|" + lastName + "|" + affiliation;
        hashkey = hashkey.toLowerCase();
        return hashkey.hashCode();
    }
}
