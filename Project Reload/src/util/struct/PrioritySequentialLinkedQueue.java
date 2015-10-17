/**
 * 
 */
package util.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author Alexander
 * @param <T> 
 *
 */
public class PrioritySequentialLinkedQueue<T> implements Iterable<T>, Queue<T>, Serializable, Collection<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4656219768455626632L;
	private static class DefaultComparator<T> implements Comparator<T> {
		@SuppressWarnings("unchecked")
		public int compare(T x, T y) {
			try {
				return ((Comparable<T>)x).compareTo(y);
			}
			catch (ClassCastException e) {
				throw e;
			}
        }
    }
	private class LinkableNode implements Comparable<T> {
		public LinkableNode previous;
		public LinkableNode next;
		public int arrayPosition;
		public final int priority;
		public final T container;
		
		public LinkableNode(T e, int pPriority, int pArrayPosition) {
			previous = null;
			next = null;
			arrayPosition = pArrayPosition;
			priority = pPriority;
			container = e;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object arg0) {
			try {
				return _cmp.compare(container, ((LinkableNode)arg0).container);
			}
			catch (ClassCastException e) {
				return (priority - ((LinkableNode)arg0).priority) % 1;
			}
		}
	}
	private Comparator<T> _cmp;
	private ArrayList<LinkableNode> _tree;
	private LinkableNode _firstElement;
	private LinkableNode _lastElement;
	private int _size;
	private float _priorityRatio;
	private float _lastRatio;
	/**
	 * The default priority that each element has if no priority is specified. The program-given default is 0.
	 */
	public int defaultPriority;
	
	/**
	 * @param pPriorityRatio The ratio you get by dividing (number of elements taken from the priority queue) / (number of elements taken from the normal queue). The value should be between 0 and 1. Given the number 0 the queue will behave like a normal queue, given the number 1 the queue will behave like a normal priority queue.
	 * 
	 */
	public PrioritySequentialLinkedQueue(float pPriorityRatio) {
		if (pPriorityRatio < 0 || pPriorityRatio > 1) {
			throw new IllegalArgumentException("pPriorityRatio must be between 0 and 1.");
		}
		_tree = new ArrayList<LinkableNode>();
		_firstElement = null;
		_lastElement = null;
		_priorityRatio = pPriorityRatio;
		_lastRatio = _priorityRatio;
		defaultPriority = 0;
		_cmp = new DefaultComparator<T>();
	}
	

	/**
	 * @param pComp
	 * @param pPriorityRatio
	 */
	public PrioritySequentialLinkedQueue(Comparator<T> pComp, float pPriorityRatio) {
		if (pPriorityRatio < 0 || pPriorityRatio > 1) {
			throw new IllegalArgumentException("pPriorityRatio must be between 0 and 1.");
		}
		_tree = new ArrayList<LinkableNode>();
		_firstElement = null;
		_lastElement = null;
		_priorityRatio = pPriorityRatio;
		_lastRatio = _priorityRatio;
		defaultPriority = 0;
		_cmp = pComp;
	}

	/**
	 * @param pCollection 
	 * @param pPriorityRatio 
	 */
	public PrioritySequentialLinkedQueue(Collection<T> pCollection, float pPriorityRatio) {
		if (pPriorityRatio < 0 || pPriorityRatio > 1) {
			throw new IllegalArgumentException("pPriorityRatio must be between 0 and 1.");
		}
		_tree = new ArrayList<LinkableNode>();
		_firstElement = null;
		_lastElement = null;
		_priorityRatio = pPriorityRatio;
		_lastRatio = _priorityRatio;
		defaultPriority = 0;
		_cmp = new DefaultComparator<T>();
		
		addAll(pCollection);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (c == this) {
			throw new IllegalArgumentException("You can't add a queue to itself.");
		}
		boolean success = true;
		boolean changed = false;
		for (T o : c) {
			if (add(o)) {
				changed = true;
			}
			else {
				success = false;
			}
		}
		return changed && success;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		_firstElement = null;
		_lastElement = null;
		_lastRatio = _priorityRatio;
		_size = 0;
		_tree.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		// Iterate over the elements and compare
		LinkableNode i = _firstElement;
		while(i != null) {
			if (i.container.equals(o)) {
				return true;
			}
			i = i.next;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return _size == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		LinkableNode i = _firstElement;
		while (i != null) {
			if (i.container.equals(o)) {
				removeNode(i);
				return true;
			}
			i = i.next;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean success = false;
		for (Object o : c) {
			while (remove(o)) {
				success = true;
			}
		}
		return success;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		LinkableNode i = _firstElement;
		boolean changed = false;
		while (i != null) {
			if (!c.contains(i.container)) {
				removeNode(i);
				changed = true;
			}
			i = i.next;
		}
		return changed;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return _size;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public T[] toArray() {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[_size];
		LinkableNode ni = _firstElement;
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ni.container;
			ni = ni.next;
		}
		return arr;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <A> A[] toArray(A[] a) {
		T[] arr = toArray();
		if (arr.length > a.length){
			return (A[]) arr;
		}
		else {
			for (int i = 0; i < arr.length; i++) {
				a[i] = (A)arr[i];
			}
			for (int i = arr.length; i < a.length; i++) {
				a[i] = null;
			}
			return a;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
	public boolean add(T e) {
		return add(e, defaultPriority);
	}
	
	/**
	 * @see PrioritySequentialLinkedQueue#add(Object)
	 * @param e The element that will be added to the queue.
	 * @param priority The priority that will be given to the added element.
	 * @return true because adding an element can't fail. (At least it shouldn't)
	 */
	public boolean add(T e, int priority) {
		return offer(e, priority);
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#element()
	 */
	@Override
	public T element() {
		if(isEmpty()) {
			throw new NoSuchElementException();
		}
		return peek();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(T e) {
		return offer(e, defaultPriority);
	}
	
	/**
	 * @see PrioritySequentialLinkedQueue#offer(Object)
	 * @param e The object that is being offered to the queue.
	 * @param priority The priority that will be assigned to the element.
	 * @return true because offering an object to this queue can't fail.
	 */
	public boolean offer(T e, int priority) {
		if (e == null) {
			throw new NullPointerException();
		}
		if (isEmpty()) {
			_lastElement = new LinkableNode(e, priority, _size);
			_firstElement = _lastElement;
			_tree.add(_lastElement);
		}
		else {
			_lastElement.next = new LinkableNode(e, priority, _size);
			_lastElement.next.previous = _lastElement;
			_lastElement = _lastElement.next;
			_tree.add(_lastElement);
			LinkableNode climber = _lastElement;
			while (climber.arrayPosition != 0 && climber.compareTo(getTreeParent(climber)) < 0) { // TODO is this > or < 0? Test!
				switchTreeNodes(climber, getTreeParent(climber));
			}
		}
		_size++;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#peek()
	 */
	@Override
	public T peek() {
		if (isEmpty()) {
			return null;
		}
		if ((_priorityRatio < 0.5 && _lastRatio >= _priorityRatio) || _lastRatio > _priorityRatio) {
			return _firstElement.container;
		}
		else {
			return _tree.get(0).container;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#poll()
	 */
	@Override
	public T poll() {
		if (isEmpty()) {
			return null;
		}
		T result = peek();
		// Remove element
		if ((_priorityRatio < 0.5 && _lastRatio >= _priorityRatio) || _lastRatio > _priorityRatio) {
			_lastRatio = _lastRatio + (0 - _priorityRatio);
			System.out.print("0");
			removeNode(_firstElement);
		}
		else {
			_lastRatio = _lastRatio + (1 - _priorityRatio);
			System.out.print("1");
			removeNode(_tree.get(0));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#remove()
	 */
	@Override
	public T remove() {
		if (isEmpty()) {
			throw (new NoSuchElementException());
		}
		return poll();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private LinkableNode _next = _firstElement;
			private LinkableNode _current = null;
			@Override
			public boolean hasNext() {
				return _next != null;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				_current = _next;
				_next = _current.next;
				return _current.container;
			}
			
			@Override
			public void remove() {
				if (_current == null) {
					throw new IllegalStateException("remove() can only be called after next().");
				}
				removeNode(_current);
				_current = null;
			}
		};
	}
	
	private final LinkableNode getTreeParent(LinkableNode e) {
		int pos = Math.floorDiv(e.arrayPosition - 1, 2);
		if (pos < 0) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final LinkableNode getTreeLeftChild(LinkableNode e) {
		int pos = e.arrayPosition * 2 + 1;
		if (pos >= _size) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final LinkableNode getTreeRightChild(LinkableNode e) {
		int pos = e.arrayPosition * 2 + 2;
		if (pos >= _size) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final void switchTreeNodes(LinkableNode e1, LinkableNode e2) {
		int e1ArrayPosition = e1.arrayPosition;
		e1.arrayPosition = e2.arrayPosition;
		e2.arrayPosition = e1ArrayPosition;
		_tree.set(e1.arrayPosition, e1);
		_tree.set(e2.arrayPosition, e2);
	}
	
	private final void removeNode(LinkableNode e) {
		// Bring e into a position where it can be removed
		LinkableNode climber = _tree.get(_size - 1);
		switchTreeNodes(e, climber);
		// Remove e
		if (_firstElement == e) {
			_firstElement = e.next;
		}
		if (_lastElement == e) {
			_lastElement = e.previous;
		}
		if (e.next != null) {
			e.next.previous = e.previous;
		}
		if (e.previous != null) {
			e.previous.next = e.next;
		}
		_tree.remove(e.arrayPosition);
		_size--;
		// Reconstruct the tree
		while (getTreeParent(climber) != null && climber.compareTo(getTreeParent(climber)) < 0) { // TODO is this > or < 0? Test!
			switchTreeNodes(climber, getTreeParent(climber));
		}
		if (climber.arrayPosition < _size) {
			LinkableNode max = null;
			while (max != climber) {
				max = climber;
				if (getTreeLeftChild(climber) != null && getTreeLeftChild(climber).compareTo(max) < 0) {
					max = getTreeLeftChild(climber);
				}
				if (getTreeRightChild(climber) != null && getTreeRightChild(climber).compareTo(max) < 0) {
					max = getTreeRightChild(climber);
				}
				switchTreeNodes(max, climber);
			}	
		}
	}

	/**
	 * @see java.util.PriorityQueue#comparator()
	 * @return The currently used comparator.
	 */
	public Object comparator() {
		return _cmp;
	}
	
	/**
	 * @param pQueue
	 * @return A String-representation of the queue.
	 */
	public static String toString(PrioritySequentialLinkedQueue<?> pQueue) {
		return pQueue.toString();
	}
	
	@Override
	public String toString() {
		String str = "";
		for (T element : this) {
			str = str + element.toString();
		}
		return str;
	}

}
