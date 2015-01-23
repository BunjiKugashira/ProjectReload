/**
 *
 */
package modularity.events;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import modularity.Reaction;
import modularity.events.errors.InterruptedErrorEvent;
import modularity.events.errors.TimeoutErrorEvent;

/**
 * This class is used to register reactions to events and call them when a new
 * event is created.
 *
 * @author Alexander Otto
 */
public abstract class Event {
	private static final Hashtable<String, Reaction> reactions = new Hashtable<String, Reaction>();
	private static final Hashtable<String, Reaction> reactionsPost = new Hashtable<String, Reaction>();
	private static final Hashtable<String, Reaction> reactionsPre = new Hashtable<String, Reaction>();

	/**
	 * Register reaction. TODO comment
	 *
	 * @param pKey
	 *            the key
	 * @param pReact
	 *            the react
	 * @return true, if successful
	 */
	public static final synchronized boolean registerReaction(
			final String pKey, final Reaction pReact) {
		return registerReaction(pKey, pReact, 0);
	}

	/**
	 * Registers a reaction that will be executed once this event occurs. If the
	 * key is already used it will not be registered.
	 *
	 * Requires pKey != null.
	 *
	 * Requires pReact != null.
	 *
	 * Requires pOrder == -1 || pOrder == 0 || pOrder == 1.
	 *
	 * @param pKey
	 *            a string-identifier for the reaction. Must be unique from all
	 *            other registered reactions.
	 *
	 * @param pReact
	 *            a reaction that is executed once this event occurs
	 *
	 * @param pOrder
	 *            if -1 then the reaction is executed before normal reactions,
	 *            if 0 it is executed normally and if 1 it is executed after
	 *            normal reactions.
	 *
	 * @return true if the key was unique and the reaction was sucessfully
	 *         registered. else false.
	 */
	public static final synchronized boolean registerReaction(
			final String pKey, final Reaction pReact, final int pOrder) {
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
	 * Removes a reaction from this event.
	 *
	 * Requires pKey != null.
	 *
	 * @param pKey
	 *            the key of the reaction that should be removed
	 */
	public static final synchronized void removeReaction(final String pKey) {
		assert (pKey != null);

		reactionsPre.remove(pKey);
		reactions.remove(pKey);
		reactionsPost.remove(pKey);
	}

	/**
	 * This timer determines how long the event will minimum wait for a reaction
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
	 * Requires _joinTimer to be positive. This value must be positive. A value of 0 can
	 * result in an infinite waiting-time. If the reactions don't finish in time
	 * a new ErrorEvent is created. If the constructor isn't overwritten the
	 * default-value fulfills this criteria.
	 */
	public Event() {
		assert (_joinTimer >= 0);

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
	 * Starts the event.
	 */
	public final void run() {
		_operatingThread.start();
	}

	/**
	 * This is a helping method to save some code. It starts a new thread for
	 * each available reaction and then waits for all of them to finish. Can
	 * create an ErrorEvent if it is interrupted or the timer runs out.
	 *
	 * @param pRea
	 *            a hashtable containing all reactions to be processed this
	 *            round
	 *
	 * @param pThis
	 *            the identifier of this event
	 */
	private void startReaction(final Hashtable<String, Reaction> pRea,
			final Event pThis) {
		// Setup threads for the reactions
		for (final Reaction r : pRea.values()) {
			final Thread thr = new Thread() {
				@Override
				public void run() {
					r.react(pThis);
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
				new InterruptedErrorEvent(e).run();
			}
			if (thr.isAlive()) {
				new TimeoutErrorEvent(new TimeoutException(
						"Thread was still alive after timer ran out.")).run();
			}
		}
	}

	/**
	 * pauses the current thread until the event is completely processed. Can
	 * create an ErrorEvent if it is interrupted.
	 */
	public final void waitForCompletion() {
		try {
			_operatingThread.join();
		} catch (final InterruptedException e) {
			new InterruptedErrorEvent(e).run();
		}
	}
}
