package edu.ucdenver.ccp.nlp.uima.shims;

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

import edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor;
import edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.impl.DefaultAnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.document.DocumentMetadataHandler;

/**
 * This interface provides constants for various shim default values. The {@link String} constants
 * are particularly useful when setting default values in UIMA AE configuration parameters.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public interface ShimDefaults {

	/**
	 * class name for a {@link DocumentMetadataHandler} that complies with the CCP type system (good
	 * for use as default values in AE configuration parameters)
	 */
	public static final String CCP_DOCUMENT_METADATA_HANDLER_CLASS_NAME = "edu.ucdenver.ccp.nlp.uima.shims.document.impl.CcpDocumentMetadataHandler";

	/**
	 * class for a {@link DocumentMetadataHandler} that complies with the CCP type system
	 */
	public static final Class<? extends DocumentMetadataHandler> CCP_DOCUMENT_METADATA_HANDLER_CLASS = CcpDocumentMetadataHandler.class;

	/**
	 * class name for a {@link AnnotationDataExtractor} that complies with the CCP type system (good
	 * for use as a default value in AE configuration parameters)
	 */
	public static final String CCP_ANNOTATION_DATA_EXTRACTOR_CLASS_NAME = "edu.ucdenver.ccp.nlp.uima.shims.annotation.impl.CcpAnnotationDataExtractor";

	/**
	 * class for a {@link AnnotationDataExtractor} that complies with the CCP type system
	 */
	public static final Class<? extends AnnotationDataExtractor> CCP_ANNOTATION_DATA_EXTRACTOR_CLASS = CcpAnnotationDataExtractor.class;

	/**
	 * class name for a {@link AnnotationDataExtractor} that extracts the annotation type from its
	 * class name and uses the default uima begin and end fields for span information
	 */
	public static final String DEFAULT_ANNOTATION_DATA_EXTRACTOR_CLASS_NAME = "edu.ucdenver.ccp.uima.shims.annotation.impl.DefaultAnnotationDataExtractor";

	/**
	 * class for a {@link AnnotationDataExtractor} that extracts the annotation type from its class
	 * name and uses the default uima begin and end fields for span information
	 */
	public static final Class<? extends AnnotationDataExtractor> DEFAULT_ANNOTATION_DATA_EXTRACTOR_CLASS = DefaultAnnotationDataExtractor.class;

}
