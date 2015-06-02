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
public class InterruptedErrorEvent extends Event<InterruptedException> {
	/**
	 *
	 */
	public static final InterruptedErrorEvent EVENT = new InterruptedErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected InterruptedErrorEvent() {
		super(InterruptedException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final InterruptedException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
