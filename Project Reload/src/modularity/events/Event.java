/**
 *
 */
package modularity.events;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import modularity.Reaction;
import modularity.ThrowingReaction;
import modularity.events.errors.InterruptedErrorEvent;
import modularity.events.errors.TimeoutErrorEvent;

/**
 * This class is used to register Reactions to events and call them when a new
 * event is created.
 *
 * @author Alexander Otto
 */
public abstract class Event {
	private static final Hashtable<String, ThrowingReaction> reactions = new Hashtable<String, ThrowingReaction>();
	private static final Hashtable<String, ThrowingReaction> reactionsPost = new Hashtable<String, ThrowingReaction>();
	private static final Hashtable<String, ThrowingReaction> reactionsPre = new Hashtable<String, ThrowingReaction>();

	/**
	 * Register Reaction. TODO comment
	 *
	 * @param pKey
	 *            the key
	 * @param pReact
	 *            the react
	 * @return true, if successful
	 */
	public static synchronized boolean registerReaction(final String pKey,
			final Reaction pReact) {
		return registerReaction(pKey, pReact, 0);
	}

	/**
	 * Registers a Reaction that will be executed once this event occurs. If the
	 * key is already used it will not be registered.
	 *
	 * Requires pKey != null.
	 *
	 * Requires pReact != null.
	 *
	 * Requires pOrder == -1 || pOrder == 0 || pOrder == 1.
	 *
	 * @param pKey
	 *            a string-identifier for the Reaction. Must be unique from all
	 *            other registered Reactions.
	 *
	 * @param pReact
	 *            a Reaction that is executed once this event occurs
	 *
	 * @param pOrder
	 *            if -1 then the Reaction is executed before normal Reactions,
	 *            if 0 it is executed normally and if 1 it is executed after
	 *            normal Reactions.
	 *
	 * @return true if the key was unique and the Reaction was sucessfully
	 *         registered. else false.
	 */
	public static synchronized boolean registerReaction(final String pKey,
			final Reaction pReact, final int pOrder) {
		assert (pKey != null);
		assert (pReact != null);
		assert ((pOrder == -1) || (pOrder == 0) || (pOrder == 1));

		switch (pOrder) {
		case -1:
			if (reactionsPre.containsKey(pKey)) {
				return false;
			} else {
				reactionsPre.put(pKey, pReact);
				return true;
			}
		case 0:
			if (reactions.containsKey(pKey)) {
				return false;
			} else {
				reactions.put(pKey, pReact);
				return true;
			}
		case 1:
			if (reactionsPost.containsKey(pKey)) {
				return false;
			} else {
				reactionsPost.put(pKey, pReact);
				return true;
			}
		default:
			return false; // Doesn't happen.
		}
	}

	/**
	 * Removes a Reaction from this event.
	 *
	 * Requires pKey != null.
	 *
	 * @param pKey
	 *            the key of the Reaction that should be removed
	 */
	public static final synchronized void removeReaction(final String pKey) {
		assert (pKey != null);

		reactionsPre.remove(pKey);
		reactions.remove(pKey);
		reactionsPost.remove(pKey);
	}

	private Exception _exc;

	private boolean _isAlive;

	/**
	 * This timer determines how long the event will minimum wait for a Reaction
	 * to finish.
	 */
	protected int _joinTimer = 100;

	private final Thread _operatingThread;

	private final ConcurrentLinkedQueue<Thread> _queue;

	private final Instant _timeStamp;

	/**
	 * Constructor of the class Event. MUST be called if the constructor is
	 * overwritten!
	 *
	 * Requires _joinTimer to be positive. This value must be positive. A value
	 * of 0 can result in an infinite waiting-time. If the Reactions don't
	 * finish in time a new ErrorEvent is created. If the constructor isn't
	 * overwritten the default-value fulfills this criteria.
	 */
	public Event() {
		assert (_joinTimer >= 0);

		_exc = null;
		_isAlive = true;
		_timeStamp = Instant.now();
		_queue = new ConcurrentLinkedQueue<Thread>();
		final Event ev = this;
		_operatingThread = new Thread() {
			@Override
			public void run() {
				startReaction(reactionsPre, ev);
				startReaction(reactions, ev);
				startReaction(reactionsPost, ev);
			}
		};
		registerEventspecificReactions();
	}

	/**
	 * Returns the instant this event was fired.
	 *
	 * @return the instant this event was fired.
	 */
	public final Instant getTimeFired() {
		return _timeStamp;
	}

	/**
	 * Kills the event. The current stage will still be completed. All following
	 * stages won't be executed.
	 */
	public final void kill() {
		_isAlive = false;
	}

	/**
	 * Registers some eventspecific Reactions. Defaults to no action at all.
	 */
	protected void registerEventspecificReactions() {

	}

	/**
	 * Starts the event.
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		_operatingThread.start();
		if (_exc != null) {
			throw _exc;
		}
	}

	/**
	 * This is a helping method to save some code. It starts a new thread for
	 * each available Reaction and then waits for all of them to finish. Can
	 * create an ErrorEvent if it is interrupted or the timer runs out.
	 *
	 * @param pReactions
	 *            a hashtable containing all Reactions to be processed this
	 *            round
	 *
	 * @param pThis
	 *            the identifier of this event
	 */
	private void startReaction(
			final Hashtable<String, ThrowingReaction> pReactions,
			final Event pThis) {
		// Check if the Event is still alive. If not there's nothing to do here.
		if (!_isAlive) {
			return;
		}
		// Setup threads for the Reactions
		for (final ThrowingReaction r : pReactions.values()) {
			final Thread thr = new Thread() {
				@Override
				public void run() {
					try {
						r.react(pThis);
					} catch (final Exception e) {
						_exc = e;
						_isAlive = false;
					}
				}
			};
			thr.start();
			_queue.add(thr);
		}
		// Wait for the threads to finish to start the next round of threads
		for (final Thread thr : _queue) {
			try {
				thr.join(_joinTimer);
			} catch (final InterruptedException e) {
				try {
					new InterruptedErrorEvent(e).run();
				} catch (final InterruptedException e1) {
					// You are not supposed to interrupt this!
					e1.printStackTrace();
				}
			}
			if (thr.isAlive()) {
				try {
					new TimeoutErrorEvent(new TimeoutException(
							"Thread was still alive after timer ran out."))
							.run();
				} catch (final TimeoutException e) {
					// The timer ran out while the thread was still alive. TODO
					// Figure out what's best to do now
					thr.interrupt();
				}
			}
		}
	}

	/**
	 * pauses the current thread until the event is completely processed. Can
	 * create an ErrorEvent if it is interrupted.
	 * 
	 * @throws Exception
	 */
	public void waitForCompletion() throws Exception {
		try {
			_operatingThread.join();
		} catch (final InterruptedException e) {
			new InterruptedErrorEvent(e).run();
		}
	}
}
