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
	private static Hashtable<String, Reaction> reactions = new Hashtable<String, Reaction>();
	
	/*
	 * TODO comment
	 */
	public Event(Object... pArgs) {
		for(Reaction r : reactions.values()) {
			r.react(pArgs);
		}
	}
	
	/*
	 *  TODO comment
	 */
	public static final synchronized boolean registerReaction(String pKey, Reaction pReact) {
		assert(pKey != null);
		assert(pReact != null);
		
		if (reactions.containsKey(pKey)) {
			return false;
		}
		else {
			reactions.put(pKey, pReact);
			return true;
		}
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
