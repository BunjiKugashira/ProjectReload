/**
 * 
 */
package test.modularity.events;

import static org.junit.Assert.*;

import java.util.IllegalFormatException;

import modularity.Reaction;
import modularity.ThrowingReaction;
import modularity.events.Event;
import modularity.events.errors.IllegalFormatErrorEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander Otto
 *
 */
public class IllegalFormatErrorEventTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < 50; i++) {
			IllegalFormatErrorEvent.registerReaction("Test " + i, new ThrowingReaction() {

				@Override
				public void react(Event pThis) throws Exception {
					wait(((pThis.hashCode() * 11111) % 5000));
					System.out.println("Test concluded.");
				}}, (i % 3) - 1);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link modularity.events.errors.IllegalFormatErrorEvent#run()}.
	 */
	@Test
	public final void testRun() {
		try {
			setUp();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean exception = false;
		
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {
			@Override
			public void react(Event pThis) {
				test1 = true;
			}}, -1);
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {
			@Override
			public void react(Event pThis) {
				test2 = true;
			}});
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {
			@Override
			public void react(Event pThis) {
				test3 = true;
			}}, 1);

		IllegalFormatErrorEvent ev = new IllegalFormatErrorEvent(null);
		try {
			ev.run();
		} catch (Exception e) {
			exception = true;
		}
		try {
			ev.waitForCompletion();
		} catch (Exception e) {
			fail("Timeout");
		}
		assertTrue(exception);
		assertTrue(test1);
	}

	boolean test1 = false;
	boolean test2 = false;
	boolean test3 = false;

	/**
	 * Test method for {@link modularity.events.errors.IllegalFormatErrorEvent#IllegalFormatErrorEvent(java.util.IllegalFormatException)}.
	 */
	@Test
	public final void testIllegalFormatErrorEvent() {
		new IllegalFormatErrorEvent(null);
	}

	/**
	 * Test method for {@link modularity.events.errors.ErrorEvent#registerEventspecificReactions()}.
	 */
	@Test
	public final void testRegisterEventspecificReactions() {
		// TODO
	}

	/**
	 * Test method for {@link modularity.events.errors.ErrorEvent#getException()}.
	 */
	@Test
	public final void testGetException() {
		IllegalFormatErrorEvent ev = new IllegalFormatErrorEvent(null);
		assert(ev.getException() == null);
	}

	/**
	 * Test method for {@link modularity.events.ThrowingEvent#registerReaction(java.lang.String, modularity.ThrowingReaction)}.
	 */
	@Test
	public final void testRegisterReactionStringThrowingReaction() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link modularity.events.ThrowingEvent#registerReaction(java.lang.String, modularity.ThrowingReaction, int)}.
	 */
	@Test
	public final void testRegisterReactionStringThrowingReactionInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link modularity.events.Event#removeReaction(java.lang.String)}.
	 */
	@Test
	public final void testRemoveReaction() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link modularity.events.Event#getTimeFired()}.
	 */
	@Test
	public final void testGetTimeFired() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link modularity.events.Event#kill()}.
	 */
	@Test
	public final void testKill() {
		test1 = false;
		test2 = false;
		test3 = false;
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {

			@Override
			public void react(Event pThis) {
				test1 = true;
				pThis.kill();
			}
			
		}, -1);
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {

			@Override
			public void react(Event pThis) {
				test2 = true;
				pThis.kill();
			}
			
		});
		IllegalFormatErrorEvent.registerReaction("T1", new Reaction() {

			@Override
			public void react(Event pThis) {
				test3 = true;
				pThis.kill();
			}
			
		}, 1);
		IllegalFormatErrorEvent ev = new IllegalFormatErrorEvent(null);
		try {
			ev.run();
		}
		catch (Exception e) {
			
		}
		try {
			ev.waitForCompletion();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(test1);
		assertFalse(test2);
		assertFalse(test3);
	}

	/**
	 * Test method for {@link modularity.events.Event#waitForCompletion()}.
	 */
	@Test
	public final void testWaitForCompletion() {
		fail("Not yet implemented"); // TODO
	}

}
