package edu.ucdenver.ccp.nlp.evaluation.bossy2013;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotationFactory;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;
import edu.ucdenver.ccp.nlp.evaluation.bossy2013.BossyMetric.ScoredAnnotationMatch;
import owltools.graph.OWLGraphWrapper;

/**
 * The GO concepts used in these unit tests are viewable as a graph here:
 * https://www.ebi.ac.uk/QuickGO/term/GO:0043231
 *
 */
public class BossyMetricTest {

	private static final BigDecimal DISTANCE_WEIGHT_FACTOR = BigDecimal.valueOf(0.65);

	@Test
	public void testScoreAllPossibleAnnotationMatches_JaccardSpan() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		testAnnot0.setAnnotationID("test_0");
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		testAnnot1.setAnnotationID("test_1");

		// inexact match
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));
		testAnnot2.setAnnotationID("test_2");

		testIdToAnnotMap.put("test_0", testAnnot0);
		testIdToAnnotMap.put("test_1", testAnnot1);
		testIdToAnnotMap.put("test_2", testAnnot2);

		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		refAnnot0.setAnnotationID("ref_0");
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		refAnnot1.setAnnotationID("ref_1");
		refIdToAnnotMap.put("ref_0", refAnnot0);
		refIdToAnnotMap.put("ref_1", refAnnot1);

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_1", "ref_1", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_1", testToOverlappingReferenceAnnotIdMap);

		List<ScoredAnnotationMatch> scoredAnnotationMatches = bm.scoreAllPossibleAnnotationMatches(
				BoundaryMatchStrategy.JACCARD, testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap);

		BigDecimal inexactMatchScore = BossyMetric
				.computeBoundaryJaccardScore(refAnnot1.getSpans(), testAnnot2.getSpans())
				.multiply(bm.computeWangSemanticSimilarity("GO:0043231", "GO:0043229"));

		Map<String, BigDecimal> matchKeyToScoreMap = new HashMap<String, BigDecimal>();
		matchKeyToScoreMap.put("test_0 -- ref_0", BigDecimal.valueOf(1.0));
		matchKeyToScoreMap.put("test_1 -- ref_1", BigDecimal.valueOf(1.0));
		matchKeyToScoreMap.put("test_2 -- ref_1", inexactMatchScore);

		assertEquals(3, scoredAnnotationMatches.size());

		for (ScoredAnnotationMatch sam : scoredAnnotationMatches) {
			String key = sam.getTestAnnotId() + " -- " + sam.getRefAnnotId();
			assertThat(sam.getScore(), comparesEqualTo(matchKeyToScoreMap.get(key)));
		}
	}

	@Test
	public void testScoreAllPossibleAnnotationMatches_exactSpan() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		testAnnot0.setAnnotationID("test_0");
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		testAnnot1.setAnnotationID("test_1");

		// inexact match
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));
		testAnnot2.setAnnotationID("test_2");

		testIdToAnnotMap.put("test_0", testAnnot0);
		testIdToAnnotMap.put("test_1", testAnnot1);
		testIdToAnnotMap.put("test_2", testAnnot2);

		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		refAnnot0.setAnnotationID("ref_0");
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		refAnnot1.setAnnotationID("ref_1");
		refIdToAnnotMap.put("ref_0", refAnnot0);
		refIdToAnnotMap.put("ref_1", refAnnot1);

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_1", "ref_1", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_1", testToOverlappingReferenceAnnotIdMap);

		List<ScoredAnnotationMatch> scoredAnnotationMatches = bm.scoreAllPossibleAnnotationMatches(
				BoundaryMatchStrategy.EXACT, testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap);

		Map<String, BigDecimal> matchKeyToScoreMap = new HashMap<String, BigDecimal>();
		matchKeyToScoreMap.put("test_0 -- ref_0", BigDecimal.valueOf(1.0));
		matchKeyToScoreMap.put("test_1 -- ref_1", BigDecimal.valueOf(1.0));
		matchKeyToScoreMap.put("test_2 -- ref_1", BigDecimal.valueOf(0.0));

		assertEquals(3, scoredAnnotationMatches.size());

		for (ScoredAnnotationMatch sam : scoredAnnotationMatches) {
			String key = sam.getTestAnnotId() + " -- " + sam.getRefAnnotId();
			assertThat(sam.getScore(), comparesEqualTo(matchKeyToScoreMap.get(key)));
		}

	}

	@Test
	public void testFinalizeScoredMatches() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);

		Set<String> pairedRefAnnotIds = new HashSet<String>();
		Set<String> pairedTestAnnotIds = new HashSet<String>();

		BigDecimal inexactMatchScore = BossyMetric
				.computeBoundaryJaccardScore(CollectionsUtil.createList(new Span(20, 50)),
						CollectionsUtil.createList(new Span(20, 33)))
				.multiply(bm.computeWangSemanticSimilarity("GO:0043231", "GO:0043229"));
		List<ScoredAnnotationMatch> scoredMatches = CollectionsUtil.createList(
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_0", "test_0"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_1", "test_1"),
				new ScoredAnnotationMatch(inexactMatchScore, "ref_1", "test_2"));

		List<ScoredAnnotationMatch> finalizeScoredMatches = BossyMetric.finalizeScoredMatches(scoredMatches,
				pairedRefAnnotIds, pairedTestAnnotIds);

		Map<String, BigDecimal> matchKeyToExpectedScoreMap = new HashMap<String, BigDecimal>();
		matchKeyToExpectedScoreMap.put("test_0 -- ref_0", BigDecimal.valueOf(1.0));
		matchKeyToExpectedScoreMap.put("test_1 -- ref_1", BigDecimal.valueOf(1.0));

		assertEquals(2, finalizeScoredMatches.size());

		for (ScoredAnnotationMatch sam : finalizeScoredMatches) {
			String key = sam.getTestAnnotId() + " -- " + sam.getRefAnnotId();
			assertThat(sam.getScore(), comparesEqualTo(matchKeyToExpectedScoreMap.get(key)));
		}

		Set<String> expectedPairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1");
		Set<String> expectedPairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1");

		assertEquals(expectedPairedRefAnnotIds, pairedRefAnnotIds);
		assertEquals(expectedPairedTestAnnotIds, pairedTestAnnotIds);
	}

	@Test
	public void testScoreAnnotationMatches() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		testAnnot0.setAnnotationID("test_0");
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		testAnnot1.setAnnotationID("test_1");

		// inexact match
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));
		testAnnot2.setAnnotationID("test_2");

		testIdToAnnotMap.put("test_0", testAnnot0);
		testIdToAnnotMap.put("test_1", testAnnot1);
		testIdToAnnotMap.put("test_2", testAnnot2);

		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		refAnnot0.setAnnotationID("ref_0");
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		refAnnot1.setAnnotationID("ref_1");
		refIdToAnnotMap.put("ref_0", refAnnot0);
		refIdToAnnotMap.put("ref_1", refAnnot1);

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_1", "ref_1", testToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_1", testToOverlappingReferenceAnnotIdMap);

		Set<String> pairedRefAnnotIds = new HashSet<String>();
		Set<String> pairedTestAnnotIds = new HashSet<String>();

		List<ScoredAnnotationMatch> scoredAnnotationMatches = bm.scoreAnnotationMatches(BoundaryMatchStrategy.JACCARD,
				testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap, pairedRefAnnotIds,
				pairedTestAnnotIds);

		Map<String, BigDecimal> matchKeyToExpectedScoreMap = new HashMap<String, BigDecimal>();
		matchKeyToExpectedScoreMap.put("test_0 -- ref_0", BigDecimal.valueOf(1.0));
		matchKeyToExpectedScoreMap.put("test_1 -- ref_1", BigDecimal.valueOf(1.0));

		assertEquals(2, scoredAnnotationMatches.size());

		for (ScoredAnnotationMatch sam : scoredAnnotationMatches) {
			String key = sam.getTestAnnotId() + " -- " + sam.getRefAnnotId();
			assertThat(sam.getScore(), comparesEqualTo(matchKeyToExpectedScoreMap.get(key)));
		}

		Set<String> expectedPairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1");
		Set<String> expectedPairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1");

		assertEquals(expectedPairedRefAnnotIds, pairedRefAnnotIds);
		assertEquals(expectedPairedTestAnnotIds, pairedTestAnnotIds);
	}

	@Test
	public void testScoreAnnotationMatches_noTestAnnots() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();

		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		refAnnot0.setAnnotationID("ref_0");
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		refAnnot1.setAnnotationID("ref_1");
		refIdToAnnotMap.put("ref_0", refAnnot0);
		refIdToAnnotMap.put("ref_1", refAnnot1);

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();

		Set<String> pairedRefAnnotIds = new HashSet<String>();
		Set<String> pairedTestAnnotIds = new HashSet<String>();

		List<ScoredAnnotationMatch> scoredAnnotationMatches = bm.scoreAnnotationMatches(BoundaryMatchStrategy.JACCARD,
				testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap, pairedRefAnnotIds,
				pairedTestAnnotIds);

		Map<String, BigDecimal> matchKeyToExpectedScoreMap = new HashMap<String, BigDecimal>();

		assertEquals(0, scoredAnnotationMatches.size());

		for (ScoredAnnotationMatch sam : scoredAnnotationMatches) {
			String key = sam.getTestAnnotId() + " -- " + sam.getRefAnnotId();
			assertThat(sam.getScore(), comparesEqualTo(matchKeyToExpectedScoreMap.get(key)));
		}

		Set<String> expectedPairedRefAnnotIds = CollectionsUtil.createSet();
		Set<String> expectedPairedTestAnnotIds = CollectionsUtil.createSet();

		assertEquals(expectedPairedRefAnnotIds, pairedRefAnnotIds);
		assertEquals(expectedPairedTestAnnotIds, pairedTestAnnotIds);
	}

	@Test
	public void testScoreAnnotationMatches_noGoldAnnots() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();
		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		testAnnot0.setAnnotationID("test_0");
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		testAnnot1.setAnnotationID("test_1");

		// inexact match
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));
		testAnnot2.setAnnotationID("test_2");

		testIdToAnnotMap.put("test_0", testAnnot0);
		testIdToAnnotMap.put("test_1", testAnnot1);
		testIdToAnnotMap.put("test_2", testAnnot2);

		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();

		Set<String> pairedRefAnnotIds = new HashSet<String>();
		Set<String> pairedTestAnnotIds = new HashSet<String>();

		List<ScoredAnnotationMatch> scoredAnnotationMatches = bm.scoreAnnotationMatches(BoundaryMatchStrategy.JACCARD,
				testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap, pairedRefAnnotIds,
				pairedTestAnnotIds);

		assertEquals(0, scoredAnnotationMatches.size());

		Set<String> expectedPairedRefAnnotIds = CollectionsUtil.createSet();
		Set<String> expectedPairedTestAnnotIds = CollectionsUtil.createSet();

		assertEquals(expectedPairedRefAnnotIds, pairedRefAnnotIds);
		assertEquals(expectedPairedTestAnnotIds, pairedTestAnnotIds);
	}

	@Test
	public void testComputeSlotErrorRate_Exact() {
		Set<String> testIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> refIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList(
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_0", "test_0"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_1", "test_1"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_2", "test_2"));
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(0, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(3, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(3.0)));
		assertThat(ser.getPrecision(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getRecall(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getSER(), comparesEqualTo(BigDecimal.valueOf(0.0)));
	}

	@Test
	public void testComputeSlotErrorRate_NoTestAnnots() {
		Set<String> testIds = CollectionsUtil.createSet();
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet();
		Set<String> refIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet();
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList();
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(0, ser.getInsertions());
		assertEquals(3, ser.getDeletions());
		assertEquals(3, ser.getReferenceCount());
		assertEquals(0, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getPrecision(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getRecall(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getSER(), comparesEqualTo(BigDecimal.valueOf(1.0)));
	}

	@Test
	public void testComputeSlotErrorRate_NoRefAnnots() {
		Set<String> testIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet();
		Set<String> refIds = CollectionsUtil.createSet();
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet();
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList();
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(3, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(0, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getPrecision(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getRecall(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getSER(), comparesEqualTo(BigDecimal.valueOf(1.0)));
	}

	@Test
	public void testComputeSlotErrorRate_NoTestAndNoRefAnnots() {
		Set<String> testIds = CollectionsUtil.createSet();
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet();
		Set<String> refIds = CollectionsUtil.createSet();
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet();
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList();
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(0, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(0, ser.getReferenceCount());
		assertEquals(0, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(0.0)));
		assertThat(ser.getPrecision(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getRecall(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(1.0)));
		assertThat(ser.getSER(), comparesEqualTo(BigDecimal.valueOf(0.0)));
	}

	@Test
	public void testComputeSlotErrorRate_Inexact() {
		Set<String> testIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> refIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList(
				new ScoredAnnotationMatch(BigDecimal.valueOf(0.65), "ref_0", "test_0"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_1", "test_1"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_2", "test_2"));
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(0, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(3, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(2.65)));
		assertThat(ser.getPrecision(), comparesEqualTo(
				BigDecimal.valueOf(2.65).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getRecall(), comparesEqualTo(
				BigDecimal.valueOf(2.65).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(2 * 2.65 / 3.0 * 2.65 / 3.0)
				.divide(BigDecimal.valueOf(2.65 / 3.0 + 2.65 / 3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getSER(), comparesEqualTo(
				BigDecimal.valueOf(0.35).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testComputeSlotErrorRate_FP() {
		Set<String> testIds = CollectionsUtil.createSet("test_0", "test_1", "test_2");
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1");
		Set<String> refIds = CollectionsUtil.createSet("ref_0", "ref_1");
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1");
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList(
				new ScoredAnnotationMatch(BigDecimal.valueOf(0.65), "ref_0", "test_0"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_1", "test_1"));
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(1, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(2, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(1.65)));
		assertThat(ser.getPrecision(), comparesEqualTo(
				BigDecimal.valueOf(1.65).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getRecall(), comparesEqualTo(
				BigDecimal.valueOf(1.65).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(2 * 1.65 / 2.0 * 1.65 / 3.0)
				.divide(BigDecimal.valueOf(1.65 / 2.0 + 1.65 / 3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getSER(), comparesEqualTo(
				BigDecimal.valueOf(1.35).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testComputeSlotErrorRate_FN() {
		Set<String> testIds = CollectionsUtil.createSet("test_0", "test_1");
		Set<String> pairedTestAnnotIds = CollectionsUtil.createSet("test_0", "test_1");
		Set<String> refIds = CollectionsUtil.createSet("ref_0", "ref_1", "ref_2");
		Set<String> pairedRefAnnotIds = CollectionsUtil.createSet("ref_0", "ref_1");
		List<ScoredAnnotationMatch> finalScoredMatches = CollectionsUtil.createList(
				new ScoredAnnotationMatch(BigDecimal.valueOf(0.65), "ref_0", "test_0"),
				new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), "ref_1", "test_1"));
		SlotErrorRate ser = BossyMetric.computeSlotErrorRate(testIds, refIds, pairedRefAnnotIds, pairedTestAnnotIds,
				finalScoredMatches);

		assertEquals(0, ser.getInsertions());
		assertEquals(1, ser.getDeletions());
		assertEquals(3, ser.getReferenceCount());
		assertEquals(2, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(1.65)));
		assertThat(ser.getPrecision(), comparesEqualTo(
				BigDecimal.valueOf(1.65).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getRecall(), comparesEqualTo(
				BigDecimal.valueOf(1.65).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(2 * 1.65 / 2.0 * 1.65 / 3.0)
				.divide(BigDecimal.valueOf(1.65 / 2.0 + 1.65 / 3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getSER(), comparesEqualTo(
				BigDecimal.valueOf(1.35).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testEvaluateAnnotations() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));

		Set<TextAnnotation> testAnnots = new HashSet<TextAnnotation>();
		testAnnots.add(testAnnot0);
		testAnnots.add(testAnnot1);
		testAnnots.add(testAnnot2);

		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));

		Set<TextAnnotation> refAnnots = new HashSet<TextAnnotation>();
		refAnnots.add(refAnnot0);
		refAnnots.add(refAnnot1);

		SlotErrorRate ser = bm.evaluate(refAnnots, testAnnots, BoundaryMatchStrategy.JACCARD);

		assertEquals(1, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(2, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(2.0)));
		assertThat(ser.getPrecision(),
				comparesEqualTo(BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getRecall(),
				comparesEqualTo(BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(2.0 * 1 * 2.0 / 3.0)
				.divide(BigDecimal.valueOf(1 + 2.0 / 3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getSER(),
				comparesEqualTo(BigDecimal.valueOf(1.0).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testEvaluateAnnotations_ignoreConceptsNotInOntology() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults("123456");

		TextAnnotation testAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		TextAnnotation testAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));
		TextAnnotation testAnnot2 = factory.createAnnotation(20, 33, "intracellular",
				new DefaultClassMention("GO:0043229"));
		TextAnnotation testAnnot3 = factory.createAnnotation(15, 20, "blah", new DefaultClassMention("GO:0001234"));

		Set<TextAnnotation> testAnnots = new HashSet<TextAnnotation>();
		testAnnots.add(testAnnot0);
		testAnnots.add(testAnnot1);
		testAnnots.add(testAnnot2);
		testAnnots.add(testAnnot3);

		TextAnnotation refAnnot0 = factory.createAnnotation(0, 13, "intracellular",
				new DefaultClassMention("GO:0005622"));
		TextAnnotation refAnnot1 = factory.createAnnotation(20, 50, "intracellular membrane-bounded",
				new DefaultClassMention("GO:0043231"));

		TextAnnotation refAnnot2 = factory.createAnnotation(55, 60, "blah", new DefaultClassMention("GO:0007890"));

		Set<TextAnnotation> refAnnots = new HashSet<TextAnnotation>();
		refAnnots.add(refAnnot0);
		refAnnots.add(refAnnot1);
		refAnnots.add(refAnnot2);

		SlotErrorRate ser = bm.evaluate(refAnnots, testAnnots, BoundaryMatchStrategy.JACCARD);

		assertEquals(1, ser.getInsertions());
		assertEquals(0, ser.getDeletions());
		assertEquals(2, ser.getReferenceCount());
		assertEquals(3, ser.getPredictedCount());
		assertThat(ser.getMatches(), comparesEqualTo(BigDecimal.valueOf(2.0)));
		assertThat(ser.getPrecision(),
				comparesEqualTo(BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getRecall(),
				comparesEqualTo(BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getFScore(), comparesEqualTo(BigDecimal.valueOf(2.0 * 1 * 2.0 / 3.0)
				.divide(BigDecimal.valueOf(1 + 2.0 / 3.0), 10, BigDecimal.ROUND_HALF_UP)));
		assertThat(ser.getSER(),
				comparesEqualTo(BigDecimal.valueOf(1.0).divide(BigDecimal.valueOf(2.0), 10, BigDecimal.ROUND_HALF_UP)));
	}

	@Test
	public void testPopulateTestIdToAnnotMap() {
		List<TextAnnotation> annots = getTestAnnotations();

		Map<String, TextAnnotation> testIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateTestIdToAnnotMap(annots);

		assertEquals("map should contain 3 values", 3, testIdToAnnotMap.size());
		assertEquals(testIdToAnnotMap.get("test_0"), annots.get(0));
		assertEquals(testIdToAnnotMap.get("test_1"), annots.get(1));
		assertEquals(testIdToAnnotMap.get("test_2"), annots.get(2));

	}

	@Test
	public void testPopulateReferenceIdToAnnotMap_Exact() {

		List<TextAnnotation> testAnnots = getTestAnnotations();
		List<TextAnnotation> refAnnots = getReferenceAnnotations_Exact();

		Map<String, TextAnnotation> testIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateTestIdToAnnotMap(testAnnots);

		ArrayList<TextAnnotation> sortedTestAnnots = new ArrayList<TextAnnotation>(testIdToAnnotMap.values());
		Collections.sort(sortedTestAnnots, TextAnnotation.BY_SPAN());

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		Map<String, TextAnnotation> referenceIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateReferenceIdToAnnotMap(refAnnots, sortedTestAnnots,
								testToOverlappingReferenceAnnotIdMap);

		assertEquals("map should contain 3 values", 3, testIdToAnnotMap.size());
		assertEquals(referenceIdToAnnotMap.get("ref_0"), refAnnots.get(0));
		assertEquals(referenceIdToAnnotMap.get("ref_1"), refAnnots.get(1));
		assertEquals(referenceIdToAnnotMap.get("ref_2"), refAnnots.get(2));

		Map<String, Set<String>> expectedTestToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", expectedTestToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_1", "ref_1", expectedTestToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_2", expectedTestToOverlappingReferenceAnnotIdMap);

		assertEquals(expectedTestToOverlappingReferenceAnnotIdMap, testToOverlappingReferenceAnnotIdMap);

	}

	@Test
	public void testPopulateReferenceIdToAnnotMap_1Extra() {

		List<TextAnnotation> testAnnots = getTestAnnotations();
		List<TextAnnotation> refAnnots = getReferenceAnnotations_Extra1();

		Map<String, TextAnnotation> testIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateTestIdToAnnotMap(testAnnots);
		ArrayList<TextAnnotation> sortedTestAnnots = new ArrayList<TextAnnotation>(testIdToAnnotMap.values());
		Collections.sort(sortedTestAnnots, TextAnnotation.BY_SPAN());

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		Map<String, TextAnnotation> referenceIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateReferenceIdToAnnotMap(refAnnots, sortedTestAnnots,
								testToOverlappingReferenceAnnotIdMap);

		assertEquals("map should contain 3 values", 3, testIdToAnnotMap.size());
		assertEquals(referenceIdToAnnotMap.get("ref_0"), refAnnots.get(0));
		assertEquals(referenceIdToAnnotMap.get("ref_1"), refAnnots.get(1));
		assertEquals(referenceIdToAnnotMap.get("ref_2"), refAnnots.get(2));
		assertEquals(referenceIdToAnnotMap.get("ref_3"), refAnnots.get(3));

		Map<String, Set<String>> expectedTestToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", expectedTestToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_1", "ref_1", expectedTestToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_2", expectedTestToOverlappingReferenceAnnotIdMap);

		assertEquals(expectedTestToOverlappingReferenceAnnotIdMap, testToOverlappingReferenceAnnotIdMap);
	}

	@Test
	public void testPopulateReferenceIdToAnnotMap_Missing1() {

		List<TextAnnotation> testAnnots = getTestAnnotations();
		List<TextAnnotation> refAnnots = getReferenceAnnotations_Missing1();

		Map<String, TextAnnotation> testIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateTestIdToAnnotMap(testAnnots);

		ArrayList<TextAnnotation> sortedTestAnnots = new ArrayList<TextAnnotation>(testIdToAnnotMap.values());
		Collections.sort(sortedTestAnnots, TextAnnotation.BY_SPAN());

		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		Map<String, TextAnnotation> referenceIdToAnnotMap = new BossyMetric(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"), DISTANCE_WEIGHT_FACTOR)
						.populateReferenceIdToAnnotMap(refAnnots, sortedTestAnnots,
								testToOverlappingReferenceAnnotIdMap);

		assertEquals("map should contain 3 values", 3, testIdToAnnotMap.size());
		assertEquals(referenceIdToAnnotMap.get("ref_0"), refAnnots.get(0));
		assertEquals(referenceIdToAnnotMap.get("ref_1"), refAnnots.get(1));

		Map<String, Set<String>> expectedTestToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		CollectionsUtil.addToOne2ManyUniqueMap("test_0", "ref_0", expectedTestToOverlappingReferenceAnnotIdMap);
		CollectionsUtil.addToOne2ManyUniqueMap("test_2", "ref_1", expectedTestToOverlappingReferenceAnnotIdMap);

		assertEquals(expectedTestToOverlappingReferenceAnnotIdMap, testToOverlappingReferenceAnnotIdMap);
	}

	public List<TextAnnotation> getTestAnnotations() {
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults();

		TextAnnotation cellAnnot = factory.createAnnotation(0, 5, "cell", new DefaultClassMention("GO:0005623"));
		TextAnnotation neuronAnnot = factory.createAnnotation(10, 16, "neuron", new DefaultClassMention("GO:0005622"));
		TextAnnotation brainAnnot = factory.createAnnotation(20, 25, "brain", new DefaultClassMention("GO:0043229"));
		TextAnnotation brainAnnot2 = factory.createAnnotation(19, 25, "brain",
				new DefaultClassMention("UBERON:0000345"));

		List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
		annots.add(cellAnnot);
		annots.add(neuronAnnot);
		annots.add(brainAnnot);
		annots.add(brainAnnot2);
		return annots;
	}

	public List<TextAnnotation> getReferenceAnnotations_Exact() {
		return getTestAnnotations();
	}

	public List<TextAnnotation> getReferenceAnnotations_Offset() {
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults();

		TextAnnotation cellAnnot = factory.createAnnotation(0, 5, "cell", new DefaultClassMention("GO:0005623"));
		TextAnnotation neuronAnnot = factory.createAnnotation(10, 18, "neuron", new DefaultClassMention("GO:0005622"));
		TextAnnotation brainAnnot = factory.createAnnotation(19, 25, "brain", new DefaultClassMention("GO:0043229"));
		TextAnnotation brainAnnot2 = factory.createAnnotation(19, 25, "brain",
				new DefaultClassMention("UBERON:0000345"));

		List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
		annots.add(cellAnnot);
		annots.add(neuronAnnot);
		annots.add(brainAnnot);
		annots.add(brainAnnot2);
		return annots;
	}

	public List<TextAnnotation> getReferenceAnnotations_Missing1() {
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults();

		TextAnnotation cellAnnot = factory.createAnnotation(0, 5, "cell", new DefaultClassMention("GO:0005623"));
		TextAnnotation brainAnnot = factory.createAnnotation(20, 25, "brain", new DefaultClassMention("GO:0043229"));

		List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
		annots.add(cellAnnot);
		annots.add(brainAnnot);
		return annots;
	}

	public List<TextAnnotation> getReferenceAnnotations_Extra1() {
		TextAnnotationFactory factory = TextAnnotationFactory.createFactoryWithDefaults();

		TextAnnotation cellAnnot = factory.createAnnotation(0, 5, "cell", new DefaultClassMention("GO:0005623"));
		TextAnnotation neuronAnnot = factory.createAnnotation(10, 18, "neuron", new DefaultClassMention("GO:0005622"));
		TextAnnotation brainAnnot = factory.createAnnotation(20, 25, "brain", new DefaultClassMention("GO:0043229"));
		TextAnnotation membraneAnnot = factory.createAnnotation(30, 38, "membrane",
				new DefaultClassMention("GO:0043231"));

		List<TextAnnotation> annots = new ArrayList<TextAnnotation>();
		annots.add(cellAnnot);
		annots.add(neuronAnnot);
		annots.add(brainAnnot);
		annots.add(membraneAnnot);
		return annots;
	}

	@Test
	public void testScoredAnnotationMatchSorting() {
		List<ScoredAnnotationMatch> scoredMatches = new ArrayList<ScoredAnnotationMatch>();

		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.00054), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.675), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.67455), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.324), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.789), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.943), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.521), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(1.0), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.00012), null, null));
		scoredMatches.add(new ScoredAnnotationMatch(BigDecimal.valueOf(0.1234), null, null));

		Collections.sort(scoredMatches);

		List<BigDecimal> expectedScoresInOrder = CollectionsUtil.createList(BigDecimal.valueOf(1.0),
				BigDecimal.valueOf(0.943), BigDecimal.valueOf(0.789), BigDecimal.valueOf(0.675),
				BigDecimal.valueOf(0.67455), BigDecimal.valueOf(0.521), BigDecimal.valueOf(0.324),
				BigDecimal.valueOf(0.1234), BigDecimal.valueOf(0.00054), BigDecimal.valueOf(0.00012));

		for (ScoredAnnotationMatch scoredMatch : scoredMatches) {
			assertThat(scoredMatch.getScore(), comparesEqualTo(expectedScoresInOrder.remove(0)));
		}

	}

	@Test
	public void testBossyMetric_wangSimilarity() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);
		BigDecimal wangScore = bm.computeWangSemanticSimilarity("GO:0043231", "GO:0043229");

		BigDecimal sv231 = BigDecimal.valueOf(1).add(DISTANCE_WEIGHT_FACTOR.pow(1)).add(DISTANCE_WEIGHT_FACTOR.pow(1))
				.add(DISTANCE_WEIGHT_FACTOR.pow(2)).add(DISTANCE_WEIGHT_FACTOR.pow(2))
				.add(DISTANCE_WEIGHT_FACTOR.pow(3)).add(DISTANCE_WEIGHT_FACTOR.pow(3));

		BigDecimal sv229 = BigDecimal.valueOf(1).add(DISTANCE_WEIGHT_FACTOR).pow(1).add(DISTANCE_WEIGHT_FACTOR.pow(1))
				.add(DISTANCE_WEIGHT_FACTOR.pow(2)).add(DISTANCE_WEIGHT_FACTOR.pow(2));

		BigDecimal overlap = BigDecimal.valueOf(1).add(DISTANCE_WEIGHT_FACTOR).pow(1).add(DISTANCE_WEIGHT_FACTOR.pow(1))
				.add(DISTANCE_WEIGHT_FACTOR.pow(2)).add(DISTANCE_WEIGHT_FACTOR.pow(2))
				.add(DISTANCE_WEIGHT_FACTOR.pow(1)).add(DISTANCE_WEIGHT_FACTOR.pow(2))
				.add(DISTANCE_WEIGHT_FACTOR.pow(2)).add(DISTANCE_WEIGHT_FACTOR.pow(3))
				.add(DISTANCE_WEIGHT_FACTOR.pow(3));

		BigDecimal expectedWangScore = overlap.divide(sv231.add(sv229), 10, BigDecimal.ROUND_HALF_UP);

		assertThat(wangScore, comparesEqualTo(expectedWangScore));

		BigDecimal sv227 = BigDecimal.valueOf(1).add(DISTANCE_WEIGHT_FACTOR).pow(1).add(DISTANCE_WEIGHT_FACTOR.pow(2));

		overlap = BigDecimal.valueOf(1).add(DISTANCE_WEIGHT_FACTOR).pow(1).add(DISTANCE_WEIGHT_FACTOR.pow(2))
				.add(DISTANCE_WEIGHT_FACTOR.pow(1)).add(DISTANCE_WEIGHT_FACTOR.pow(2))
				.add(DISTANCE_WEIGHT_FACTOR.pow(3));

		wangScore = bm.computeWangSemanticSimilarity("GO:0043231", "GO:0043227");

		expectedWangScore = overlap.divide(sv231.add(sv227), 10, BigDecimal.ROUND_HALF_UP);

		assertThat(wangScore, comparesEqualTo(expectedWangScore));

	}

	@Test
	public void testBossyMetric_populateSValueMap() {
		BossyMetric bm = new BossyMetric(ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample.obo"),
				DISTANCE_WEIGHT_FACTOR);

		Map<String, BigDecimal> conceptToSValueMap = bm.populateSvalueMap("GO:0043231");

		Map<String, BigDecimal> expectedConceptToSValueMap = new HashMap<String, BigDecimal>();
		/* @formatter:off */
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043231", BigDecimal.valueOf(1.0));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043229", BigDecimal.valueOf(0.65).pow(1));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043227", BigDecimal.valueOf(0.65).pow(1));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043226", BigDecimal.valueOf(0.65).pow(2));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0044424", BigDecimal.valueOf(0.65).pow(2));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0044464", BigDecimal.valueOf(0.65).pow(3));
		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0005575", BigDecimal.valueOf(0.65).pow(3));
		/* @formatter:on */

		assertEquals("s-value map should have the expected number of entries", expectedConceptToSValueMap.size(),
				conceptToSValueMap.size());
		for (Entry<String, BigDecimal> entry : conceptToSValueMap.entrySet()) {
			assertThat("testing value for " + entry.getKey(), entry.getValue(),
					comparesEqualTo(expectedConceptToSValueMap.get(entry.getKey())));
		}

	}

	@Test
	public void testAdaptedJaccardIndex() {
		List<Span> referenceSpans = CollectionsUtil.createList(new Span(0, 5));
		List<Span> testSpans = CollectionsUtil.createList(new Span(0, 5));
		BigDecimal jaccardScore = BossyMetric.computeBoundaryJaccardScore(referenceSpans, testSpans);
		assertThat(jaccardScore, comparesEqualTo(BigDecimal.valueOf(1.0)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		jaccardScore = BossyMetric.computeBoundaryJaccardScore(referenceSpans, testSpans);
		assertThat(jaccardScore, comparesEqualTo(BigDecimal.valueOf(1.0)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		jaccardScore = BossyMetric.computeBoundaryJaccardScore(referenceSpans, testSpans);
		assertThat(jaccardScore, comparesEqualTo(
				BigDecimal.valueOf(8.0).divide(BigDecimal.valueOf(10 + 8 - 8), 10, BigDecimal.ROUND_HALF_UP)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 20));
		jaccardScore = BossyMetric.computeBoundaryJaccardScore(referenceSpans, testSpans);
		assertThat(jaccardScore, comparesEqualTo(
				BigDecimal.valueOf(8.0).divide(BigDecimal.valueOf(15 + 8 - 8), 10, BigDecimal.ROUND_HALF_UP)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(5, 8), new Span(16, 20));
		jaccardScore = BossyMetric.computeBoundaryJaccardScore(referenceSpans, testSpans);
		assertThat(jaccardScore, comparesEqualTo(BigDecimal.valueOf(0.0)));

	}

	@Test
	public void testComputeBoundaryMatchScore() {
		List<Span> referenceSpans = CollectionsUtil.createList(new Span(0, 5));
		List<Span> testSpans = CollectionsUtil.createList(new Span(0, 5));
		BigDecimal boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.JACCARD);

		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(1.0)));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.EXACT);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(1.0)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.JACCARD);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(1)));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.EXACT);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(1)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 15));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.JACCARD);
		assertThat(boundaryMatchScore, comparesEqualTo(
				BigDecimal.valueOf(8.0).divide(BigDecimal.valueOf(10 + 8 - 8), 10, BigDecimal.ROUND_HALF_UP)));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.EXACT);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(0)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(0, 5), new Span(10, 20));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.JACCARD);
		assertThat(boundaryMatchScore, comparesEqualTo(
				BigDecimal.valueOf(8.0).divide(BigDecimal.valueOf(15 + 8 - 8), 10, BigDecimal.ROUND_HALF_UP)));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.EXACT);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(0)));

		referenceSpans = CollectionsUtil.createList(new Span(0, 3), new Span(10, 15));
		testSpans = CollectionsUtil.createList(new Span(5, 8), new Span(16, 20));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.JACCARD);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(0)));
		boundaryMatchScore = BossyMetric.computeBoundaryMatchScore(referenceSpans, testSpans,
				BoundaryMatchStrategy.EXACT);
		assertThat(boundaryMatchScore, comparesEqualTo(BigDecimal.valueOf(0)));
	}

	@Test
	public void testExtConceptExtractionFromOntology() throws OWLOntologyCreationException, IOException {
		OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(
				ClassPathUtil.getResourceStreamFromClasspath(getClass(), "sample_ext.obo"));
		OWLGraphWrapper graph = new OWLGraphWrapper(ont);

		OWLClass concept = BossyMetric.getOWLClass("GO_EXT:killing", graph);
		assertNotNull(concept);
		
		 concept = BossyMetric.getOWLClass("GO_EXT:muscle_structure_or_tissue_development", graph);
		assertNotNull(concept);

		for (OWLClass owlClass : graph.getAllOWLClasses()) {
			System.out.println("IRI: " + owlClass.getIRI().toString());
		}


		graph.close();
	}

	// @Test
	// public void testVsPythonImplementation() throws IOException {
	//
	// BossyMetric bm = new BossyMetric(
	// new FileInputStream(new File(
	// "/Users/bill/Dropbox/work/projects/craft-shared-task-2019/ca-task/bossy-bionlp2013/OntoBiotope_BioNLP-ST13.obo")),
	// DISTANCE_WEIGHT_FACTOR);
	//
	// SlotErrorRate ser = bm.evaluate(
	// new File(
	// "/Users/bill/Dropbox/work/projects/craft-shared-task-2019/ca-task/bossy-bionlp2013/BioNLP-ST-2013_Bacteria_Biotopes_train/craft-bionlp-format/gold"),
	// new File(
	// "/Users/bill/Dropbox/work/projects/craft-shared-task-2019/ca-task/bossy-bionlp2013/BioNLP-ST-2013_Bacteria_Biotopes_train/craft-bionlp-format/test"),
	// BoundaryMatchStrategy.JACCARD, ".a2");
	//
	// System.out.println("SER: " + ser.toString());
	//
	// assertEquals(0, ser.getInsertions());
	// assertEquals(1, ser.getDeletions());
	// assertEquals(21, ser.getReferenceCount());
	// assertEquals(20, ser.getPredictedCount());
	// assertEquals(19.8176241022, ser.getMatches(), 1e-4);
	// assertEquals(0.990881205112, ser.getPrecision(), 1e-4);
	// assertEquals(0.943696385821, ser.getRecall(), 1e-4);
	// assertEquals(0.966713370841, ser.getFScore(), 1e-4);
	// assertEquals(0.0563036141787, ser.getSER(), 1e-4);
	//
	// }
	//
	// @Test
	// public void testBossyMetric_populateSValueMap_biotope() throws FileNotFoundException {
	// BossyMetric bm = new BossyMetric(
	// new FileInputStream(new File(
	// "/Users/bill/Dropbox/work/projects/craft-shared-task-2019/ca-task/bossy-bionlp2013/OntoBiotope_BioNLP-ST13.obo")),
	// DISTANCE_WEIGHT_FACTOR);
	//
	// Map<String, Double> conceptToSValueMap = bm.populateSvalueMap("MBTO:00001514");
	//
	// Map<String, Double> sortedMap = CollectionsUtil.sortMapByValues(conceptToSValueMap,
	// SortOrder.DESCENDING);
	//
	// for (Entry<String, Double> entry : sortedMap.entrySet()) {
	// System.out.println(entry.getKey() + " -- " + entry.getValue());
	// }
	//
	// // Map<String, Double> expectedConceptToSValueMap = new HashMap<String, Double>();
////		/* @formatter:off */
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043231", 1.0);
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043229", 1.0 * Math.pow(0.65, 1));
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043227", 1.0 * Math.pow(0.65, 1));
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0043226", 1.0 * Math.pow(0.65, 1) * Math.pow(0.65, 2));
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0044424", 1.0 * Math.pow(0.65, 1) * Math.pow(0.65, 2));
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0044464", 1.0 * Math.pow(0.65, 1) * Math.pow(0.65, 2) * Math.pow(0.65, 3));
////		expectedConceptToSValueMap.put("http://purl.obolibrary.org/obo/GO_0005575", 1.0 * Math.pow(0.65, 1) * Math.pow(0.65, 2) * Math.pow(0.65, 3));
////		/* @formatter:on */
	// //
	// // assertEquals("s-value map should have the expected number of entries",
	// // expectedConceptToSValueMap.size(),
	// // conceptToSValueMap.size());
	// // for (Entry<String, Double> entry : conceptToSValueMap.entrySet()) {
	// // assertEquals("testing value for " + entry.getKey(),
	// // expectedConceptToSValueMap.get(entry.getKey()),
	// // entry.getValue(), 1e-4);
	// // }
	//
	// }

}
