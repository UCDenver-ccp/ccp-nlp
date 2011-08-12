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

import edu.ucdenver.ccp.nlp.core.mention.Mention;

/**
 * The IdenticalMentionComparator enforces a strict comparison of mention names. The mention names must be identical
 * (although it is not case-sensitive).
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class IdenticalMentionComparator extends MentionComparator {

	/**
	 * Compare the mention names for the two mentions. If they are identical, return true, else return false;
	 */
	public boolean hasEquivalentMentionNames(Mention mention1, Mention mention2) {
		return mention1.getMentionName().equalsIgnoreCase(mention2.getMentionName());
	}

}
