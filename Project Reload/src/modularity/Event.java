/**
 * 
 */
package modularity;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Alexander Otto
 *
 */
public abstract class Event {
	private static final Hashtable<String, Reaction> reactionsPre = new Hashtable<String, Reaction>();
	private static final Hashtable<String, Reaction> reactions = new Hashtable<String, Reaction>();
	private static final Hashtable<String, Reaction> reactionsPost = new Hashtable<String, Reaction>();
	
	private final ConcurrentLinkedQueue<Thread> _queue;
	private final Thread _operatingThread;
	protected int _joinTimer = 100;
	
	private final Instant _timeStamp;
	
	/*
	 * Constructor of the class Event. MUST be called if the constructor is overwritten! Must be called AFTER any changes to the constructor were made!
	 * 
	 * @Require _joinTimer >= 0 This value must be positive. A value of 0 can result in an infinite waiting-time. If the reactions don't finish in time a new ErrorEvent is created. If the constructor isn't overwritten the default-value fulfills this criteria.
	 */
	public Event() {
		assert(_joinTimer >= 0);
		
		_timeStamp = Instant.now();
		_queue = new ConcurrentLinkedQueue<Thread>();
		Event ev = this;
		_operatingThread = new Thread() {
			@Override
			public void run() {
				startReaction(reactionsPre, ev);
				startReaction(reactions, ev);
				startReaction(reactionsPost, ev);
			}
		};
		_operatingThread.start();
	}
	
	/*
	 * Returns the instant this event was fired.
	 * 
	 * @return the instant this event was fired.
	 */
	public final Instant getTimeFired() {
		return _timeStamp;
	}
	
	/*
	 * This is a helping method to save some code. It starts a new thread for each available reaction and then waits for all of them to finish.
	 * Can create an ErrorEvent if it is interrupted or the timer runs out.
	 * 
	 * @param pRea a hashtable containing all reactions to be processed this round
	 * @param pThis the identifier of this event
	 */
	private void startReaction(Hashtable<String, Reaction> pRea, Event pThis) {
		// Setup threads for the reactions
		for(Reaction r : pRea.values()) {
			Thread thr = new Thread() {
				@Override
				public void run() {
					r.react(pThis);
				}
			};
		thr.start();
			_queue.add(thr);
		}
		// Wait for the threads to finish to start the next round of threads
		for(Thread thr : _queue) {
			try {
				thr.join(_joinTimer);
			} catch (InterruptedException e) {
				// TODO throw some Error
			}
			if(thr.isAlive()) {
				// TODO throw some Error
			}
		}
	}
	
	/*
	 * pauses the current thread until the event is completely processed.
	 * Can create an ErrorEvent if it is interrupted.
	 */
	public void waitForCompletion() {
		try {
			_operatingThread.join();
		} catch (InterruptedException e) {
			// TODO throw some Error
		}
	}
	
	/*
	 * Registers a reaction that will be executed once this event occurs. If the key is already used it will not be registered.
	 * 
	 * @require pKey != null
	 * @require pReact != null
	 * @require pOrder == -1 || pOrder == 0 || pOrder == 1
	 * 
	 * @param pKey a string-identifier for the reaction. Must be unique from all other registered reactions.
	 * @param pReact a reaction that is executed once this event occurs
	 * @param pOrder if -1 then the reaction is executed before normal reactions, if 0 it is executed normally and if 1 it is executed after normal reactions.
	 * 
	 * @return true if the key was unique and the reaction was sucessfully registered. else false.
	 */
	public static final synchronized boolean registerReaction(String pKey, Reaction pReact, int pOrder) {
		assert(pKey != null);
		assert(pReact != null);
		assert(pOrder == -1 || pOrder == 0 || pOrder == 1);
		
		switch (pOrder) {
		case -1:
			if (reactionsPre.containsKey(pKey)) {
				return false;
			}
			else {
				reactionsPre.put(pKey, pReact);
				return true;
			}
		case 0:
			if (reactions.containsKey(pKey)) {
				return false;
			}
			else {
				reactions.put(pKey, pReact);
				return true;
			}
		case 1:
			if (reactionsPost.containsKey(pKey)) {
				return false;
			}
			else {
				reactionsPost.put(pKey, pReact);
				return true;
			}
		default:
			return false; // Doesn't happen.
		}
	}
	
	/*
	 * TODO comment
	 */
	public static final synchronized boolean registerReaction(String pKey, Reaction pReact) {
		return registerReaction(pKey, pReact, 0);
	}
	
	/*
	 * Removes a reaction from this event.
	 * 
	 * @assert pKey != null
	 * 
	 * @param pKey the key of the reaction that should be removed
	 */
	public static final synchronized void removeReaction(String pKey) {
		assert(pKey != null);
		
		reactionsPre.remove(pKey);
		reactions.remove(pKey);
		reactionsPost.remove(pKey);
	}
}
