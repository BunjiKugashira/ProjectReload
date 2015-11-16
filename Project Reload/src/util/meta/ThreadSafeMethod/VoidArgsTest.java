/**
 * 
 */
package util.meta.ThreadSafeMethod;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Alexander
 *
 */
public class VoidArgsTest {

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
		VoidArgs<Object> v;
		v = new VoidArgs<Object>(new VoidArgs.Field(this, "test")) {
			@Override
			protected void run(Object pArg) {
				// TODO Auto-generated method stub
				
			}
		};
		v.start(new Object());
		fail("Not yet implemented"); // TODO
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
