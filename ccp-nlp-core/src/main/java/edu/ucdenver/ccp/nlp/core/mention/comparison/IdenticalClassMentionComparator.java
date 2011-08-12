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
import edu.ucdenver.ccp.nlp.core.mention.Mention;

/**
 * This class implements a comparator which compares ClassMentions. ClassMentions must be identical.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class IdenticalClassMentionComparator extends IdenticalMentionComparator implements ClassMentionComparator {

    /**
     * Compare two mentions. They must be ClassMentions or else an UnsupportedOperationException is thrown.
     */
    public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth, int depth) {
        if ((mention1 instanceof ClassMention) & (mention2 instanceof ClassMention)) {
            return compare((ClassMention) mention1, (ClassMention) mention2, spanComparator, maximumComparisonDepth, depth);
        } else {
            throw new ClassCastException("Cannot support comparison between a ClassMention and a non-ClassMention");
        }

    }

    /**
     * 
     * @param cm1
     * @param cm2
     * @param spanComparator
     * @param maximumComparisonDepth
     * @return
     */
    @Override
    public int compare(ClassMention cm1, ClassMention cm2, SpanComparator spanComparator, int maximumComparisonDepth) {
        return compare(cm1, cm2, spanComparator, maximumComparisonDepth, -1);
    }
    
    /**
     * This is a more specific compare() method for ClassMentions.
     * 
     * @param cm1
     * @param cm2
     * @param maximumDepth
     *            specifies the depth in the mention hierarchy
     * @param depth
     * @param compareLinkedAnnotationSpans
     *            toggles the comparison of the linked TextAnnotations. In some cases, the linked TextAnnotations may have already been compared, so
     *            an infinite loop would be created by comparing them again.
     * @return
     * 
     * TODO: we are assuming only a single text annotation per class mention
     */
    private int compare(ClassMention cm1, ClassMention cm2, SpanComparator spanComparator, int maximumComparisonDepth, int depth) {

        /* increment the depth level */
        depth++;

        if (hasEquivalentMentionNames(cm1, cm2)) {
            /*
             * the mention names are identical, so we need to now check the slots and complex slots, unless we have reached the maximum comparison
             * depth
             */
            /* if we have reached the maximum comparison depth, then return now */
            if (maximumComparisonDepth != -1 && depth >= maximumComparisonDepth) {
                return spanComparator.compare(cm1.getTextAnnotation().getSpans(), cm2.getTextAnnotation().getSpans());
            } else {
                /* we have not yet reached the maximum depth, so we need to compare the slot mentions and complex slot mentions */
                boolean annotationSpansMatch = false;
                annotationSpansMatch = (spanComparator.compare(cm1.getTextAnnotation().getSpans(), cm2.getTextAnnotation()
                        .getSpans()) == 0);

//                boolean slotMentionsMatch = equalSlotMentions(cm1.getPrimitiveSlotMentions(), cm2.getPrimitiveSlotMentions(), spanComparator,
//                        new IdenticalSlotMentionComparator(), maximumComparisonDepth, depth);
//                
//                
//                boolean complexSlotMentionsMatch = equalComplexSlotMentions(cm1.getComplexSlotMentions(), cm2.getComplexSlotMentions(),
//                        spanComparator, new IdenticalComplexSlotMentionComparator(), maximumComparisonDepth, depth);

                boolean slotMentionsMatch = false;
                boolean complexSlotMentionsMatch = false;
                // TODO: FIX THE COMPARATORS!!!!!
                
//                System.err.println("SPANS MATCH: " + annotationSpansMatch + "  SLOT MENTIONS MATCH: " + slotMentionsMatch + "  CSMs MATCH: " + complexSlotMentionsMatch);
                
                if (annotationSpansMatch & slotMentionsMatch & complexSlotMentionsMatch) {
                    return 0;
                } else {
                    return cm1.getSingleLineRepresentation().compareTo(cm2.getSingleLineRepresentation()) * MULTIPLIER;
                }
            }
        } else {
            return cm1.getSingleLineRepresentation().compareTo(cm2.getSingleLineRepresentation()) * MULTIPLIER;
        }
    }

}
