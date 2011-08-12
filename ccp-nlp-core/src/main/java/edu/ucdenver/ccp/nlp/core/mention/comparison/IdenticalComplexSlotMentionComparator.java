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

package edu.ucdenver.ccp.nlp.core.mention.comparison;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.mention.ClassMention;
import edu.ucdenver.ccp.nlp.core.mention.ComplexSlotMention;
import edu.ucdenver.ccp.nlp.core.mention.Mention;

/**
 * Implements the comparison of a pair of <code>ComplexSlotMentions</code>.
 * 
 * @author William A Baumgartner, Jr.
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
