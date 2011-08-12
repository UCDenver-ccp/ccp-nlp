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

import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

public class View_Util {

	/**
	 * This enum contains some commonly used UIMA view names.
	 * 
	 * @author Bill Baumgartner
	 * 
	 */
	public static enum View {
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
	
	/**
	 * Helper method that will retrieve a view with a given name. If the view does not exist, then
	 * it is created.
	 * 
	 * @param jcas
	 * @param viewName
	 * @return
	 * @throws CASException
	 */
	public static JCas getView(JCas jcas, String viewName) throws CASException {
		for (Iterator<JCas> viewIter = jcas.getViewIterator(); viewIter.hasNext();) {
			JCas view = viewIter.next();
			if (view.getViewName().equals(viewName))
				return view;
		}
		/* View does not already exist so create it */
		return jcas.createView(viewName);
	}

	/**
	 * This method returns the proper view in the case of defaults. If the view name is "default"
	 * then the main jcas itself is returned. If the view name is something other than
	 * "default" then the correct view is returned, or created then returned if it doesn't already
	 * exist.  
	 * 
	 * @return
	 * @throws CASException
	 */
	public static JCas getViewHandleDefault(JCas jcas, String viewName) throws CASException {
		if (viewName.equalsIgnoreCase(View.DEFAULT.name()))
			viewName = View.DEFAULT.viewName();
		
		return View_Util.getView(jcas, viewName);
	}
	
}
