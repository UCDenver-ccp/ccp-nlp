package edu.ucdenver.ccp.nlp.doc2txt.pmc;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineTrim;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ShowWhiteSpace;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser.Annotation;

public class CcpXmlParserTest {

	@Test
	public void test() throws IOException, SAXException {
		String expectedPlainText = ClassPathUtil.getContentsFromClasspathResource(getClass(), "/sample.txt",
				CharacterEncoding.UTF_8);

		CcpXmlParser parser = new CcpXmlParser();
		String plainText = parser.parse(new InputSource(this.getClass().getResourceAsStream("/sample_ccp.xml")));

		List<String> expectedPlainTextLines = Arrays.asList(expectedPlainText.split("\\n"));
		List<String> plainTextLines = Arrays.asList(plainText.split("\\n"));
		assertTrue(FileComparisonUtil.hasExpectedLines(plainTextLines, expectedPlainTextLines, null,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE, LineTrim.OFF, ShowWhiteSpace.ON));
		
		assertTrue(parser.getAnnotations().size() > 0);
		for (Annotation annotation : parser.getAnnotations()) {
			assertNotNull(annotation);
			assertNotNull(annotation.getType());
			assertTrue(annotation.getStart() < plainText.length()+1);
			assertTrue(annotation.getEnd() < plainText.length()+1);
		}

	}

}
