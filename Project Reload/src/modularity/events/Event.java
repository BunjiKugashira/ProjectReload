/**
 * 
 */
package modularity.events;

import java.time.Instant;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import error.Log;
import util.meta.Block;
import util.meta.ManagedThread;

/**
 * @author Alexander Otto
 * @param <T>
 *
 */
public class Event<T> {
	/**
	 * Time in MILLIS that all reactions have to complete. Pre- main- and postoperations are timed independantly.
	 */
	public final int _timeout;
	/**
	 * Priority of the event. Further information: ManagedThread - Priority
	 */
	public int _priority;
	private final Hashtable<String, Reaction> _reactionsPre;
	private final Hashtable<String, Reaction> _reactions;
	private final Hashtable<String, Reaction> _reactionsPost;
	private static final Hashtable<String, Event<?>> _events = new Hashtable<String, Event<?>>(); // Contains all Events + their identifier
	private ManagedThread _thr; // The thread that's launching the reactions
	private static Block _statBlock = new Block();
	private Block _block;
	
	/**
	 * Constructor of class Event. Creates a new event that can be operated on if you have the identifier.
	 * 
	 * @param pEvent The Event's identifier
	 * @param pTimeout The time in MILLIS that all reactions to this event get to complete. Should be greater than 0.
	 */
	public Event(String pEvent, int pTimeout) {
		_block = new Block();
		_timeout = pTimeout;
		_reactionsPre = new Hashtable<String, Reaction>();
		_reactions = new Hashtable<String, Reaction>();
		_reactionsPost = new Hashtable<String, Reaction>();
		registerEvent(pEvent);
	}
	/**
	 * Ref. Event(String pEvent, 100)
	 * 
	 * @param pEvent
	 */
	public Event(String pEvent) {
		_block = new Block();
		_timeout = 100;
		_reactionsPre = new Hashtable<String, Reaction>();
		_reactions = new Hashtable<String, Reaction>();
		_reactionsPost = new Hashtable<String, Reaction>();
		registerEvent(pEvent);
	}
	
	private final void registerEvent(String pEvent) {
		_statBlock.write();
		if (_events.containsKey(pEvent)) {
			// TODO error
		}
		else {
			_events.put(pEvent, this);
		}
		_statBlock.release();
	}
	
	/**
	 * Removes an Event from the list. The event is no longer accessible afterwards.
	 * @param pEvent The event to remove.
	 */
	public static final void removeEvent(String pEvent) {
		_statBlock.write();
		_events.remove(pEvent);
		_statBlock.release();
	}
	
	/**
	 * Returns the Event-Object for a specific identifier.
	 * @param pEvent The Event's identifier.
	 * @return The Event-Object.
	 */
	public static final Event<?> getEvent(String pEvent) {
		_statBlock.read();
		Event<?> ev = _events.get(pEvent);
		_statBlock.release();
		return ev;
	}
	
	/**
	 * Checks whether an identifier is already registered.
	 * @param pEvent The identifier to check.
	 * @return True if the identifier is already registered, else false.
	 */
	public static final boolean hasEvent(String pEvent) {
		_statBlock.read();
		boolean bl = _events.containsKey(pEvent);
		_statBlock.release();
		return bl;
	}
	
	/**
	 * Registers a Reaction to a specific event. Reactions will be executed when the event is being run.
	 * @param pId The reaction's identifier.
	 * @param pOrder The stage in which the reaction should be executed. -1 for the pre-stage, 0 for the normal stage and 1 for post-stage.
	 * @param pReact
	 */
	public final void registerReaction(String pId, int pOrder, Reaction pReact) {
		_block.write();
		Hashtable<String, Reaction> rea;
		rea = getReactions(pOrder);
		if (rea.containsKey(pId)) {
			// TODO error
		}
		else {
			rea.put(pId, pReact);
		}
		_block.release();
	}
	
	/**
	 * Ref. registerReaction(String pId, 0, Reaction pReact)
	 * @param pId
	 * @param pReact
	 */
	public final void registerReaction(String pId, Reaction pReact) {
		registerReaction(pId, 0, pReact);
	}
	
