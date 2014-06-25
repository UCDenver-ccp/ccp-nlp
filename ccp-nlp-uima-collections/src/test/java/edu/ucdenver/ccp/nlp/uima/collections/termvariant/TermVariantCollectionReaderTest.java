package edu.ucdenver.ccp.nlp.uima.collections.termvariant;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

public class TermVariantCollectionReaderTest extends DefaultTestCase {

	private final String TEST_INPUT_FILE_NAME = "validInputFile.txt";

	@Test
	public void testCollectionReader() throws IOException, UIMAException  {
		TypeSystemDescription tsd = TypeSystemUtil.getCcpTypeSystem();

		File termVariantFile = folder.newFile("termVariant.input");
		ClassPathUtil.copyClasspathResourceToFile(getClass(), TEST_INPUT_FILE_NAME, termVariantFile);
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(TermVariantCollectionReader
				.createDefaultDescription(tsd, termVariantFile, CharacterEncoding.UTF_8));

		JCasIterable jcasIter = new JCasIterable(cr);
		assertTrue(jcasIter.hasNext());
		JCas jCas = jcasIter.next();
		assertEquals("document 1", UIMA_Util.getDocumentID(jCas));
		assertEquals(4, jCas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());
		Collection<CCPTextAnnotation> taList = JCasUtil.select(jCas, CCPTextAnnotation.class);
		boolean seenSentence1 = false;
		boolean seenGO1 = false;
		boolean seenSentence2 = false;
		boolean seenGO2 = false;

		for (CCPTextAnnotation ccpTA : taList) {
			if (ccpTA.getClassMention().getMentionName().equals(ClassMentionType.SENTENCE.typeName())) {
				if (ccpTA.getBegin() == 0 & ccpTA.getEnd() == 13) {
					seenSentence1 = true;
				} else if (ccpTA.getBegin() == 14 & ccpTA.getEnd() == 27) {
					seenSentence2 = true;
				}
			} else if (ccpTA.getClassMention().getMentionName().equals("GO:000456") & ccpTA.getBegin() == 1
					& ccpTA.getEnd() == 12) {
				seenGO1 = true;
			} else if (ccpTA.getClassMention().getMentionName().equals("GO:352059") & ccpTA.getBegin() == 15
					& ccpTA.getEnd() == 26) {
				seenGO2 = true;
			}
		}

		assertTrue(seenSentence1);
		assertTrue(seenSentence2);
		assertTrue(seenGO1);
		assertTrue(seenGO2);

		assertTrue(jcasIter.hasNext());
		jCas = jcasIter.next();

		assertEquals("document 2", UIMA_Util.getDocumentID(jCas));
		assertEquals(7, jCas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());
		taList = JCasUtil.select(jCas, CCPTextAnnotation.class);

		seenSentence1 = false;
		boolean seenCHEBI1 = false;
		seenSentence2 = false;
		boolean seenCHEBI2 = false;
		boolean seenCHEBI3 = false;
		boolean seenCHEBI4=false;

		for (CCPTextAnnotation ccpTA : taList) {
			if (ccpTA.getClassMention().getMentionName().equals(ClassMentionType.SENTENCE.typeName())) {
				if (ccpTA.getBegin() == 0 & ccpTA.getEnd() == 13) {
					seenSentence1 = true;
				} else if (ccpTA.getBegin() == 14 & ccpTA.getEnd() == 27) {
					seenSentence2 = true;
				}
			} else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:0942") & ccpTA.getBegin() == 1
					& ccpTA.getEnd() == 12) {
				seenCHEBI1 = true;
			} else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:0283") & ccpTA.getBegin() == 15
					& ccpTA.getEnd() == 26) {
				seenCHEBI2 = true;
			}else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:1234") & ccpTA.getBegin() == 29
					& ccpTA.getEnd() == 56) {
				seenCHEBI3 = true;
			}else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:5678") & ccpTA.getBegin() == 29
					& ccpTA.getEnd() == 56) {
				seenCHEBI4 = true;
			}
		}

		assertTrue(seenSentence1);
		assertTrue(seenSentence2);
		assertTrue(seenCHEBI1);
		assertTrue(seenCHEBI2);
		assertTrue(seenCHEBI3);
		assertTrue(seenCHEBI4);

	}
	
	@Test(expected=IllegalStateException.class)
	public void testWhenFileDNE() throws ResourceInitializationException {
		TypeSystemDescription tsd = TypeSystemUtil.getCcpTypeSystem();
		CollectionReaderFactory.createCollectionReader(TermVariantCollectionReader
				.createDefaultDescription(tsd, new File("this_file_does_not.exist"), CharacterEncoding.UTF_8));
	}
	
	
	@Test(expected=ResourceInitializationException.class)
	public void testWhenFileIsEmpty() throws ResourceInitializationException, IOException {
		TypeSystemDescription tsd = TypeSystemUtil.getCcpTypeSystem();
		File emptyFile = folder.newFile("empty.file");
		CollectionReaderFactory.createCollectionReader(TermVariantCollectionReader
				.createDefaultDescription(tsd, emptyFile, CharacterEncoding.UTF_8));
	}

}
