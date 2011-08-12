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

package edu.ucdenver.ccp.nlp.core.annotation.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AnnotationMetadata {

	List<AnnotationMetadataProperty> metadataProperties;

	private boolean isTruePositive;

	private boolean isFalsePositive;

	private boolean isFalseNegative;

	private String openDMAPPattern;

	private int openDMAPPatternID;
	
	private String annotationComment;

    static Logger logger = Logger.getLogger(AnnotationMetadata.class);

	public AnnotationMetadata() {
		metadataProperties = new ArrayList<AnnotationMetadataProperty>();
	}

	public void addMetadataProperty(AnnotationMetadataProperty metadataProperty) {
		metadataProperties.add(metadataProperty);

		if (metadataProperty instanceof EvaluationResultProperty) {
			if (isTruePositive | isFalsePositive | isFalseNegative) {
				logger.warn("The evalutation result property for this annotation has already been set. Attempting to overwrite TP:"
						+ isTruePositive + " FP:" + isFalsePositive + " FN:" + isFalseNegative + " with "
						+ metadataProperty.getClass().getName());
			} else {
				if (metadataProperty instanceof TruePositiveProperty) {
					isTruePositive = true;
				} else if (metadataProperty instanceof FalsePositiveProperty) {
					isFalsePositive = true;
				} else if (metadataProperty instanceof FalseNegativeProperty) {
					isFalseNegative = true;
				} else {
					logger.error("EvaluationResultProperty: " + metadataProperty.getClass().getName() + " not yet handled.");
				}
			}
		} else if (metadataProperty instanceof OpenDMAPPatternProperty) {
			OpenDMAPPatternProperty prop = (OpenDMAPPatternProperty) metadataProperty;
			openDMAPPattern = prop.getPattern();
			openDMAPPatternID = prop.getPatternID();
		} else if (metadataProperty instanceof AnnotationCommentProperty) {
			AnnotationCommentProperty prop = (AnnotationCommentProperty) metadataProperty;
			annotationComment = prop.getComment();
		}
	}

	public boolean isTruePositive() {
		return isTruePositive;
	}

	public boolean isFalsePositive() {
		return isFalsePositive;
	}

	public boolean isFalseNegative() {
		return isFalseNegative;
	}

	public List<AnnotationMetadataProperty> getMetadataProperties() {
		return metadataProperties;
	}

	public void setMetadataProperties(List<AnnotationMetadataProperty> metadataProperties) {
		this.metadataProperties = metadataProperties;
	}

	public String getOpenDMAPPattern() {
		return openDMAPPattern;
	}

	public int getOpenDMAPPatternID() {
		return openDMAPPatternID;
	}
	
	public String getAnnotationComment() {
		return annotationComment;
	}
}
