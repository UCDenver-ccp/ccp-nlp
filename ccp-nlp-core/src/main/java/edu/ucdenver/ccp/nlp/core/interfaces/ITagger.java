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

import edu.ucdenver.ccp.nlp.core.exception.InitializationException;


/**
 * An interface for a generic tagger
 * 
 * @author Bill Baumgartner
 * 
 */
public interface ITagger {

	public static final int ENTITY_TAGGER = 0;

	public static final int POS_TAGGER = 1;

	public static final int TOKENIZER = 2;
	
	public static final int SENTENCE_DETECTOR = 3;
	
	public static final String[] TAGGER_TYPES = {"ENTITY_TAGGER","POS_TAGGER","TOKENIZER","SENTENCE_DETECTOR"};

	/**
	 * Initialization method.
	 * 
	 * @param taggerType
	 *            integer specifying the tagger type to be initialized
	 * @param args
	 *            Array of input parameters. These parameters can vary depending on the tagger type selected
	 */
	public void initialize(int taggerType, String[] args) throws InitializationException;

}
