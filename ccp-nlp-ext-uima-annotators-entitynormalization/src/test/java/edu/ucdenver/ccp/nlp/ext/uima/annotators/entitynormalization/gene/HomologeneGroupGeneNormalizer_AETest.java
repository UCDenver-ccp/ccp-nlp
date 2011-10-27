/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.AccessMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.Dictionary.LoadMode;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.BerkeleyDbDictionary;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.BerkeleyDbGeneNameEntry;
import edu.ucdenver.ccp.nlp.ext.dictionary.berkeleydb.impl.gene.HomologeneDictionaryBuilder;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl.CcpGeneIdAnnotationDecorator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class HomologeneGroupGeneNormalizer_AETest extends DefaultUIMATestCase {

	private static final String SAMPLE_EG_INFO_FILE_NAME = "gene_info";
	private static final String SAMPLE_HOMOLOGENE_FILE_NAME = "homologene.data";

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.ucdenver.ccp.nlp.core.uima.test.DefaultUIMATestCase#initJCas()
	 */
	@Override
	protected void initJCas() throws Exception {
		/* 0123456789012345678901234567890123456789 */
		String documentText = "ABC-1 regulates XYZ34 but not CBS29.";
		jcas.setDocumentText(documentText);
		addTextAnnotationToJCas(0, 5, "protein"); // EG_1234
		addTextAnnotationToJCas(16, 21, "protein"); // HOMOLOGENE_GROUP_5555
		addTextAnnotationToJCas(30, 35, "protein"); // not in the dictionary so not normalized
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private File createSampleHomologeneDictionary() throws IOException {
		File dictionaryDirectory = folder.newFolder("dict");
		File dataDirectory = folder.newFolder("data");
		ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_EG_INFO_FILE_NAME, dataDirectory);
		ClassPathUtil.copyClasspathResourceToDirectory(getClass(), SAMPLE_HOMOLOGENE_FILE_NAME, dataDirectory);
		HomologeneDictionaryBuilder dictBuilder = new HomologeneDictionaryBuilder(dictionaryDirectory, dataDirectory,
				false);
		dictBuilder.buildDictionary(LoadMode.OVERWRITE);
		
		validateDictionary(dictionaryDirectory);
		
		return dictionaryDirectory;
	}

	/**
	 * @param dictionaryDirectory
	 */
	private void validateDictionary(File dictionaryDirectory) {
		Dictionary<BerkeleyDbGeneNameEntry> dictionary = new BerkeleyDbDictionary<BerkeleyDbGeneNameEntry>(
				dictionaryDirectory, HomologeneDictionaryBuilder.STORE_NAME,
				HomologeneDictionaryBuilder.ENTRY_INDEX_ACCESSOR_CLASS,
				HomologeneDictionaryBuilder.DICTIONARY_ENTRY_CLASS);
		dictionary.open(AccessMode.READONLY);
		
		BerkeleyDbGeneNameEntry entry = dictionary.retrieve("HOMOLOGENE_GROUP_5555");
		assertNotNull(entry);
		entry = dictionary.retrieve("EG_1234");
		assertNotNull(entry);
		
		
		dictionary.shutdown();
	}

	@Test
	public void testProteinNormalization() throws IOException, ResourceInitializationException,
			AnalysisEngineProcessException {
		File dictionaryDirectory = createSampleHomologeneDictionary();

		AnalysisEngine normalizer = AnalysisEngineFactory.createPrimitive(HomologeneGroupGeneNormalizer_AE
				.createAnalysisEngineDescription(tsd, dictionaryDirectory));

		normalizer.process(jcas);

		for (Iterator<CCPTextAnnotation> proteinAnnotIter = UIMA_Util.getTextAnnotationIterator(jcas, "protein"); proteinAnnotIter
				.hasNext();) {
			TextAnnotation proteinAnnot = new WrappedCCPTextAnnotation(proteinAnnotIter.next());
			PrimitiveSlotMention<?> egIdSlot = proteinAnnot.getClassMention().getPrimitiveSlotMentionByName(
					CcpGeneIdAnnotationDecorator.ENTREZ_GENE_ID_SLOT_NAME);
			PrimitiveSlotMention<?> hgIdSlot = proteinAnnot.getClassMention().getPrimitiveSlotMentionByName(
					CcpGeneIdAnnotationDecorator.HOMOLOGENE_GROUP_ID_SLOT_NAME);
			if (proteinAnnot.getAggregateSpan().getSpanStart() == 0) {
				assertNull(hgIdSlot);
				assertNotNull(egIdSlot);
				assertEquals("eg id for this protein should be EG_1234", 1234, egIdSlot.getSingleSlotValue());
			} else if (proteinAnnot.getAggregateSpan().getSpanStart() == 16) {
				assertNull(egIdSlot);
				assertNotNull(hgIdSlot);
				assertEquals("hg id for this protein should be HOMOLOGENE_GROUP_5555", 5555,
						hgIdSlot.getSingleSlotValue());
			} else if (proteinAnnot.getAggregateSpan().getSpanStart() == 30) {
				assertNull(hgIdSlot);
				assertNull(egIdSlot);
			} else
				throw new RuntimeException("Unexpected start offset for a protein annotation: "
						+ proteinAnnot.getAggregateSpan().getSpanStart());

		}

	}

}
