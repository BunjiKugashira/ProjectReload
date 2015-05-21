/**
 *
 */
package modularity.events.errors;

import modularity.ReactionOld;
import modularity.events.EventOld;

/**
 * @author Alexander
 *
 */
public class InterruptedErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected InterruptedErrorEvent(final EventContainer pEv,
			final InterruptedException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public InterruptedErrorEvent(final InterruptedException pExc) {
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
