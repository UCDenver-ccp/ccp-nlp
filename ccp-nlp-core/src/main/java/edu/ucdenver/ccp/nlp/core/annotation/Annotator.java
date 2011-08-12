/*
 * Annotator.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */

package edu.ucdenver.ccp.nlp.core.annotation;

import java.io.PrintStream;

/**
 * This class provides a means to identify who/what created an annotation. The annotator is used to represent both human annotators as well as
 * programs that generate annotations, e.g. gene taggers, etc. An annotator can be assigned to a <code>TextAnnotation</code> object.
 * 
 * @author William A Baumgartner, Jr.
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
