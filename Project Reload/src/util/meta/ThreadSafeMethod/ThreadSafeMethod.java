/**
 * 
 */
package util.meta.ThreadSafeMethod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import util.Container;
import util.meta.DeadlockException;

/**
 * @author Alexander Otto !!! WARNING !!! All useful implementations of this
 *         class are already implemented. If you intend to write a thread safe
 *         method, please have a look at Void, VoidArgs, Ret and RetArgs. This
 *         class is a template for other classes.
 * 
 *         Objects of any type of ThreadSafeMethod are supposed to replace
 *         normal methods in the code. By overwriting the run method and
 *         starting it with the start method, the ThreadSafeMethod works as a
 *         wrapper to prevent multiple threads from altering the same fields.
 *
 */
public abstract class ThreadSafeMethod {
	public static final class FieldList implements
			Iterable<LockManager.FieldInfo> {
		public static enum Status {
			DEFAULT, INHERITED, BORROWED;
		}

		private final ArrayList<LockManager.FieldInfo> _list = new ArrayList<LockManager.FieldInfo>();
		private final Hashtable<LockManager.FieldInfo, Field> _fields = new Hashtable<LockManager.FieldInfo, Field>();
		private final Hashtable<LockManager.FieldInfo, Status> _restorationList = new Hashtable<LockManager.FieldInfo, Status>();

		private TimeoutException _texc = null;
		private DeadlockException _dexc = null;

		private void setException(TimeoutException pE) {
			_texc = pE;
		}

		private void setException(DeadlockException pE) {
			_dexc = pE;
		}

		public TimeoutException getTimeoutException() {
			return _texc;
		}

		public DeadlockException getDeadlockException() {
			return _dexc;
		}

		private LockManager.FieldInfo getInfo(int pPos) {
			return _list.get(pPos);
		}

		public int length() {
			return _list.size();
		}

		public boolean isEmpty() {
			return _list.isEmpty();
		}

		private void offer(LockManager.FieldInfo pF, Field pFi, boolean pRestore) {
			_list.add(pF);
			_fields.put(pF, pFi);
			_restorationList.put(pF, pRestore ? Status.INHERITED
					: Status.DEFAULT);
		}

		private Field get(LockManager.FieldInfo pF) {
			return _fields.get(pF);
		}

		private boolean check(LockManager.ThreadInfo pThr) {
			return _list.get(0).check(pThr, this, 0);
		}

		private void sort() {
			_list.sort(new Comparator<LockManager.FieldInfo>() {

				@Override
				public int compare(LockManager.FieldInfo arg0,
						LockManager.FieldInfo arg1) {
					return arg0.compareTo(arg1);
				}

			});
		}

		@Override
		public Iterator<LockManager.FieldInfo> iterator() {
			return _list.iterator();
		}

		private boolean getRestore(LockManager.FieldInfo pF) {
			return Status.INHERITED.equals(_restorationList.get(pF));
		}

	}

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

			public String getDescription() {
				String s = super.toString() + "\n";
				s += "- Contains Field: \n";
				s += _field.toString() + "\n";
				s += "- Registered " + _registered.size() + " ThreadInfos: \n";
				for (ThreadInfo ti : _registered.keySet()) {
					s += ti.toString() + " readOnly? " + _registered.get(ti)
							+ "\n";
				}
				return s;
			}

