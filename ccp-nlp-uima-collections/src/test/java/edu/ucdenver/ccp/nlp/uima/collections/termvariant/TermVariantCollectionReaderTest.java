/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.uima.collections.termvariant;

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
				if (ccpTA.getBegin() == 0 & ccpTA.getEnd() == 11) {
					seenSentence1 = true;
				} else if (ccpTA.getBegin() == 12 & ccpTA.getEnd() == 23) {
					seenSentence2 = true;
				}
			} else if (ccpTA.getClassMention().getMentionName().equals("GO:000456") & ccpTA.getBegin() == 0
					& ccpTA.getEnd() == 11) {
				seenGO1 = true;
			} else if (ccpTA.getClassMention().getMentionName().equals("GO:352059") & ccpTA.getBegin() == 12
					& ccpTA.getEnd() == 23) {
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
		assertEquals(4, jCas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size());
		taList = JCasUtil.select(jCas, CCPTextAnnotation.class);

		seenSentence1 = false;
		boolean seenCHEBI1 = false;
		seenSentence2 = false;
		boolean seenCHEBI2 = false;

		for (CCPTextAnnotation ccpTA : taList) {
			if (ccpTA.getClassMention().getMentionName().equals(ClassMentionType.SENTENCE.typeName())) {
				if (ccpTA.getBegin() == 0 & ccpTA.getEnd() == 11) {
					seenSentence1 = true;
				} else if (ccpTA.getBegin() == 12 & ccpTA.getEnd() == 23) {
					seenSentence2 = true;
				}
			} else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:0942") & ccpTA.getBegin() == 0
					& ccpTA.getEnd() == 11) {
				seenCHEBI1 = true;
			} else if (ccpTA.getClassMention().getMentionName().equals("CHEBI:0283") & ccpTA.getBegin() == 12
					& ccpTA.getEnd() == 23) {
				seenCHEBI2 = true;
			}
		}

		assertTrue(seenSentence1);
		assertTrue(seenSentence2);
		assertTrue(seenCHEBI1);
		assertTrue(seenCHEBI2);

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
