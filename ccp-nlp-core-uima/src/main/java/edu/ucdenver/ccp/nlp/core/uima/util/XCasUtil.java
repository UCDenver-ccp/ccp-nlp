package edu.ucdenver.ccp.nlp.core.uima.util;

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
					.format(
							"Number of annotations in jcas not equal to the number of annotations in the generic document. %d != %d",
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
