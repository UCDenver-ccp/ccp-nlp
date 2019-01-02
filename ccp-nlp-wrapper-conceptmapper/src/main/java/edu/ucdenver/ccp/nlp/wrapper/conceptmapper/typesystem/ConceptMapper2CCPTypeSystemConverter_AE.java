/*
 * ConceptMapper2CCPTypeSystemConverter.java
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

package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.typesystem;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.OntologyTerm;

/**
 * Converts ConceptMapper OntologyTerm annotations to a corresponding
 * CCPTextAnnotation object.
 * 
 * @author Bill Baumgartner
 * 
 */
public class ConceptMapper2CCPTypeSystemConverter_AE extends JCasAnnotator_ImplBase {

	public final static String PARAM_ADD_CANON_SLOT = "addCanonSlot";
	@ConfigurationParameter(mandatory = false, description = "True if you want a slot with the canonical name")
	private boolean addCanonSlot = false;

	/**
	 * Cycle through all OntologyTerms and TokenAnnotations and converts to
	 * CCPTextAnnotations. OntologyTerm and TokenAnnotation annotations are
	 * removed from the CAS once converted.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		/* Convert OntologyTerm annotations */
		List<CCPTextAnnotation> annotations2add = new ArrayList<CCPTextAnnotation>();
		List<Annotation> annotations2remove = new ArrayList<Annotation>();
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(OntologyTerm.type)
				.iterator(); annotIter.hasNext();) {
			OntologyTerm ot = (OntologyTerm) annotIter.next();
			CCPTextAnnotation ccpTA = CCPConceptMapperTypeSystemConverter_Util.convertOntologyTerm(ot, jcas,
					addCanonSlot);
			if (ccpTA != null) {
				annotations2add.add(ccpTA);
				annotations2remove.add(ot);
			}
		}

		/* Now convert token annotations */
		int tokenNumber = 0;
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(TokenAnnotation.type)
				.iterator(); annotIter.hasNext();) {
			TokenAnnotation token = (TokenAnnotation) annotIter.next();
			CCPTextAnnotation ccpToken = CCPConceptMapperTypeSystemConverter_Util.convertToken(token, jcas,
					tokenNumber);
			tokenNumber++;
			if (ccpToken != null) {
				annotations2add.add(ccpToken);
				annotations2remove.add(token);
			}
		}

		for (CCPTextAnnotation ccpTA : annotations2add) {
			ccpTA.addToIndexes();
		}

		for (Annotation annot : annotations2remove) {
			annot.removeFromIndexes();
		}
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ConceptMapper2CCPTypeSystemConverter_AE.class, tsd,
				PARAM_ADD_CANON_SLOT, false);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
			boolean addCanonicalSlot) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ConceptMapper2CCPTypeSystemConverter_AE.class, tsd,
				PARAM_ADD_CANON_SLOT, addCanonicalSlot);
	}
}
