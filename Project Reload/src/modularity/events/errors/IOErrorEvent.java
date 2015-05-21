/**
 *
 */
package modularity.events.errors;

import java.io.IOException;

import modularity.ReactionOld;
import modularity.events.EventOld;

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
		container.registerReaction("ErrorEvent.throwException", new ReactionOld() {

			@Override
			public void react(final EventOld pThis) {
				getException().printStackTrace();
			}
		}, 2);
	}
}
