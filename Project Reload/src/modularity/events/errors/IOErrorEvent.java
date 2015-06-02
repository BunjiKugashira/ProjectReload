/**
 *
 */
package modularity.events.errors;

import java.io.IOException;

import error.Log;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class IOErrorEvent extends Event<IOException> {
	/**
	 *
	 */
	public static final IOErrorEvent EVENT = new IOErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected IOErrorEvent() {
		super(IOException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final IOException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
