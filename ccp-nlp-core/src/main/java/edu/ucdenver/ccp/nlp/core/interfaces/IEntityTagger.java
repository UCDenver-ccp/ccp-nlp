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

package edu.ucdenver.ccp.nlp.core.interfaces;

import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * An interface for a named entity tagger
 * 
 * @author William A Baumgartner Jr
 * 
 */
public interface IEntityTagger extends ITagger {

	/**
	 * Given some input text and a document ID, return a list of TextAnnotation objects representing named entities
	 * within the input text.
	 * 
	 * @param inputText
	 * @param documentID
	 * @return
	 */
	public List<TextAnnotation> getEntitiesFromText(String inputText, String documentID);
	public void shutdown();

	// public List<TextAnnotation> getEntitiesFromText(String inputText);

}
