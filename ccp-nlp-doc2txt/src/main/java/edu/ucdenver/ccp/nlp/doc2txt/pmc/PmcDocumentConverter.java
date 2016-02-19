package edu.ucdenver.ccp.nlp.doc2txt.pmc;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.nlp.doc2txt.CcpXmlParser;
import edu.ucdenver.ccp.nlp.doc2txt.XsltConverter;

/**
 * @author Center for Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class PmcDocumentConverter {

	private static final Logger logger = Logger.getLogger(PmcDocumentConverter.class);

	@Option(name = "-r", usage = "set to true to recurse through the input directory when converting PMC XML files to plain text. Default=true")
	private boolean recurseDirectoryStructure = true;

	@Option(name = "-i", usage = "indicates a single PMC XML file or a directory containing PMC XML files to be converted to plain text.")
	private File inputFileOrDirectory;

	@Option(name = "-o", usage = "indicates the output directory where plain text files will be written. This parameter is optional. If not specified, the plain text files will be written to the directory containing the input PMC XML files.")
	private File outputDirectory = null;

	@Option(name = "-a", usage = "set to true to output document zone annotations to file. Default=true")
	private boolean outputAnnotations = true;

	@Option(name = "-n", usage = "the number of nxml files to process. Default = -1 (i.e. process them all)")
	private int numToProcess = -1;

	@Option(name = "-s", usage = "the number of nxml files to skip before beginning to process them. Default = 0.")
	private int numToSkip = 0;

	@Option(name = "-l", usage = "a list of files to process. path is relative to the directory specified in the -i parameter. MUST BE ABSOLUTE PATH.")
	private File listOfNxmlFile;

	@Argument
	private List<String> fileSuffixesToProcess = CollectionsUtil.createList(".nxml", ".nxml.gz");

	private void convertPmcToPlainText(String documentId, File pmcXmlFile, File outputDirectory) throws IOException,
			SAXException {
		// convert PMC XML to simpler CCP XML
		XsltConverter xslt = new XsltConverter(new PmcDtdClasspathResolver());
		InputStream xmlStream = null;
		if (pmcXmlFile.getName().endsWith(".gz")) {
			xmlStream = new GZIPInputStream(new FileInputStream(pmcXmlFile));
		} else {
			xmlStream = new FileInputStream(pmcXmlFile);
		}
		String ccpXml = xslt.convert(xmlStream, PmcXslLocator.getPmcXslStream());

		// convert CCP XML to plain text
		CcpXmlParser parser = new CcpXmlParser();
		String plainText = parser.parse(ccpXml, documentId);

		String outputFilename = documentId + ".utf8.gz";
		File outputFile = (outputDirectory == null) ? new File(pmcXmlFile.getParentFile(), outputFilename) : new File(
				outputDirectory, outputFilename);
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(
				new GZIPOutputStream(new FileOutputStream(outputFile)), CharacterEncoding.UTF_8);
		writer.write(plainText);
		writer.close();

		if (outputAnnotations) {
			List<CcpXmlParser.Annotation> annotations = parser.getAnnotations();
			String annotationFilename = documentId + ".ann.gz";
			File annotOutputFile = (outputDirectory == null) ? new File(pmcXmlFile.getParentFile(), annotationFilename)
					: new File(outputDirectory, annotationFilename);
			BufferedWriter annotWriter = FileWriterUtil.initBufferedWriter(new GZIPOutputStream(new FileOutputStream(
					annotOutputFile)), CharacterEncoding.UTF_8);
			try {
				for (CcpXmlParser.Annotation annot : annotations) {
					String annotLine = annot.type + "|" + annot.name + "|" + annot.start + "|" + annot.end + "\n";
					annotWriter.write(annotLine);
				}
			} finally {
				annotWriter.close();
			}
		}

	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		new PmcDocumentConverter().doConversion(args);
	}

	private void doConversion(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
			if (inputFileOrDirectory == null) {
				throw new CmdLineException(parser, new IllegalArgumentException(
						"You must specify an input file or directory to process."));
			}
			boolean isDir = inputFileOrDirectory.isDirectory();
			if (isDir) {
				logger.info("Processing files in " + inputFileOrDirectory.getAbsolutePath());
			} else {
				logger.info("Processing single file: " + inputFileOrDirectory.getAbsolutePath());
			}

			if (listOfNxmlFile.getName().contains("NULL")) {
				listOfNxmlFile = null;
			}
			if (listOfNxmlFile != null) {
				logger.info("Processing files from the list in: " + listOfNxmlFile.getAbsolutePath());
			}

			if (isDir && recurseDirectoryStructure) {
				logger.info("-r flag is set, the input directory structure will be traversed recursively");
			} else {
				logger.info("-r flag is not set; the input directory structure will not be traversed");
			}

			if (fileSuffixesToProcess.size() > 0) {
				logger.info("Input files will be restricted to those with the following suffix(es): "
						+ fileSuffixesToProcess.toString());
			} else {
				logger.info("Input files will not be restricted based on file suffix (None have been specified).");
			}

			if (outputDirectory == null) {
				logger.info("No output directory has been specified. Plain text files will be written in the same directory as input PMC XML file.");
			} else {
				logger.info("All plain text files will be written to: " + outputDirectory.getAbsolutePath());
			}

			if (outputAnnotations) {
				logger.info("Annotations will be saved to a .ann file.");
			} else {
				logger.info("Annotations will not be saved to a separate file.");
			}

			logger.info("Set to skip " + numToSkip + " files prior to processing.");
			logger.info("Set to process " + numToProcess + " files after skip.");

		} catch (CmdLineException e) {
			logger.error(e);
			System.err.println("java SampleMain [options...] arguments...");
			parser.printUsage(System.err);
			System.err.println();
			return;
		}

		try {
			Iterator<File> fileIter = null;
			if (listOfNxmlFile != null) {
				fileIter = getFileListIterator();
			} else {
				fileIter = FileUtil.getFileIterator(inputFileOrDirectory, recurseDirectoryStructure,
						fileSuffixesToProcess.toArray(new String[fileSuffixesToProcess.size()]));
			}

			int count = 0;
			while (fileIter.hasNext()) {
				File pmcXmlFile = fileIter.next();
				if (count >= numToSkip) {
					if (numToProcess < 0 || count < (numToSkip + numToProcess)) {
						logger.info("processing file: " + pmcXmlFile.getAbsolutePath());
						String documentId = pmcXmlFile.getName();
						convertPmcToPlainText(documentId, pmcXmlFile, outputDirectory);
					}
				}
				count++;
			}
		} catch (Exception e) {
			logger.error("Failure during PMC XML conversion to plain text.");
			logger.error(e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private Iterator<File> getFileListIterator() throws IOException {
		List<String> relativeFileNames = FileReaderUtil.loadLinesFromFile(listOfNxmlFile, CharacterEncoding.US_ASCII);

		final Iterator<String> fileIter = relativeFileNames.iterator();
		return new Iterator<File>() {

			@Override
			public boolean hasNext() {
				return fileIter.hasNext();
			}

			@Override
			public File next() {
				String relativeFileName = fileIter.next();
				return new File(inputFileOrDirectory, relativeFileName);
			}
		};
	}

}
