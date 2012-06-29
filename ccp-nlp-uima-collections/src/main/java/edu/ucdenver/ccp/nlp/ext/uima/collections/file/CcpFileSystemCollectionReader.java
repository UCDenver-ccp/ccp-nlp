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
