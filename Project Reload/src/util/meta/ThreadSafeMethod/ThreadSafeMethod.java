/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.meta.DeadlockException;
import util.meta.ManagedThread;

/**
 * @author Alexander
 *
 */
abstract class ThreadSafeMethod {
	private static final class LockManager {
		private static class Container<Type> {
			public Type _content;
		}
		private static final class Pair<A, B> {
			public final A a;
			public final B b;
			public Pair(A pA, B pB) {
				a = pA;
				b = pB;
			}
		}
		private static class FieldInfo {
			private static final Hashtable<Field, FieldInfo> _fieldInfos = new Hashtable<Field, FieldInfo>();
			private final Hashtable<ManagedThread, ManagedThread> _curWorking = new Hashtable<ManagedThread, ManagedThread>();
			private boolean _lastRead = false;
			public final Field _field;
			public final Hashtable<ThreadInfo, Stack<Boolean>> _reserved = new Hashtable<ThreadInfo, Stack<Boolean>>();
			public final LinkedBlockingQueue<ThreadInfo> _waitReading = new LinkedBlockingQueue<ThreadInfo>();
			public final LinkedBlockingQueue<ThreadInfo> _waitWriting = new LinkedBlockingQueue<ThreadInfo>();
			
			private FieldInfo(Field pField) {
				_field = pField;
			}
			private static final synchronized void criticalStuffStat(Runnable pRun) {
				pRun.run();
			}
			private final synchronized void criticalStuff(Runnable pRun) {
				pRun.run();
			}
			public static final FieldInfo getFieldInfo(Field pField) {
				Container<FieldInfo> c = new Container<FieldInfo>();
				criticalStuffStat(new Runnable() {

					@Override
					public void run() {
						c._content = _fieldInfos.get(pField);
						if (c._content == null) {
							c._content = new FieldInfo(pField);
							_fieldInfos.put(pField, c._content);
						}
						c._content.startWork();
					}
					
				});
				return c._content;
			}
			public final void register(ThreadInfo pThr, boolean pReadOnly) {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (pReadOnly) {
							_waitReading.offer(pThr);
						}
						else {
							_waitWriting.offer(pThr);
						}
						proceedQueue();
					}
					
				});
			}
			private final void proceedQueue() {
				for (ThreadInfo thr : _reserved.keySet()) {
					if (_reserved.get(thr).contains(false)) { // See if a thread is already writing on this field.
						return;
					}
				}
				if (_lastRead) {
					proceedWriting();
					proceedReading();
				}
				else {
					proceedReading();
					proceedWriting();
				}
			}
			private final void proceedReading() {
				if (!_lastRead || _waitWriting.isEmpty()) {
					if (!_waitReading.isEmpty()) {
						_waitReading.peek().proceed(this);
					}
				}
			}
			private final void proceedWriting() {
				if (_lastRead || _waitReading.isEmpty()) {
					if (!_waitWriting.isEmpty()) {
						_waitWriting.peek().proceed(this);
					}
				}
			}
			public final void release(ThreadInfo pThr) {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (pThr._waiting != null) {
							_waitReading.remove(pThr);
							_waitWriting.remove(pThr);
						}
						else {
							Stack<Boolean> s = _reserved.get(pThr);
							s.pop();
							if (s.isEmpty()) {
								_reserved.remove(pThr);
							}
							proceedQueue();
						}
					}
					
				});
			}
			private final void startWork() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						_curWorking.put(ManagedThread.currentThread(), ManagedThread.currentThread());
					}
					
				});
			}
			public final void endWork() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						_curWorking.remove(ManagedThread.currentThread());
						if (_curWorking.isEmpty() && _reserved.isEmpty()) {
							criticalStuffStat(new Runnable() {

								@Override
								public void run() {
									_fieldInfos.remove(_field);
								}
								
							});
						}
					}
					
				});				
			}
		}
		private static class ThreadInfo {
			private static final Hashtable<ManagedThread, ThreadInfo> _threadInfos = new Hashtable<ManagedThread, ThreadInfo>();
			private final Hashtable<ManagedThread, ManagedThread> _curWorking = new Hashtable<ManagedThread, ManagedThread>();
			public final ManagedThread _thr;
			public final Stack<FieldInfo> _fieldInfos = new Stack<FieldInfo>();
			public Field[] _waitingFields = null;
			public FieldInfo[] _waiting = null;
			private final Hashtable<FieldInfo, Boolean> _acknowledged = new Hashtable<FieldInfo, Boolean>();
			
			private ThreadInfo(ManagedThread pThr) {
				_thr = pThr;
			}
			private static final synchronized void criticalStuffStat(Runnable pRun) {
				pRun.run();
			}
			private final synchronized void criticalStuff(Runnable pRun) {
				pRun.run();
			}
			public static final ThreadInfo getThreadInfo(ManagedThread pThr) {
				Container<ThreadInfo> c = new Container<ThreadInfo>();
				criticalStuffStat(new Runnable() {

					@Override
					public void run() {
						c._content = _threadInfos.get(pThr);
						if (c._content == null) {
							c._content = new ThreadInfo(pThr);
							_threadInfos.put(pThr, c._content);
						}
						c._content.startWork();
					}
					
				});
				return c._content;
			}
			private final void startWork() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						_curWorking.put(ManagedThread.currentThread(), ManagedThread.currentThread());
					}
					
				});
			}
			public final void endWork() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						_curWorking.remove(ManagedThread.currentThread());
					}
					
				});				
			}
			public final void register(Instant pInst, Field[] pF, FieldInfo... fi) throws DeadlockException {
				ThreadInfo t = this;
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						_waiting = fi;
						_waitingFields = pF;
						_acknowledged.clear();
						for (int i = 0; i < pF.length; i++) {
							_acknowledged.put(fi[i], false);
							fi[i].register(t, pF[i]._readOnly);
						}
					}
					
				});
			}
			public final void proceed(FieldInfo pF) {
				_acknowledged.put(pF, true);
				if (!_acknowledged.contains(false)) {
					for (int i = 0; i < _waiting.length; i++) {
						_fieldInfos.push(_waiting[i]);
						if (_waitingFields[i]._readOnly) {
							_waiting[i]._waitReading.poll();
						}
						else {
							_waiting[i]._waitWriting.poll();
						}
						Stack<Boolean> s = _waiting[i]._reserved.get(this);
						if (s == null) {
							s = new Stack<Boolean>();
							_waiting[i]._reserved.put(this, s);
						}
						s.push(_waitingFields[i]._readOnly);
					}
					_waiting = null;
					_waitingFields = null;
				}
			}
			public final void free(FieldInfo... pF) {
				
			}
		}
		public static final void register(Instant pInst, ManagedThread pThr, Field... pF) throws DeadlockException {
			FieldInfo[] fi = new FieldInfo[pF.length];
			ThreadInfo thr = ThreadInfo.getThreadInfo(pThr);
			for (int i = 0; i < pF.length; i++) {
				fi[i] = FieldInfo.getFieldInfo(pF[i]);
			}
			thr.register(pInst, pF, fi);
			
			// Tell the Infos that they're no longer being worked on
			for (FieldInfo f : fi) {
				f.endWork();
			}
			thr.endWork();
		}
		public static final void free(ManagedThread pThr, Field... pF) {
			ThreadInfo thr = ThreadInfo.getThreadInfo(pThr);
			for (Field f : pF) {
				FieldInfo fi = FieldInfo.getFieldInfo(f);
				thr.free(fi);
				fi.endWork();
			}
			
			// Tell the Infos that they're no longer being worked on
			thr.endWork();
		}
	}
	
	public static abstract class Field {
		public final Object _owner;
		public final String _name;
		public final boolean _readOnly;
		
		public Field(Object pOwner, String pName) {
			_owner = pOwner;
			_name = pName;
			_readOnly = false;
		}
		public Field(Object pOwner, String pName, boolean pReadOnly) {
			_owner = pOwner;
			_name = pName;
			_readOnly = pReadOnly;
		}
		public boolean equals(Object pObj) {
			if (pObj == null || !(pObj instanceof Field))
				return false;
			Field f = (Field) pObj;
			return _owner.equals(f._owner) && _name.equals(f._name);
		}
	}
	
	protected final ThreadSafeMethod[] _subCalls;
	protected final Field[] _vars;
	private final Stack<Field> _registered = new Stack<Field>();
	private boolean _didPre = false;
	
	protected ThreadSafeMethod(ThreadSafeMethod[] pSub, Field... pVars) {
		_subCalls = pSub;
		_vars = pVars;
	}
	
	protected ThreadSafeMethod(Field... pVars) {
		_subCalls = null;
		_vars = pVars;
	}
	
	protected final void pre(Instant pInst) throws DeadlockException {
		Collection<Field> c = collectFields(this);
		Field[] fArr = new Field[0];
		fArr = c.toArray(fArr);
		try {
			LockManager.register(pInst, ManagedThread.currentThread(), fArr);
		} catch (DeadlockException e) {
			throw e;
		}
		if (fArr != null)
			_registered.addAll(c);
	}
	
	private final Collection<Field> collectFields(ThreadSafeMethod pT) {
		_didPre = true;
		Stack<Field> it = new Stack<Field>();
		for (Field f : _vars) {
			it.add(f);
		}
		for (ThreadSafeMethod t : _subCalls) {
			it.addAll(collectFields(t));
		}
		return it;
	}
	
	protected final void post() {
		while (!_registered.isEmpty()) {
			Field f = _registered.pop();
			LockManager.free(ManagedThread.currentThread(), f);
		}
	}
}
