/**
 *
 */
package modularity.events.errors;

import java.io.FileNotFoundException;

import modularity.events.Event;
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
		EVENT.registerReaction("ErrorEvent.throwException", 2, EVENT.new Reaction() {

			@Override
			public void react(final FileNotFoundException pExc) {
				Log.logError(pExc);
			}
		});
	}
}
