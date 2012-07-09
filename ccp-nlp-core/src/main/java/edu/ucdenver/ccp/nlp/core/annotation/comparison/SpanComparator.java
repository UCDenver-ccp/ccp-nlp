/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.nlp.core.annotation.InvalidSpanException;
import edu.ucdenver.ccp.nlp.core.annotation.Span;

/**
 * This class implements a variety of methods for comparing spans, as described in Olsson et al. 2002.
 * 
 * @author Bill Baumgartner
 * 
 */

public abstract class SpanComparator implements Comparator<Span> {
private static Logger logger = Logger.getLogger(SpanComparator.class);
	/**
	 * This flag is used as an indication to allow a more optimized comparison of all annotations in a document. When
	 * set to true, this span comparator requires spans to, at the very least, overlap in order to match. This condition
	 * allows the number of comparisons in a large document to be limited to only annotations in a neighborhood. Before
	 * this flag, each annotation was compared to every other annotation in the document. If set to false, this will
	 * imply that each annotation should be compared to every other annotation in the document. This is true for the
	 */
	protected boolean spansMustOverlapToMatch = true;

	public SpanComparator(boolean spansMustOverlapToMatch) {
		this.spansMustOverlapToMatch = spansMustOverlapToMatch;
	}

	/**
	 * This method will be overridden by subclasses of SpanComparator
	 */
	public abstract int compare(Span span1, Span span2);

	/**
	 * 
	 * This method will be overridden by subclasses of SpanComparator
	 * 
	 * @param spanList1
	 * @param spanList2
	 * @return
	 */
	public abstract int compare(List<Span> spanList1, List<Span> spanList2);

