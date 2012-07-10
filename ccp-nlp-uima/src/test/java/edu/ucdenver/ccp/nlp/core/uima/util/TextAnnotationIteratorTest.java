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
package edu.ucdenver.ccp.nlp.core.uima.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class TextAnnotationIteratorTest extends DefaultUIMATestCase {

	@Override
	protected void initJCas() throws Exception {
		jcas.setDocumentText("blahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblah");
		addGeneAnnotationToJCas(0, 1, 12345);
		addGeneAnnotationToJCas(2, 3, 12345);
		addGeneAnnotationToJCas(4, 5, 12345);

		addSentenceAnnotationToJCas(6, 7);
		addSentenceAnnotationToJCas(1, 2);
	}

	@Test
	public void testTextAnnotationIter_OneInputType() throws Exception {
		assertEquals(String.format("Should be 3 gene annotations"), 3, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, ClassMentionType.GENE.typeName())).size());
	}

	@Test
	public void testTextAnnotationIter_ZeroInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas)).size());
	}

	@Test
	public void testTextAnnotationIter_NullInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, (String[]) null)).size());
	}

	@Test
	public void testTextAnnotationIter_TwoInputTypes() throws Exception {
		assertEquals(String.format("Should have 5 annotations"), 5, CollectionsUtil.createList(
				UIMA_Util.getTextAnnotationIterator(jcas, ClassMentionType.GENE.typeName(), ClassMentionType.SENTENCE.typeName())).size());
	}

}
