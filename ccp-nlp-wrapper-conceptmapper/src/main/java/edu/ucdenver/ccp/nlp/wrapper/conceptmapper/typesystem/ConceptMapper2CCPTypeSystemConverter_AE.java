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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.conceptMapper.OntologyTerm;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * Converts ConceptMapper OntologyTerm annotations to a corresponding CCPTextAnnotation object.
 * 
 * @author Bill Baumgartner
 * 
 */
public class ConceptMapper2CCPTypeSystemConverter_AE extends JCasAnnotator_ImplBase {

	/**
	 * Cycle through all OntologyTerms and TokenAnnotations and converts to CCPTextAnnotations.
	 * OntologyTerm and TokenAnnotation annotations are removed from the CAS once converted.
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		/* Convert OntologyTerm annotations */
		List<CCPTextAnnotation> annotations2add = new ArrayList<CCPTextAnnotation>();
		List<Annotation> annotations2remove = new ArrayList<Annotation>();
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(OntologyTerm.type)
				.iterator(); annotIter.hasNext();) {
			OntologyTerm ot = (OntologyTerm) annotIter.next();
			CCPTextAnnotation ccpTA = CCPConceptMapperTypeSystemConverter_Util.convertOntologyTerm(ot, jcas);
			if (ccpTA != null) {
				annotations2add.add(ccpTA);
				annotations2remove.add(ot);
			}
		}

		/* Now convert token annotations */
		for (FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(TokenAnnotation.type)
				.iterator(); annotIter.hasNext();) {
			int tokenNumber = 0;
			TokenAnnotation token = (TokenAnnotation) annotIter.next();
			CCPTextAnnotation ccpToken = CCPConceptMapperTypeSystemConverter_Util
					.convertToken(token, jcas, tokenNumber);
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
}
