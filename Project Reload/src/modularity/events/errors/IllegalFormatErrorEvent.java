/**
 *
 */
package modularity.events.errors;

import java.util.IllegalFormatException;

import modularity.Reaction;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class IllegalFormatErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected IllegalFormatErrorEvent(final EventContainer pEv,
			final IllegalFormatException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public IllegalFormatErrorEvent(final IllegalFormatException pExc) {
		super(container, pExc);
	}

	@Override
	protected void registerEventspecificReactions() {
		container.registerReaction("ErrorEvent.throwException", new Reaction() {

			@Override
			public void react(final Event pThis) {
				getException().printStackTrace();
			}
		}, 2);
	}
}
