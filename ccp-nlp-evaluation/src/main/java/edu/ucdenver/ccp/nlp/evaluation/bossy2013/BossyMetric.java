package edu.ucdenver.ccp.nlp.evaluation.bossy2013;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SloppySpanComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import owltools.graph.OWLGraphEdge;
import owltools.graph.OWLGraphWrapper;

/**
 * An implementation of the entity evaluation metric described in
 * 
 * BioNLP shared Task 2013 – An Overview of the Bacteria Biotope Task <br/>
 * Robert Bossy , Wiktoria Golik , Zorana Ratkovic, Philippe Bessières, and Claire Nédellec
 * 
 * Proceedings of the BioNLP Shared Task 2013 Workshop, pages 161–169, <br/>
 * Sofia, Bulgaria, August 9 2013
 *
 */
public class BossyMetric {

	private OWLGraphWrapper graph;
	private final BigDecimal distanceWeightFactor;

	public BossyMetric(InputStream ontologyStream) {
		/* Bossy et al. 2013 suggest a default weight factor of 0.65 */
		this(ontologyStream, BigDecimal.valueOf(0.65));
	}

	public BossyMetric(InputStream ontologyStream, BigDecimal distanceWeightFactor) {
		this.distanceWeightFactor = distanceWeightFactor;
		try {
			OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
			OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(ontologyStream);
			graph = new OWLGraphWrapper(ont);
		} catch (OWLOntologyCreationException e) {
			throw new IllegalStateException("Unable to load ontology", e);
		}
	}

	/**
	 * Evaluate the collection of test annotations against the set of reference annotations
	 * 
	 * @param boundaryMatchStrategy
	 *            EXACT to mandate that annotation spans match exactly, JACCARD to allow for
	 *            flexibility in matching annotation spans, i.e. any annotation that overlaps will
	 *            be scored as a potential match.
	 * 
	 * @param referenceAnnots
	 * @param testAnnots
	 * @return the evaluation performance in the form of the slot error rate (see Bossy et al 2013
	 *         for explanation)
	 */
	public SlotErrorRate evaluate(Collection<TextAnnotation> referenceAnnotations,
			Collection<TextAnnotation> testAnnotations, BoundaryMatchStrategy boundaryMatchStrategy) {
		List<TextAnnotation> refAnnots = new ArrayList<TextAnnotation>(
				new HashSet<TextAnnotation>(referenceAnnotations));
		List<TextAnnotation> testAnnots = new ArrayList<TextAnnotation>(new HashSet<TextAnnotation>(testAnnotations));

		Collections.sort(refAnnots, TextAnnotation.BY_SPAN());
		Collections.sort(testAnnots, TextAnnotation.BY_SPAN());

		/* assign a unique identifier to each annotation and populate the annot-id-to-annot maps */
		Map<String, TextAnnotation> testIdToAnnotMap = populateTestIdToAnnotMap(testAnnots);

		/*
		 * this map will eventually store mappings from test annotations to the reference
		 * annotations with which they overlap
		 */
		Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap = new HashMap<String, Set<String>>();
		Map<String, TextAnnotation> refIdToAnnotMap = populateReferenceIdToAnnotMap(refAnnots, testAnnots,
				testToOverlappingReferenceAnnotIdMap);

		/* keep track of test and reference annotations that get paired in a match */
		Set<String> pairedRefAnnotIds = new HashSet<String>();
		Set<String> pairedTestAnnotIds = new HashSet<String>();

		/*
		 * pair up annotations that overlap, score them, then keep high scoring pairs if there are
		 * annotations involved in multiple matches, i.e. if there are test annotations that overlap
		 * with multiple reference annotations, make the pairings based on the highest score. This
		 * strategy is described in Bossy et al 2013, Section 6.2 under the 'Entity pairing'
		 * heading.
		 */
		List<ScoredAnnotationMatch> finalScoredMatches = scoreAnnotationMatches(boundaryMatchStrategy, testIdToAnnotMap,
				testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap, pairedRefAnnotIds, pairedTestAnnotIds);

		return computeSlotErrorRate(testIdToAnnotMap.keySet(), refIdToAnnotMap.keySet(), pairedRefAnnotIds,
				pairedTestAnnotIds, finalScoredMatches);
	}

