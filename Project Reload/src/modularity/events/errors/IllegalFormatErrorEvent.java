/**
 *
 */
package modularity.events.errors;

import java.util.IllegalFormatException;

import error.Log;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class IllegalFormatErrorEvent extends Event<IllegalFormatException> {
	/**
	 *
	 */
	public static final IllegalFormatErrorEvent EVENT = new IllegalFormatErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected IllegalFormatErrorEvent() {
		super(IllegalFormatException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final IllegalFormatException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
