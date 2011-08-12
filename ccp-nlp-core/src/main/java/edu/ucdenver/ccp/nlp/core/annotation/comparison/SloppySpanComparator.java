/*
 * SloppySpanComprarator.java 
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

package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * 
 * For "sloppy" comparison, the spans are required to overlap by at least one character. If the spans overlap, return 0,
 * else return -1 is span1 occurs prior to span2, 1 otherwise;
 * 
 * @author Bill Baumgartner
 * 
 */
public class SloppySpanComparator extends SpanComparator {

	public SloppySpanComparator() {
		/* spansMustOverlapToMatch = true */
		super(true);
	}

	/**
	 * Compare two spans using the sloppy (overlap) match criteria.
	 */
	@Override
	public int compare(Span span1, Span span2) {
		return overlaps(span1, span2);
	}

	/**
	 * Compare two span lists using the sloppy (overlap) match criteria.
	 * 
	 * @param spanList1
	 * @param spanList2
	 * @return
	 */
	@Override
	public int compare(List<Span> spanList1, List<Span> spanList2) {
		return overlaps(spanList1, spanList2);
	}

}
