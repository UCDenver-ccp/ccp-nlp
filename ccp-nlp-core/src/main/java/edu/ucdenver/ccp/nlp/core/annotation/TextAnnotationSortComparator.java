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

package edu.ucdenver.ccp.nlp.core.annotation;

import java.util.Comparator;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;

/**
 * A Comparator used for sorting TextAnnotations, e.g. Collections.sort(List<TextAnnotation>, TextAnnotationSortComparator);
 * @author Bill Baumgartner
 *
 */
public class TextAnnotationSortComparator implements Comparator<TextAnnotation> {

	public int compare(TextAnnotation ta1, TextAnnotation ta2) {
		return new StrictSpanComparator().compare(ta1.getAggregateSpan(), ta2.getAggregateSpan());
	}

}
