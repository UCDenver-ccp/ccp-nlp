package edu.ucdenver.ccp.nlp.core.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.Comparator;
import java.util.List;

import edu.ucdenver.ccp.nlp.core.annotation.comparison.SloppySpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.SpanComparator;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;

/**
 * The <code>Span</code> class is used to store the character offsets for a span of text. If an
 * invalid span is detected, e.g. the end offset is less than the start offset, an
 * <code>InvalidSpanException</code> is thrown. The <code>Span</code> class is used by the
 * <code>TextAnnotation</code> class.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Span implements Comparable {

	private int spanStart;

	private int spanEnd;

	public Span(int spanStart, int spanEnd) {
		this.spanStart = spanStart;
		this.spanEnd = spanEnd;
		validateSpan();
	}

	public int getSpanEnd() {
		return spanEnd;
	}

	public void setSpanEnd(int spanEnd) {
		this.spanEnd = spanEnd;
		validateSpan();
	}

	public int getSpanStart() {
		return spanStart;
	}

	public void setSpanStart(int spanStart) {
		this.spanStart = spanStart;
		validateSpan();
	}

	private void validateSpan() {
		if (!isValid()) {
			throw new InvalidSpanException(
					"Invalid span: Negative offsets or start of span is greater than end of span. spanStart="
							+ spanStart + "  spanEnd=" + spanEnd);
		}
	}

	public boolean isValid() {
		if (spanStart <= spanEnd && spanStart > -1 && spanEnd > -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the length of the span
	 * 
	 * @return
	 */
	public int length() {
		return spanEnd - spanStart;
	}

	/**
	 * The default equals() method uses the StrictSpanComparator
	 */
	@Override
	public boolean equals(Object spanToEquate) {
		if (!(spanToEquate instanceof Span)) {
			throw new ClassCastException("A Span object expected.");
		} else {
			SpanComparator sc = new StrictSpanComparator();
			Span span = (Span) spanToEquate;

			if (sc.compare(this, span) == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * The overlaps() method uses the SloppySpanComparator
	 * 
	 * @param overlappingSpan
	 * @return
	 */
	public boolean overlaps(Span overlappingSpan) {
		SpanComparator sc = new SloppySpanComparator();
		if (sc.compare(this, overlappingSpan) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean overlaps(int index) {
		Span overlappingSpan = null;
		try {
			overlappingSpan = new Span(index, index + 1);
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}
		SpanComparator sc = new SloppySpanComparator();
		if (sc.compare(this, overlappingSpan) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true if this span starts before the inputted span, false otherwise. (Equal span
	 * starts result in false).
	 * 
	 * @param span
	 * @return
	 */
	public boolean startsBefore(Span span) {
		if (this.spanStart < span.getSpanStart()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true if this span ends before the inputted span, false otherwise. (Equal span ends
	 * result in false).
	 * 
	 * @param span
	 * @return
	 */
	public boolean endsBefore(Span span) {
		if (this.spanEnd < span.getSpanEnd()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * REturns true if this span encompasses the inputted span, i.e. if it overlaps the input span
	 * entirely.
	 * 
	 * @param span
	 * @return
	 */
	public boolean containsSpan(Span span) {
		if (this.spanStart <= span.getSpanStart() & this.spanEnd >= span.getSpanEnd()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return "[" + this.spanStart + ".." + this.spanEnd + "]";
	}

	/**
	 * Returns a String for a list of spans taking the form: [s1b..s1e--s2b..s2e--s3b..s3e], where
	 * s1b=span1 begin and s1e=span1 end
	 * 
	 * @param spanList
	 * @return
	 */
	public static String toString(List<Span> spanList) {
		StringBuffer sb = new StringBuffer();
		for (Span span : spanList) {
			sb.append(String.format("[%d..%d]_", span.getSpanStart(), span.getSpanEnd()));
		}
		if (spanList.size() > 0) {
			sb.replace(sb.length() - 1, sb.length(), "");
		} else {
			sb.append("[]");
		}
		return sb.toString();
	}

	@Override
	public Span clone() {
		Span newSpan = null;
		try {
			newSpan = new Span(this.spanStart, this.spanEnd);
		} catch (InvalidSpanException e) {
			e.printStackTrace();
		}
		return newSpan;
	}

	public int compareTo(Object spanToEquate) {
		if (spanToEquate instanceof Span) {
			SpanComparator sc = new StrictSpanComparator();
			Span span = (Span) spanToEquate;

			return sc.compare(this, span);
		} else {
			System.err.println("Error, cannot compare a Span to a " + spanToEquate.getClass().getName());
			return -1;
		}
	}

	/**
	 * Returns a Comparator that compares TextAnnotations based on their respective spans
	 * 
	 * @return
	 */
	public static Comparator<Span> ASCENDING() {
		return new Comparator<Span>() {
			public int compare(Span span1, Span span2) {
				return new StrictSpanComparator().compare(span1, span2);
			}
		};
	}

}
