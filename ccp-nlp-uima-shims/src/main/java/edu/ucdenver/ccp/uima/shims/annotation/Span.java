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
package edu.ucdenver.ccp.uima.shims.annotation;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2016 Regents of the University of Colorado
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

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class Span implements Comparable<Span> {

	private final int spanStart;

	private final int spanEnd;

	public Span(int spanStart, int spanEnd) {
		this.spanStart = spanStart;
		this.spanEnd = spanEnd;
		validateSpan();
	}

	private final void validateSpan() {
		if (!isValid()) {
			throw new IllegalArgumentException(
					"Invalid span: Negative offsets or start of span is greater than end of span. spanStart="
							+ getSpanStart() + "  spanEnd=" + getSpanEnd());
		}
	}

	public boolean isValid() {
		if (getSpanStart() <= getSpanEnd() && getSpanStart() > -1 && getSpanEnd() > -1) {
			return true;
		}
		return false;
	}

	/**
	 * @return the span start offset
	 */
	public int getSpanStart() {
		return spanStart;
	}

	/**
	 * @return the span end offset
	 */
	public int getSpanEnd() {
		return spanEnd;
	}

	/**
	 * Return the length of the span
	 * 
	 * @return
	 */
	public int length() {
		return spanEnd - spanStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + spanEnd;
		result = prime * result + spanStart;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Span other = (Span) obj;
		if (spanEnd != other.spanEnd)
			return false;
		if (spanStart != other.spanStart)
			return false;
		return true;
	}

	/**
	 * 
	 * @param overlappingSpan
	 * @return
	 */
	public boolean overlaps(Span overlappingSpan) {
		int s0start = this.getSpanStart();
		int s0end = this.getSpanEnd();
		int s1start = overlappingSpan.getSpanStart();
		int s1end = overlappingSpan.getSpanEnd();
		if ((s0start >= s1start && s0start < s1end) || (s0end > s1start && s0end <= s1end)
				|| (s0start <= s1start && s0end > s1start) || (s0end >= s1end && s0start < s1end)) {
			return true;
		}
		return false;
	}

	public boolean overlaps(int index) {
		Span overlappingSpan = new Span(index, index + 1);
		return overlaps(overlappingSpan);
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
		return new Span(this.spanStart, this.spanEnd);
	}

	/**
	 * Returns a Comparator that compares TextAnnotations based on their respective spans
	 * 
	 * @return
	 */
	public static Comparator<Span> ASCENDING() {
		return new Comparator<Span>() {
			public int compare(Span span1, Span span2) {
				return span1.compareTo(span2);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Span span) {
		if ((this.getSpanStart() == span.getSpanStart()) && (this.getSpanEnd() == span.getSpanEnd())) {
			/* if the spans are equal, then return 0 */
			return 0;
		} else if (this.getSpanStart() == span.getSpanStart()) {
			/*
			 * if the spans have equal span start indexes, then return -1 if span1 ends before
			 * span2, 1 otherwise
			 */
			if (this.endsBefore(span)) {
				return -1;
			} else {
				return 1;
			}
		} else {
			/*
			 * the spans are not equal, and they don't have the same span start indexes, so return
			 * -1 if span1 starts before span2, 1 otherwise
			 */
			if (this.startsBefore(span)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

}
