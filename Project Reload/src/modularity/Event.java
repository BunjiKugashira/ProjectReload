/**
 * 
 */
package modularity;

import java.util.Hashtable;

/**
 * @author Alexander Otto
 *
 */
public abstract class Event {
	private static Hashtable<String, Reaction> reactionsPre = new Hashtable<String, Reaction>();
	private static Hashtable<String, Reaction> reactions = new Hashtable<String, Reaction>();
	private static Hashtable<String, Reaction> reactionsPost = new Hashtable<String, Reaction>();
	
	/*
	 * TODO comment
	 */
	public Event(Object... pArgs) {
		for(Reaction r : reactionsPre.values()) {
			r.react(pArgs);
		}
		for(Reaction r : reactions.values()) {
			r.react(pArgs);
		}
		for(Reaction r : reactionsPost.values()) {
			r.react(pArgs);
		}
	}
	
	/*
	 *  TODO comment
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
	 * TODO comment
	 */
	public static final synchronized void removeReaction(String pKey) {
		assert(pKey != null);
		
		reactions.remove(pKey);
	}
	
	/*
	 * TODO comment
	 */
	public abstract class Reaction {
		public abstract void react(Object... args);
	}
}
