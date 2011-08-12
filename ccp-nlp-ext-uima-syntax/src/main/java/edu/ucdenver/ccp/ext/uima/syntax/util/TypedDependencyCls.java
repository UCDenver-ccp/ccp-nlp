/*
 * TypedDependencyCls.java
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

/**
 * This class stores information contained in the Stanford Parser typed dependency output.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class TypedDependencyCls {

	private int governorTokenNum;

	private int dependentTokenNum;

	private String relation;

	public TypedDependencyCls(int governorToken, int dependentToken, String relation) {
		this.dependentTokenNum = dependentToken;
		this.governorTokenNum = governorToken;
		this.relation = relation;
	}

	public int getDependentTokenNum() {
		return dependentTokenNum;
	}

	public void setDependentTokenNum(int dependentTokenNum) {
		this.dependentTokenNum = dependentTokenNum;
	}

	public int getGovernerTokenNum() {
		return governorTokenNum;
	}

	public void setGovernerTokenNum(int governorTokenNum) {
		this.governorTokenNum = governorTokenNum;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String toString() {
		return "GOV: " + governorTokenNum + "  DEP: " + dependentTokenNum + "  REL: " + relation;
	}

}
