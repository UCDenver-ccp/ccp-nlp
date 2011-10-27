/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.ao.AoAnnotationRdfGenerator.AoAnnotationOffsetRangeRdfGenerator;
import edu.ucdenver.ccp.rdf.ao.AoSelectorType;
import edu.ucdenver.ccp.rdf.craft.CcpCraftAnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.craft.CraftAoDocumentRdfGenerator;
import edu.ucdenver.ccp.rdf.craft.CraftDocument;
import edu.ucdenver.ccp.rdf.craft.CraftDocumentUrlFactory;
import edu.ucdenver.ccp.rdf.craft.CraftUriFactory;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil.RdfFormat;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class RdfSerialization_AE_AOFormat_Test extends DefaultUIMATestCase {

	CraftDocument testDocument = CraftDocument.CRAFT_11319941;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		/* 012345678901234567890123456789 */
		jcas.setDocumentText("ABC-1 regulates XYZ45.");
		UIMA_Util.setDocumentID(jcas, testDocument.pmid().getDataElement().toString() + ".txt");
		addTextAnnotationToJCas(0, 5, "PR:000123");
		addTextAnnotationToJCas(16, 21, "PR:000789");
	}

	@Test
	public void testAoRdfSerialization() throws ResourceInitializationException, AnalysisEngineProcessException,
			IOException {
		File outputDirectory = folder.newFolder("rdf-output");
		String outputFilePrefix = "test";
		AoSelectorType selectorType = AoSelectorType.OFFSET_RANGE_TEXT_SELECTOR;
		RdfFormat format = RdfFormat.RDFXML;

		AnalysisEngine rdfSerializationAe = RdfSerialization_AE.createAnalysisEngine(tsd, outputDirectory,
				outputFilePrefix, selectorType, format, CcpDocumentMetaDataExtractor.class,
				CcpCraftAnnotationDataExtractor.class, AoAnnotationOffsetRangeRdfGenerator.class,
				CraftAoDocumentRdfGenerator.class, CraftUriFactory.class,0,0);

		rdfSerializationAe.process(jcas);
		rdfSerializationAe.collectionProcessComplete();
		File documentRdfFile = new File(outputDirectory, String.format("%s-documents.batch0.0.%s", outputFilePrefix,
				format.defaultFileExtension()));
		File annotationRdfFile = new File(outputDirectory, String.format("%s-annotations.batch0.0.%s", outputFilePrefix,
				format.defaultFileExtension()));

		// for (String line : FileReaderUtil.loadLinesFromFile(documentRdfFile,
		// CharacterEncoding.UTF_8)) {
		// System.out.println("DOC RDF LINE: " + line);
		// }
		//
		// for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile,
		// CharacterEncoding.UTF_8)) {
		// System.out.println("ANN RDF LINE: " + line);
		// }

		assertTrue(FileComparisonUtil.hasExpectedLines(documentRdfFile, CharacterEncoding.UTF_8,
				getExpectedDocumentRdf(), null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));

		String[] annotationUuids = extractAnnotationUuid(annotationRdfFile);
		String[] createdOnTimes = extractAnnotationCreatedOnTimes(annotationRdfFile);

		
		System.out.println("ANNOTATION UIDS: " + Arrays.toString(annotationUuids));
		
		for (String line : getExpectedAnnotationRdf(annotationUuids, createdOnTimes))
			System.out.println("EXP ANN LINE: " + line);

		assertTrue(FileComparisonUtil.hasExpectedLines(annotationRdfFile, CharacterEncoding.UTF_8,
				getExpectedAnnotationRdf(annotationUuids, createdOnTimes), null, LineOrder.AS_IN_FILE,
				ColumnOrder.AS_IN_FILE));

	}

	/**
	 * @param annotationRdfFile
	 * @return
	 * @throws IOException
	 */