	/**
	 * @param testIds
	 * @param refIds
	 * @param pairedRefAnnotIds
	 * @param pairedTestAnnotIds
	 * @param finalScoredMatches
	 * @return the slot error rate given the scored annotations matches
	 */
	static SlotErrorRate computeSlotErrorRate(Set<String> testIds, Set<String> refIds, Set<String> pairedRefAnnotIds,
			Set<String> pairedTestAnnotIds, List<ScoredAnnotationMatch> finalScoredMatches) {
		/*
		 * At this point, finalScoredMatches holds all matches and their match scores. The
		 * difference between pairedRefAnnotIds and refIds will be the false negative (deletion)
		 * annotations. The difference between pairedTestAnnotIds and testIds will be the false
		 * positive (insertion) annotations.
		 */

		Set<String> falseNegativeAnnotIds = new HashSet<String>(refIds);
		falseNegativeAnnotIds.removeAll(pairedRefAnnotIds);

		Set<String> falsePositiveAnnotIds = new HashSet<String>(testIds);
		falsePositiveAnnotIds.removeAll(pairedTestAnnotIds);

		/* variables below correspond with those used in Bossy et al. 2013 */
		/* M = matches */
		/* D = deletions */
		/* I = insertions */
		/* N = number of entities in the reference */
		/* P = number of predicted entities */

		BigDecimal M = BigDecimal.valueOf(0.0);
		for (ScoredAnnotationMatch scoredMatch : finalScoredMatches) {
			// M += scoredMatch.getScore();
			M = M.add(scoredMatch.getScore());
		}

		int D = falseNegativeAnnotIds.size();
		int I = falsePositiveAnnotIds.size();
		int N = refIds.size();
		int P = testIds.size();

		return new SlotErrorRate(M, I, D, N, P);
	}

	/**
	 * Compute the score for all annotation matches. A score is generated for any test/reference
	 * annotation pairing where the test and reference annotations pass the boundary match test,
	 * then for annotations that may be involved in more than one test/reference pairing, the match
	 * score is used to keep the highest scoring pairs. This conforms with the statement in Bossy et
	 * al. 2013: "Each reference entity is paired with the predicted entity for which the similarity
	 * is the highest among non-zero similarities."
	 * 
	 * @param boundaryMatchStrategy
	 * @param testIdToAnnotMap
	 * @param testToOverlappingReferenceAnnotIdMap
	 * @param refIdToAnnotMap
	 * @param pairedRefAnnotIds
	 * @param pairedTestAnnotIds
	 * @return
	 */
	List<ScoredAnnotationMatch> scoreAnnotationMatches(BoundaryMatchStrategy boundaryMatchStrategy,
			Map<String, TextAnnotation> testIdToAnnotMap, Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap,
			Map<String, TextAnnotation> refIdToAnnotMap, Set<String> pairedRefAnnotIds,
			Set<String> pairedTestAnnotIds) {
		/*
		 * We now have a mapping of possible matches based on overlapping spans. It is possible
		 * there are test annotations that overlap with multiple reference annotations and/or
		 * reference annotations that overlap with multiple test annotations. We will score all
		 * matches, and the highest score will be used to pair the test-reference annotations when
		 * there is ambiguity. This conforms with the statement in Bossy et al. 2013:
		 * "Each reference entity is paired with the predicted entity for which the similarity is the highest among non-zero similarities."
		 */

		List<ScoredAnnotationMatch> scoredMatches = scoreAllPossibleAnnotationMatches(boundaryMatchStrategy,
				testIdToAnnotMap, testToOverlappingReferenceAnnotIdMap, refIdToAnnotMap);

		/*
		 * if there are annotations involved in multiple matches, keep the higher scoring match and
		 * do not use any others
		 */
		List<ScoredAnnotationMatch> finalScoredMatches = finalizeScoredMatches(scoredMatches, pairedRefAnnotIds,
				pairedTestAnnotIds);
		return finalScoredMatches;
	}

