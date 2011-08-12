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

/**
 * This is an interface for comparing class mentions
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public interface ClassMentionComparator {

    /**
     * Compares two <code>ClassMention</code> objects using the specified <code>SpanComparator</code> to a user-specified depth in the mention
     * hierarchy.
     * 
     * @param classMention1
     *            a <code>ClassMention</code> to compare
     * @param classMention2
     *            the other <code>ClassMention</code> to compare
     * @param spanComparator
     *            the <code>SpanComparator</code> to use during the comparison
     * @param maximumComparisonDepth
     *            This parameter specifies how deep into the mention hierarchy the comparison should delve. A value of zero specifies that the
     *            comparison will look at only the types of the <code>ClassMention</code> objects. A value of 1 will include a comparison of the
     *            <code>ClassMention</code> type (name), but also of any <code>SlotMentions</code> and <code>ComplexSlotMentions</code>. See {@link MentionComparator} for a more detailed explanation.
     * @return 0 if the <code>ClassMention</code> objects are equal, otherwise, something other than zero
     */
    public int compare(ClassMention classMention1, ClassMention classMention2, SpanComparator spanComparator, int maximumComparisonDepth);
}
