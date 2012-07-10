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
package edu.ucdenver.ccp.nlp.core.mention.comparison;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;

/**
 * Implements the comparison of a pair of <code>ComplexSlotMentions</code>.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */

public class IdenticalComplexSlotMentionComparator extends IdenticalMentionComparator implements ComplexSlotMentionComparator {

    /**
     * Compares two complex slot mentions. They must be ClassMentions or else an UnsupportedOperationException is thrown. The CSMs must have identical
     * mention names, and identical ClassMentions fillers.
     * 
     */
    public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth, int depth) {
        if ((mention1 instanceof ComplexSlotMention) & (mention2 instanceof ComplexSlotMention)) {
            return compare((ComplexSlotMention) mention1, (ComplexSlotMention) mention2, spanComparator, maximumComparisonDepth, depth);
        } else {
            throw new ClassCastException("Cannot support comparison between a ComplexSlotMention and a non-ComplexSlotMention");
        }
    }

    /**
     * Compares two complex slot mentions. The CSMs must have identical mention names, and identical ClassMentions fillers.
     * 
     * @param complexSlotMention1
     * @param complexSlotMention2
     * @param maximumComparisonDepth
     * @param depth
     * @param spanComparator
     * @param compareLinkedAnnotationSpans
     * @return
     */
    @Override
    public int compare(ComplexSlotMention complexSlotMention1, ComplexSlotMention complexSlotMention2, SpanComparator spanComparator,
            int maximumComparisonDepth, int depth) {
//    	System.err.println("CSM MATCH: COMPARING CSMs");
        if (complexSlotMention1.getClassMentions().size() == complexSlotMention2.getClassMentions().size()) {
            /* if the cms's do not have an equal number of class mentions then return false */
            if (hasEquivalentMentionNames(complexSlotMention1, complexSlotMention2)) {
                /* initialize a new IdenticalClassMentionComparator to compare the ClassMentions contained in each CSM */
                IdenticalClassMentionComparator icc = new IdenticalClassMentionComparator();
                for (ClassMention cm : complexSlotMention1.getClassMentions()) {
                    boolean cmHasMatch = false;
                    for (ClassMention cmToCompare : complexSlotMention2.getClassMentions()) {
                        if (icc.compare(cm, cmToCompare, spanComparator, maximumComparisonDepth, depth) == 0) {
                            cmHasMatch = true;
                        }
                    }
                    if (!cmHasMatch) {
//                    	System.err.println("CSM MATCH: NO MATCH FOR CM: "  + cm.toString());
                        return complexSlotMention1.getSingleLineRepresentation().compareTo(
                                complexSlotMention2.getSingleLineRepresentation())
                                * MULTIPLIER;
                    }
                }
                /* if we get to this point, then all class mentions have a match, so return 0 */
                return 0;
            } else {
//            	System.err.println("CSM MATCH: CSMs HAVE DIFFERENT MENTION NAMES ");
                /* the csm's have different mention names */
                return complexSlotMention1.getSingleLineRepresentation().compareTo(complexSlotMention2.getSingleLineRepresentation())
                        * MULTIPLIER;
            }
        } else {
//        	System.err.println("CSM MATCH: UNEQUAL NUMBER OF CLASS MENTIONS ");
            /* the csm's have an unequal number of class mentions */
            return complexSlotMention1.getSingleLineRepresentation().compareTo(complexSlotMention2.getSingleLineRepresentation())
                    * MULTIPLIER;
        }
    }

}
