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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Interface for modifying (decorating) annotations
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * @param <T>
 *            the object decorating the annotation, e.g. it may be a Lemma decorating a token
 *            annotation
 * 
 */
public interface AnnotationDecorator<T> {

	/**
	 * Creates a new annotation in the input JCas using the specified type
	 * 
	 * @param jcas
	 * @param type
	 * @param span
	 * @return
	 */
	public Annotation newAnnotation(JCas jcas, String type, Span span);

	/**
	 * @param inputAnnotation
	 *            represents the {@link Annotation} that was acted upon to generate an attribute
	 *            that needs to be assigned. In the example explained below this would represent one
	 *            of the initial token annotations.
	 * @param annotationDataExtractor
	 *            implementation of {@link AnnotationDataExtractor} to use to extract information
	 *            from the inputAnnotation
	 * @return a reference to the {@link Annotation} to decorate with attributes. This
	 *         {@link Annotation} may be a new annotation created by the {@link AnnotationDecorator}
	 *         , but it could also simply be an annotation that already exists. <br>
	 * <br>
	 *         Let's use part of speech tagging as an example. A POS Tagger component typically
	 *         takes token annotations as input. The component then determines one or more
	 *         part-of-speech tags to assign to a given token. Depending on the type system being
	 *         used, the output of the POS tagger may involve creating a new annotation called
	 *         POSToken that has a field for storing the POS tag, or it may involve simply assigning
	 *         the POS tag to the already present token annotation. For the former, the
	 *         {@link AnnotationDecorator} implementation that decorates annotations with POS tags
	 *         would first create a new POSToken annotation and then assign it the POS tag for the
	 *         input token. For the latter case (when the POS tag is simply added to the
	 *         pre-existing token annotation) this method would simply return the pre-existing token
	 *         annotation, i.e. the inputAnnotation.
	 */
	public Annotation getAnnotationToDecorate(Annotation inputAnnotation,
			AnnotationDataExtractor annotationDataExtractor);

	/**
	 * @param annotation
	 * @param attributeType
	 * @param attribute
	 */
	public void decorateAnnotation(Annotation annotation, String attributeType, T attribute);

	/**
	 * @param annotation
	 * @param attributeType
	 * @return
	 */
	public List<T> extractAttribute(Annotation annotation, String attributeType);

}
