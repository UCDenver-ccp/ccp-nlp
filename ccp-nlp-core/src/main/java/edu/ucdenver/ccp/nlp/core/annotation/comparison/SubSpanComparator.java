package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

public class SubSpanComparator extends SpanComparator {
	
	public SubSpanComparator() {
		super(true);
	}

	@Override
	public int compare(Span span1, Span span2) {
		// If they are the same length, they must be equal.
		if (span1.compareTo(span2) == 0) {
			return 0;
		}
		
		Span shortSpan=null;
		Span longSpan=null;
		if (span1.length() > span2.length()) {
			longSpan = span1;
			shortSpan = span2;
		}
		else {
			longSpan = span2;
			shortSpan = span1;
		}
		
		// If they are different lengths then one of the following
		// cases holds:
		
		// long: -----------
		// short:    ----
		if (   longSpan.startsBefore(shortSpan) 
			&& shortSpan.endsBefore(longSpan)) {
			return 0;
		}
		
		// long: ----------
		// short:     -----
		if (   longSpan.startsBefore(shortSpan) 
				&& shortSpan.getSpanEnd() == longSpan.getSpanEnd()) {
				return 0;
			}
		// long:  --------
		// short: ---
		if (   longSpan.getSpanStart() == shortSpan.getSpanStart() 
				&& shortSpan.endsBefore(longSpan)) {
				return 0;
			}

		return this.matches(longSpan, shortSpan);
	}

	private List<Integer> getMinMax(List<Span> list) {
		int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
		
		for (Span s : list )  {
			if (s.getSpanStart() < min) {
				min = s.getSpanStart();
			}
			if (s.getSpanEnd() > max ) {
				max = s.getSpanEnd();
			}
		}
		List<Integer> retval = new ArrayList<Integer>();
		retval.add(min);
		retval.add(max);
		return retval;
	}
	
	@Override
	public int compare(List<Span> spanList1, List<Span> spanList2) {
		List<Integer> minMax1 = getMinMax(spanList1), 
				      minMax2 = getMinMax(spanList2);
		
		// 1 is outside 2
		if (   minMax1.get(0) <= minMax2.get(0) 
			&& minMax1.get(1) >= minMax2.get(1) ) {
			return 0;
		}
		// 2 is outside 1
		if (   minMax2.get(0) <= minMax1.get(0) 
			&& minMax2.get(1) >= minMax1.get(1) ) {
			return 0;
		}
		// 1 is left of 2
		if (   minMax1.get(0) <= minMax2.get(0) 
				&& minMax1.get(1) <= minMax2.get(1) ) {
				return -1;
			}
		
		// 2 is left of 1
		if (   minMax2.get(0) <= minMax1.get(0) 
				&& minMax2.get(1) <= minMax1.get(1) ) {
				return 1;
			}
		return 1;
	}

}
