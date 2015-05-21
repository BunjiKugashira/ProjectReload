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
 * This is the basic Eventclass. It is used to create Events and have other classes react to them.
 * @author Alexander
 *
 */
public abstract class EventOld {
	/**
	 * This is the Eventcontainer. It contains all the variables of an eventtype (because static variables are not inherited, but remain property of the parent class). This way an inheriting eventclass only needs to create one static Eventcontainer.
	 * @author Alexander
	 *
	 */
	public static class EventContainer {
		private final Hashtable<String, ThrowingReaction> reactions;
		private final Hashtable<String, ThrowingReaction> reactionsPost;
		private final Hashtable<String, ThrowingReaction> reactionsPre;

		/**
		 * The constructor of the EventContainer. It inicializes the fields.
		 */
		public EventContainer() {
			reactionsPre = new Hashtable<String, ThrowingReaction>();
			reactions = new Hashtable<String, ThrowingReaction>();
			reactionsPost = new Hashtable<String, ThrowingReaction>();
		}

		/**
		 * Registers a reaction with a specific key, if the key is not already taken.
		 * Same as registerReaction(pKey, pReact, 1)
		 * @param pKey The key that represents this reaction. It must be unique and can be used to remove the reaction again.
		 * @param pReact The reaction that should be registered.
		 * @return True if the key is unique, else false.
		 */
		public synchronized boolean registerReaction(final String pKey,
				final ThrowingReaction pReact) {
			return registerReaction(pKey, pReact, 1);
		}

		/**
		 * Registers a reaction with a specific key, if the key is not already taken.
		 * @param pKey The key that represents this reaction. It must be unique and can be used to remove the reaction again.
		 * @param pReact The reaction that should be registered.
		 * @param pOrder 0 to run the reaction in the pre-stage, 1 to run it in the normal stage and 2 to run it in the post-stage.
		 * @return True if the key is unique, else false.
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
		 * Removes a reaction from this event.
		 * @param pKey The key for the reaction to remove.
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
	 * The time in millis that each reaction has minimum to finish whatever it is doing. The standard is 100 millis.
	 */
	protected int _joinTimer = 100;

	private final Thread _operatingThread;

	private final ConcurrentLinkedQueue<Thread> _queue;

	private final Instant _timestamp;

	/**
	 * The constructor of the class Event. It initializes the fields.
	 * @param pEv The eventcontainer of the top-level event that was created.
	 *
	 */
	protected EventOld(final EventContainer pEv) {
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
	 * It is possible that some reactions threw exceptions while running. However the event can't crash. That's why you can check for exceptions here.
	 * @return the Exceptions
	 */
	public final HashSet<Exception> getExceptions() {
		return _exceptions;
	}

	/**
	 * The instant that this event was created by calling it's constructor.
	 * @return time fired
	 */
	public final Instant getTimeFired() {
		return _timestamp;
	}

	/**
	 * Kills the event. The current stage will still be completed, but following stages won't. Doesn't do anything if the event is already in the post-stage.
	 */
	public final synchronized void kill() {
		_isAlive = false;
	}

	/**
	 * This method can be overwritten to make an event register some reactions for itself.
	 */
	protected void registerEventspecificReactions() {

	}

	/**
	 * Starts the event. It is necessary to call this method or else the event won't do anything.
	 */
	public final void run() {
		registerEventspecificReactions();
		_operatingThread.start();
	}

	private void runReactions(final Hashtable<String, ThrowingReaction> pRea) {
		if (_isAlive) {
			final EventOld ev = this;
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
	 * Pauses the current thread until all reactions on the event are completed or timed out.
	 */
	public final void waitForCompletion() {
		try {
			_operatingThread.join();
		} catch (final InterruptedException e) {
			new InterruptedErrorEvent(e).run();
		}
	}

}
