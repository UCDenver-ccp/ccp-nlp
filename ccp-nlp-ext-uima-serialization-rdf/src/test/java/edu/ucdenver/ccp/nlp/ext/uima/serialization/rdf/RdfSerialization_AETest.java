/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.serialization.rdf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;

import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdResolver;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.uima.shims.document.impl.CcpDocumentMetaDataExtractor;
import edu.ucdenver.ccp.rdf.CcpAnnotationRdfGenerator.CcpAnnotationOffsetRangeRdfGenerator;
import edu.ucdenver.ccp.rdf.CcpSemanticStatementGenerator;
import edu.ucdenver.ccp.rdf.DataSourceIdentifierUriFactory;
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
public class RdfSerialization_AETest extends DefaultUIMATestCase {

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
	
	@Test
	public void testBatchOutput() throws AnalysisEngineProcessException, ResourceInitializationException {
		File outputDirectory = folder.newFolder("rdf-output");
		String outputFilePrefix = "test";
		AoSelectorType selectorType = AoSelectorType.OFFSET_RANGE_TEXT_SELECTOR;
		RdfFormat format = RdfFormat.NQUADS;

		AnalysisEngine rdfSerializationAe = RdfSerialization_AE.createAnalysisEngine(tsd, outputDirectory,
				outputFilePrefix, selectorType, format, CcpDocumentMetaDataExtractor.class,
				SampleCcpRdfAnnotationDataExtractor.class, CcpAnnotationOffsetRangeRdfGenerator.class,
				CraftAoDocumentRdfGenerator.class, CraftUriFactory.class,2,15);

		rdfSerializationAe.process(jcas);
		rdfSerializationAe.process(jcas);
		rdfSerializationAe.process(jcas);
		rdfSerializationAe.process(jcas);
		rdfSerializationAe.process(jcas);
		rdfSerializationAe.collectionProcessComplete();
		
		assertEquals("Should be 6 output files (3 annotation, 3 document) in the output directory.", 6, outputDirectory.list().length);
		
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
