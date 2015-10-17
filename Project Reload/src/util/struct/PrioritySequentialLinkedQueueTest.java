/**
 * 
 */
package util.struct;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander
 *
 */
public class PrioritySequentialLinkedQueueTest {

	private static final int SIZE = 10;
	private Object zero = new Object();
	private Object one = new Object();
	private Object two = new Object();
	private Object three = new Object();
	
	/**
	 * From here on are premade tests for the official classes of the tck.
	 */
	/**
     * Returns a new queue of given size containing consecutive
     * Integers 0 ... n.
     */
    private PrioritySequentialLinkedQueue<Integer> populatedQueue(int n) {
        PrioritySequentialLinkedQueue<Integer> q = new PrioritySequentialLinkedQueue<Integer>(0);
        assertTrue(q.isEmpty());
        for (int i = 0; i < n; ++i)
            assertTrue(q.offer(new Integer(i)));
        assertFalse(q.isEmpty());
        assertEquals(n, q.size());
        return q;
    }

    /**
     * new queue is empty
     */
	@Test
    public void testConstructor1() {
        assertEquals(0, new PrioritySequentialLinkedQueue(0).size());
    }

    /**
     * Initializing from null Collection throws NPE
     */
	@Test
    public void testConstructor3() {
        try {
            new PrioritySequentialLinkedQueue((Collection)null, 0);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
	 * 
	 */
	private void shouldThrow() {
		fail("Method should have thrown an exception.");
	}

	/**
     * Initializing from Collection of null elements throws NPE
     */
	@Test
    public void testConstructor4() {
        try {
            new PrioritySequentialLinkedQueue(Arrays.asList(new Integer[SIZE]), 0);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Initializing from Collection with some null elements throws NPE
     */
	@Test
    public void testConstructor5() {
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE - 1; ++i)
            ints[i] = new Integer(i);
        try {
            new PrioritySequentialLinkedQueue(Arrays.asList(ints), 0);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements of collection used to initialize
     */
	@Test
    public void testConstructor6() {
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = new Integer(i);
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(Arrays.asList(ints), 0);
        for (int i = 0; i < SIZE; ++i)
            assertEquals(ints[i], q.poll());
    }

    /**
     * isEmpty is true before add, false after
     */
	@Test
    public void testEmpty() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        assertTrue(q.isEmpty());
        q.add(one);
        assertFalse(q.isEmpty());
        q.add(two);
        q.remove();
        q.remove();
        assertTrue(q.isEmpty());
    }

    /**
     * size changes when elements added and removed
     */
	@Test
    public void testSize() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(SIZE - i, q.size());
            q.remove();
        }
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            q.add(new Integer(i));
        }
    }

    /**
     * offer(null) throws NPE
     */
	@Test
    public void testOfferNull() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        try {
            q.offer(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * add(null) throws NPE
     */
	@Test
    public void testAddNull() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        try {
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Offer returns true
     */
	@Test
    public void testOffer() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        assertTrue(q.offer(zero));
        assertTrue(q.offer(one));
    }

    /**
     * add returns true
     */
	@Test
    public void testAdd() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            assertTrue(q.add(new Integer(i)));
        }
    }

    /**
     * addAll(null) throws NPE
     */
	@Test
    public void testAddAll1() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        try {
            q.addAll(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * addAll(this) throws IAE
     */
	@Test
    public void testAddAllSelf() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        try {
            q.addAll(q);
            shouldThrow();
        } catch (IllegalArgumentException success) {}
    }

    /**
     * addAll of a collection with null elements throws NPE
     */
	@Test
    public void testAddAll2() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        try {
            q.addAll(Arrays.asList(new Integer[SIZE]));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
	@Test
    public void testAddAll3() {
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE - 1; ++i)
            ints[i] = new Integer(i);
        try {
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements, in traversal order, of successful addAll
     */
	@Test
    public void testAddAll5() {
        Integer[] empty = new Integer[0];
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = new Integer(i);
        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        assertFalse(q.addAll(Arrays.asList(empty)));
        assertTrue(q.addAll(Arrays.asList(ints)));
        for (int i = 0; i < SIZE; ++i)
            assertEquals(ints[i], q.poll());
    }

    /**
     * poll succeeds unless empty
     */
	@Test
    public void testPoll() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.poll());
        }
        assertNull(q.poll());
    }

    /**
     * peek returns next element, or null if empty
     */
	@Test
    public void testPeek() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.peek());
            assertEquals(i, q.poll());
            assertTrue(q.peek() == null ||
                       !q.peek().equals(i));
        }
        assertNull(q.peek());
    }

    /**
     * element returns next element, or throws NSEE if empty
     */
	@Test
    public void testElement() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.element());
            assertEquals(i, q.poll());
        }
        try {
            q.element();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * remove removes next element, or throws NSEE if empty
     */
	@Test
    public void testRemove() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.remove());
        }
        try {
            q.remove();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * remove(x) removes x and returns true if present
     */
	@Test
    public void testRemoveElement() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 1; i < SIZE; i += 2) {
            assertTrue(q.contains(i));
            assertTrue(q.remove(i));
            assertFalse(q.contains(i));
            assertTrue(q.contains(i - 1));
        }
        for (int i = 0; i < SIZE; i += 2) {
            assertTrue(q.contains(i));
            assertTrue(q.remove(i));
            assertFalse(q.contains(i));
            assertFalse(q.remove(i + 1));
            assertFalse(q.contains(i + 1));
        }
        assertTrue(q.isEmpty());
    }

    /**
     * contains(x) reports true when elements added but not yet removed
     */
	@Test
    public void testContains() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.poll();
            assertFalse(q.contains(new Integer(i)));
        }
    }

    /**
     * clear removes all elements
     */
	@Test
    public void testClear() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        q.clear();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        q.add(one);
        assertFalse(q.isEmpty());
        q.clear();
        assertTrue(q.isEmpty());
    }

    /**
     * containsAll(c) is true when c contains a subset of elements
     */
	@Test
    public void testContainsAll() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        PrioritySequentialLinkedQueue p = new PrioritySequentialLinkedQueue(0);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(new Integer(i));
        }
        assertTrue(p.containsAll(q));
    }

    /**
     * retainAll(c) retains only those elements of c and reports true if change
     */
	@Test
    public void testRetainAll() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        PrioritySequentialLinkedQueue p = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE - i, q.size());
            p.remove();
        }
    }

    /**
     * removeAll(c) removes only those elements of c and reports true if changed
     */
	@Test
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
            PrioritySequentialLinkedQueue p = populatedQueue(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE - i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer x = (Integer)(p.remove());
                assertFalse(q.contains(x));
            }
        }
    }

    /**
     * toArray contains all elements in FIFO order
     */
	@Test
    public void testToArray() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        Object[] o = q.toArray();
        for (int i = 0; i < o.length; i++)
            assertSame(o[i], q.poll());
    }

    /**
     * toArray(a) contains all elements in FIFO order
     */
	@Test
    public void testToArray2() {
        PrioritySequentialLinkedQueue<Integer> q = populatedQueue(SIZE);
        Integer[] ints = new Integer[SIZE];
        Integer[] array = q.toArray(ints);
        assertSame(ints, array);
        for (int i = 0; i < ints.length; i++)
            assertSame(ints[i], q.poll());
    }

    /**
     * toArray(null) throws NullPointerException
     */
	@Test
    public void testToArray_NullArg() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        try {
            q.toArray(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * toArray(incompatible array type) throws ArrayStoreException
     */
	@Test
    public void testToArray1_BadArg() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        try {
            q.toArray(new String[10]);
            shouldThrow();
        } catch (ArrayStoreException success) {}
    }

    /**
     * iterator iterates through all elements
     */
	@Test
    public void testIterator() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        Iterator it = q.iterator();
        int i;
        for (i = 0; it.hasNext(); i++)
            assertTrue(q.contains(it.next()));
        assertEquals(i, SIZE);
        assertIteratorExhausted(it);
    }

    /**
	 * @param it
	 */
	private void assertIteratorExhausted(Iterator it) {
		assertFalse(it.hasNext());
	}

	/**
     * iterator of empty collection has no elements
     */
	@Test
    public void testEmptyIterator() {
        assertIteratorExhausted(new PrioritySequentialLinkedQueue(0).iterator());
    }

    /**
     * iterator ordering is FIFO
     */
	@Test
    public void testIteratorOrdering() {
        final PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        q.add(1);
        q.add(2);
        q.add(3);

        int k = 0;
        for (Iterator it = q.iterator(); it.hasNext();) {
            assertEquals(++k, it.next());
        	//System.out.println(++k + " : " + it.next());
        }

        assertEquals(3, k);
    }

    /**
     * Modifications do not cause iterators to fail
     */
	@Test
    public void testWeaklyConsistentIteration() {
        final PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        q.add(one);
        q.add(two);
        q.add(three);

        for (Iterator it = q.iterator(); it.hasNext();) {
            q.remove();
            it.next();
        }

        assertEquals("queue should be empty again", 0, q.size());
    }

    /**
     * iterator.remove removes current element
     */
	@Test
    public void testIteratorRemove() {
        final PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(0);
        q.add(one);
        q.add(two);
        q.add(three);
        Iterator it = q.iterator();
        it.next();
        it.remove();
        it = q.iterator();
        assertSame(it.next(), two);
        assertSame(it.next(), three);
        assertFalse(it.hasNext());
    }

    /**
     * toString contains toStrings of elements
     */
	@Test
    public void testToString() {
        PrioritySequentialLinkedQueue q = populatedQueue(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.contains(String.valueOf(i)));
        }
    }

    /**
     * A deserialized serialized queue has same elements in same order
     */
	@Test
    public void testSerialization() throws Exception {
        Queue x = populatedQueue(SIZE);
        Queue y = populatedQueue(SIZE);

        assertNotSame(x, y);
        assertEquals(x.size(), y.size());
        assertEquals(x.toString(), y.toString());
        assertTrue(Arrays.equals(x.toArray(), y.toArray()));
        while (!x.isEmpty()) {
            assertFalse(y.isEmpty());
            assertEquals(x.remove(), y.remove());
        }
        assertTrue(y.isEmpty());
    }

    /**
     * remove(null), contains(null) always return false
     */
	@Test
    public void testNeverContainsNull() {
        Collection<?>[] qs = {
            new PrioritySequentialLinkedQueue<Object>(0),
            populatedQueue(2),
        };

        for (Collection<?> q : qs) {
            assertFalse(q.contains(null));
            assertFalse(q.remove(null));
        }
    }
	
	/**
	 * Here starts the test for the priority aspect of the queue.
	 */
	static class MyReverseComparator implements Comparator {
	        public int compare(Object x, Object y) {
	            return ((Comparable)y).compareTo(x);
	        }
	    }

	    /**
	     * Returns a new queue of given size containing consecutive
	     * Integers 0 ... n.
	     */
	    private PrioritySequentialLinkedQueue<Integer> populatedQueue1(int n) {
	        PrioritySequentialLinkedQueue<Integer> q = new PrioritySequentialLinkedQueue<Integer>(1);
	        assertTrue(q.isEmpty());
	        for (int i = n - 1; i >= 0; i -= 2)
	            assertTrue(q.offer(new Integer(i)));
	        for (int i = (n & 1); i < n; i += 2)
	            assertTrue(q.offer(new Integer(i)));
	        assertFalse(q.isEmpty());
	        assertEquals(n, q.size());
	        return q;
	    }

	    /**
	     * A new queue has unbounded capacity
	     */
	    @Test
	    public void testConstructor11() {
	        assertEquals(0, new PrioritySequentialLinkedQueue(1).size());
	    }

	    /**
	     * Initializing from null Collection throws NPE
	     */
	    @Test
	    public void testConstructor31() {
	        try {
	            new PrioritySequentialLinkedQueue((Collection)null, 1);
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * The comparator used in constructor is used
	     */
	    @Test
	    public void testConstructor7() {
	        MyReverseComparator cmp = new MyReverseComparator();
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(cmp, 1);
	        assertEquals(cmp, q.comparator());
	        Integer[] ints = new Integer[SIZE];
	        for (int i = 0; i < SIZE; ++i)
	            ints[i] = new Integer(i);
	        q.addAll(Arrays.asList(ints));
	        for (int i = SIZE - 1; i >= 0; --i)
	            assertEquals(ints[i], q.poll());
	    }

	    /**
	     * isEmpty is true before add, false after
	     */
	    @Test
	    public void testEmpty1() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        assertTrue(q.isEmpty());
	        q.add(new Integer(1));
	        assertFalse(q.isEmpty());
	        q.add(new Integer(2));
	        q.remove();
	        q.remove();
	        assertTrue(q.isEmpty());
	    }

	    /**
	     * size changes when elements added and removed
	     */
	    @Test
	    public void testSize1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(SIZE - i, q.size());
	            q.remove();
	        }
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.size());
	            q.add(new Integer(i));
	        }
	    }

	    /**
	     * offer(null) throws NPE
	     */
	    @Test
	    public void testOfferNull1() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        try {
	            q.offer(null);
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * add(null) throws NPE
	     */
	    @Test
	    public void testAddNull1() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        try {
	            q.add(null);
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * Offer of comparable element succeeds
	     */
	    @Test
	    public void testOffer1() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        assertTrue(q.offer(zero));
	        assertTrue(q.offer(one));
	    }

	    /**
	     * add of comparable succeeds
	     */
	    @Test
	    public void testAdd1() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.size());
	            assertTrue(q.add(new Integer(i)));
	        }
	    }

	    /**
	     * addAll(null) throws NPE
	     */
	    @Test
	    public void testAddAll11() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        try {
	            q.addAll(null);
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * addAll of a collection with null elements throws NPE
	     */
	    @Test
	    public void testAddAll21() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        try {
	            q.addAll(Arrays.asList(new Integer[SIZE]));
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * addAll of a collection with any null elements throws NPE after
	     * possibly adding some elements
	     */
	    @Test
	    public void testAddAll31() {
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        Integer[] ints = new Integer[SIZE];
	        for (int i = 0; i < SIZE - 1; ++i)
	            ints[i] = new Integer(i);
	        try {
	            q.addAll(Arrays.asList(ints));
	            shouldThrow();
	        } catch (NullPointerException success) {}
	    }

	    /**
	     * Queue contains all elements of successful addAll
	     */
	    @Test
	    public void testAddAll51() {
	        Integer[] empty = new Integer[0];
	        Integer[] ints = new Integer[SIZE];
	        for (int i = 0; i < SIZE; ++i)
	            ints[i] = new Integer(SIZE - 1 - i);
	        PrioritySequentialLinkedQueue q = new PrioritySequentialLinkedQueue(1);
	        assertFalse(q.addAll(Arrays.asList(empty)));
	        assertTrue(q.addAll(Arrays.asList(ints)));
	        for (int i = 0; i < SIZE; ++i)
	            assertEquals(new Integer(i), q.poll());
	    }

	    /**
	     * poll succeeds unless empty
	     */
	    @Test
	    public void testPoll1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.poll());
	        }
	        assertNull(q.poll());
	    }

	    /**
	     * peek returns next element, or null if empty
	     */
	    @Test
	    public void testPeek1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.peek());
	            assertEquals(i, q.poll());
	            assertTrue(q.peek() == null ||
	                       !q.peek().equals(i));
	        }
	        assertNull(q.peek());
	    }

	    /**
	     * element returns next element, or throws NSEE if empty
	     */
	    @Test
	    public void testElement1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.element());
	            assertEquals(i, q.poll());
	        }
	        try {
	            q.element();
	            shouldThrow();
	        } catch (NoSuchElementException success) {}
	    }

	    /**
	     * remove removes next element, or throws NSEE if empty
	     */
	    @Test
	    public void testRemove1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertEquals(i, q.remove());
	        }
	        try {
	            q.remove();
	            shouldThrow();
	        } catch (NoSuchElementException success) {}
	    }

	    /**
	     * remove(x) removes x and returns true if present
	     */
	    @Test
	    public void testRemoveElement1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 1; i < SIZE; i += 2) {
	            assertTrue(q.contains(i));
	            assertTrue(q.remove(i));
	            assertFalse(q.contains(i));
	            assertTrue(q.contains(i - 1));
	        }
	        for (int i = 0; i < SIZE; i += 2) {
	            assertTrue(q.contains(i));
	            assertTrue(q.remove(i));
	            assertFalse(q.contains(i));
	            assertFalse(q.remove(i + 1));
	            assertFalse(q.contains(i + 1));
	        }
	        assertTrue(q.isEmpty());
	    }

	    /**
	     * contains(x) reports true when elements added but not yet removed
	     */
	    @Test
	    public void testContains1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            assertTrue(q.contains(new Integer(i)));
	            q.poll();
	            assertFalse(q.contains(new Integer(i)));
	        }
	    }

	    /**
	     * clear removes all elements
	     */
	    @Test
	    public void testClear1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        q.clear();
	        assertTrue(q.isEmpty());
	        assertEquals(0, q.size());
	        q.add(new Integer(1));
	        assertFalse(q.isEmpty());
	        q.clear();
	        assertTrue(q.isEmpty());
	    }

	    /**
	     * containsAll(c) is true when c contains a subset of elements
	     */
	    @Test
	    public void testContainsAll1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        PrioritySequentialLinkedQueue p = new PrioritySequentialLinkedQueue(1);
	        for (int i = 0; i < SIZE; ++i) {
	            assertTrue(q.containsAll(p));
	            assertFalse(p.containsAll(q));
	            p.add(new Integer(i));
	        }
	        assertTrue(p.containsAll(q));
	    }

	    /**
	     * retainAll(c) retains only those elements of c and reports true if changed
	     */
	    @Test
	    public void testRetainAll1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        PrioritySequentialLinkedQueue p = populatedQueue1(SIZE);
	        for (int i = 0; i < SIZE; ++i) {
	            boolean changed = q.retainAll(p);
	            if (i == 0)
	                assertFalse(changed);
	            else
	                assertTrue(changed);

	            assertTrue(q.containsAll(p));
	            assertEquals(SIZE - i, q.size());
	            p.remove();
	        }
	    }

	    /**
	     * removeAll(c) removes only those elements of c and reports true if changed
	     */
	    @Test
	    public void testRemoveAll1() {
	        for (int i = 1; i < SIZE; ++i) {
	            PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	            PrioritySequentialLinkedQueue p = populatedQueue1(i);
	            assertTrue(q.removeAll(p));
	            assertEquals(SIZE - i, q.size());
	            for (int j = 0; j < i; ++j) {
	                Integer x = (Integer)(p.remove());
	                assertFalse(q.contains(x));
	            }
	        }
	    }

	    /**
	     * toArray contains all elements
	     */
	    @Test
	    public void testToArray1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        Object[] o = q.toArray();
	        Arrays.sort(o);
	        for (int i = 0; i < o.length; i++)
	            assertSame(o[i], q.poll());
	    }

	    /**
	     * toArray(a) contains all elements
	     */
	    @Test
	    public void testToArray21() {
	        PrioritySequentialLinkedQueue<Integer> q = populatedQueue1(SIZE);
	        Integer[] ints = new Integer[SIZE];
	        Integer[] array = q.toArray(ints);
	        assertSame(ints, array);
	        Arrays.sort(ints);
	        for (int i = 0; i < ints.length; i++) {
	            assertSame(ints[i], q.poll());
	        }
	    }

	    /**
	     * iterator iterates through all elements
	     */
	    @Test
	    public void testIterator1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        Iterator it = q.iterator();
	        int i;
	        for (i = 0; it.hasNext(); i++)
	            assertTrue(q.contains(it.next()));
	        assertEquals(i, SIZE);
	        assertIteratorExhausted(it);
	    }

	    /**
	     * iterator of empty collection has no elements
	     */
	    @Test
	    public void testEmptyIterator1() {
	        assertIteratorExhausted(new PrioritySequentialLinkedQueue(1).iterator());
	    }

	    /**
	     * toString contains toStrings of elements
	     */
	    @Test
	    public void testToString1() {
	        PrioritySequentialLinkedQueue q = populatedQueue1(SIZE);
	        String s = q.toString();
	        for (int i = 0; i < SIZE; ++i) {
	            assertTrue(s.contains(String.valueOf(i)));
	        }
	    }

	    /**
	     * A deserialized serialized queue has same elements
	     */
	    @Test
	    public void testSerialization1() throws Exception {
	        Queue x = populatedQueue1(SIZE);
	        Queue y = populatedQueue1(SIZE);

	        assertNotSame(x, y);
	        assertEquals(x.size(), y.size());
	        while (!x.isEmpty()) {
	            assertFalse(y.isEmpty());
	            assertEquals(x.remove(), y.remove());
	        }
	        assertTrue(y.isEmpty());
	    }
}
