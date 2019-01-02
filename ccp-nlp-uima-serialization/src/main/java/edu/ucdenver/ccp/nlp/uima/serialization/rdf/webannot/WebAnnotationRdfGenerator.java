package edu.ucdenver.ccp.nlp.uima.serialization.rdf.webannot;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

import edu.ucdenver.ccp.common.digest.DigestUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.AnnotationRdfGenerator;
import edu.ucdenver.ccp.nlp.uima.serialization.rdf.UriFactory;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

/**
 * Generates RDF representing annotations using the W3C Web Annotation standard.
 * https://www.w3.org/TR/annotation-model/
 */
public class WebAnnotationRdfGenerator implements AnnotationRdfGenerator {

	private final WebAnnotationSelectorType selectorType;

	public WebAnnotationRdfGenerator(WebAnnotationSelectorType selectorType) {
		this.selectorType = selectorType;
	}

	@Override
	public Collection<? extends Statement> generateRdf(AnnotationDataExtractor annotationDataExtractor,
			Annotation annotation, UriFactory uriFactory, URI documentUri, String documentText) {
		List<Statement> stmts = new ArrayList<Statement>();

		URI bodyUri = uriFactory.getResourceUri(annotationDataExtractor, annotation);

		/*
		 * the bodyUri can be null, for example in the case of the document
		 * annotation since we are not interested in representing it in the RDF
		 * model
		 */
		if (bodyUri != null) {
			List<Span> spans = annotationDataExtractor.getAnnotationSpans(annotation);
			Collections.sort(spans, Span.ASCENDING());
			String annotationKey = annotationDataExtractor.getAnnotationType(annotation) + "_" + documentUri + "_"
					+ spans.toString();
			String annotationDigest = DigestUtil.getBase64Sha1Digest(annotationKey);

			URIImpl annotationUri = new URIImpl(DataSource.KABOB.longName() + "lice/A_" + annotationDigest);

			URIImpl targetUri = new URIImpl(DataSource.KABOB.longName() + "lice/SR_" + annotationDigest);

			/* annotationInstance --rdf:type--> oa:Annotation */
			stmts.add(new StatementImpl(annotationUri, RDF.TYPE, WebAnnotationClass.ANNOTATION.uri()));
			/* annotationInstance --oa:hasBody--> body */
			stmts.add(new StatementImpl(annotationUri, WebAnnotationProperty.HAS_BODY.uri(), bodyUri));
			/* annotationInstance --oa:hasTarget--> targetInstance */
			stmts.add(new StatementImpl(annotationUri, WebAnnotationProperty.HAS_TARGET.uri(), targetUri));
			/* targetInstance --rdf:type--> oa:SpecificResource */
			stmts.add(new StatementImpl(targetUri, RDF.TYPE, WebAnnotationClass.SPECIFIC_RESOURCE.uri()));
			/* targetInstance --oa:hasSource--> documentIri */
			stmts.add(new StatementImpl(targetUri, WebAnnotationProperty.HAS_SOURCE.uri(), documentUri));

			stmts.addAll(selectorType.getStatements(targetUri, documentUri,
					annotationDataExtractor.getAnnotationSpans(annotation), documentText));
		}

		return stmts;
	}

	public static class TextPositionWebAnnotationRdfGenerator extends WebAnnotationRdfGenerator {

		public TextPositionWebAnnotationRdfGenerator() {
			super(WebAnnotationSelectorType.TEXT_POSITION);
		}

	}
}
