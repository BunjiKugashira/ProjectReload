/**
 *
 */
package modularity.events.errors;

import java.util.concurrent.TimeoutException;

import modularity.ReactionOld;
import modularity.events.EventOld;

/**
 * @author Alexander
 *
 */
public class TimeoutErrorEvent extends ErrorEvent {
	/**
	 *
	 */
	public static final EventContainer container = new EventContainer();

	/**
	 * @param pEv
	 * @param pExc
	 */
	protected TimeoutErrorEvent(final EventContainer pEv,
			final TimeoutException pExc) {
		super(pEv, pExc);
	}

	/**
	 * @param pExc
	 */
	public TimeoutErrorEvent(final TimeoutException pExc) {
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
