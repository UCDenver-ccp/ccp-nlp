package edu.ucdenver.ccp.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.nlp.core.annotation.Span;
import edu.ucdenver.ccp.nlp.core.annotation.comparison.StrictSpanComparator;

public class SpanUtilExtra {
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

    /**
     * Compares input spans and returns any intervening spans if the input spans are not contiguous.
     * For example, if the input spans are [0..5] [8..19] this method would return a single
     * intervening span of [5..8]
     * 
     * @param inputSpans
     * @return
     */
    public static Collection<Span> getInterveningSpans(Collection<Span> inputSpans) {
            if (inputSpans.size() < 2)
                    return Collections.emptyList();
            List<Span> spanList = new ArrayList<Span>(inputSpans);
            Collections.sort(spanList, ASCENDING());
            Collection<Span> interveningSpans = new ArrayList<Span>();
            for (int i = 1; i < spanList.size(); i++) {
                    if (spanList.get(i - 1).getSpanEnd() < spanList.get(i).getSpanStart())
                            interveningSpans.add(new Span(spanList.get(i - 1).getSpanEnd(), spanList.get(i).getSpanStart()));
            }
            return interveningSpans;
    }

    
    /**
     * Compares the input spans and returns any spans that are shared by two or more of the spans,
     * e.g. if the input spans are [0..10] and [6..15] then this method will return a span of
     * [6..10]
     * 
     * @param inputSpans
     * @return
     */
    public static Collection<Span> getCommonSpans(Collection<Span> inputSpans) {
            if (inputSpans.size() < 2)
                    return Collections.emptyList();
            Collection<Span> commonSpans = new ArrayList<Span>();
            List<Span> spanList = new ArrayList<Span>(inputSpans);
            for (int i = 0; i < inputSpans.size() - 1; i++)
                    for (int j = i + 1; j < inputSpans.size(); j++)
                            if (spanList.get(i).overlaps(spanList.get(j)))
                                    commonSpans.add(getCommonSpan(spanList.get(i), spanList.get(j)));
            return commonSpans;
    }

    
    /**
     * For the input pair of spans, this method returns the common span if the two spans overlap.
     * Null is returned if there is no overlap.
     * 
     * @param span1
     * @param span2
     * @return
     */
    public static Span getCommonSpan(Span span1, Span span2) {
            if (!span1.overlaps(span2))
                    return null;
            int spanStart = Math.max(span1.getSpanStart(), span2.getSpanStart());
            int spanEnd = Math.min(span1.getSpanEnd(), span2.getSpanEnd());
            return new Span(spanStart, spanEnd);
    }

    /**
     * For the input collection of spans, this method looks for spans or parts of spans that are not
     * shared by other spans.
     * 
     * @param inputSpans
     * @return
     */
    public static Collection<Span> getUniqueSpans(Collection<Span> inputSpans) {
            if (inputSpans.size() < 2)
                    return new ArrayList<Span>(inputSpans);
            Collection<Span> uniqueSpans = new ArrayList<Span>();
            List<Span> spanList = new ArrayList<Span>(inputSpans);
            for (int i = 0; i < spanList.size() - 1; i++) {
                    boolean span_i_overlaps = false;
                    for (int j = i + 1; j < spanList.size(); j++) {
                            if (spanList.get(i).overlaps(spanList.get(j))) {
                                    uniqueSpans.addAll(getUniqueSpans(spanList.get(i), spanList.get(j)));
                                    span_i_overlaps = true;
                            }
                    }
                    if (!span_i_overlaps)
                            uniqueSpans.add(spanList.get(i));
            }

            return uniqueSpans;
    }

    /**
     * Compares the two input spans and returns spans that are unique to one or the other input
     * spans, i.e. the part of the spans that do not overlap.
     * 
     * @param span1
     * @param span2
     * @return
     * @throws IllegalArgumentException
     *             if the input spans do not overlap
     */
    public static Collection<Span> getUniqueSpans(Span span1, Span span2) {
            if (!span1.overlaps(span2))
                    throw new IllegalArgumentException("Input spans are expected to overlap");
            List<Span> spanList = new ArrayList<Span>();
            spanList.add(span1);
            spanList.add(span2);
            Collections.sort(spanList, ASCENDING());

            int spanStart1 = spanList.get(0).getSpanStart();
            int spanStart2 = spanList.get(1).getSpanStart();
            int spanEnd1 = spanList.get(0).getSpanEnd();
            int spanEnd2 = spanList.get(1).getSpanEnd();

            Collection<Span> uniqueSpans = new ArrayList<Span>();
            if (spanStart1 != spanStart2)
                    uniqueSpans.add(new Span(spanStart1, spanStart2));
            if (spanEnd1 != spanEnd2)
                    uniqueSpans.add(new Span(spanEnd1, spanEnd2));

            return uniqueSpans;
    }


    /**
     * returns the covered text for a given span and given text
     * 
     * @param span
     * @param text
     * @return
     */
    public static String getCoveredText(Span span, String text) {
            return text.substring(span.getSpanStart(), span.getSpanEnd());
    }

    /**
     * Adjusts the input span such that the covered text does not start nor end with whitespace
     * 
     * @param documentText
     * @param span
     * @return
     */
    public static Span trim(String documentText, Span span) {
            Span trimmedSpan = new Span(span.getSpanStart(), span.getSpanEnd());
            String coveredText = getCoveredText(trimmedSpan, documentText);
            while (coveredText.startsWith(StringConstants.SPACE)) {
                    trimmedSpan = new Span(trimmedSpan.getSpanStart() + 1, trimmedSpan.getSpanEnd());
                    coveredText = getCoveredText(trimmedSpan, documentText);
            }
            while (coveredText.endsWith(StringConstants.SPACE)) {
                    trimmedSpan = new Span(trimmedSpan.getSpanStart(), trimmedSpan.getSpanEnd() - 1);
                    coveredText = getCoveredText(trimmedSpan, documentText);
            }
            return trimmedSpan;
    }

}
