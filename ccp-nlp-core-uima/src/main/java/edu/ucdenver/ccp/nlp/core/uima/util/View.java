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

package edu.ucdenver.ccp.nlp.core.uima.util;

import org.apache.uima.cas.CAS;

/**
 * This enum contains some commonly used UIMA view names.
 * 
 * @author Bill Baumgartner
 * 
 */
public enum View {
	DEFAULT(CAS.NAME_DEFAULT_SOFA), RAW("rawView"), XML("xmlView"), HTML("htmlView"), GOLD_STANDARD("goldStandardView");

	private final String viewName;

	private View(String viewName) {
		this.viewName = viewName;
	}

	public String viewName() {
		return viewName;
	}
	public String toString() {
		return viewName;
	}
}
