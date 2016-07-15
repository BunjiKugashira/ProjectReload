/**
 *
 */
package modularity.events.errors;

import java.util.concurrent.TimeoutException;

import modularity.events.Event;
import util.meta.DeadlockException;
import error.Log;

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
		try {
			EVENT.registerReaction.start("ErrorEvent.throwException", 2,
					EVENT.new Reaction() {

						@Override
						public void react(final Exception pExc) {
							Log.logError(pExc);
						}
					});
		} catch (DeadlockException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
