/**
 *
 */
package modularity.events;

import java.util.Hashtable;

import modularity.ThrowingReaction;

/**
 * @author Alexander
 *
 */
public abstract class ThrowingEvent extends Event {
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
			final ThrowingReaction pReact) {
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
			final ThrowingReaction pReact, final int pOrder) {
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
}
