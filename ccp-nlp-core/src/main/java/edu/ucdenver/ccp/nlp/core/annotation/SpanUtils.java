package edu.ucdenver.ccp.nlp.core.annotation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SpanUtils {

    
    public static Span outerSpan (List<Span> spans) {
        int minSpan = spans.get(0).getSpanStart();
        int maxSpan = spans.get(0).getSpanEnd();
        for (Span span : spans) {
            if (span.getSpanStart() < minSpan) {
                minSpan = span.getSpanStart();
            }
            if (span.getSpanEnd() > maxSpan) {
                maxSpan = span.getSpanEnd();
            }
        }
        try {
            return new Span(minSpan, maxSpan);
        } catch (InvalidSpanException e) { }
        return null;
    }
    
    
    
    public static List<Span> normalizeSpans (List<Span> spans) {
        //Why is the following a cast exception?
        //return mergeSpans((Span[])spans.toArray());
        List<Span> mergedSpans = new LinkedList<Span>();
        for (Span s : spans) {
            mergedSpans = mergeSpan(mergedSpans, s);
        }
        return mergedSpans;

    }
    
    public static List<Span> mergeSpans (List<Span> spans, List<Span> newSpans) {
        List<Span> mergedSpans = new LinkedList<Span>(spans);
        for (Span s : newSpans) {
            mergedSpans = mergeSpan(mergedSpans, s);
        }
        return mergedSpans;
    }

    public static List<Span> mergeSpans (List<Span> spans, Span... newSpans) {
        List<Span> mergedSpans = new LinkedList<Span>(spans);
        for (Span s : newSpans) {
            mergedSpans = mergeSpan(mergedSpans, s);
        }
        return mergedSpans;
    }

    public static List<Span> mergeSpans (Span... newSpans) {
        List<Span> mergedSpans = new LinkedList<Span>();
        for (Span s : newSpans) {
            mergedSpans = mergeSpan(mergedSpans, s);
        }
        return mergedSpans;
    }
    
    
    public static List<Span> mergeSpan (List<Span> spans, Span newSpan) {
        LinkedList<Span> lowSpans = new LinkedList<Span>();
        Span s;
        if (spans.isEmpty()) {
            lowSpans.add(newSpan);
            return lowSpans;
        }
        //iterate off the low spans
        Iterator<Span> sIter = spans.iterator();
        boolean keepGoing = true;
        do {
            s = sIter.next();
            if (s.getSpanStart() < newSpan.getSpanStart()){
                lowSpans.add(s);
            } else {
                keepGoing = false;
            }
        } while (sIter.hasNext() && keepGoing);
        //at this point the sIter points at only intersecting or greater segments
        //as the last span did not have a start before our spans start
        //so now just pop off wholly subsumed spans
        while ((s.getSpanEnd() < newSpan.getSpanEnd()) && sIter.hasNext()) {
            s = sIter.next();
        }
        //at this point s is equal to a span that is either intersects with our span or is greater
        //and the rest of the iterator is greater
        LinkedList<Span> mergedSpans = new LinkedList<Span>();
        while(sIter.hasNext()) {
            mergedSpans.add(sIter.next());
        }
        mergedSpans = reduceSpans(s, mergedSpans);
        mergedSpans = reduceSpans(newSpan, mergedSpans);
        if (! lowSpans.isEmpty()) {
            mergedSpans = reduceSpans(lowSpans.removeLast(), mergedSpans);
        }
        if (! lowSpans.isEmpty()) {
            mergedSpans.addAll(0, lowSpans);
        }
        return mergedSpans;
    }

    public static int max (int i, int j) {
        if (j > i) {
            return j;
        } else {
            return i;
        }
    }
    
    //assumes that s1.start is < s2.start
    public static LinkedList<Span> reduceSpans (Span s1, Span s2) {
        LinkedList<Span> spans = new LinkedList<Span>();
        if (s1.getSpanEnd() >= s2.getSpanStart()) {
            try {
                spans.add(new Span(s1.getSpanStart(), max(s1.getSpanEnd(), s2.getSpanEnd())));
            } catch (InvalidSpanException e) { } //I refuse to throw this exception
        } else {
            spans.add(s1);
            spans.add(s2);
        }
        return spans;
    }

    //s1 could overlap with multiple elements in s2
    //  but it shouldn't be called that way by merge    
    public static LinkedList<Span> reduceSpans (Span s1, List<Span> s2) {
        LinkedList<Span> spans = new LinkedList<Span>(s2);
        if ((!s2.isEmpty()) && (s1.getSpanEnd() >= s2.get(0).getSpanStart())) {
            Span s3 = spans.removeFirst();
            try {
                //spans.add(new Span(s1.getSpanStart(), s3.getSpanEnd()));
                //spans.addFirst(new Span(s1.getSpanStart(), s3.getSpanEnd()));
                spans.addFirst(new Span(s1.getSpanStart(), max(s1.getSpanEnd(), s3.getSpanEnd())));                
            } catch (InvalidSpanException e) { } //I refuse to throw this exception
        } else {
            //spans.add(s1);
            spans.addFirst(s1);
        }
        return spans;
    }
        
        

    
    
    
    public static List<Span> spansEqualOrLesser (List<Span> spans, int val) {
        LinkedList<Span> selectedSpans = new LinkedList<Span>();
        if (spans.isEmpty()) {
            return selectedSpans;
        } 
        Span s = spans.get(0); 
        if (s.getSpanStart() > val) { //they are all over the val
            return selectedSpans;
        }
        //iterate and collect all under val
        Iterator<Span> sIter = spans.iterator();
        do {
            s = sIter.next();
            if ((s.getSpanStart() < val) || (s.getSpanEnd() < val)) {
                selectedSpans.add(s);
            } else {
                return selectedSpans;
            }
        } while (sIter.hasNext());
        return selectedSpans;
    }
    
    //this is a double traversal unless the first copy is a lazy copy
//    public static List<Span> spansEqualOrGreater (List<Span> spans, int val) {
//        LinkedList<Span> selectedSpans = new LinkedList<Span>(spans);
//        if (spans.isEmpty()) {
//            return selectedSpans;
//        } 
//        
//        
//        Span s = spans.get(0); 
//        //iterate and collect all under val
//        Iterator<Span> sIter = spans.iterator();
//        do {
//            s = sIter.next();
//            if ((s.getSpanStart() < val) || (s.getSpanEnd() < val)) {
//                selectedSpans.add(s);
//            } else {
//                return selectedSpans;
//            }
//        } while (sIter.hasNext());
//        return selectedSpans;
//    }

    
}
