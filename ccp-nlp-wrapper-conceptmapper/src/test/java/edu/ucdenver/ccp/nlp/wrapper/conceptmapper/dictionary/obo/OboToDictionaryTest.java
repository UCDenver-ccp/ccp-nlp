/**
 * 
 */
package edu.ucdenver.ccp.nlp.wrapper.conceptmapper.dictionary.obo;

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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineTrim;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ShowWhiteSpace;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil;
import edu.ucdenver.ccp.datasource.fileparsers.obo.OntologyUtil.SynonymType;

/**
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class OboToDictionaryTest extends DefaultTestCase {

	private static final String SAMPLE_SO_OBO_FILE_NAME = "sample.so.obo";
	private static final String SAMPLE_CL_OBO_FILE_NAME = "sample.cl.obo";

	@Test
	public void testExactSynonymOnly_SO_OBO() throws IOException, OWLOntologyCreationException {
		File oboFile = ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_SO_OBO_FILE_NAME,
				folder.newFolder("input"));
		OntologyUtil ontUtil = new OntologyUtil(oboFile);
		File outputFile = folder.newFile("dict.xml");
		OboToDictionary.buildDictionary(outputFile, ontUtil, null, SynonymType.EXACT);
		/* @formatter:off */
		List<String> expectedLines = CollectionsUtil.createList(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
				"<synonym>", 
				"<token id=\"http://purl.obolibrary.org/obo/SO_0000012\" canonical=\"scRNA_primary_transcript\">",
				"<variant base=\"scRNA_primary_transcript\"/>", 
				"<variant base=\"scRNA primary transcript\"/>", 
				"<variant base=\"scRNA transcript\"/>",
				"<variant base=\"small cytoplasmic RNA transcript\"/>", 
				"</token>", 
				"</synonym>");
		/* @formatter:on */
		assertTrue(FileComparisonUtil.hasExpectedLines(outputFile, CharacterEncoding.UTF_8, expectedLines, null,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE, LineTrim.ON, ShowWhiteSpace.ON));
	}

	@Test
	public void testExactSynonymOnly_CL_OBO() throws IOException, OWLOntologyCreationException {
		File oboFile = ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_CL_OBO_FILE_NAME,
				folder.newFolder("input"));
		OntologyUtil ontUtil = new OntologyUtil(oboFile);
		File outputFile = folder.newFile("dict.xml");
		OboToDictionary.buildDictionary(outputFile, ontUtil, null, SynonymType.EXACT);
		/* @formatter:off */
		List<String> expectedLines = CollectionsUtil.createList(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
				"<synonym>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000000\" canonical=\"cell\">",
				"<variant base=\"cell\"/>", 
				"</token>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000009\" canonical=\"fusiform initial\">",
				"<variant base=\"fusiform initial\"/>", 
				"</token>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000041\" canonical=\"mature eosinophil\">",
				"<variant base=\"mature eosinophil\"/>", 
				"<variant base=\"mature eosinocyte\"/>", 
				"<variant base=\"mature eosinophil leucocyte\"/>", 
				"<variant base=\"mature eosinophil leukocyte\"/>", 
				"</token>", 
				"</synonym>");
		/* @formatter:on */
		assertTrue(FileComparisonUtil.hasExpectedLines(outputFile, CharacterEncoding.UTF_8, expectedLines, null,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE, LineTrim.ON, ShowWhiteSpace.ON));
	}

	@Test
	public void testIncludeAllSynonyms_CL_OBO() throws IOException, OWLOntologyCreationException {
		File oboFile = ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_CL_OBO_FILE_NAME,
				folder.newFolder("input"));
		OntologyUtil ontUtil = new OntologyUtil(oboFile);
		File outputFile = folder.newFile("dict.xml");
		OboToDictionary.buildDictionary(outputFile, ontUtil, null, SynonymType.ALL);
		/* @formatter:off */
		List<String> expectedLines = CollectionsUtil.createList(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
				"<synonym>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000000\" canonical=\"cell\">",
				"<variant base=\"cell\"/>", 
				"</token>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000009\" canonical=\"fusiform initial\">",
				"<variant base=\"fusiform initial\"/>", 
				"<variant base=\"xylem initial\"/>", 
				"<variant base=\"xylem mother cell\"/>", 
				"<variant base=\"xylem mother cell activity\"/>", 
				"</token>", 
				"<token id=\"http://purl.obolibrary.org/obo/CL_0000041\" canonical=\"mature eosinophil\">",
				"<variant base=\"mature eosinophil\"/>", 
				"<variant base=\"mature eosinocyte\"/>", 
				"<variant base=\"mature eosinophil leucocyte\"/>", 
				"<variant base=\"mature eosinophil leukocyte\"/>", 
				"<variant base=\"polymorphonuclear leucocyte\"/>", 
				"<variant base=\"polymorphonuclear leukocyte\"/>", 
				"</token>", 
				"</synonym>");
		/* @formatter:on */
		assertTrue(FileComparisonUtil.hasExpectedLines(outputFile, CharacterEncoding.UTF_8, expectedLines, null,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE, LineTrim.ON, ShowWhiteSpace.ON));
	}

	@Test
	public void testIncludeAllSynonyms_CL_OBO_WithExternalSyns() throws IOException, OWLOntologyCreationException {
		File oboFile = ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_CL_OBO_FILE_NAME,
				folder.newFolder("input"));
		OntologyUtil ontUtil = new OntologyUtil(oboFile);
		File outputFile = folder.newFile("dict.xml");

		Map<String, Set<String>> id2synsMap = new HashMap<String, Set<String>>();
		id2synsMap.put("CL:0000009", CollectionsUtil.createSet("fusi init", "fusi fusi"));
		id2synsMap.put("CL:0000041", CollectionsUtil.createSet("mat eo", "mature eos"));

		OboToDictionary.buildDictionary(outputFile, ontUtil, null, SynonymType.ALL, id2synsMap);
		/* @formatter:off */
	               List<String> expectedLines = CollectionsUtil.createList(
	                               "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
	                               "<synonym>", 
	                               "<token id=\"http://purl.obolibrary.org/obo/CL_0000000\" canonical=\"cell\">",
	                               "<variant base=\"cell\"/>", 
	                               "</token>", 
	                               "<token id=\"http://purl.obolibrary.org/obo/CL_0000009\" canonical=\"fusiform initial\">",
	                               "<variant base=\"fusiform initial\"/>", 
	                               "<variant base=\"xylem initial\"/>", 
	                               "<variant base=\"xylem mother cell\"/>", 
	                               "<variant base=\"xylem mother cell activity\"/>", 
	                               "<variant base=\"fusi init\"/>", 
	                               "<variant base=\"fusi fusi\"/>", 
	                               "</token>", 
	                               "<token id=\"http://purl.obolibrary.org/obo/CL_0000041\" canonical=\"mature eosinophil\">",
	                               "<variant base=\"mature eosinophil\"/>", 
	                               "<variant base=\"mature eosinocyte\"/>", 
	                               "<variant base=\"mature eosinophil leucocyte\"/>", 
	                               "<variant base=\"mature eosinophil leukocyte\"/>", 
	                               "<variant base=\"polymorphonuclear leucocyte\"/>", 
	                               "<variant base=\"polymorphonuclear leukocyte\"/>", 
	                               "<variant base=\"mat eo\"/>", 
	                               "<variant base=\"mature eos\"/>", 
	                               "</token>", 
	                               "</synonym>");
	               /* @formatter:on */
		assertTrue(FileComparisonUtil.hasExpectedLines(outputFile, CharacterEncoding.UTF_8, expectedLines, null,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE, LineTrim.ON, ShowWhiteSpace.ON));
	}

}
