package edu.ucdenver.ccp.uima.ae.printer.inline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.xml.XmlUtil;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.DocumentMetaDataExtractor;

/**
 * This class is an extension of {@link InlinePrinter_AE}. It is designed to print inline XML
 * annotations. This class differs from its parent {@link InlinePrinter_AE} in two important ways.
 * First, an XML header is inserted at the top of the inlined output file. And second, characters
 * that cannot be included in their raw form inside an XML document are escaped. {@see
 * XmlUtil#escapeForXml(char)}
 * 
 * @author bill
 * 
 */
public class XmlInlinePrinter extends InlinePrinter_AE {

	/**
	 * Inserts an XML header at the top of each output file, e.g.
	 * 
	 * <pre>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * </pre>
	 * 
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlinePrinter#insertOutputFileHeader(java.io.BufferedWriter,
	 *      edu.ucdenver.ccp.common.file.CharacterEncoding)
	 */
	@Override
	protected void insertOutputFileHeader(BufferedWriter writer, CharacterEncoding documentEncoding) {
		try {
			writer.append(XmlUtil.getXmlHeader1_0(documentEncoding));
			writer.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/** Converts characters that are not permitted inside XML into their corresponding escape sequences.
	 * @see edu.uchsc.ccp.uima.ae.util.printer.inline.InlinePrinter#processDocumentCharacter(char)
	 */
	@Override
	protected String processDocumentCharacter(char c) {
		return XmlUtil.escapeForXml(c);
	}

	/**
	 * Returns an initialized {@link XmlInlinePrinter} in the form of a UIMA {@link AnalysisEngine}.
	 * This class differs from its parent {@link InlinePrinter_AE} in two important ways. First, an XML
	 * header is inserted at the top of the inlined output file. And second, characters that cannot
	 * be included in their raw form inside an XML document are escaped. {@see
	 * XmlUtil#escapeForXml(char)}
	 * 
	 * @param tsd
	 *            the relevant {@link TypeSystemDescription}; will depend on the pipline being run
	 *            and the types being printed
	 * @param outputDirectory
	 *            a reference to the directory where inlined-annotation files will be stored when
	 *            they are created
	 * @param viewNameToProcess
	 *            the name of the view from which to extract the document text and annotations
	 * @param documentMetaDataExtractorClass
	 *            an implementation of {@link DocumentMetaDataExtractor} to use to extract document
	 *            identifier and encoding information from the {@JCas}
	 * @param annotationExtractorClasses
	 *            implementation(s) of {@link InlineTagExtractor} that will be used to
	 *            generate the annotations that will be inlined with the document text in the output
	 * @return an initialized {@link AnalysisEngine} that will print inlined XML annotations to
	 *         files in the specified output directory. An XML header will be included and certain
	 *         XML characters will be escaped
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File outputDirectory,
			String viewNameToProcess, Class<? extends DocumentMetaDataExtractor> documentMetaDataExtractorClass,
			Class<? extends InlineTagExtractor>... annotationExtractorClasses)
			throws ResourceInitializationException {
		String[] annotationExtractorClassNames = new String[annotationExtractorClasses.length];
		int i = 0;
		for (Class<? extends InlineTagExtractor> extractorClass : annotationExtractorClasses)
			annotationExtractorClassNames[i++] = extractorClass.getName();

		return createAnalysisEngine(XmlInlinePrinter.class, tsd, outputDirectory, viewNameToProcess,
				documentMetaDataExtractorClass, annotationExtractorClassNames);
	}

	/**
	 * Returns an initialized {@link XmlInlinePrinter} in the form of a UIMA {@link AnalysisEngine}.
	 * This class differs from its parent {@link InlinePrinter_AE} in two important ways. First, an XML
	 * header is inserted at the top of the inlined output file. And second, characters that cannot
	 * be included in their raw form inside an XML document are escaped. {@see
	 * XmlUtil#escapeForXml(char)}. This method can be used if only a single
	 * {@link InlineTagExtractor} is needed. It prevents a compiler warning dealing with
	 * arrays of generics.
	 * 
	 * @param tsd
	 *            the relevant {@link TypeSystemDescription}; will depend on the pipline being run
	 *            and the types being printed
	 * @param outputDirectory
	 *            a reference to the directory where inlined-annotation files will be stored when
	 *            they are created
	 * @param viewNameToProcess
	 *            the name of the view from which to extract the document text and annotations
	 * @param documentMetaDataExtractorClass
	 *            an implementation of {@link DocumentMetaDataExtractor} to use to extract document
	 *            identifier and encoding information from the {@JCas}
	 * @param annotationExtractorClasses
	 *            implementation(s) of {@link InlineTagExtractor} that will be used to
	 *            generate the annotations that will be inlined with the document text in the output
	 * @return an initialized {@link AnalysisEngine} that will print inlined XML annotations to
	 *         files in the specified output directory. An XML header will be included and certain
	 *         XML characters will be escaped
	 * @throws ResourceInitializationException
	 *             if an error occurs during {@link AnalysisEngine} initialization
	 */
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, File outputDirectory,
			String viewNameToProcess, Class<? extends DocumentMetaDataExtractor> documentMetaDataExtractorClass,
			Class<? extends InlineTagExtractor> annotationExtractorClass) throws ResourceInitializationException {
		String[] annotationExtractorClassNames = new String[] { annotationExtractorClass.getName() };
		return createAnalysisEngine(XmlInlinePrinter.class, tsd, outputDirectory, viewNameToProcess,
				documentMetaDataExtractorClass, annotationExtractorClassNames);
	}

}
