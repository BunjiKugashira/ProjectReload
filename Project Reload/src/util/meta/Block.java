/**
 * 
 */
package util.meta;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import error.Log;
import junit.framework.ComparisonFailure;
import util.struct.PrioritySequentialLinkedQueue;

/**
 * @author Alexander
 *
 */
public class Block {
	public static float PRIORITYRATIO = 0.5f;
	private static class DefaultComparator<T> implements Comparator<T> {
	@SuppressWarnings("unchecked")
	public int compare(T x, T y) {
		try {
			Comparable<T> cX = (Comparable<T>) x;
			Comparable<T> cY = (Comparable<T>) y;
			int cmp = cX.compareTo(x);
			if (cmp != 0)
				throw new ComparisonFailure(x.toString() + " does not equal itself.", "" + 0, "" + cmp);
			cmp = cY.compareTo(y);
			if (cmp != 0)
				throw new ComparisonFailure(y.toString() + " does not equal itself.", "" + 0, "" + cmp);
			int xy = cX.compareTo(y);
			int yx = cY.compareTo(x);
			if (xy != 0 - yx)
				throw new ComparisonFailure(x.toString() + " and " + y.toString() + " ", "x.compareTo(y) == 0 - y.compareTo(x)", xy + " == " + yx);
			return ((Comparable<T>)x).compareTo(y);
		}
		catch (ClassCastException e) {
			return 0;
		}
		catch (Exception e1) {
			Log.logError(e1);
			return 0;
		}
    }
}
	private static class ObjectInformation {
		public final Object _obj;
		public ThreadInformation _curWriting = null;
		public int _writingDepth = 0;
		public final Hashtable<ThreadInformation, ThreadInformation> _curReading = new Hashtable<ThreadInformation, ThreadInformation>();
		public final PrioritySequentialLinkedQueue<ThreadInformation> _waiWriting = new PrioritySequentialLinkedQueue<ThreadInformation>(PRIORITYRATIO);
		public final ConcurrentLinkedQueue<ThreadInformation> _waiReading = new ConcurrentLinkedQueue<ThreadInformation>();
		
		public ObjectInformation(Object pObj) {
			_obj = pObj;
		}
		
