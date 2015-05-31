/**
 * 
 */
package util.meta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander
 *
 */
public class BlockTest {
	private static final int TIMEOUT = 100; // MILLIS
	private Block _bl;
	private int _memory;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_bl = new Block();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link util.meta.Block#Block()}.
	 */
	@Test
	public void testBlock() {
		Block bl = new Block();
		assertNotNull(bl);
	}

	/**
	 * Test method for {@link util.meta.Block#read(int)}.
	 */
	@Test
	public void testReadInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.Block#read()}.
	 */
	@Test
	public void testRead() {
		// Test if writing is blocked
		_bl.read();
		_bl.read();
		_bl.read();
		_memory = 0;
		new Thread() {
			public void run() {
				_bl.write();
				_memory = 1;
				_bl.release();
			}
		}.start();
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		_bl.release();
		assertTrue(_memory == 0);
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		assertTrue(_memory == 1);
		// Test if reading is not blocked
		_bl.read();
		_bl.read();
		_bl.read();
		_memory = 0;
		new Thread() {
			public void run() {
				_bl.read();
				_memory = 1;
				_bl.release();
			}
		}.start();
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		_bl.release();
		assertTrue(_memory == 1);
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		assertTrue(_memory == 1);
	}

	/**
	 * Test method for {@link util.meta.Block#write(int)}.
	 */
	@Test
	public void testWriteInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.Block#write()}.
	 */
	@Test
	public void testWrite() {
		// Test if writing is blocked
		_bl.write();
		_bl.write();
		_bl.write();
		_memory = 0;
		new Thread() {
			public void run() {
				_bl.write();
				_memory = 1;
				_bl.release();
			}
		}.start();
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		_bl.release();
		assertTrue(_memory == 0);
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		assertTrue(_memory == 1);
		// Test if reading is blocked
		_bl.write();
		_bl.write();
		_bl.write();
		_memory = 0;
		new Thread() {
			public void run() {
				_bl.read();
				_memory = 1;
				_bl.release();
			}
		}.start();
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		_bl.release();
		assertTrue(_memory == 0);
		_bl.release();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		assertTrue(_memory == 1);
	}

	/**
	 * Test method for {@link util.meta.Block#release()}.
	 */
	@Test
	public void testRelease() {
		fail("Not yet implemented"); // TODO
	}

}
