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

package edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.view;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.core.uima.util.View;

/**
 * Abstract implementation of an analysis engine that copies the document text from one view to
 * another. The abstract transformText() method is use to convert the text before it is stored in
 * the destination view. The source view must exist prior to using this analysis engine.
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class ViewConverter_AE extends JCasAnnotator_ImplBase {
	private static final Logger logger = Logger.getLogger(ViewConverter_AE.class);
	public static String PARAM_SOURCE_VIEW_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			ViewConverter_AE.class, "sourceViewName");

	@ConfigurationParameter(mandatory = true)
	private String sourceViewName;

	public static String PARAM_DESTINATION_VIEW_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			ViewConverter_AE.class, "destinationViewName");

	@ConfigurationParameter(mandatory = false, defaultValue = "default")
	private String destinationViewName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			JCas sourceView = jcas.getView(sourceViewName);
			JCas destinationView = getDestinationView(jcas);
			convertView(sourceView, destinationView);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * This method is used by instantiations of this class to convert from the source view to the
	 * destination view
	 * 
	 * @param sourceView
	 * @param destinationView
	 */
	protected abstract void convertView(JCas sourceView, JCas destinationView) throws AnalysisEngineProcessException;

	/**
	 * This method returns the proper destination view. If the destination view name is "default"
	 * then the main jcas itself is returned. If the destination view name is something other than
	 * "default" then the correct view is returned, or created then returned if it doesn't already
	 * exist.
	 * 
	 * @return
	 * @throws CASException
	 */
	private JCas getDestinationView(JCas jcas) throws CASException {
		if (destinationViewName.equalsIgnoreCase(View.DEFAULT.name()))
			destinationViewName = View.DEFAULT.viewName();
		return getView(jcas, destinationViewName);
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
	private JCas getView(JCas jcas, String viewName) throws CASException {
		for (Iterator<JCas> viewIter = jcas.getViewIterator(); viewIter.hasNext();) {
			JCas view = viewIter.next();
			if (view.getViewName().equals(viewName))
				return view;
		}
		/* View does not already exist so create it */
		return jcas.createView(viewName);
	}

}
