/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.nlp.core.annotation;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;



public class SpanUtilsTests {
    
    @Test
    public void testSpan0() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(3, 5);
        assertEquals(span, span2);
        assertEquals(span.getSpanStart(), 3);
        assertEquals(span.getSpanEnd(), 5);
    }

    @Test
    public void testSpan1_1() throws Exception {
        Span span = new Span(3, 6);
        Span span2 = new Span(5, 10);
        List<Span> norm = SpanUtils.reduceSpans(span, span2);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan1_2() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(7, 10);
        List<Span> norm = SpanUtils.reduceSpans(span, span2);
        assertEquals(2, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }
    
//    @Test
//    public void testSpan1_3() throws Exception {
//        Span span2 = new Span(3, 6);
//        Span span = new Span(5, 10);
//        List<Span> norm = reduceSpans(span, span2);
//        assertEquals(1, norm.size());
//        Span mergeSpan = norm.get(0);
//        assertEquals(3, mergeSpan.getSpanStart());
//        assertEquals(10, mergeSpan.getSpanEnd());
//    }
//
//    @Test
//    public void testSpan1_4() throws Exception {
//        Span span2 = new Span(3, 5);
//        Span span = new Span(7, 10);
//        List<Span> norm = reduceSpans(span, span2);
//        assertEquals(2, norm.size());
//        Span mergeSpan = norm.get(0);
//        assertEquals(3, mergeSpan.getSpanStart());
//        assertEquals(5, mergeSpan.getSpanEnd());
//        mergeSpan = norm.get(1);
//        assertEquals(7, mergeSpan.getSpanStart());
//        assertEquals(10, mergeSpan.getSpanEnd());
//    }
    
    @Test
    public void testSpan2_1() throws Exception {
        Span span = new Span(3, 6);
        Span span2 = new Span(5, 10);
        List<Span> spanList = new LinkedList<Span>();
        spanList.add(span2);
        List<Span> norm = SpanUtils.reduceSpans(span, spanList);

        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan2_2() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(7, 10);
        List<Span> spanList = new LinkedList<Span>();
        spanList.add(span2);
        List<Span> norm = SpanUtils.reduceSpans(span, spanList);
        assertEquals(2, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }
    

    @Test
    public void testSpan3_1() throws Exception {
        Span span = new Span(3, 6);
        Span span2 = new Span(5, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span normSpan = norm.get(0);
        assertEquals(3, normSpan.getSpanStart());
        assertEquals(10, normSpan.getSpanEnd());
    }
    @Test
    public void testSpan3_2() throws Exception {
        Span span2 = new Span(3, 6);
        Span span = new Span(5, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span normSpan = norm.get(0);
        assertEquals(3, normSpan.getSpanStart());
        assertEquals(10, normSpan.getSpanEnd());
    }

    @Test
    public void testSpan4_1() throws Exception {
        Span span = new Span(3, 6);
        Span span2 = new Span(5, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan4_2() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(7, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(2, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }
    
    @Test
    public void testSpan4_3() throws Exception {
        Span span2 = new Span(3, 6);
        Span span = new Span(5, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan4_4() throws Exception {
        Span span2 = new Span(3, 5);
        Span span = new Span(7, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(2, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }
    
    @Test
    public void testSpan4_5() throws Exception {
        Span span = new Span(3, 6);
        Span span2 = new Span(5, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan4_6() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(7, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(2, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }
    

    @Test
    public void testSpan5_1() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(7, 10);
        Span span3 = new Span(4, 8);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        spans.add(span3);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan5_2() throws Exception {
        Span span = new Span(3, 5);
        Span span2 = new Span(2, 10);
        Span span3 = new Span(4, 8);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        spans.add(span3);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(1, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(2, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
    }

    @Test
    public void testSpan6_1() throws Exception {
        Span span = new Span(13, 15);
        Span span2 = new Span(3, 5);
        Span span3 = new Span(7, 10);
        List<Span> spans = new LinkedList<Span>();
        spans.add(span);
        spans.add(span2);
        spans.add(span3);
        List<Span> norm = SpanUtils.normalizeSpans(spans);
        assertEquals(3, norm.size());
        Span mergeSpan = norm.get(0);
        assertEquals(3, mergeSpan.getSpanStart());
        assertEquals(5, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(1);
        assertEquals(7, mergeSpan.getSpanStart());
        assertEquals(10, mergeSpan.getSpanEnd());
        mergeSpan = norm.get(2);
        assertEquals(13, mergeSpan.getSpanStart());
        assertEquals(15, mergeSpan.getSpanEnd());
    }

    
    
    
}
