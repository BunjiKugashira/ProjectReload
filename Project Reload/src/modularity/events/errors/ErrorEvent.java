/**
 *
 */
package modularity.events.errors;

import error.Log;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class ErrorEvent extends Event<Exception> {
	/**
	 *
	 */
	public static final ErrorEvent EVENT = new ErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected ErrorEvent() {
		super(Exception.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final Exception pExc) {
				Log.logError(pExc);
			}
		});
	}
}