	/**
	 * Get the Hashtable containing all the reactions. For whatever reason you might need it.
	 * @param pOrder -1 for the pre-reactions, 0 for the normal reactions and 1 for post-reactions.
	 * @return The hashtable containing all reactions.
	 */
	private final Hashtable<String, Reaction> getReactions(int pOrder) {
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

	/**
	 * Removes a reaction from a specific Event. The reaction will no longer be executed.
	 * @param pId The reaction's identifier.
	 * @param pOrder The stage from which the reaction should be removed. -1 for pre, 0 for normal and 1 for post stage.
	 */
	public final void removeReaction(String pId, int pOrder) {
		_block.write();
		Hashtable<String, Reaction> rea;
		rea = getReactions(pOrder);
		rea.remove(pId);
		_block.release();
	}
	
	/**
	 * Ref. removeReaction(String pId, 0)
	 * @param pId
	 */
	public final void removeReaction(String pId)
	{
		removeReaction(pId, 0);
	}
	
	/**
	 * Checks whether an identifier is already registered.
	 * @param pId The identifier to check.
	 * @param pOrder The stage to check. -1 for pre, 0 for normal and 1 for post stage.
	 * @return True if the reaction is already registered, else false.
	 */
	public final boolean hasReaction(String pId, int pOrder) {
		_block.read();
		boolean bl;
		Hashtable<String, Reaction> rea;
		rea = getReactions(pOrder);
		bl = rea.containsKey(pId);
		_block.release();
		return bl;
	}
	
	/**
	 * Executes the Event's reactions with parameters.
	 * @param pObj An array of parameters that might be needed by the reactions.
	 * @return A list of all exceptions that occurred during the execution.
	 */
	public final Collection<Exception> run(T pObj) {
		_block.write();
		final Collection<Exception> es = Collections.synchronizedList(new LinkedList<Exception>()); // List that collects the thrown exceptions.
		_thr = new ManagedThread() {
			public void run() {
				// Execute the specific stages one after another
				es.addAll(runReactions(_reactionsPre, pObj));
				es.addAll(runReactions(_reactions, pObj));
				es.addAll(runReactions(_reactionsPost, pObj));
			}
		};
		_thr.start(_priority);
		_block.release();
		return es;
	}
	
	private final Collection<Exception> runReactions(Hashtable<String, Reaction> pRea, T pArgs) {
		Collection<Exception> exceptions = Collections.synchronizedList(new LinkedList<Exception>()); // List of all thrown exceptions
		Queue<ManagedThread> thrs = new LinkedList<ManagedThread>(); // List of all Threads that were started to run the reactions parallel
		Enumeration<String> en = pRea.keys(); // The list of keys in the hashtable to iterate over
		
		// Start the threads and put them into a list
		while (en.hasMoreElements()) {
			String rea = en.nextElement(); // Iterating over the hashtable
			ManagedThread thr = new ManagedThread() { // Start a new thread per reaction
				public void run() {
					try {
						pRea.get(rea).react(pArgs);
					} catch (Exception e) {
						exceptions.add(e);
					}
				}
			};
			thr.setName(rea);
			thr.start(pRea.get(rea).getPriority());
			thrs.add(thr); // Add the thread to the list
		}
		
		// Wait for the threads to finish or to time out
		Instant timeStart = Instant.now(); // Remember when waiting started
		while (!thrs.isEmpty()) {
			ManagedThread thr = thrs.poll(); // Iterate over all threads. Start with the threads that were started first to give every thread approx the same time.
			long timeLeft = _timeout - timeStart.until(Instant.now(), ChronoUnit.MILLIS); // Calculate how much time is left until timeout
			if (timeLeft > 0) { // If there's still time left try to wait for completion
				try {
					thr.join(timeLeft); // Wait max timeLeft MILLIS for the thread to complete
				} catch (InterruptedException e) { // I hope this doesn't happen. At least it shouldn't in a normal running program.
					try {
						@SuppressWarnings("unchecked")
						Event<InterruptedException> ev = (Event<InterruptedException>) Event.getEvent(e.getClass().getName());
						ev.run(e);
					} catch (ClassCastException e1) {
						Log.logError(e1);
						Log.logError(e);
						Log.crash();
					} catch (NullPointerException e1) {
						Log.logError(e1);
						Log.logError(e);
						Log.crash();
					} // Eventbased errorhandling
				}
			}
			
			// Interrupt all threads that didn't finish on time (Can't wait forever)
			if (thr.isAlive()) {
				exceptions.add(new TimeoutException("Reaction " + thr.getName() + " timed out after " + _timeout + " millis."));
				thr.interrupt();
			}
		}
		
		return exceptions;
	}
	
	/**
	 * Pauses this thread until all reactions are completed or timed-out.
	 * @throws InterruptedException
	 */
	public final void waitForCompletion() throws InterruptedException {
		_thr.join();
	}
	
	/**
	 * Pauses this thread until all reactions are completed or timed-out. Waits at most pMillis MILLIS.
	 * @param pMillis The maximum amount of time to wait in MILLIS.
	 * @throws InterruptedException
	 */
	public final void waitForCompletion(long pMillis) throws InterruptedException {
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
