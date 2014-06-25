package edu.ucdenver.ccp.nlp.uima.collections.file;

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
import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.nlp.uima.util.TypeSystemUtil;
import edu.ucdenver.ccp.nlp.uima.util.View;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class FileSystemCollectionReaderTest extends DefaultTestCase {

	private static final String DOC1_TEXT = "This is sample document 1.";
	private static final String DOC2_TEXT = "This is sample document 2.";
	private static final String DOC3_TEXT = "This is sample document 3.";
	private static final String DOC4_TEXT = "This is sample document 4.";
	private static final String DOC5_TEXT = "This is sample document 5.";
	private static final String DOC6_TEXT = "This is sample document 6.";
	private static final String DOC7_TEXT = "This is sample document 7.";
	private static final String DOC8_TEXT = "This is sample document 8.";
	private static final String DOC1_ID = "doc1.txt";
	private static final String DOC2_ID = "doc2.txt";
	private static final String DOC3_ID = "doc3.txt";
	private static final String DOC4_ID = "doc4.utf8";
	private static final String DOC5_ID = "doc5.xml";
	private static final String DOC6_ID = "doc6.txt";
	private static final String DOC7_ID = "doc7.csv";
	private static final String DOC8_ID = "doc8.txt";

	private static final CharacterEncoding ENCODING = CharacterEncoding.UTF_8;

	private File baseDir = null;

	/**
	 * Sets up a directory hierarchy like the following:
	 * 
	 * <pre>
	 * base
	 * ---doc1.txt
	 * ---doc2.txt
	 * ---doc3.txt
	 * ---doc4.utf8
	 * ---dir1
	 * ------doc5.xml
	 * ---dir2
	 * ------doc6.txt
	 * ------dir3
	 * ---------doc7.csv
	 * ---------doc8.txt
	 * </pre>
	 * 
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		baseDir = folder.newFolder("base");
		File doc1 = new File(baseDir, DOC1_ID);
		File doc2 = new File(baseDir, DOC2_ID);
		File doc3 = new File(baseDir, DOC3_ID);
		File doc4 = new File(baseDir, DOC4_ID);
		File dir1 = new File(baseDir, "dir1");
		assertTrue(dir1.mkdir());
		File doc5 = new File(dir1, DOC5_ID);
		File dir2 = new File(baseDir, "dir2");
		assertTrue(dir2.mkdir());
		File doc6 = new File(dir2, DOC6_ID);
		File dir3 = new File(dir2, "dir3");
		assertTrue(dir3.mkdir());
		File doc7 = new File(dir3, DOC7_ID);
		File doc8 = new File(dir3, DOC8_ID);

		FileWriterUtil.printLines(CollectionsUtil.createList(DOC1_TEXT), doc1, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC2_TEXT), doc2, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC3_TEXT), doc3, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC4_TEXT), doc4, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC5_TEXT), doc5, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC6_TEXT), doc6, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC7_TEXT), doc7, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
		FileWriterUtil.printLines(CollectionsUtil.createList(DOC8_TEXT), doc8, ENCODING, WriteMode.OVERWRITE,
				FileSuffixEnforcement.OFF);
	}

	@Test
	public void testFileSystemCollectionReader_recurseFalse() throws UIMAException, IOException {
		boolean recurse = false;
		int num2process = 8;
		int num2skip = 0;
		String viewName = View.DEFAULT.viewName();
		String[] fileSuffixesToProcess = new String[0];
		CollectionReaderDescription desc = FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, recurse, ENCODING, "en", false, num2process, num2skip,
				viewName, fileSuffixesToProcess);

		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {
			casCount++;
		}

		assertEquals(4, casCount);

	}
	@Test
	public void testFileSystemCollectionReader_alternateView() 
	throws UIMAException, IOException {
		String[] fileSuffixesToProcess = {};
		CollectionReaderDescription desc 
			= FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, false, 
				ENCODING, "en", false, 8,0, View.RAW.viewName(), 
				fileSuffixesToProcess);
		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {

			// check views' names for RAW
			 for (Iterator<JCas> viewIter = jcas.getViewIterator(); viewIter.hasNext();) {
            	JCas view = viewIter.next();
            	if (view.getViewName().equals(View.RAW.viewName())) {
					casCount++;
				}
        	}

		}

		assertEquals(4, casCount);
	}

	@Test
	public void testFileSystemCollectionReader_recurseTrue() throws UIMAException, IOException {
		boolean recurse = true;
		int num2process = 8;
		int num2skip = 0;
		String viewName = View.DEFAULT.viewName();
		String[] fileSuffixesToProcess = new String[0];
		CollectionReaderDescription desc = FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, recurse, ENCODING, "en", false, num2process, num2skip,
				viewName, fileSuffixesToProcess);

		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {
			casCount++;
		}

		assertEquals(8, casCount);
	}

	@Test
	public void testFileSystemCollectionReader_recurseTrue_SuffixTxt() throws UIMAException, IOException {
		boolean recurse = true;
		int num2process = 8;
		int num2skip = 0;
		String viewName = View.DEFAULT.viewName();
		String[] fileSuffixesToProcess = new String[] { ".txt" };
		CollectionReaderDescription desc = FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, recurse, ENCODING, "en", false, num2process, num2skip,
				viewName, fileSuffixesToProcess);

		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {
			casCount++;
		}

		assertEquals(5, casCount);
	}

	@Test
	public void testFileSystemCollectionReader_recurseTrue_SuffixTxt_Skip4() throws UIMAException, IOException {
		boolean recurse = true;
		int num2process = 8;
		int num2skip = 4;
		String viewName = View.DEFAULT.viewName();
		String[] fileSuffixesToProcess = new String[] { ".txt" };
		CollectionReaderDescription desc = FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, recurse, ENCODING, "en", false, num2process, num2skip,
				viewName, fileSuffixesToProcess);

		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {
			casCount++;
		}

		assertEquals(1, casCount);
	}

	@Test
	public void testFileSystemCollectionReader_recurseTrue_Num2ProcessMinusOne() throws UIMAException, IOException {
		boolean recurse = true;
		int num2process = -1;
		int num2skip = 0;
		String viewName = View.DEFAULT.viewName();
		String[] fileSuffixesToProcess = new String[0];
		CollectionReaderDescription desc = FileSystemCollectionReader.createDescription(
				TypeSystemUtil.getCcpTypeSystem(), baseDir, recurse, ENCODING, "en", false, num2process, num2skip,
				viewName, fileSuffixesToProcess);

		int casCount = 0;
		for (JCas jcas : new JCasIterable(CollectionReaderFactory.createCollectionReader(desc))) {
			casCount++;
		}

		assertEquals(8, casCount);
	}

}