	/**
	 * Compute the initial match score for all test/reference annotation pairs where the test and
	 * reference annotations overlap.
	 * 
	 * @param boundaryMatchStrategy
	 * @param testIdToAnnotMap
	 * @param testToOverlappingReferenceAnnotIdMap
	 * @param refIdToAnnotMap
	 * @return
	 */
	List<ScoredAnnotationMatch> scoreAllPossibleAnnotationMatches(BoundaryMatchStrategy boundaryMatchStrategy,
			Map<String, TextAnnotation> testIdToAnnotMap, Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap,
			Map<String, TextAnnotation> refIdToAnnotMap) {
		List<ScoredAnnotationMatch> scoredMatches = new ArrayList<ScoredAnnotationMatch>();
		for (Entry<String, Set<String>> entry : testToOverlappingReferenceAnnotIdMap.entrySet()) {
			String testAnnotId = entry.getKey();
			TextAnnotation testAnnot = testIdToAnnotMap.get(testAnnotId);

			Set<String> refAnnotIds = entry.getValue();
			for (String refAnnotId : refAnnotIds) {
				TextAnnotation refAnnot = refIdToAnnotMap.get(refAnnotId);
				BigDecimal similarityScore = computeAnnotationSimilarity(refAnnot, testAnnot, boundaryMatchStrategy);
				scoredMatches.add(new ScoredAnnotationMatch(similarityScore, refAnnot.getAnnotationID(),
						testAnnot.getAnnotationID()));
			}
		}
		return scoredMatches;
	}

	/**
	 * Using the match scores, determine the final test/reference pairings such that higher match
	 * scores are used in cases where an annotation participates in more than one pairing. This
	 * strategy ensures that each test and reference annotation end up in a most a single pairing.
	 * 
	 * @param scoredMatches
	 * @param alreadyPairedRefAnnotIds
	 * @param alreadyPairedTestAnnotIds
	 * @return
	 */
	static List<ScoredAnnotationMatch> finalizeScoredMatches(List<ScoredAnnotationMatch> scoredMatches,
			Set<String> alreadyPairedRefAnnotIds, Set<String> alreadyPairedTestAnnotIds) {

		/* After sort, the scored annotation matches are ordered from highest score to lowest */
		Collections.sort(scoredMatches);

		List<ScoredAnnotationMatch> finalScoredMatches = new ArrayList<ScoredAnnotationMatch>();
		for (ScoredAnnotationMatch scoredMatch : scoredMatches) {
			/*
			 * if neither the reference nor the test annotation has already been paired, then keep
			 * this as a final scored match, else one of the annotations has already been paired in
			 * a higher scoring match so we discard this particular match.
			 */
			if (!alreadyPairedRefAnnotIds.contains(scoredMatch.getRefAnnotId())
					&& !alreadyPairedTestAnnotIds.contains(scoredMatch.getTestAnnotId())) {
				finalScoredMatches.add(scoredMatch);
				alreadyPairedRefAnnotIds.add(scoredMatch.getRefAnnotId());
				alreadyPairedTestAnnotIds.add(scoredMatch.getTestAnnotId());
			}
		}
		return finalScoredMatches;
	}

	/**
	 * 
	 * @param refAnnots
	 * @param testAnnots
	 * @param testToOverlappingReferenceAnnotIdMap
	 * @return a mapping of a unique identifier, e.g. ref_33, to each reference annotation. As a
	 *         side effect, the testToOverlappingReferenceAnnotIdMap is also populated such that it
	 *         maps identifiers for test annotations to identifiers of reference annotations with
	 *         which they overlap.
	 */
	static Map<String, TextAnnotation> populateReferenceIdToAnnotMap(List<TextAnnotation> refAnnots,
			List<TextAnnotation> testAnnots, Map<String, Set<String>> testToOverlappingReferenceAnnotIdMap) {
		Map<String, TextAnnotation> refIdToAnnotMap = new HashMap<String, TextAnnotation>();
		SloppySpanComparator ssc = new SloppySpanComparator();
		int index = 0;
		for (TextAnnotation refAnnot : refAnnots) {
			String id = "ref_" + index++;
			refAnnot.setAnnotationID(id);
			refIdToAnnotMap.put(id, refAnnot);
			for (TextAnnotation testAnnot : testAnnots) {
				if (ssc.overlaps(refAnnot.getSpans(), testAnnot.getSpans()) == 0) {
					CollectionsUtil.addToOne2ManyUniqueMap(testAnnot.getAnnotationID(), id,
							testToOverlappingReferenceAnnotIdMap);
				}
				if (testAnnot.getAggregateSpan().getSpanStart() > refAnnot.getAggregateSpan().getSpanEnd()) {
					/*
					 * then b/c the list of test annots is sorted, we can quit looking for
					 * overlapping annotations
					 */
					break;
				}
			}
		}
		return refIdToAnnotMap;
	}

