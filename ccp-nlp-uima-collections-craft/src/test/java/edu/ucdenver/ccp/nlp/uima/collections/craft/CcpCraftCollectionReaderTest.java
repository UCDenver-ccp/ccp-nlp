
package edu.ucdenver.ccp.nlp.uima.collections.craft;

/*
 * #%L
 * Colorado Computational Pharmacology's CRAFT-related code module
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;

/**
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class CcpCraftCollectionReaderTest {

	@Test
	public void testTextOnlyCollectionReader_CCP() throws UIMAException, IOException {
		CollectionReaderDescription crDesc = CcpCraftCollectionReader.getDescription(CraftRelease.MAIN,
				new HashSet<CraftConceptType>());

		int docCount = 0;
		for (JCas jCas : new JCasIterable(crDesc)) {
			docCount++;
			assertFalse(jCas.getDocumentText().isEmpty());
			// one DocumentAnnotation + one CCPDocumentInfo
			assertEquals(2, JCasUtil.select(jCas, TOP.class).size());
		}

		assertEquals(67, docCount);
	}

	@Test
	public void testTextOnlyCollectionReader_CCP_DevSet() throws UIMAException, IOException {
		CollectionReaderDescription crDesc = CcpCraftCollectionReader.getDescription(CraftRelease.MAIN_DEV,
				new HashSet<CraftConceptType>());

		int docCount = 0;
		for (JCas jCas : new JCasIterable(crDesc)) {
			docCount++;
			assertFalse(jCas.getDocumentText().isEmpty());
			// one DocumentAnnotation + one CCPDocumentInfo
			assertEquals(2, JCasUtil.select(jCas, TOP.class).size());
		}

		assertEquals(7, docCount);
	}

	@Test
	public void testTextOnlyCollectionReader_nullConceptTypeInput() throws UIMAException, IOException {
		CollectionReaderDescription crDesc = CcpCraftCollectionReader.getDescription(CraftRelease.MAIN, null);

		int docCount = 0;
		for (JCas jCas : new JCasIterable(crDesc)) {
			docCount++;
			assertFalse(jCas.getDocumentText().isEmpty());
			// one DocumentAnnotation + one CCPDocumentInfo
			assertEquals(2, JCasUtil.select(jCas, Annotation.class).size());
		}

		assertEquals(67, docCount);
	}

	@Test
	public void testAllConceptsCollectionReader_CCP() throws UIMAException, IOException {
		CollectionReaderDescription crDesc = CcpCraftCollectionReader.getDescription(CraftRelease.MAIN,
				EnumSet.allOf(CraftConceptType.class));

		int docCount = 0;
		for (JCas jCas : new JCasIterable(crDesc)) {
			docCount++;
			assertFalse(jCas.getDocumentText().isEmpty());
			assertTrue(JCasUtil.select(jCas, Annotation.class).size() > 1000);
		}

		assertEquals(67, docCount);
	}


	/**
	 * Metadata for the CCP XML descriptor:
	 * 
	 * <pre>
	 * 	    <name>CCP CRAFT Collection Reader</name>
	 *     	<description>This collection reader provides an entry point to process the concept annotations in the CRAFT corpus in the context of the CCP type system and the syntactic (treebank) annotations in the context of the ClearTK type system.</description>
	 *     	<version>v0.9</version>
	 *     	<vendor>UC Denver - Colorado Computational Pharmacology</vendor>
	 * </pre>
	 * 
	 * Capabilities for the CCP XML descriptor:
	 * 
	 * <pre>
	 * <capabilities>
	 *       <capability>
	 *         <inputs/>
	 *         <outputs>
	 *           <type allAnnotatorFeatures=
	"true">org.cleartk.syntax.constituent.type.TreebankNode</type>
	 *           <type allAnnotatorFeatures=
	"true">org.cleartk.syntax.constituent.type.TopTreebankNode</type>
	 *           <type allAnnotatorFeatures=
	"true">org.cleartk.syntax.constituent.type.TerminalTreebankNode</type>
	 *           <type allAnnotatorFeatures=
	"true">org.cleartk.token.type.Sentence</type>
	 *           <type allAnnotatorFeatures=
	"true">edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation</type>
	 *           <type allAnnotatorFeatures=
	"true">edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation</type>
	 *           <type allAnnotatorFeatures=
	"true">org.cleartk.token.type.Token</type>
	 *         </outputs>
	 *         <languagesSupported/>
	 *       </capability>
	 *     </capabilities>
	 * </pre>
	 * 
	 * 
	 * @throws ResourceInitializationException
	 * @throws IOException
	 * @throws SAXException
	 */
	@Ignore("Un-ignore to create an XML descriptor file for the CcpCraftCollectionReader")
	@Test
	public void writeDescriptor() throws ResourceInitializationException, IOException, SAXException {
		CollectionReaderDescription crDesc = CcpCraftCollectionReader.getDescription(CraftRelease.MAIN,
				EnumSet.allOf(CraftConceptType.class));
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(new File("CcpCraftCollectionReader.xml"),
				CharacterEncoding.UTF_8, WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		crDesc.toXML(writer);
		writer.close();

	}

}
