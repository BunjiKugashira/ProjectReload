/**
 *
 */
package modularity.events.errors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import modularity.events.Event;
import util.meta.DeadlockException;
import error.Log;

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
		try {
			EVENT.registerReaction.start("ErrorEvent.throwException", 2,
					EVENT.new Reaction() {

						@Override
						public void react(final IOException pExc) {
							Log.logError(pExc);
						}
					});
		} catch (DeadlockException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
