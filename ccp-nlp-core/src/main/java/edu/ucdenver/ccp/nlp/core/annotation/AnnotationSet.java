/*
 * AnnotationSet.java
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
 * This class provides a means to arbitrarily classify/group <code>TextAnnotations</code>. Each annotation set has a name, description, and integer
 * identifier that can be used to uniquely identify the set. Some example uses of the annotation set include defining a group of gold standard annotations, or
 * classifying a group of annotations created using different parameters, but the same annotator. <code>TextAnnotations</code> can be assigned to
 * multiple annotations sets.<p>
 * 
 * 
 * @author William A Baumgartner, Jr.
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
