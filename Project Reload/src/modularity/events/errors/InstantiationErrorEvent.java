/**
 *
 */
package modularity.events.errors;

import java.lang.InstantiationException;

import modularity.events.Event;
import error.Log;

/**
 * @author Alexander
 *
 */
public class InstantiationErrorEvent extends Event<InstantiationException> {
	/**
	 *
	 */
	public static final InstantiationErrorEvent EVENT = new InstantiationErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected InstantiationErrorEvent() {
		super(InstantiationException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final InstantiationException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
