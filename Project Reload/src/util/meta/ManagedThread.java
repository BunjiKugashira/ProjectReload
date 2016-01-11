/**
 * 
 */
package util.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.ldap.HasControls;

import util.Container;
import error.Log;

/**
 * @author Alexander Otto
 *
 */
public class ManagedThread implements Runnable {
	private static Hashtable<Thread, ManagedThread> _threads = new Hashtable<Thread, ManagedThread>();
	private static int _numActiveThreads = 0;
	private static int _numSleepingThreads = 0;
	public static final Object DEFAULTKEY = new Object();
	
	private final Thread _thr;
	private int _priority = 0;
	private String _name = "";
	private boolean _isSleeping = false;
	// Possible threadstates: 0=running, 1=tired, 2=sleeping, 3=hasCoffee
	private Hashtable<Object, Integer> _threadState = new Hashtable<Object, Integer>();
	
	public ManagedThread(Thread pThr) {
		_thr = pThr;
		_threads.put(_thr, this);
	}
	
	public ManagedThread() {
		_thr = new Thread(this);
		_threads.put(_thr, this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	}

	/**
	 * @param _priority
	 */
	public void start(int pPriority) {
		setPriority(pPriority);
		// TODO queue for start
		_thr.start();
	}

	/**
	 * @param rea
	 */
	public void setName(String pName) {
		_name = pName;
	}

	/**
	 * @param pMillis 
	 * @throws InterruptedException 
	 */
	public void join(long pMillis) throws InterruptedException {
		_thr.join(pMillis);
	}

	/**
	 * @return
	 */
	public boolean isAlive() {
		return _thr.isAlive();
	}

	/**
	 * 
	 */
	@Deprecated
	public void interrupt() {
		_thr.interrupt();
	}

	/**
	 * @return
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return 
	 */
	public Boolean join() {
		try {
			_thr.join();
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
	
	public static ManagedThread getManagedThread(Thread pThr) {
		ManagedThread thr = _threads.get(pThr);
		if (thr == null) {
			thr = new ManagedThread(pThr);
		}
		return thr;
	}

	/**
	 * @return
	 */
	public static ManagedThread currentThread() {
		return getManagedThread(Thread.currentThread());
	}
	
	@Override
	public boolean equals(Object pObj) {
		try {
			return _thr.equals(((ManagedThread) pObj)._thr);
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return _thr.hashCode();
	}

	/**
	 * Makes the thread sleep (pause execution) until woken up.
	 * @return false as to accommodate sleep(int pMillis). After calling this method the thread will never wake up by itself to return true.
	 */
	public Boolean sleep() {
		return sleep(DEFAULTKEY);
	}
	public Boolean sleep(Object pKey) {
		return sleep(pKey, -1);
	}

	/**
	 * Makes the thread sleep (pause execution) for a specified amount of time or until woken up.
	 * @param pMillis The time in millis that the thread is sleeping until it wakes up by itself. If pMillis == -1 then the thread won't wake up by itself.
	 * @return true if the thread woke up by itself. false if it was woken by another thread.
	 */
	public boolean sleep(int pMillis) {
		return sleep(DEFAULTKEY, pMillis);
	}
	public boolean sleep(Object pKey, int pMillis) {
		Container<Boolean> ret = new Container<Boolean>();
		ManagedThread cur = currentThread();
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (cur._threadState.containsKey(pKey) && cur._threadState.get(pKey) == 3) {
					cur._threadState.remove(pKey);
					ret.cont = false;
					return;
				}
				cur._isSleeping = true;
				cur._threadState.put(pKey, 2);
				ret.cont = true;
			}
			
		});
		if (pMillis == -1) {
			try {
				while (_isSleeping)
					Thread.sleep(100); // TODO change
			} catch (InterruptedException e) {
				ret.cont = false;
			}
		}
		else {
			try {
				Thread.sleep(pMillis); // TODO change
			} catch (InterruptedException e) {
				ret.cont = false;
			}
		}
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				cur._isSleeping = false;
				cur._threadState.remove(pKey);
			}
			
		});
		return ret.cont;
	}

	/**
	 * Wakes up another thread. Gives coffee if the thread is still awake.
	 */
	public void wakeUp() {
		wakeUp(DEFAULTKEY);
	}
	public void wakeUp(Object pKey) {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (isSleeping(pKey)) {
					_threadState.remove(pKey);
					_isSleeping = false;
					_thr.interrupt(); // TODO change
				}
				else if(_threadState.containsKey(pKey) && _threadState.get(pKey) == 1) {
					_threadState.put(pKey, 3);
				}
			}
			
		});
	}
	
	/**
	 * Tells whether the thread is sleeping or not.
	 * @return true if the thread is sleeping, else false;
	 */
	public boolean isSleeping() {
		return _isSleeping;
	}
	public boolean isSleeping(Object pKey) {
		Container<Boolean> c = new Container<Boolean>();
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (_threadState.containsKey(pKey))
					c.cont = _threadState.get(pKey) == 2;
				else
					c.cont = false;
			}
			
		});
		return c.cont;
	}
	
	public void setTired() {
		setTired(DEFAULTKEY);
	}
	public void setTired(Object pKey) {
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				_threadState.put(pKey, 1);
			}
			
		});
	}
	public boolean isTired() {
		return isTired(DEFAULTKEY);
	}
	public boolean isTired(Object pKey) {
		Container<Boolean> c = new Container<Boolean>();
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (_threadState.containsKey(pKey))
					c.cont = _threadState.get(pKey) == 1;
				else
					c.cont = false;
			}
			
		});
		return c.cont;
	}
	
	public boolean hasCoffee() {
		return hasCoffee(DEFAULTKEY);
	}
	public boolean hasCoffee(Object pKey) {
		Container<Boolean> c = new Container<Boolean>();
		doCriticalStuff(new Runnable() {

			@Override
			public void run() {
				if (_threadState.containsKey(pKey))
					c.cont = _threadState.get(pKey) == 3;
				else
					c.cont = false;
			}
			
		});
		return c.cont;
	}
	
	public long getIdentifier() {
		return _thr.getId();
	}

	/**
	 * @return the _priority
	 */
	public int getPriority() {
		if (isAlive())
			return _priority;
		else
			return Integer.MIN_VALUE;
	}

	/**
	 * @param _priority the _priority to set
	 */
	public void setPriority(int pPriority) {
		_priority = pPriority;
	}
	
	public void killThread() {
		Log.logError(new Exception("The thread " + toString() + " had to be killed."));
		try {
			Method m = Thread.class.getDeclaredMethod("stop0", new Class[]{Object.class});
			m.setAccessible(true);
			m.invoke(_thr, new ThreadDeath());
		} catch (Exception e) {
			Log.logError(e);
			Log.crash();
		}
	}
	
	private synchronized void doCriticalStuff(Runnable pRun) {
		pRun.run();
	}
}