	/**
	 * @param testAnnots
	 * @return a mapping from a unique id, e.g. test_12, to its corresponding test annotation
	 */
	static Map<String, TextAnnotation> populateTestIdToAnnotMap(List<TextAnnotation> testAnnots) {
		Map<String, TextAnnotation> testIdToAnnotMap = new HashMap<String, TextAnnotation>();

		/* assign a unique identifier to each annotation and populate the annot-id-to-annot maps */
		int index = 0;
		for (TextAnnotation testAnnot : testAnnots) {
			String id = "test_" + index++;
			testAnnot.setAnnotationID(id);
			testIdToAnnotMap.put(id, testAnnot);
		}

		return testIdToAnnotMap;
	}

	/**
	 * @param ta1
	 * @param ta2
	 * @param boundaryMatchStrategy
	 * @return a measure of annotation similarity as described in Bossy et al. 2013. This measure is
	 *         the boundary match score multiplied by the Wang semantic simlarity for the concepts
	 *         of each annotation.
	 */
	BigDecimal computeAnnotationSimilarity(TextAnnotation ta1, TextAnnotation ta2,
			BoundaryMatchStrategy boundaryMatchStrategy) {
		BigDecimal boundaryMatchScore = computeBoundaryMatchScore(ta1.getSpans(), ta2.getSpans(),
				boundaryMatchStrategy);
		BigDecimal wangScore = computeWangSemanticSimilarity(ta1.getClassMention().getMentionName(),
				ta2.getClassMention().getMentionName());

		return boundaryMatchScore.multiply(wangScore);
	}

