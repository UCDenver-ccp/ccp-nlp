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
package edu.ucdenver.ccp.nlp.uima.test;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.After;
import org.junit.Before;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * provides a tsd and jcas for common UIMA tests. (Type System Description, and JCas) has a hook for
 * initializing the jcas with some annotations: initJCas() also has convenience functions for adding
 * various annotations.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class DefaultUIMATestCase extends DefaultTestCase {

	/**
	 * 
	 */
	public static final String HAS_ENTREZ_GENE_ID_SLOT_NAME = "has Entrez Gene ID";
	protected TypeSystemDescription tsd;
	protected JCas jcas;

	@Before
	public void setUp() throws Exception {
		tsd = getTypeSystem();
		jcas = JCasFactory.createJCas(tsd);
		initJCas();
	}

	/**
	 * Override to set a different type system
	 */
	protected TypeSystemDescription getTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription("edu.ucdenver.ccp.nlp.core.uima.TypeSystem");
	}

	@After
	public void tearDown() throws Exception {
		if (jcas != null) {
			jcas.release();
		}
	}

	protected abstract void initJCas() throws Exception;

	protected CCPTextAnnotation addTextAnnotationToJCas(int spanStart, int spanEnd, String classMentionName) {
		return UIMA_Annotation_Util.createCCPTextAnnotation(classMentionName, new int[] { spanStart, spanEnd }, jcas);
	}

	protected CCPTextAnnotation addSentenceAnnotationToJCas(int spanStart, int spanEnd) {
		return addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionType.SENTENCE.typeName());
	}

	protected CCPTextAnnotation addParagraphAnnotationToJCas(int spanStart, int spanEnd) {
		return addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionType.PARAGRAPH.typeName());
	}

	protected CCPTextAnnotation addGeneAnnotationToJCas(int spanStart, int spanEnd, int entrezGeneID)
			throws CASException {
		CCPTextAnnotation ccpTA = addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionType.GENE.typeName());
		UIMA_Util.addSlotValue(ccpTA.getClassMention(), HAS_ENTREZ_GENE_ID_SLOT_NAME, Integer.toString(entrezGeneID));
		return ccpTA;
	}

	protected CCPTextAnnotation addTranscriptAnnotationToJCas(int spanStart, int spanEnd, int entrezGeneID)
			throws CASException {
		CCPTextAnnotation ccpTA = addTextAnnotationToJCas(spanStart, spanEnd, ClassMentionType.TRANSCRIPT.typeName());
		UIMA_Util.addSlotValue(ccpTA.getClassMention(), HAS_ENTREZ_GENE_ID_SLOT_NAME, Integer.toString(entrezGeneID));
		return ccpTA;
	}

}
