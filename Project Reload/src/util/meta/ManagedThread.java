/**
 * 
 */
package util.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ldap.HasControls;

import error.Log;

/**
 * @author Alexander Otto
 *
 */
public class ManagedThread implements Runnable {
	private static int _numActiveThreads = 0;
	private static int _numSleepingThreads = 0;
	
	private final Thread _thr;
	private int _priority;
	private String _name;
	private boolean _isSleeping;
	private boolean _hasCoffee;
	
	public ManagedThread(Thread pThr) {
		_thr = pThr;
		setPriority(0);
	}
	
	public ManagedThread() {
		_thr = new Thread(this);
		setPriority(0);
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

	/**
	 * @return
	 */
	public static ManagedThread currentThread() {
		return new ManagedThread(Thread.currentThread());
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
	public static Boolean sleep() {
		return sleep(-1);
	}

	/**
	 * Makes the thread sleep (pause execution) for a specified amount of time or until woken up.
	 * @param pMillis The time in millis that the thread is sleeping until it wakes up by itself. If pMillis == -1 then the thread won't wake up by itself.
	 * @return true if the thread woke up by itself. false if it was woken by another thread.
	 */
	public static boolean sleep(int pMillis) {
		if (currentThread().hasCoffee()) {
			currentThread().takeCoffee();
			return true;
		}
		currentThread()._isSleeping = true;
		if (pMillis == -1) {
			try {
				while (true)
					Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				currentThread().takeCoffee();
				return false;
			}
		}
		else {
			try {
				Thread.sleep(pMillis);
			} catch (InterruptedException e) {
				currentThread().takeCoffee();
				return false;
			}
		}
		currentThread().takeCoffee();
		return true;
	}

	/**
	 * Wakes up another thread. Gives coffee if the thread is still awake.
	 */
	public void wakeUp() {
		if (_isSleeping)
			_thr.interrupt();
		else
			giveCoffee();
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

	/**
	 * @return
	 */
	public boolean hasCoffee() {
		return _hasCoffee;
	}

	/**
	 * 
	 */
	public void takeCoffee() {
		_hasCoffee = false;
	}
	
	/**
	 * 
	 */
	public void giveCoffee() {
		_hasCoffee = true;
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
}
