/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation.impl;

import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationTypeExtractor;

/**
 * Implementation of {@link AnnotationTypeExtractor} specific to the CCP type system
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpAnnotationTypeExtractor implements AnnotationTypeExtractor {

	/**
	 * Returns the class mention name for the input {@link Annotation} which must be an instance of
	 * {@link CCPTextAnnotation}
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.shim.annotation.uchsc.ccp.uima.shim.AnnotationTypeExtractor#getAnnotationType(org.apache.uima.jcas.tcas.Annotation)
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not an instance of {@link CCPTextAnnotation}
	 */
	@Override
	public String getAnnotationType(Annotation annotation) {
		if (annotation instanceof CCPTextAnnotation)
			return ((CCPTextAnnotation) annotation).getClassMention().getMentionName();
//		throw new IllegalArgumentException("Cannot return type for a non-CCPTextAnnotation annotation: "
//				+ annotation.getClass().getName());
		return null;
	}

}
