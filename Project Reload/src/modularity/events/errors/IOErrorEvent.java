/**
 *
 */
package modularity.events.errors;

import java.io.IOException;

import modularity.Reaction;
import modularity.events.Event;

/**
 * @author Alexander
 *
 */
public class IOErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected IOErrorEvent(final EventContainer pEv, final IOException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public IOErrorEvent(final IOException pExc) {
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
