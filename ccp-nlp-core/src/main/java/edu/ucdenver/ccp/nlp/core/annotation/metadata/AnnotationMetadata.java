package edu.ucdenver.ccp.nlp.core.annotation.metadata;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
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
						+ isTruePositive
						+ " FP:"
						+ isFalsePositive
						+ " FN:"
						+ isFalseNegative
						+ " with "
						+ metadataProperty.getClass().getName());
			} else {
				if (metadataProperty instanceof TruePositiveProperty) {
					isTruePositive = true;
				} else if (metadataProperty instanceof FalsePositiveProperty) {
					isFalsePositive = true;
				} else if (metadataProperty instanceof FalseNegativeProperty) {
					isFalseNegative = true;
				} else {
					logger.error("EvaluationResultProperty: " + metadataProperty.getClass().getName()
							+ " not yet handled.");
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
