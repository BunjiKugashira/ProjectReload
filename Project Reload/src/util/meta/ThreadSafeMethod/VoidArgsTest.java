/**
 * 
 */
package util.meta.ThreadSafeMethod;

import static org.junit.Assert.*;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import util.meta.DeadlockException;
import util.meta.ManagedThread;

/**
 * @author Alexander
 *
 */
public class VoidArgsTest {
	private int _size = 1;
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
							ManagedThread.sleep(_timeout*2);
							assertTrue(_counter == 0);
							_counter = 1;
						}
						
					}.start(-1, new Object());
				} catch (DeadlockException | TimeoutException e) {
					fail(e.toString());
				}
			}
			
		}.start(0);
		ManagedThread.sleep(_timeout);
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
		// TODO
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test timeout
		// TODO
		assertTrue(ThreadSafeMethod.isEmpty());
		// Test deadlock
		// TODO
		assertTrue(ThreadSafeMethod.isEmpty());
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
