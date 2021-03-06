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
		try {
			EVENT.registerReaction.start("ErrorEvent.throwException", 2,
					EVENT.new Reaction() {

						@Override
						public void react(final InstantiationException pExc) {
							Log.logError(pExc);
						}
					});
		} catch (DeadlockException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
