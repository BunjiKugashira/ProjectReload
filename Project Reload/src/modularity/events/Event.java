/**
 *
 */
package modularity.events;

import java.time.Instant;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import modularity.ThrowingReaction;
import modularity.events.errors.InterruptedErrorEvent;
import modularity.events.errors.TimeoutErrorEvent;

/**
 * @author Alexander
 *
 */
public abstract class Event {
	/**
	 * @author Alexander
	 *
	 */
	public static class EventContainer {
		private final Hashtable<String, ThrowingReaction> reactions;
		private final Hashtable<String, ThrowingReaction> reactionsPost;
		private final Hashtable<String, ThrowingReaction> reactionsPre;

		/**
		 *
		 */
		public EventContainer() {
			reactionsPre = new Hashtable<String, ThrowingReaction>();
			reactions = new Hashtable<String, ThrowingReaction>();
			reactionsPost = new Hashtable<String, ThrowingReaction>();
		}

		/**
		 * @param pKey
		 * @param pReact
		 * @return fdsa
		 */
		public synchronized boolean registerReaction(final String pKey,
				final ThrowingReaction pReact) {
			return registerReaction(pKey, pReact, 1);
		}

		/**
		 * @param pKey
		 * @param pReact
		 * @param pOrder
		 * @return bla
		 */
		public synchronized boolean registerReaction(final String pKey,
				final ThrowingReaction pReact, final int pOrder) {
			assert (pKey != null);
			assert (pReact != null);
			assert ((pOrder == 0) || (pOrder == 1) || (pOrder == 2));
			switch (pOrder) {
			case 0:
				if (reactionsPre.containsKey(pKey)) {
					return false;
				} else {
					reactionsPre.put(pKey, pReact);
					return true;
				}
			case 1:
				if (reactions.containsKey(pKey)) {
					return false;
				} else {
					reactions.put(pKey, pReact);
					return true;
				}
			case 2:
				if (reactionsPost.containsKey(pKey)) {
					return false;
				} else {
					reactionsPost.put(pKey, pReact);
					return true;
				}
			default:
				return false;
			}
		}

		/**
		 * @param pKey
		 */
		public synchronized void removeReaction(final String pKey) {
			assert (pKey != null);

			reactionsPre.remove(pKey);
			reactions.remove(pKey);
			reactionsPost.remove(pKey);
		}
	}

	private final HashSet<Exception> _exceptions;
	private boolean _isAlive = true;

	/**
	 *
	 */
	protected int _joinTimer = 100;

	private final Thread _operatingThread;

	private final ConcurrentLinkedQueue<Thread> _queue;

	private final Instant _timestamp;

	/**
	 * @param pEv
	 *
	 */
	protected Event(final EventContainer pEv) {
		registerEventspecificReactions();
		_timestamp = Instant.now();
		_queue = new ConcurrentLinkedQueue<Thread>();
		_exceptions = new HashSet<Exception>();
		_operatingThread = new Thread() {
			@Override
			public void run() {
				runReactions(pEv.reactionsPre);
				runReactions(pEv.reactions);
				runReactions(pEv.reactionsPost);
			}
		};
	}

	/**
	 * @return the Exceptions
	 */
	public final HashSet<Exception> getExceptions() {
		return _exceptions;
	}

	/**
	 * @return time fired
	 */
	public final Instant getTimeFired() {
		return _timestamp;
	}

	/**
	 *
	 */
	public final synchronized void kill() {
		_isAlive = false;
	}

	/**
	 *
	 */
	protected void registerEventspecificReactions() {

	}

	/**
	 *
	 */
	public final void run() {
		_operatingThread.start();
	}

	private void runReactions(final Hashtable<String, ThrowingReaction> pRea) {
		if (_isAlive) {
			final Event ev = this;
			for (final String s : pRea.keySet()) {
				final Thread thr = new Thread() {
					@Override
					public void run() {
						try {
							pRea.get(s).react(ev);
						} catch (final Exception e) {
							_exceptions.add(e);
							kill();
						}
					}
				};
				thr.setName(s);
				thr.start();
				_queue.add(thr);
			}
		}
		while (!_queue.isEmpty()) {
			final Thread thr = _queue.poll();
			try {
				thr.join(_joinTimer);
			} catch (final InterruptedException e) {
				new InterruptedErrorEvent(e).run();
			}
			if (thr.isAlive()) {
				new TimeoutErrorEvent(new TimeoutException("Thread "
						+ thr.getName() + " timed out after " + _joinTimer
						+ " millis.")).run();
			}
		}
	}

	/**
	 * @throws InterruptedException
	 */
	public final void waitForCompletion() throws InterruptedException {
		try {
			_operatingThread.join();
		} catch (final InterruptedException e) {
			new InterruptedErrorEvent(e).run();
		}
	}

}
