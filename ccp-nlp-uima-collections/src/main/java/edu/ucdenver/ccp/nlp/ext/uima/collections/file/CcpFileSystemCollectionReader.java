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
package edu.ucdenver.ccp.nlp.ext.uima.collections.file;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.View;

/**
 * This is a subclass of FileSystemCollectionReader that initializes the document ID 
 * for the CCP type system. The difference is in the initialize method.
 * @author williamb
 *
 */
public class CcpFileSystemCollectionReader extends FileSystemCollectionReader {

	private static final Logger logger = Logger.getLogger(
			CcpFileSystemCollectionReader.class);

	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, 
			File baseFileOrDirectory,
			boolean recurse, 
			CharacterEncoding encoding, 
			String language, 
			boolean disableProgress, 
			int documentCollectionID,
			int num2process, 
			int num2skip, 
			String viewName, 
			String... fileSuffixesToProcess)
			throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(), 
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse, 
				PARAM_DISABLE_PROGRESS, disableProgress, 
				PARAM_DOCUMENT_COLLECTION_ID, documentCollectionID,
				PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess, 
				PARAM_LANGUAGE, language, 
				PARAM_NUM2PROCESS, num2process, 
				PARAM_NUM2SKIP, num2skip, 
				PARAM_VIEWNAME, viewName);
	}

	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, File baseFileOrDirectory,CharacterEncoding encoding,
			boolean recurse) 
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(),
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse);
	}

	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, File baseFileOrDirectory,CharacterEncoding encoding,
			boolean recurse, View view) 
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(), 
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse, 
				PARAM_VIEWNAME, view.viewName());
	}
	
	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, File baseFileOrDirectory,CharacterEncoding encoding,
			boolean recurse, View view, int num2skip, int num2process) 
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(), 
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse, 
				PARAM_VIEWNAME, view.viewName(), 
				PARAM_NUM2SKIP, num2skip, 
				PARAM_NUM2PROCESS, num2process);
	}

	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, File baseFileOrDirectory,CharacterEncoding encoding,
			boolean recurse, boolean disableProgressTracking) 
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(), 
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse, 
				PARAM_DISABLE_PROGRESS,disableProgressTracking);
	}

	public static CollectionReader createCollectionReader(
			TypeSystemDescription tsd, File baseFileOrDirectory,CharacterEncoding encoding,
			boolean recurse, String... fileSuffixesToProcess) 
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, baseFileOrDirectory.getAbsolutePath(), 
				PARAM_ENCODING, encoding.getCharacterSetName(), 
				PARAM_RECURSE, recurse,
				PARAM_FILESUFFIXES_TO_PROCESS, fileSuffixesToProcess);
	}

	
	public static String createXmlDescriptor(TypeSystemDescription tsd) 
	throws SAXException, IOException, ResourceInitializationException {
		CollectionReaderDescription crd = 
			CollectionReaderFactory.createDescription(
					CcpFileSystemCollectionReader.class, tsd,
				PARAM_BASE_FILE, "REQUIRED -- BASE FILE OR DIRECTORY GOES HERE",
				PARAM_ENCODING, "UTF-8", 
				PARAM_RECURSE, true, 
				PARAM_DISABLE_PROGRESS, false, 
				PARAM_DOCUMENT_COLLECTION_ID, -1,
				PARAM_FILESUFFIXES_TO_PROCESS, new String[0], 
				PARAM_LANGUAGE, "English", 
				PARAM_NUM2PROCESS, -1, 
				PARAM_NUM2SKIP, 0, 
				PARAM_VIEWNAME, "DEFAULT");
		StringWriter sw = new StringWriter();
		crd.toXML(sw);
		sw.close();
		return sw.toString();
	}
	
	@Override
	protected void initializeJCas(JCas jcas, JCas view, File file) {
		 logger.debug(String.format("Setting docID for file: %s in view %s", 
				 file.getName(),
		 view.getViewName()));
//		UIMA_Util.setDocumentID(jcas, file.getName());
//		UIMA_Util.setDocumentCollectionID(jcas, this.documentCollectionID);
		UIMA_Util.setDocumentID(view, file.getName());
		UIMA_Util.setDocumentCollectionID(view, this.documentCollectionID);
		UIMA_Util.setDocumentEncoding(view, CharacterEncoding.valueOf(this.encoding.replaceAll("-", "_")));
	}
}