//	private String[] extractAnnotationUuid(File annotationRdfFile) throws IOException {
//		String[] uuids = new String[2];
//		String patternStr = "<rdf:Description rdf:about=\"http://craft\\.ucdenver\\.edu/annotation([^\"/]+)\">";
//		boolean foundFirstUuid = false;
//		Pattern p = Pattern.compile(patternStr);
//		Matcher m;
//		for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile, CharacterEncoding.UTF_8)) {
//			m = p.matcher(line);
//			if (m.find()) {
//				String uuid = m.group(1);
//				if (!foundFirstUuid) {
//					uuids[0] = uuid;
//					foundFirstUuid = true;
//				} else if (!uuids[0].equals(uuid)) {
//					uuids[1] = uuid;
//					return uuids;
//				}
//			}
//		}
//		throw new IllegalArgumentException("Annotation RDF did not contain the expected annotation URI pattern. "
//				+ "Please check and adjust the test if necessary.");
//	}
	
	private String[] extractAnnotationUuid(File annotationRdfFile) throws IOException {
		String[] uuids = new String[2];
		String patternStr = "<rdf:Description rdf:about=\"http://craft\\.ucdenver\\.edu/annotation([^>]{36})\">";
		int index = 0;
		Pattern p = Pattern.compile(patternStr);
		Matcher m;
		for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile, CharacterEncoding.UTF_8)) {
			m = p.matcher(line);
			if (m.find()) {
				String uuid = m.group(1);
				System.out.println("FOUND UUID: " +uuid);
				boolean alreadyLogged = false;
				for (String loggedUuid : uuids)
					if (uuid.equals(loggedUuid))
						alreadyLogged = true;
				if (!alreadyLogged)
					uuids[index++] = uuid;
				if (index == 2)
					return uuids;
			}
		}
		throw new IllegalArgumentException("Annotation RDF did not contain the expected annotation URI pattern. "
				+ "Please check and adjust the test if necessary.");
	}

	/**
	 * @param annotationRdfFile
	 * @return
	 * @throws IOException
	 */
	private String[] extractAnnotationCreatedOnTimes(File annotationRdfFile) throws IOException {
		String[] uuids = new String[2];
		String patternStr = "<createdOn xmlns=\"http://purl.org/pav/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">(.*?)</createdOn>";
		boolean foundFirstTime = false;
		Pattern p = Pattern.compile(patternStr);
		Matcher m;
		for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile, CharacterEncoding.UTF_8)) {
			m = p.matcher(line);
			if (m.find()) {
				String uuid = m.group(1);
				if (!foundFirstTime) {
					uuids[0] = uuid;
					foundFirstTime = true;
				} else if (!uuids[0].equals(uuid)) {
					uuids[1] = uuid;
					return uuids;
				}

			}
		}
		throw new IllegalArgumentException("Annotation RDF did not contain the expected annotation URI pattern. "
				+ "Please check and adjust the test if necessary.");
	}

	/**
	 * @param createdOnTimes
	 * @param annotationUuid
	 * @return
	 */
	private List<String> getExpectedAnnotationRdf(String[] annotationUuids, String[] createdOnTimes) {
		return CollectionsUtil
				.createList(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
						"<rdf:RDF",
						"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">",
						"",
						"<rdf:Description rdf:about=\"http://compbio.ucdenver.edu/Hunter_lab/\">",
						"\t<rdf:type rdf:resource=\"http://xmlns.com/foaf/0.1/Organization\"/>",
						"</rdf:Description>",
						"",
						"<rdf:Description rdf:about=\"http://craft.ucdenver.edu/annotation" + annotationUuids[0]
								+ "\">",
						"\t<rdf:type rdf:resource=\"http://purl.org/ao/types/ExactQualifier\"/>",
						"\t<hasTopic xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://purl.obolibrary.org/obo/PR_000123\"/>",
						"\t<annotatesDocument xmlns=\"http://purl.org/ao/foaf/\" rdf:resource=\"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432\"/>",
						"\t<onSourceDocument xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/document/PMC31432\"/>",
						"\t<createdBy xmlns=\"http://purl.org/pav/\" rdf:resource=\"http://kabob.ucdenver.edu/annotatorDefaultAnnotator\"/>",
						"\t<createdOn xmlns=\"http://purl.org/pav/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">"
								+ createdOnTimes[0] + "</createdOn>",
						"\t<context xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/annotation"
								+ annotationUuids[0] + "-selector-0\"/>",
						"</rdf:Description>",
						"",
						"<rdf:Description rdf:about=\"http://craft.ucdenver.edu/annotation" + annotationUuids[0]
								+ "-selector-0\">",
						"\t<onSourceDocument xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/document/PMC31432\"/>",
						"\t<onDocument xmlns=\"http://purl.org/ao/foaf/\" rdf:resource=\"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432\"/>",
						"\t<rdf:type rdf:resource=\"http://purl.org/ao/selectors/OffsetRangeSelector\"/>",
						"\t<exact xmlns=\"http://purl.org/ao/selectors/\">ABC-1</exact>",
						"\t<offset xmlns=\"http://purl.org/ao/selectors/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">0</offset>",
						"\t<range xmlns=\"http://purl.org/ao/selectors/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">5</range>",
						"</rdf:Description>",
						"",
						"<rdf:Description rdf:about=\"http://craft.ucdenver.edu/annotation" + annotationUuids[1]
								+ "\">",
						"\t<rdf:type rdf:resource=\"http://purl.org/ao/types/ExactQualifier\"/>",
						"\t<hasTopic xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://purl.obolibrary.org/obo/PR_000789\"/>",
						"\t<annotatesDocument xmlns=\"http://purl.org/ao/foaf/\" rdf:resource=\"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432\"/>",
						"\t<onSourceDocument xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/document/PMC31432\"/>",
						"\t<createdBy xmlns=\"http://purl.org/pav/\" rdf:resource=\"http://kabob.ucdenver.edu/annotatorDefaultAnnotator\"/>",
						"\t<createdOn xmlns=\"http://purl.org/pav/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">"
								+ createdOnTimes[1] + "</createdOn>",
						"\t<context xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/annotation"
								+ annotationUuids[1] + "-selector-0\"/>",
						"</rdf:Description>",
						"",
						"<rdf:Description rdf:about=\"http://craft.ucdenver.edu/annotation" + annotationUuids[1]
								+ "-selector-0\">",
						"\t<onSourceDocument xmlns=\"http://purl.org/ao/core/\" rdf:resource=\"http://craft.ucdenver.edu/document/PMC31432\"/>",
						"\t<onDocument xmlns=\"http://purl.org/ao/foaf/\" rdf:resource=\"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432\"/>",
						"\t<rdf:type rdf:resource=\"http://purl.org/ao/selectors/OffsetRangeSelector\"/>",
						"\t<exact xmlns=\"http://purl.org/ao/selectors/\">XYZ45</exact>",
						"\t<offset xmlns=\"http://purl.org/ao/selectors/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">16</offset>",
						"\t<range xmlns=\"http://purl.org/ao/selectors/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">5</range>",
						"</rdf:Description>", "", "</rdf:RDF>");
	}

	/**
	 * @return
	 */
	private List<String> getExpectedDocumentRdf() {
		return CollectionsUtil
				.createList(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
						"<rdf:RDF",
						"\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">",
						"",
						"<rdf:Description rdf:about=\"http://craft.ucdenver.edu/document/PMC31432\">",
						"\t<retrievedFrom xmlns=\"http://purl.org/pav/\" rdf:resource=\"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432\"/>",
						"\t<rdf:type rdf:resource=\"http://purl.org/pav/SourceDocument\"/>", "</rdf:Description>", "",
						"</rdf:RDF>");
	}
}
