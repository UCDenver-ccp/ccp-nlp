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

package edu.ucdenver.ccp.nlp.core.document;

/**
 * An interface that defines some document section identifiers.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public interface DocumentSectionTypes {

	public static final String OTHER_NAME = "other";

	public static final int OTHER_ID = 0;

	public static final String TITLE_NAME = "title";

	public static final int TITLE_ID = 1;

	public static final String ABSTRACT_NAME = "abstract";

	public static final int ABSTRACT_ID = 2;

}
