/**
 * 
 */
package util.meta;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class Block {
	private class Pair<T0, T1> {
		public T0 _v0;
		public T1 _v1;
		public Pair(T0 pV0, T1 pV1) {
			_v0 = pV0;
			_v1 = pV1;
		}
	}
	
	private Hashtable<Long, Pair<ManagedThread, Stack<Boolean>>> _threads;
	private ManagedThread _writingThread;
	private int _writingDepth;
	
	private boolean _writeSwitch;
	private Queue<ManagedThread> _waitingForReading;
	private Queue<ManagedThread> _waitingForWriting;
	/**
	 * 
	 */
	public Block() {
		_writeSwitch = false;
		_writingThread = null;
		_writingDepth = 0;
		_threads = new Hashtable<Long, Pair<ManagedThread, Stack<Boolean>>>();
		_waitingForReading = new LinkedList<ManagedThread>();
		_waitingForWriting = new LinkedList<ManagedThread>();
	}
	
	private void addToStack(boolean pWriting) {
		ManagedThread thr = ManagedThread.currentThread();
		if (!_threads.containsKey(thr.getIdentifier())) {
			_threads.put(thr.getIdentifier(), new Pair<ManagedThread, Stack<Boolean>>(thr, new Stack<Boolean>()));
		}
		_threads.get(thr.getIdentifier())._v1.push(pWriting);
	}
	
	/**
	 * Blocks the calling Thread or waits until it can be blocked.
	 * @param pWriting 
	 * @param pTimeout 
	 * @param pLevel true if blocked for writing, else false
	 * @return true if no waiting is needed and false if it is needed
	 */
	private synchronized boolean block(boolean pWriting) {
		ManagedThread thr = ManagedThread.currentThread();
		// Check if reading is possible
		if (_writingThread == null || _writingThread.equals(thr)) {
			// Check if writing is desired
			if (pWriting) {
				// Check if writing is possible
				if (_threads.isEmpty() || (_threads.size() == 1 && _threads.containsKey(thr.getIdentifier()))) {
					_writingThread = thr;
					_writingDepth++;
					addToStack(true);
					return true;
				}
				// Else fail
				else {
					_waitingForWriting.add(thr);
					return false;
				}
			}
			else {
				// If writing is not desired just add to reading
				addToStack(false);
				return true;
			}
		}
		// If reading is impossible fail
		else {
			if (pWriting) {
				_waitingForWriting.add(thr);
			}
			else {
				_waitingForReading.add(thr);
			}
			return false;
		}
	}
	
	private boolean waitForBlock(boolean pWriting, int pTimeout) {
		final Instant timeStart = Instant.now();
		while (pTimeout == -1 || timeStart.until(Instant.now(), ChronoUnit.MILLIS) < pTimeout) {
			if (block(pWriting)) {
				return true;
			}
			else {
				try {
					if (pTimeout < 0) {
						ManagedThread.sleep();
					}
					else {
						ManagedThread.sleep(pTimeout);
					}
				} catch (InterruptedException e) {
					// TODO throw some kind of error
				}
			}
		}
		return false;
	}
	
	public final boolean read(int pTimeout) {
		return waitForBlock(false, pTimeout);
	}
	
	public final void read() {
		read(-1);
	}
	
	public final boolean write(int pTimeout) {
		return waitForBlock(true, pTimeout);
	}
	
	public final void write() {
		write(-1);
	}
	
	public final void release() {
		ManagedThread thr = ManagedThread.currentThread();
		if (_threads.containsKey(thr.getIdentifier())) {
			if (_threads.get(thr.getIdentifier())._v1.pop()) {
				_writingDepth--;
				if (_writingDepth == 0) {
					_writingThread = null;
				}
			}
			if (_threads.get(thr.getIdentifier())._v1.isEmpty()) {
				_threads.remove(thr.getIdentifier());
			}
			
			if (_writeSwitch || _waitingForReading.isEmpty()) {
				if (!_waitingForWriting.isEmpty())
				_waitingForWriting.poll().wakeUp();
				_writeSwitch = false;
			}
			else {
				if (!_waitingForReading.isEmpty())
				_waitingForReading.poll().wakeUp();
				_writeSwitch = true;
			}
		}
		else {
			// Do nothing :-)
		}
	}
}
