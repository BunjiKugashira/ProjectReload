/**
 * 
 */
package modularity.events;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import util.ArrayHelper;
import util.meta.DeadlockException;
import util.meta.ThreadSafeMethod.RetArgs;
import util.meta.ThreadSafeMethod.ThreadSafeMethod;
import util.meta.ThreadSafeMethod.VoidArgs;
import error.Log;

/**
 * @author Alexander Otto
 * @param <T>
 *
 */
public class Event<T> {
	/**
	 * Time in MILLIS that all reactions have to complete. Pre- main- and
	 * postoperations are timed independantly.
	 */
	public final int _timeout;
	/**
	 * Priority of the event. Further information: Thread - Priority
	 */
	public int _priority = Thread.NORM_PRIORITY;
	private final Hashtable<String, Reaction> _reactionsPre;
	private final Hashtable<String, Reaction> _reactions;
	private final Hashtable<String, Reaction> _reactionsPost;
	private final Event<T> _this = this;
	private static final Hashtable<String, Event<?>> _events = new Hashtable<String, Event<?>>(); // Contains
																									// all
																									// Events
																									// +
																									// their
																									// identifier
	private Thread _thr; // The thread that's launching the reactions

	/**
	 * Constructor of class Event. Creates a new event that can be operated on
	 * if you have the identifier.
	 * 
	 * @param pEvent
	 *            The Event's identifier
	 * @param pTimeout
	 *            The time in MILLIS that all reactions to this event get to
	 *            complete. Should be greater than 0.
	 */
	public Event(String pEvent, int pTimeout) {
		_timeout = pTimeout;
		_reactionsPre = new Hashtable<String, Reaction>();
		_reactions = new Hashtable<String, Reaction>();
		_reactionsPost = new Hashtable<String, Reaction>();
		try {
			registerEvent.start(pEvent);
		} catch (DeadlockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Ref. Event(String pEvent, 100)
	 * 
	 * @param pEvent
	 */
	public Event(String pEvent) {
		_timeout = 100;
		_reactionsPre = new Hashtable<String, Reaction>();
		_reactions = new Hashtable<String, Reaction>();
		_reactionsPost = new Hashtable<String, Reaction>();
		try {
			registerEvent.start(pEvent);
		} catch (DeadlockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final VoidArgs<String> registerEvent = new VoidArgs<String>(
			new VoidArgs.Field(this, "_events")) {

		@Override
		protected void run(String pEvent) {
			if (_events.containsKey(pEvent)) {
				// TODO error
			} else {
				_events.put(pEvent, _this);
			}
		}
	};

	/**
	 * Removes an Event from the list. The event is no longer accessible
	 * afterwards.
	 * 
	 * @param pEvent
	 *            The event to remove.
	 */
	public static final VoidArgs<String> removeEvent = new VoidArgs<String>(
			new VoidArgs.Field(Event.class, "_events")) {

		@Override
		protected void run(String pEvent) {
			_events.remove(pEvent);
		}
	};

	/**
	 * Returns the Event-Object for a specific identifier.
	 * 
	 * @param pEvent
	 *            The Event's identifier.
	 * @return The Event-Object.
	 */
	public static final RetArgs<Event<?>, String> getEvent = new RetArgs<Event<?>, String>(
			new RetArgs.Field(Event.class, "_events", true)) {

		@Override
		protected Event<?> run(String pEvent) {
			return _events.get(pEvent);
		}
	};

	/**
	 * Checks whether an identifier is already registered.
	 * 
	 * @param pEvent
	 *            The identifier to check.
	 * @return True if the identifier is already registered, else false.
	 */
	public static final RetArgs<Boolean, String> hasEvent = new RetArgs<Boolean, String>(
			new RetArgs.Field(Event.class, "_events", true)) {

		@Override
		protected Boolean run(String pEvent) {
			return _events.containsKey(pEvent);
		}
	};

	/**
	 * Registers a Reaction to a specific event. Reactions will be executed when
	 * the event is being run.
	 * 
	 * @param pId
	 *            The reaction's identifier.
	 * @param pOrder
	 *            The stage in which the reaction should be executed. -1 for the
	 *            pre-stage, 0 for the normal stage and 1 for post-stage.
	 * @param pReact
	 */
	public final registerReactionClass registerReaction = new registerReactionClass(
			new VoidArgs.Field(this, "_events"));

	public final class registerReactionClass extends
			VoidArgs<registerReactionArgs> {
		public registerReactionClass(Field... pFields) {
			super(pFields);
		}

		@Override
		protected void run(registerReactionArgs pArgs) {
			Hashtable<String, Reaction> rea;
			try {
				rea = getReactions.start(pArgs.pOrder);
			} catch (DeadlockException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			if (rea.containsKey(pArgs.pId)) {
				// TODO error
			} else {
				rea.put(pArgs.pId, pArgs.pReact);
			}
		}

		@Override
		protected ThreadSafeMethod[] registerSubCalls() {
			return ArrayHelper.getArray(getReactions);
		}

		public void start(int pTimeout, String pId, int pOrder, Reaction pReact)
				throws DeadlockException, TimeoutException {
			start(pTimeout, new registerReactionArgs(pId, pOrder, pReact));
		}

		public void start(String pId, int pOrder, Reaction pReact)
				throws DeadlockException, TimeoutException {
			start(new registerReactionArgs(pId, pOrder, pReact));
		}

		public void start(int pTimeout, String pId, Reaction pReact)
				throws DeadlockException, TimeoutException {
			start(pTimeout, pId, 0, pReact);
		}

		public void start(String pId, Reaction pReact)
				throws DeadlockException, TimeoutException {
			start(pId, 0, pReact);
		}
	};

	private class registerReactionArgs {
		public final String pId;
		public final int pOrder;
		public final Reaction pReact;

		public registerReactionArgs(String ppId, int ppOrder, Reaction ppReact) {
			pId = ppId;
			pOrder = ppOrder;
			pReact = ppReact;
		}
	}

	/**
	 * Get the Hashtable containing all the reactions. For whatever reason you
	 * might need it.
	 * 
	 * @param pOrder
	 *            -1 for the pre-reactions, 0 for the normal reactions and 1 for
	 *            post-reactions.
	 * @return The hashtable containing all reactions.
	 */
	private final RetArgs<Hashtable<String, Reaction>, Integer> getReactions = new RetArgs<Hashtable<String, Reaction>, Integer>() {

		@Override
		public Hashtable<String, Reaction> run(Integer pOrder) {
			switch (pOrder) {
			case -1:
				return _reactionsPre;
			case 0:
				return _reactions;
			case 1:
				return _reactionsPost;
			default:
				return null;
			}
		}
	};

	/**
	 * Removes a reaction from a specific Event. The reaction will no longer be
	 * executed.
	 * 
	 * @param pId
	 *            The reaction's identifier.
	 * @param pOrder
	 *            The stage from which the reaction should be removed. -1 for
	 *            pre, 0 for normal and 1 for post stage.
	 */
	public final removeReactionClass removeReaction = new removeReactionClass(
			new VoidArgs.Field(this, "_events"));

	public final class removeReactionClass extends VoidArgs<removeReactionArgs> {

		public removeReactionClass(Field... pFields) {
			super(pFields);
		}

		@Override
		public void run(removeReactionArgs pArgs) {
			Hashtable<String, Reaction> rea;
			try {
				rea = getReactions.start(pArgs.pOrder);
			} catch (DeadlockException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			rea.remove(pArgs.pId);
		}

		@Override
		protected ThreadSafeMethod[] registerSubCalls() {
			return ArrayHelper.getArray(getReactions);
		}

		public void start(int pTimeout, String pId, int pOrder)
				throws DeadlockException, TimeoutException {
			start(pTimeout, new removeReactionArgs(pId, pOrder));
		}

		public void start(String pId, int pOrder) throws DeadlockException,
				TimeoutException {
			start(new removeReactionArgs(pId, pOrder));
		}

		public void start(int pTimeout, String pId) throws DeadlockException,
				TimeoutException {
			start(pTimeout, pId, 0);
		}

		public void start(String pId) throws DeadlockException,
				TimeoutException {
			start(pId, 0);
		}
	};

	private class removeReactionArgs {
		public final String pId;
		public final int pOrder;

		public removeReactionArgs(String ppId, int ppOrder) {
			pId = ppId;
			pOrder = ppOrder;
		}
	}

	/**
	 * Checks whether an identifier is already registered.
	 * 
	 * @param pId
	 *            The identifier to check.
	 * @param pOrder
	 *            The stage to check. -1 for pre, 0 for normal and 1 for post
	 *            stage.
	 * @return True if the reaction is already registered, else false.
	 */
	public final hasReactionClass hasReaction = new hasReactionClass(
			new RetArgs.Field(this, "_events", true));

	public final class hasReactionClass extends
			RetArgs<Boolean, hasReactionArgs> {

		public hasReactionClass(Field... pFields) {
			super(pFields);
		}

		@Override
		public Boolean run(hasReactionArgs pArgs) {
			Hashtable<String, Reaction> rea;
			try {
				rea = getReactions.start(pArgs.pOrder);
			} catch (DeadlockException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return rea.containsKey(pArgs.pId);
		}

		@Override
		protected ThreadSafeMethod[] registerSubCalls() {
			return ArrayHelper.getArray(getReactions);
		}

		public boolean start(int pTimeout, String pId, int pOrder)
				throws DeadlockException, TimeoutException {
			return start(pTimeout, new hasReactionArgs(pId, pOrder));
		}

		public boolean start(String pId, int pOrder) throws DeadlockException,
				TimeoutException {
			return start(new hasReactionArgs(pId, pOrder));
		}

		public boolean start(int pTimeout, String pId)
				throws DeadlockException, TimeoutException {
			return start(pTimeout, pId, 0);
		}

		public boolean start(String pId) throws DeadlockException,
				TimeoutException {
			return start(pId, 0);
		}
	};

	private class hasReactionArgs {
		public final String pId;
		public final int pOrder;

		public hasReactionArgs(String ppId, int ppOrder) {
			pId = ppId;
			pOrder = ppOrder;
		}
	}

	/**
	 * Executes the Event's reactions with parameters.
	 * 
	 * @param pObj
	 *            An array of parameters that might be needed by the reactions.
	 * @return A list of all exceptions that occurred during the execution.
	 */
	public final RetArgs<Collection<Exception>, T> run = new RetArgs<Collection<Exception>, T>(
			new RetArgs.Field(this, "_events")) {

		@Override
		public Collection<Exception> run(T pObj) {
			final Collection<Exception> es = Collections
					.synchronizedList(new LinkedList<Exception>()); // List that
																	// collects
																	// the
																	// thrown
																	// exceptions.
			_thr = new Thread() {
				@Override
				public void run() {
					// Execute the specific stages one after another
					es.addAll(runReactions(_reactionsPre, pObj));
					es.addAll(runReactions(_reactions, pObj));
					es.addAll(runReactions(_reactionsPost, pObj));
				}
			};
			_thr.setPriority(_priority);
			_thr.start();
			return es;
		}
	};

	private final Collection<Exception> runReactions(
			Hashtable<String, Reaction> pRea, T pArgs) {
		Collection<Exception> exceptions = Collections
				.synchronizedList(new LinkedList<Exception>()); // List of all
																// thrown
																// exceptions
		Queue<Thread> thrs = new LinkedList<Thread>(); // List of
														// all
														// Threads
														// that
														// were
														// started
														// to
														// run
														// the
														// reactions
														// parallel
		Enumeration<String> en = pRea.keys(); // The list of keys in the
												// hashtable to iterate over

		// Start the threads and put them into a list
		while (en.hasMoreElements()) {
			String rea = en.nextElement(); // Iterating over the hashtable
			Thread thr = new Thread() { // Start a new thread per
										// reaction
				@Override
				public void run() {
					try {
						pRea.get(rea).react(pArgs);
					} catch (Exception e) {
						exceptions.add(e);
					}
				}
			};
			thr.setName(rea);
			thr.setPriority(pRea.get(rea).getPriority());
			thr.start();
			thrs.add(thr); // Add the thread to the list
		}

		// Wait for the threads to finish or to time out
		Instant timeStart = Instant.now(); // Remember when waiting started
		while (!thrs.isEmpty()) {
			Thread thr = thrs.poll(); // Iterate over all threads. Start
										// with the threads that were
										// started first to give every
										// thread approx the same time.
			long timeLeft = _timeout
					- timeStart.until(Instant.now(), ChronoUnit.MILLIS); // Calculate
																			// how
																			// much
																			// time
																			// is
																			// left
																			// until
																			// timeout
			if (timeLeft > 0) { // If there's still time left try to wait for
								// completion
				try {
					thr.join(timeLeft); // Wait max timeLeft MILLIS for the
										// thread to complete
				} catch (InterruptedException e) { // I hope this doesn't
													// happen. At least it
													// shouldn't in a normal
													// running program.
					try {
						@SuppressWarnings("unchecked")
						Event<InterruptedException> ev = (Event<InterruptedException>) Event.getEvent
								.start(e.getClass().getName());
						ev.run.start(e);
					} catch (ClassCastException e1) {
						Log.logError(e1);
						Log.logError(e);
						Log.crash();
					} catch (NullPointerException e1) {
						Log.logError(e1);
						Log.logError(e);
						Log.crash();
					} // Eventbased errorhandling
					catch (DeadlockException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TimeoutException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			// Interrupt all threads that didn't finish on time (Can't wait
			// forever)
			if (thr.isAlive()) {
				exceptions.add(new TimeoutException("Reaction " + thr.getName()
						+ " timed out after " + _timeout + " millis."));
				thr.interrupt();
			}
		}

		return exceptions;
	}

	/**
	 * Pauses this thread until all reactions are completed or timed-out.
	 * 
	 * @throws InterruptedException
	 */
	public final void waitForCompletion() throws InterruptedException {
		_thr.join();
	}

	/**
	 * Pauses this thread until all reactions are completed or timed-out. Waits
	 * at most pMillis MILLIS.
	 * 
	 * @param pMillis
	 *            The maximum amount of time to wait in MILLIS.
	 * @throws InterruptedException
	 */
	public final void waitForCompletion(long pMillis)
			throws InterruptedException {
		_thr.join(pMillis);
	}

	/**
	 * @author Alexander Otto
	 *
	 */
	public abstract class Reaction {

		/**
		 * @param pArgs
		 * @throws Exception
		 */
		public abstract void react(T pArgs) throws Exception;

		/**
		 * @return 0
		 */
		public int getPriority() {
			return 0;
		}
	}
}
