/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import util.Container;
import util.meta.DeadlockException;
import util.meta.ManagedThread;

/**
 * @author Alexander
 *
 */
abstract class ThreadSafeMethod {
	private static final class LockManager {
		private static final class FieldInfo implements Comparable<FieldInfo> {
			private static final Hashtable<Field, FieldInfo> _fieldInfos = new Hashtable<Field, FieldInfo>();
			
			private final LinkedList<ThreadInfo> _waitingToRead = new LinkedList<ThreadInfo>();
			private final LinkedList<ThreadInfo> _waitingToWrite = new LinkedList<ThreadInfo>();
			private final LinkedList<ThreadInfo> _waitingToReadNewb = new LinkedList<ThreadInfo>();
			private final LinkedList<ThreadInfo> _waitingToWriteNewb = new LinkedList<ThreadInfo>();
			private final Hashtable<ThreadInfo, Boolean> _registered = new Hashtable<ThreadInfo, Boolean>();
			private final Field _field;
			
			private boolean _lastRead = false;
			
			public FieldInfo(Field pF) {
				_field = pF;
			}
			
			public void registerWait(ThreadInfo pThr, boolean pReadOnly) {
				FieldInfo t = this;
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (_registered.containsKey(pThr)) {
							if (pReadOnly) {
								_waitingToRead.offerLast(pThr);
							}
							else {
								_waitingToWrite.offerLast(pThr);
							}
						}
						else {
							if (pReadOnly) {
								_waitingToReadNewb.offerLast(pThr);
							}
							else {
								_waitingToWriteNewb.offerLast(pThr);
							}
						}
					}
					
				});
				
			}
			public void removeWait(ThreadInfo pT) {
				criticalStuff(new Runnable() {
					
					@Override
					public void run() {
						_waitingToRead.removeFirstOccurrence(pT);
						_waitingToReadNewb.removeFirstOccurrence(pT);
						_waitingToWrite.removeFirstOccurrence(pT);
						_waitingToWriteNewb.removeFirstOccurrence(pT);
						removeFieldInfo();
					}
					
				});
			}
			private void removeFieldInfo() {
				FieldInfo t = this;
				if (_registered.isEmpty() && _waitingToRead.isEmpty() && _waitingToWrite.isEmpty() && _waitingToReadNewb.isEmpty() && _waitingToWriteNewb.isEmpty()) {
					criticalStuffStat(new Runnable() {
						
						@Override
						public void run() {
							_fieldInfos.remove(t._field);
						}
						
					});
				}
			}
			public int compareTo(FieldInfo pF) {
				return _field.compareTo(pF._field);
			}
			public boolean check(ThreadInfo pThr, FieldList pFields, int pCounter) {
				FieldInfo t = this;
				Container<Boolean> ans = new Container<Boolean>();
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						Field f = pFields.get(t);
						if (f._readOnly) {
							ans.cont = !_registered.containsValue(false); // only read if no-one is writing
						}
						else {
							ans.cont = _registered.isEmpty(); // only write if no-one else is doing anything
						}
						
						if (ans.cont && (pCounter+1 >= pFields.length() || pFields.getInfo(pCounter+1).check(pThr, pFields, pCounter+1))) {
							// register the thread
							if (f._readOnly) {
								_waitingToRead.removeFirstOccurrence(pThr);
								_waitingToReadNewb.removeFirstOccurrence(pThr);
							}
							else {
								_waitingToWrite.removeFirstOccurrence(pThr);
								_waitingToWriteNewb.removeFirstOccurrence(pThr);
							}
							_registered.put(pThr, f._readOnly);
						}
					}
					
				});
				return ans.cont;
			}
			public void tryNext() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (!_waitingToWrite.isEmpty() || !_waitingToRead.isEmpty()) {
							if (_lastRead || _waitingToRead.isEmpty())
								tryNextList(_waitingToWrite);
							else
								tryNextList(_waitingToRead);
						}
						else if (!_waitingToWriteNewb.isEmpty() || !_waitingToReadNewb.isEmpty()) {
							if (_lastRead || _waitingToReadNewb.isEmpty())
								tryNextList(_waitingToWriteNewb);
							else
								tryNextList(_waitingToReadNewb);
						}
					}
					
				});
			}
			private void tryNextList(LinkedList<ThreadInfo> pList) {
				LinkedList<ThreadInfo> l = new LinkedList<ThreadInfo>(pList); // TODO Find a better way to prevent concurrentmodificationexception
				for (ThreadInfo t : l) {
					t.check();
				}
			}
			public void remove(ThreadInfo pThr, boolean pRestore) {
				FieldInfo t = this;
				criticalStuff(new Runnable() {
					
					@Override
					public void run() {
						if (pRestore) {
							_registered.put(pThr, true);
						}
						else {
							_registered.remove(pThr);
							removeFieldInfo();
						}
					}
				});
			}
			
			public static FieldInfo getInfo(Field pF) {
				Container<FieldInfo> c = new Container<FieldInfo>();
				criticalStuffStat(new Runnable() {

					@Override
					public void run() {
						c.cont = _fieldInfos.get(pF);
						if (c.cont == null) {
							c.cont = new FieldInfo(pF);
							_fieldInfos.put(pF, c.cont);
						}
					}
					
				});
				return c.cont;
			}
			
			private synchronized void criticalStuff(Runnable pRun) {
				pRun.run();
			}
		}
		private static final class ThreadInfo {
			private static final Hashtable<ManagedThread, ThreadInfo> _threadInfos = new Hashtable<ManagedThread, ThreadInfo>();

			private final Hashtable<Field, Boolean> _registered = new Hashtable<Field, Boolean>();
			private final ManagedThread _thr;
			
			private FieldList _waiting = null;
			private boolean _deadLockChecked = false;
			
			public ThreadInfo(ManagedThread pThr) {
				_thr = pThr;
			}
			
			public boolean isWaiting() {
				return _waiting != null;
			}
			public void registerWait(FieldList pFields) {
				_waiting = pFields;
				_deadLockChecked = false;
			}
			public boolean hasRights(Field pF) {
				if (!_registered.contains(pF))
					return false;
				else
					return pF._readOnly || !_registered.get(pF);
			}
			public void check() {
				ThreadInfo t = this;
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (_waiting.check(t)) {
							_thr.wakeUp(_waiting);
							_waiting = null;
						}
						else if (!_deadLockChecked) {
							_deadLockChecked = true;
							// TODO find deadlocks
						}
					}
					
				});
			}
			public void removeWait() {
				_waiting = null;
				removeThreadInfo();
			}
			public void remove(Field pF, boolean pRestore) {
				ThreadInfo t = this;
				criticalStuff(new Runnable() {
					
					@Override
					public void run() {
						if (pRestore) {
							_registered.put(pF, true);
						}
						else {
							_registered.remove(pF);
							removeThreadInfo();
						}
					}
				});
			}
			private void removeThreadInfo() {
				ThreadInfo t = this;
				criticalStuff(new Runnable() {
					
					@Override
					public void run() {
						if (_registered.isEmpty() && _waiting == null) {
							criticalStuffStat(new Runnable() {
								
								@Override
								public void run() {
									_threadInfos.remove(t._thr);
								}
							});
						}
					}
					
				});
			}
			
			public static ThreadInfo getInfo(ManagedThread pF) {
				Container<ThreadInfo> c = new Container<ThreadInfo>();
				criticalStuffStat(new Runnable() {

					@Override
					public void run() {
						c.cont = _threadInfos.get(pF);
						if (c.cont == null) {
							c.cont = new ThreadInfo(pF);
							_threadInfos.put(pF, c.cont);
						}
					}
					
				});
				return c.cont;
			}
			
			private synchronized void criticalStuff(Runnable pRun) {
				pRun.run();
			}
		}
		public static final class FieldList implements Iterable<FieldInfo> {
			private final ArrayList<FieldInfo> _list = new ArrayList<FieldInfo>();
			private final Hashtable<FieldInfo, Field> _fields = new Hashtable<FieldInfo, Field>();
			private final Hashtable<FieldInfo, Boolean> _restorationList = new Hashtable<FieldInfo, Boolean>();
			
			private TimeoutException _texc = null;
			private DeadlockException _dexc = null;
			
			public void setException(TimeoutException pE) {
				_texc = pE;
			}
			public void setException(DeadlockException pE) {
				_dexc = pE;
			}
			public TimeoutException getTimeoutException() {
				return _texc;
			}
			public DeadlockException getDeadlockException() {
				return _dexc;
			}
			public FieldInfo getInfo(int pPos) {
				return _list.get(pPos);
			}
			public int length() {
				return _list.size();
			}
			public boolean isEmpty() {
				return _list.isEmpty();
			}
			public void offer(FieldInfo pF, Field pFi, boolean pRestore) {
				_list.add(pF);
				_fields.put(pF, pFi);
				_restorationList.put(pF, pRestore);
			}
			public Field get(FieldInfo pF) {
				return _fields.get(pF);
			}
			public boolean check(ThreadInfo pThr) {
				return _list.get(0).check(pThr, this, 0);
			}
			public void sort() {
				_list.sort(new Comparator<FieldInfo>() {

					@Override
					public int compare(FieldInfo arg0, FieldInfo arg1) {
						return arg0.compareTo(arg1);
					}
					
				});
			}
			@Override
			public Iterator<FieldInfo> iterator() {
				return _list.iterator();
			}
			public boolean getRestore(FieldInfo pF) {
				return _restorationList.get(pF);
			}
			
		}
		
		/**
		 * @param pInst
		 * @param pThr
		 * @param pFields
		 * @return
		 * @throws DeadlockException 
		 * @throws TimeoutException 
		 */
		public static FieldList register(Instant pInst, ManagedThread pThr, Field[] pFields) {
			// remove duplicates
			for (int i = 0; i < pFields.length-1; i++) {
				for (int j = i+1; j < pFields.length; j++) {
					if (pFields[i].equals(pFields[j]))
						throw new IllegalArgumentException();
				}
			}
			// register waits
			FieldList fl = new FieldList();
			Container<FieldInfo> ficont = new Container<FieldInfo>();
			criticalStuffStat(new Runnable() {

				@Override
				public void run() {
					ThreadInfo ti = ThreadInfo.getInfo(pThr);
					for (Field f : pFields) {
						FieldInfo fi = FieldInfo.getInfo(f);
						if (!ti.hasRights(f)) {
							fi.registerWait(ti, f._readOnly);
							fl.offer(fi, f, ti.hasRights(new Field(f._owner, f._name, true)));
							ficont.cont = fi;
						}
					}
					if (!fl.isEmpty())
						ti.registerWait(fl);
				}
				
			});
			// see whether the program can run immediately
			if (fl.isEmpty())
				return null;
			fl.sort();
			pThr.setTired(fl);
			ficont.cont.tryNext();
			if (pThr.isTired(fl)) {
				int sleeptime = 0;
				if (Instant.now().plusMillis(Integer.MAX_VALUE).isBefore(pInst))
					sleeptime = -1;
				else {
					sleeptime = (int)Instant.now().until(pInst, ChronoUnit.MILLIS);
					if (sleeptime < 0)
						sleeptime = 0;
				}
				if (pThr.sleep(fl, sleeptime)) {
					fl.setException(new TimeoutException("Thread " + pThr.toString() + " timed out after " + sleeptime + " MILLIS."));
				}
				else if (true) {
					// TODO check whether a deadlock flag is set
					// fl.setException();
				}	
			}
			return fl;
		}

		/**
		 * @param pThr
		 * @param pFields
		 */
		public static void free(ManagedThread pThr, FieldList pFields) {
			criticalStuffStat(new Runnable() {

				@Override
				public void run() {
					ThreadInfo t = ThreadInfo.getInfo(pThr);
					if (!t.isWaiting()) {
						for (FieldInfo f : pFields) {
							// remove the fields from the list of registered fields
							boolean restore = pFields.getRestore(f);
							f.remove(t, restore);
							t.remove(pFields.get(f), restore);
						}
					}
					else {
						for (FieldInfo f : t._waiting) {
							f.removeWait(t);
						}
						t.removeWait();
					}
					for (FieldInfo f : pFields) {
						// see whether a new thread can access this field
						f.tryNext();
					}
				}
				
			});
		}

		/**
		 * @return
		 */
		public static boolean isEmpty() {
			return FieldInfo._fieldInfos.isEmpty() && ThreadInfo._threadInfos.isEmpty();
		}
		
		private static synchronized void criticalStuffStat(Runnable pRun) {
			pRun.run();
		}
	}
	
	public static class Field implements Comparable<Field> {
		private int _idcounter = 0;
		public final int _id;
		public final Object _owner;
		public final String _name;
		public final boolean _readOnly;
		
		public Field(Object pOwner, String pName) {
			assert(pOwner != null);
			assert(pName != null);
			
			_id = _idcounter++;
			_owner = pOwner;
			_name = pName;
			_readOnly = false;
		}
		public Field(Object pOwner, String pName, boolean pReadOnly) {
			assert(pOwner != null);
			assert(pName != null);
			
			_id = _idcounter++;
			_owner = pOwner;
			_name = pName;
			_readOnly = pReadOnly;
		}
		@Override
		public int compareTo(Field pF) {
			int ret = _id - pF._id;
			if (ret > 0)
				return 1;
			else if (ret < 0)
				return -1;
			else
				return _name.compareTo(pF._name);
		}
		@Override
		public int hashCode() {
			return _owner.hashCode() * _name.hashCode();
		}
		@Override
		public boolean equals(Object pObj) {
			try {
				Field f = (Field) pObj;
				return _owner.equals(f._owner) && _name.equals(f._name);
			}
			catch (Exception e) {
				return false;
			}
		}
		@Override
		public String toString() {
			return _owner.toString() + " : " + _name + " readOnly? " + _readOnly;
		}
	}
	
	protected final ThreadSafeMethod[] _subCalls;
	protected final Field[] _vars;
	private LockManager.FieldList _registered = null;
	private boolean _didPre = false;
	
	protected ThreadSafeMethod(ThreadSafeMethod[] pSub, Field... pVars) {
		assert(pSub != null);
		assert(pVars != null);
		for (ThreadSafeMethod t : pSub)
			assert(t != null);
		for (Field f : pVars)
			assert(f != null);
		
		_subCalls = pSub;
		_vars = pVars;
	}
	
	protected ThreadSafeMethod(Field... pVars) {
		assert(pVars != null);
		for (Field f : pVars)
			assert(f != null);
		
		_subCalls = null;
		_vars = pVars;
	}
	
	protected final void pre(Instant pInst) throws DeadlockException, TimeoutException {
		if (_didPre)
			return;
		
		_registered = LockManager.register(pInst, ManagedThread.currentThread(), collectFields());
		if (_registered.getDeadlockException() != null)
			throw _registered.getDeadlockException();
		if (_registered.getTimeoutException() != null)
			throw _registered.getTimeoutException();
	}
	
	private Field[] collectFields() {
		// TODO collect fields from all sub calls
		return _vars;
	}
	
	protected final void post() {
		if (_registered != null)
			LockManager.free(ManagedThread.currentThread(), _registered);
		_registered = null;
	}
	
	public static final boolean isEmpty() {
		if (!LockManager.isEmpty()) {
			if (!LockManager.FieldInfo._fieldInfos.isEmpty()) {
				System.out.println("FieldInfos still has " + LockManager.FieldInfo._fieldInfos.size() + " elements.");
				for (Field f : LockManager.FieldInfo._fieldInfos.keySet()) {
					String s = f.toString() + "\n";
					s += LockManager.FieldInfo._fieldInfos.get(f)._registered.size();
					System.out.println(s);
				}
			}
			if (!LockManager.ThreadInfo._threadInfos.isEmpty()) {
				System.out.println("ThreadInfos still has " + LockManager.ThreadInfo._threadInfos.size() + " elements.");
			}
		}
		return LockManager.isEmpty();
	}
}