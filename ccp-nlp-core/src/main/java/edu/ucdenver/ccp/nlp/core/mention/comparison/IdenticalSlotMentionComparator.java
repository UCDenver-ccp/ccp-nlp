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
import edu.ucdenver.ccp.nlp.core.mention.Mention;
import edu.ucdenver.ccp.nlp.core.mention.SlotMention;

/**
 * Implements the comparison of a pair of <code>SlotMentions</code>.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class IdenticalSlotMentionComparator extends IdenticalMentionComparator implements SlotMentionComparator {

    /**
     * Compare two slot mentions. They must be SlotMentions or else an UnsupportedOperationException is thrown. In order to be identical (and return 0),
     * they must have the name mention name, and the identical slot values
     */
    public int compare(Mention mention1, Mention mention2, SpanComparator spanComparator, int maximumComparisonDepth, int depth) {
        if ((mention1 instanceof SlotMention) & (mention2 instanceof SlotMention)) {
            return compare((SlotMention) mention1, (SlotMention) mention2, spanComparator, maximumComparisonDepth, depth);
        } else {
            throw new ClassCastException("Cannot support comparison between a SlotMention and a non-SlotMention");
        }
    }

    /**
     * Compare two SlotMentions. In order to be identical (and return 0), they must have the same mention name, and the identical slot values
     * 
     * @param slotMention1
     * @param slotMention2
     * @param maximumComparisonDepth
     * @param depth
     * @param spanComparator
     * @param compareLinkedAnnotationSpans
     * @return
     */
    @Override
    public int compare(SlotMention slotMention1, SlotMention slotMention2, SpanComparator spanComparator, int maximumComparisonDepth,
            int depth) {
        if (slotMention1.getSlotValues().size() == slotMention2.getSlotValues().size()) {
            /* quick check to make sure each slot mention has the same number of slot values */
            if (hasEquivalentMentionNames(slotMention1, slotMention2)) {
                for (Object slotValue : slotMention1.getSlotValues()) {
                    boolean objectHasMatch = false;
                    for (Object svToCompare : slotMention2.getSlotValues()) {
                    	if (svToCompare instanceof String) {
	                        if (((String) slotValue).equalsIgnoreCase((String)svToCompare)) {
	                            objectHasMatch = true;
	                        }
                    	}
                    	else {
	                        if (slotValue.equals(svToCompare)) {
	                            objectHasMatch = true;
	                        }
                    	}
                    }
                    if (!objectHasMatch) {
                        /* if they do not match, then simply return the comparison between their single line representation strings */
                        return slotMention1.getSingleLineRepresentation().compareTo(slotMention2.getSingleLineRepresentation())
                                * MULTIPLIER;
                    }
                }
                /* if we get to this point, then all slot values have a match, so return 0 */
                return 0;
            } else {
                /* the sm's have different mention names */
                return slotMention1.getSingleLineRepresentation().compareTo(slotMention2.getSingleLineRepresentation()) * MULTIPLIER;
            }
        } else {
            /* the sm's have unequal number of slot values */
            return slotMention1.getSingleLineRepresentation().compareTo(slotMention2.getSingleLineRepresentation()) * MULTIPLIER;
        }

    }

}
