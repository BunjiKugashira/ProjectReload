/**
 * 
 */
package util.meta.ThreadSafeMethod;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Stack;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import util.meta.DeadlockException;
import util.meta.ManagedThread;
import util.meta.ThreadSafeMethod.VoidArgs.Arglist;

/**
 * @author Alexander
 *
 */
public class VoidArgsTest {
	private int _size = 1;
	private int _heavyLoadSize = 2;
	private int _heavyLoadMaxTimeout = 500;
	private int _heavyLoadMaxSleep = 100;
	private int _heavyLoadMaxBranching = 1;
	private int _counter = 0;
	private int _timeout = 1000;
	boolean _success = true;
	/**
	 * Test method for {@link util.meta.ThreadSafeMethod.VoidArgs#VoidArgs(util.meta.ThreadSafeMethod.ThreadSafeMethod.Field[])}.
	 */
	@Test
	public void testVoidArgs() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStartRead() {
		@SuppressWarnings("unchecked")
		VoidArgs<Object>[] v = new VoidArgs[_size];
		VoidArgs.Field f = new VoidArgs.Field(this, "test", true);
		for (int i = 0; i < _size; i++)
		v[i] = new VoidArgs<Object>(f) {
			@Override
			protected void run(Object pArg) {
			}
		};
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test sequential start
		for (int i = 0; i < _size; i++)
		try {
			v[i].start(-1, new Object());
		} catch (DeadlockException e) {
			fail(e.toString());
		} catch (TimeoutException e) {
			fail(e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test stacked start
		_counter = 0;
		for (int i = 0; i < _size; i++)
		v[i] = new VoidArgs<Object>(f) {
			@Override
			protected void run(Object pArg) {
				_counter ++;
				if (_counter < _size)
					try {
						v[_counter].start(-1, new Object());
					} catch (Exception e) {
						fail(e.toString());
					}
			}
		};
		try {
			v[0].start(-1, new Object());
		} catch (DeadlockException | TimeoutException e) {
			fail (e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test multiple fields
		VoidArgs.Field[] fi = new VoidArgs.Field[_size];
		for (int i = 0; i < fi.length; i++) {
			fi[i] = new VoidArgs.Field(this, "Field " + i, true);
		}
		try {
			new VoidArgs<Object>(fi) {

				@Override
				protected void run(Object pArg) {
					
				}
				
			}.start(-1, new Object());
		} catch (DeadlockException | TimeoutException e) {
			fail(e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
	}
	/**
	 * Test method for {@link util.meta.ThreadSafeMethod.VoidArgs#start(java.lang.Object)}.
	 */
	@Test
	public void testStart() {
		@SuppressWarnings("unchecked")
		VoidArgs<Object>[] v = new VoidArgs[_size];
		VoidArgs.Field f = new VoidArgs.Field(this, "test");
		for (int i = 0; i < _size; i++)
		v[i] = new VoidArgs<Object>(f) {
			@Override
			protected void run(Object pArg) {
			}
		};
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test sequential start
		for (int i = 0; i < _size; i++)
		try {
			v[i].start(-1, new Object());
		} catch (DeadlockException e) {
			fail(e.toString());
		} catch (TimeoutException e) {
			fail(e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test stacked start
		_counter = 0;
		for (int i = 0; i < _size; i++)
		v[i] = new VoidArgs<Object>(f) {
			@Override
			protected void run(Object pArg) {
				_counter ++;
				if (_counter < _size)
					try {
						v[_counter].start(-1, new Object());
					} catch (Exception e) {
						fail(e.toString());
					}
			}
		};
		try {
			v[0].start(-1, new Object());
		} catch (DeadlockException | TimeoutException e) {
			fail (e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test multiple fields
		VoidArgs.Field[] fi = new VoidArgs.Field[_size];
		for (int i = 0; i < fi.length; i++) {
			fi[i] = new VoidArgs.Field(this, "Field " + i);
		}
		try {
			new VoidArgs<Object>(fi) {

				@Override
				protected void run(Object pArg) {
					
				}
				
			}.start(-1, new Object());
		} catch (DeadlockException | TimeoutException e) {
			fail(e.toString());
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test waiting situation
		System.out.println("Waiting Situation reached!");
		_counter = 0;
		_success = true;
		new ManagedThread() {
			
			@Override
			public void run() {
				try {
					new VoidArgs<Object>(f) {
						
						@Override
						protected void run(Object pArg) {
							ManagedThread.currentThread().sleep(_timeout*2);
							assertTrue(_counter == 0);
							_counter = 1;
						}
						
					}.start(-1, new Object());
				} catch (DeadlockException | TimeoutException e) {
					fail(e.toString());
				}
			}
			
		}.start(0);
		ManagedThread.currentThread().sleep(_timeout);
		ManagedThread[] thrs = new ManagedThread[_size];
		for (int i = 0; i < _size; i++) {
			v[i] = new VoidArgs<Object>(f) {

				@Override
				protected void run(Object pArg) {
					assertTrue(_counter == 1);
				}
				
			};
			VoidArgs<Object> va = v[i];
			thrs[i] = new ManagedThread() {
				VoidArgs<Object> vtemp = va;
				
				@Override
				public void run() {
					try {
						vtemp.start(-1, new Object());
					} catch (DeadlockException | TimeoutException e) {
						fail(e.toString());
					}
				}
				
			};
			thrs[i].start(0);
		}
		for (ManagedThread t : thrs) {
			t.join();
		}
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test timeout
		
		new ManagedThread() {
			
			@Override
			public void run() {
				try {
					new VoidArgs<Object>(f) {

						@Override
						protected void run(Object pArg) {
							System.out.println("I was here");
							ManagedThread.sleep(_timeout*2);
						}
						
					}.start(-1, new Object());
				} catch (DeadlockException | TimeoutException e) {
					fail(e.toString());
				}
			}
			
		}.start(0);
		VoidArgs<Object> varg = new VoidArgs<Object>(f) {

			@Override
			protected void run(Object pArg) {
				fail("Field should be occupied.");
			}
			
		};
		ManagedThread.sleep(_timeout);
		_success = false;
		try {
			varg.start(_timeout, new Object());
		} catch (DeadlockException e) {
			fail(e.toString());
			e.printStackTrace();
		} catch (TimeoutException e) {
			// This is supposed to happen.
			_success = true;
		}
		assertTrue(_success);
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test deadlock
		
		// TODO
		assertTrue(ThreadSafeMethod.isEmpty());
	}
	
	/**
	 * Heavy load test
	 */
	@Test
	public void testHeavyLoad() {
		VoidArgs.Field fields[] = new VoidArgs.Field[_heavyLoadSize*2];
		Hashtable<ManagedThread, Integer>[] fieldWriting = new Hashtable[fields.length];
		Hashtable<ManagedThread, Integer>[] fieldReading = new Hashtable[fields.length];
		ManagedThread threads[] = new ManagedThread[_heavyLoadSize];
		VoidArgs<Integer> methods[] = new VoidArgs[fields.length];
		
		for (int i = 0; i < _heavyLoadSize; i++) {
			fields[i] = new VoidArgs.Field(this, "Field" + i, false);
			fields[i + _heavyLoadSize] = new VoidArgs.Field(this, "Field" + i, true);
		}
		for (int i = 0; i < methods.length; i++) {
			fieldWriting[i] = new Hashtable<ManagedThread, Integer>();
			fieldReading[i] = new Hashtable<ManagedThread, Integer>();
			_counter = i;
			
			methods[i] = new VoidArgs<Integer>(fields[i]) {
				Field f = fields[_counter];
				Hashtable<ManagedThread, Integer> fw = fieldWriting[_counter];
				Hashtable<ManagedThread, Integer> fr = fieldReading[_counter];
				
				@Override
				protected void run(Integer pArg) {
					ManagedThread t = ManagedThread.currentThread();
					System.out.println(t.toString() + " registered " + f.toString() + ".");
					// TODO remember that this thread owns the field
					
					assertTrue(ManagedThread.currentThread().sleep((int)Math.round(Math.random() * _heavyLoadMaxSleep))); // This is to simulate the thread doing something
					if (pArg > _heavyLoadMaxBranching) {
						try {
							methods[(int)(Math.random() * methods.length)].start((int)(Math.random() * _heavyLoadMaxTimeout), pArg - 1);
						} catch (DeadlockException e) {
							System.out.println(t.toString() + " had to abort due to deadlock.");
						} catch (TimeoutException e) {
							System.out.println(t.toString() + " had to abort due to timeout.");
						}
					}
					assertTrue(ManagedThread.currentThread().sleep((int)Math.round(Math.random() * _heavyLoadMaxSleep))); // This is to simulate the thread doing something
					
					// TODO remember to free this thread's fields
				}
			};
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ManagedThread() {
				
				@Override
				public void run() {
					ManagedThread t = ManagedThread.currentThread();
					for (int n = 0; n < _heavyLoadSize; n++) {
						try {
							methods[(int)(Math.random() * methods.length)].start((int)(Math.random() * _heavyLoadMaxTimeout), (int)(Math.random() * _heavyLoadMaxBranching));
						} catch (DeadlockException e) {
							System.out.println(t.toString() + " had to abort due to deadlock.");
						} catch (TimeoutException e) {
							System.out.println(t.toString() + " had to abort due to timeout.");
						}
					}
				}
				
			};
		}
		
		for (ManagedThread t : threads) {
			t.start(0);
		}
		for (ManagedThread t : threads) {
			t.join();
		}
		assertTrue(VoidArgs.isEmpty());
	}

	/**
	 * Test method for {@link util.meta.ThreadSafeMethod.ThreadSafeMethod#subCall(util.meta.ThreadSafeMethod.ThreadSafeMethod[])}.
	 */
	@Test
	public void testSubCall() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.ThreadSafeMethod.ThreadSafeMethod#pre()}.
	 */
	@Test
	public void testPre() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.ThreadSafeMethod.ThreadSafeMethod#post()}.
	 */
	@Test
	public void testPost() {
		fail("Not yet implemented"); // TODO
	}

}
