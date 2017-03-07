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
import java.util.List;
import java.util.UUID;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.rdfizer.rdf.ice.RdfUtil;
import edu.ucdenver.ccp.uima.shims.annotation.Span;

public enum WebAnnotationSelectorType {
	TEXT_POSITION {
		@Override
		public Collection<? extends Statement> getStatements(URIImpl specificResourceUri, List<Span> spans,
				String documentText) {
			List<Statement> stmts = new ArrayList<Statement>();

			for (Span span : spans) {
				URIImpl selectorUri = new URIImpl(
						DataSource.KABOB.longName() + "lice/selector_" + UUID.randomUUID().toString());
				/* selectorInstance --rdf:type--> oa:TextPositionSelector */
				stmts.add(new StatementImpl(selectorUri, RDF.TYPE, WebAnnotationClass.TEXT_POSITION_SELECTOR.uri()));
				/* specificResourceInstance --oa:hasSelector--> selectorInst. */
				stmts.add(
						new StatementImpl(specificResourceUri, WebAnnotationProperty.HAS_SELECTOR.uri(), selectorUri));
				/* selectorInstance --oa:start--> spanStartOffset */
				stmts.add(new StatementImpl(selectorUri, WebAnnotationProperty.START.uri(),
						RdfUtil.createLiteral(span.getSpanStart())));
				/* selectorInstance --oa:end--> spanEndOffset */
				stmts.add(new StatementImpl(selectorUri, WebAnnotationProperty.END.uri(),
						RdfUtil.createLiteral(span.getSpanEnd())));
			}
			return stmts;
		}
	};

	public abstract Collection<? extends Statement> getStatements(URIImpl specificResourceUri, List<Span> spans,
			String documentText);
}
