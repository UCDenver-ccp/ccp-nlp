package edu.ucdenver.ccp.nlp.core.annotation.comparison;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.Span;

public class SubSpanComparatorTest {
	
	@Test 
	public void testEqual() throws Exception  {
		// -----
		// -----
		Span a = new Span(3,10);
		Span b = new Span(3,10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
	}
	
	@Test
	public void testInside() throws Exception  {
		//  ---
		// -----
		Span a = new Span(5,8);
		Span b = new Span(3,10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideLeft()  throws Exception {
		// --
		//     ---
		Span a = new Span(3,5);
		Span b = new Span(6,10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideRight()  throws Exception {
		//      --
		// --
		Span a = new Span(8,10);
		Span b = new Span(3,5);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a,b));
		
	}

	@Test
	public void testOutsideLeftEqual()  throws Exception {
		// ---
		//   ---
		Span a = new Span(3,5);
		Span b = new Span(5,10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideRightEqual()  throws Exception {
		//    ----
		// ----
		Span a = new Span(6,10);
		Span b = new Span(3,6);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testEqualLeft()  throws Exception {
		// -----
		// --
		Span a = new Span(3,10);
		Span b = new Span(3,6);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}
	
	@Test
	public void testEqualRight()  throws Exception {
		// -----
		//   ---
		Span a = new Span(3,10);
		Span b = new Span(8,10);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}

	
	
	
	
	
	
	
	
	
	@Test 
	public void testEqualLists() throws Exception  {
		// -----
		// -----
		Span a = new Span(3,10);	Span a1 = new Span(13,20);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(3,10);	Span b1 = new Span(13,20);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(aList,bList));
	}
	
	@Test
	public void testInsideLists() throws Exception  {
		//  ---
		// -----
		Span a = new Span(5,8); Span a1 = new Span(9,12);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(3,10); Span b1 = new Span(12,20);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideLeftLists()  throws Exception {
		// --
		//     ---
		Span a = new Span(3,5); Span a1 = new Span(7,9);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(11,13); Span b1 = new Span(15,20);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideRightLists()  throws Exception {
		//      --
		// --
		Span a = new Span(11, 13); Span a1 = new Span(15, 17);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(3,5); Span b1 = new Span(7,9);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a,b));
		
	}

	@Test
	public void testOutsideLeftEqualLists()  throws Exception {
		// ---
		//   ---
		Span a = new Span(3,5); Span a1 = new Span(8,12);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(5,10); Span b1 = new Span(12, 15);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testOutsideRightEqualLists()  throws Exception {
		//    ----
		// ----
		Span a = new Span(8,10); Span a1 = new Span(12,15);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(3,6); Span b1 = new Span(7,11);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(-1, ssc.compare(a,b));
		
	}
	
	@Test
	public void testEqualLeftLists()  throws Exception {
		// -----
		// --
		Span a = new Span(3,10); Span a1 = new Span(12,20);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(3,6); Span b1 = new Span(7,10);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}
	
	@Test
	public void testEqualRightLists()  throws Exception {
		// -----
		//   ---
		Span a = new Span(3,10); Span a1 = new Span(12,20);
		List<Span> aList= new ArrayList<Span>();
		aList.add(a); aList.add(a1);
		Span b = new Span(8,10); Span b1 = new Span(12,20);
		List<Span> bList= new ArrayList<Span>();
		bList.add(b); bList.add(b1);
		SubSpanComparator ssc = new SubSpanComparator();
		assertEquals(0, ssc.compare(a,b));
		
	}
}
