package edu.ucdenver.ccp.nlp.evaluation.bossy2013;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Utility class for storing the components required to compute slot error rate
 */
@Data
@AllArgsConstructor
public class SlotErrorRate {
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