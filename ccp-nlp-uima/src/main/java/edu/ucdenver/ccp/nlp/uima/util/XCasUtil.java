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
package edu.ucdenver.ccp.nlp.uima.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XCASDeserializer;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.nlp.core.document.GenericDocument;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class XCasUtil {

	public static GenericDocument loadXCasFile(File xcasFile) throws UIMAException, FileNotFoundException {
		return loadXCasFile(new FileInputStream(xcasFile));
	}

	/**
	 * Loads the input XCas file and returns a GenericDocument containing the document text and all
	 * annotations.
	 * 
	 * @param xcasFile
	 * @return
	 */
	public static GenericDocument loadXCasFile(InputStream xcasStream) throws UIMAException {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("edu.uchsc.ccp.uima.CCPTypeSystem");
		JCas jcas = JCasFactory.createJCas(tsd);
		try {
			XCASDeserializer.deserialize(xcasStream, jcas.getCas());
		} catch (SAXException e) {
			throw new UIMAException(e);
		} catch (IOException e) {
			throw new UIMAException(e);
		}

		GenericDocument gd = new GenericDocument();
		UIMA_Util.swapDocumentInfo(jcas, gd);

		int numAnnotationsInGenericDocument = gd.getAnnotations().size();
		int numAnnotationsInJCas = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size();
		assert numAnnotationsInGenericDocument == numAnnotationsInJCas : String
				.format("Number of annotations in jcas not equal to the number of annotations in the generic document. %d != %d",
						numAnnotationsInJCas, numAnnotationsInGenericDocument);

		return gd;
	}

	/**
	 * Serializes a JCas to file
	 * 
	 * @param tsd
	 * @param jcas
	 * @param outputFile
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void serializeToXCas(TypeSystemDescription tsd, JCas jcas, File outputFile) throws SAXException,
			IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		XCASSerializer.serialize(jcas.getCas(), fos);
		fos.close();
	}
}
