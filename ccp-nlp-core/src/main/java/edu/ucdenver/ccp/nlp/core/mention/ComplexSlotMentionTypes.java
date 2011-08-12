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

package edu.ucdenver.ccp.nlp.core.mention;

/**
 * This interface contains some common complex slot mention types.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public interface ComplexSlotMentionTypes {

	// public static final String TOKEN_PART_OF_SPEECH = "part of speech";
	// public static final String PHRASE_TYPE = "phrase type";
	// public static final String CLAUSE_TYPE = "clause type";

	/** ComplexSlotMention Identifier - if this changes, Annotation DB must be adjusted accordingly */
	public static final String COMPLEX_SLOT_MENTION_TYPE = "complex slot mention";

	// public static final String TOKEN_TYPED_DEPENDENCIES = "typedDependencies";

	public static final String SUBSTITUTION_MUTANT_ELEMENT = "mutant element";
	public static final String SUBSTITUTION_WILDTYPE_ELEMENT = "wild type element";
	public static final String DELETION_DELETED_ELEMENT = "deleted element";
	public static final String INSERTION_INSERTED_ELEMENT = "inserted element";
	public static final String INSERTION_INSERTION_START = "insertion start";
	public static final String BIOLOGICAL_SEQUENCE_ELEMENT_POSITION_IN_SEQUENCE = "position in sequence";
	public static final String BIOLOGICAL_SUBSEQUENCE_START_ELEMENT = "start element";
	public static final String BIOLOGICAL_SUBSEQUENCE_END_ELEMENT = "end element";
}