	/**
	 * Returns 0 if the spans are equal, -1 if span1 starts before span2, 1 if span1 starts after span2. <br>
	 * If the spans start at the same index, then -1 if span1 ends before span2 and 1 if span1 ends after span2
	 */
	public int matches(Span span1, Span span2) {
		if ((span1.getSpanStart() == span2.getSpanStart()) && (span1.getSpanEnd() == span2.getSpanEnd())) {
			/* if the spans are equal, then return 0 */
			return 0;
		} else if (span1.getSpanStart() == span2.getSpanStart()) {
			/* if the spans have equal span start indexes, then return -1 if span1 ends before span2, 1 otherwise */
			if (span1.endsBefore(span2)) {
				return -1;
			} else {
				return 1;
			}
		} else {
			/*
			 * the spans are not equal, and they don't have the same span start indexes, so return -1 if span1 starts
			 * before span2, 1 otherwise
			 */
			if (span1.startsBefore(span2)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	/**
	 * Compares a list of spans. If the lists have identical contents, return 0. If spanList1 starts before spanList2,
	 * return -1, otherwise return 1.<br>
	 * Note: an empty list will also return 0. Note: the aggregate span is being checked here.. not the individual spans
	 * in the list.
	 */
	public int matches(List<Span> spanList1, List<Span> spanList2) {
		SpanComparator sc = new StrictSpanComparator();
		/* sort the lists just to be certain */
		Collections.sort(spanList1, sc);
		Collections.sort(spanList2, sc);

		// if (spanList1.equals(spanList2)) {
		// /* if the lists are identical, return 0 */
		// return 0;
		// } else
		if (spanList1.size() == 0 && spanList2.size() > 0) {
			/*
			 * if list1 is empty, and list2 is non-empty, then return 1, as the empty list "comes after" the non-empty
			 * list
			 */
			return 1;
		} else if (spanList1.size() > 0 && spanList2.size() == 0) {
			/*
			 * if list1 is non-empty and list2 is empty, then return -1, as the non-empty list "comes before" the empty
			 * list
			 */
			return -1;
		} else if (spanList1.size() == 0 && spanList2.size() == 0) {
			/* if both lists are empty, consider them equal. */
			return 0;
		} else {
			/*
			 * there are members in both span lists, so pick the first and last member, since it is sorted, and use them
			 * to determine what to return.
			 */
			Collections.sort(spanList1, sc);
			Collections.sort(spanList2, sc);
			Span firstSpanFromList1 = spanList1.get(0);
			Span firstSpanFromList2 = spanList2.get(0);
			Span lastSpanFromList1 = spanList1.get(spanList1.size() - 1);
			Span lastSpanFromList2 = spanList2.get(spanList2.size() - 1);

			try {
				Span compositeSpan1 = new Span(firstSpanFromList1.getSpanStart(), lastSpanFromList1.getSpanEnd());
				Span compositeSpan2 = new Span(firstSpanFromList2.getSpanStart(), lastSpanFromList2.getSpanEnd());
				//
				// return sc.compare(compositeSpan1, compositeSpan2);

				if (spanList1.size() == spanList2.size()) {
					for (int i = 0; i < spanList1.size(); i++) {
						int ans = sc.compare(spanList1.get(i), spanList2.get(i));
						if (ans != 0) {
							return ans;
						}
					}
					return 0;
				} else {
					/* the number of spans is not the same, so they can't possibly be equal */
					int ans = sc.compare(compositeSpan1, compositeSpan2);
					if (ans != 0) {
						return ans;
					} else {
						return 1;
					}
				}

			} catch (InvalidSpanException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Returns 0 if span1 and span2 overlap. Otherwise, it returns -1 if span1 starts before span2, and 1 if span1
	 * starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int overlaps(Span span1, Span span2) {
		int s0start = span1.getSpanStart();
		int s0end = span1.getSpanEnd();
		int s1start = span2.getSpanStart();
		int s1end = span2.getSpanEnd();
		if ((s0start >= s1start && s0start < s1end) | (s0end > s1start && s0end <= s1end) | (s0start <= s1start && s0end > s1start)
				| (s0end >= s1end && s0start < s1end)) {
			return 0;
		} else {
			return matches(span1, span2);
		}
	}

	/**
	 * Returns 0 if the span lists overlap. This method requires piecemeal overlap. That is, a list containing [1,5]
	 * [15,20] overlaps with a list containing [3,6] but does not overlap with the list: [6,11] [22,25]. Otherwise, it
	 * returns -1 if span1 starts before span2, and 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int overlaps(List<Span> spanList1, List<Span> spanList2) {
		SpanComparator sc = new SloppySpanComparator();
		if (spanList1.size() == 0 && spanList2.size() > 0) {
			/*
			 * if list1 is empty, and list2 is non-empty, then return 1, as the empty list "comes after" the non-empty
			 * list
			 */
			return 1;
		} else if (spanList1.size() > 0 && spanList2.size() == 0) {
			/*
			 * if list1 is non-empty and list2 is empty, then return -1, as the non-empty list "comes before" the empty
			 * list
			 */
			return -1;
		} else if (spanList1.size() == 0 && spanList2.size() == 0) {
			/* if both lists are empty, consider them overlapping - though this is weird. */
			return 0;
		} else {
			boolean spanOverlapDetected = false;
			for (Span span1 : spanList1) {
				for (Span span2 : spanList2) {
					if (sc.compare(span1, span2) == 0) {
						spanOverlapDetected = true;
						break;
					}
				}
				if (spanOverlapDetected) {
					break;
				}
			}
			if (spanOverlapDetected) {
				return 0;
			} else {
				return matches(spanList1, spanList2);
			}
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same start index. Otherwise, it returns -1 if span1 starts before span2,
	 * and 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanStart(Span span1, Span span2) {
		if (span1.getSpanStart() == span2.getSpanStart()) {
			return 0;
		} else {
			return matches(span1, span2);
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same start index. Otherwise, it returns -1 if span1 starts before span2,
	 * and 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanStart(List<Span> spanList1, List<Span> spanList2) {
		SpanComparator sc = new SharedStartSpanComparator();
		if (spanList1.size() == 0 && spanList2.size() > 0) {
			/*
			 * if list1 is empty, and list2 is non-empty, then return 1, as the empty list "comes after" the non-empty
			 * list
			 */
			return 1;
		} else if (spanList1.size() > 0 && spanList2.size() == 0) {
			/*
			 * if list1 is non-empty and list2 is empty, then return -1, as the non-empty list "comes before" the empty
			 * list
			 */
			return -1;
		} else if (spanList1.size() == 0 && spanList2.size() == 0) {
			/* if both lists are empty, consider them overlapping - though this is weird. */
			return 0;
		} else {
			/*
			 * there are members in both span lists, so pick the first and last member, since it is sorted, and use them
			 * to determine what to return.
			 */
			Collections.sort(spanList1, sc);
			Collections.sort(spanList2, sc);
			Span firstSpanFromList1 = spanList1.get(0);
			Span firstSpanFromList2 = spanList2.get(0);
			Span lastSpanFromList1 = spanList1.get(spanList1.size() - 1);
			Span lastSpanFromList2 = spanList2.get(spanList2.size() - 1);

			try {
				Span compositeSpan1 = new Span(firstSpanFromList1.getSpanStart(), lastSpanFromList1.getSpanEnd());
				Span compositeSpan2 = new Span(firstSpanFromList2.getSpanStart(), lastSpanFromList2.getSpanEnd());

				return sc.compare(compositeSpan1, compositeSpan2);
			} catch (InvalidSpanException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same end index. Otherwise, it returns -1 if span1 starts before span2, and
	 * 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanEnd(Span span1, Span span2) {
		if (span1.getSpanEnd() == span2.getSpanEnd()) {
			return 0;
		} else {
			return matches(span1, span2);
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same end index. Otherwise, it returns -1 if span1 starts before span2, and
	 * 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanEnd(List<Span> spanList1, List<Span> spanList2) {
		SpanComparator sc = new SharedEndSpanComparator();
		if (spanList1.size() == 0 && spanList2.size() > 0) {
			/*
			 * if list1 is empty, and list2 is non-empty, then return 1, as the empty list "comes after" the non-empty
			 * list
			 */
			return 1;
		} else if (spanList1.size() > 0 && spanList2.size() == 0) {
			/*
			 * if list1 is non-empty and list2 is empty, then return -1, as the non-empty list "comes before" the empty
			 * list
			 */
			return -1;
		} else if (spanList1.size() == 0 && spanList2.size() == 0) {
			/* if both lists are empty, consider them overlapping - though this is weird. */
			return 0;
		} else {
			/*
			 * there are members in both span lists, so pick the first and last member, since it is sorted, and use them
			 * to determine what to return.
			 */
			Collections.sort(spanList1, sc);
			Collections.sort(spanList2, sc);
			Span firstSpanFromList1 = spanList1.get(0);
			Span firstSpanFromList2 = spanList2.get(0);
			Span lastSpanFromList1 = spanList1.get(spanList1.size() - 1);
			Span lastSpanFromList2 = spanList2.get(spanList2.size() - 1);

			try {
				Span compositeSpan1 = new Span(firstSpanFromList1.getSpanStart(), lastSpanFromList1.getSpanEnd());
				Span compositeSpan2 = new Span(firstSpanFromList2.getSpanStart(), lastSpanFromList2.getSpanEnd());

				return sc.compare(compositeSpan1, compositeSpan2);
			} catch (InvalidSpanException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same start or end index (or both). Otherwise, return -1 if span1 starts
	 * before span2, and 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanStartOrEnd(Span span1, Span span2) {
		if ((sharesSpanStart(span1, span2) == 0) | (sharesSpanEnd(span1, span2) == 0)) {
			return 0;
		} else {
			return matches(span1, span2);
		}
	}

	/**
	 * Returns 0 if span1 and span2 share the same start or end index (or both). Otherwise, return -1 if span1 starts
	 * before span2, and 1 if span1 starts after span2
	 * 
	 * @param span1
	 * @param span2
	 * @return
	 */
	public int sharesSpanStartOrEnd(List<Span> spanList1, List<Span> spanList2) {
		SpanComparator sc = new SharedStartOrEndSpanComparator();
		if (spanList1.size() == 0 && spanList2.size() > 0) {
			/*
			 * if list1 is empty, and list2 is non-empty, then return 1, as the empty list "comes after" the non-empty
			 * list
			 */
			return 1;
		} else if (spanList1.size() > 0 && spanList2.size() == 0) {
			/*
			 * if list1 is non-empty and list2 is empty, then return -1, as the non-empty list "comes before" the empty
			 * list
			 */
			return -1;
		} else if (spanList1.size() == 0 && spanList2.size() == 0) {
			/* if both lists are empty, consider them overlapping - though this is weird. */
			return 0;
		} else {
			/*
			 * there are members in both span lists, so pick the first and last member, since it is sorted, and use them
			 * to determine what to return.
			 */
			Collections.sort(spanList1, sc);
			Collections.sort(spanList2, sc);
			Span firstSpanFromList1 = spanList1.get(0);
			Span firstSpanFromList2 = spanList2.get(0);
			Span lastSpanFromList1 = spanList1.get(spanList1.size() - 1);
			Span lastSpanFromList2 = spanList2.get(spanList2.size() - 1);

			try {
				Span compositeSpan1 = new Span(firstSpanFromList1.getSpanStart(), lastSpanFromList1.getSpanEnd());
				Span compositeSpan2 = new Span(firstSpanFromList2.getSpanStart(), lastSpanFromList2.getSpanEnd());

				return sc.compare(compositeSpan1, compositeSpan2);
			} catch (InvalidSpanException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * This flag is used as an indication to allow a more optimized comparison of all annotations in a document. When
	 * set to true, this span comparator requires spans to, at the very least, overlap in order to match. This condition
	 * allows the number of comparisons in a large document to be limited to only annotations in a neighborhood. Before
	 * this flag, each annotation was compared to every other annotation in the document. If set to false, this will
	 * imply that each annotation should be compared to every other annotation in the document. This is true for the
	 */
	public boolean spansMustOverlapToMatch() {
		return spansMustOverlapToMatch;
	}

}
