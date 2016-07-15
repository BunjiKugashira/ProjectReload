/**
 *
 */
package modularity.events.errors;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;

import modularity.events.Event;
import util.meta.DeadlockException;
import error.Log;

/**
 * @author Alexander
 *
 */
public class FileNotFoundErrorEvent extends Event<FileNotFoundException> {
	/**
	 *
	 */
	public static final FileNotFoundErrorEvent EVENT = new FileNotFoundErrorEvent();

	/**
	 * Instantiates a new error event. TODO comment
	 *
	 * @param pEv
	 *
	 * @param pExc
	 *            the exc
	 */
	protected FileNotFoundErrorEvent() {
		super(FileNotFoundException.class.getName());
		registerEventspecificReactions();
	}

	protected void registerEventspecificReactions() {
		try {
			EVENT.registerReaction.start("ErrorEvent.throwException", 2,
					EVENT.new Reaction() {

						@Override
						public void react(final FileNotFoundException pExc) {
							Log.logError(pExc);
						}
					});
		} catch (DeadlockException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
