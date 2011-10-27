/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.CcpAnnotationRdfGenerator.CcpAnnotationOffsetRangeRdfGenerator;
import edu.ucdenver.ccp.rdf.CcpSemanticStatementGenerator;
import edu.ucdenver.ccp.rdf.DataSourceIdentifierUriFactory;
import edu.ucdenver.ccp.rdf.ao.AoAnnotationRdfGenerator.AoAnnotationOffsetRangeRdfGenerator;
import edu.ucdenver.ccp.rdf.ao.AoSelectorType;
import edu.ucdenver.ccp.rdf.craft.CcpCraftAnnotationDataExtractor;
import edu.ucdenver.ccp.rdf.craft.CraftAoDocumentRdfGenerator;
import edu.ucdenver.ccp.rdf.craft.CraftDocument;
import edu.ucdenver.ccp.rdf.craft.CraftUriFactory;
import edu.ucdenver.ccp.rdfizer.rdf.RdfNamespace;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil;
import edu.ucdenver.ccp.rdfizer.rdf.RdfUtil.RdfFormat;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class RdfSerialization_AE_CCPFormat_Test extends DefaultUIMATestCase {

	/**
	 * 
	 */
	private static final String REGULATEE_SLOT_NAME = "regulatee";

	/**
	 * 
	 */
	private static final String REGULATOR_SLOT_NAME = "regulator";

	/**
	 * 
	 */
	private static final String GO_GENE_REGULATION = "GO:0001234";

	private static final URI REGULATES_PREDICATE = new URIImpl(RdfUtil.createUri(RdfNamespace.RO, "regulates")
			.toString());

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
		CCPTextAnnotation proteinTa1 = addTextAnnotationToJCas(0, 5, "PR:000123");
		CCPTextAnnotation proteinTa2 = addTextAnnotationToJCas(16, 21, "PR:000789");
		CCPTextAnnotation regulation = addTextAnnotationToJCas(6, 15, GO_GENE_REGULATION);
		UIMA_Util.addSlotValue(regulation.getClassMention(), REGULATOR_SLOT_NAME, proteinTa1.getClassMention());
		UIMA_Util.addSlotValue(regulation.getClassMention(), REGULATEE_SLOT_NAME, proteinTa2.getClassMention());
	}

	/**
	 * The AO format does not currently represent complex annotations, i.e. annotations comprised of
	 * other annotations, so an exception is expected to thrown here if one is encoutered while
	 * attempting to produce AO format output.
	 * 
	 * @throws ResourceInitializationException
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAoRdfSerializationFailsWhenComplexAnnotationsArePresent() throws ResourceInitializationException {
		File outputDirectory = folder.newFolder("rdf-output");
		String outputFilePrefix = "test";
		AoSelectorType selectorType = AoSelectorType.OFFSET_RANGE_TEXT_SELECTOR;
		RdfFormat format = RdfFormat.RDFXML;

		AnalysisEngine rdfSerializationAe = RdfSerialization_AE.createAnalysisEngine(tsd, outputDirectory,
				outputFilePrefix, selectorType, format, CcpDocumentMetaDataExtractor.class,
				CcpCraftAnnotationDataExtractor.class, AoAnnotationOffsetRangeRdfGenerator.class,
				CraftAoDocumentRdfGenerator.class, CraftUriFactory.class,10000);

		try {
			rdfSerializationAe.process(jcas);
		} catch (AnalysisEngineProcessException e) {
			throw new IllegalArgumentException(e.getCause());
		}
	}

	@Test
	public void testCcpFormatRdfSerialization() throws ResourceInitializationException, AnalysisEngineProcessException,
			IOException {
		File outputDirectory = folder.newFolder("rdf-output");
		String outputFilePrefix = "test";
		AoSelectorType selectorType = AoSelectorType.OFFSET_RANGE_TEXT_SELECTOR;
		RdfFormat format = RdfFormat.NQUADS;

		AnalysisEngine rdfSerializationAe = RdfSerialization_AE.createAnalysisEngine(tsd, outputDirectory,
				outputFilePrefix, selectorType, format, CcpDocumentMetaDataExtractor.class,
				SampleCcpRdfAnnotationDataExtractor.class, CcpAnnotationOffsetRangeRdfGenerator.class,
				CraftAoDocumentRdfGenerator.class, CraftUriFactory.class,0);

		rdfSerializationAe.process(jcas);
		rdfSerializationAe.collectionProcessComplete();
		File documentRdfFile = new File(outputDirectory, String.format("%s-documents.0.%s", outputFilePrefix,
				format.defaultFileExtension()));
		File annotationRdfFile = new File(outputDirectory, String.format("%s-annotations.0.%s", outputFilePrefix,
				format.defaultFileExtension()));


		assertTrue(FileComparisonUtil.hasExpectedLines(documentRdfFile, CharacterEncoding.UTF_8,
				getExpectedDocumentRdf(), null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));

		String[] annotationUuids = extractAnnotationUuid(annotationRdfFile);
		String[] createdOnTimes = extractAnnotationCreatedOnTimes(annotationRdfFile);

		assertTrue(FileComparisonUtil.hasExpectedLines(annotationRdfFile, CharacterEncoding.UTF_8,
				getExpectedAnnotationRdf(annotationUuids, createdOnTimes), null, LineOrder.AS_IN_FILE,
				ColumnOrder.AS_IN_FILE));

	}

	/**
	 * @param annotationRdfFile
	 * @return
	 * @throws IOException
	 */
	private String[] extractAnnotationUuid(File annotationRdfFile) throws IOException {
		String[] uuids = new String[3];
		String patternStr = "<http://craft\\.ucdenver\\.edu/annotation([^>]{36})>";
		int index = 0;
		Pattern p = Pattern.compile(patternStr);
		Matcher m;
		for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile, CharacterEncoding.UTF_8)) {
			m = p.matcher(line);
			if (m.find()) {
				String uuid = m.group(1);
				boolean alreadyLogged = false;
				for (String loggedUuid : uuids)
					if (uuid.equals(loggedUuid))
						alreadyLogged = true;
				if (!alreadyLogged)
					uuids[index++] = uuid;
				if (index == 3)
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
		String[] createdOnTimes = new String[3];
		String patternStr = "<http://craft\\.ucdenver\\.edu/annotation[^>]{36}> <http://purl\\.org/pav/createdOn> \"(.*?)\"\\^\\^<http://www\\.w3\\.org/2001/XMLSchema#dateTime>  \\.";
		int index = 0;
		Pattern p = Pattern.compile(patternStr);
		Matcher m;
		for (String line : FileReaderUtil.loadLinesFromFile(annotationRdfFile, CharacterEncoding.UTF_8)) {
			System.out.println("LINE: " + line);
			m = p.matcher(line);
			if (m.find()) {
				String uuid = m.group(1);
				boolean alreadyLogged = false;
				for (String loggedUuid : createdOnTimes)
					if (uuid.equals(loggedUuid))
						alreadyLogged = true;
				if (!alreadyLogged)
					createdOnTimes[index++] = uuid;
				if (index == 3)
					return createdOnTimes;
			}
		}
		throw new IllegalArgumentException("Annotation RDF did not contain the expected createdOn URI pattern. "
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
						"<http://compbio.ucdenver.edu/Hunter_lab/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Organization>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://kabob.ucdenver.edu/iao/ResourceAnnotation>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://kabob.ucdenver.edu/iao/denotesResource> <http://purl.obolibrary.org/obo/PR_000123>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://purl.org/ao/foaf/annotatesDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://purl.org/pav/createdBy> <http://kabob.ucdenver.edu/annotatorDefaultAnnotator>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://purl.org/pav/createdOn> \""+createdOnTimes[0]+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>  .",
						"<http://craft.ucdenver.edu/document/PMC31432> <http://kabob.ucdenver.edu/iao/mentionsProtein> <http://purl.obolibrary.org/obo/PR_000123>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"> <http://purl.org/ao/core/context> <http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://purl.org/ao/foaf/onDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/ao/selectors/OffsetRangeSelector>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://purl.org/ao/selectors/exact> \"ABC-1\"  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://purl.org/ao/selectors/offset> \"0\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0> <http://purl.org/ao/selectors/range> \"5\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://kabob.ucdenver.edu/iao/ResourceAnnotation>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://kabob.ucdenver.edu/iao/denotesResource> <http://purl.org/obo/owl/GO#GO_0001234>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/foaf/annotatesDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/pav/createdBy> <http://kabob.ucdenver.edu/annotatorDefaultAnnotator>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/pav/createdOn> \""+createdOnTimes[1]+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>  .",
						"<http://craft.ucdenver.edu/document/PMC31432> <http://purl.obolibrary.org/obo/mentions> <http://purl.org/obo/owl/GO#GO_0001234>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/core/context> <http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://purl.org/ao/foaf/onDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/ao/selectors/OffsetRangeSelector>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://purl.org/ao/selectors/exact> \"regulates\"  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://purl.org/ao/selectors/offset> \"6\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-selector-0> <http://purl.org/ao/selectors/range> \"9\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/core/context> <http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/core/context> <http://craft.ucdenver.edu/annotation"+annotationUuids[0]+"-selector-0>  .",
						"<http://purl.obolibrary.org/obo/PR_000123> <http://www.obofoundry.org/ro/ro.owl#regulates> <http://purl.obolibrary.org/obo/PR_000789> <http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-semantics-graph> .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://purl.org/ao/core/hasBody> <http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"-semantics-graph>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://kabob.ucdenver.edu/iao/ResourceAnnotation>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://kabob.ucdenver.edu/iao/denotesResource> <http://purl.obolibrary.org/obo/PR_000789>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://purl.org/ao/foaf/annotatesDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://purl.org/pav/createdBy> <http://kabob.ucdenver.edu/annotatorDefaultAnnotator>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://purl.org/pav/createdOn> \""+createdOnTimes[2]+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>  .",
						"<http://craft.ucdenver.edu/document/PMC31432> <http://kabob.ucdenver.edu/iao/mentionsProtein> <http://purl.obolibrary.org/obo/PR_000789>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"> <http://purl.org/ao/core/context> <http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://purl.org/ao/core/onSourceDocument> <http://craft.ucdenver.edu/document/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://purl.org/ao/foaf/onDocument> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/ao/selectors/OffsetRangeSelector>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://purl.org/ao/selectors/exact> \"XYZ45\"  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://purl.org/ao/selectors/offset> \"16\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[2]+"-selector-0> <http://purl.org/ao/selectors/range> \"5\"^^<http://www.w3.org/2001/XMLSchema#int>  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://kabob.ucdenver.edu/iao/basedOn> <http://craft.ucdenver.edu/annotation"+annotationUuids[2]+">  .",
						"<http://craft.ucdenver.edu/annotation"+annotationUuids[1]+"> <http://kabob.ucdenver.edu/iao/basedOn> <http://craft.ucdenver.edu/annotation"+annotationUuids[0]+">  .");
	}

	/**
	 * @return
	 */
	private List<String> getExpectedDocumentRdf() {
		return CollectionsUtil
				.createList(
						"<http://craft.ucdenver.edu/document/PMC31432> <http://purl.org/pav/retrievedFrom> <http://www.ncbi.nlm.nih.gov/pmc/articles/PMC31432>  .",
						"<http://craft.ucdenver.edu/document/PMC31432> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/pav/SourceDocument>  .");
	}

	private static class SampleCcpSemanticGenerator extends CcpSemanticStatementGenerator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * edu.ucdenver.ccp.rdf.CcpSemanticStatementGenerator#generateSemanticStatements(edu.ucdenver
		 * .ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation, org.openrdf.model.URI)
		 */
		@Override
		protected Collection<? extends Statement> generateSemanticStatements(TextAnnotation wrappedTa,
				URI graphUri,Map<String, URI> annotationKeyToSemanticInstanceUriMap) {
			Collection<Statement> stmts = new ArrayList<Statement>();
			if (wrappedTa.getClassMention().getMentionName().equals(GO_GENE_REGULATION)) {
				URI regulatorUri = new DataSourceIdentifierUriFactory().getUri(DataSourceIdResolver.resolveId(wrappedTa
						.getClassMention().getComplexSlotMentionByName(REGULATOR_SLOT_NAME).getSingleSlotValue()
						.getMentionName()));
				URI regulateeUri = new DataSourceIdentifierUriFactory().getUri(DataSourceIdResolver.resolveId(wrappedTa
						.getClassMention().getComplexSlotMentionByName(REGULATEE_SLOT_NAME).getSingleSlotValue()
						.getMentionName()));
				stmts.add(new ContextStatementImpl(regulatorUri, REGULATES_PREDICATE, regulateeUri, graphUri));
			}
			return stmts;
		}

	}

	private static class SampleCcpRdfAnnotationDataExtractor extends CcpCraftAnnotationDataExtractor {
		public SampleCcpRdfAnnotationDataExtractor() {
			setSemanticStatementGenerator(new SampleCcpSemanticGenerator());
		}
	}

}