			public void registerWait(ThreadInfo pThr, boolean pReadOnly) {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (_registered.containsKey(pThr)) {
							if (pReadOnly) {
								_waitingToRead.offerLast(pThr);
							} else {
								_waitingToWrite.offerLast(pThr);
							}
						} else {
							if (pReadOnly) {
								_waitingToReadNewb.offerLast(pThr);
							} else {
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
				if (_registered.isEmpty() && _waitingToRead.isEmpty()
						&& _waitingToWrite.isEmpty()
						&& _waitingToReadNewb.isEmpty()
						&& _waitingToWriteNewb.isEmpty()) {
					criticalStuffStat(new Runnable() {

						@Override
						public void run() {
							_fieldInfos.remove(t._field);
						}

					});
				}
			}

			@Override
			public int compareTo(FieldInfo pF) {
				return _field.compareTo(pF._field);
			}

			public boolean check(ThreadInfo pThr, FieldList pFields,
					int pCounter) {
				FieldInfo t = this;
				Container<Boolean> ans = new Container<Boolean>();
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						Field f = pFields.get(t);
						if (f._readOnly) {
							// only read if no-one is writing
							ans.cont = !_registered.containsValue(false);
						} else {
							// only write if no-one else is doing anything
							ans.cont = _registered.isEmpty();
						}

						if (ans.cont
								&& (pCounter + 1 >= pFields.length() || pFields
										.getInfo(pCounter + 1).check(pThr,
												pFields, pCounter + 1))) {
							// register the thread
							if (f._readOnly) {
								_waitingToRead.removeFirstOccurrence(pThr);
								_waitingToReadNewb.removeFirstOccurrence(pThr);
							} else {
								_waitingToWrite.removeFirstOccurrence(pThr);
								_waitingToWriteNewb.removeFirstOccurrence(pThr);
							}
							_registered.put(pThr, f._readOnly);
							_lastRead = f._readOnly;
						}
					}

				});
				return ans.cont;
			}

			public void tryNext() {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (!_waitingToWrite.isEmpty()
								|| !_waitingToRead.isEmpty()) {
							if (_lastRead || _waitingToRead.isEmpty())
								tryNextList(_waitingToWrite);
							else
								tryNextList(_waitingToRead);
						} else if (!_waitingToWriteNewb.isEmpty()
								|| !_waitingToReadNewb.isEmpty()) {
							if (_lastRead || _waitingToReadNewb.isEmpty())
								tryNextList(_waitingToWriteNewb);
							else
								tryNextList(_waitingToReadNewb);
						}
					}

				});
			}

			private void tryNextList(LinkedList<ThreadInfo> pList) {
				// TODO Find a better way to prevent
				// concurrentmodificationexception
				LinkedList<ThreadInfo> l = new LinkedList<ThreadInfo>(pList);
				for (ThreadInfo t : l) {
					t.check();
				}
			}

			public void remove(ThreadInfo pThr, boolean pRestore) {
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (pRestore) {
							_registered.put(pThr, true);
						} else {
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
			private static final Hashtable<Thread, ThreadInfo> _threadInfos = new Hashtable<Thread, ThreadInfo>();

			private final Hashtable<Field, Boolean> _registered = new Hashtable<Field, Boolean>();
			private final Thread _thr;

			private FieldList _waiting = null;
			private boolean _deadLockChecked = false;

			public ThreadInfo(Thread pThr) {
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
							for (FieldInfo fi : _waiting) {
								_registered.put(_waiting.get(fi),
										_waiting.get(fi)._readOnly);
							}
							System.out
									.println("Testing " + _waiting.toString());
							while (_waiting.getDeadlockException() != null
									&& _waiting.getTimeoutException() != null
									&& !Thread.currentThread().equals(_thr)
									&& LockSupport.getBlocker(_thr) != _waiting) {
								LockSupport.parkNanos(CATCHUP_TIME);
							}
							System.out.println("Unparking "
									+ _waiting.toString());
							_waiting = null;
							LockSupport.unpark(_thr);
						} else if (!_deadLockChecked) {
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
				criticalStuff(new Runnable() {

					@Override
					public void run() {
						if (pRestore) {
							_registered.put(pF, true);
						} else {
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

			public static ThreadInfo getInfo(Thread pF) {
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

			/**
			 * @return bla
			 */
			public String getDescription() {
				String s = super.toString() + "\n";
				s += "- Contains ManagedThread: \n";
				s += _thr.toString() + "\n";
				s += "- Registered " + _registered.size() + " FieldInfos: \n";
				for (Field f : _registered.keySet()) {
					s += FieldInfo.getInfo(f).toString() + " readOnly? "
							+ _registered.get(f) + "\n";
				}
				return s;
			}
		}

		// TODO Calibrate
		public static int CATCHUP_TIME = 1000000;

		public static void testIntegrity() {
			criticalStuffStat(new Runnable() {

				@Override
				public void run() {
					if (!FieldInfo._fieldInfos.isEmpty()) {
						for (Field f : FieldInfo._fieldInfos.keySet()) {
							FieldInfo fi = FieldInfo.getInfo(f);
							for (ThreadInfo ti : fi._registered.keySet()) {
								if (!ThreadInfo._threadInfos.contains(ti)) {
									System.out
											.println("!!! ERROR: Field "
													+ f._name
													+ " has an unknown thread registered: "
													+ ti._thr.toString());
								} else if (!ti._registered.containsKey(f)) {
									System.out
											.println("!!! ERROR: Field "
													+ f._name
													+ " has a thread registered, that doesn't register the Field back: "
													+ ti._thr.toString());
								}
							}
						}
					}
					if (!ThreadInfo._threadInfos.isEmpty()) {
						for (Thread t : ThreadInfo._threadInfos.keySet()) {
							ThreadInfo ti = ThreadInfo.getInfo(t);
							for (Field f : ti._registered.keySet()) {
								if (!FieldInfo._fieldInfos.containsKey(f)) {
									System.out
											.println("!!! ERROR: Thread "
													+ t.toString()
													+ " has an unknown field registered: "
													+ f._name);
								} else if (!FieldInfo._fieldInfos.get(f)._registered
										.containsKey(ti)) {
									System.out
											.println("!!! ERROR: Thread "
													+ t.toString()
													+ " has a field registered, that doesn't register the thread back: "
													+ f._name);
								}
							}
						}
					}
				}

			});
		}

		/**
		 * @param pInst
		 * @param pThr
		 * @param pFields
		 * @return bla
		 */
		public static FieldList register(Instant pInst, Thread pThr,
				Field[] pFields) {
			// remove duplicates
			for (int i = 0; i < pFields.length - 1; i++) {
				for (int j = i + 1; j < pFields.length; j++) {
					if (pFields[i].equals(pFields[j]))
						throw new IllegalArgumentException();
				}
			}
			// register waits
			FieldList fl = new FieldList();
			Container<FieldInfo> ficont = new Container<FieldInfo>();
			ThreadInfo ti = ThreadInfo.getInfo(pThr);
			criticalStuffStat(new Runnable() {

				@Override
				public void run() {
					for (Field f : pFields) {
						FieldInfo fi = FieldInfo.getInfo(f);
						if (!ti.hasRights(f)) {
							fi.registerWait(ti, f._readOnly);
							fl.offer(fi, f, ti.hasRights(new Field(f._owner,
									f._name, true)));
							ficont.cont = fi;
						}
					}
					if (!fl.isEmpty()) {
						fl.sort();
						ti.registerWait(fl);
					}
				}

			});
			// see whether the program can run immediately
			if (fl.isEmpty())
				return null;
			ficont.cont.tryNext();
			long sleeptime = 0;
			if (Instant.now().plusNanos(Long.MAX_VALUE).isBefore(pInst))
				sleeptime = -1;
			else
				sleeptime = Math.max(
						Instant.now().until(pInst, ChronoUnit.NANOS), 0);
			while (ti.isWaiting() && Instant.now().isBefore(pInst)) {
				System.out.println("Parking " + fl.toString());
				if (sleeptime == -1)
					LockSupport.park(fl);
				else
					LockSupport.parkNanos(fl, Math.max(
							Instant.now().until(pInst, ChronoUnit.NANOS), 0));
			}
			if (ti.isWaiting()) {
				fl.setException(new TimeoutException("Thread "
						+ pThr.toString() + " timed out after " + sleeptime
						+ " MILLIS."));
			} else if (true) {
				// TODO check whether a deadlock flag is set
				// fl.setException();
			}
			return fl;
		}

		/**
		 * @param pThr
		 * @param pFields
		 */
		public static void free(Thread pThr, FieldList pFields) {
			criticalStuffStat(new Runnable() {

				@Override
				public void run() {
					ThreadInfo t = ThreadInfo.getInfo(pThr);
					if (!t.isWaiting()) {
						for (FieldInfo f : pFields) {
							// remove the fields from the list of registered
							// fields
							boolean restore = pFields.getRestore(f);
							f.remove(t, restore);
							t.remove(pFields.get(f), restore);
						}
					} else {
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
		 * @return bla
		 */
		public static boolean isEmpty() {
			return FieldInfo._fieldInfos.isEmpty()
					&& ThreadInfo._threadInfos.isEmpty();
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
			assert (pOwner != null);
			assert (pName != null);

			_id = _idcounter++;
			_owner = pOwner;
			_name = pName;
			_readOnly = false;
		}

		public Field(Object pOwner, String pName, boolean pReadOnly) {
			assert (pOwner != null);
			assert (pName != null);

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
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public String toString() {
			return _owner.toString() + " : " + _name + " readOnly? "
					+ _readOnly;
		}
	}

	public static final boolean DEBUG = true;
	protected final ThreadSafeMethod[] _subCalls;
	protected final Field[] _vars;
	private boolean _didPre = false;

	protected ThreadSafeMethod(ThreadSafeMethod[] pSub, Field... pVars) {
		assert (pSub != null);
		assert (pVars != null);
		for (ThreadSafeMethod t : pSub)
			assert (t != null);
		for (Field f : pVars)
			assert (f != null);

		_subCalls = pSub;
		_vars = pVars;
	}

	protected ThreadSafeMethod(Field... pVars) {
		assert (pVars != null);
		for (Field f : pVars)
			assert (f != null);

		_subCalls = registerSubCalls();
		_vars = pVars;
	}

	protected ThreadSafeMethod[] registerSubCalls() {
		return null;
	}

	protected final FieldList pre(Instant pInst) throws DeadlockException,
			TimeoutException {
		if (_didPre)
			return null;

		FieldList registered = LockManager.register(pInst,
				Thread.currentThread(), collectFields());
		if (DEBUG)
			testIntegrity();

		return registered;
	}

	private Field[] collectFields() {
		// TODO collect fields from all sub calls
		return _vars;
	}

	protected final void post(FieldList pRegistered) {
		if (pRegistered != null)
			LockManager.free(Thread.currentThread(), pRegistered);

		if (DEBUG)
			testIntegrity();
		if (pRegistered != null) {
			System.out.println("Freed " + pRegistered.length() + " Fields:");
			for (LockManager.FieldInfo fi : pRegistered) {
				System.out.println(fi._field._name);
			}
		}
	}

	public static final boolean isEmpty() {
		if (!LockManager.isEmpty() && DEBUG) {
			System.out
					.println("--- ThreadSafeMethod not empty. Beginning list of remaining stuff:");
			if (!LockManager.FieldInfo._fieldInfos.isEmpty()) {
				System.out.println("FieldInfos still has "
						+ LockManager.FieldInfo._fieldInfos.size()
						+ " elements.");
				for (Field f : LockManager.FieldInfo._fieldInfos.keySet()) {
					System.out.println(LockManager.FieldInfo.getInfo(f)
							.getDescription());
				}
			}
			if (!LockManager.ThreadInfo._threadInfos.isEmpty()) {
				System.out.println("ThreadInfos still has "
						+ LockManager.ThreadInfo._threadInfos.size()
						+ " elements.");
				for (Thread t : LockManager.ThreadInfo._threadInfos.keySet()) {
					System.out.println(LockManager.ThreadInfo.getInfo(t)
							.getDescription());
				}
			}
			System.out.println("ThreadSafeMethod end of list.");
		}
		return LockManager.isEmpty();
	}

	public static void testIntegrity() {
		LockManager.testIntegrity();
	}
}
