/*
 * AnnotationGroup.java
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;

/**
 * Used by the annotation comparison machinery, the <code>AnnotationGroup</code> provides a means for clustering groups
 * of annotations together. For example, if you are comparing a tool that outputs both proteins and genes to a gold
 * standard corpus that includes only proteins, you may want to group together the proteins and genes from the tool into
 * a single annotation group to see how performance is affected.
 * 
 * @author Bill Baumgartner
 * 
 */
public class AnnotationGroup {

	private static Logger logger = Logger.getLogger(AnnotationGroup.class);

	private Integer groupID;
	private Integer annotationSetID;
	private Integer annotatorID;

	private List<String> annotationTypeList;
	private List<String> annotationTypeRegexList;

	public AnnotationGroup() {
		this.groupID = null;
		this.annotationSetID = null;
		this.annotatorID = null;
		this.annotationTypeList = new ArrayList<String>();
		this.annotationTypeRegexList = new ArrayList<String>();
	}
	
	public AnnotationGroup(int groupID, int annotatorID, int annotationSetID) {
		this();
		this.groupID = groupID;
		this.annotationSetID = annotationSetID;
		this.annotatorID = annotatorID;
	}

	/* returns true if all fields in the AnnotationGroup are non-null */
	public boolean isValid() {
		if (groupID != null && annotationSetID != null && annotatorID != null
				&& (annotationTypeList.size() != 0 || annotationTypeRegexList.size() != 0)) {
			return true;
		} else {
			logger.warn("Invalid AnnotationGroup detected.");
			if (groupID == null) {
				logger.warn("Reason: No group ID has been assigned.");
			}
			if (annotationSetID == null) {
				logger.warn("Reason: No annotation set ID has been assigned.");
			}
			if (annotatorID == null) {
				logger.warn("Reason: No annotator ID has been assigned.");
			}
			if (annotationTypeList.size() == 0 & annotationTypeRegexList.size() == 0) {
				logger.warn("Reason: No annotation types have been assigned.");
			}

			return false;
		}
	}

	/**
	 * Returns true if this input TextAnnotation object meets the criteria to be a member of this annotation group
	 * 
	 * @param ta
	 * @return
	 */
	public boolean hasMemberAnnotation(TextAnnotation ta) {

		String annotatorID = ta.getAnnotator().getAnnotatorID();
		Set<Integer> annotationSetIDs = ta.getAnnotationSetIDs();
		String classMentionType = ta.getClassMention().getMentionName();

		if (!annotatorID.equals(this.annotatorID)) {
			return false;
		}

		if (!annotationSetIDs.contains(annotationSetID)) {
			return false;
		}

		/*
		 * we check for explicit types if there are any. if there is a match, return true. If not, then check the regex
		 * matches. If there is an explicit type mentioned then no regexes get checked.
		 */
		if (annotationTypeList.size() > 0) {
			if (annotationTypeList.contains(classMentionType)) {
				return true;
			}
		} else {
			for (String regex : annotationTypeRegexList) {
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(classMentionType);
				if (m.matches()) {
					return true;
				}
			}
		}
		return false;
	}

	public Integer getAnnotationSetID() {
		return annotationSetID;
	}

	public void setAnnotationSetID(Integer annotationSetID) {
		this.annotationSetID = annotationSetID;
	}

	public List<String> getAnnotationTypeList() {
		return annotationTypeList;
	}

	public void setAnnotationTypeList(List<String> annotationTypeList) {
		this.annotationTypeList = annotationTypeList;
	}

	public List<String> getAnnotationTypeRegexList() {
		return annotationTypeRegexList;
	}

	public void setAnnotationTypeRegexList(List<String> annotationTypeRegexList) {
		this.annotationTypeRegexList = annotationTypeRegexList;
	}

	public void addAnnotationType(String type) {
		this.annotationTypeList.add(type);
	}

	public void addAnnotationTypeRegex(String type) {
		this.annotationTypeRegexList.add(type);
	}

	public Integer getAnnotatorID() {
		return annotatorID;
	}

	public void setAnnotatorID(Integer annotatorID) {
		this.annotatorID = annotatorID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	@Override
	public String toString() {
		String typeStr = "";
		String typeRegexStr = "";
		for (String type : this.annotationTypeList) {
			typeStr += (type + ", ");
		}
		if (typeStr.length() > 0) {
			typeStr = typeStr.substring(0, typeStr.lastIndexOf(","));
		}

		for (String typeRegex : annotationTypeRegexList) {
			typeRegexStr += (typeRegex + ", ");
		}
		if (typeRegexStr.length() > 0) {
			typeRegexStr = typeRegexStr.substring(0, typeRegexStr.lastIndexOf(","));
		}

		String outputTypeStr = null;
		if (typeStr.length() > 0) {
			outputTypeStr = " Type(s): " + typeStr;
		} else {
			outputTypeStr = " TypeRegex(s): " + typeRegexStr;
		}

		return ("Annotation Group: ID: " + this.groupID + " Annotator: " + this.getAnnotatorID() + " AnnotationSet: "
				+ this.getAnnotationSetID() + outputTypeStr);
	}

	public void print(PrintStream ps) {
		ps.println(this.toString());
	}

}
