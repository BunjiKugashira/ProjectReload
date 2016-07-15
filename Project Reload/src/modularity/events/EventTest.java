/**
 * 
 */
package modularity.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.meta.DeadlockException;

/**
 * @author Alexander
 *
 */
public class EventTest {
	private int _maxTimeout = 1000; // MILLIS
	private int _numEvents = 50;
	public int success = 0;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < _numEvents; i++) {
			Event<Object> ev = new Event<Object>("Test" + i, 0);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction.start("TestReaction" + j, -1,
						ev.new Reaction() {
							@Override
							public void react(Object pArgs)
									throws InterruptedException {
								Thread.sleep(Math.round(Math.random()
										* _maxTimeout));
							}
						});
				ev.registerReaction.start("TestReaction" + j, 0,
						ev.new Reaction() {
							@Override
							public void react(Object pArgs)
									throws InterruptedException {
								Thread.sleep(Math.round(Math.random()
										* _maxTimeout));
							}
						});
				ev.registerReaction.start("TestReaction" + j, 1,
						ev.new Reaction() {
							@Override
							public void react(Object pArgs)
									throws InterruptedException {
								Thread.sleep(Math.round(Math.random()
										* _maxTimeout));
							}
						});
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#Event(java.lang.String, int)}.
	 */
	@Test
	public void testEventStringInt() {
		Event<?> ev = new Event<Object>("Test", 0);
		assertNotNull(ev);
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#removeEvent(java.lang.String)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testRemoveEvent() throws DeadlockException, TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		new Event<Object>("TestObject");
		assertTrue(Event.hasEvent.start("TestObject"));
		Event.removeEvent.start("TestObject");
		assertFalse(Event.hasEvent.start("TestObject"));
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#getEvent(java.lang.String)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testGetEvent() throws DeadlockException, TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		Event<Object> ev = new Event<Object>("TestObject");
		assertEquals(ev, Event.getEvent.start("TestObject"));
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#hasEvent(java.lang.String)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testHasEvent() throws DeadlockException, TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		for (int i = 0; i < _numEvents; i++) {
			assertTrue(Event.hasEvent.start("Test" + i));
		}
		for (int i = -_numEvents; i < 0; i++) {
			assertFalse(Event.hasEvent.start("Test" + i));
		}
		for (int i = _numEvents; i < _numEvents * 2; i++) {
			assertFalse(Event.hasEvent.start("Test" + i));
		}
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#registerReaction(java.lang.String, int, modularity.events.Event.Reaction)}
	 * .
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testRegisterReactionStringIntReaction()
			throws DeadlockException, TimeoutException {
		Event<String> ev = new Event<String>("Test", 100);
		ev.registerReaction.start("TestReaction", -1, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
		ev.registerReaction.start("TestReaction", 0, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
		ev.registerReaction.start("TestReaction", 1, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#removeReaction(java.lang.String, int)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testRemoveReactionStringInt() throws DeadlockException,
			TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		Event<String> ev = new Event<String>("TestEvent", 100);
		ev.registerReaction.start("TestReaction", 0, ev.new Reaction() {
			@Override
			public void react(String pArgs) throws Exception {
			}
		});
		assertTrue(ev.hasReaction.start("TestReaction", 0));
		ev.removeReaction.start("TestReaction", 0);
		assertFalse(ev.hasReaction.start("TestReaction", 0));
	}

	/**
	 * Test method for
	 * {@link modularity.events.Event#hasReaction(java.lang.String, int)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@Test
	public void testHasReaction() throws DeadlockException, TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		for (int i = 0; i < _numEvents; i++) {
			for (int j = -_numEvents; j < 0; j++) {
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, -1));
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 0));
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 1));
			}
			for (int j = 0; j < _numEvents; j++) {
				assertTrue(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, -1));
				assertTrue(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 0));
				assertTrue(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 1));
			}
			for (int j = _numEvents; j < _numEvents * 2; j++) {
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, -1));
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 0));
				assertFalse(Event.getEvent.start("Test" + i).hasReaction.start(
						"TestReaction" + j, 1));
			}
		}
	}

	/**
	 * Test method for {@link modularity.events.Event#run(java.lang.Object)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRun() throws DeadlockException, TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		// Events complete on time
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction.start("TestReaction1" + j, -1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 0,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
			}
		}
		assert (success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent.start("Test1" + i)).run.start(1);
			try {
				((Event<Integer>) Event.getEvent.start("Test1" + i))
						.waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert (success == _numEvents * _numEvents * 3);
		// Events time out
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction.start("TestReaction1" + j, -1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 0,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
			}
		}
		assert (success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent.start("Test1" + i)).run.start(1);
			try {
				((Event<Integer>) Event.getEvent.start("Test1" + i))
						.waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert (success == 0);
	}

	/**
	 * Test method for {@link modularity.events.Event#waitForCompletion(long)}.
	 * 
	 * @throws TimeoutException
	 * @throws DeadlockException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWaitForCompletionLong() throws DeadlockException,
			TimeoutException {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		// Events complete on time
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction.start("TestReaction1" + j, -1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 0,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								success += pArgs;
							}
						});
			}
		}
		assert (success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent.start("Test1" + i)).run.start(1);
			try {
				((Event<Integer>) Event.getEvent.start("Test1" + i))
						.waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert (success == _numEvents * _numEvents * 3);
		// Events time out
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction.start("TestReaction1" + j, -1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 0,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
				ev.registerReaction.start("TestReaction1" + j, 1,
						ev.new Reaction() {
							@Override
							public void react(Integer pArgs) throws Exception {
								Thread.sleep(Long.MAX_VALUE);
								success += pArgs;
							}
						});
			}
		}
		assert (success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent.start("Test1" + i)).run.start(1);
			try {
				((Event<Integer>) Event.getEvent.start("Test1" + i))
						.waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert (success == 0);
	}
}
