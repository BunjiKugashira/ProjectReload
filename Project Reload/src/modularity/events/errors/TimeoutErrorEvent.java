/**
 *
 */
package modularity.events.errors;

import java.util.concurrent.TimeoutException;

import error.Log;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class TimeoutErrorEvent extends Event<TimeoutException> {
	/**
	 *
	 */
	public static final TimeoutErrorEvent EVENT = new TimeoutErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected TimeoutErrorEvent() {
		super(TimeoutException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final TimeoutException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
