/**
 *
 */
package modularity.events.errors;

import modularity.events.Event;
import error.Log;

/**
 * @author Alexander
 *
 */
public class SecurityErrorEvent extends Event<SecurityException> {
	/**
	 *
	 */
	public static final SecurityErrorEvent EVENT = new SecurityErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected SecurityErrorEvent() {
		super(SecurityException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final SecurityException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
