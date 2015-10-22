/**
 * 
 */
package util.meta;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Alexander
 *
 */
public class BlockTest {
	public static int AMOUNT = 10;
	public static int timeout = 100;
	public int storage = 0;
	
	@Test
	public void testReadThenWrite() {
		fail("Not yet implemented.");
	}
	
	@Test
	public void testWriteThenRead() {
		fail("Not yet implemented.");
	}
	
	@Test
	public void bigAssRandomUsage() {
		fail("Not yet implemented.");
	}
	
	/**
	 * Test method for {@link util.meta.Block#read(java.lang.Object)}.
	 */
	@Test
	public void testReadObject() {
		ManagedThread t1 = new ManagedThread() {
			@Override
			public void run() {
				System.out.println("Parallel process started.");
				try {
					Block.read(storage);
				} catch (DeadlockException e) {
					fail(e.toString());
				}
				storage = 1;
				Block.release();
			}
		};
		// Start with a simple lock and release
		try {
			Block.read(t1);
		} catch (DeadlockException e) {
			fail();
		}
		Block.release();
		// See wether several locks can be stacked
		for (int i = 0; i < AMOUNT; i++) {
			try {
				System.out.println("Blocking variable.");
				Block.read(storage);
			} catch (DeadlockException e) {
				fail(e.toString());
			}
		}
		storage = 0;
		t1.start(0);
		ManagedThread.sleep(timeout);
		System.out.println("Finished sleeping.");
		assertEquals(1, storage);
		for (int i = 0; i < AMOUNT; i++) {
			Block.release();
		}
		System.out.println("Finished releasing.");
		t1.join();
		assertEquals(1, storage);
	}

	/**
	 * Test method for {@link util.meta.Block#read(java.lang.Object, int)}.
	 */
	@Test
	public void testReadObjectInt() {
		storage = 0;
		try {
			Block.write(storage);
		} catch (DeadlockException e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for {@link util.meta.Block#write(java.lang.Object)}.
	 */
	@Test
	public void testWriteObject() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.Block#write(java.lang.Object, int)}.
	 */
	@Test
	public void testWriteObjectInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.Block#release()}.
	 */
	@Test
	public void testRelease() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link util.meta.Block#isDeadLocked()}.
	 */
	@Test
	public void testIsDeadLocked() {
		fail("Not yet implemented"); // TODO
	}

}
