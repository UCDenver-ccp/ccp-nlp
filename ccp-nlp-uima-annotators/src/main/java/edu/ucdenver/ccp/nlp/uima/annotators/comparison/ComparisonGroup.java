/*
 * ComparisonGroup.java
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

package edu.ucdenver.ccp.nlp.uima.annotators.comparison;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Used by the annotation comparison machinery, the <code>ComparisonGroup</code> provides a means for clustering groups
 * of <code>AnnotationGroups</code> together. This allows an extra layer of flexibility when combining annotations to be
 * compared.
 */
public class ComparisonGroup {
	private static Logger logger = Logger.getLogger(ComparisonGroup.class);
	private boolean isGoldStandard = false;
	private String description;
	private List<Integer> annotationGroupMembersList;
	private Integer ID;

	public ComparisonGroup() {
		this.isGoldStandard = false;
		this.description = null;
		this.annotationGroupMembersList = new ArrayList<Integer>();
	}
	
	public ComparisonGroup(boolean isGoldStandard, String description, int id) {
		this.isGoldStandard = isGoldStandard;
		this.description = description;
		annotationGroupMembersList = new ArrayList<Integer>();
		annotationGroupMembersList.add(id);
		ID = id;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer id) {
		ID = id;
	}

	/* ComparisonGroup is valid as long as it has at least one AnnotationGroup member associated with it */
	public boolean isValid() {
		if (annotationGroupMembersList.size() != 0 && ID != null) {
			return true;
		} else {
			logger.warn("Invalid ComparisonGroup detected.");
			if (ID == null) {
				logger.warn("Reason: No ComparisonGroup has been assigned (This should happen automatically in the code.");
			}
			if (annotationGroupMembersList.size() == 0) {
				logger.warn("Reason: No AnnotationGroups have been assigned to this ComparisonGroup");
			}
			return false;
		}
	}

	/**
	 * Returns true if the input AnnotationGroup is a member of this ComparisonGroup.
	 * 
	 * @param ag
	 * @return
	 */
	public boolean hasMemberAnnotationGroup(AnnotationGroup ag) {
		if (annotationGroupMembersList.contains(ag.getGroupID())) {
			return true;
		} else {
			return false;
		}
	}

	public List<Integer> getAnnotationGroupList() {
		return annotationGroupMembersList;
	}

	public void setAnnotationGroupList(List<Integer> annotationGroupMembersList) {
		this.annotationGroupMembersList = annotationGroupMembersList;
	}

	public void addAnnotationGroup(Integer groupId) {
		this.annotationGroupMembersList.add(groupId);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isGoldStandard() {
		return isGoldStandard;
	}

	public void setGoldStandard(boolean isGoldStandard) {
		this.isGoldStandard = isGoldStandard;
	}

	@Override
	public String toString() {
		String membersStr = "";
		for (Integer groupID : this.annotationGroupMembersList) {
			membersStr += (groupID + ", ");
		}
		membersStr = membersStr.substring(0, membersStr.lastIndexOf(","));
		return ("ComparisonGroup: isGold=" + this.isGoldStandard + " Annotation_Group_Members=[" + membersStr + "] Description: " + this.description);
	}

	public void print(PrintStream ps) {
		ps.println(this.toString());
	}

}
