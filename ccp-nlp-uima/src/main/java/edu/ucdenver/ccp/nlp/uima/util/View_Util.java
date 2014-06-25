package edu.ucdenver.ccp.nlp.uima.util;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

/**
 * Utility class for working with UIMA views
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class View_Util {

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
	 * then the main jcas itself is returned. If the view name is something other than "default"
	 * then the correct view is returned, or created then returned if it doesn't already exist.
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