	/**
	 * @param referenceConceptId
	 * @param testConceptId
	 * @return a value representing the Wang Semantic Similarity score (Wang et al. 2007).
	 */
	BigDecimal computeWangSemanticSimilarity(String referenceConceptId, String testConceptId) {
		Map<String, BigDecimal> referenceSvalueMap = populateSvalueMap(referenceConceptId);
		Map<String, BigDecimal> testSvalueMap = populateSvalueMap(testConceptId);

		/* get the set of overlapping concept ids between the reference and test sets */
		Set<String> overlappingConceptIds = new HashSet<String>(referenceSvalueMap.keySet());
		overlappingConceptIds.retainAll(testSvalueMap.keySet());

		/* compute SVref and SVtest by summing the scores for all ancestors */
		BigDecimal svRef = sum(referenceSvalueMap.values());
		BigDecimal svTest = sum(testSvalueMap.values());

		/* compute the aggregate score of the overlapping concepts */
		BigDecimal svOverlap = BigDecimal.valueOf(0.0);
		for (String conceptId : overlappingConceptIds) {
			svOverlap = svOverlap.add(referenceSvalueMap.get(conceptId));
			svOverlap = svOverlap.add(testSvalueMap.get(conceptId));
		}

		return svOverlap.divide(svRef.add(svTest), 10, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param values
	 * @return the sum of the input collection of BigDecimals
	 */
	private BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal sum = BigDecimal.valueOf(0.0);
		for (BigDecimal val : values) {
			sum = sum.add(val);
		}
		return sum;
	}

	/**
	 * For the specified concept identifier, find all ancestors (via subClassOf relations only), and
	 * compute their corresponding S-values using the Wang 2007 semantic-similarity metric where
	 * each ancestor is weighted with a factor equal to w^d, where w is the weight factor (default =
	 * 0.65 in Bossy et al. 2013) and d is an integer specifying the distance a concept is from its
	 * descendant (the concept asserted by the annotation).
	 * 
	 * @param conceptId
	 * @return a mapping from concept IDs for the ancestors of the specified concept (the
	 *         descendant) to their S-value relative to the descendant
	 */
	Map<String, BigDecimal> populateSvalueMap(String conceptId) {

		Map<String, BigDecimal> conceptIdToSValueMap = new HashMap<String, BigDecimal>();
		Map<String, Integer> conceptIdToDistanceMap = new HashMap<String, Integer>();

		Queue<OWLClass> queue = new LinkedList<OWLClass>();
		OWLClass concept = graph.getOWLClassByIdentifier(conceptId);

		if (concept == null) {
			System.err.println("SIZE: " + graph.getAllOWLClasses().size());
			System.err.println("Concept identifier (" + conceptId
					+ ") was not found in the ontology. Make sure the concept identifier uses the correct format, e.g. "
					+ graph.getAllOWLClasses().iterator().next().toStringID());
			System.err.println("Please adjust your concept identifiers accordingly and re-try. Exiting.");
			System.exit(-1);
		}

		queue.add(concept);

		conceptIdToSValueMap.put(concept.toStringID(), BigDecimal.valueOf(1.0));
		conceptIdToDistanceMap.put(concept.toStringID(), 0);

		/*
		 * below is a BFS from the concept asserted by the annotation. This allows the depth of
		 * ancestor concepts to be cataloged.
		 */
		while (!queue.isEmpty()) {
			concept = queue.poll();
			int distance = conceptIdToDistanceMap.get(concept.toStringID()) + 1;

			for (OWLGraphEdge edge : graph.getOutgoingEdges(concept)) {
				if (edge.getSingleQuantifiedProperty().getProperty() == null
						&& edge.getSingleQuantifiedProperty().isSubClassOf() && edge.getTarget() instanceof OWLClass) {

					OWLClass superClass = (OWLClass) edge.getTarget();
					String superClassId = superClass.toStringID();

					if (!conceptIdToSValueMap.containsKey(superClassId)) {
						queue.add(superClass);
						conceptIdToDistanceMap.put(superClassId, distance);
					}

					BigDecimal sValue = distanceWeightFactor.pow(distance);

					if (conceptIdToSValueMap.containsKey(superClassId)) {
						// store the max S-value if there is a value already present
						BigDecimal previousSValue = conceptIdToSValueMap.get(superClassId);
						conceptIdToSValueMap.put(superClassId, sValue.max(previousSValue));
					} else {
						conceptIdToSValueMap.put(superClassId, sValue);
					}

				}
			}
		}

		return conceptIdToSValueMap;
	}

	/**
	 * @param referenceSpans
	 * @param testSpans
	 * @param boundaryMatchStrategy
	 * @return number between 0 and 1 indicating the boundary match where 1 indicates a perfect
	 *         match. If the boundary match strategy equals EXACT, then either 1 or 0 will be
	 *         returned. If the JACCARD strategy is used, then the value returned is based on an
	 *         adapted Jaccard index from Bossy et al. 2012 (see reference in Bossy et al. 2013).
	 */
	static BigDecimal computeBoundaryMatchScore(List<Span> referenceSpans, List<Span> testSpans,
			BoundaryMatchStrategy boundaryMatchStrategy) {
		if (boundaryMatchStrategy == BoundaryMatchStrategy.EXACT) {
			if (referenceSpans.equals(testSpans)) {
				return BigDecimal.valueOf(1.0);
			}
			return BigDecimal.valueOf(0.0);
		} else if (boundaryMatchStrategy == BoundaryMatchStrategy.JACCARD) {
			return computeBoundaryJaccardScore(referenceSpans, testSpans);
		} else {
			throw new IllegalArgumentException("Requested boundary match strategy (" + boundaryMatchStrategy.name()
					+ ") not currently handled. Code revision required.");
		}
	}

	/**
	 * Jaccard Index adapted for segments as defined in Bossy 2012
	 * 
	 * @param spans
	 * @param spans2
	 * @return
	 */
	static BigDecimal computeBoundaryJaccardScore(List<Span> referenceSpans, List<Span> testSpans) {
		Set<Integer> referenceOffsets = new HashSet<Integer>();
		Set<Integer> testOffsets = new HashSet<Integer>();

		for (Span referenceSpan : referenceSpans) {
			for (int i = referenceSpan.getSpanStart(); i < referenceSpan.getSpanEnd(); i++) {
				referenceOffsets.add(i);
			}
		}

		for (Span testSpan : testSpans) {
			for (int i = testSpan.getSpanStart(); i < testSpan.getSpanEnd(); i++) {
				testOffsets.add(i);
			}
		}

		BigDecimal referenceLength = BigDecimal.valueOf(referenceOffsets.size());
		BigDecimal testLength = BigDecimal.valueOf(testOffsets.size());

		referenceOffsets.retainAll(testOffsets);
		BigDecimal overlap = BigDecimal.valueOf(referenceOffsets.size());

		return overlap.divide(referenceLength.add(testLength).subtract(overlap), 10, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Utility class for storing a scored annotation match
	 */
	@Data
	static class ScoredAnnotationMatch implements Comparable<ScoredAnnotationMatch> {
		private final BigDecimal score;
		private final String refAnnotId;
		private final String testAnnotId;

		public int compareTo(ScoredAnnotationMatch o) {
			return -1 * getScore().compareTo(o.getScore());
		}
	}

	/**
	 * Utility class for storing the components required to compute slot error rate
	 */
	@Data
	@AllArgsConstructor
	static class SlotErrorRate {
		private BigDecimal matches;
		private int insertions;
		private int deletions;
		private int referenceCount;
		private int predictedCount;

		public BigDecimal getSubstitutions() {
			return BigDecimal.valueOf(referenceCount).subtract(BigDecimal.valueOf(deletions)).subtract(matches);
		}

		public final BigDecimal getSER() {
			if (predictedCount == 0 && referenceCount == 0) {
				return BigDecimal.valueOf(0.0);
			}
			return (referenceCount == 0) ? BigDecimal.valueOf(1.0)
					: getSubstitutions().add(BigDecimal.valueOf(insertions).add(BigDecimal.valueOf(deletions)))
							.divide(BigDecimal.valueOf(referenceCount), 10, BigDecimal.ROUND_HALF_UP);
		}

		public void update(SlotErrorRate ser) {
			this.matches = this.matches.add(ser.getMatches());
			this.insertions += ser.getInsertions();
			this.deletions += ser.getDeletions();
			this.referenceCount += ser.getReferenceCount();
			this.predictedCount += ser.getPredictedCount();
		}

		public final BigDecimal getPrecision() {
			if (predictedCount == 0 && referenceCount == 0) {
				return BigDecimal.valueOf(1.0);
			}
			return (predictedCount == 0) ? BigDecimal.valueOf(0)
					: matches.divide(BigDecimal.valueOf(predictedCount), 10, BigDecimal.ROUND_HALF_UP);
		}

		public final BigDecimal getRecall() {
			if (predictedCount == 0 && referenceCount == 0) {
				return BigDecimal.valueOf(1.0);
			}
			return (referenceCount == 0) ? BigDecimal.valueOf(0)
					: matches.divide(BigDecimal.valueOf(referenceCount), 10, BigDecimal.ROUND_HALF_UP);
		}

		public final BigDecimal getFScore() {
			BigDecimal p = getPrecision();
			BigDecimal r = getRecall();
			return (p.compareTo(BigDecimal.valueOf(0)) == 0) ? BigDecimal.valueOf(0)
					: p.multiply(r).multiply(BigDecimal.valueOf(2)).divide(p.add(r), 10, BigDecimal.ROUND_HALF_UP);
		}

		@Override
		public String toString() {
			return "SlotErrorRate [M=" + matches + ", I=" + insertions + ", D=" + deletions + ", N=" + referenceCount
					+ ", P=" + predictedCount + ", getSER()=" + getSER() + ", getPrecision()=" + getPrecision()
					+ ", getRecall()=" + getRecall() + ", getFScore()=" + getFScore() + "]";
		}

	}
}
