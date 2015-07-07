/**
 * 
 */
package util.struct;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author Alexander
 *
 */
public class PrioritySequentialLinkedQueue<T> implements Iterable<T>, Queue<T> {
	private class LinkableNode {
		public LinkableNode previous;
		public LinkableNode next;
		public int arrayPosition;
		public int priority;
		public T container;
		
		public LinkableNode(T e, int pPriority, int pArrayPosition) {
			previous = null;
			next = null;
			arrayPosition = pArrayPosition;
			priority = pPriority;
			container = e;
		}
	}
	private ArrayList<LinkableNode> _tree;
	private LinkableNode _firstElement;
	private LinkableNode _lastElement;
	private int _size;
	private float _priorityRatio;
	private float _lastRatio;
	public int defaultPriority;
	/**
	 * 
	 */
	public PrioritySequentialLinkedQueue(float pPriorityRatio) {
		_tree = new ArrayList<LinkableNode>();
		_firstElement = null;
		_lastElement = null;
		_priorityRatio = pPriorityRatio;
		_lastRatio = _priorityRatio;
		defaultPriority = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean success = true;
		for (T o : c) {
			if (!add(o)) {
				success = false;
			}
		}
		return success;
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		// Iterate over the elements and compare
		LinkableNode i = _firstElement;
		while(i != null) {
			if (i == o) {
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
			if (i.container == o) {
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
		boolean success = true;
		for (Object o : c) {
			if (!remove(o)) {
				success = false;
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
		while (i != null) {
			if (!c.contains(i.container)) {
				removeNode(i);
			}
			i = i.next;
		}
		return true;
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
		A[] aarr;
		try {
			aarr = (A[]) arr;
		}
		catch(ClassCastException e) {
			return null;
		}
		return aarr;
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
	public boolean add(T e) {
		return add(e, defaultPriority);
	}
	
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
	
	public boolean offer(T e, int priority) {
		_size++;
		if (isEmpty()) {
			_lastElement = new LinkableNode(e, priority, _size - 1);
			_firstElement = _lastElement;
			_tree.add(_lastElement);
		}
		else {
			_lastElement.next = new LinkableNode(e, priority, _size - 1);
			_lastElement = _lastElement.next;
			_tree.add(_lastElement);
			LinkableNode climber = _lastElement;
			while (climber.arrayPosition != 0 && climber.priority > getTreeParent(climber).priority) {
				switchTreeNodes(climber, getTreeParent(climber));
			}
		}
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
		if (_lastRatio > _priorityRatio) {
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
		if (_lastRatio > _priorityRatio) {
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
			@Override
			public boolean hasNext() {
				return _next != null;
			}

			@Override
			public T next() {
				T e = _next.container;
				_next = _next.next;
				return e;
			}
		};
	}
	
	private final LinkableNode getTreeParent(LinkableNode e) {
		int pos = Math.floorDiv(e.arrayPosition, 2);
		if (pos < 0) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final LinkableNode getTreeLeftChild(LinkableNode e) {
		int pos = e.arrayPosition * 2;
		if (pos >= _size) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final LinkableNode getTreeRightChild(LinkableNode e) {
		int pos = e.arrayPosition * 2 + 1;
		if (pos >= _size) {
			return null;
		}
		else {
			return _tree.get(pos);
		}
	}
	
	private final void switchTreeNodes(LinkableNode e1, LinkableNode e2) {
		_tree.set(e1.arrayPosition, e2);
		_tree.set(e2.arrayPosition, e1);
		int e1ArrayPosition = e1.arrayPosition;
		e1.arrayPosition = e2.arrayPosition;
		e2.arrayPosition = e1ArrayPosition;
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
		_tree.remove(_size - 1);
		_size--;
		// Reconstruct the tree
		while (getTreeParent(climber) != null && climber.priority > getTreeParent(climber).priority) {
			switchTreeNodes(climber, getTreeParent(climber));
		}
		if (climber.arrayPosition < _size) {
			LinkableNode max = null;
			while (max != climber) {
				max = climber;
				if (getTreeLeftChild(climber) != null && getTreeLeftChild(climber).priority > max.priority) {
					max = getTreeLeftChild(climber);
				}
				if (getTreeRightChild(climber) != null && getTreeRightChild(climber).priority > max.priority) {
					max = getTreeRightChild(climber);
				}
				switchTreeNodes(max, climber);
			}	
		}
	}

}
