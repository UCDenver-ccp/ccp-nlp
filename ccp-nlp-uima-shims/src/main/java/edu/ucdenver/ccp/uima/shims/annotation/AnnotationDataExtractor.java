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
package edu.ucdenver.ccp.uima.shims.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2016 Regents of the University of Colorado
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


import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * Base class that combines several interfaces useful for extracting information from UIMA
 * {@link Annotation} instances.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class AnnotationDataExtractor implements AnnotationTypeExtractor, AnnotationSpanExtractor{

	/**
	 * The {@link AnnotationTypeExtractor} to use for extracting annotation type information
	 */
	private final AnnotationTypeExtractor typeExtractor;

	/**
	 * The {@link AnnotationSpanExtractor} to use for extracting annotation span information
	 */
	private final AnnotationSpanExtractor spanExtractor;

	/**
	 * Consolidates several interfaces useful from extracting information from UIMA
	 * {@link Annotation} instances
	 * 
	 * @param typeExtractor
	 *            The {@link AnnotationTypeExtractor} to use for extracting annotation type
	 *            information
	 * @param spanExtractor
	 *            The {@link AnnotationSpanExtractor} to use for extracting annotation span
	 *            information
	 */
	public AnnotationDataExtractor(AnnotationTypeExtractor typeExtractor, AnnotationSpanExtractor spanExtractor) {
		this.typeExtractor = typeExtractor;
		this.spanExtractor = spanExtractor;
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.shim.annotation.uchsc.ccp.uima.shim.AnnotationTypeExtractor#getAnnotationType(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getAnnotationType(Annotation annotation) {
		return typeExtractor.getAnnotationType(annotation);
	}

	/**
	 * @see AnnotationSpanExtractor#getAnnotationSpans(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<Span> getAnnotationSpans(Annotation annotation) {
		return spanExtractor.getAnnotationSpans(annotation);
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationSpanExtractor#getCoveredText(org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String getCoveredText(Annotation annotation) {
		return spanExtractor.getCoveredText(annotation);
	}

}
