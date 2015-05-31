/**
 * 
 */
package modularity.events;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.meta.ManagedThread;

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
				ev.registerReaction("TestReaction" + j, -1, ev.new Reaction() {
					@Override public void react(Object pArgs) throws InterruptedException{
						Thread.sleep(Math.round(Math.random() * _maxTimeout));
					}
				});
				ev.registerReaction("TestReaction" + j, 0, ev.new Reaction() {
					@Override public void react(Object pArgs) throws InterruptedException{
						Thread.sleep(Math.round(Math.random() * _maxTimeout));
					}
				});
				ev.registerReaction("TestReaction" + j, 1, ev.new Reaction() {
					@Override public void react(Object pArgs) throws InterruptedException{
						Thread.sleep(Math.round(Math.random() * _maxTimeout));
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
	 * Test method for {@link modularity.events.Event#Event(java.lang.String, int)}.
	 */
	@Test
	public void testEventStringInt() {
		Event<?> ev = new Event<Object>("Test", 0);
		assertNotNull(ev);
	}

	/**
	 * Test method for {@link modularity.events.Event#removeEvent(java.lang.String)}.
	 */
	@Test
	public void testRemoveEvent() {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		new Event<Object>("TestObject");
		assertTrue(Event.hasEvent("TestObject"));
		Event.removeEvent("TestObject");
		assertFalse(Event.hasEvent("TestObject"));
	}

	/**
	 * Test method for {@link modularity.events.Event#getEvent(java.lang.String)}.
	 */
	@Test
	public void testGetEvent() {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		Event<Object> ev = new Event<Object>("TestObject");
		assertEquals(ev, Event.getEvent("TestObject"));
	}

	/**
	 * Test method for {@link modularity.events.Event#hasEvent(java.lang.String)}.
	 */
	@Test
	public void testHasEvent() {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		for (int i = 0; i < _numEvents; i++) {
			assertTrue(Event.hasEvent("Test" + i));
		}
		for (int i = -_numEvents; i < 0; i++) {
			assertFalse(Event.hasEvent("Test" + i));
		}
		for (int i = _numEvents; i < _numEvents * 2; i++) {
			assertFalse(Event.hasEvent("Test" + i));
		}
	}

	/**
	 * Test method for {@link modularity.events.Event#registerReaction(java.lang.String, int, modularity.events.Event.Reaction)}.
	 */
	@Test
	public void testRegisterReactionStringIntReaction() {
		Event<String> ev = new Event<String>("Test", 100);
		ev.registerReaction("TestReaction", -1, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
		ev.registerReaction("TestReaction", 0, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
		ev.registerReaction("TestReaction", 1, ev.new Reaction() {
			@Override
			public void react(String pArgs) {
			}
		});
	}

	/**
	 * Test method for {@link modularity.events.Event#removeReaction(java.lang.String, int)}.
	 */
	@Test
	public void testRemoveReactionStringInt() {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		Event<String> ev = new Event<String>("TestEvent", 100);
		ev.registerReaction("TestReaction", 0, ev.new Reaction() {
			@Override
			public void react(String pArgs) throws Exception {
			}
		});
		assertTrue(ev.hasReaction("TestReaction", 0));
		ev.removeReaction("TestReaction", 0);
		assertFalse(ev.hasReaction("TestReaction", 0));
	}

	/**
	 * Test method for {@link modularity.events.Event#hasReaction(java.lang.String, int)}.
	 */
	@Test
	public void testHasReaction() {
		try {
			setUp();
		} catch (Exception e) {
			fail(e.toString());
		}
		for (int i = 0; i < _numEvents; i++) {
			for (int j = -_numEvents; j < 0; j++) {
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, -1));
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 0));
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 1));
			}
			for (int j = 0; j < _numEvents; j++) {
				assertTrue(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, -1));
				assertTrue(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 0));
				assertTrue(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 1));
			}
			for (int j = _numEvents; j < _numEvents * 2; j++) {
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, -1));
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 0));
				assertFalse(Event.getEvent("Test" + i).hasReaction("TestReaction" + j, 1));
			}
		}
	}

	/**
	 * Test method for {@link modularity.events.Event#run(java.lang.Object)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRun() {
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
				ev.registerReaction("TestReaction1" + j, -1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 0, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
			}
		}
		assert(success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent("Test1" + i)).run(1);
			try {
				((Event<Integer>) Event.getEvent("Test1" + i)).waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert(success == _numEvents * _numEvents * 3);
		// Events time out
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction("TestReaction1" + j, -1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 0, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
			}
		}
		assert(success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent("Test1" + i)).run(1);
			try {
				((Event<Integer>) Event.getEvent("Test1" + i)).waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert(success == 0);
	}

	/**
	 * Test method for {@link modularity.events.Event#waitForCompletion(long)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWaitForCompletionLong() {
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
				ev.registerReaction("TestReaction1" + j, -1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 0, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						success += pArgs;
					}
				});
			}
		}
		assert(success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent("Test1" + i)).run(1);
			try {
				((Event<Integer>) Event.getEvent("Test1" + i)).waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert(success == _numEvents * _numEvents * 3);
		// Events time out
		success = 0;
		for (int i = 0; i < _numEvents; i++) {
			Event<Integer> ev = new Event<Integer>("Test1" + i, 100);
			for (int j = 0; j < _numEvents; j++) {
				ev.registerReaction("TestReaction1" + j, -1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 0, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
				ev.registerReaction("TestReaction1" + j, 1, ev.new Reaction() {
					@Override
					public void react(Integer pArgs) throws Exception {
						ManagedThread.sleep();
						success += pArgs;
					}
				});
			}
		}
		assert(success == 0);
		for (int i = 0; i < _numEvents; i++) {
			((Event<Integer>) Event.getEvent("Test1" + i)).run(1);
			try {
				((Event<Integer>) Event.getEvent("Test1" + i)).waitForCompletion();
			} catch (InterruptedException e) {
				fail(e.toString());
			}
		}
		assert(success == 0);
		}
	}
