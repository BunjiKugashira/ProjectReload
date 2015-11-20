/**
 * 
 */
package util.meta.ThreadSafeMethod;

import static org.junit.Assert.*;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import util.meta.DeadlockException;

/**
 * @author Alexander
 *
 */
public class VoidArgsTest {
	private int _size = 10;
	private int _counter = 0;
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
		// Test sequential start
		for (int i = 0; i < _size; i++)
		try {
			v[i].start(-1, new Object());
		} catch (DeadlockException e) {
			fail(e.toString());
		} catch (TimeoutException e) {
			fail(e.toString());
		}
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
