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
import java.util.Iterator;
import java.util.List;

import org.apache.uima.conceptMapper.OntologyTerm;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPTokenAnnotation;

/**
 * Converts ConceptMapper OntologyTerm annotations to a corresponding CCPTextAnnotation object.
 * 
 * @author Bill Baumgartner
 * 
 */
public class ConceptMapper2CCPTypeSystemConverter_AE extends JCasAnnotator_ImplBase {

    /**
     * Cycle through all OntologyTerms and TokenAnnotations and converts to CCPTextAnnotations. OntologyTerm and TokenAnnotation annotations are removed from
     * the CAS once converted.
     */
    public void process(JCas jcas)  {
        /* Convert OntologyTerm annotations */
        List<CCPTextAnnotation> annotations2add = new ArrayList<CCPTextAnnotation>();
        List<Annotation> annotations2remove = new ArrayList<Annotation>();
        Iterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(OntologyTerm.type).iterator();
        while (annotIter.hasNext()) {
            Object possibleAnnot = annotIter.next();
            if (possibleAnnot instanceof OntologyTerm) {
                OntologyTerm ot = (OntologyTerm) possibleAnnot;
                CCPTextAnnotation ccpTA = CCPConceptMapperTypeSystemConverter_Util.convertOntologyTerm(ot, jcas);
                if (ccpTA != null) {
                    annotations2add.add(ccpTA);
                    annotations2remove.add(ot);
                }
            } else {
                error("OntologyTerm expected but instead got " + possibleAnnot.getClass().getName());
            }
        }

        /* Now convert token annotations */
        annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(TokenAnnotation.type).iterator();
        int tokenNumber = 0;
        while (annotIter.hasNext()) {
            Object possibleAnnot = annotIter.next();
            if (possibleAnnot instanceof TokenAnnotation) {
                TokenAnnotation token = (TokenAnnotation) possibleAnnot;
                CCPTokenAnnotation ccpToken = CCPConceptMapperTypeSystemConverter_Util.convertToken(token, jcas, tokenNumber);
                tokenNumber++;
                if (ccpToken != null) {
                    annotations2add.add(ccpToken);
                    annotations2remove.add(token);
                }
            } else {
                error("TokenAnnotation expected but instead got " + possibleAnnot.getClass().getName());
            }
        }

        for (CCPTextAnnotation ccpTA : annotations2add) {
            ccpTA.addToIndexes();
        }

        for (Annotation annot : annotations2remove) {
            annot.removeFromIndexes();
        }

    }

    private void error(String message) {
        System.err.println("ERROR -- ConceptMapper2CCPTypeSystemConverter: " + message);
    }
}
