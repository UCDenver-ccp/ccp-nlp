/*
 * TypedDependencyCls_Util.java
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

package edu.ucdenver.ccp.ext.uima.syntax.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a general utility class for dealing with <code>TypedDependencyCls</code> objects.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class TypedDependencyCls_Util {

	/**
	 * Parses a string taking the form of "gov:# dep:# rel:relation" and returns a <code>TypedDependencyCls</code>.
	 * 
	 * @return
	 */
	public static TypedDependencyCls parseTypedDependencyClsString(String tdStr) throws InvalidTypedDependencyException {
		int govToken = -1;
		int depToken = -1;
		String relation = "";

		Pattern govPattern = Pattern.compile("gov:([\\d]+)");
		Matcher matcher = govPattern.matcher(tdStr);

		if (matcher.find()) {
			govToken = Integer.parseInt(matcher.group(1));
		} else {
			System.err.println("Invalid Typed Dependency String. Missing governor token.");
			throw new InvalidTypedDependencyException();
		}

		Pattern depPattern = Pattern.compile("dep:([\\d]+)");
		matcher = depPattern.matcher(tdStr);

		if (matcher.find()) {
			depToken = Integer.parseInt(matcher.group(1));
		} else {
			System.err.println("Invalid Typed Dependency String. Missing dependent token.");
			throw new InvalidTypedDependencyException();
		}

		Pattern relPattern = Pattern.compile("rel:([\\w]+)");
		matcher = relPattern.matcher(tdStr);

		if (matcher.find()) {
			relation = matcher.group(1);
		} else {
			System.err.println("Invalid Typed Dependency String. Missing relation.");
			throw new InvalidTypedDependencyException();
		}

		return new TypedDependencyCls(govToken, depToken, relation);
	}

}
