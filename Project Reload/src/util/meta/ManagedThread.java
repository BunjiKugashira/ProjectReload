/**
 * 
 */
package util.meta;

import java.util.HashMap;
import java.util.Map;

import error.Log;

/**
 * @author Alexander Otto
 *
 */
public class ManagedThread implements Runnable {
	private int _numActiveThreads;
	private int _numSleepingThreads;
	
	private final Thread _thr;
	private int _priority;
	private String _name;
	
	public ManagedThread(Thread pThr) {
		_thr = pThr;
		_priority = 0;
	}
	
	public ManagedThread() {
		_thr = new Thread(this);
		_priority = 0;
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
		_priority = pPriority;
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
	 * @throws InterruptedException 
	 * 
	 */
	public void join() throws InterruptedException {
		_thr.join();
	}

	/**
	 * @return
	 */
	public static ManagedThread currentThread() {
		return new ManagedThread(Thread.currentThread());
	}
	
	public boolean equals(Object pObj) {
		try {
			return _thr.equals(((ManagedThread) pObj)._thr);
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 */
	public static void sleep() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}

	/**
	 * @param pTimeout
	 */
	public static void sleep(int pMillis) throws InterruptedException {
		Thread.sleep(pMillis);
	}

	/**
	 * 
	 */
	public void wakeUp() {
		_thr.interrupt();
	}
	
	public long getIdentifier() {
		return _thr.getId();
	}
}