		public void write(ThreadInformation pThr) {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					if (_curWriting._thr.equals(pThr)) {
						_writingDepth++;
						pThr.allow();
						return;
					}
					_waiWriting.add(pThr);
					if (isFree())
						launchThreads(true);
				}
				
			});
		}
		
		public void read(ThreadInformation pThr) {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					if ((_curWriting != null && _curWriting._thr.equals(pThr._thr)) || _curReading.containsKey(pThr)) {
						_curReading.put(pThr, pThr);
						pThr.allow();
						return;
					}
					_waiReading.add(pThr);
					if (_waiWriting.isEmpty())
						launchThreads(false);
				}
				
			});
		}
		
		public void release(ThreadInformation pThr) {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					if (pThr.isWriting()) {
						_writingDepth--;
						if (_writingDepth == 0)
							_curWriting = null;
						pThr.release();
						launchThreads(false);
						launchThreads(true);
					}
					else {
						_curReading.remove(pThr);
						pThr.release();
						launchThreads(true);
						launchThreads(false);
					}
				}
				
			});
		}
		// ----- Internal Methods -----
		public boolean isFree() {
			return _curWriting == null && _curReading.isEmpty();
		}
		private void launchThreads(boolean pWrite) {
			if (_curWriting == null) {
				if (_curReading.isEmpty() && pWrite && !_waiWriting.isEmpty()) {
					_curWriting = _waiWriting.poll();
					_curWriting.allow();
				}
				else if (!pWrite && !_waiReading.isEmpty()) {
					for (ThreadInformation thr : _waiReading) {
						_curReading.put(thr, thr);
						thr.allow();
					}
					_waiReading.clear();
				}
			}
		}
		private synchronized void doCriticalStuff(Runnable pRun) {
			pRun.run();
		}
	}
	
	private static class ThreadInformation {
		public final class ExtraInformation {
			public ObjectInformation obj = null;
			public boolean write = false;
			public DeadlockException dead = null;
		}
		public final ManagedThread _thr;
		public boolean _isWriting = false;
		public final Stack<ExtraInformation> _curLocked = new Stack<ExtraInformation>();
		public ExtraInformation _waiting = null;
		
		public ThreadInformation() {
			_thr = ManagedThread.currentThread();
		}
		
		public DeadlockException getDeadlockException() {
			if (_waiting != null && _waiting.dead != null)
				return _waiting.dead;
			else if (!_curLocked.isEmpty())
				return _curLocked.peek().dead;
			else
				return null;
		}
		
		public boolean isWriting() {
			return _isWriting;
		}
		
		public void allow() {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					_isWriting = _waiting.write;
					_curLocked.push(_waiting);
					_waiting = null;
				}
				
			});
			_thr.wakeUp();
		}
		
		public void release() {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					_curLocked.pop();
					_isWriting = !_curLocked.empty() && _curLocked.peek().write;
				}
				
			});
		}
		
		public void write(ObjectInformation pObj) {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					_waiting = new ExtraInformation();
					_waiting.obj = pObj;
					_waiting.write = true;
				}
				
			});
		}
		
		public void read(ObjectInformation pObj) {
			doCriticalStuff(new Runnable() {

				@Override
				public void run() {
					_waiting = new ExtraInformation();
					_waiting.obj = pObj;
					_waiting.write = false;
				}
				
			});
		}
		
		private synchronized void doCriticalStuff(Runnable pRun) {
			pRun.run();
		}
	}
	
	private static Hashtable<ManagedThread, ThreadInformation> _thrTable = new Hashtable<ManagedThread, ThreadInformation>();
	private static Hashtable<Object, ObjectInformation> _varTable = new Hashtable<Object, ObjectInformation>();
	private Comparator<Object> _cmp;
	/**
	 * 
	 */
	private Block() {
		_cmp = new DefaultComparator<Object>();
	}
	
	private Block(Comparator<Object> pComp) {
		_cmp = pComp;
	}
	
	public static void read(Object pObj) throws DeadlockException {
		try {
			read(pObj, -1);
		} catch (TimeoutException e) { // This should be impossible.
			Log.logError(e);
			Log.crash();
		}
	}
	
	public static void read(Object pObj, int pTimeout) throws TimeoutException, DeadlockException {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				ThreadInformation thr = checkHashtableThread();
				ObjectInformation obj = checkHashtableVariable(pObj);
				thr.read(obj);
				obj.read(thr);
			}
			
		});
		ThreadInformation thr = _thrTable.get(ManagedThread.currentThread());
		if (thr._waiting != null) {
			markDeadlocks(thr);
			if (ManagedThread.sleep(pTimeout)) {
				release();
				throw new TimeoutException();
			}
			if (isDeadLocked()) {
				release();
				throw new DeadlockException(thr.getDeadlockException());
			}
		}
	}
	
	public static void write(Object pObj) throws DeadlockException {
		try {
			write(pObj, -1);
		} catch (TimeoutException e) { // This should be impossible.
			Log.logError(e);
			Log.crash();
		}
	}
	
	public static void write(Object pObj, int pTimeout) throws TimeoutException, DeadlockException {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				ThreadInformation thr = checkHashtableThread();
				ObjectInformation obj = checkHashtableVariable(pObj);
				thr.write(obj);
				obj.write(thr);
			}
			
		});
		ThreadInformation thr = _thrTable.get(ManagedThread.currentThread());
		if (thr._waiting != null) {
			markDeadlocks(thr);
			if (ManagedThread.sleep(pTimeout)) {
				release();
				throw new TimeoutException();
			}
			if (isDeadLocked(thr)) {
				release();
				throw new DeadlockException(thr.getDeadlockException());
			}
		}
	}
	
	public static void release() {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				ThreadInformation thr = checkHashtableThread();
				ObjectInformation obj;
				if (thr._waiting != null) {
					obj = thr._waiting.obj;
					if (thr._waiting.write) {
						obj._waiWriting.remove(thr);
					}
					else {
						obj._waiReading.remove(thr);
					}
					thr._waiting = null;
				}
				else {
					if (thr._curLocked.isEmpty())
						throw new IllegalStateException("You can't release objects from a thread that didn't lock any.");
					obj = thr._curLocked.peek().obj;
					obj.release(thr);
				}
				if (obj._curReading.isEmpty() && obj._curWriting == null && obj._waiReading.isEmpty() && obj._waiWriting.isEmpty()) {
					_varTable.remove(obj._obj);
				}
				if (thr._curLocked.isEmpty()) {
					_thrTable.remove(thr._thr);
				}
			}
			
		});
	}
	
	public static boolean isDeadLocked() {
		ThreadInformation thr = _thrTable.get(ManagedThread.currentThread());
		return isDeadLocked(thr);
	}
	
	// --------------------------- Internal Mechanics coming ------------------------------

	private static boolean isDeadLocked(ThreadInformation pThr) {
		if (pThr == null)
			return false;
		else
			return pThr.getDeadlockException() != null;
	}
	
	private static ThreadInformation checkHashtableThread() {
		ThreadInformation thr = _thrTable.get(ManagedThread.currentThread());
		if (thr == null) {
			thr = new ThreadInformation();
			_thrTable.put(ManagedThread.currentThread(), thr);
		}
		return thr;
	}
	
	private static ObjectInformation checkHashtableVariable(Object pObj) {
		ObjectInformation obj = _varTable.get(pObj);
		if (obj == null) {
			obj = new ObjectInformation(pObj);
			_varTable.put(pObj, obj);
		}
		return obj;
	}
	
	/**
	 * This method is synchronized over the whole class and thus threadsafe. Anything that requires a threadsafe environment can be wrapped into a runnable class and run by this method.
	 * @param pRun The object that should be run.
	 */
	private static synchronized void doCriticalStuff(Runnable pRun) {
		pRun.run();
	}
	
	private static Iterable<ThreadInformation> getWaiters(ThreadInformation pThr) {
		if (pThr == null || pThr._waiting == null) {
			return new ConcurrentLinkedQueue<ThreadInformation>();
		}
		if (pThr._waiting.write) {
			ConcurrentLinkedQueue<ThreadInformation> q = pThr._waiting.obj._waiReading;
			q.addAll(pThr._waiting.obj._waiWriting);
			return q;
		}
		else {
			return pThr._waiting.obj._waiWriting;
		}
	}
	
	private static void helpMarkDeadlocks(ThreadInformation pLocked, ThreadInformation pLocker) {
		Stack<ThreadInformation.ExtraInformation> st = new Stack<ThreadInformation.ExtraInformation>();
		while (!pLocker._curLocked.isEmpty() && !pLocker._curLocked.peek().obj.equals(pLocked._waiting.obj)) {
			st.push(pLocker._curLocked.pop());
		}
		if (pLocker._curLocked.isEmpty())
			throw new IllegalStateException(); // Should not be possible.
		DeadlockException e = new DeadlockException("Deadlock detected in thread " + pLocked.toString() + " and " + pLocker.toString() + " in variable " + pLocked._waiting.obj.toString() + " and " + pLocker._waiting.obj.toString() + ". The second thread will be forced to free the first variable.");
		Log.logError(e);
		while (!st.isEmpty()) {
			st.peek().dead = e;
			pLocker._curLocked.push(st.pop());
		}
	}
	
	private static void markDeadlocks(ThreadInformation pThr) {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (pThr == null)
					return;
				if (pThr._waiting == null)
					return;
				if (pThr._curLocked.isEmpty())
					return;
				if (pThr._waiting.obj._curWriting == null && !pThr._waiting.write)
					return;
				
				for (ThreadInformation thr : getWaiters(pThr)) {
					for (ThreadInformation thr2 : getWaiters(thr)) {
						if (thr2.equals(pThr)) {
							if (pThr._thr.getPriority() > thr._thr.getPriority())
								helpMarkDeadlocks(pThr, thr);
							else
								helpMarkDeadlocks(thr, pThr);
						}
					}
				}
			}
			
		});
	}
}
