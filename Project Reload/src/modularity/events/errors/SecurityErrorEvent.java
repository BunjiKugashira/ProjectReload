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
		try {
			EVENT.registerReaction.start("ErrorEvent.throwException", 2,
					EVENT.new Reaction() {

						@Override
						public void react(final SecurityException pExc) {
							Log.logError(pExc);
						}
					});
		} catch (DeadlockException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
