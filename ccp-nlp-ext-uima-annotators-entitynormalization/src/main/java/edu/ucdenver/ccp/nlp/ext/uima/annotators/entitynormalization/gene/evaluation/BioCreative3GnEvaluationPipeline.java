/**
 * 
 */
package edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.gene.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;

import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.data.internal.biocreative3.GnTrainingSet1Document;
import edu.ucdenver.ccp.data.internal.pmc.PmcDtdEntityResolver;
import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationUtil;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.PRFResult;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.core.uima.util.AnalysisEngineType;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Util;
import edu.ucdenver.ccp.nlp.core.uima.util.View;
import edu.ucdenver.ccp.nlp.core.uima.util.View_Util;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.comparison.AnnotationComparator_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.comparison.AnnotationComparator_AE.MentionComparatorType;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.comparison.AnnotationComparator_AE.SpanComparatorType;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.AnnotationSetOverrider_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.AnnotatorOverrider_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.ClassMentionConverter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.DocumentLevelAnnotationCreator_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.converters.view.ViewXMLXSLTConverter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.ABNER_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitydetection.BannerEntityTagger_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.entitynormalization.gene.HomologeneGroupGeneNormalizer_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.filter.AnnotationConsensusFilter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.filter.AnnotationSetFilter_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.LingPipeSentenceDetector_AE;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.SentenceDetector_AE;
import edu.ucdenver.ccp.nlp.ext.uima.shims.annotation.entity.bio.impl.CcpGeneIdAnnotationDecorator;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class BioCreative3GnEvaluationPipeline {

	private static final Logger logger = Logger.getLogger(BioCreative3GnEvaluationPipeline.class);
	private final TypeSystemDescription tsd;
	private final AnalysisEngine pmcXmlToTextConverter;
	private final AnalysisEngine documentLevelAnnotationCreator_GS_EG;
	private final AnalysisEngine documentLevelAnnotationCreator_GS_HG;
	private final AnalysisEngine documentLevelAnnotationCreator_TEST_SET_EG;
	private final AnalysisEngine documentLevelAnnotationCreator_TEST_SET_HG;
	private final AnalysisEngine annotationComparator;
	private static final String XML_VIEW_NAME = View.XML.name();
	private static final String XSLT_FILE_RESOURCE_PATH = "/edu/ucdenver/ccp/nlp/ext/uima/annotators/converters/view/PMCOpenAccess.xsl";
	private static final String BC3_GN_COMPARATOR_CONFIG_FILE_NAME = "biocreative3GnAnnotationComparatorConfig.xml";
	private AnalysisEngine annotationSetOverrider_GoldStandard;
	private AnalysisEngine annotationSetOverrider_TestSet;
	private AnalysisEngine annotatorOverrider_GoldStandard;
	private AnalysisEngine annotatorOverrider_TestSet;

	private static final int GS_ANNOTATOR_ID = 0;
	private static final int GS_ANNOTATION_SET_ID = 0;
	private static final int TEST_SET_ANNOTATOR_ID = 1;
	private static final int TEST_ANNOTATION_SET_ID = 1;

	public BioCreative3GnEvaluationPipeline(TypeSystemDescription tsd) {
		this.tsd = tsd;
		try {
			this.pmcXmlToTextConverter = ViewXMLXSLTConverter_AE.createAnalysisEngine(tsd, XML_VIEW_NAME,
					View.DEFAULT.name(), XSLT_FILE_RESOURCE_PATH, PmcDtdEntityResolver.class);
			this.documentLevelAnnotationCreator_GS_EG = AnalysisEngineFactory
					.createPrimitive(DocumentLevelAnnotationCreator_AE.createAnalysisEngineDescription(tsd, "protein",
							new String[] { CcpGeneIdAnnotationDecorator.ENTREZ_GENE_ID_SLOT_NAME },
							new Integer[TEST_SET_ANNOTATOR_ID]));
			this.documentLevelAnnotationCreator_GS_HG = AnalysisEngineFactory
					.createPrimitive(DocumentLevelAnnotationCreator_AE.createAnalysisEngineDescription(tsd, "protein",
							new String[] { CcpGeneIdAnnotationDecorator.HOMOLOGENE_GROUP_ID_SLOT_NAME },
							new Integer[TEST_SET_ANNOTATOR_ID]));

			this.documentLevelAnnotationCreator_TEST_SET_EG = AnalysisEngineFactory
					.createPrimitive(DocumentLevelAnnotationCreator_AE.createAnalysisEngineDescription(tsd, "protein",
							new String[] { CcpGeneIdAnnotationDecorator.ENTREZ_GENE_ID_SLOT_NAME },
							new Integer[GS_ANNOTATOR_ID]));
			this.documentLevelAnnotationCreator_TEST_SET_HG = AnalysisEngineFactory
					.createPrimitive(DocumentLevelAnnotationCreator_AE.createAnalysisEngineDescription(tsd, "protein",
							new String[] { CcpGeneIdAnnotationDecorator.HOMOLOGENE_GROUP_ID_SLOT_NAME },
							new Integer[GS_ANNOTATOR_ID]));

			File comparatorConfigFile = File.createTempFile("bc3gnComparatorConfig", "xml");
			ClassPathUtil.copyClasspathResourceToFile(getClass(), BC3_GN_COMPARATOR_CONFIG_FILE_NAME,
					comparatorConfigFile);
			this.annotationComparator = AnnotationComparator_AE.createAnalysisEngine(tsd, comparatorConfigFile,
					SpanComparatorType.STRICT, MentionComparatorType.IDENTICAL, null);

			annotationSetOverrider_GoldStandard = AnalysisEngineFactory.createPrimitive(AnnotationSetOverrider_AE
					.createAnalysisEngineDescription(tsd, GS_ANNOTATION_SET_ID, "GS", "Gold Standard",
							new int[] { TEST_ANNOTATION_SET_ID }));
			annotationSetOverrider_TestSet = AnalysisEngineFactory.createPrimitive(AnnotationSetOverrider_AE
					.createAnalysisEngineDescription(tsd, TEST_ANNOTATION_SET_ID, "TS", "TestSet",
							new int[] { GS_ANNOTATION_SET_ID }));

			annotatorOverrider_GoldStandard = AnalysisEngineFactory.createPrimitive(AnnotatorOverrider_AE
					.createAnalysisEngineDescription(tsd, GS_ANNOTATOR_ID, "GS", "GS", "BC3",
							new int[] { TEST_SET_ANNOTATOR_ID }));

			annotatorOverrider_TestSet = AnalysisEngineFactory.createPrimitive(AnnotatorOverrider_AE
					.createAnalysisEngineDescription(tsd, TEST_SET_ANNOTATOR_ID, "TS", "TS", "CCP",
							new int[] { GS_ANNOTATOR_ID }));

		} catch (ResourceInitializationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Iterator<JCas> getBc3JCasIterator() {
		final Iterator<GnTrainingSet1Document> documentIterator = Arrays.asList(GnTrainingSet1Document.values())
				.iterator();
		return new Iterator<JCas>() {

			private JCas nextJCas = null;

			public boolean hasNext() {
				if (nextJCas == null) {
					if (documentIterator.hasNext()) {
						nextJCas = initializeJCas(documentIterator.next());
						return true;
					}
					return false;
				}
				return true;
			}

			public JCas next() {
				if (!hasNext())
					throw new NoSuchElementException();
				JCas jcasToReturn = nextJCas;
				nextJCas = null;
				return jcasToReturn;
			}

			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}

		};

	}

	/**
	 * @param next
	 * @return
	 */
	protected JCas initializeJCas(GnTrainingSet1Document bc3Document) {
		String documentXml = bc3Document.documentText();
		try {
			JCas jCas = JCasFactory.createJCas(tsd);
			JCas xmlView = View_Util.getView(jCas, XML_VIEW_NAME);
			xmlView.setDocumentText(documentXml);
			pmcXmlToTextConverter.process(jCas);
			UIMA_Util.setDocumentID(jCas, bc3Document.name());
			return jCas;
		} catch (UIMAException e) {
			throw new RuntimeException(e);
		}
	}

	public PRFResult evaluateNormalizationPipeline(AnalysisEngineDescription normalizationDescription,
			AnalysisEngineType aeType) throws AnalysisEngineProcessException, ResourceInitializationException {

		AnalysisEngine sentenceDetector = AnalysisEngineFactory.createPrimitive(AnalysisEngineFactory
				.createPrimitiveDescription(LingPipeSentenceDetector_AE.class, tsd,
						SentenceDetector_AE.PARAM_TREAT_LINE_BREAKS_AS_SENTENCE_BOUNDARIES, true));

		AnalysisEngine abnerBcDescription = AnalysisEngineFactory.createPrimitive(ABNER_AE
				.createAnalysisEngineDescription_BioCreative(tsd));
		AnalysisEngine abnerNlpbaDescription = AnalysisEngineFactory.createPrimitive(ABNER_AE
				.createAnalysisEngineDescription_NLPBA(tsd));
		AnalysisEngine bannerBcDescription = AnalysisEngineFactory.createPrimitive(BannerEntityTagger_AE
				.createAnalysisEngineDescription_BioCreative(tsd));

		AnalysisEngine geneToProteinConverter = AnalysisEngineFactory.createPrimitive(ClassMentionConverter_AE
				.createAnalysisEngineDescription(tsd, "protein", new String[] { "gene" }));

		AnalysisEngine consensusFilter = AnalysisEngineFactory.createPrimitive(AnalysisEngineFactory
				.createPrimitiveDescription(AnnotationConsensusFilter_AE.class, tsd,
						AnnotationConsensusFilter_AE.PARAM_CONSENSUS_THRESHOLD, 2,
						AnnotationConsensusFilter_AE.PARAM_ANNOTATION_TYPE_OF_INTEREST, "protein",
						AnnotationConsensusFilter_AE.PARAM_ANNOTATION_SETS_TO_IGNORE, new int[] {}));

		// // the annotation set id for consensus annotations is 88 - this AE will remove all other
		// // annotations
		// // zero is typically the annotation set for gold standard annotations
		// AnalysisEngine annotationSetFilter =
		// AnalysisEngineFactory.createPrimitive(AnalysisEngineFactory.createPrimitiveDescription(
		// AnnotationSetFilter_AE.class, tsd,
		// AnnotationSetFilter_AE.PARAM_ANNOTATIONSET_IDS_TO_KEEP_LIST,
		// new int[] { 88 , 0}));
		//
		// AnalysisEngine homologeneGroupNormalizer =
		// AnalysisEngineFactory.createPrimitive(HomologeneGroupGeneNormalizer_AE
		// .createAnalysisEngineDescription(tsd, homologeneDictionaryDirectory));

		AnalysisEngine normalizationEngine = null;
		if (aeType.equals(AnalysisEngineType.PRIMITIVE))
			normalizationEngine = AnalysisEngineFactory.createPrimitive(normalizationDescription);
		else if (aeType.equals(AnalysisEngineType.AGGREGATE))
			normalizationEngine = AnalysisEngineFactory.createAggregate(normalizationDescription);

		for (Iterator<JCas> jCasIter = getBc3JCasIterator(); jCasIter.hasNext();) {
			JCas jCas = jCasIter.next();

			logger.info("INITIAL ANNOTATION COUNT: " + getAnnotationCount(jCas));

			logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Adding GS annotations to the CAS");
			addGoldStandardDocumentAnnotations(jCas);
			logger.info("AFTER GS INSERTION ANNOTATION COUNT: " + getAnnotationCount(jCas));
			annotationSetOverrider_GoldStandard.process(jCas);
			annotatorOverrider_GoldStandard.process(jCas);

			logger.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Adding TEST SET annotations to the CAS");
			logger.info("BEFORE NORM ANNOTATION COUNT: " + getAnnotationCount(jCas));
			normalizationEngine.process(jCas);

			logger.info("AFTER NORM ANNOTATION COUNT: " + getAnnotationCount(jCas));
			// documentLevelAnnotationCreator_TEST_SET_EG.process(jCas);
			// logger.info("AFTER TEST EG DOC LEVEL ANNOTATION COUNT: " + getAnnotationCount(jCas));
			// documentLevelAnnotationCreator_TEST_SET_HG.process(jCas);
			// logger.info("AFTER TEST HG DOC LEVEL ANNOTATION COUNT: " + getAnnotationCount(jCas));
			annotationSetOverrider_TestSet.process(jCas);
			annotatorOverrider_TestSet.process(jCas);

			logger.info("BEFORE COMPARISON ANNOTATION COUNT: " + getAnnotationCount(jCas));
			annotationComparator.process(jCas);
			break;
		}
		normalizationEngine.collectionProcessComplete();
		annotationComparator.collectionProcessComplete();

		return null;

	}

	private int getAnnotationCount(JCas jcas) {
		return jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).size();
	}

	/**
	 * @param jCas
	 */
	private void addGoldStandardDocumentAnnotations(JCas jCas) {
		GnTrainingSet1Document document = GnTrainingSet1Document.valueOf(UIMA_Util.getDocumentID(jCas));
		try {
			createGoldStandardDocumentAnnotations(jCas, document.entrezGeneIds());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param jCas
	 * @param ids
	 * @throws Exception
	 */
	private void createGoldStandardDocumentAnnotations(JCas jCas, Set<? extends DataSourceIdentifier<?>> ids)
			throws Exception {
		for (DataSourceIdentifier<?> id : ids) {
			CCPTextAnnotation ccpTa = new CCPTextAnnotation(jCas);
			CCPClassMention cm = new CCPClassMention(jCas);
			cm.setMentionName("DocumentLevel-protein");
			ccpTa.setClassMention(cm);
			cm.setCcpTextAnnotation(ccpTa);
			String slotName = null;
			if (id.getDataSource().equals(DataSource.EG))
				slotName = CcpGeneIdAnnotationDecorator.ENTREZ_GENE_ID_SLOT_NAME;
			else if (id.getDataSource().equals(DataSource.HOMOLOGENE))
				slotName = CcpGeneIdAnnotationDecorator.HOMOLOGENE_GROUP_ID_SLOT_NAME;
			else
				throw new RuntimeException("unknown data source: " + id.getDataSource().name());
			TextAnnotationUtil.addSlotValue(new WrappedCCPTextAnnotation(ccpTa), slotName, id.getDataElement());
			new WrappedCCPTextAnnotation(ccpTa).setSpan(new Span(0, 1));
			ccpTa.addToIndexes();
		}
	}

}
